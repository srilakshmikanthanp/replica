package com.srilakshmikanthanp.replica.integration.outbox;

import com.srilakshmikanthanp.replica.outbox.Outbox;
import com.srilakshmikanthanp.replica.outbox.OutboxTransporter;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class RecordingOutboxTransporter implements OutboxTransporter {
  @Getter
  private final List<Long> sentIds = new ArrayList<>();

  @Override
  public void send(List<Outbox> messages) {
    messages.forEach(message -> sentIds.add(message.getId()));
  }
}
