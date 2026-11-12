package com.srilakshmikanthanp.replica.spring.inbox;

import com.srilakshmikanthanp.replica.spring.ReplicaTables;
import com.srilakshmikanthanp.replica.spring.station.StationEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "ReplicaInbox")
@Table(
	name = ReplicaTables.INBOX,
	uniqueConstraints = @UniqueConstraint(columnNames = {"SOURCE_ID", "MESSAGE_ID"}),
	indexes = @Index(columnList = "SOURCE_ID, PROCESSED_AT, MESSAGE_ID")
)
@Getter
@Setter
public class InboxEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private long id;

	@Column(name = "SOURCE_ID", nullable = false)
	private long sourceId;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@ManyToOne
	@JoinColumn(name = "SOURCE_ID", insertable = false, updatable = false)
	private StationEntity source;

	@Column(name = "MESSAGE_ID", nullable = false)
	private long messageId;

	@Column(name = "EVENT_TYPE", nullable = false, length = 500)
	private String eventType;

	@Column(name = "PAYLOAD", nullable = false, columnDefinition = "TEXT")
	private String payload;

	@Column(name = "CREATED_AT", nullable = false)
	private long createdAt;

	@Column(name = "RECEIVED_AT", nullable = false)
	private long receivedAt;

	@Column(name = "PROCESSED_AT")
	private Long processedAt;
}
