package com.srilakshmikanthanp.replica.playground.product;

import com.srilakshmikanthanp.replica.playground.category.Category;
import com.srilakshmikanthanp.replica.playground.tag.Tag;
import jakarta.persistence.*;
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
