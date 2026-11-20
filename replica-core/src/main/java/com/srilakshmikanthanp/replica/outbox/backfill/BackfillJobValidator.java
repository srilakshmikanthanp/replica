package com.srilakshmikanthanp.replica.outbox.backfill;

import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
class BackfillJobValidator {
  private final BackfillJobStepRepository backfillJobStepRepository;

  void requireNotRunning(long destinationId, List<BackfillJobStep> steps) {
    Set<String> running = new HashSet<>();

    for (BackfillJobStep unfinished : backfillJobStepRepository.findUnfinishedStepsForShare(List.of(destinationId)).getOrDefault(destinationId, List.of())) {
      running.add(unfinished.getEventType());
    }

    Set<String> overlapping = new HashSet<>();

    for (BackfillJobStep step : steps) {
      if (running.contains(step.getEventType())) {
        overlapping.add(step.getEventType());
      }
    }

    if (!overlapping.isEmpty()) {
      throw new BackfillAlreadyRunningException(destinationId, overlapping);
    }
  }
}
