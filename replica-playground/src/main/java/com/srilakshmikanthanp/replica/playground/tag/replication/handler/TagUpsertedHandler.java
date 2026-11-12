package com.srilakshmikanthanp.replica.playground.tag.replication.handler;

import com.srilakshmikanthanp.replica.event.EventHandler;
import com.srilakshmikanthanp.replica.playground.tag.Tag;
import com.srilakshmikanthanp.replica.playground.tag.TagRepository;
import com.srilakshmikanthanp.replica.playground.tag.replication.event.TagUpserted;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TagUpsertedHandler implements EventHandler<TagUpserted> {
	private final TagRepository tags;

	@Override
	public void handle(TagUpserted event) {
		Tag tag = tags.findById(event.name()).orElseGet(Tag::new);
		tag.setName(event.name());
		tags.save(tag);
	}
}
