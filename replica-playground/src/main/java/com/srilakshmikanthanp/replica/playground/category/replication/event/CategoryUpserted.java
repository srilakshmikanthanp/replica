package com.srilakshmikanthanp.replica.playground.category.replication.event;

import com.srilakshmikanthanp.replica.event.Event;

public record CategoryUpserted(String code, String name) implements Event {}
