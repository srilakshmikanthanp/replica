package com.srilakshmikanthanp.replica.playground.product.replication.event;

import com.srilakshmikanthanp.replica.event.Event;
import com.srilakshmikanthanp.replica.event.EventDependsOn;
import com.srilakshmikanthanp.replica.playground.category.replication.event.CategoryUpserted;

@EventDependsOn({CategoryUpserted.class})
public record ProductUpserted(String sku, String categoryCode, String name, long price) implements Event {}
