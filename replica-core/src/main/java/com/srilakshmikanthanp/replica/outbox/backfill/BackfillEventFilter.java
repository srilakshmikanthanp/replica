package com.srilakshmikanthanp.replica.outbox.backfill;

import com.srilakshmikanthanp.replica.event.Event;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BackfillEventFilter {
  private final BackfillJobStepRepository backfillJobStepRepository;
  private final BackfillRegistry backfillRegistry;
  private final CursorCodec cursorCodec;

  public <E extends Event> Map<Long, E> filter(Map<Long, E> eventsByDestination) {
    Set<Long> affectedDestinations = eventsByDestination.entrySet().stream()
      .filter(entry -> !backfillRegistry.findBackfillEventsAffectedBy(entry.getValue().getClass()).isEmpty())
      .map(Map.Entry::getKey)
      .collect(Collectors.toSet());

    if (affectedDestinations.isEmpty()) {
      return eventsByDestination;
    }

    Map<Long, List<BackfillJobStep>> stepsByDestination = backfillJobStepRepository.findUnfinishedStepsForShare(affectedDestinations);
    Map<Long, E> kept = new LinkedHashMap<>();

    for (Map.Entry<Long, E> entry : eventsByDestination.entrySet()) {
      BackfillDestinationFilter destination = new BackfillDestinationFilter(stepsByDestination.getOrDefault(entry.getKey(), List.of()), backfillRegistry, cursorCodec);

      if (destination.shouldSend(entry.getValue())) {
        kept.put(entry.getKey(), entry.getValue());
      }
    }

    return kept;
  }
}
