package com.srilakshmikanthanp.replica.integration.outbox.backfill;

import com.srilakshmikanthanp.replica.event.EventSerializer;
import com.srilakshmikanthanp.replica.integration.IntegrationTest;
import com.srilakshmikanthanp.replica.integration.station.TestStationService;
import com.srilakshmikanthanp.replica.integration.user.*;
import com.srilakshmikanthanp.replica.outbox.Outbox;
import com.srilakshmikanthanp.replica.outbox.OutboxRepository;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJob;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobRunner;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobService;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillPlan;
import com.srilakshmikanthanp.replica.station.Station;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@IntegrationTest(TestUserConfig.class)
class CursorBackfillPolicyTest {
  @Autowired
  private EventSerializer eventSerializer;

  @Autowired
  private TestStationService testStationService;

  @Autowired
  private TestUserService testUserService;

  @Autowired
  private BackfillJobService backfillJobService;

  @Autowired
  private OutboxRepository outboxRepository;

  @Autowired
  private BackfillJobRunner backfillJobRunner;

  @Test
  void shouldSkipEventBeyondCursor() {
    Station station = testStationService.create("station");
    TestUser first = testUserService.create("alice");
    TestUser second = testUserService.create("bob");
    TestUser third = testUserService.create("carol");
    BackfillPlan plan = BackfillPlan.planner().addEvent(TestUserCreatedEvent.class).plan();
    BackfillJob job = backfillJobService.create(station.getId(), plan).job();

    backfillJobRunner.runNextBatch(job);
    testUserService.renameAndBroadcast(third.getId(), "caroline", station.getId());
    List<Outbox> rows = outboxRepository.findUnpublished(station.getId(), 10);

    List<String> expectedPayloads = List.of(
      eventSerializer.serialize(TestUserCreatedEvent.of(first)),
      eventSerializer.serialize(TestUserCreatedEvent.of(second))
    );

    List<String> expectedTypes = List.of(
      TestUserCreatedEvent.TYPE,
      TestUserCreatedEvent.TYPE
    );

    List<String> payloads = rows.stream().map(Outbox::getPayload).toList();
    List<String> types = rows.stream().map(Outbox::getEventType).toList();

    assertEquals(expectedTypes, types);
    assertEquals(expectedPayloads, payloads);
  }

  @Test
  void shouldSendEventBehindCursor() {
    Station station = testStationService.create("station");
    TestUser first = testUserService.create("alice");
    TestUser second = testUserService.create("bob");
    TestUser third = testUserService.create("carol");
    BackfillPlan plan = BackfillPlan.planner().addEvent(TestUserCreatedEvent.class).plan();
    BackfillJob job = backfillJobService.create(station.getId(), plan).job();

    backfillJobRunner.runNextBatch(job);
    TestUser renamed = testUserService.renameAndBroadcast(first.getId(), "alicia", station.getId());
    List<Outbox> rows = outboxRepository.findUnpublished(station.getId(), 10);

    List<String> expectedPayloads = List.of(
      eventSerializer.serialize(TestUserCreatedEvent.of(first)),
      eventSerializer.serialize(TestUserCreatedEvent.of(second)),
      eventSerializer.serialize(TestUserRenamedEvent.of(renamed))
    );

    List<String> expectedTypes = List.of(
      TestUserCreatedEvent.TYPE,
      TestUserCreatedEvent.TYPE,
      TestUserRenamedEvent.TYPE
    );

    List<String> payloads = rows.stream().map(Outbox::getPayload).toList();
    List<String> types = rows.stream().map(Outbox::getEventType).toList();

    assertEquals(expectedTypes, types);
    assertEquals(expectedPayloads, payloads);
  }
}
