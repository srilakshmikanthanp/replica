package com.srilakshmikanthanp.replica.playground.replication.inbox;

public record InboxRowDto(long id, String source, long messageId, String eventType, String payload, long createdAt, long receivedAt, Long processedAt) {}
