package com.srilakshmikanthanp.replica.playground.replication.backfill;

import com.srilakshmikanthanp.replica.spring.outbox.backfill.BackfillJobEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface BackfillJobEntityRepository extends Repository<BackfillJobEntity, Long> {
  @Modifying
  @Query("DELETE FROM ReplicaBackfillJob j WHERE j.destinationId = :destinationId")
  void deleteByDestinationId(@Param("destinationId") long destinationId);
}
