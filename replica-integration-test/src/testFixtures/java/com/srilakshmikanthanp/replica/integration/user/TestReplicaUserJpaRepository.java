package com.srilakshmikanthanp.replica.integration.user;

import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface TestReplicaUserJpaRepository extends Repository<TestReplicaUser, Long> {
  TestReplicaUser save(TestReplicaUser user);

  Optional<TestReplicaUser> findById(long id);

  List<TestReplicaUser> findAll();
}
