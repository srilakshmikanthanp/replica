package com.srilakshmikanthanp.replica.integration.inbox;

import com.srilakshmikanthanp.replica.event.Event;
import com.srilakshmikanthanp.replica.event.EventHandler;

public interface TestEventHandler<E extends Event> extends EventHandler<E> {
  Class<E> getEventClass();
}
