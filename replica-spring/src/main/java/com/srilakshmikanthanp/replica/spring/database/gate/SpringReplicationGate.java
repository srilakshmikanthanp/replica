package com.srilakshmikanthanp.replica.spring.database.gate;

import com.srilakshmikanthanp.replica.database.gate.ReplicationGate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;

@Slf4j
public class SpringReplicationGate implements ReplicationGate {
  private static final String REPLICATION_GATE_ROW_MISSING = "Replication gate row is missing";

	private final GateJpaRepository gateJpaRepository;

	public SpringReplicationGate(GateJpaRepository gateJpaRepository) {
		this.gateJpaRepository = gateJpaRepository;

    try {
      gateJpaRepository.save(GateEntity.createDefault());
    } catch (DataIntegrityViolationException e) {
      log.info("Replication gate row already exists, skipping creation");
    }
	}

	@Override
	public void enterShared() {
		gateJpaRepository.lockShared().orElseThrow(() -> new IllegalStateException(REPLICATION_GATE_ROW_MISSING));
	}

	@Override
	public void enterExclusive() {
		gateJpaRepository.lockExclusive().orElseThrow(() -> new IllegalStateException(REPLICATION_GATE_ROW_MISSING));
	}
}
