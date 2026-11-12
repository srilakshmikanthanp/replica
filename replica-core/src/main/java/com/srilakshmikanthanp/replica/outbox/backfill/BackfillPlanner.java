package com.srilakshmikanthanp.replica.outbox.backfill;

import com.srilakshmikanthanp.replica.event.EventDependsOn;
import com.srilakshmikanthanp.replica.event.Event;
import com.srilakshmikanthanp.replica.graph.Node;
import com.srilakshmikanthanp.replica.graph.TopologicalSorter;

import java.util.*;

public class BackfillPlanner {
	private final Set<Class<? extends Event>> events = new LinkedHashSet<>();

	private Collection<Node<Class<? extends Event>>> createGraph() {
		Map<Class<? extends Event>, Node<Class<? extends Event>>> nodes = new LinkedHashMap<>();

		for (Class<? extends Event> event : events) {
			nodes.put(event, new Node<>(event));
		}

		for (var node : nodes.values()) {
			EventDependsOn dependsOn = node.getValue().getAnnotation(EventDependsOn.class);

			if (dependsOn == null) {
				continue;
			}

			for (Class<? extends Event> dependency : dependsOn.value()) {
				if (nodes.get(dependency) != null) node.addDependency(nodes.get(dependency));
			}
		}

		return nodes.values();
	}

	public BackfillPlanner addEvent(Class<? extends Event> event) {
		events.add(event);
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
