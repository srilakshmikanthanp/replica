package com.srilakshmikanthanp.replica.playground.product.replication.handler;

import com.srilakshmikanthanp.replica.event.EventHandler;
import com.srilakshmikanthanp.replica.playground.product.Product;
import com.srilakshmikanthanp.replica.playground.product.ProductRepository;
import com.srilakshmikanthanp.replica.playground.product.replication.event.ProductTagAdded;
import com.srilakshmikanthanp.replica.playground.tag.Tag;
import com.srilakshmikanthanp.replica.playground.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductTagAddedHandler implements EventHandler<ProductTagAdded> {
	private final ProductRepository products;
	private final TagRepository tags;

	@Override
	public void handle(ProductTagAdded event) {
		Product product = products.findById(event.productSku()).orElseThrow(() -> new IllegalStateException("Cannot apply " + event + ": product " + event.productSku() + " does not exist"));
		Tag tag = tags.findById(event.tagName()).orElseThrow(() -> new IllegalStateException("Cannot apply " + event + ": tag " + event.tagName() + " does not exist"));
		product.getTags().add(tag);
		products.save(product);
	}
}
