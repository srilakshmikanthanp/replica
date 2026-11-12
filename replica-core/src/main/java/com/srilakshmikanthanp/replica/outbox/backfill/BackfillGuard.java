package com.srilakshmikanthanp.replica.outbox.backfill;

import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor
public class BackfillGuard {
  private final BackfillJobStepRepository backfillJobStepRepository;

  public void requireReady(Collection<Long> destinationIds) {
    for (long destinationId : destinationIds) {
      if (backfillJobStepRepository.hasPendingSteps(destinationId)) {
        throw new DestinationNotReadyException(destinationId);
      }
    }
  }
}
