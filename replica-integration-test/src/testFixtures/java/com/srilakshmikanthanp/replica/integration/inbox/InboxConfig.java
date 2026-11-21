package com.srilakshmikanthanp.replica.integration.inbox;

import com.srilakshmikanthanp.replica.crypto.PayloadCryptography;
import com.srilakshmikanthanp.replica.database.transaction.TransactionExecutor;
import com.srilakshmikanthanp.replica.event.EventDeserializer;
import com.srilakshmikanthanp.replica.event.EventRegistry;
import com.srilakshmikanthanp.replica.event.EventSerializer;
import com.srilakshmikanthanp.replica.inbox.InboxRepository;
import com.srilakshmikanthanp.replica.spring.inbox.InboxJpaRepository;
import com.srilakshmikanthanp.replica.spring.inbox.InboxSpringRepository;
import com.srilakshmikanthanp.replica.spring.station.StationJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class InboxConfig {
  @Bean
  InboxRepository inboxRepository(InboxJpaRepository jpa, StationJpaRepository stationJpa, TransactionTemplate transactionTemplate) {
    return new InboxSpringRepository(jpa, stationJpa, transactionTemplate, PayloadCryptography.none());
  }

  @Bean
  TestInboxService testInboxService(InboxRepository inboxRepository, EventRegistry eventRegistry, EventSerializer eventSerializer) {
    return new TestInboxService(inboxRepository, eventRegistry, eventSerializer);
  }

  @Bean
  TestInboxProcessorFactory testInboxProcessorFactory(
    TransactionExecutor transactionExecutor,
    InboxRepository inboxRepository,
    EventRegistry eventRegistry,
    EventDeserializer eventDeserializer
  ) {
    return new TestInboxProcessorFactory(transactionExecutor, inboxRepository, eventRegistry, eventDeserializer);
  }
}
