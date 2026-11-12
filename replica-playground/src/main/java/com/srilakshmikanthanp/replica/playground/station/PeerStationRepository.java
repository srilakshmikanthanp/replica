package com.srilakshmikanthanp.replica.playground.station;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PeerStationRepository extends JpaRepository<PeerStation, Long> {
	boolean existsByStationIdInAndReadyFalse(Collection<Long> stationIds);

	List<PeerStation> findByReadyFalse();
}
