package com.srilakshmikanthanp.replica.event;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EventRegistry {
	private final Map<String, Class<? extends Event>> eventTypeVsEventClazz;
	private final Map<Class<? extends Event>, String> eventClazzVsEventType;

	private EventRegistry(Map<String, Class<? extends Event>> eventTypeVsEventClazz, Map<Class<? extends Event>, String> eventClazzVsEventType) {
		this.eventTypeVsEventClazz = Map.copyOf(eventTypeVsEventClazz);
		this.eventClazzVsEventType = Map.copyOf(eventClazzVsEventType);
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
	
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private final Map<String, Class<? extends Event>> eventTypeVsEventClazz = new HashMap<>();
		private final Map<Class<? extends Event>, String> eventClazzVsEventType = new HashMap<>();

		public Builder registerEvent(String type, Class<? extends Event> clazz) {
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

		public Builder add(EventRegistry eventRegistry) {
			eventRegistry.eventTypeVsEventClazz.forEach(this::registerEvent);
			return this;
		}

		public EventRegistry build() {
			return new EventRegistry(eventTypeVsEventClazz, eventClazzVsEventType);
		}
	}
}
