package com.srilakshmikanthanp.replica.outbox.backfill;

import com.srilakshmikanthanp.replica.event.Event;

public interface EventSource<T extends Event, C extends Cursor> {
	/**
	 * Get the next batch of events from the event source.
	 *
	 * <p>Called inside the transaction that writes the batch to the outbox. Lock the rows read
	 * (for example with {@code SELECT ... FOR SHARE}) so that concurrent writes to them are
	 * ordered after this batch.</p>
	 *
	 * @param job The backfill job being run. Modules keep their own job parameters in their own table, keyed by the job ID.
	 * @param cursor The cursor indicating the position to start fetching events from, will be null for the first batch.
	 * @param limit The maximum number of events to fetch.
	 * @return An EventBatch containing the fetched events and the updated cursor.
	 */
	EventBatch<T, C> getNextBatch(BackfillJob job, C cursor, int limit);

	/**
	 * Get the class of the cursor used by this event source.
	 * @return The class of the cursor used by this event source.
	 */
	Class<C> getCursorClass();
}
