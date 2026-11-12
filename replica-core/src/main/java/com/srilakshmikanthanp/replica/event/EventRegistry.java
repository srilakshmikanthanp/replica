package com.srilakshmikanthanp.replica.event;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class EventRegistry {
  final Map<String, Class<? extends Event>> eventTypeVsEventClazz;
  final Map<Class<? extends Event>, String> eventClazzVsEventType;

  EventRegistry(Map<String, Class<? extends Event>> eventTypeVsEventClazz, Map<Class<? extends Event>, String> eventClazzVsEventType) {
    this.eventTypeVsEventClazz = Map.copyOf(eventTypeVsEventClazz);
    this.eventClazzVsEventType = Map.copyOf(eventClazzVsEventType);
  }

  public static EventRegistryBuilder builder() {
    return new EventRegistryBuilder();
  }

  public Optional<Class<? extends Event>> findEventClassByEventType(String type) {
    return Optional.ofNullable(eventTypeVsEventClazz.get(type));
  }

  public Optional<String> findEventTypeByEventClass(Class<? extends Event> clazz) {
    return Optional.ofNullable(eventClazzVsEventType.get(clazz));
  }

  public Class<? extends Event> getEventClassByEventType(String type) {
    return findEventClassByEventType(type).orElseThrow(() -> NoSuchEventTypeException.ofUnknownEventType(type));
  }

  public String getEventTypeByEventClass(Class<? extends Event> clazz) {
    return findEventTypeByEventClass(clazz).orElseThrow(() -> NoSuchEventTypeException.ofClass(clazz));
  }

  public Set<String> getEventTypes() {
    return eventTypeVsEventClazz.keySet();
  }

}
