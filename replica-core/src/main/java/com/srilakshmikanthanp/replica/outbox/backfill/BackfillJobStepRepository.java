package com.srilakshmikanthanp.replica.outbox.backfill;

import java.util.List;

public interface BackfillJobStepRepository {
  /**
   * Moves the step to a new checkpoint (and completes it when asked), only if it is still at the checkpoint the
   * given step holds and not yet completed. The update locks the row until the transaction ends.
   *
   * @return false if the step has moved on
   */
  boolean advance(BackfillJobStep step, String checkpoint, boolean completed);

	List<BackfillJobStep> getAllByJobId(long jobId);

	/**
	 * Inserts the steps and sets each one's generated ID.
	 */
	List<BackfillJobStep> saveAll(List<BackfillJobStep> steps);
}
