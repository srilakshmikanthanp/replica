package com.srilakshmikanthanp.replica.playground.station;

import com.srilakshmikanthanp.replica.database.gate.ReplicationGate;
import com.srilakshmikanthanp.replica.database.transaction.TransactionExecutor;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobService;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillPlan;
import com.srilakshmikanthanp.replica.playground.replication.Master;
import com.srilakshmikanthanp.replica.station.Station;
import com.srilakshmikanthanp.replica.station.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Master
@Service
@RequiredArgsConstructor
public class StationService {
  private final TransactionExecutor transactionExecutor;
  private final ReplicationGate replicationGate;
  private final StationRepository stationRepository;
  private final PeerStationRepository peerStationRepository;
  private final BackfillJobService backfillJobService;
  private final BackfillPlan backfillPlan;

  public PeerStation create(String name, String url) {
    return transactionExecutor.execute(() -> {
      replicationGate.enterExclusive();

      if (stationRepository.findByName(name).isPresent()) throw new StationAlreadyExistsException(name);

      Station station = new Station();
      station.setName(name);
      stationRepository.save(station);

      PeerStation peerStation = new PeerStation();
      peerStation.setStationId(station.getId());
      peerStation.setUrl(url);
      peerStation.setReady(false);
      peerStationRepository.save(peerStation);

      backfillJobService.create(station.getId(), backfillPlan);
      return peerStation;
    });
  }
}
