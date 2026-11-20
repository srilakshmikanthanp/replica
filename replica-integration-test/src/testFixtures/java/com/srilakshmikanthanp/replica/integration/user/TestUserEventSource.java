package com.srilakshmikanthanp.replica.integration.user;

import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJob;
import com.srilakshmikanthanp.replica.outbox.backfill.EventBatch;
import com.srilakshmikanthanp.replica.outbox.backfill.EventSource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;

import java.util.List;

@RequiredArgsConstructor
public class TestUserEventSource implements EventSource<TestUserCreatedEvent, TestUserIdCursor> {
  private final TestUserJpaRepository testUserJpaRepository;

  @Override
  public EventBatch<TestUserCreatedEvent, TestUserIdCursor> getNextBatch(BackfillJob job, TestUserIdCursor cursor, int limit) {
    long after = cursor == null ? 0 : cursor.lastId();
    List<TestUser> users = testUserJpaRepository.findAllByIdGreaterThanOrderByIdAsc(after, Limit.of(limit));
    List<TestUserCreatedEvent> events = users.stream().map(TestUserCreatedEvent::of).toList();
    long last = users.isEmpty() ? after : users.getLast().getId();
    return new EventBatch<>(events, new TestUserIdCursor(last), users.size() == limit);
  }

  @Override
  public Class<TestUserIdCursor> getCursorClass() {
    return TestUserIdCursor.class;
  }
}
