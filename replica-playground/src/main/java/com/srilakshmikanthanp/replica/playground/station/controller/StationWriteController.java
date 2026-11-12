package com.srilakshmikanthanp.replica.playground.station.controller;

import com.srilakshmikanthanp.replica.playground.replication.Master;
import com.srilakshmikanthanp.replica.playground.replication.outbox.OutboxViewRepository;
import com.srilakshmikanthanp.replica.playground.station.PeerStation;
import com.srilakshmikanthanp.replica.playground.station.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	private final OutboxViewRepository outbox;

	@PostMapping
	ResponseEntity<StationResponseDto> create(@RequestBody StationRequestDto request) {
		if (request == null || request.name() == null || request.name().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A name is required");
		}

		if (request.url() == null || !request.url().matches("https?://.+")) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A URL starting with http:// or https:// is required");
		}

		PeerStation station = stationService.create(request.name().trim(), request.url().trim().replaceAll("/+$", ""));
		StationResponseDto body = StationController.view(station.getStationId(), request.name().trim(), station.getUrl(), station.isReady(), outbox);
		return ResponseEntity.status(HttpStatus.CREATED).body(body);
	}
}
