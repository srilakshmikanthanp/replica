package com.srilakshmikanthanp.replica.playground.replication.backfill;

import com.srilakshmikanthanp.replica.database.transaction.TransactionExecutor;
import com.srilakshmikanthanp.replica.event.EventRegistry;
import com.srilakshmikanthanp.replica.event.EventSerializer;
import com.srilakshmikanthanp.replica.outbox.OutboxRepository;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobRepository;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobRunner;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobService;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobStepRepository;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillPlan;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillRegistry;
import com.srilakshmikanthanp.replica.outbox.backfill.CursorCodec;
import com.srilakshmikanthanp.replica.playground.category.replication.CategoryEventSource;
import com.srilakshmikanthanp.replica.playground.category.replication.event.CategoryUpserted;
import com.srilakshmikanthanp.replica.playground.product.replication.ProductEventSource;
import com.srilakshmikanthanp.replica.playground.product.replication.ProductTagEventSource;
import com.srilakshmikanthanp.replica.playground.product.replication.event.ProductTagAdded;
import com.srilakshmikanthanp.replica.playground.product.replication.event.ProductUpserted;
import com.srilakshmikanthanp.replica.playground.replication.Master;
import com.srilakshmikanthanp.replica.playground.replication.ReplicationProperties;
import com.srilakshmikanthanp.replica.playground.tag.replication.TagEventSource;
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
    ProductTagEventSource productTags
  ) {
    return BackfillRegistry.builder()
      .register(CategoryUpserted.class, categories)
      .register(TagUpserted.class, tags)
      .register(ProductUpserted.class, products)
      .register(ProductTagAdded.class, productTags)
      .build();
  }

  @Bean
  BackfillPlan backfillPlan() {
    return BackfillPlan.planner()
      .addEvent(CategoryUpserted.class)
      .addEvent(TagUpserted.class)
      .addEvent(ProductUpserted.class)
      .addEvent(ProductTagAdded.class)
      .plan();
  }

  @Bean
  BackfillJobService backfillJobService(
    TransactionExecutor transactionExecutor,
    BackfillJobRepository jobs,
    BackfillJobStepRepository steps,
    EventRegistry eventRegistry
  ) {
    return new BackfillJobService(transactionExecutor, jobs, steps, eventRegistry);
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
