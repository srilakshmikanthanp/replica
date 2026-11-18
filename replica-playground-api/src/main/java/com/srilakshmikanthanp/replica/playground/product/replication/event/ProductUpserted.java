package com.srilakshmikanthanp.replica.playground.product.replication.event;

import com.srilakshmikanthanp.replica.event.Event;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillDependsOn;
import com.srilakshmikanthanp.replica.playground.category.replication.event.CategoryUpserted;

@BackfillDependsOn({CategoryUpserted.class})
public record ProductUpserted(String sku, String categoryCode, String name, long price) implements Event {
}
