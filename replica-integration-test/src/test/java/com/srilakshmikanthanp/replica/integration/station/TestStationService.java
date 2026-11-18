package com.srilakshmikanthanp.replica.integration.station;

import com.srilakshmikanthanp.replica.station.Station;
import com.srilakshmikanthanp.replica.station.StationRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestStationService {
  private final StationRepository stationRepository;

  public Station create(String name) {
    Station station = new Station();
    station.setName(name);
    return stationRepository.save(station);
  }
}
