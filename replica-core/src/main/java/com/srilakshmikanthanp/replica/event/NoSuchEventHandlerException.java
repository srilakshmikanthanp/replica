package com.srilakshmikanthanp.replica.event;

public class NoSuchEventHandlerException extends EventException {
	public NoSuchEventHandlerException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoSuchEventHandlerException(String message) {
		super(message);
	}

	public static NoSuchEventHandlerException ofEventClass(Class<?> eventClass) {
		return new NoSuchEventHandlerException("No event handler found for event class: %s, Please register the event handler in the EventHandlerRegistry.".formatted(eventClass.getName()));
	}
}
