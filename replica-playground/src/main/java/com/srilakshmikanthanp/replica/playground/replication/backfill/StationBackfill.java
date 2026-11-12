package com.srilakshmikanthanp.replica.playground.replication.backfill;

import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJob;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobRepository;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobRunner;
import com.srilakshmikanthanp.replica.playground.replication.Master;
import com.srilakshmikanthanp.replica.playground.station.PeerStation;
import com.srilakshmikanthanp.replica.playground.station.PeerStationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Master
@Component
@RequiredArgsConstructor
public class StationBackfill {
	private final PeerStationRepository peerStations;
	private final BackfillJobRepository jobs;
	private final BackfillJobRunner runner;

	@Scheduled(fixedDelayString = "${playground.interval-ms:1000}")
	public void backfill() {
		for (PeerStation station : peerStations.findByReadyFalse()) {
			BackfillJob job = jobs.findAll().stream()
				.filter(candidate -> candidate.getDestinationId() == station.getStationId())
				.findFirst()
				.orElseThrow();

			if (runner.run(job.getId())) {
				station.setReady(true);
				peerStations.save(station);
			}
		}
	}
}
