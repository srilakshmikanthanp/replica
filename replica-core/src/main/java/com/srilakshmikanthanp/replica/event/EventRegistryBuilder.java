package com.srilakshmikanthanp.replica.event;

import java.util.HashMap;
import java.util.Map;

public class EventRegistryBuilder {
  private final Map<String, Class<? extends Event>> eventTypeVsEventClazz = new HashMap<>();
  private final Map<Class<? extends Event>, String> eventClazzVsEventType = new HashMap<>();

  public EventRegistryBuilder registerEvent(String type, Class<? extends Event> clazz) {
    Class<? extends Event> registeredClazz = eventTypeVsEventClazz.get(type);
    String registeredType = eventClazzVsEventType.get(clazz);

    if (registeredClazz != null && registeredClazz != clazz) {
      throw new IllegalArgumentException("Event type %s is already registered for %s".formatted(type, registeredClazz.getName()));
    }

    if (registeredType != null && !registeredType.equals(type)) {
      throw new IllegalArgumentException("Event class %s is already registered as %s".formatted(clazz.getName(), registeredType));
    }

    eventTypeVsEventClazz.put(type, clazz);
    eventClazzVsEventType.put(clazz, type);
    return this;
  }

  public EventRegistryBuilder add(EventRegistry eventRegistry) {
    eventRegistry.eventTypeVsEventClazz.forEach(this::registerEvent);
    return this;
  }

  public EventRegistry build() {
    return new EventRegistry(eventTypeVsEventClazz, eventClazzVsEventType);
  }
}
