package com.srilakshmikanthanp.replica.outbox.backfill;

import com.srilakshmikanthanp.replica.event.Event;

import java.util.Map;

public sealed interface LiveEventFilter permits BackfillPolicy {
  <E extends Event> Map<Long, E> filter(Map<Long, E> eventsByDestination);
}
