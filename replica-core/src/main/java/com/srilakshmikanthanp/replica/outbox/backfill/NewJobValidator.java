package com.srilakshmikanthanp.replica.outbox.backfill;

import java.util.List;

public sealed interface NewJobValidator permits BackfillPolicy {
  void validate(long destinationId, List<BackfillJobStep> steps);
}
