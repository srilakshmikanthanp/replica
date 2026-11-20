package com.srilakshmikanthanp.replica.integration.user;

import com.srilakshmikanthanp.replica.integration.inbox.TestEventHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestUserRenamedHandler implements TestEventHandler<TestUserRenamedEvent> {
  private final TestReplicaUserJpaRepository testReplicaUserJpaRepository;

  @Override
  public Class<TestUserRenamedEvent> getEventClass() {
    return TestUserRenamedEvent.class;
  }

  @Override
  public void handle(TestUserRenamedEvent event) {
    TestReplicaUser user = testReplicaUserJpaRepository.findById(event.id()).orElseThrow();
    user.setName(event.name());
    testReplicaUserJpaRepository.save(user);
  }
}
