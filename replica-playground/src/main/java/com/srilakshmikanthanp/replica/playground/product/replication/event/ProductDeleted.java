package com.srilakshmikanthanp.replica.playground.product.replication.event;

import com.srilakshmikanthanp.replica.event.Event;

public record ProductDeleted(String sku) implements Event {}
