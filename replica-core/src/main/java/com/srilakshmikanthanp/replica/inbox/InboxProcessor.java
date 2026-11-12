package com.srilakshmikanthanp.replica.inbox;

/**
 * Applies a source's received inbox messages to the local database.
 */
public interface InboxProcessor {
  /**
   * Applies the unprocessed messages and returns once none can be applied.
   */
  void process();
}
