package com.srilakshmikanthanp.replica.outbox.backfill;

import com.srilakshmikanthanp.replica.event.Event;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CursorBackfillPolicyBuilder {
  private final BackfillJobStepRepository backfillJobStepRepository;
  private final BackfillRegistry backfillRegistry;
  private final CursorCodec cursorCodec;
  private final CursorRules rules = new CursorRules();

  public <T extends Event, C extends Cursor> CursorBackfillBuilder<C> backfill(Class<T> eventClass, Class<C> cursorClass) {
    if (backfillRegistry.getEventSource(eventClass).getCursorClass() != cursorClass) {
      throw new IllegalArgumentException("The event source of " + eventClass.getName() + " does not use " + cursorClass.getName());
    }

    if (rules.hasBackfill(eventClass)) {
      throw new IllegalArgumentException(eventClass.getName() + " is added twice");
    }

    return new CursorBackfillBuilder<>(this, eventClass, cursorClass, rules.addBackfill(eventClass));
  }

  public CursorBackfillPolicy build() {
    for (Class<? extends Event> backfilled : backfillRegistry.getBackfillEvents()) {
      if (!rules.hasBackfill(backfilled)) {
        throw new IllegalArgumentException(backfilled.getName() + " is registered for backfill but has no affectedBy rules");
      }
    }

    return new CursorBackfillPolicy(backfillJobStepRepository, backfillRegistry, cursorCodec, rules);
  }
}
