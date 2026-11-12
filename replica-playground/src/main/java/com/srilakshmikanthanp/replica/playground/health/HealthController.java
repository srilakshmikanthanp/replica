package com.srilakshmikanthanp.replica.playground.health;

import com.srilakshmikanthanp.replica.playground.replication.ReplicationProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
	private final String role;
	private final String name;

	HealthController(@Value("${playground.role}") String role, ReplicationProperties properties) {
		this.role = role;
		this.name = properties.getStationName();
	}

	@GetMapping("/health")
	HealthResponseDto health() {
		return new HealthResponseDto("UP", role, name);
	}
}
