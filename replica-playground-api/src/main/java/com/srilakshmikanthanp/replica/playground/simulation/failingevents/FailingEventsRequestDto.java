package com.srilakshmikanthanp.replica.playground.simulation.failingevents;

import java.util.Set;

public record FailingEventsRequestDto(Set<String> failingEventTypes) {
}
