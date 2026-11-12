package com.srilakshmikanthanp.replica.event;

public record EventContext(EventRegistry eventRegistry, EventHandlerRegistry eventHandlerRegistry) {
	public static EventContextBuilder builder() {
		return new EventContextBuilder();
	}
}
