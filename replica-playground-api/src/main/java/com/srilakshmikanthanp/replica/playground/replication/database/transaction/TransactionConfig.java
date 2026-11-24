package com.srilakshmikanthanp.replica.playground.replication.database.transaction;

import com.srilakshmikanthanp.replica.database.transaction.TransactionExecutor;
import com.srilakshmikanthanp.replica.spring.database.transaction.SpringTransactionExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TransactionConfig {
  @Bean
  TransactionExecutor transactionExecutor(PlatformTransactionManager transactionManager) {
    return new SpringTransactionExecutor(transactionManager);
  }
}
