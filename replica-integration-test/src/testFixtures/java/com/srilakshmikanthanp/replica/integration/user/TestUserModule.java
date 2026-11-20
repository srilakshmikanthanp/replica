package com.srilakshmikanthanp.replica.integration.user;

import com.srilakshmikanthanp.replica.event.EventHandlerRegistry;
import com.srilakshmikanthanp.replica.event.EventModule;
import com.srilakshmikanthanp.replica.event.EventRegistry;

public class TestUserModule implements EventModule {
  @Override
  public EventRegistry eventRegistry() {
    return EventRegistry.builder()
      .registerEvent(TestUserCreatedEvent.TYPE, TestUserCreatedEvent.class)
      .registerEvent(TestUserRenamedEvent.TYPE, TestUserRenamedEvent.class)
      .build();
  }

  @Override
  public EventHandlerRegistry eventHandlerRegistry() {
    return EventHandlerRegistry.builder().build();
  }
}
