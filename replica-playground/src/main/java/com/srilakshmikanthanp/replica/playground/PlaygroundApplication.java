package com.srilakshmikanthanp.replica.playground;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EntityScan({"com.srilakshmikanthanp.replica.spring", "com.srilakshmikanthanp.replica.playground"})
@EnableJpaRepositories({"com.srilakshmikanthanp.replica.spring", "com.srilakshmikanthanp.replica.playground"})
public class PlaygroundApplication {
	public static void main(String[] args) {
		SpringApplication.run(PlaygroundApplication.class, args);
	}
}
