package com.srilakshmikanthanp.replica.outbox;

import com.srilakshmikanthanp.replica.event.Event;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public record TransactionalOperationResult<R, E extends Event>(
	R result,
	Map<Long, E> eventsByDestination
) {
	public static <R, E extends Event> TransactionalOperationResult<R, E> broadcast(R result, E event, Collection<Long> destinationIds) {
		Map<Long, E> eventsByDestination = new LinkedHashMap<>();
		for (Long destinationId : destinationIds) {
			eventsByDestination.put(destinationId, event);
		}
		return new TransactionalOperationResult<>(result, eventsByDestination);
	}

	public TransactionalOperationResult {
		eventsByDestination = Collections.unmodifiableMap(new LinkedHashMap<>(eventsByDestination));
	}
}
