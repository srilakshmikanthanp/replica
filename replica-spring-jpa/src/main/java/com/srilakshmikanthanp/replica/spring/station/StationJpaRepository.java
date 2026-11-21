package com.srilakshmikanthanp.replica.spring.station;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StationJpaRepository extends Repository<StationEntity, Long> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT s FROM ReplicaStation s WHERE s.id = :id")
  Optional<StationEntity> findByIdForUpdate(@Param("id") long id);

  List<StationEntity> findAllByOrderByIdAsc();

  StationEntity save(StationEntity station);

  Optional<StationEntity> findByName(String name);
}
