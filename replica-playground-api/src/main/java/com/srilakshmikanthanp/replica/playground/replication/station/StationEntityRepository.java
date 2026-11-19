package com.srilakshmikanthanp.replica.playground.replication.station;

import com.srilakshmikanthanp.replica.spring.station.StationEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface StationEntityRepository extends Repository<StationEntity, Long> {
  @Modifying
  @Query("DELETE FROM ReplicaStation s WHERE s.id = :id")
  void deleteById(@Param("id") long id);
}
