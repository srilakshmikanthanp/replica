package com.srilakshmikanthanp.replica.playground.replication.inbox;

import com.srilakshmikanthanp.replica.database.transaction.TransactionExecutor;
import com.srilakshmikanthanp.replica.event.EventDeserializer;
import com.srilakshmikanthanp.replica.event.EventHandlerRegistry;
import com.srilakshmikanthanp.replica.event.EventRegistry;
import com.srilakshmikanthanp.replica.inbox.InboxProcessor;
import com.srilakshmikanthanp.replica.inbox.InboxRepository;
import com.srilakshmikanthanp.replica.inbox.PollingInboxProcessor;
import com.srilakshmikanthanp.replica.playground.replication.ReplicationProperties;
import com.srilakshmikanthanp.replica.playground.replication.Slave;
import com.srilakshmikanthanp.replica.playground.simulation.failingevents.FailingEventHandlers;
import com.srilakshmikanthanp.replica.station.Station;
import com.srilakshmikanthanp.replica.station.StationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Slave
@Component
@RequiredArgsConstructor
public class InboxProcessorRunner {
  private final TransactionExecutor transactionExecutor;
  private final InboxRepository inboxRepository;
  private final EventRegistry eventRegistry;
  private final EventDeserializer eventDeserializer;
  private final EventHandlerRegistry eventHandlerRegistry;
  private final StationRepository stationRepository;
  private final ReplicationProperties properties;
  private final FailingEventHandlers failingEventHandlers;

  private final ExecutorService pool = Executors.newCachedThreadPool(runnable -> {
    Thread thread = new Thread(runnable, "inbox-processor");
    thread.setDaemon(true);
    return thread;
  });

  private final Set<Long> running = ConcurrentHashMap.newKeySet();

  @Scheduled(fixedDelayString = "${playground.interval-ms:1000}")
  public void process() {
    for (Station station : stationRepository.findAll()) {
      if (running.add(station.getId())) pool.submit(() -> process(station));
    }
  }

  private InboxProcessor processor(Station station) {
    EventHandlerRegistry handlers = failingEventHandlers.wrap(eventHandlerRegistry);
    int batchSize = properties.getBatchSize();

    return new PollingInboxProcessor(transactionExecutor, inboxRepository, eventRegistry, eventDeserializer, handlers, batchSize, station.getId());
  }

  private void process(Station station) {
    try {
      processor(station).process();
    } catch (Exception e) {
      log.warn("Applying events from station {} stopped, will try again: {}", station.getName(), e.getMessage());
    } finally {
      running.remove(station.getId());
    }
  }
}
