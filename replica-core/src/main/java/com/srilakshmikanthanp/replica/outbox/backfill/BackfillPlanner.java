package com.srilakshmikanthanp.replica.outbox.backfill;

import com.srilakshmikanthanp.replica.event.Event;
import com.srilakshmikanthanp.replica.graph.Node;
import com.srilakshmikanthanp.replica.graph.TopologicalSorter;

import java.util.*;

public class BackfillPlanner {
  private final Map<Class<? extends Event>, List<Class<? extends Event>>> events = new LinkedHashMap<>();

  private Collection<Node<Class<? extends Event>>> createGraph() {
    Map<Class<? extends Event>, Node<Class<? extends Event>>> nodes = new LinkedHashMap<>();

    for (Class<? extends Event> event : events.keySet()) {
      nodes.put(event, new Node<>(event));
    }

    for (var node : nodes.values()) {
      for (Class<? extends Event> dependency : events.get(node.getValue())) {
        if (nodes.get(dependency) != null) node.addDependency(nodes.get(dependency));
      }
    }

    return nodes.values();
  }

  @SafeVarargs
  public final BackfillPlanner addEvent(Class<? extends Event> event, Class<? extends Event>... dependsOn) {
    events.put(event, List.of(dependsOn));
    return this;
  }

  public BackfillPlan plan() {
    List<Class<? extends Event>> ordered = new ArrayList<>();

    for (var node : TopologicalSorter.sort(createGraph())) {
      ordered.add(node.getValue());
    }

    return new BackfillPlan(ordered);
  }
}
