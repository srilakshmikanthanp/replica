package com.srilakshmikanthanp.replica.playground.tag.controller;

import com.srilakshmikanthanp.replica.playground.tag.Tag;

import java.time.Instant;

public record TagResponseDto(String name, Instant updatedAt) {
	static TagResponseDto of(Tag tag) {
		return new TagResponseDto(tag.getName(), tag.getUpdatedAt());
	}
}
