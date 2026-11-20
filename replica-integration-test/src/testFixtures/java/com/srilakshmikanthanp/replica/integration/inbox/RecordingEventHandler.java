package com.srilakshmikanthanp.replica.integration.inbox;

import com.srilakshmikanthanp.replica.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class RecordingEventHandler<E extends Event> implements TestEventHandler<E> {
  private final Class<E> eventClass;
  private final List<E> handledEvents = new ArrayList<>();

  @Override
  public void handle(E event) {
    handledEvents.add(event);
  }
}
