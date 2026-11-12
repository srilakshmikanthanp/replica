package com.srilakshmikanthanp.replica.playground.replication.station.controller;

import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJob;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobRepository;
import com.srilakshmikanthanp.replica.playground.replication.outbox.OutboxEntityRepository;
import com.srilakshmikanthanp.replica.playground.replication.station.PeerStation;
import com.srilakshmikanthanp.replica.playground.replication.station.PeerStationRepository;
import com.srilakshmikanthanp.replica.station.Station;
import com.srilakshmikanthanp.replica.station.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stations")
@RequiredArgsConstructor
public class StationController {
  private final StationRepository stations;
  private final PeerStationRepository peerStations;
  private final BackfillJobRepository jobs;
  private final OutboxEntityRepository outbox;

  static StationResponseDto view(long id, String name, String url, boolean ready, OutboxEntityRepository outbox) {
    return new StationResponseDto(id, name, url, ready, outbox.countByDestinationIdAndPublishedAtIsNull(id));
  }

  private StationResponseDto view(Station station, Set<Long> backfilling) {
    PeerStation peerStation = peerStations.findById(station.getId()).orElseThrow(() -> new IllegalStateException("No URL is known for station " + station.getName()));

    return view(station.getId(), station.getName(), peerStation.getUrl(), !backfilling.contains(station.getId()), outbox);
  }

  private Set<Long> backfilling() {
    return jobs.findUnfinished().stream().map(BackfillJob::getDestinationId).collect(Collectors.toSet());
  }

  @GetMapping
  List<StationResponseDto> list() {
    Set<Long> backfilling = backfilling();
    return stations.findAll().stream().map(station -> view(station, backfilling)).toList();
  }

  @GetMapping("/{name}")
  StationResponseDto get(@PathVariable String name) {
    Set<Long> backfilling = backfilling();
    return stations.findByName(name).map(station -> view(station, backfilling)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No station " + name));
  }
}
