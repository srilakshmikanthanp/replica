package com.srilakshmikanthanp.replica.outbox.backfill;

import com.srilakshmikanthanp.replica.event.Event;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class CursorBackfillPolicy implements BackfillPolicy {
  private final BackfillJobStepRepository backfillJobStepRepository;
  private final BackfillRegistry backfillRegistry;
  private final CursorCodec cursorCodec;
  private final CursorRules rules;

  public static CursorBackfillPolicyBuilder builder(BackfillJobStepRepository backfillJobStepRepository, BackfillRegistry backfillRegistry, CursorCodec cursorCodec) {
    return new CursorBackfillPolicyBuilder(backfillJobStepRepository, backfillRegistry, cursorCodec);
  }

  @Override
  public void validate(long destinationId, List<BackfillJobStep> steps) {
    // A destination's events are checked against one cursor per event type, so two unfinished steps of a type cannot coexist.
    new BackfillJobValidator(backfillJobStepRepository).requireNotRunning(destinationId, steps);
  }

  @Override
  public <E extends Event> Map<Long, E> filter(Map<Long, E> eventsByDestination) {
    Set<Long> affectedDestinations = eventsByDestination.entrySet().stream()
      .filter(entry -> !rules.findBackfillEventsAffectedBy(entry.getValue().getClass()).isEmpty())
      .map(Map.Entry::getKey)
      .collect(Collectors.toSet());

    if (affectedDestinations.isEmpty()) {
      return eventsByDestination;
    }

    Map<Long, List<BackfillJobStep>> stepsByDestination = backfillJobStepRepository.findUnfinishedStepsForShare(affectedDestinations);
    Map<Long, E> kept = new LinkedHashMap<>();

    for (Map.Entry<Long, E> entry : eventsByDestination.entrySet()) {
      CursorDestinationFilter destination = new CursorDestinationFilter(stepsByDestination.getOrDefault(entry.getKey(), List.of()), backfillRegistry, rules, cursorCodec);

      if (destination.shouldSend(entry.getValue())) {
        kept.put(entry.getKey(), entry.getValue());
      }
    }

    return kept;
  }
}
