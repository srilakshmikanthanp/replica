package com.srilakshmikanthanp.replica.spring.outbox.backfill;

import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobStep;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobStepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.support.TransactionOperations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class BackfillJobStepSpringRepository implements BackfillJobStepRepository {
  private final BackfillJobStepJpaRepository jpaRepository;
  private final TransactionOperations transactionOperations;

  private static BackfillJobStepEntity toEntity(BackfillJobStep step) {
    BackfillJobStepEntity entity = new BackfillJobStepEntity();
    entity.setJobId(step.getJobId());
    entity.setOrdinal(step.getOrdinal());
    entity.setEventType(step.getEventType());
    entity.setCheckpoint(step.getCheckpoint().orElse(null));
    entity.setCompletedAt(step.getCompletedAt().orElse(null));
    return entity;
  }

  private static BackfillJobStep toModel(BackfillJobStepEntity entity) {
    BackfillJobStep step = new BackfillJobStep();
    step.setId(entity.getId());
    step.setJobId(entity.getJobId());
    step.setOrdinal(entity.getOrdinal());
    step.setEventType(entity.getEventType());
    step.setCheckpoint(entity.getCheckpoint());
    step.setCompletedAt(entity.getCompletedAt());
    return step;
  }

  @Override
  public Map<Long, List<BackfillJobStep>> findUnfinishedStepsForShare(Collection<Long> destinationIds) {
    if (destinationIds.isEmpty()) {
      return Map.of();
    }

    Map<Long, List<BackfillJobStep>> stepsByDestination = new HashMap<>();

    for (UnfinishedStep unfinished : jpaRepository.findUnfinishedForShare(destinationIds)) {
      stepsByDestination.computeIfAbsent(unfinished.getDestinationId(), id -> new ArrayList<>()).add(toModel(unfinished.getStep()));
    }

    return stepsByDestination;
  }

  @Override
  public Optional<BackfillJobStep> findStepForUpdate(long stepId) {
    return jpaRepository.findByIdForUpdate(stepId).map(BackfillJobStepSpringRepository::toModel);
  }

  @Override
  public List<BackfillJobStep> getAllByJobId(long jobId) {
    if (jobId <= 0) {
      throw new IllegalArgumentException("jobId must be greater than 0");
    }

    return jpaRepository
      .findAllByJobIdOrderByOrdinalAsc(jobId)
      .stream()
      .map(BackfillJobStepSpringRepository::toModel)
      .toList();
  }

  @Override
  public List<BackfillJobStep> saveAll(List<BackfillJobStep> steps) {
    List<BackfillJobStepEntity> entities = steps.stream().map(BackfillJobStepSpringRepository::toEntity).toList();

    transactionOperations.executeWithoutResult(status -> jpaRepository.saveAll(entities));

    for (int i = 0; i < steps.size(); i++) {
      steps.get(i).setId(entities.get(i).getId());
    }

    return steps;
  }

  @Override
  public boolean advance(BackfillJobStep step, String checkpoint, boolean completed) {
    Long completedAt = completed ? System.currentTimeMillis() : null;
    String expected = step.getCheckpoint().orElse(null);

    Integer updated = transactionOperations.execute(status -> jpaRepository.advance(step.getId(), expected, checkpoint, completedAt));

    if (updated == null || updated == 0) {
      return false;
    }

    step.setCheckpoint(checkpoint);
    step.setCompletedAt(completedAt);
    return true;
  }
}
