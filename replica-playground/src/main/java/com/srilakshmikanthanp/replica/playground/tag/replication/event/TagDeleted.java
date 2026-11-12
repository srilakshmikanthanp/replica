package com.srilakshmikanthanp.replica.playground.tag.replication.event;

import com.srilakshmikanthanp.replica.event.Event;

public record TagDeleted(String name) implements Event {}
