package com.srilakshmikanthanp.replica.outbox.backfill;

import lombok.Getter;

@Getter
public class DestinationNotReadyException extends RuntimeException {
  private final long destinationId;

  public DestinationNotReadyException(long destinationId) {
    super("Destination %d cannot receive events yet: it is still being backfilled".formatted(destinationId));
    this.destinationId = destinationId;
  }
}
