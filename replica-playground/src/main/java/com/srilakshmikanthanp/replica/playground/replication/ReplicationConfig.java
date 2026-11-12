package com.srilakshmikanthanp.replica.playground.replication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.srilakshmikanthanp.replica.database.transaction.TransactionExecutor;
import com.srilakshmikanthanp.replica.event.EventContext;
import com.srilakshmikanthanp.replica.event.EventHandlerRegistry;
import com.srilakshmikanthanp.replica.event.EventModule;
import com.srilakshmikanthanp.replica.event.EventRegistry;
import com.srilakshmikanthanp.replica.event.EventSerializer;
import com.srilakshmikanthanp.replica.crypto.PayloadCryptography;
import com.srilakshmikanthanp.replica.database.gate.ReplicationGate;
import com.srilakshmikanthanp.replica.inbox.InboxRepository;
import com.srilakshmikanthanp.replica.outbox.OutboxRepository;
import com.srilakshmikanthanp.replica.outbox.OutboxTransactionExecutor;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobRepository;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobStepRepository;
import com.srilakshmikanthanp.replica.playground.replication.backfill.JsonCursorCodec;
import com.srilakshmikanthanp.replica.playground.replication.event.JsonEventCodec;
import com.srilakshmikanthanp.replica.spring.database.transaction.SpringTransactionExecutor;
import com.srilakshmikanthanp.replica.spring.database.gate.GateJpaRepository;
import com.srilakshmikanthanp.replica.spring.database.gate.SpringReplicationGate;
import com.srilakshmikanthanp.replica.spring.inbox.InboxJpaRepository;
import com.srilakshmikanthanp.replica.spring.inbox.InboxSpringRepository;
import com.srilakshmikanthanp.replica.spring.outbox.OutboxJpaRepository;
import com.srilakshmikanthanp.replica.spring.outbox.OutboxSpringRepository;
import com.srilakshmikanthanp.replica.spring.outbox.backfill.BackfillJobJpaRepository;
import com.srilakshmikanthanp.replica.spring.outbox.backfill.BackfillJobSpringRepository;
import com.srilakshmikanthanp.replica.spring.outbox.backfill.BackfillJobStepJpaRepository;
import com.srilakshmikanthanp.replica.spring.outbox.backfill.BackfillJobStepSpringRepository;
import com.srilakshmikanthanp.replica.spring.station.StationJpaRepository;
import com.srilakshmikanthanp.replica.spring.station.StationSpringRepository;
import com.srilakshmikanthanp.replica.station.StationRepository;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Configuration
@EnableConfigurationProperties(ReplicationProperties.class)
public class ReplicationConfig {
  @Bean
  TransactionExecutor transactionExecutor(PlatformTransactionManager transactionManager) {
    return new SpringTransactionExecutor(transactionManager);
  }

  @Bean
  PayloadCryptography payloadCryptography() {
    return PayloadCryptography.none();
  }

  @Bean
  JsonEventCodec jsonEventCodec(ObjectMapper objectMapper) {
    return new JsonEventCodec(objectMapper);
  }

  @Bean
  StationRepository stationRepository(StationJpaRepository jpa, TransactionTemplate transactionTemplate) {
    return new StationSpringRepository(jpa, transactionTemplate);
  }

  @Bean
  OutboxRepository outboxRepository(OutboxJpaRepository jpa, TransactionTemplate transactionTemplate, PayloadCryptography payloadCryptography) {
    return new OutboxSpringRepository(jpa, transactionTemplate, payloadCryptography);
  }

  @Bean
  InboxRepository inboxRepository(InboxJpaRepository jpa, TransactionTemplate transactionTemplate, PayloadCryptography payloadCryptography) {
    return new InboxSpringRepository(jpa, transactionTemplate, payloadCryptography);
  }

  @Bean
  JsonCursorCodec jsonCursorCodec(ObjectMapper objectMapper) {
    return new JsonCursorCodec(objectMapper);
  }

  @Bean
  BackfillJobRepository backfillJobRepository(BackfillJobJpaRepository jpa, TransactionTemplate transactionTemplate) {
    return new BackfillJobSpringRepository(jpa, transactionTemplate);
  }

  @Bean
  BackfillJobStepRepository backfillJobStepRepository(BackfillJobStepJpaRepository jpa, TransactionTemplate transactionTemplate) {
    return new BackfillJobStepSpringRepository(jpa, transactionTemplate);
  }

  @Bean
  EventContext eventContext(List<EventModule> modules) {
    return EventContext.builder().addModules(modules).build();
  }

  @Bean
  EventRegistry eventRegistry(EventContext eventContext) {
    return eventContext.eventRegistry();
  }

  @Bean
  EventHandlerRegistry eventHandlerRegistry(EventContext eventContext) {
    return eventContext.eventHandlerRegistry();
  }

  @Master
  @Bean
  ReplicationGate replicationGate(GateJpaRepository jpa) {
    return new SpringReplicationGate(jpa);
  }

  @Master
  @Bean
  OutboxTransactionExecutor outboxTransactionExecutor(
    OutboxRepository outboxRepository,
    TransactionExecutor transactionExecutor,
    ReplicationGate replicationGate,
    EventRegistry eventRegistry,
    EventSerializer eventSerializer
  ) {
    return new OutboxTransactionExecutor(outboxRepository, transactionExecutor, replicationGate, eventRegistry, eventSerializer);
  }
}
