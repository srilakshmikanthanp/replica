package com.srilakshmikanthanp.replica.outbox.backfill;

import com.srilakshmikanthanp.replica.event.Event;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class BlockingBackfillPolicy implements BackfillPolicy {
  private final BackfillJobStepRepository backfillJobStepRepository;

  @Override
  public void validate(long destinationId, List<BackfillJobStep> steps) {
    // No live event reaches a destination while it is backfilled, so jobs of the same event type can overlap.
  }

  @Override
  public <E extends Event> Map<Long, E> filter(Map<Long, E> eventsByDestination) {
    if (eventsByDestination.isEmpty()) {
      return eventsByDestination;
    }

    Set<Long> backfilling = backfillJobStepRepository.findUnfinishedStepsForShare(eventsByDestination.keySet()).keySet();

    if (!backfilling.isEmpty()) {
      throw new DestinationNotReadyException(backfilling.iterator().next());
    }

    return eventsByDestination;
  }
}
