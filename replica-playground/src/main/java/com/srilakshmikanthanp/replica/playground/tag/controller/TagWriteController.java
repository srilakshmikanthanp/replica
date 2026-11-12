package com.srilakshmikanthanp.replica.playground.tag.controller;

import com.srilakshmikanthanp.replica.playground.replication.Master;
import com.srilakshmikanthanp.replica.playground.tag.Tag;
import com.srilakshmikanthanp.replica.playground.tag.TagRepository;
import com.srilakshmikanthanp.replica.playground.tag.replication.TagReplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Master
@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagWriteController {
	private final TagRepository tags;
	private final TagReplicationRepository replicatedTags;

	@PutMapping("/{name}")
	ResponseEntity<TagResponseDto> put(@PathVariable String name) {
		if (name.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A name is required");
		}

		boolean existed = tags.existsById(name);

		Tag tag = new Tag();
		tag.setName(name);

		TagResponseDto saved = TagResponseDto.of(replicatedTags.save(tag));
		return ResponseEntity.status(existed ? HttpStatus.OK : HttpStatus.CREATED).body(saved);
	}

	@DeleteMapping("/{name}")
	ResponseEntity<Void> delete(@PathVariable String name) {
		if (!tags.existsById(name)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No tag " + name);
		}

		replicatedTags.delete(name);
		return ResponseEntity.noContent().build();
	}
}
