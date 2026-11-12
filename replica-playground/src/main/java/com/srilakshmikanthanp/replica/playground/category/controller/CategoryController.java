package com.srilakshmikanthanp.replica.playground.category.controller;

import com.srilakshmikanthanp.replica.playground.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
	private final CategoryRepository categories;

	@GetMapping
	List<CategoryResponseDto> list() {
		return categories.findAllByOrderByCodeAsc().stream().map(CategoryResponseDto::of).toList();
	}

	@GetMapping("/{code}")
	CategoryResponseDto get(@PathVariable String code) {
		return categories.findById(code).map(CategoryResponseDto::of)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No category " + code));
	}
}
