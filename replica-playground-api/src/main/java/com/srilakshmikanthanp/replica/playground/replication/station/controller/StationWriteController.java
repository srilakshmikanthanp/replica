package com.srilakshmikanthanp.replica.playground.replication.station.controller;

import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobRepository;
import com.srilakshmikanthanp.replica.playground.replication.Master;
import com.srilakshmikanthanp.replica.playground.replication.outbox.OutboxEntityRepository;
import com.srilakshmikanthanp.replica.playground.replication.station.PeerStation;
import com.srilakshmikanthanp.replica.playground.replication.station.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Master
@RestController
@RequestMapping("/stations")
@RequiredArgsConstructor
public class StationWriteController {
  private final StationService stationService;
  private final BackfillJobRepository jobs;
  private final OutboxEntityRepository outbox;

  @PostMapping
  ResponseEntity<StationResponseDto> create(@RequestBody StationRequestDto request) {
    if (request == null || request.name() == null || request.name().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A name is required");
    }

    if (request.url() == null || !request.url().matches("https?://.+")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A URL starting with http:// or https:// is required");
    }

    PeerStation station = stationService.create(request.name().trim(), request.url().trim().replaceAll("/+$", ""));
    boolean ready = jobs.findUnfinished().stream().noneMatch(job -> job.getDestinationId() == station.getStationId());
    StationResponseDto body = StationController.view(station.getStationId(), request.name().trim(), station.getUrl(), ready, outbox);
    return ResponseEntity.status(HttpStatus.CREATED).body(body);
  }

  @DeleteMapping("/{name}")
  ResponseEntity<Void> remove(@PathVariable String name) {
    stationService.remove(name);
    return ResponseEntity.noContent().build();
  }
}
