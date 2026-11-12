package com.srilakshmikanthanp.replica.playground.replication.inbox;

import com.srilakshmikanthanp.replica.spring.inbox.InboxEntity;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface InboxViewRepository extends Repository<InboxEntity, Long> {
	List<InboxEntity> findTop100ByOrderByIdDesc();
}
