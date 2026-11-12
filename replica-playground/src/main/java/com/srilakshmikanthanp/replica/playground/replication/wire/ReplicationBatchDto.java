package com.srilakshmikanthanp.replica.playground.replication.wire;

import java.util.List;

public record ReplicationBatchDto(String sender, List<ReplicationMessageDto> messages) {}
