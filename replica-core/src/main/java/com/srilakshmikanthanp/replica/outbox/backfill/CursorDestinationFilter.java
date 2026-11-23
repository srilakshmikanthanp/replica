package com.srilakshmikanthanp.replica.outbox.backfill;

import com.srilakshmikanthanp.replica.event.Event;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

class CursorDestinationFilter {
  private final Map<Class<? extends Event>, BackfillJobStep> stepByBackfillEvent;
  private final BackfillRegistry backfillRegistry;
  private final CursorRules rules;
  private final CursorCodec cursorCodec;

  private boolean isBeyondCursor(BackfillJobStep step, Class<? extends Event> backfillEvent, Event event) {
    if (step.getCheckpoint().isEmpty()) return true;
    Cursor cursor = cursorCodec.decode(step.getCheckpoint().get(), backfillRegistry.getEventSource(backfillEvent).getCursorClass());
    return rules.findPredicate(backfillEvent, event.getClass()).orElseThrow().isBeyondCursor(cursor, event);
  }

  CursorDestinationFilter(List<BackfillJobStep> steps, BackfillRegistry backfillRegistry, CursorRules rules, CursorCodec cursorCodec) {
    this.stepByBackfillEvent = steps.stream().collect(Collectors.toMap(step -> backfillRegistry.getBackfillEvent(step.getEventType()), Function.identity()));
    this.backfillRegistry = backfillRegistry;
    this.rules = rules;
    this.cursorCodec = cursorCodec;
  }

  boolean shouldSend(Event event) {
    List<Class<? extends Event>> backfillEvents = rules.findBackfillEventsAffectedBy(event.getClass());

    if (backfillEvents.isEmpty()) {
      return true;
    }

    return backfillEvents.stream().anyMatch(backfillEvent -> {
      BackfillJobStep step = stepByBackfillEvent.get(backfillEvent);
      return step == null || !isBeyondCursor(step, backfillEvent, event);
    });
  }
}
