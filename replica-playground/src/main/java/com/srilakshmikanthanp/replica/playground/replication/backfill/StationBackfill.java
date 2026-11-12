package com.srilakshmikanthanp.replica.playground.replication.backfill;

import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobRunner;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobService;
import com.srilakshmikanthanp.replica.playground.replication.Master;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Master
@Component
@RequiredArgsConstructor
public class StationBackfill {
  private final BackfillJobService backfillJobService;
  private final BackfillJobRunner runner;

  @Scheduled(fixedDelayString = "${playground.interval-ms:1000}")
  public void backfill() {
    backfillJobService.findUnfinished().forEach(job -> runner.run(job.getId()));
  }
}
