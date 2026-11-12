package com.srilakshmikanthanp.replica.outbox.backfill;

import com.srilakshmikanthanp.replica.database.transaction.TransactionExecutor;
import com.srilakshmikanthanp.replica.event.Event;
import com.srilakshmikanthanp.replica.event.EventRegistry;
import com.srilakshmikanthanp.replica.event.EventSerializer;
import com.srilakshmikanthanp.replica.outbox.Outbox;
import com.srilakshmikanthanp.replica.outbox.OutboxRepository;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Runs a backfill job's steps in ordinal order, writing each batch of events to the outbox. Safe to
 * call many times at once for the same job, from one server or several: only one call writes each
 * batch.
 * <p>
 * Each batch runs in its own transaction. After reading the batch from the source, its first write
 * moves the step to the new checkpoint (and completes it on the last batch), only if the step is still
 * at the checkpoint this call last saw. The update locks the step until commit, so another call on the
 * same step waits, then updates 0 rows, stops without writing anything, and {@link #run(BackfillJob)}
 * returns false. Otherwise the batch's outbox rows and the new checkpoint commit together.
 * <p>
 * Nothing is held between batches. If a call dies, its open batch rolls back, and the next call
 * continues from the last committed checkpoint.
 * <p>
 * Call it outside a transaction: {@code executeInNewTransaction} throws if one is already open.
 */
@RequiredArgsConstructor
public class BackfillJobRunner {
	private final BackfillJobRepository backfillJobRepository;
	private final BackfillJobStepRepository backfillJobStepRepository;
	private final EventRegistry eventRegistry;
	private final BackfillRegistry backfillRegistry;
	private final TransactionExecutor transactionExecutor;
	private final EventSerializer eventSerializer;
	private final OutboxRepository outboxRepository;
	private final CursorCodec cursorCodec;
	private final int batchSize;

	private <T extends Event, C extends Cursor> Optional<EventBatch<T, C>> processNextBatch(BackfillJob job, BackfillJobStep step, EventSource<T, C> source, C cursor) {
		return transactionExecutor.executeInNewTransaction(() -> {
			EventBatch<T, C> batch = source.getNextBatch(job, cursor, batchSize);

			if (!backfillJobStepRepository.advance(step, cursorCodec.encode(batch.cursor()), !batch.hasMore())) {
				return Optional.empty();
			}

			List<Outbox> outboxes = new ArrayList<>();

			for (Event event : batch.events()) {
				Outbox outbox = new Outbox();
				outbox.setDestinationId(job.getDestinationId());
				outbox.setEventType(eventRegistry.getEventTypeByEventClass(event.getClass()));
				outbox.setPayload(eventSerializer.serialize(event));
				outboxes.add(outbox);
			}

			if (!outboxes.isEmpty()) {
				outboxRepository.saveAll(outboxes);
			}

			return Optional.of(batch);
		});
	}

	private <T extends Event, C extends Cursor> boolean run(BackfillJob job, BackfillJobStep step, EventSource<T, C> source) {
		C cursor = step.getCheckpoint()
			.map(checkpoint -> cursorCodec.decode(checkpoint, source.getCursorClass()))
			.orElse(null);

		while (true) {
			Optional<EventBatch<T, C>> batch = processNextBatch(job, step, source, cursor);
			if (batch.isEmpty()) return false;
			if (!batch.get().hasMore()) return true;
			cursor = batch.get().cursor();
		}
	}

	public boolean run(BackfillJob job) {
		for (var step : backfillJobStepRepository.getAllByJobId(job.getId())) {
			if (step.getCompletedAt().isPresent()) continue;
			Class<? extends Event> eventClass = eventRegistry.getEventClassByEventType(step.getEventType());
			EventSource<? extends Event, ? extends Cursor> eventSource = backfillRegistry.getEventSource(eventClass);
			if (!this.run(job, step, eventSource)) return false;
		}
		return true;
	}

	public boolean run(long jobId) {
		return this.run(backfillJobRepository.getById(jobId));
	}
}
