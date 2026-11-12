package com.srilakshmikanthanp.replica.playground.category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "pg_category")
@Getter
@Setter
public class Category {
	@Id
	private String code;

	private String name;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private Instant updatedAt;
}
