package com.srilakshmikanthanp.replica.playground.product.replication.event;

import com.srilakshmikanthanp.replica.event.Event;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillDependsOn;
import com.srilakshmikanthanp.replica.playground.tag.replication.event.TagUpserted;

@BackfillDependsOn({ProductUpserted.class, TagUpserted.class})
public record ProductTagAdded(String productSku, String tagName) implements Event {
}
