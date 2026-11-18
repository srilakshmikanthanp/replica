package com.srilakshmikanthanp.replica.integration.database;

import com.srilakshmikanthanp.replica.database.gate.ReplicationGate;
import com.srilakshmikanthanp.replica.database.transaction.TransactionExecutor;
import com.srilakshmikanthanp.replica.spring.database.gate.GateJpaRepository;
import com.srilakshmikanthanp.replica.spring.database.gate.SpringReplicationGate;
import com.srilakshmikanthanp.replica.spring.database.transaction.SpringTransactionExecutor;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@EnableJpaRepositories(basePackages = {"com.srilakshmikanthanp.replica.spring", "com.srilakshmikanthanp.replica.integration"})
public class DatabaseConfig {
  @Bean
  DataSource dataSource() {
    return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).generateUniqueName(true).build();
  }

  @Bean
  LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    factory.setDataSource(dataSource);
    factory.setPackagesToScan("com.srilakshmikanthanp.replica.spring", "com.srilakshmikanthanp.replica.integration");
    factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
    factory.setJpaPropertyMap(Map.of("hibernate.hbm2ddl.auto", "create-drop", "hibernate.physical_naming_strategy", PhysicalNamingStrategyStandardImpl.class.getName()));
    return factory;
  }

  @Bean
  PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }

  @Bean
  TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
    return new TransactionTemplate(transactionManager);
  }

  @Bean
  TransactionExecutor transactionExecutor(TransactionTemplate transactionTemplate) {
    return new SpringTransactionExecutor(transactionTemplate);
  }

  @Bean
  ReplicationGate replicationGate(GateJpaRepository jpa) {
    return new SpringReplicationGate(jpa);
  }
}
