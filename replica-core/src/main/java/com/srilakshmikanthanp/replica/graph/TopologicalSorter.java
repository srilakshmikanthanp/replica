package com.srilakshmikanthanp.replica.graph;

import java.util.*;

public class TopologicalSorter {
	public static <T> List<Node<T>> sort(Collection<Node<T>> nodes) {
		Map<Node<T>, List<Node<T>>> graph = new LinkedHashMap<>();
		Map<Node<T>, Integer> inDegree = new LinkedHashMap<>();

		for (Node<T> node : nodes) {
			graph.put(node, new ArrayList<>());
			inDegree.put(node, 0);
		}

		for (Node<T> node : nodes) {
			for (Node<T> dependency : node.getDependencies()) {
				// Reverse the edge direction for topological sorting
				graph.get(dependency).add(node);
				inDegree.merge(node, 1, Integer::sum);
			}
		}

		Queue<Node<T>> queue = new LinkedList<>();

		for (Map.Entry<Node<T>, Integer> entry : inDegree.entrySet()) {
			if (entry.getValue() == 0) {
				queue.add(entry.getKey());
			}
		}

		List<Node<T>> sorted = new ArrayList<>();

		while (!queue.isEmpty()) {
			Node<T> node = queue.poll();
			sorted.add(node);

			for (Node<T> neighbor : graph.get(node)) {
				inDegree.merge(neighbor, -1, Integer::sum);
				if (inDegree.get(neighbor) == 0) {
					queue.add(neighbor);
				}
			}
		}

		if (sorted.size() != nodes.size()) {
			// TODO: May need to show cycles instead of just throwing an exception?
			throw new IllegalStateException("Graph has at least one cycle");
		}

		return sorted;
	}
}
