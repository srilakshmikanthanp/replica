package com.srilakshmikanthanp.replica.outbox.backfill;

import com.srilakshmikanthanp.replica.event.Event;
import com.srilakshmikanthanp.replica.event.EventRegistry;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
public class BackfillRegistryBuilder {
  private final BackfillRegistry registry;
  private final EventRegistry eventRegistry;

  public <T extends Event, C extends Cursor> BackfillRegistrationBuilder<C> register(
    Class<T> eventClass,
    EventSource<T, C> eventSource
  ) {
    Map<Class<? extends Event>, CursorEventPredicate<Cursor, Event>> affected = new LinkedHashMap<>();

    if (eventRegistry.findEventTypeByEventClass(eventClass).isEmpty()) {
      throw new IllegalArgumentException(eventClass.getName() + " is not a registered event type");
    }

    if (registry.registry.containsKey(eventClass)) {
      throw new IllegalArgumentException(eventClass.getName() + " is registered twice");
    }

    registry.registry.put(eventClass, eventSource);
    registry.affected.put(eventClass, affected);

    return new BackfillRegistrationBuilder<>(
      this,
      eventClass,
      eventSource.getCursorClass(),
      affected
    );
  }

  public BackfillRegistry build() {
    return registry;
  }
}
