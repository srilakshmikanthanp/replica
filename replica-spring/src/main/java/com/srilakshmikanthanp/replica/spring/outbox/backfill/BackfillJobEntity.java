package com.srilakshmikanthanp.replica.spring.outbox.backfill;

import com.srilakshmikanthanp.replica.spring.ReplicaTables;
import com.srilakshmikanthanp.replica.spring.station.StationEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "ReplicaBackfillJob")
@Table(
	name = ReplicaTables.BACKFILL_JOB
)
@Getter
@Setter
public class BackfillJobEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private long id;

	@Column(name = "DESTINATION_ID", nullable = false)
	private long destinationId;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@ManyToOne
	@JoinColumn(name = "DESTINATION_ID", insertable = false, updatable = false)
	private StationEntity destination;
}
