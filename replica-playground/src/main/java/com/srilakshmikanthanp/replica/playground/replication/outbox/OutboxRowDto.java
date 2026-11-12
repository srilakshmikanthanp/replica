package com.srilakshmikanthanp.replica.playground.replication.outbox;

public record OutboxRowDto(long id, String destination, String eventType, String payload, long createdAt, Long publishedAt) {}
