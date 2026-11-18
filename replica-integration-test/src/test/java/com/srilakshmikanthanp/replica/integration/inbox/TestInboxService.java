package com.srilakshmikanthanp.replica.integration.inbox;

import com.srilakshmikanthanp.replica.event.Event;
import com.srilakshmikanthanp.replica.event.EventRegistry;
import com.srilakshmikanthanp.replica.event.EventSerializer;
import com.srilakshmikanthanp.replica.inbox.Inbox;
import com.srilakshmikanthanp.replica.inbox.InboxRepository;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class TestInboxService {
  private final InboxRepository inboxRepository;
  private final EventRegistry eventRegistry;
  private final EventSerializer eventSerializer;

  private Inbox message(long sourceId, long messageId, Event event) {
    Inbox message = new Inbox();
    message.setSourceId(sourceId);
    message.setMessageId(messageId);
    message.setEventType(eventRegistry.getEventTypeByEventClass(event.getClass()));
    message.setPayload(eventSerializer.serialize(event));
    message.setCreatedAt(System.currentTimeMillis());
    message.setReceivedAt(System.currentTimeMillis());
    return message;
  }

  public void receive(long sourceId, long messageId, Event event) {
    inboxRepository.saveAll(List.of(message(sourceId, messageId, event)));
  }

  public void receive(long sourceId, Event... events) {
    List<Inbox> messages = new ArrayList<>();

    for (int i = 0; i < events.length; i++) {
      messages.add(message(sourceId, i + 1, events[i]));
    }

    inboxRepository.saveAll(messages);
  }

  public List<Long> unprocessedMessageIds(long sourceId) {
    return inboxRepository.findUnprocessed(sourceId, 10).stream().map(Inbox::getMessageId).toList();
  }
}
