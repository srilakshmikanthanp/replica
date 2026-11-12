package com.srilakshmikanthanp.replica.spring.outbox;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OutboxJpaRepository extends Repository<OutboxEntity, Long> {
	@Query("SELECT o FROM ReplicaOutbox o WHERE o.publishedAt IS NULL AND o.destinationId = :destinationId ORDER BY o.id ASC")
	List<OutboxEntity> findUnpublished(@Param("destinationId") long destinationId, Pageable pageable);

	<S extends OutboxEntity> List<S> saveAll(Iterable<S> entities);

	@Modifying
	@Query("UPDATE ReplicaOutbox o SET o.publishedAt = :publishedAt WHERE o.id = :id AND o.publishedAt IS NULL")
	int markAsPublished(@Param("id") long id, @Param("publishedAt") long publishedAt);
}
