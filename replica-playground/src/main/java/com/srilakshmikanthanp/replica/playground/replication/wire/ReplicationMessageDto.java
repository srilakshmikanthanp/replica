package com.srilakshmikanthanp.replica.playground.replication.wire;

public record ReplicationMessageDto(long id, String eventType, String payload, long createdAt) {}
