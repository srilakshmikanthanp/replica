package com.srilakshmikanthanp.replica.playground.replication.station;

import com.srilakshmikanthanp.replica.database.transaction.TransactionExecutor;
import com.srilakshmikanthanp.replica.playground.replication.ReplicationProperties;
import com.srilakshmikanthanp.replica.playground.replication.Slave;
import com.srilakshmikanthanp.replica.station.Station;
import com.srilakshmikanthanp.replica.station.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Slave
@Component
@RequiredArgsConstructor
public class MasterStationInitializer implements SmartInitializingSingleton {
  private final TransactionExecutor transactionExecutor;
  private final StationRepository stationRepository;
  private final PeerStationRepository peerStationRepository;
  private final ReplicationProperties properties;

  private void setupMasterStation(String name, String url) {
    for (Station existing : stationRepository.findAll()) {
      if (!existing.getName().equals(name)) {
        throw new IllegalStateException("This slave already receives from '" + existing.getName() + "', not '" + name + "'");
      }
    }

    Station station = stationRepository.findByName(name).orElseGet(() -> {
      Station created = new Station();
      created.setName(name);
      return stationRepository.save(created);
    });

    PeerStation peerStation = peerStationRepository.findById(station.getId()).orElseGet(PeerStation::new);
    peerStation.setStationId(station.getId());
    peerStation.setUrl(url);
    peerStationRepository.save(peerStation);
  }

  @Override
  public void afterSingletonsInstantiated() {
    Assert.hasText(properties.getMasterName(), "A slave needs playground.master-name");
    Assert.hasText(properties.getMasterUrl(), "A slave needs playground.master-url");
    String name = properties.getMasterName().trim();
    String url = properties.getMasterUrl().trim().replaceAll("/+$", "");
    transactionExecutor.run(() -> setupMasterStation(name, url));
  }
}
