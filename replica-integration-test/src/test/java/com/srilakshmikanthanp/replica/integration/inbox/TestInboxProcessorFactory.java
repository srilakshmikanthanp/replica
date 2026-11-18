package com.srilakshmikanthanp.replica.integration.inbox;

import com.srilakshmikanthanp.replica.database.transaction.TransactionExecutor;
import com.srilakshmikanthanp.replica.event.Event;
import com.srilakshmikanthanp.replica.event.EventDeserializer;
import com.srilakshmikanthanp.replica.event.EventHandlerRegistry;
import com.srilakshmikanthanp.replica.event.EventHandlerRegistryBuilder;
import com.srilakshmikanthanp.replica.event.EventRegistry;
import com.srilakshmikanthanp.replica.inbox.InboxProcessor;
import com.srilakshmikanthanp.replica.inbox.InboxRepository;
import com.srilakshmikanthanp.replica.inbox.PollingInboxProcessor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestInboxProcessorFactory {
  private static final int BATCH_SIZE = 2;

  private final TransactionExecutor transactionExecutor;
  private final InboxRepository inboxRepository;
  private final EventRegistry eventRegistry;
  private final EventDeserializer eventDeserializer;

  private static <E extends Event> void register(EventHandlerRegistryBuilder builder, TestEventHandler<E> handler) {
    builder.registerEventHandler(handler.getEventClass(), handler);
  }

  private static EventHandlerRegistry registry(TestEventHandler<?>... handlers) {
    EventHandlerRegistryBuilder builder = EventHandlerRegistry.builder();

    for (TestEventHandler<?> handler : handlers) {
      register(builder, handler);
    }

    return builder.build();
  }

  public InboxProcessor polling(long sourceId, TestEventHandler<?>... handlers) {
    return new PollingInboxProcessor(transactionExecutor, inboxRepository, eventRegistry, eventDeserializer, registry(handlers), BATCH_SIZE, sourceId);
  }
}
