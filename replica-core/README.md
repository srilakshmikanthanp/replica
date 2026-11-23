# Replica

Replicates changes from a source database to other stations with a transactional outbox on the source and
an inbox on each destination, and can backfill existing data to a station added later. This is how to use it,
from start to end.

## 1. What you need

Replica works through a few interfaces: `StationRepository`, `OutboxRepository`, `InboxRepository`,
`BackfillJobRepository`, `BackfillJobStepRepository`, `TransactionExecutor` and `ReplicationGate`. The examples below
assume you have an instance of each, named after its type: `stationRepository`, `outboxRepository` and so on.

The model classes (`Station`, `Outbox`, `Inbox`, `BackfillJob`, `BackfillJobStep`) are plain objects: create them with
`new`. Payloads can be encrypted at rest with a `PayloadCryptography`; `PayloadCryptography.none()` stores them as they
are.

## 2. Register stations

A destination or a source is a `Station`. Events are addressed to station IDs.

```java
Station station = new Station();
station.setName("branch-1");
stationRepository.save(station);      // station.getId() is now set
```

## 3. Define events

```java
public record ProductUpserted(String sku, String name, long price) implements Event {}
```

Events must be self-contained: carry the full state the destination needs, so applying one never depends on
reading other data.

Replaying events in order reproduces the source's final state only under some conditions on how you write them, such
as building an event from rows you read after locking them. [docs/outbox/ordering.md](docs/outbox/ordering.md) has the
reasoning.

Provide an `EventSerializer` and `EventDeserializer` that turn an event into the payload string and back (for example
JSON).

## 4. Register events and handlers

Group a feature's events and handlers in an `EventModule`. The type name is stored with each message, so the
source and the destination must use the same name.

```java
public class ProductEventModule implements EventModule {
  private final EventHandler<ProductUpserted> productUpsertedHandler;

  public ProductEventModule(EventHandler<ProductUpserted> productUpsertedHandler) {
    this.productUpsertedHandler = productUpsertedHandler;
  }

  @Override
  public EventRegistry eventRegistry() {
    return EventRegistry.builder().registerEvent("ProductUpserted", ProductUpserted.class).build();
  }

  @Override
  public EventHandlerRegistry eventHandlerRegistry() {
    return EventHandlerRegistry.builder().registerEventHandler(ProductUpserted.class, productUpsertedHandler).build();
  }
}
```

A module that only sends events can return `EventHandlerRegistry.builder().build()`. Combine all modules into one
context and use its registries everywhere below:

```java
EventContext context = EventContext.builder()
  .addModule(productEventModule)
  .addModule(otherFeatureModule)
  .build();
```

## 5. Write business changes and events together

`OutboxTransactionExecutor` runs your operation and stores the events in the same transaction. Your operation
returns the result and the event to send to each destination station:

```java
BackfillPolicy backfillPolicy = CursorBackfillPolicy.builder(backfillJobStepRepository, backfillRegistry, cursorCodec)
  .backfill(CategoryUpserted.class, CategoryCursor.class)
    .affectedBy(CategoryUpserted.class, CategoryUpserted::code, CategoryCursor::lastCode)
    .affectedBy(CategoryDeleted.class, CategoryDeleted::code, CategoryCursor::lastCode)
    .done()
  .backfill(ProductUpserted.class, ProductCursor.class)
    .affectedBy(ProductUpserted.class, ProductUpserted::id, ProductCursor::lastId, Long::compare)
    .affectedBy(ProductDeleted.class, ProductDeleted::id, ProductCursor::lastId, Long::compare)
    .done()
  .build();

OutboxTransactionExecutor outboxExecutor = new OutboxTransactionExecutor(
  outboxRepository,
  backfillPolicy,
  transactionExecutor,
  replicationGate,
  context.eventRegistry(),
  eventSerializer
);

Product saved = outboxExecutor.execute(() -> {
  Product product = productRepository.upsert(input);
  ProductUpserted event = new ProductUpserted(product.sku(), product.name(), product.price());
  Map<Long, ProductUpserted> eventsByDestination = Map.of(1L, event, 2L, event);
  return new TransactionalOperationResult<>(product, eventsByDestination);
}).result();
```

Each map entry is one message: destination station ID → event. A station that isn't in the map gets nothing. If a
transaction is already open, the executor joins it.

The executor runs your operation in a transaction with the gate held shared. Work out the destinations inside the
operation, not before it, so a station added by a backfill job is seen by every write that starts after the job is
created.

The `BackfillPolicy` decides what a write does with its events for a destination that is being backfilled:

- `CursorBackfillPolicy` lets writes run. An event is skipped if the backfill has not reached its row yet, because the
  backfill will copy that row later. Other events are sent as usual. See `affectedBy` below, and
  [docs/outbox/backfill/cursor-backfill-policy.md](docs/outbox/backfill/cursor-backfill-policy.md) for why writes need not wait for the backfill.
- `BlockingBackfillPolicy` rejects the write with `DestinationNotReadyException` until the backfill finishes. It needs no
  `affectedBy`, so use it when your events are hard to map to row keys.
- Your own implementation, for any other rule. It runs inside the write's transaction, before the events go to the
  outbox.

The policy also decides which jobs can run together, in `validate`. `CursorBackfillPolicy` rejects a job with
`BackfillAlreadyRunningException` if the destination already has an unfinished step of one of its event types, because
it checks events against one cursor per type. `BlockingBackfillPolicy` accepts it, so for example two jobs can copy
different subsets of the same event type at once.

