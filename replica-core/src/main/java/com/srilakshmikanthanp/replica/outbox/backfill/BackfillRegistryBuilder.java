package com.srilakshmikanthanp.replica.outbox.backfill;

import com.srilakshmikanthanp.replica.event.Event;
import com.srilakshmikanthanp.replica.event.EventRegistry;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BackfillRegistryBuilder {
  private final BackfillRegistry registry;
  private final EventRegistry eventRegistry;

  public <T extends Event, C extends Cursor> BackfillRegistryBuilder register(Class<T> eventClass, EventSource<T, C> eventSource) {
    if (eventRegistry.findEventTypeByEventClass(eventClass).isEmpty()) {
      throw new IllegalArgumentException(eventClass.getName() + " is not a registered event type");
    }

    if (registry.hasEventSource(eventClass)) {
      throw new IllegalArgumentException(eventClass.getName() + " is registered twice");
    }

    registry.addEventSource(eventClass, eventSource);
    return this;
  }

  public BackfillRegistry build() {
    return registry;
  }
}
