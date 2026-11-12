package com.srilakshmikanthanp.replica.playground.replication.inbox;

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
public class InboxController {
	private final InboxViewRepository inbox;
	private final StationRepository stations;

	@GetMapping("/replication/inbox")
	List<InboxRowDto> latest() {
		Map<Long, String> names = stations.findAll().stream().collect(Collectors.toMap(Station::getId, Station::getName));

		return inbox.findTop100ByOrderByIdDesc().stream()
			.map(row -> new InboxRowDto(row.getId(), names.getOrDefault(row.getSourceId(), String.valueOf(row.getSourceId())), row.getMessageId(), row.getEventType(), row.getPayload(), row.getCreatedAt(), row.getReceivedAt(), row.getProcessedAt()))
			.toList();
	}
}
