package com.srilakshmikanthanp.replica.outbox.backfill;

import lombok.Getter;

import java.util.Set;

@Getter
public class BackfillAlreadyRunningException extends RuntimeException {
  private final long destinationId;
  private final Set<String> eventTypes;

  public BackfillAlreadyRunningException(long destinationId, Set<String> eventTypes) {
    super("Destination %d is already being backfilled with %s".formatted(destinationId, eventTypes));
    this.destinationId = destinationId;
    this.eventTypes = Set.copyOf(eventTypes);
  }
}
