package com.srilakshmikanthanp.replica.playground.replication.event;

import com.srilakshmikanthanp.replica.event.EventRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventTypeController {
  private final EventRegistry eventRegistry;

  @GetMapping("/replication/events")
  List<EventTypeDto> list() {
    List<EventTypeDto> eventTypes = new ArrayList<>();

    for (String eventType : eventRegistry.getEventTypes().stream().sorted().toList()) {
      eventTypes.add(new EventTypeDto(eventType));
    }

    return eventTypes;
  }
}
