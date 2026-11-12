package com.srilakshmikanthanp.replica.event;

import java.util.HashMap;
import java.util.Map;

public class EventHandlerRegistryBuilder {
  private final Map<Class<? extends Event>, EventHandler<? extends Event>> eventClazzVsEventHandler = new HashMap<>();

  private void put(Class<? extends Event> clazz, EventHandler<? extends Event> handler) {
    EventHandler<? extends Event> registeredHandler = eventClazzVsEventHandler.get(clazz);

    if (registeredHandler != null && registeredHandler != handler) {
      throw new IllegalArgumentException("An event handler is already registered for %s".formatted(clazz.getName()));
    }

    eventClazzVsEventHandler.put(clazz, handler);
  }

  public <T extends Event> EventHandlerRegistryBuilder registerEventHandler(Class<T> clazz, EventHandler<T> handler) {
    put(clazz, handler);
    return this;
  }

  public EventHandlerRegistryBuilder add(EventHandlerRegistry eventHandlerRegistry) {
    eventHandlerRegistry.eventClazzVsEventHandler.forEach(this::put);
    return this;
  }

  public EventHandlerRegistry build() {
    return new EventHandlerRegistry(eventClazzVsEventHandler);
  }
}
