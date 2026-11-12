package com.srilakshmikanthanp.replica.outbox.backfill;

import com.srilakshmikanthanp.replica.event.Event;

public class BackfillRegistryBuilder {
  private final BackfillRegistry registry = new BackfillRegistry();

  public <T extends Event> BackfillRegistryBuilder register(
    Class<T> eventClass,
    EventSource<T, ? extends Cursor> eventSource
  ) {
    registry.registry.put(eventClass, eventSource);
    return this;
  }

  public BackfillRegistry build() {
    return registry;
  }
}
