package com.srilakshmikanthanp.replica.event;

public class EventException extends RuntimeException {
	public EventException(String message, Throwable cause) {
		super(message, cause);
	}

	public EventException(String message) {
		super(message);
	}
}
