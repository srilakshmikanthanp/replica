package com.srilakshmikanthanp.replica.integration.user;

import com.srilakshmikanthanp.replica.event.Event;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillDependsOn;

@BackfillDependsOn(TestUserCreatedEvent.class)
public record TestUserRenamedEvent(long id, String name) implements Event {
  public static TestUserRenamedEvent of(TestUser user) {
    return new TestUserRenamedEvent(user.getId(), user.getName());
  }
}
