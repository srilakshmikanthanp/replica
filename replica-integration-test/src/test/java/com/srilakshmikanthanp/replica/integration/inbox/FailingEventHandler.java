package com.srilakshmikanthanp.replica.integration.inbox;

import com.srilakshmikanthanp.replica.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FailingEventHandler<E extends Event> implements TestEventHandler<E> {
  private final Class<E> eventClass;

  @Override
  public void handle(E event) {
    throw new IllegalStateException("handle failed");
  }
}
