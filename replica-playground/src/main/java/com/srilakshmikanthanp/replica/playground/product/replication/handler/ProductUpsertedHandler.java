package com.srilakshmikanthanp.replica.playground.product.replication.handler;

import com.srilakshmikanthanp.replica.event.EventHandler;
import com.srilakshmikanthanp.replica.playground.category.Category;
import com.srilakshmikanthanp.replica.playground.category.CategoryRepository;
import com.srilakshmikanthanp.replica.playground.product.Product;
import com.srilakshmikanthanp.replica.playground.product.ProductRepository;
import com.srilakshmikanthanp.replica.playground.product.replication.event.ProductUpserted;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductUpsertedHandler implements EventHandler<ProductUpserted> {
	private final ProductRepository products;
	private final CategoryRepository categories;

	@Override
	public void handle(ProductUpserted event) {
		Category category = categories.findById(event.categoryCode()).orElseThrow(() -> new IllegalStateException("Cannot apply " + event + ": category " + event.categoryCode() + " does not exist"));
		Product product = products.findById(event.sku()).orElseGet(Product::new);
		product.setSku(event.sku());
		product.setCategory(category);
		product.setName(event.name());
		product.setPrice(event.price());
		products.save(product);
	}
}
