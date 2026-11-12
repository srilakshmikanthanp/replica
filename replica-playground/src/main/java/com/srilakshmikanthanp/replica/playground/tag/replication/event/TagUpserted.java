package com.srilakshmikanthanp.replica.playground.tag.replication.event;

import com.srilakshmikanthanp.replica.event.Event;

public record TagUpserted(String name) implements Event {}
