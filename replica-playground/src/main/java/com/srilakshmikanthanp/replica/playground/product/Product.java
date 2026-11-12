package com.srilakshmikanthanp.replica.playground.product;

import com.srilakshmikanthanp.replica.playground.category.Category;
import com.srilakshmikanthanp.replica.playground.tag.Tag;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pg_product")
@Getter
@Setter
public class Product {
	@Id
	private String sku;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "category_code", referencedColumnName = "code", nullable = false)
	private Category category;

	private String name;

	private long price;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
		name = "pg_product_tag",
		joinColumns = @JoinColumn(name = "product_sku"),
		inverseJoinColumns = @JoinColumn(name = "tag_name")
	)
	private Set<Tag> tags = new HashSet<>();

	@UpdateTimestamp
	@Column(name = "updated_at")
	private Instant updatedAt;
}
