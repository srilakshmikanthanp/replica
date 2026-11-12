package com.srilakshmikanthanp.replica.playground.replication;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "playground")
public class ReplicationProperties {
	private String stationName;
	private long intervalMs = 1000;
	private int batchSize = 50;
}
