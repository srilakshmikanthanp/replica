package com.srilakshmikanthanp.replica.outbox.backfill;

import com.srilakshmikanthanp.replica.event.Event;

public interface EventSource<T extends Event, C extends Cursor> {
  /**
   * Get the next batch of events from the event source.
   *
   * <p>Called inside the transaction that writes the batch to the outbox, after the step is locked. Read the rows
   * without locking them: a write holding a row lock may be waiting for the step lock, so locking rows here could
   * deadlock.</p>
   *
   * @param job    The backfill job being run. Modules keep their own job parameters in their own table, keyed by the job ID.
   * @param cursor The cursor indicating the position to start fetching events from, will be null for the first batch.
   * @param limit  The maximum number of events to fetch.
   * @return An EventBatch containing the fetched events and the updated cursor.
   */
  EventBatch<T, C> getNextBatch(BackfillJob job, C cursor, int limit);

  /**
   * Get the class of the cursor used by this event source.
   *
   * @return The class of the cursor used by this event source.
   */
  Class<C> getCursorClass();
}
