package com.srilakshmikanthanp.replica.spring.outbox.backfill;

public interface UnfinishedStep {
  BackfillJobStepEntity getStep();
  long getDestinationId();
}
