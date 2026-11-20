package com.srilakshmikanthanp.replica.integration.outbox;

import com.srilakshmikanthanp.replica.integration.IntegrationTest;
import com.srilakshmikanthanp.replica.integration.station.TestStationService;
import com.srilakshmikanthanp.replica.integration.user.TestUserConfig;
import com.srilakshmikanthanp.replica.integration.user.TestUserService;
import com.srilakshmikanthanp.replica.outbox.Outbox;
import com.srilakshmikanthanp.replica.outbox.OutboxPublisher;
import com.srilakshmikanthanp.replica.outbox.OutboxRepository;
import com.srilakshmikanthanp.replica.outbox.OutboxTransportException;
import com.srilakshmikanthanp.replica.station.Station;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest(TestUserConfig.class)
class PollingOutboxPublisherTest {
  @Autowired
  private TestStationService testStationService;

  @Autowired
  private TestUserService testUserService;

  @Autowired
  private OutboxRepository outboxRepository;

  @Autowired
  private TestOutboxPublisherFactory publishers;

  @Test
  void shouldSendUnpublishedRowsInIdOrderAndMarkThemPublished() {
    Station station = testStationService.create("station");
    testUserService.createAndBroadcast("alice", station.getId());
    testUserService.createAndBroadcast("bob", station.getId());
    testUserService.createAndBroadcast("carol", station.getId());
    List<Long> rowIds = outboxRepository.findUnpublished(station.getId(), 10).stream().map(Outbox::getId).toList();
    RecordingOutboxTransporter transporter = new RecordingOutboxTransporter();
    OutboxPublisher publisher = publishers.polling(station.getId(), transporter);

    publisher.publish();

    assertEquals(rowIds, transporter.getSentIds());
    assertTrue(outboxRepository.findUnpublished(station.getId(), 10).isEmpty());
  }

  @Test
  void shouldKeepRowsUnpublishedWhenSendFails() {
    Station station = testStationService.create("station");
    testUserService.createAndBroadcast("alice", station.getId());
    List<Long> rowIds = outboxRepository.findUnpublished(station.getId(), 10).stream().map(Outbox::getId).toList();
    OutboxPublisher publisher = publishers.polling(station.getId(), new FailingOutboxTransporter());

    assertThrows(OutboxTransportException.class, publisher::publish);

    assertEquals(rowIds, outboxRepository.findUnpublished(station.getId(), 10).stream().map(Outbox::getId).toList());
  }
}
