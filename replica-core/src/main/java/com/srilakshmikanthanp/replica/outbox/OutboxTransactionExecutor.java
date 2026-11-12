package com.srilakshmikanthanp.replica.outbox;

import com.srilakshmikanthanp.replica.database.transaction.TransactionExecutor;
import com.srilakshmikanthanp.replica.event.*;
import com.srilakshmikanthanp.replica.database.gate.ReplicationGate;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class OutboxTransactionExecutor {
	private final OutboxRepository outboxRepository;
	private final TransactionExecutor transactionExecutor;
	private final ReplicationGate replicationGate;
	private final EventRegistry eventRegistry;
	private final EventSerializer eventSerializer;

	public record Result<R>(
		R result,
		List<Outbox> outbox
	) {}

	public <R, E extends Event> Result<R> execute(
		TransactionalOperation<R, E> operation
	) {
		return transactionExecutor.execute(() -> {
      List<Outbox> outboxes = new ArrayList<>();
			replicationGate.enterShared();
			var result = operation.execute();

			for (Map.Entry<Long, E> entry : result.eventsByDestination().entrySet()) {
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

			return new Result<>(
				result.result(),
				outboxes
			);
		});
	}
}
