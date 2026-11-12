package com.srilakshmikanthanp.replica.playground.tag;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "pg_tag")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Tag {
	@Id
	@EqualsAndHashCode.Include
	private String name;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private Instant updatedAt;
}
