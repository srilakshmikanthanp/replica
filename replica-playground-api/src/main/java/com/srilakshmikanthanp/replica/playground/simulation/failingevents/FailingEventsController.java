package com.srilakshmikanthanp.replica.playground.simulation.failingevents;

import com.srilakshmikanthanp.replica.event.EventRegistry;
import com.srilakshmikanthanp.replica.playground.replication.Slave;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slave
@RestController
@RequestMapping("/simulations/failing-events")
@RequiredArgsConstructor
public class FailingEventsController {
  private final FailingEvents failingEvents;
  private final EventRegistry eventRegistry;

  @GetMapping
  FailingEventsDto get() {
    return new FailingEventsDto(failingEvents.getFailingEventTypes());
  }

  @PutMapping
  FailingEventsDto update(@RequestBody FailingEventsRequestDto request) {
    if (request == null || request.failingEventTypes() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The failing event types are required");
    }

    if (!eventRegistry.getEventTypes().containsAll(request.failingEventTypes())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown event type in " + request.failingEventTypes());
    }

    failingEvents.setFailingEventTypes(request.failingEventTypes());
    return get();
  }
}
