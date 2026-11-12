package com.srilakshmikanthanp.replica.outbox.backfill;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class BackfillJobStep {
  private long id;
  private long jobId;
  private int ordinal;
  private String eventType;
  private String checkpoint;
  private Long completedAt;

  public Optional<String> getCheckpoint() {
    return Optional.ofNullable(checkpoint);
  }

  public Optional<Long> getCompletedAt() {
    return Optional.ofNullable(completedAt);
  }
}
