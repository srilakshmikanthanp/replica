package com.srilakshmikanthanp.replica.playground.replication.station;

import com.srilakshmikanthanp.replica.database.gate.ReplicationGate;
import com.srilakshmikanthanp.replica.database.transaction.TransactionExecutor;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobService;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillPlan;
import com.srilakshmikanthanp.replica.playground.replication.Master;
import com.srilakshmikanthanp.replica.playground.replication.backfill.BackfillJobEntityRepository;
import com.srilakshmikanthanp.replica.playground.replication.backfill.BackfillJobStepEntityRepository;
import com.srilakshmikanthanp.replica.playground.replication.inbox.InboxEntityRepository;
import com.srilakshmikanthanp.replica.playground.replication.outbox.OutboxEntityRepository;
import com.srilakshmikanthanp.replica.station.Station;
import com.srilakshmikanthanp.replica.station.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Master
@Service
@RequiredArgsConstructor
public class StationService {
  private final TransactionExecutor transactionExecutor;
  private final StationRepository stationRepository;
  private final PeerStationRepository peerStationRepository;
  private final BackfillJobStepEntityRepository backfillJobSteps;
  private final BackfillJobEntityRepository backfillJobs;
  private final OutboxEntityRepository outbox;
  private final InboxEntityRepository inbox;
  private final StationEntityRepository stations;
  private final ReplicationGate replicationGate;
  private final BackfillJobService backfillJobService;
  private final BackfillPlan backfillPlan;

  public PeerStation create(String name, String url) {
    return transactionExecutor.execute(() -> {
      if (stationRepository.findByName(name).isPresent()) throw new StationAlreadyExistsException(name);

      Station station = new Station();
      station.setName(name);
      stationRepository.save(station);

      PeerStation peerStation = new PeerStation();
      peerStation.setStationId(station.getId());
      peerStation.setUrl(url);
      peerStationRepository.save(peerStation);

      backfillJobService.create(station.getId(), backfillPlan);
      return peerStation;
    });
  }

  public void remove(String name) {
    transactionExecutor.run(() -> {
      replicationGate.enterExclusive();
      Station station = stationRepository.findByName(name).orElseThrow(() -> new NoSuchElementException("No station " + name));
      long stationId = station.getId();

      peerStationRepository.deleteById(stationId);
      backfillJobSteps.deleteByDestinationId(stationId);
      backfillJobs.deleteByDestinationId(stationId);
      outbox.deleteByDestinationId(stationId);
      inbox.deleteBySourceId(stationId);
      stations.deleteById(stationId);
    });
  }
}
