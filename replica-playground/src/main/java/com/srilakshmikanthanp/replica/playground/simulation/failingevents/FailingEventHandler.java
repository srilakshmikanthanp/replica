package com.srilakshmikanthanp.replica.playground.simulation.failingevents;

import com.srilakshmikanthanp.replica.event.Event;
import com.srilakshmikanthanp.replica.event.EventHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FailingEventHandler<E extends Event> implements EventHandler<E> {
  private final FailingEvents failingEvents;
  private final String eventType;
  private final EventHandler<E> handler;

  @Override
  public void handle(E event) {
    if (failingEvents.isFailing(eventType)) {
      throw new IllegalStateException("Simulated failure for " + eventType);
    }

    handler.handle(event);
  }
}
