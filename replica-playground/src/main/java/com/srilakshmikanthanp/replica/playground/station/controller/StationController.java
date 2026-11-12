package com.srilakshmikanthanp.replica.playground.station.controller;

import com.srilakshmikanthanp.replica.playground.replication.outbox.OutboxViewRepository;
import com.srilakshmikanthanp.replica.playground.station.PeerStation;
import com.srilakshmikanthanp.replica.playground.station.PeerStationRepository;
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

@RestController
@RequestMapping("/stations")
@RequiredArgsConstructor
public class StationController {
	private final StationRepository stations;
	private final PeerStationRepository peerStations;
	private final OutboxViewRepository outbox;

	static StationResponseDto view(long id, String name, String url, boolean ready, OutboxViewRepository outbox) {
		return new StationResponseDto(id, name, url, ready, outbox.countByDestinationIdAndPublishedAtIsNull(id));
	}

	private StationResponseDto view(Station station) {
		PeerStation peerStation = peerStations.findById(station.getId()).orElse(null);

		// A station without a PeerStation is not one this instance sends to (a slave's master), so there is nothing to wait for.
		return view(station.getId(), station.getName(), peerStation == null ? null : peerStation.getUrl(), peerStation == null || peerStation.isReady(), outbox);
	}

	@GetMapping
	List<StationResponseDto> list() {
		return stations.findAll().stream().map(this::view).toList();
	}

	@GetMapping("/{name}")
	StationResponseDto get(@PathVariable String name) {
		return stations.findByName(name).map(this::view)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No station " + name));
	}
}
