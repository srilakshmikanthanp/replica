package com.srilakshmikanthanp.replica.integration.outbox;

import com.srilakshmikanthanp.replica.integration.IntegrationTest;
import com.srilakshmikanthanp.replica.integration.station.TestStationService;
import com.srilakshmikanthanp.replica.integration.user.TestUserConfig;
import com.srilakshmikanthanp.replica.integration.user.TestUserService;
import com.srilakshmikanthanp.replica.outbox.OutboxPublisher;
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
  private TestOutboxService testOutboxService;

  @Autowired
  private TestOutboxPublisherFactory publishers;

  @Test
  void shouldSendUnpublishedRowsInIdOrderAndMarkThemPublished() {
    Station station = testStationService.create("station");
    testUserService.createAndBroadcast("alice", station.getId());
    testUserService.createAndBroadcast("bob", station.getId());
    testUserService.createAndBroadcast("carol", station.getId());
    List<Long> rowIds = testOutboxService.unpublishedIds(station.getId());
    RecordingOutboxTransporter transporter = new RecordingOutboxTransporter();
    OutboxPublisher publisher = publishers.polling(station.getId(), transporter);

    publisher.publish();

    assertEquals(rowIds, transporter.getSentIds());
    assertTrue(testOutboxService.unpublishedIds(station.getId()).isEmpty());
  }

  @Test
  void shouldKeepRowsUnpublishedWhenSendFails() {
    Station station = testStationService.create("station");
    testUserService.createAndBroadcast("alice", station.getId());
    List<Long> rowIds = testOutboxService.unpublishedIds(station.getId());
    OutboxPublisher publisher = publishers.polling(station.getId(), new FailingOutboxTransporter());

    assertThrows(OutboxTransportException.class, publisher::publish);

    assertEquals(rowIds, testOutboxService.unpublishedIds(station.getId()));
  }
}
