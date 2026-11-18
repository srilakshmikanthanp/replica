package com.srilakshmikanthanp.replica.integration.user;

import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface TestUserJpaRepository extends Repository<TestUser, Long> {
  TestUser save(TestUser user);

  Optional<TestUser> findById(long id);

  long count();
}
