package com.srilakshmikanthanp.replica.playground.product.replication.event;

import com.srilakshmikanthanp.replica.event.Event;

public record ProductUpserted(String sku, String categoryCode, String name, long price) implements Event {
}
