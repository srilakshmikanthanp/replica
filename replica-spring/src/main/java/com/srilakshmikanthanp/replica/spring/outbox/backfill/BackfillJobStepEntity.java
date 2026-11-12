package com.srilakshmikanthanp.replica.spring.outbox.backfill;

import com.srilakshmikanthanp.replica.spring.ReplicaTables;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "ReplicaBackfillJobStep")
@Table(
	name = ReplicaTables.BACKFILL_JOB_STEP,
	uniqueConstraints = @UniqueConstraint(columnNames = {"JOB_ID", "ORDINAL"})
)
@Getter
@Setter
public class BackfillJobStepEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private long id;

	@Column(name = "JOB_ID", nullable = false)
	private long jobId;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@ManyToOne
	@JoinColumn(name = "JOB_ID", insertable = false, updatable = false)
	private BackfillJobEntity job;

	@Column(name = "ORDINAL", nullable = false)
	private int ordinal;

	@Column(name = "EVENT_TYPE", nullable = false, length = 500)
	private String eventType;

	@Column(name = "CHECKPOINT", columnDefinition = "TEXT")
	private String checkpoint;

	@Column(name = "COMPLETED_AT")
	private Long completedAt;
}
