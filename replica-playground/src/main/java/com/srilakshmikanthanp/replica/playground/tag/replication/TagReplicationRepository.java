package com.srilakshmikanthanp.replica.playground.tag.replication;

import com.srilakshmikanthanp.replica.playground.replication.Master;
import com.srilakshmikanthanp.replica.playground.replication.outbox.ReplicationWriter;
import com.srilakshmikanthanp.replica.playground.tag.Tag;
import com.srilakshmikanthanp.replica.playground.tag.TagRepository;
import com.srilakshmikanthanp.replica.playground.tag.replication.event.TagDeleted;
import com.srilakshmikanthanp.replica.playground.tag.replication.event.TagUpserted;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Master
@Repository
@RequiredArgsConstructor
public class TagReplicationRepository {
	private final TagRepository tags;
	private final ReplicationWriter replication;

	public Tag save(Tag tag) {
		return replication.write(
			() -> tags.saveAndFlush(tag),
			saved -> new TagUpserted(saved.getName())
		);
	}

	public void delete(String name) {
		replication.write(
			() -> {
				tags.deleteById(name);
				tags.flush();
				return name;
			},
			TagDeleted::new
		);
	}
}
