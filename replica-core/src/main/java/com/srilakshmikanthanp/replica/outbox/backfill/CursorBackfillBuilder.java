package com.srilakshmikanthanp.replica.outbox.backfill;

import com.srilakshmikanthanp.replica.event.Event;

import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;

public class CursorBackfillBuilder<C extends Cursor> {
  private final CursorBackfillPolicyBuilder parent;
  private final Class<? extends Event> backfilledClass;
  private final Class<C> cursorClass;
  private final Map<Class<? extends Event>, CursorEventPredicate<Cursor, Event>> affected;

  CursorBackfillBuilder(CursorBackfillPolicyBuilder parent, Class<? extends Event> backfilledClass, Class<C> cursorClass, Map<Class<? extends Event>, CursorEventPredicate<Cursor, Event>> affected) {
    this.parent = parent;
    this.backfilledClass = backfilledClass;
    this.cursorClass = cursorClass;
    this.affected = affected;
  }

  public <E extends Event> CursorBackfillBuilder<C> affectedBy(Class<E> eventClass, CursorEventPredicate<? super C, ? super E> predicate) {
    if (affected.containsKey(eventClass)) {
      throw new IllegalArgumentException(eventClass.getName() + " is added twice to the backfill of " + backfilledClass.getName());
    }

    affected.put(eventClass, (cursor, event) -> predicate.isBeyondCursor(cursorClass.cast(cursor), eventClass.cast(event)));
    return this;
  }

  public <E extends Event, K> CursorBackfillBuilder<C> affectedBy(
    Class<E> eventClass,
    Function<? super E, ? extends K> eventKey,
    Function<? super C, ? extends K> cursorKey,
    Comparator<? super K> comparator
  ) {
    return affectedBy(eventClass, (cursor, event) -> comparator.compare(eventKey.apply(event), cursorKey.apply(cursor)) > 0);
  }

  public <E extends Event, K extends Comparable<? super K>> CursorBackfillBuilder<C> affectedBy(
    Class<E> eventClass,
    Function<? super E, ? extends K> eventKey,
    Function<? super C, ? extends K> cursorKey
  ) {
    return affectedBy(eventClass, eventKey, cursorKey, Comparator.naturalOrder());
  }

  public CursorBackfillPolicyBuilder done() {
    // A live event of the backfilled type changes the rows being copied, so it must take the step lock like any other.
    if (!affected.containsKey(backfilledClass)) {
      throw new IllegalArgumentException(backfilledClass.getName() + " must be listed in its own affectedBy");
    }

    return parent;
  }
}
