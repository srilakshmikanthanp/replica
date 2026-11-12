package com.srilakshmikanthanp.replica.spring.outbox.backfill;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface BackfillJobJpaRepository extends Repository<BackfillJobEntity, Long> {
  Optional<BackfillJobEntity> findById(long id);

  List<BackfillJobEntity> findAllByOrderByIdAsc();

  @Query("SELECT j FROM ReplicaBackfillJob j WHERE EXISTS (SELECT 1 FROM ReplicaBackfillJobStep s WHERE s.jobId = j.id AND s.completedAt IS NULL) ORDER BY j.id")
  List<BackfillJobEntity> findUnfinished();

  BackfillJobEntity save(BackfillJobEntity job);
}
