package com.srilakshmikanthanp.replica.integration.outbox.backfill;

import com.srilakshmikanthanp.replica.database.gate.ReplicationGate;
import com.srilakshmikanthanp.replica.database.transaction.TransactionExecutor;
import com.srilakshmikanthanp.replica.event.EventRegistry;
import com.srilakshmikanthanp.replica.event.EventSerializer;
import com.srilakshmikanthanp.replica.outbox.OutboxRepository;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobRunner;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillRegistry;
import com.srilakshmikanthanp.replica.outbox.backfill.CursorCodec;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobRepository;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobService;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobStepRepository;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillPolicy;
import com.srilakshmikanthanp.replica.spring.outbox.backfill.BackfillJobJpaRepository;
import com.srilakshmikanthanp.replica.spring.outbox.backfill.BackfillJobSpringRepository;
import com.srilakshmikanthanp.replica.spring.outbox.backfill.BackfillJobStepJpaRepository;
import com.srilakshmikanthanp.replica.spring.outbox.backfill.BackfillJobStepSpringRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class BackfillConfig {
  @Bean
  BackfillJobRepository backfillJobRepository(BackfillJobJpaRepository jpa, TransactionTemplate transactionTemplate) {
    return new BackfillJobSpringRepository(jpa, transactionTemplate);
  }

  @Bean
  BackfillJobStepRepository backfillJobStepRepository(BackfillJobStepJpaRepository jpa, TransactionTemplate transactionTemplate) {
    return new BackfillJobStepSpringRepository(jpa, transactionTemplate);
  }

  @Bean
  BackfillJobService backfillJobService(
    TransactionExecutor transactionExecutor,
    ReplicationGate replicationGate,
    BackfillJobRepository backfillJobRepository,
    BackfillJobStepRepository backfillJobStepRepository,
    EventRegistry eventRegistry,
    BackfillPolicy backfillPolicy
  ) {
    return new BackfillJobService(transactionExecutor, replicationGate, backfillJobRepository, backfillJobStepRepository, eventRegistry, backfillPolicy);
  }

  @Bean
  CursorCodec cursorCodec() {
    return new JsonCursorCodec();
  }

  @Bean
  BackfillJobRunner backfillJobRunner(
    BackfillJobRepository backfillJobRepository,
    BackfillJobStepRepository backfillJobStepRepository,
    EventRegistry eventRegistry,
    BackfillRegistry backfillRegistry,
    TransactionExecutor transactionExecutor,
    EventSerializer eventSerializer,
    OutboxRepository outboxRepository,
    CursorCodec cursorCodec
  ) {
    return new BackfillJobRunner(
      backfillJobRepository,
      backfillJobStepRepository,
      eventRegistry,
      backfillRegistry,
      transactionExecutor,
      eventSerializer,
      outboxRepository,
      cursorCodec,
      2
    );
  }
}
