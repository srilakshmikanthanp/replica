package com.srilakshmikanthanp.replica.playground.product.replication.event;

import com.srilakshmikanthanp.replica.event.Event;

public record ProductTagRemoved(String productSku, String tagName) implements Event {}
