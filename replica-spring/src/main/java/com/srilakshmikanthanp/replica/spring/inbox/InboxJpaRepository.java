package com.srilakshmikanthanp.replica.spring.inbox;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface InboxJpaRepository extends Repository<InboxEntity, Long> {
  @Query("SELECT i FROM ReplicaInbox i WHERE i.processedAt IS NULL AND i.sourceId = :sourceId ORDER BY i.messageId ASC")
  List<InboxEntity> findUnprocessed(@Param("sourceId") long sourceId, Pageable pageable);

  @Modifying
  @Query("UPDATE ReplicaInbox i SET i.processedAt = :processedAt WHERE i.id = :id AND i.processedAt IS NULL")
  int markAsProcessed(@Param("id") long id, @Param("processedAt") long processedAt);

  @Query("SELECT i.messageId FROM ReplicaInbox i WHERE i.sourceId = :sourceId AND i.messageId IN :messageIds")
  List<Long> findMessageIds(@Param("sourceId") long sourceId, @Param("messageIds") Collection<Long> messageIds);

  <S extends InboxEntity> void saveAll(Iterable<S> entities);
}
