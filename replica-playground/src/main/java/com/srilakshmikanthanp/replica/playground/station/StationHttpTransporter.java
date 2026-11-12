package com.srilakshmikanthanp.replica.playground.station;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srilakshmikanthanp.replica.outbox.Outbox;
import com.srilakshmikanthanp.replica.outbox.OutboxTransportException;
import com.srilakshmikanthanp.replica.outbox.OutboxTransporter;
import com.srilakshmikanthanp.replica.playground.replication.Master;
import com.srilakshmikanthanp.replica.playground.replication.ReplicationProperties;
import com.srilakshmikanthanp.replica.playground.replication.wire.ReplicationBatchDto;
import com.srilakshmikanthanp.replica.playground.replication.wire.ReplicationMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

@Master
@Component
@RequiredArgsConstructor
public class StationHttpTransporter implements OutboxTransporter {
	private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);

	private final ObjectMapper objectMapper;
	private final PeerStationRepository peerStations;
	private final ReplicationProperties properties;

	private final HttpClient httpClient = HttpClient.newBuilder()
		.connectTimeout(Duration.ofSeconds(2))
		.followRedirects(HttpClient.Redirect.NEVER)
		.build();

	@Override
	public void send(List<Outbox> messages) throws OutboxTransportException {
		long stationId = messages.getFirst().getDestinationId();
		PeerStation peerStation = peerStations.findById(stationId).orElseThrow(() -> new OutboxTransportException("No URL is known for station " + stationId));

		List<ReplicationMessageDto> dtos = messages.stream()
			.map(message -> new ReplicationMessageDto(message.getId(), message.getEventType(), message.getPayload(), message.getCreatedAt()))
			.toList();

		String body;

		try {
			body = objectMapper.writeValueAsString(new ReplicationBatchDto(properties.getStationName(), dtos));
		} catch (JsonProcessingException e) {
			throw new OutboxTransportException("Cannot encode the batch", e);
		}

		HttpRequest request = HttpRequest.newBuilder(URI.create(peerStation.getUrl() + "/replica/receive"))
			.timeout(REQUEST_TIMEOUT)
			.header("Content-Type", "application/json")
			.POST(HttpRequest.BodyPublishers.ofString(body))
			.build();

		try {
			HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
			if (response.statusCode() / 100 != 2) throw new OutboxTransportException("Got status " + response.statusCode() + " from " + request.uri());
		} catch (IOException e) {
			throw new OutboxTransportException("Failed to send to " + request.uri(), e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new OutboxTransportException("Interrupted while sending to " + request.uri(), e);
		}
	}
}
