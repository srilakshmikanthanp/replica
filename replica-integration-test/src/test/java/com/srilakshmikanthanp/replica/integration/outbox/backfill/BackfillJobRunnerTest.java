package com.srilakshmikanthanp.replica.integration.outbox.backfill;

import com.srilakshmikanthanp.replica.inbox.InboxProcessor;
import com.srilakshmikanthanp.replica.inbox.InboxRepository;
import com.srilakshmikanthanp.replica.integration.IntegrationTest;
import com.srilakshmikanthanp.replica.integration.inbox.TestInboxProcessorFactory;
import com.srilakshmikanthanp.replica.integration.outbox.InboxOutboxTransporter;
import com.srilakshmikanthanp.replica.integration.outbox.TestOutboxPublisherFactory;
import com.srilakshmikanthanp.replica.integration.station.TestStationService;
import com.srilakshmikanthanp.replica.integration.user.TestReplicaUser;
import com.srilakshmikanthanp.replica.integration.user.TestReplicaUserJpaRepository;
import com.srilakshmikanthanp.replica.integration.user.TestUser;
import com.srilakshmikanthanp.replica.integration.user.TestUserConfig;
import com.srilakshmikanthanp.replica.integration.user.TestUserCreatedEvent;
import com.srilakshmikanthanp.replica.integration.user.TestUserCreatedHandler;
import com.srilakshmikanthanp.replica.integration.user.TestUserJpaRepository;
import com.srilakshmikanthanp.replica.integration.user.TestUserRenamedHandler;
import com.srilakshmikanthanp.replica.integration.user.TestUserService;
import com.srilakshmikanthanp.replica.outbox.OutboxPublisher;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJob;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobRunner;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobService;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillPlan;
import com.srilakshmikanthanp.replica.station.Station;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@IntegrationTest(TestUserConfig.class)
class BackfillJobRunnerTest {
  @Autowired
  private TestStationService testStationService;

  @Autowired
  private TestUserService testUserService;

  @Autowired
  private TestUserJpaRepository testUserJpaRepository;

  @Autowired
  private TestReplicaUserJpaRepository testReplicaUserJpaRepository;

  @Autowired
  private TestUserCreatedHandler testUserCreatedHandler;

  @Autowired
  private TestUserRenamedHandler testUserRenamedHandler;

  @Autowired
  private InboxRepository inboxRepository;

  @Autowired
  private TestOutboxPublisherFactory publishers;

  @Autowired
  private TestInboxProcessorFactory processors;

  @Autowired
  private BackfillJobService backfillJobService;

  @Autowired
  private BackfillJobRunner backfillJobRunner;

  @Test
  void shouldReplicateSourceWhenWritesRunBetweenBatches() {
    Station source = testStationService.create("source");
    Station destination = testStationService.create("destination");
    TestUser alice = testUserService.create("alice");
    TestUser bob = testUserService.create("bob");
    TestUser carol = testUserService.create("carol");
    TestUser dave = testUserService.create("dave");
    BackfillPlan plan = BackfillPlan.planner().addEvent(TestUserCreatedEvent.class).plan();
    BackfillJob job = backfillJobService.create(destination.getId(), plan).job();
    OutboxPublisher publisher = publishers.polling(destination.getId(), new InboxOutboxTransporter(inboxRepository, source.getId()));
    InboxProcessor processor = processors.polling(source.getId(), testUserCreatedHandler, testUserRenamedHandler);

    backfillJobRunner.runNextBatch(job);
    testUserService.renameAndBroadcast(alice.getId(), "alicia", destination.getId());
    testUserService.renameAndBroadcast(carol.getId(), "caroline", destination.getId());
    testUserService.createAndBroadcast("erin", destination.getId());
    publisher.publish();
    processor.process();
    backfillJobRunner.runNextBatch(job);
    testUserService.renameAndBroadcast(bob.getId(), "bobby", destination.getId());
    testUserService.renameAndBroadcast(dave.getId(), "david", destination.getId());
    publisher.publish();
    processor.process();
    backfillJobRunner.run(job);
    publisher.publish();
    processor.process();

    Map<Long, String> sourceUsers = testUserJpaRepository.findAll().stream().collect(Collectors.toMap(TestUser::getId, TestUser::getName));
    Map<Long, String> destinationUsers = testReplicaUserJpaRepository.findAll().stream().collect(Collectors.toMap(TestReplicaUser::getId, TestReplicaUser::getName));

    assertEquals(sourceUsers, destinationUsers);
  }
}
