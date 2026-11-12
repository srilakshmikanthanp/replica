package com.srilakshmikanthanp.replica.outbox;

/**
 * Sends a destination's outbox rows to it.
 */
public interface OutboxPublisher {
  /**
   * Sends the unpublished rows and returns once none are left.
   */
  void publish();
}
