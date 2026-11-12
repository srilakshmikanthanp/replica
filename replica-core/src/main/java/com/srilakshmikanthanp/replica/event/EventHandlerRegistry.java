package com.srilakshmikanthanp.replica.event;

import java.util.Map;
import java.util.Optional;

public class EventHandlerRegistry {
  final Map<Class<? extends Event>, EventHandler<? extends Event>> eventClazzVsEventHandler;

  EventHandlerRegistry(Map<Class<? extends Event>, EventHandler<? extends Event>> eventClazzVsEventHandler) {
    this.eventClazzVsEventHandler = Map.copyOf(eventClazzVsEventHandler);
  }

  public static EventHandlerRegistryBuilder builder() {
    return new EventHandlerRegistryBuilder();
  }

  @SuppressWarnings("unchecked")
  public <T extends Event> Optional<EventHandler<T>> findEventHandlerByEventClass(Class<T> clazz) {
    return Optional.ofNullable((EventHandler<T>) eventClazzVsEventHandler.get(clazz));
  }

  public <T extends Event> EventHandler<T> getEventHandlerByEventClass(Class<T> clazz) {
    return findEventHandlerByEventClass(clazz).orElseThrow(() -> NoSuchEventHandlerException.ofEventClass(clazz));
  }

}
