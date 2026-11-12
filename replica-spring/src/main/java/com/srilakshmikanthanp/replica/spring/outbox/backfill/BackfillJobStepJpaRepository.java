package com.srilakshmikanthanp.replica.spring.outbox.backfill;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BackfillJobStepJpaRepository extends Repository<BackfillJobStepEntity, Long> {
  @Modifying
  @Query("UPDATE ReplicaBackfillJobStep s SET s.checkpoint = :checkpoint, s.completedAt = :completedAt WHERE s.id = :id AND s.completedAt IS NULL AND (s.checkpoint = :expected OR (s.checkpoint IS NULL AND :expected IS NULL))")
  int advance(@Param("id") long id, @Param("expected") String expected, @Param("checkpoint") String checkpoint, @Param("completedAt") Long completedAt);

	List<BackfillJobStepEntity> findAllByJobIdOrderByOrdinalAsc(long jobId);

	<S extends BackfillJobStepEntity> List<S> saveAll(Iterable<S> entities);
}
