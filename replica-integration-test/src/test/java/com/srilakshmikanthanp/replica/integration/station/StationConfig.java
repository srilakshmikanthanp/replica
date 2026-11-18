package com.srilakshmikanthanp.replica.integration.station;

import com.srilakshmikanthanp.replica.spring.station.StationJpaRepository;
import com.srilakshmikanthanp.replica.spring.station.StationSpringRepository;
import com.srilakshmikanthanp.replica.station.StationRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class StationConfig {
  @Bean
  StationRepository stationRepository(StationJpaRepository jpa, TransactionTemplate transactionTemplate) {
    return new StationSpringRepository(jpa, transactionTemplate);
  }

  @Bean
  TestStationService testStationService(StationRepository stationRepository) {
    return new TestStationService(stationRepository);
  }
}
