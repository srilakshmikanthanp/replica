package com.srilakshmikanthanp.replica.playground.replication.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.srilakshmikanthanp.replica.event.EventContext;
import com.srilakshmikanthanp.replica.event.EventHandlerRegistry;
import com.srilakshmikanthanp.replica.event.EventModule;
import com.srilakshmikanthanp.replica.event.EventRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class EventConfig {
  @Bean
  JsonEventCodec jsonEventCodec(ObjectMapper objectMapper) {
    return new JsonEventCodec(objectMapper);
  }

  @Bean
  EventContext eventContext(List<EventModule> modules) {
    return EventContext.builder().addModules(modules).build();
  }

  @Bean
  EventRegistry eventRegistry(EventContext eventContext) {
    return eventContext.eventRegistry();
  }

  @Bean
  EventHandlerRegistry eventHandlerRegistry(EventContext eventContext) {
    return eventContext.eventHandlerRegistry();
  }
}
