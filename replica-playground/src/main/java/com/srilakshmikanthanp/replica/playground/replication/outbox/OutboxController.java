package com.srilakshmikanthanp.replica.playground.replication.outbox;

import com.srilakshmikanthanp.replica.station.Station;
import com.srilakshmikanthanp.replica.station.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OutboxController {
	private final OutboxViewRepository outbox;
	private final StationRepository stations;

	@GetMapping("/replication/outbox")
	List<OutboxRowDto> latest() {
		Map<Long, String> names = stations.findAll().stream().collect(Collectors.toMap(Station::getId, Station::getName));

		return outbox.findTop100ByOrderByIdDesc().stream()
			.map(row -> new OutboxRowDto(row.getId(), names.getOrDefault(row.getDestinationId(), String.valueOf(row.getDestinationId())), row.getEventType(), row.getPayload(), row.getCreatedAt(), row.getPublishedAt()))
			.toList();
	}
}
