package com.srilakshmikanthanp.replica.integration.user;

import com.srilakshmikanthanp.replica.event.Event;

public record TestUserCreatedEvent(long id, String name) implements Event {
  public static TestUserCreatedEvent of(TestUser user) {
    return new TestUserCreatedEvent(user.getId(), user.getName());
  }
}
