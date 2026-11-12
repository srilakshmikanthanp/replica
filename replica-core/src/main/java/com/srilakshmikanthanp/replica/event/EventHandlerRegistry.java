package com.srilakshmikanthanp.replica.event;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EventHandlerRegistry {
	private final Map<Class<? extends Event>, EventHandler<? extends Event>> eventClazzVsEventHandler;

	private EventHandlerRegistry(Map<Class<? extends Event>, EventHandler<? extends Event>> eventClazzVsEventHandler) {
		this.eventClazzVsEventHandler = Map.copyOf(eventClazzVsEventHandler);
	}

	@SuppressWarnings("unchecked")
	public <T extends Event> Optional<EventHandler<T>> findEventHandlerByEventClass(Class<T> clazz) {
		return Optional.ofNullable((EventHandler<T>) eventClazzVsEventHandler.get(clazz));
	}

	public <T extends Event> EventHandler<T> getEventHandlerByEventClass(Class<T> clazz) {
		return findEventHandlerByEventClass(clazz).orElseThrow(() -> NoSuchEventHandlerException.ofEventClass(clazz));
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private final Map<Class<? extends Event>, EventHandler<? extends Event>> eventClazzVsEventHandler = new HashMap<>();

		private void put(Class<? extends Event> clazz, EventHandler<? extends Event> handler) {
			EventHandler<? extends Event> registeredHandler = eventClazzVsEventHandler.get(clazz);

			if (registeredHandler != null && registeredHandler != handler) {
				throw new IllegalArgumentException("An event handler is already registered for %s".formatted(clazz.getName()));
			}

			eventClazzVsEventHandler.put(clazz, handler);
		}

		public <T extends Event> Builder registerEventHandler(Class<T> clazz, EventHandler<T> handler) {
			put(clazz, handler);
			return this;
		}

		public Builder add(EventHandlerRegistry eventHandlerRegistry) {
			eventHandlerRegistry.eventClazzVsEventHandler.forEach(this::put);
			return this;
		}

		public EventHandlerRegistry build() {
			return new EventHandlerRegistry(eventClazzVsEventHandler);
		}
	}
}
