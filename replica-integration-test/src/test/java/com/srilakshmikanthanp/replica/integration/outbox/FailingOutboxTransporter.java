package com.srilakshmikanthanp.replica.integration.outbox;

import com.srilakshmikanthanp.replica.outbox.Outbox;
import com.srilakshmikanthanp.replica.outbox.OutboxTransportException;
import com.srilakshmikanthanp.replica.outbox.OutboxTransporter;

import java.util.List;

public class FailingOutboxTransporter implements OutboxTransporter {
  @Override
  public void send(List<Outbox> messages) {
    throw new OutboxTransportException("send failed");
  }
}
