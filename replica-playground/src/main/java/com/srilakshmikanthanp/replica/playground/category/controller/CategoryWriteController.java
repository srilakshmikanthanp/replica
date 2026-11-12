package com.srilakshmikanthanp.replica.playground.category.controller;

import com.srilakshmikanthanp.replica.playground.category.Category;
import com.srilakshmikanthanp.replica.playground.category.CategoryRepository;
import com.srilakshmikanthanp.replica.playground.category.replication.CategoryReplicationRepository;
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
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryWriteController {
	private final CategoryRepository categories;
	private final CategoryReplicationRepository replicatedCategories;

	@PutMapping("/{code}")
	ResponseEntity<CategoryResponseDto> put(@PathVariable String code, @RequestBody CategoryRequestDto request) {
		if (request == null || request.name() == null || request.name().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A name is required");
		}

		boolean existed = categories.existsById(code);

		Category category = new Category();
		category.setCode(code);
		category.setName(request.name());

		CategoryResponseDto saved = CategoryResponseDto.of(replicatedCategories.save(category));
		return ResponseEntity.status(existed ? HttpStatus.OK : HttpStatus.CREATED).body(saved);
	}

	@DeleteMapping("/{code}")
	ResponseEntity<Void> delete(@PathVariable String code) {
		if (!categories.existsById(code)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No category " + code);
		}

		replicatedCategories.delete(code);
		return ResponseEntity.noContent().build();
	}
}
