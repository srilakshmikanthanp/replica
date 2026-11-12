package com.srilakshmikanthanp.replica.outbox.backfill;

import com.srilakshmikanthanp.replica.event.Event;

import java.util.List;

public record EventBatch<T extends Event, C extends Cursor>(
	List<T> events,
	C cursor,
	boolean hasMore
) {
}
