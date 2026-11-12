package com.srilakshmikanthanp.replica.playground.product.replication;

import com.srilakshmikanthanp.replica.event.EventHandlerRegistry;
import com.srilakshmikanthanp.replica.event.EventModule;
import com.srilakshmikanthanp.replica.event.EventRegistry;
import com.srilakshmikanthanp.replica.playground.product.replication.event.ProductDeleted;
import com.srilakshmikanthanp.replica.playground.product.replication.event.ProductTagAdded;
import com.srilakshmikanthanp.replica.playground.product.replication.event.ProductTagRemoved;
import com.srilakshmikanthanp.replica.playground.product.replication.event.ProductUpserted;
import com.srilakshmikanthanp.replica.playground.product.replication.handler.ProductDeletedHandler;
import com.srilakshmikanthanp.replica.playground.product.replication.handler.ProductTagAddedHandler;
import com.srilakshmikanthanp.replica.playground.product.replication.handler.ProductTagRemovedHandler;
import com.srilakshmikanthanp.replica.playground.product.replication.handler.ProductUpsertedHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductEventModule implements EventModule {
	private final ProductUpsertedHandler productUpsertedHandler;
	private final ProductDeletedHandler productDeletedHandler;
	private final ProductTagAddedHandler productTagAddedHandler;
	private final ProductTagRemovedHandler productTagRemovedHandler;

	@Override
	public EventRegistry eventRegistry() {
		return EventRegistry.builder()
			.registerEvent("ProductUpserted", ProductUpserted.class)
			.registerEvent("ProductDeleted", ProductDeleted.class)
			.registerEvent("ProductTagAdded", ProductTagAdded.class)
			.registerEvent("ProductTagRemoved", ProductTagRemoved.class)
			.build();
	}

	@Override
	public EventHandlerRegistry eventHandlerRegistry() {
		return EventHandlerRegistry.builder()
			.registerEventHandler(ProductUpserted.class, productUpsertedHandler)
			.registerEventHandler(ProductDeleted.class, productDeletedHandler)
			.registerEventHandler(ProductTagAdded.class, productTagAddedHandler)
			.registerEventHandler(ProductTagRemoved.class, productTagRemovedHandler)
			.build();
	}
}
