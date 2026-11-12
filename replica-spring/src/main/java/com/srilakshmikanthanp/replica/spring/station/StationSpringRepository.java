package com.srilakshmikanthanp.replica.spring.station;

import com.srilakshmikanthanp.replica.station.Station;
import com.srilakshmikanthanp.replica.station.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.support.TransactionOperations;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class StationSpringRepository implements StationRepository {
	private final StationJpaRepository jpaRepository;
	private final TransactionOperations transactionOperations;

  private static StationEntity toEntity(Station station) {
    StationEntity entity = new StationEntity();
    entity.setName(station.getName());
    return entity;
  }

	private static Station toModel(StationEntity entity) {
		Station station = new Station();
		station.setId(entity.getId());
		station.setName(entity.getName());
		return station;
	}

	@Override
	public Optional<Station> findByName(String name) {
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("name cannot be null or blank");
		}

		return jpaRepository.findByName(name).map(StationSpringRepository::toModel);
	}

	@Override
	public List<Station> findAll() {
		return jpaRepository.findAllByOrderByIdAsc().stream().map(StationSpringRepository::toModel).toList();
	}

	@Override
	public Station save(Station station) {
		StationEntity entity = toEntity(station);
		transactionOperations.executeWithoutResult(status -> jpaRepository.save(entity));
		station.setId(entity.getId());
		return station;
	}
}
