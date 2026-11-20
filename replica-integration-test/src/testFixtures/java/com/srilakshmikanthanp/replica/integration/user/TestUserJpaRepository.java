package com.srilakshmikanthanp.replica.integration.user;

import org.springframework.data.domain.Limit;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface TestUserJpaRepository extends Repository<TestUser, Long> {
  List<TestUser> findAll();

  List<TestUser> findAllByIdGreaterThanOrderByIdAsc(long id, Limit limit);

  TestUser save(TestUser user);

  Optional<TestUser> findById(long id);

  long count();
}
