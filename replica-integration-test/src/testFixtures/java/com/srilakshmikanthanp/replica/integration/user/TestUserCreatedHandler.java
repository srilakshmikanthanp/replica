package com.srilakshmikanthanp.replica.integration.user;

import com.srilakshmikanthanp.replica.integration.inbox.TestEventHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestUserCreatedHandler implements TestEventHandler<TestUserCreatedEvent> {
  private final TestReplicaUserJpaRepository testReplicaUserJpaRepository;

  @Override
  public Class<TestUserCreatedEvent> getEventClass() {
    return TestUserCreatedEvent.class;
  }

  @Override
  public void handle(TestUserCreatedEvent event) {
    TestReplicaUser user = testReplicaUserJpaRepository.findById(event.id()).orElseGet(TestReplicaUser::new);
    user.setId(event.id());
    user.setName(event.name());
    testReplicaUserJpaRepository.save(user);
  }
}
