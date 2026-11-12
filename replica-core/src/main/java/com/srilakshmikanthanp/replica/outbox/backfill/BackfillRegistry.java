package com.srilakshmikanthanp.replica.outbox.backfill;

import com.srilakshmikanthanp.replica.event.Event;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BackfillRegistry {
  final Map<Class<? extends Event>, EventSource<? extends Event, ? extends Cursor>> registry = new HashMap<>();

  public static BackfillRegistryBuilder builder() {
    return new BackfillRegistryBuilder();
  }

  @SuppressWarnings("unchecked")
  public <T extends Event> Optional<EventSource<T, ? extends Cursor>> findEventSource(Class<T> eventClass) {
    return Optional.ofNullable((EventSource<T, ?>) registry.get(eventClass));
  }

  public <T extends Event> EventSource<T, ? extends Cursor> getEventSource(Class<T> eventClass) {
    return this.findEventSource(eventClass).orElseThrow(() -> new IllegalArgumentException("No event source registered for event class: " + eventClass.getName()));
  }

}
