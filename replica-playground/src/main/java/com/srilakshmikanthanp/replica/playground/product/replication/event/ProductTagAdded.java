package com.srilakshmikanthanp.replica.playground.product.replication.event;

import com.srilakshmikanthanp.replica.event.Event;
import com.srilakshmikanthanp.replica.event.EventDependsOn;
import com.srilakshmikanthanp.replica.playground.tag.replication.event.TagUpserted;

@EventDependsOn({ProductUpserted.class, TagUpserted.class})
public record ProductTagAdded(String productSku, String tagName) implements Event {}
