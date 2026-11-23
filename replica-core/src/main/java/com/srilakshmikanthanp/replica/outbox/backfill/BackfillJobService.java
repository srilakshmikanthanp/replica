package com.srilakshmikanthanp.replica.outbox.backfill;

import com.srilakshmikanthanp.replica.database.gate.ReplicationGate;
import com.srilakshmikanthanp.replica.database.transaction.TransactionExecutor;
import com.srilakshmikanthanp.replica.event.Event;
import com.srilakshmikanthanp.replica.event.EventRegistry;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class BackfillJobService {
  private final TransactionExecutor transactionExecutor;
  private final ReplicationGate replicationGate;
  private final BackfillJobRepository backfillJobRepository;
  private final BackfillJobStepRepository backfillJobStepRepository;
  private final EventRegistry eventRegistry;
  private final NewJobValidator newJobValidator;

  public BackfillJobCreateResult create(long destinationId, BackfillPlan plan) {
    if (plan == null) {
      throw new IllegalArgumentException("plan cannot be null");
    }

    BackfillJob job = new BackfillJob();
    job.setDestinationId(destinationId);

    List<BackfillJobStep> steps = new ArrayList<>();
    int ordinal = 1;

    for (Class<? extends Event> event : plan.orderedEvents()) {
      BackfillJobStep step = new BackfillJobStep();
      step.setOrdinal(ordinal++);
      step.setEventType(eventRegistry.getEventTypeByEventClass(event));
      steps.add(step);
    }

    return transactionExecutor.execute(() -> {
      replicationGate.enterExclusive();
      newJobValidator.validate(destinationId, steps);
      backfillJobRepository.save(job);
      steps.forEach(step -> step.setJobId(job.getId()));
      backfillJobStepRepository.saveAll(steps);
      return new BackfillJobCreateResult(job, steps);
    });
  }

  public List<BackfillJob> findAll() {
    return backfillJobRepository.findAll();
  }

  public List<BackfillJob> findUnfinished() {
    return backfillJobRepository.findUnfinished();
  }

  public BackfillJob getById(long id) {
    return backfillJobRepository.getById(id);
  }

}
