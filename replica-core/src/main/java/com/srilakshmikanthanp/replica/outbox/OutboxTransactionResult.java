package com.srilakshmikanthanp.replica.outbox;

import java.util.List;

public record OutboxTransactionResult<R>(
  R result,
  List<Outbox> outbox
) {
}
