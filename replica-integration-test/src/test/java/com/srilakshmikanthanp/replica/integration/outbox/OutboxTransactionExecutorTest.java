package com.srilakshmikanthanp.replica.integration.outbox;

import com.srilakshmikanthanp.replica.event.EventSerializer;
import com.srilakshmikanthanp.replica.integration.IntegrationTest;
import com.srilakshmikanthanp.replica.integration.station.TestStationService;
import com.srilakshmikanthanp.replica.integration.user.TestUser;
import com.srilakshmikanthanp.replica.integration.user.TestUserConfig;
import com.srilakshmikanthanp.replica.integration.user.TestUserCreatedEvent;
import com.srilakshmikanthanp.replica.integration.user.TestUserService;
import com.srilakshmikanthanp.replica.outbox.Outbox;
import com.srilakshmikanthanp.replica.station.Station;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

@IntegrationTest(TestUserConfig.class)
class OutboxTransactionExecutorTest {
  @Autowired
  private EventSerializer eventSerializer;

  @Autowired
  private TestStationService testStationService;

  @Autowired
  private TestUserService testUserService;

  @Autowired
  private TestOutboxService testOutboxService;

  @Test
  void shouldWriteOneRowForEachDestination() {
    Station stationOne = testStationService.create("stationOne");
    Station stationTwo = testStationService.create("stationTwo");

    testUserService.createAndBroadcast("alice", stationOne.getId(), stationTwo.getId());

    assertEquals(1, testOutboxService.unpublished(stationOne.getId()).size());
    assertEquals(1, testOutboxService.unpublished(stationTwo.getId()).size());
  }

  @Test
  void shouldStoreEventTypeAndPayload() {
    Station station = testStationService.create("station");

    TestUser user = testUserService.createAndBroadcast("bob", station.getId());

    Outbox row = testOutboxService.unpublished(station.getId()).getFirst();
    assertEquals("test.user.created", row.getEventType());
    assertEquals(eventSerializer.serialize(TestUserCreatedEvent.of(user)), row.getPayload());
  }
}
