package com.srilakshmikanthanp.replica.playground.product.replication.handler;

import com.srilakshmikanthanp.replica.event.EventHandler;
import com.srilakshmikanthanp.replica.playground.product.ProductRepository;
import com.srilakshmikanthanp.replica.playground.product.replication.event.ProductDeleted;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductDeletedHandler implements EventHandler<ProductDeleted> {
	private final ProductRepository products;

	@Override
	public void handle(ProductDeleted event) {
		products.deleteById(event.sku());
	}
}
