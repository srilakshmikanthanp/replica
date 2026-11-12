package com.srilakshmikanthanp.replica.spring.outbox.backfill;

import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface BackfillJobJpaRepository extends Repository<BackfillJobEntity, Long> {
	Optional<BackfillJobEntity> findById(long id);

	List<BackfillJobEntity> findAllByOrderByIdAsc();

	BackfillJobEntity save(BackfillJobEntity job);
}
