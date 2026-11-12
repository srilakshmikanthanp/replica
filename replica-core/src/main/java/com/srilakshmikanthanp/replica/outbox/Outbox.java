package com.srilakshmikanthanp.replica.outbox;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class Outbox {
	private long id;
	private long destinationId;
	private String eventType;
	private String payload;
	private long createdAt;
	private Long publishedAt;

	public Optional<Long> getPublishedAt() {
		return Optional.ofNullable(publishedAt);
	}
}
