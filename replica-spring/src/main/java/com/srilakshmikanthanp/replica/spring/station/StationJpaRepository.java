package com.srilakshmikanthanp.replica.spring.station;

import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface StationJpaRepository extends Repository<StationEntity, Long> {
	Optional<StationEntity> findByName(String name);

	List<StationEntity> findAllByOrderByIdAsc();

	StationEntity save(StationEntity station);
}
