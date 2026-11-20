package com.srilakshmikanthanp.replica.integration.user;

import com.srilakshmikanthanp.replica.outbox.OutboxTransactionExecutor;
import com.srilakshmikanthanp.replica.outbox.TransactionalOperationResult;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class TestUserService {
  private final TestUserJpaRepository testUserJpaRepository;
  private final OutboxTransactionExecutor outboxTransactionExecutor;

  public TestUser renameAndBroadcast(long id, String name, long... destinationIds) {
    List<Long> destinations = Arrays.stream(destinationIds).boxed().toList();

    return outboxTransactionExecutor.execute(() -> {
      TestUser user = rename(id, name);
      return TransactionalOperationResult.broadcast(user, TestUserRenamedEvent.of(user), destinations);
    }).result();
  }

  public TestUser createAndBroadcast(String name, long... destinationIds) {
    List<Long> destinations = Arrays.stream(destinationIds).boxed().toList();

    return outboxTransactionExecutor.execute(() -> {
      TestUser user = create(name);
      return TransactionalOperationResult.broadcast(user, TestUserCreatedEvent.of(user), destinations);
    }).result();
  }

  public TestUser rename(long id, String name) {
    TestUser user = testUserJpaRepository.findById(id).orElseThrow();
    user.setName(name);
    return testUserJpaRepository.save(user);
  }

  public TestUser create(String name) {
    TestUser user = new TestUser();
    user.setName(name);
    return testUserJpaRepository.save(user);
  }
}
