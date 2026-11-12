package com.srilakshmikanthanp.replica.playground.simulation.failingevents;

import com.srilakshmikanthanp.replica.event.Event;
import com.srilakshmikanthanp.replica.event.EventHandlerRegistry;
import com.srilakshmikanthanp.replica.event.EventHandlerRegistryBuilder;
import com.srilakshmikanthanp.replica.event.EventRegistry;
import com.srilakshmikanthanp.replica.playground.replication.Slave;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Slave
@Component
@RequiredArgsConstructor
public class FailingEventHandlers {
  private final EventRegistry eventRegistry;
  private final FailingEvents failingEvents;

  private <E extends Event> void register(EventHandlerRegistryBuilder builder, EventHandlerRegistry handlers, String eventType, Class<E> eventClass) {
    handlers.findEventHandlerByEventClass(eventClass).ifPresent(handler ->
      builder.registerEventHandler(eventClass, new FailingEventHandler<>(failingEvents, eventType, handler))
    );
  }

  public EventHandlerRegistry wrap(EventHandlerRegistry handlers) {
    EventHandlerRegistryBuilder builder = EventHandlerRegistry.builder();

    for (String eventType : eventRegistry.getEventTypes()) {
      register(builder, handlers, eventType, eventRegistry.getEventClassByEventType(eventType));
    }

    return builder.build();
  }
}
