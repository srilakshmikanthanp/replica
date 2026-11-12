package com.srilakshmikanthanp.replica.playground.category.controller;

import com.srilakshmikanthanp.replica.playground.category.Category;

import java.time.Instant;

public record CategoryResponseDto(String code, String name, Instant updatedAt) {
	static CategoryResponseDto of(Category category) {
		return new CategoryResponseDto(category.getCode(), category.getName(), category.getUpdatedAt());
	}
}
