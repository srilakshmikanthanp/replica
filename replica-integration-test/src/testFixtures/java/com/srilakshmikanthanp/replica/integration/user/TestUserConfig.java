package com.srilakshmikanthanp.replica.integration.user;

import com.srilakshmikanthanp.replica.integration.IntegrationTestConfig;
import com.srilakshmikanthanp.replica.event.EventRegistry;
import com.srilakshmikanthanp.replica.outbox.OutboxTransactionExecutor;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobStepRepository;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillRegistry;
import com.srilakshmikanthanp.replica.outbox.backfill.CursorCodec;
import com.srilakshmikanthanp.replica.outbox.backfill.CursorBackfillPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(IntegrationTestConfig.class)
public class TestUserConfig {
  @Bean
  TestUserModule testUserModule() {
    return new TestUserModule();
  }

  @Bean
  TestUserService testUserService(TestUserJpaRepository testUserJpaRepository, OutboxTransactionExecutor outboxTransactionExecutor) {
    return new TestUserService(testUserJpaRepository, outboxTransactionExecutor);
  }

  @Bean
  TestUserEventSource testUserEventSource(TestUserJpaRepository testUserJpaRepository) {
    return new TestUserEventSource(testUserJpaRepository);
  }

  @Bean
  TestUserCreatedHandler testUserCreatedHandler(TestReplicaUserJpaRepository testReplicaUserJpaRepository) {
    return new TestUserCreatedHandler(testReplicaUserJpaRepository);
  }

  @Bean
  TestUserRenamedHandler testUserRenamedHandler(TestReplicaUserJpaRepository testReplicaUserJpaRepository) {
    return new TestUserRenamedHandler(testReplicaUserJpaRepository);
  }

  @Bean
  BackfillRegistry backfillRegistry(EventRegistry eventRegistry, TestUserEventSource testUserEventSource) {
    return BackfillRegistry.builder(eventRegistry)
      .register(TestUserCreatedEvent.class, testUserEventSource)
      .build();
  }

  @Bean
  CursorBackfillPolicy cursorBackfillPolicy(BackfillJobStepRepository steps, BackfillRegistry backfillRegistry, CursorCodec cursorCodec) {
    return CursorBackfillPolicy.builder(steps, backfillRegistry, cursorCodec)
      .backfill(TestUserCreatedEvent.class, TestUserIdCursor.class)
        .affectedBy(TestUserCreatedEvent.class, TestUserCreatedEvent::id, TestUserIdCursor::lastId)
        .affectedBy(TestUserRenamedEvent.class, TestUserRenamedEvent::id, TestUserIdCursor::lastId)
        .done()
      .build();
  }
}
