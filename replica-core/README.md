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
as building an event from rows you read after locking them. [docs/ordering.md](docs/ordering.md) has the
reasoning.

Provide an `EventSerializer` and `EventDeserializer` that turn an event into the payload string and back
(for example JSON).

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
OutboxTransactionExecutor outboxExecutor = new OutboxTransactionExecutor(
  outboxRepository,
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
    List<Product> rows = productRepository.findAllAfterIdForShare(afterId, limit);
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
- Lock the rows you read (`FOR SHARE`), so a concurrent write to them gets a later outbox ID than the backfill.
- Provide a `CursorCodec` that turns a cursor into the stored checkpoint string and back.

Plan the order of the steps, create the job, and run it:

```java
BackfillPlan plan = BackfillPlan.planner()
  .addEvent(CategoryUpserted.class)
  .addEvent(ProductUpserted.class)
  .plan();

BackfillJobService backfillJobService = new BackfillJobService(transactionExecutor, backfillJobRepository, backfillJobStepRepository, context.eventRegistry());
BackfillJob job = backfillJobService.create(stationId, plan).job();

BackfillRegistry backfillRegistry = BackfillRegistry.builder()
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

Annotate an event with `@EventDependsOn(CategoryUpserted.class)` to make the planner put categories first. A run
can be repeated: it continues from the last committed batch. Backfilled events go through the normal outbox to the
job's station, so steps 6 to 8 deliver them.

Adding a station safely also means taking `replicationGate.enterExclusive()` in the same transaction that records the
station and creates the job, and keeping live changes away from the station until the copy is done. The playground app
shows how: see `StationService`, `StationBackfill` and `StationReadinessService`.

## 10. Run it

Run `PollingOutboxPublisher`, `PollingInboxProcessor` and `BackfillJobRunner` from your own scheduler. Replica
doesn't schedule anything. They can run many times at once, from one server or several, and only one call sends
or applies each row. Call them outside a transaction: they open their own and throw if one is already open.
