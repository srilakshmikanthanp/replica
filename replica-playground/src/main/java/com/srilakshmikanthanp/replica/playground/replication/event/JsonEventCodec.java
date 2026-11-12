package com.srilakshmikanthanp.replica.playground.replication.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srilakshmikanthanp.replica.event.Event;
import com.srilakshmikanthanp.replica.event.EventDeserializer;
import com.srilakshmikanthanp.replica.event.EventException;
import com.srilakshmikanthanp.replica.event.EventSerializer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JsonEventCodec implements EventSerializer, EventDeserializer {
	private final ObjectMapper objectMapper;

	@Override
	public String serialize(Event event) {
		try {
			return objectMapper.writeValueAsString(event);
		} catch (JsonProcessingException e) {
			throw new EventException("Cannot serialize " + event.getClass().getName(), e);
		}
	}

	@Override
	public <T extends Event> T deserialize(String payload, Class<T> clazz) {
		try {
			return objectMapper.readValue(payload, clazz);
		} catch (JsonProcessingException e) {
			throw new EventException("Cannot deserialize " + clazz.getName(), e);
		}
	}
}
