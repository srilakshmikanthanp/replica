package com.srilakshmikanthanp.replica.integration.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TEST_USER")
@Getter
@Setter
public class TestUser {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", nullable = false)
  private long id;

  @Column(name = "NAME", nullable = false)
  private String name;
}
