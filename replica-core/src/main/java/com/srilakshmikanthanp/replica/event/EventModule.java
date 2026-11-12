package com.srilakshmikanthanp.replica.event;

public interface EventModule {
	EventHandlerRegistry eventHandlerRegistry();
	EventRegistry eventRegistry();
}
