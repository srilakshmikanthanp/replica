package com.srilakshmikanthanp.replica.playground.product.controller;

import com.srilakshmikanthanp.replica.playground.product.Product;
import com.srilakshmikanthanp.replica.playground.tag.Tag;

import java.time.Instant;
import java.util.List;

public record ProductResponseDto(String sku, String categoryCode, String name, long price, List<String> tags, Instant updatedAt) {
	static ProductResponseDto of(Product product) {
		return new ProductResponseDto(
			product.getSku(),
			product.getCategory().getCode(),
			product.getName(),
			product.getPrice(),
			product.getTags().stream().map(Tag::getName).sorted().toList(),
			product.getUpdatedAt()
		);
	}
}
