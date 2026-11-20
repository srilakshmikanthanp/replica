package com.srilakshmikanthanp.replica.integration.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TEST_REPLICA_USER")
@Getter
@Setter
public class TestReplicaUser {
  @Id
  @Column(name = "ID", nullable = false)
  private long id;

  @Column(name = "NAME", nullable = false)
  private String name;
}
