package com.srilakshmikanthanp.replica.playground.category.replication;

import com.srilakshmikanthanp.replica.event.EventHandlerRegistry;
import com.srilakshmikanthanp.replica.event.EventModule;
import com.srilakshmikanthanp.replica.event.EventRegistry;
import com.srilakshmikanthanp.replica.playground.category.replication.event.CategoryDeleted;
import com.srilakshmikanthanp.replica.playground.category.replication.event.CategoryUpserted;
import com.srilakshmikanthanp.replica.playground.category.replication.handler.CategoryDeletedHandler;
import com.srilakshmikanthanp.replica.playground.category.replication.handler.CategoryUpsertedHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryEventModule implements EventModule {
	private final CategoryUpsertedHandler categoryUpsertedHandler;
	private final CategoryDeletedHandler categoryDeletedHandler;

	@Override
	public EventRegistry eventRegistry() {
		return EventRegistry.builder()
			.registerEvent("CategoryUpserted", CategoryUpserted.class)
			.registerEvent("CategoryDeleted", CategoryDeleted.class)
			.build();
	}

	@Override
	public EventHandlerRegistry eventHandlerRegistry() {
		return EventHandlerRegistry.builder()
			.registerEventHandler(CategoryUpserted.class, categoryUpsertedHandler)
			.registerEventHandler(CategoryDeleted.class, categoryDeletedHandler)
			.build();
	}
}
