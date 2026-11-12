package com.srilakshmikanthanp.replica.outbox;

import com.srilakshmikanthanp.replica.database.transaction.TransactionExecutor;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Sends a destination's outbox rows in ID order. Safe to call many times at once, from one server or
 * several: only one call sends each row.
 * <p>
 * Each batch is sent in its own transaction, whose first statement claims it:
 * {@code UPDATE ... SET PUBLISHED_AT = ? WHERE ID = ? AND PUBLISHED_AT IS NULL}, for each row in ID
 * order. The updates lock the rows until the send commits, so another call on the same rows waits on
 * the lowest one, then updates 0 rows for them and sends only rows that come after.
 * <p>
 * Order holds because every call reads the lowest unpublished rows and claims them in ascending order.
 * None can get past a row another call is still sending. If the send throws, the claim rolls back and
 * the rows are sent again on the next call, in the same order. The inbox drops the duplicates.
 * <p>
 * The transaction stays open during the send, so its timeout must be longer than the transporter's.
 * Call it outside a transaction: {@code runInNewTransaction} throws if one is already open.
 */
@RequiredArgsConstructor
public class PollingOutboxPublisher implements Callable<Void> {
	private final TransactionExecutor transactionExecutor;
	private final OutboxRepository outboxRepository;
	private final OutboxTransporter outboxTransporter;
	private final int batchSize;
	private final long destinationId;

	private void publish(List<Outbox> batch) {
		transactionExecutor.runInNewTransaction(() -> {
			List<Outbox> published = outboxRepository.markAsPublished(batch);
			if (!published.isEmpty()) outboxTransporter.send(published);
		});
	}

	@Override
	public Void call() {
		while (true) {
			List<Outbox> batch = outboxRepository.findUnpublished(destinationId, batchSize);
			if (batch.isEmpty()) break;
			publish(batch);
		}
		return null;
	}
}
