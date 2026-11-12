package com.srilakshmikanthanp.replica.inbox;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class Inbox {
	private long id;
	private long sourceId;
	private long messageId;
	private String eventType;
	private String payload;
	private long createdAt;
	private long receivedAt;
	private Long processedAt;

	public Optional<Long> getProcessedAt() {
		return Optional.ofNullable(processedAt);
	}
}
