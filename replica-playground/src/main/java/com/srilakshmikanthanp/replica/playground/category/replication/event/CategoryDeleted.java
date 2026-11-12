package com.srilakshmikanthanp.replica.playground.category.replication.event;

import com.srilakshmikanthanp.replica.event.Event;

public record CategoryDeleted(String code) implements Event {}
