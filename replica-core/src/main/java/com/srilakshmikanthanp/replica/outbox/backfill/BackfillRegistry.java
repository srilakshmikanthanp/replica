package com.srilakshmikanthanp.replica.outbox.backfill;

import com.srilakshmikanthanp.replica.event.Event;
import com.srilakshmikanthanp.replica.event.EventRegistry;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class BackfillRegistry {
  final Map<Class<? extends Event>, Map<Class<? extends Event>, CursorEventPredicate<Cursor, Event>>> affected = new HashMap<>();
  final Map<Class<? extends Event>, EventSource<? extends Event, ? extends Cursor>> registry = new HashMap<>();
  private final EventRegistry eventRegistry;

  @SuppressWarnings("unchecked")
  public <T extends Event> Optional<EventSource<T, ? extends Cursor>> findEventSource(Class<T> eventClass) {
    return Optional.ofNullable((EventSource<T, ?>) registry.get(eventClass));
  }

  public <T extends Event> EventSource<T, ? extends Cursor> getEventSource(Class<T> eventClass) {
    return this.findEventSource(eventClass).orElseThrow(() -> new IllegalArgumentException("No event source registered for event class: " + eventClass.getName()));
  }

  public List<Class<? extends Event>> findBackfillEventsAffectedBy(Class<? extends Event> eventClass) {
    return affected.entrySet().stream().filter(entry -> entry.getValue().containsKey(eventClass)).<Class<? extends Event>>map(Map.Entry::getKey).toList();
  }

  public Optional<CursorEventPredicate<Cursor, Event>> findPolicy(Class<? extends Event> backfilledClass, Class<? extends Event> eventClass) {
    return Optional.ofNullable(affected.getOrDefault(backfilledClass, Map.of()).get(eventClass));
  }

  public Class<? extends Event> getBackfillEvent(String eventType) {
    return eventRegistry.getEventClassByEventType(eventType);
  }

  public static BackfillRegistryBuilder builder(EventRegistry eventRegistry) {
    return new BackfillRegistryBuilder(new BackfillRegistry(eventRegistry), eventRegistry);
  }
}
