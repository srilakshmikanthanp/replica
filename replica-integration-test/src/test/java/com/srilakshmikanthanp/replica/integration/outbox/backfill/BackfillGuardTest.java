package com.srilakshmikanthanp.replica.integration.outbox.backfill;

import com.srilakshmikanthanp.replica.integration.IntegrationTest;
import com.srilakshmikanthanp.replica.integration.outbox.TestOutboxService;
import com.srilakshmikanthanp.replica.integration.station.TestStationService;
import com.srilakshmikanthanp.replica.integration.user.TestUserConfig;
import com.srilakshmikanthanp.replica.integration.user.TestUserCreatedEvent;
import com.srilakshmikanthanp.replica.integration.user.TestUserRenamedEvent;
import com.srilakshmikanthanp.replica.integration.user.TestUserService;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobCreateResult;
import com.srilakshmikanthanp.replica.outbox.backfill.DestinationNotReadyException;
import com.srilakshmikanthanp.replica.station.Station;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest(TestUserConfig.class)
class BackfillGuardTest {
  @Autowired
  private TestStationService testStationService;

  @Autowired
  private TestUserService testUserService;

  @Autowired
  private TestOutboxService testOutboxService;

  @Autowired
  private TestBackfillService testBackfillService;

  @Test
  void shouldRejectEventWhileDestinationIsBackfilled() {
    Station station = testStationService.create("station");
    testBackfillService.start(station.getId(), List.of(TestUserRenamedEvent.class));

    DestinationNotReadyException exception = assertThrows(DestinationNotReadyException.class, () ->
      testUserService.createAndBroadcast("alice", station.getId())
    );

    assertEquals(station.getId(), exception.getDestinationId());
    assertEquals(0, testUserService.count());
    assertTrue(testOutboxService.unpublished(station.getId()).isEmpty());
  }

  @Test
  void shouldAcceptEventOnceBackfillCompletes() {
    Station station = testStationService.create("station");
    BackfillJobCreateResult backfill = testBackfillService.start(station.getId(), List.of(TestUserCreatedEvent.class));
    testBackfillService.complete(backfill.steps().getFirst());

    testUserService.createAndBroadcast("alice", station.getId());

    assertEquals(1, testUserService.count());
    assertEquals(1, testOutboxService.unpublished(station.getId()).size());
  }
}
