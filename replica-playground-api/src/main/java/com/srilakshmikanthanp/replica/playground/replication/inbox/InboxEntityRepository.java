package com.srilakshmikanthanp.replica.playground.replication.inbox;

import com.srilakshmikanthanp.replica.spring.inbox.InboxEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InboxEntityRepository extends Repository<InboxEntity, Long> {
  List<InboxEntity> findTop100ByOrderByIdDesc();


  @Modifying
  @Query("DELETE FROM ReplicaInbox i WHERE i.sourceId = :sourceId")
  void deleteBySourceId(@Param("sourceId") long sourceId);
}
