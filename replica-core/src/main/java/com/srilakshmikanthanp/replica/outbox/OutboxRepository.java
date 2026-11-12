package com.srilakshmikanthanp.replica.outbox;

import com.srilakshmikanthanp.replica.crypto.PayloadCryptography;

import java.util.List;

public interface OutboxRepository {
	List<Outbox> findUnpublished(long destinationId, int batchSize);

	/**
	 * Inserts the rows, setting each one's generated ID and creation time. Payloads are stored encrypted if the
	 * implementation has a {@link PayloadCryptography}, and the given objects keep
	 * their plain payload.
	 */
	List<Outbox> saveAll(List<Outbox> messages);

	default Outbox save(Outbox message) {
		return saveAll(List.of(message)).getFirst();
	}

	/**
	 * Marks each row published, in the order given, only if it is still unpublished. The updates lock the rows
	 * until the transaction ends.
	 *
	 * @return the rows this call marked, in the order given
	 */
	List<Outbox> markAsPublished(List<Outbox> messages);
}
