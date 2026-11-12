package com.srilakshmikanthanp.replica.graph;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Node<T> {
	@Getter
	@EqualsAndHashCode.Include
	private final T value;

	private final Set<Node<T>> dependencies = new HashSet<>();

	public void addDependency(Node<T> node) {
		this.dependencies.add(node);
	}

	public List<Node<T>> getDependencies() {
		return new ArrayList<>(dependencies);
	}
}
