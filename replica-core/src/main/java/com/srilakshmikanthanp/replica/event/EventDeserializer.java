package com.srilakshmikanthanp.replica.event;

public interface EventDeserializer {
	<T extends Event> T deserialize(String payload, Class<T> clazz);
}
