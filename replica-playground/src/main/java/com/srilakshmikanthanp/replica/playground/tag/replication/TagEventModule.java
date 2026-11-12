package com.srilakshmikanthanp.replica.playground.tag.replication;

import com.srilakshmikanthanp.replica.event.EventHandlerRegistry;
import com.srilakshmikanthanp.replica.event.EventModule;
import com.srilakshmikanthanp.replica.event.EventRegistry;
import com.srilakshmikanthanp.replica.playground.tag.replication.event.TagDeleted;
import com.srilakshmikanthanp.replica.playground.tag.replication.event.TagUpserted;
import com.srilakshmikanthanp.replica.playground.tag.replication.handler.TagDeletedHandler;
import com.srilakshmikanthanp.replica.playground.tag.replication.handler.TagUpsertedHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TagEventModule implements EventModule {
	private final TagUpsertedHandler tagUpsertedHandler;
	private final TagDeletedHandler tagDeletedHandler;

	@Override
	public EventRegistry eventRegistry() {
		return EventRegistry.builder()
			.registerEvent("TagUpserted", TagUpserted.class)
			.registerEvent("TagDeleted", TagDeleted.class)
			.build();
	}

	@Override
	public EventHandlerRegistry eventHandlerRegistry() {
		return EventHandlerRegistry.builder()
			.registerEventHandler(TagUpserted.class, tagUpsertedHandler)
			.registerEventHandler(TagDeleted.class, tagDeletedHandler)
			.build();
	}
}
