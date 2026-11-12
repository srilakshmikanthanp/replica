package com.srilakshmikanthanp.replica.playground.simulation.failingevents;

import java.util.List;
import java.util.Set;

public record FailingEventsDto(List<String> eventTypes, Set<String> failingEventTypes) {
}
