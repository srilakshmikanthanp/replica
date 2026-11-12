package com.srilakshmikanthanp.replica.event;

public class NoSuchEventTypeException extends EventException {
	public NoSuchEventTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoSuchEventTypeException(String message) {
		super(message);
	}

	public static NoSuchEventTypeException ofUnknownEventType(String eventType) {
		return new NoSuchEventTypeException("Unknown event type: %s, Please register the event type in the EventRegistry.".formatted(eventType));
	}

	public static NoSuchEventTypeException ofClass(Class<?> eventClass) {
		return new NoSuchEventTypeException("Unknown event class: %s, Please register the event class in the EventRegistry.".formatted(eventClass.getName()));
	}
}
