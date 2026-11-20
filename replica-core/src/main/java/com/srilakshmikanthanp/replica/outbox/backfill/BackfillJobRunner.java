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

/**
 * Runs a backfill job's steps in ordinal order, writing each batch of events to the outbox. Safe to
 * call many times at once for the same job, from one server or several: only one call writes each
 * batch.
 * <p>
 * Each batch runs in its own transaction, which first locks the step until commit. Only then does it read the batch
 * from the source, so a write that changes the step's rows never decides against the checkpoint while the batch reads
 * them and moves it. If the step is no longer at the checkpoint this call last saw, another call has moved it, so this
 * one stops without writing anything. Otherwise the batch's outbox rows
 * and the new checkpoint (completed on the last batch) commit together.
 * <p>
 * Run it at READ COMMITTED, so the rows read after the lock include every change committed before it.
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

  private EventSource<? extends Event, ? extends Cursor> getEventSource(BackfillJobStep step) {
    return backfillRegistry.getEventSource(eventRegistry.getEventClassByEventType(step.getEventType()));
  }

  private <T extends Event, C extends Cursor> boolean processNextBatch(BackfillJob job, BackfillJobStep step, EventSource<T, C> source) {
    return transactionExecutor.executeInNewTransaction(() -> {
      boolean stillAt = backfillJobStepRepository.findStepForUpdate(step.getId())
        .map(locked -> locked.getCompletedAt().isEmpty() && locked.getCheckpoint().equals(step.getCheckpoint()))
        .orElse(false);

      if (!stillAt) {
        return false;
      }

      C cursor = step.getCheckpoint()
        .map(checkpoint -> cursorCodec.decode(checkpoint, source.getCursorClass()))
        .orElse(null);

      EventBatch<T, C> batch = source.getNextBatch(job, cursor, batchSize);
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

      if (!backfillJobStepRepository.advance(step, cursorCodec.encode(batch.cursor()), !batch.hasMore())) {
        throw new IllegalStateException("Backfill step " + step.getId() + " moved while it was locked");
      }

      return true;
    });
  }

  public boolean runNextBatch(BackfillJob job) {
    for (var step : backfillJobStepRepository.getAllByJobId(job.getId())) {
      if (step.getCompletedAt().isPresent()) continue;
      return processNextBatch(job, step, getEventSource(step));
    }
    return false;
  }

  public boolean runNextBatch(long jobId) {
    return this.runNextBatch(backfillJobRepository.getById(jobId));
  }

  public void run(BackfillJob job) {
    while (runNextBatch(job));
  }

  public void run(long jobId) {
    this.run(backfillJobRepository.getById(jobId));
  }
}
