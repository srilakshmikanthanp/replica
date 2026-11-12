package com.srilakshmikanthanp.replica.playground.product.controller;

import com.srilakshmikanthanp.replica.playground.category.Category;
import com.srilakshmikanthanp.replica.playground.category.CategoryRepository;
import com.srilakshmikanthanp.replica.playground.product.Product;
import com.srilakshmikanthanp.replica.playground.product.ProductRepository;
import com.srilakshmikanthanp.replica.playground.product.replication.ProductReplicationRepository;
import com.srilakshmikanthanp.replica.playground.replication.Master;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Master
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductWriteController {
	private final ProductRepository products;
	private final ProductReplicationRepository replicatedProducts;
	private final CategoryRepository categories;

	private ProductResponseDto view(String sku) {
		return products.findById(sku).map(ProductResponseDto::of)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No product " + sku));
	}

	@PutMapping("/{sku}")
	ResponseEntity<ProductResponseDto> put(@PathVariable String sku, @RequestBody ProductRequestDto request) {
		if (request == null || request.categoryCode() == null || request.name() == null || request.name().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A category code and a name are required");
		}

		Category category = categories.findById(request.categoryCode())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown category " + request.categoryCode()));

		boolean existed = products.existsById(sku);

		Product product = new Product();
		product.setSku(sku);
		product.setCategory(category);
		product.setName(request.name());
		product.setPrice(request.price());

		replicatedProducts.save(product);
		return ResponseEntity.status(existed ? HttpStatus.OK : HttpStatus.CREATED).body(view(sku));
	}

	@DeleteMapping("/{sku}")
	ResponseEntity<Void> delete(@PathVariable String sku) {
		if (!products.existsById(sku)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No product " + sku);
		}

		replicatedProducts.delete(sku);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{sku}/tags/{tag}")
	ProductResponseDto addTag(@PathVariable String sku, @PathVariable String tag) {
		replicatedProducts.addTag(sku, tag);
		return view(sku);
	}

	@DeleteMapping("/{sku}/tags/{tag}")
	ProductResponseDto removeTag(@PathVariable String sku, @PathVariable String tag) {
		replicatedProducts.removeTag(sku, tag);
		return view(sku);
	}
}
