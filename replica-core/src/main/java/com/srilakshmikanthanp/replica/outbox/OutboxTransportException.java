package com.srilakshmikanthanp.replica.outbox;

public class OutboxTransportException extends RuntimeException {
	public OutboxTransportException(String message, Throwable cause) {
		super(message, cause);
	}

	public OutboxTransportException(String message) {
		super(message);
	}
}
