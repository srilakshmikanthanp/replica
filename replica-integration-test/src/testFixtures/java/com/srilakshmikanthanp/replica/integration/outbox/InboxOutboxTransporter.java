package com.srilakshmikanthanp.replica.integration.outbox;

import com.srilakshmikanthanp.replica.inbox.Inbox;
import com.srilakshmikanthanp.replica.inbox.InboxRepository;
import com.srilakshmikanthanp.replica.outbox.Outbox;
import com.srilakshmikanthanp.replica.outbox.OutboxTransporter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class InboxOutboxTransporter implements OutboxTransporter {
  private final InboxRepository inboxRepository;
  private final long sourceId;

  private Inbox toInbox(Outbox message) {
    Inbox inbox = new Inbox();
    inbox.setSourceId(sourceId);
    inbox.setMessageId(message.getId());
    inbox.setEventType(message.getEventType());
    inbox.setPayload(message.getPayload());
    inbox.setCreatedAt(message.getCreatedAt());
    inbox.setReceivedAt(System.currentTimeMillis());
    return inbox;
  }

  @Override
  public void send(List<Outbox> messages) {
    inboxRepository.saveAll(messages.stream().map(this::toInbox).toList());
  }
}
