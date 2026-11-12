package com.srilakshmikanthanp.replica.playground.station;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pg_peer_station")
@Getter
@Setter
public class PeerStation {
	@Id
	private long stationId;
	private String url;
	private boolean ready;
}
