package com.srilakshmikanthanp.replica.event;

@FunctionalInterface
public interface EventHandler<E extends Event> {
	void handle(E event);
}
