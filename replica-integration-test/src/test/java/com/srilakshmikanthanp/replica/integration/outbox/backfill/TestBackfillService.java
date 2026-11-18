package com.srilakshmikanthanp.replica.integration.outbox.backfill;

import com.srilakshmikanthanp.replica.event.Event;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobCreateResult;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobService;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobStep;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobStepRepository;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillPlan;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillPlanner;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class TestBackfillService {
  private final BackfillJobService backfillJobService;
  private final BackfillJobStepRepository backfillJobStepRepository;

  public BackfillJobCreateResult start(long destinationId, List<Class<? extends Event>> events) {
    BackfillPlanner planner = BackfillPlan.planner();
    events.forEach(planner::addEvent);
    return backfillJobService.create(destinationId, planner.plan());
  }

  public void complete(BackfillJobStep step) {
    backfillJobStepRepository.advance(step, step.getCheckpoint().orElse(null), true);
  }
}
