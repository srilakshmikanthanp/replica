package com.srilakshmikanthanp.replica.playground.replication.outbox;

import com.srilakshmikanthanp.replica.spring.outbox.OutboxEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OutboxEntityRepository extends Repository<OutboxEntity, Long> {
  long countByDestinationIdAndPublishedAtIsNull(long destinationId);

  List<OutboxEntity> findTop100ByOrderByIdDesc();


  @Modifying
  @Query("DELETE FROM ReplicaOutbox o WHERE o.destinationId = :destinationId")
  void deleteByDestinationId(@Param("destinationId") long destinationId);
}
