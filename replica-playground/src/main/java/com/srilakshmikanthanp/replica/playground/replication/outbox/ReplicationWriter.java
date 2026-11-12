package com.srilakshmikanthanp.replica.playground.replication.outbox;

import com.srilakshmikanthanp.replica.event.Event;
import com.srilakshmikanthanp.replica.outbox.OutboxTransactionExecutor;
import com.srilakshmikanthanp.replica.outbox.TransactionalOperationResult;
import com.srilakshmikanthanp.replica.playground.replication.Master;
import com.srilakshmikanthanp.replica.playground.station.StationReadinessService;
import com.srilakshmikanthanp.replica.station.Station;
import com.srilakshmikanthanp.replica.station.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;


@Master
@Component
@RequiredArgsConstructor
public class ReplicationWriter {
  private final OutboxTransactionExecutor outboxTransactionExecutor;
  private final StationRepository stationRepository;
  private final StationReadinessService readiness;

  public <R, E extends Event> R write(Supplier<R> writer, Function<R, E> eventFor) {
    return outboxTransactionExecutor.execute(() -> {
      List<Long> stationIds = stationRepository.findAll().stream().map(Station::getId).toList();
      readiness.requireReady(stationIds);
      R result = writer.get();
      return TransactionalOperationResult.broadcast(result, eventFor.apply(result), stationIds);
    }).result();
  }
}
