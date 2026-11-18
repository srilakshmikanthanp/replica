package com.srilakshmikanthanp.replica.integration.outbox;

import com.srilakshmikanthanp.replica.outbox.Outbox;
import com.srilakshmikanthanp.replica.outbox.OutboxRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class TestOutboxService {
  private final OutboxRepository outboxRepository;

  public List<Outbox> unpublished(long destinationId) {
    return outboxRepository.findUnpublished(destinationId, 10);
  }

  public List<Long> unpublishedIds(long destinationId) {
    return unpublished(destinationId).stream().map(Outbox::getId).toList();
  }
}
