package com.srilakshmikanthanp.replica.playground.replication.inbox;

import com.srilakshmikanthanp.replica.database.transaction.TransactionExecutor;
import com.srilakshmikanthanp.replica.inbox.Inbox;
import com.srilakshmikanthanp.replica.inbox.InboxRepository;
import com.srilakshmikanthanp.replica.playground.replication.Slave;
import com.srilakshmikanthanp.replica.playground.replication.wire.ReplicationBatchDto;
import com.srilakshmikanthanp.replica.playground.replication.wire.ReplicationResultDto;
import com.srilakshmikanthanp.replica.station.Station;
import com.srilakshmikanthanp.replica.station.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slave
@RestController
@RequiredArgsConstructor
public class ReceiveController {
	private final TransactionExecutor transactionExecutor;
	private final StationRepository stationRepository;
	private final InboxRepository inboxRepository;

  private Station resolveSender(String name) {
    List<Station> stations = stationRepository.findAll();

    if (stations.isEmpty()) {
      Station station = new Station();
      station.setName(name);
      return stationRepository.save(station);
    }

    Station existing = stations.getFirst();
    if (!existing.getName().equals(name)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "This station already receives from '" + existing.getName() + "', not '" + name + "'");
    }

    return existing;
  }

	@PostMapping("/replica/receive")
	ReplicationResultDto receive(@RequestBody ReplicationBatchDto batch) {
		if (batch == null || batch.sender() == null || batch.sender().isBlank() || batch.messages() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A sender and its messages are required");
		}

		return transactionExecutor.execute(() -> {
			Station sender = resolveSender(batch.sender());
			long receivedAt = System.currentTimeMillis();

			List<Inbox> rows = batch.messages().stream().map(message -> {
				Inbox inbox = new Inbox();
				inbox.setSourceId(sender.getId());
				inbox.setMessageId(message.id());
				inbox.setEventType(message.eventType());
				inbox.setPayload(message.payload());
				inbox.setCreatedAt(message.createdAt());
				inbox.setReceivedAt(receivedAt);
				return inbox;
			}).toList();

			int inserted = inboxRepository.saveAll(rows).size();
			return new ReplicationResultDto(rows.size(), inserted);
		});
	}
}
