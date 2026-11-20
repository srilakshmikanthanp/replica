package com.srilakshmikanthanp.replica.outbox.backfill;

import com.srilakshmikanthanp.replica.event.Event;

@FunctionalInterface
public interface CursorEventPredicate<C extends Cursor, E extends Event> {
  boolean isBeyondCursor(C cursor, E event);
}
