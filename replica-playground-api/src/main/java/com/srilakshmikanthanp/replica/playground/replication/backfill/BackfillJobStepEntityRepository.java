package com.srilakshmikanthanp.replica.playground.replication.backfill;

import com.srilakshmikanthanp.replica.spring.outbox.backfill.BackfillJobStepEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface BackfillJobStepEntityRepository extends Repository<BackfillJobStepEntity, Long> {
  @Modifying
  @Query("DELETE FROM ReplicaBackfillJobStep s WHERE s.jobId IN (SELECT j.id FROM ReplicaBackfillJob j WHERE j.destinationId = :destinationId)")
  void deleteByDestinationId(@Param("destinationId") long destinationId);
}
