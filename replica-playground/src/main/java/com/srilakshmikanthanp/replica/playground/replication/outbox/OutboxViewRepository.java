package com.srilakshmikanthanp.replica.playground.replication.outbox;

import com.srilakshmikanthanp.replica.spring.outbox.OutboxEntity;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface OutboxViewRepository extends Repository<OutboxEntity, Long> {
	long countByDestinationIdAndPublishedAtIsNull(long destinationId);

	List<OutboxEntity> findTop100ByOrderByIdDesc();
}
