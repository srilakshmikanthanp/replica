package com.srilakshmikanthanp.replica.outbox.backfill;

import com.srilakshmikanthanp.replica.event.Event;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BackfillDestinationFilter {
  private final Map<Class<? extends Event>, BackfillJobStep> stepByBackfillEvent;
  private final BackfillRegistry backfillRegistry;
  private final CursorCodec cursorCodec;

  private boolean isBeyondCursor(BackfillJobStep step, Class<? extends Event> backfillEvent, Event event) {
    if (step.getCheckpoint().isEmpty()) return true;
    Cursor cursor = cursorCodec.decode(step.getCheckpoint().get(), backfillRegistry.getEventSource(backfillEvent).getCursorClass());
    return backfillRegistry.findPolicy(backfillEvent, event.getClass()).orElseThrow().isBeyondCursor(cursor, event);
  }

  public BackfillDestinationFilter(List<BackfillJobStep> steps, BackfillRegistry backfillRegistry, CursorCodec cursorCodec) {
    this.stepByBackfillEvent = steps.stream().collect(Collectors.toMap(step -> backfillRegistry.getBackfillEvent(step.getEventType()), Function.identity()));
    this.backfillRegistry = backfillRegistry;
    this.cursorCodec = cursorCodec;
  }

  public boolean shouldSend(Event event) {
    List<Class<? extends Event>> backfillEvents = backfillRegistry.findBackfillEventsAffectedBy(event.getClass());

    if (backfillEvents.isEmpty()) {
      return true;
    }

    return backfillEvents.stream().anyMatch(backfillEvent -> {
      BackfillJobStep step = stepByBackfillEvent.get(backfillEvent);
      return step == null || !isBeyondCursor(step, backfillEvent, event);
    });
  }
}
