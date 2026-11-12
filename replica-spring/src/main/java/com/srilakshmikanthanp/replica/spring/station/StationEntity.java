package com.srilakshmikanthanp.replica.spring.station;

import com.srilakshmikanthanp.replica.spring.ReplicaTables;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "ReplicaStation")
@Table(
	name = ReplicaTables.STATION,
	uniqueConstraints = @UniqueConstraint(columnNames = "NAME")
)
@Getter
@Setter
public class StationEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private long id;

	@Column(name = "NAME", nullable = false, length = 255)
	private String name;
}
