package com.srilakshmikanthanp.replica.station;

import java.util.List;
import java.util.Optional;

public interface StationRepository {
	Optional<Station> findByName(String name);

	/**
	 * @return every station, lowest ID first
	 */
	List<Station> findAll();

	/**
	 * Inserts the station and sets its generated ID.
	 */
	Station save(Station station);
}
