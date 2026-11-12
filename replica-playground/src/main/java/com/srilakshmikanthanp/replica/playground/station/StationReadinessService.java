package com.srilakshmikanthanp.replica.playground.station;

import com.srilakshmikanthanp.replica.playground.replication.Master;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Master
@Service
@RequiredArgsConstructor
public class StationReadinessService {
	private final PeerStationRepository peerStations;

	public void requireReady(Collection<Long> stationIds) {
		if (!stationIds.isEmpty() && peerStations.existsByStationIdInAndReadyFalse(stationIds)) {
			throw new StationNotReadyException();
		}
	}
}
