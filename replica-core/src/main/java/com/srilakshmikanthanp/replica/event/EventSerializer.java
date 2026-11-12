package com.srilakshmikanthanp.replica.event;

public interface EventSerializer {
	String serialize(Event event);
}
