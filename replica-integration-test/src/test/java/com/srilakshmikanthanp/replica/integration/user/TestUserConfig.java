package com.srilakshmikanthanp.replica.integration.user;

import com.srilakshmikanthanp.replica.integration.IntegrationTestConfig;
import com.srilakshmikanthanp.replica.outbox.OutboxTransactionExecutor;
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
}
