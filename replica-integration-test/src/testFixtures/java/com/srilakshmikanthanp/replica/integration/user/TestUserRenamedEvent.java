package com.srilakshmikanthanp.replica.integration.user;

import com.srilakshmikanthanp.replica.event.Event;

public record TestUserRenamedEvent(long id, String name) implements Event {
  public static final String TYPE = "test.user.renamed";

  public static TestUserRenamedEvent of(TestUser user) {
    return new TestUserRenamedEvent(user.getId(), user.getName());
  }
}
