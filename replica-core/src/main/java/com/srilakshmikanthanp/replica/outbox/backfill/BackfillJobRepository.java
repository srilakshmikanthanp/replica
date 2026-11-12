package com.srilakshmikanthanp.replica.outbox.backfill;

import java.util.List;
import java.util.NoSuchElementException;

public interface BackfillJobRepository {
  /**
   * @throws NoSuchElementException if there is no such job
   */
  BackfillJob getById(long jobId);

  List<BackfillJob> findAll();

  /**
   * The jobs that still have a step that is not completed, oldest first.
   */
  List<BackfillJob> findUnfinished();

  /**
   * Inserts the job and sets its generated ID.
   */
  BackfillJob save(BackfillJob job);
}
