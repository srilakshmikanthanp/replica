package com.srilakshmikanthanp.replica.playground.replication.inbox;

import com.srilakshmikanthanp.replica.crypto.PayloadCryptography;
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
  InboxRepository inboxRepository(
    InboxJpaRepository jpa,
    StationJpaRepository stationJpa,
    TransactionTemplate transactionTemplate,
    PayloadCryptography payloadCryptography
  ) {
    return new InboxSpringRepository(jpa, stationJpa, transactionTemplate, payloadCryptography);
  }
}
