package com.srilakshmikanthanp.replica.playground.replication.outbox;

import com.srilakshmikanthanp.replica.database.transaction.TransactionExecutor;
import com.srilakshmikanthanp.replica.outbox.OutboxRepository;
import com.srilakshmikanthanp.replica.outbox.OutboxTransporter;
import com.srilakshmikanthanp.replica.outbox.PollingOutboxPublisher;
import com.srilakshmikanthanp.replica.playground.replication.Master;
import com.srilakshmikanthanp.replica.playground.replication.ReplicationProperties;
import com.srilakshmikanthanp.replica.station.Station;
import com.srilakshmikanthanp.replica.station.StationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Master
@Component
@RequiredArgsConstructor
public class OutboxPublisherRunner {
	private final TransactionExecutor transactionExecutor;
	private final OutboxRepository outboxRepository;
	private final OutboxTransporter outboxTransporter;
	private final StationRepository stationRepository;
	private final ReplicationProperties properties;

	private final ExecutorService pool = Executors.newCachedThreadPool(runnable -> {
		Thread thread = new Thread(runnable, "outbox-publisher");
		thread.setDaemon(true);
		return thread;
	});

	private final Set<Long> running = ConcurrentHashMap.newKeySet();

	@Scheduled(fixedDelayString = "${playground.interval-ms:1000}")
	public void publish() {
		for (Station station : stationRepository.findAll()) {
			if (running.add(station.getId())) pool.submit(() -> publish(station));
		}
	}

	private void publish(Station station) {
		try {
			new PollingOutboxPublisher(transactionExecutor, outboxRepository, outboxTransporter, properties.getBatchSize(), station.getId()).call();
		} catch (Exception e) {
			log.warn("Publishing to station {} stopped, will try again: {}", station.getName(), e.getMessage());
		} finally {
			running.remove(station.getId());
		}
	}
}
