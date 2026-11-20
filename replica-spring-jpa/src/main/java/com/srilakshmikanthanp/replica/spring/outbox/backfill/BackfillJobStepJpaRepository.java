package com.srilakshmikanthanp.replica.spring.outbox.backfill;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BackfillJobStepJpaRepository extends Repository<BackfillJobStepEntity, Long> {
  @Lock(LockModeType.PESSIMISTIC_READ)
  @Query("SELECT s AS step, j.destinationId AS destinationId FROM ReplicaBackfillJobStep s JOIN ReplicaBackfillJob j ON j.id = s.jobId WHERE s.completedAt IS NULL AND j.destinationId IN :destinationIds ORDER BY s.id")
  List<UnfinishedStep> findUnfinishedForShare(@Param("destinationIds") Collection<Long> destinationIds);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT s FROM ReplicaBackfillJobStep s WHERE s.id = :id")
  Optional<BackfillJobStepEntity> findByIdForUpdate(@Param("id") long id);

  @Modifying
  @Query("UPDATE ReplicaBackfillJobStep s SET s.checkpoint = :checkpoint, s.completedAt = :completedAt WHERE s.id = :id AND s.completedAt IS NULL AND (s.checkpoint = :expected OR (s.checkpoint IS NULL AND :expected IS NULL))")
  int advance(@Param("id") long id, @Param("expected") String expected, @Param("checkpoint") String checkpoint, @Param("completedAt") Long completedAt);

  List<BackfillJobStepEntity> findAllByJobIdOrderByOrdinalAsc(long jobId);

  <S extends BackfillJobStepEntity> void saveAll(Iterable<S> entities);
}
