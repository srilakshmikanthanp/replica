package com.srilakshmikanthanp.replica.spring.database.gate;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface GateJpaRepository extends Repository<GateEntity, Integer> {
	<S extends GateEntity> S save(S entity);

	@Lock(LockModeType.PESSIMISTIC_READ)
	@Query("SELECT g FROM ReplicaGate g")
	Optional<GateEntity> lockShared();

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT g FROM ReplicaGate g")
	Optional<GateEntity> lockExclusive();
}
