package com.srilakshmikanthanp.replica.playground.tag.controller;

import com.srilakshmikanthanp.replica.playground.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {
	private final TagRepository tags;

	@GetMapping
	List<TagResponseDto> list() {
		return tags.findAllByOrderByNameAsc().stream().map(TagResponseDto::of).toList();
	}

	@GetMapping("/{name}")
	TagResponseDto get(@PathVariable String name) {
		return tags.findById(name).map(TagResponseDto::of)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No tag " + name));
	}
}
