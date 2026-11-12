package com.srilakshmikanthanp.replica.playground.product.replication.handler;

import com.srilakshmikanthanp.replica.event.EventHandler;
import com.srilakshmikanthanp.replica.playground.product.Product;
import com.srilakshmikanthanp.replica.playground.product.ProductRepository;
import com.srilakshmikanthanp.replica.playground.product.replication.event.ProductTagRemoved;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductTagRemovedHandler implements EventHandler<ProductTagRemoved> {
	private final ProductRepository products;

	@Override
	public void handle(ProductTagRemoved event) {
		Product product = products.findById(event.productSku()).orElseThrow(() -> new IllegalStateException("Cannot apply " + event + ": product " + event.productSku() + " does not exist"));
		product.getTags().removeIf(tag -> tag.getName().equals(event.tagName()));
		products.save(product);
	}
}
