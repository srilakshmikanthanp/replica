package com.srilakshmikanthanp.replica.playground.product.controller;

import com.srilakshmikanthanp.replica.playground.product.Product;
import com.srilakshmikanthanp.replica.playground.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
	private final ProductRepository products;

	/**
	 * All products, or only those in a category and/or with a tag.
	 */
	@GetMapping
	List<ProductResponseDto> list(@RequestParam(required = false) String category, @RequestParam(required = false) String tag) {
		List<Product> found;

		if (tag != null) {
			found = products.findByTag(tag).stream().filter(p -> category == null || p.getCategory().getCode().equals(category)).toList();
		} else if (category != null) {
			found = products.findByCategoryCodeOrderBySkuAsc(category);
		} else {
			found = products.findAllByOrderBySkuAsc();
		}

		return found.stream().map(ProductResponseDto::of).toList();
	}

	@GetMapping("/{sku}")
	ProductResponseDto get(@PathVariable String sku) {
		return products.findById(sku).map(ProductResponseDto::of)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No product " + sku));
	}
}