For `CursorBackfillPolicy`, name each backfilled event with the cursor class its source uses, then use `affectedBy` to list
the live events that change the rows that source copies, including the backfilled event itself. `done()` throws if the
backfilled event is missing, and `build()` throws if an event registered for backfill has no rules. For each one, give how to read the row key from the event and from the cursor. An event
whose key is greater than the cursor's is skipped, because the backfill will copy that row later. Keys are compared in
natural order, or with a comparator such as `Long::compare`. For other cases, such as keys made of several columns,
pass a `CursorEventPredicate` instead.

The comparison must order keys exactly as the source's `ORDER BY` does. Strings compare by character code in Java, but
many database collations ignore case or accents, so order string keys with a binary collation in the source, or page by
a numeric key.

## 6. Publish the outbox

Implement `OutboxTransporter` to deliver a batch of outbox rows to a station (for example over HTTP). Throw
`OutboxTransportException` if delivery fails, and the batch is retried on the next run.

```java
OutboxTransporter transporter = messages -> { /* send the batch to the station */ };

for (long stationId : stationIds) {
  scheduler.submit(new PollingOutboxPublisher(transactionExecutor, outboxRepository, transporter, 100, stationId));
}
```

Delivery is at least once, so the receiving side must handle a batch it has seen before. The transaction stays
open while sending, so set its timeout longer than the transporter's.

## 7. Receive into the inbox

Replica doesn't include the endpoint that receives a batch. In your endpoint, turn each delivered outbox row into
an `Inbox` row and save it:

```java
Inbox inbox = new Inbox();
inbox.setSourceId(sendingStationId);
inbox.setMessageId(outbox.getId());
inbox.setEventType(outbox.getEventType());
inbox.setPayload(outbox.getPayload());
inbox.setCreatedAt(outbox.getCreatedAt());
inbox.setReceivedAt(System.currentTimeMillis());

inboxRepository.saveAll(List.of(inbox));
```

`saveAll` skips rows that were already received (same `sourceId` and `messageId`) and returns the ones it stored,
so a batch that is delivered again can be saved again. Acknowledge the batch either way.

## 8. Apply inbox events

```java
scheduler.submit(new PollingInboxProcessor(
  transactionExecutor,
  inboxRepository,
  context.eventRegistry(),
  eventDeserializer,
  context.eventHandlerRegistry(),
  100,
  sourceStationId
));
```

Each message is applied in its own transaction with its handler. If a handler throws, that message is retried on
the next run, and later messages wait behind it so the order is kept.

## 9. Backfill a new station

The live outbox only carries future changes. To copy existing data to a station, backfill it.

Implement an `EventSource` that reads your data in pages:

```java
public record ProductCursor(long lastId) implements Cursor {}

public class ProductEventSource implements EventSource<ProductUpserted, ProductCursor> {
  @Override
  public EventBatch<ProductUpserted, ProductCursor> getNextBatch(BackfillJob job, @Nullable ProductCursor cursor, int limit) {
    long afterId = cursor != null ? cursor.lastId() : 0;
    List<Product> rows = productRepository.findAllAfterId(afterId, limit);
    List<ProductUpserted> events = rows.stream().map(ProductUpserted::from).toList();
    long lastId = rows.isEmpty() ? afterId : rows.getLast().id();
    return new EventBatch<>(events, new ProductCursor(lastId), rows.size() == limit);
  }

  @Override
  public Class<ProductCursor> getCursorClass() {
    return ProductCursor.class;
  }
}
```

- Page by a stable key (usually the ID). Return `hasMore = false` on the last batch.
- Read the rows without locking them (no `FOR SHARE` or `FOR UPDATE`). The runner locks the step before calling
  the source, and a write holding a row lock may be waiting for that step lock.
- Run the backfill at READ COMMITTED, so a batch sees every change committed before it locked the step.
- Provide a `CursorCodec` that turns a cursor into the stored checkpoint string and back.

Register the sources, plan the order of the steps, create the job, and run it:

```java
BackfillPlan plan = BackfillPlan.planner()
  .addEvent(CategoryUpserted.class)
  .addEvent(ProductUpserted.class, CategoryUpserted.class)
  .plan();

BackfillJobService backfillJobService = new BackfillJobService(
  transactionExecutor,
  replicationGate,
  backfillJobRepository,
  backfillJobStepRepository,
  context.eventRegistry(),
  backfillPolicy
);

BackfillJob job = backfillJobService.create(stationId, plan).job();

BackfillRegistry backfillRegistry = BackfillRegistry.builder(context.eventRegistry())
  .register(CategoryUpserted.class, categoryEventSource)
  .register(ProductUpserted.class, productEventSource)
  .build();

BackfillJobRunner runner = new BackfillJobRunner(
  backfillJobRepository,
  backfillJobStepRepository,
  context.eventRegistry(),
  backfillRegistry,
  transactionExecutor,
  eventSerializer,
  outboxRepository,
  cursorCodec,
  500
);

runner.run(job.getId());
```

Pass the event types an event depends on after it, such as `CategoryUpserted` for `ProductUpserted` whose rows point
at categories. The planner copies those first, in any order they are added.

A job can be created at any time, not only when a station is added, for example to copy a new event type to a station
that is already receiving changes. Creating it takes the gate exclusively, so call it in the same transaction that
records anything else the job depends on, such as a new station. It waits for the writes in progress to commit, and
every later write sees the job's pending steps.

## 10. Run it

Run `PollingOutboxPublisher`, `PollingInboxProcessor` and `BackfillJobRunner` from your own scheduler. Replica
doesn't schedule anything. They can run many times at once, from one server or several, and only one call sends
or applies each row. Call them outside a transaction: they open their own and throw if one is already open.
