package com.srilakshmikanthanp.replica.inbox;

import com.srilakshmikanthanp.replica.crypto.PayloadCryptography;

import java.util.List;

public interface InboxRepository {
	List<Inbox> findUnprocessed(long sourceId, int limit);

	/**
	 * Inserts the rows that have not been received yet and skips the rest: a row whose source ID and message ID
	 * already exist is left unchanged, and a repeat inside the batch counts once. So a batch that is delivered
	 * again can be saved again.
	 * <p>
	 * Payloads are stored encrypted if the implementation has a {@link PayloadCryptography}, and the given objects
	 * keep their plain payload.
	 *
	 * @return the rows that were inserted, with their generated IDs set
	 */
	List<Inbox> saveAll(List<Inbox> messages);

	/**
	 * Marks the row processed only if it is still unprocessed. The update locks the row until the transaction ends.
	 *
	 * @return false if another call has already marked it
	 */
	boolean markAsProcessed(Inbox message);
}
