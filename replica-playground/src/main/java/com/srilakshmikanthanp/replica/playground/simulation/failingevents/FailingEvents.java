package com.srilakshmikanthanp.replica.playground.simulation.failingevents;

import com.srilakshmikanthanp.replica.playground.replication.Slave;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slave
@Component
public class FailingEvents {
  private final Set<String> failingEventTypes = ConcurrentHashMap.newKeySet();

  public Set<String> getFailingEventTypes() {
    return Set.copyOf(failingEventTypes);
  }

  public void setFailingEventTypes(Set<String> eventTypes) {
    failingEventTypes.clear();
    failingEventTypes.addAll(eventTypes);
  }

  public boolean isFailing(String eventType) {
    return failingEventTypes.contains(eventType);
  }
}
