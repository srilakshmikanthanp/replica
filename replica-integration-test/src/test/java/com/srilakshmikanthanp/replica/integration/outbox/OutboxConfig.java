package com.srilakshmikanthanp.replica.integration.outbox;

import com.srilakshmikanthanp.replica.crypto.PayloadCryptography;
import com.srilakshmikanthanp.replica.database.gate.ReplicationGate;
import com.srilakshmikanthanp.replica.database.transaction.TransactionExecutor;
import com.srilakshmikanthanp.replica.event.EventRegistry;
import com.srilakshmikanthanp.replica.event.EventSerializer;
import com.srilakshmikanthanp.replica.outbox.OutboxRepository;
import com.srilakshmikanthanp.replica.outbox.OutboxTransactionExecutor;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillEventFilter;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobStepRepository;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillRegistry;
import com.srilakshmikanthanp.replica.spring.outbox.OutboxJpaRepository;
import com.srilakshmikanthanp.replica.spring.outbox.OutboxSpringRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class OutboxConfig {
  @Bean
  OutboxRepository outboxRepository(OutboxJpaRepository jpa, TransactionTemplate transactionTemplate) {
    return new OutboxSpringRepository(jpa, transactionTemplate, PayloadCryptography.none());
  }

  @Bean
  BackfillEventFilter backfillEventFilter(BackfillJobStepRepository steps, EventRegistry eventRegistry) {
    return new BackfillEventFilter(steps, BackfillRegistry.builder(eventRegistry).build(), new NoCursorCodec());
  }

  @Bean
  OutboxTransactionExecutor outboxTransactionExecutor(
    OutboxRepository outboxRepository,
    BackfillEventFilter backfillEventFilter,
    TransactionExecutor transactionExecutor,
    ReplicationGate replicationGate,
    EventRegistry eventRegistry,
    EventSerializer eventSerializer
  ) {
    return new OutboxTransactionExecutor(outboxRepository, backfillEventFilter, transactionExecutor, replicationGate, eventRegistry, eventSerializer);
  }

  @Bean
  TestOutboxService testOutboxService(OutboxRepository outboxRepository) {
    return new TestOutboxService(outboxRepository);
  }

  @Bean
  TestOutboxPublisherFactory testOutboxPublisherFactory(TransactionExecutor transactionExecutor, OutboxRepository outboxRepository) {
    return new TestOutboxPublisherFactory(transactionExecutor, outboxRepository);
  }
}
