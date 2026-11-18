package com.srilakshmikanthanp.replica.integration.outbox.backfill;

import com.srilakshmikanthanp.replica.database.gate.ReplicationGate;
import com.srilakshmikanthanp.replica.database.transaction.TransactionExecutor;
import com.srilakshmikanthanp.replica.event.EventRegistry;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobRepository;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobService;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobStepRepository;
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
    EventRegistry eventRegistry
  ) {
    return new BackfillJobService(transactionExecutor, replicationGate, backfillJobRepository, backfillJobStepRepository, eventRegistry);
  }

  @Bean
  TestBackfillService testBackfillService(BackfillJobService backfillJobService, BackfillJobStepRepository backfillJobStepRepository) {
    return new TestBackfillService(backfillJobService, backfillJobStepRepository);
  }
}
