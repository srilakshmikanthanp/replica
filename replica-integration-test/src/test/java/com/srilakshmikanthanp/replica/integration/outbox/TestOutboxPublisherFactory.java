package com.srilakshmikanthanp.replica.integration.outbox;

import com.srilakshmikanthanp.replica.database.transaction.TransactionExecutor;
import com.srilakshmikanthanp.replica.outbox.OutboxPublisher;
import com.srilakshmikanthanp.replica.outbox.OutboxRepository;
import com.srilakshmikanthanp.replica.outbox.OutboxTransporter;
import com.srilakshmikanthanp.replica.outbox.PollingOutboxPublisher;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestOutboxPublisherFactory {
  private static final int BATCH_SIZE = 2;

  private final TransactionExecutor transactionExecutor;
  private final OutboxRepository outboxRepository;

  public OutboxPublisher polling(long destinationId, OutboxTransporter outboxTransporter) {
    return new PollingOutboxPublisher(transactionExecutor, outboxRepository, outboxTransporter, BATCH_SIZE, destinationId);
  }
}
