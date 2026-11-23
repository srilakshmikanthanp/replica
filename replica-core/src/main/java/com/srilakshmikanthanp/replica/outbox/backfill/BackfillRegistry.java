package com.srilakshmikanthanp.replica.outbox.backfill;

import com.srilakshmikanthanp.replica.event.Event;
import com.srilakshmikanthanp.replica.event.EventRegistry;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class BackfillRegistry {
  private final Map<Class<? extends Event>, EventSource<? extends Event, ? extends Cursor>> registry = new HashMap<>();
  private final EventRegistry eventRegistry;

  @SuppressWarnings("unchecked")
  public <T extends Event> Optional<EventSource<T, ? extends Cursor>> findEventSource(Class<T> eventClass) {
    return Optional.ofNullable((EventSource<T, ?>) registry.get(eventClass));
  }

  public <T extends Event> EventSource<T, ? extends Cursor> getEventSource(Class<T> eventClass) {
    return this.findEventSource(eventClass).orElseThrow(() -> new IllegalArgumentException("No event source registered for event class: " + eventClass.getName()));
  }

  public Class<? extends Event> getBackfillEvent(String eventType) {
    return eventRegistry.getEventClassByEventType(eventType);
  }

  boolean hasEventSource(Class<? extends Event> eventClass) {
    return registry.containsKey(eventClass);
  }

  void addEventSource(Class<? extends Event> eventClass, EventSource<? extends Event, ? extends Cursor> eventSource) {
    registry.put(eventClass, eventSource);
  }

  public Set<Class<? extends Event>> getBackfillEvents() {
    return registry.keySet();
  }

  public static BackfillRegistryBuilder builder(EventRegistry eventRegistry) {
    return new BackfillRegistryBuilder(new BackfillRegistry(eventRegistry), eventRegistry);
  }
}
