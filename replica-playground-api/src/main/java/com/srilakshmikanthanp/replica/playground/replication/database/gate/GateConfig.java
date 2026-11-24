package com.srilakshmikanthanp.replica.playground.replication.database.gate;

import com.srilakshmikanthanp.replica.database.gate.ReplicationGate;
import com.srilakshmikanthanp.replica.playground.replication.Master;
import com.srilakshmikanthanp.replica.spring.database.gate.GateJpaRepository;
import com.srilakshmikanthanp.replica.spring.database.gate.SpringReplicationGate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GateConfig {
  @Master
  @Bean
  ReplicationGate replicationGate(GateJpaRepository jpa) {
    return new SpringReplicationGate(jpa);
  }
}
