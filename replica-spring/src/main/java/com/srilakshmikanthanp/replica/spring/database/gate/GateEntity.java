package com.srilakshmikanthanp.replica.spring.database.gate;

import com.srilakshmikanthanp.replica.spring.ReplicaTables;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "ReplicaGate")
@Table(name = ReplicaTables.GATE)
@Getter
@Setter
public class GateEntity {
	@Id
	@Column(name = "ID", nullable = false)
	private int id;

  public static GateEntity createDefault() {
    GateEntity gate = new GateEntity();
    gate.setId(1);
    return gate;
  }
}
