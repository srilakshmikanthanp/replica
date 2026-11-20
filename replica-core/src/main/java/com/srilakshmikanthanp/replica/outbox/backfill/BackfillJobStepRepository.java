package com.srilakshmikanthanp.replica.outbox.backfill;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BackfillJobStepRepository {
  /**
   * Locks the unfinished steps of the destinations, shared and until the transaction ends, and returns them as stored,
   * by destination.
   *
   * A write calls it before deciding which events the backfill will copy anyway, so the checkpoints it reads cannot
   * move until it commits. The steps are locked in ID order, so writes never deadlock each other. A database without
   * shared row locks may lock them exclusively. Must be called inside a transaction.
   */
  Map<Long, List<BackfillJobStep>> findUnfinishedStepsForShare(Collection<Long> destinationIds);

  /**
   * Locks the step exclusively until the transaction ends and returns it as stored. The backfill runner calls it before
   * reading a batch, so no write can decide against the step's checkpoint while the batch reads rows and moves it.
   * Must be called inside a transaction.
   *
   * @return empty if the step does not exist
   */
  Optional<BackfillJobStep> findStepForUpdate(long stepId);

  /**
   * Moves the step to a new checkpoint (and completes it when asked), only if it is still at the checkpoint the
   * given step holds and not yet completed. The update locks the row until the transaction ends.
   *
   * @return false if the step has moved on
   */
  boolean advance(BackfillJobStep step, String checkpoint, boolean completed);

  /**
   * @return all steps of the job
   */
  List<BackfillJobStep> getAllByJobId(long jobId);

  /**
   * Inserts the steps and sets each one's generated ID.
   */
  List<BackfillJobStep> saveAll(List<BackfillJobStep> steps);
}
