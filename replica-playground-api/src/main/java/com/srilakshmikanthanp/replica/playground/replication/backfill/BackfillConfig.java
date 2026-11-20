package com.srilakshmikanthanp.replica.playground.replication.backfill;

import com.srilakshmikanthanp.replica.database.transaction.TransactionExecutor;
import com.srilakshmikanthanp.replica.database.gate.ReplicationGate;
import com.srilakshmikanthanp.replica.event.EventRegistry;
import com.srilakshmikanthanp.replica.event.EventSerializer;
import com.srilakshmikanthanp.replica.outbox.OutboxRepository;
import com.srilakshmikanthanp.replica.outbox.backfill.*;
import com.srilakshmikanthanp.replica.playground.category.replication.CategoryEventSource;
import com.srilakshmikanthanp.replica.playground.category.replication.event.CategoryDeleted;
import com.srilakshmikanthanp.replica.playground.category.replication.event.CategoryUpserted;
import com.srilakshmikanthanp.replica.playground.product.replication.ProductEventSource;
import com.srilakshmikanthanp.replica.playground.product.replication.ProductTagEventSource;
import com.srilakshmikanthanp.replica.playground.product.replication.event.ProductDeleted;
import com.srilakshmikanthanp.replica.playground.product.replication.event.ProductTagAdded;
import com.srilakshmikanthanp.replica.playground.product.replication.event.ProductTagRemoved;
import com.srilakshmikanthanp.replica.playground.product.replication.event.ProductUpserted;
import com.srilakshmikanthanp.replica.playground.replication.Master;
import com.srilakshmikanthanp.replica.playground.replication.ReplicationProperties;
import com.srilakshmikanthanp.replica.playground.tag.replication.TagEventSource;
import com.srilakshmikanthanp.replica.playground.tag.replication.event.TagDeleted;
import com.srilakshmikanthanp.replica.playground.tag.replication.event.TagUpserted;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Master
@Configuration
public class BackfillConfig {
  @Bean
  BackfillRegistry backfillRegistry(
    CategoryEventSource categories,
    TagEventSource tags,
    ProductEventSource products,
    ProductTagEventSource productTags,
    EventRegistry eventRegistry
  ) {
    return BackfillRegistry.builder(eventRegistry)
      .register(CategoryUpserted.class, categories)
        .affectedBy(CategoryUpserted.class, CategoryUpserted::code, KeyCursor::last)
        .affectedBy(CategoryDeleted.class, CategoryDeleted::code, KeyCursor::last)
        .done()
      .register(TagUpserted.class, tags)
        .affectedBy(TagUpserted.class, TagUpserted::name, KeyCursor::last)
        .affectedBy(TagDeleted.class, TagDeleted::name, KeyCursor::last)
        .done()
      .register(ProductUpserted.class, products)
        .affectedBy(ProductUpserted.class, ProductUpserted::sku, KeyCursor::last)
        .affectedBy(ProductDeleted.class, ProductDeleted::sku, KeyCursor::last)
        .done()
      .register(ProductTagAdded.class, productTags)
        .affectedBy(ProductTagAdded.class, ProductTagAdded::productSku, KeyCursor::last)
        .affectedBy(ProductTagRemoved.class, ProductTagRemoved::productSku, KeyCursor::last)
        .affectedBy(ProductDeleted.class, ProductDeleted::sku, KeyCursor::last)
        .done()
      .build();
  }

  @Bean
  BackfillPlan backfillPlan() {
    return BackfillPlan.planner()
      .addEvent(CategoryUpserted.class)
      .addEvent(TagUpserted.class)
      .addEvent(ProductUpserted.class, CategoryUpserted.class)
      .addEvent(ProductTagAdded.class, ProductUpserted.class, TagUpserted.class)
      .plan();
  }

  @Bean
  BackfillJobService backfillJobService(
    TransactionExecutor transactionExecutor,
    ReplicationGate replicationGate,
    BackfillJobRepository jobs,
    BackfillJobStepRepository steps,
    EventRegistry eventRegistry
  ) {
    return new BackfillJobService(transactionExecutor, replicationGate, jobs, steps, eventRegistry);
  }

  @Bean
  BackfillJobRunner backfillJobRunner(
    BackfillJobRepository jobs,
    BackfillJobStepRepository steps,
    EventRegistry eventRegistry,
    BackfillRegistry backfillRegistry,
    TransactionExecutor transactionExecutor,
    EventSerializer eventSerializer,
    OutboxRepository outboxRepository,
    CursorCodec cursorCodec,
    ReplicationProperties properties
  ) {
    return new BackfillJobRunner(
      jobs,
      steps,
      eventRegistry,
      backfillRegistry,
      transactionExecutor,
      eventSerializer,
      outboxRepository,
      cursorCodec,
      properties.getBatchSize()
    );
  }
}
