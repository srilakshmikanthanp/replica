package com.srilakshmikanthanp.replica.spring.outbox;

import com.srilakshmikanthanp.replica.spring.ReplicaTables;
import com.srilakshmikanthanp.replica.spring.station.StationEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "ReplicaOutbox")
@Table(
	name = ReplicaTables.OUTBOX,
	indexes = @Index(columnList = "DESTINATION_ID, PUBLISHED_AT, ID")
)
@Getter
@Setter
public class OutboxEntity {
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

	@Column(name = "EVENT_TYPE", nullable = false, length = 500)
	private String eventType;

	@Column(name = "PAYLOAD", nullable = false, columnDefinition = "TEXT")
	private String payload;

	@Column(name = "CREATED_AT", nullable = false)
	private long createdAt;

	@Column(name = "PUBLISHED_AT")
	private Long publishedAt;
}
