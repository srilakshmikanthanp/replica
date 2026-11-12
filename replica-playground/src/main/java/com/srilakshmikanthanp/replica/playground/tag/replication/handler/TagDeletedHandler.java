package com.srilakshmikanthanp.replica.playground.tag.replication.handler;

import com.srilakshmikanthanp.replica.event.EventHandler;
import com.srilakshmikanthanp.replica.playground.tag.TagRepository;
import com.srilakshmikanthanp.replica.playground.tag.replication.event.TagDeleted;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TagDeletedHandler implements EventHandler<TagDeleted> {
	private final TagRepository tags;

	@Override
	public void handle(TagDeleted event) {
		tags.deleteById(event.name());
	}
}
