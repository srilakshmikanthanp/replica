package com.srilakshmikanthanp.replica.outbox;

import com.srilakshmikanthanp.replica.database.gate.ReplicationGate;
import com.srilakshmikanthanp.replica.database.transaction.TransactionExecutor;
import com.srilakshmikanthanp.replica.event.Event;
import com.srilakshmikanthanp.replica.event.EventRegistry;
import com.srilakshmikanthanp.replica.event.EventSerializer;
import com.srilakshmikanthanp.replica.outbox.backfill.LiveEventFilter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class OutboxTransactionExecutor {
  private final OutboxRepository outboxRepository;
  private final LiveEventFilter liveEventFilter;
  private final TransactionExecutor transactionExecutor;
  private final ReplicationGate replicationGate;
  private final EventRegistry eventRegistry;
  private final EventSerializer eventSerializer;

  public <R, E extends Event> OutboxTransactionResult<R> execute(
    TransactionalOperation<R, E> operation
  ) {
    return transactionExecutor.execute(() -> {
      List<Outbox> outboxes = new ArrayList<>();

      replicationGate.enterShared();

      var result = operation.execute();

      for (Map.Entry<Long, E> entry : liveEventFilter.filter(result.eventsByDestination()).entrySet()) {
        E event = entry.getValue();
        Outbox outbox = new Outbox();
        outbox.setEventType(eventRegistry.getEventTypeByEventClass(event.getClass()));
        outbox.setPayload(eventSerializer.serialize(event));
        outbox.setDestinationId(entry.getKey());
        outboxes.add(outbox);
      }

      if (!outboxes.isEmpty()) {
        outboxRepository.saveAll(outboxes);
      }

      return new OutboxTransactionResult<>(
        result.result(),
        outboxes
      );
    });
  }
}
