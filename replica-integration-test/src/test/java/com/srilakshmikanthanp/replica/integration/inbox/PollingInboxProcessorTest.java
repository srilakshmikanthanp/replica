package com.srilakshmikanthanp.replica.integration.inbox;

import com.srilakshmikanthanp.replica.inbox.InboxProcessor;
import com.srilakshmikanthanp.replica.integration.IntegrationTest;
import com.srilakshmikanthanp.replica.integration.station.TestStationService;
import com.srilakshmikanthanp.replica.integration.user.TestUserConfig;
import com.srilakshmikanthanp.replica.integration.user.TestUserCreatedEvent;
import com.srilakshmikanthanp.replica.station.Station;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest(TestUserConfig.class)
class PollingInboxProcessorTest {
  @Autowired
  private TestStationService testStationService;

  @Autowired
  private TestInboxService testInboxService;

  @Autowired
  private TestInboxProcessorFactory processors;

  @Test
  void shouldApplyMessagesInOrderAndMarkThemProcessed() {
    Station station = testStationService.create("station");
    TestUserCreatedEvent alice = new TestUserCreatedEvent(1, "alice");
    TestUserCreatedEvent bob = new TestUserCreatedEvent(2, "bob");
    TestUserCreatedEvent carol = new TestUserCreatedEvent(3, "carol");
    testInboxService.receive(station.getId(), 3, carol);
    testInboxService.receive(station.getId(), 1, alice);
    testInboxService.receive(station.getId(), 2, bob);
    RecordingEventHandler<TestUserCreatedEvent> created = new RecordingEventHandler<>(TestUserCreatedEvent.class);
    InboxProcessor processor = processors.polling(station.getId(), created);

    processor.process();

    assertEquals(List.of(alice, bob, carol), created.getHandledEvents());
    assertTrue(testInboxService.unprocessedMessageIds(station.getId()).isEmpty());
  }

  @Test
  void shouldKeepMessageUnprocessedWhenHandlerFails() {
    Station station = testStationService.create("station");
    testInboxService.receive(station.getId(), new TestUserCreatedEvent(1, "alice"));
    InboxProcessor processor = processors.polling(station.getId(), new FailingEventHandler<>(TestUserCreatedEvent.class));

    assertThrows(IllegalStateException.class, processor::process);

    assertEquals(List.of(1L), testInboxService.unprocessedMessageIds(station.getId()));
  }
}
