package com.srilakshmikanthanp.replica.outbox.backfill;

import com.srilakshmikanthanp.replica.event.EventDependsOn;
import com.srilakshmikanthanp.replica.event.Event;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BackfillPlanTest {
	static class ShouldSortSimpleDependencyGraphA implements Event {}

	@EventDependsOn(ShouldSortSimpleDependencyGraphA.class)
	static class ShouldSortSimpleDependencyGraphB implements Event {}

	@EventDependsOn(ShouldSortSimpleDependencyGraphB.class)
	static class ShouldSortSimpleDependencyGraphC implements Event {}

	@EventDependsOn(ShouldSortSimpleDependencyGraphC.class)
	static class ShouldSortSimpleDependencyGraphD implements Event {}

	@Test
	void shouldSortSimpleDependencyGraph() {
		BackfillPlan planner = BackfillPlan.planner()
			.addEvent(ShouldSortSimpleDependencyGraphA.class)
			.addEvent(ShouldSortSimpleDependencyGraphC.class)
			.addEvent(ShouldSortSimpleDependencyGraphB.class)
			.addEvent(ShouldSortSimpleDependencyGraphD.class)
			.plan();

		List<Class<? extends Event>> ordered = planner.orderedEvents();

		assertTrue(ordered.indexOf(ShouldSortSimpleDependencyGraphA.class) < ordered.indexOf(ShouldSortSimpleDependencyGraphB.class));
		assertTrue(ordered.indexOf(ShouldSortSimpleDependencyGraphB.class) < ordered.indexOf(ShouldSortSimpleDependencyGraphC.class));
		assertTrue(ordered.indexOf(ShouldSortSimpleDependencyGraphC.class) < ordered.indexOf(ShouldSortSimpleDependencyGraphD.class));
	}

	static class ShouldSortComplexDependencyGraphA implements Event {}

	@EventDependsOn(ShouldSortComplexDependencyGraphA.class)
	static class ShouldSortComplexDependencyGraphB implements Event {}

	@EventDependsOn(ShouldSortComplexDependencyGraphA.class)
	static class ShouldSortComplexDependencyGraphC implements Event {}

	@EventDependsOn({ShouldSortComplexDependencyGraphB.class, ShouldSortComplexDependencyGraphC.class})
	static class ShouldSortComplexDependencyGraphD implements Event {}

	@EventDependsOn(ShouldSortComplexDependencyGraphB.class)
	static class ShouldSortComplexDependencyGraphE implements Event {}

	@EventDependsOn({ShouldSortComplexDependencyGraphD.class, ShouldSortComplexDependencyGraphE.class})
	static class ShouldSortComplexDependencyGraphF implements Event {}

	@Test
	void shouldSortComplexDependencyGraph() {
		BackfillPlan planner = BackfillPlan.planner()
			.addEvent(ShouldSortComplexDependencyGraphF.class)
			.addEvent(ShouldSortComplexDependencyGraphC.class)
			.addEvent(ShouldSortComplexDependencyGraphD.class)
			.addEvent(ShouldSortComplexDependencyGraphA.class)
			.addEvent(ShouldSortComplexDependencyGraphE.class)
			.addEvent(ShouldSortComplexDependencyGraphB.class)
			.plan();

		List<Class<? extends Event>> ordered = planner.orderedEvents();

		assertTrue(ordered.indexOf(ShouldSortComplexDependencyGraphA.class) < ordered.indexOf(ShouldSortComplexDependencyGraphB.class));
		assertTrue(ordered.indexOf(ShouldSortComplexDependencyGraphA.class) < ordered.indexOf(ShouldSortComplexDependencyGraphC.class));
		assertTrue(ordered.indexOf(ShouldSortComplexDependencyGraphB.class) < ordered.indexOf(ShouldSortComplexDependencyGraphD.class));
		assertTrue(ordered.indexOf(ShouldSortComplexDependencyGraphC.class) < ordered.indexOf(ShouldSortComplexDependencyGraphD.class));
		assertTrue(ordered.indexOf(ShouldSortComplexDependencyGraphB.class) < ordered.indexOf(ShouldSortComplexDependencyGraphE.class));
		assertTrue(ordered.indexOf(ShouldSortComplexDependencyGraphD.class) < ordered.indexOf(ShouldSortComplexDependencyGraphF.class));
		assertTrue(ordered.indexOf(ShouldSortComplexDependencyGraphE.class) < ordered.indexOf(ShouldSortComplexDependencyGraphF.class));
	}

	@EventDependsOn(ShouldThrowWhenDependencyHasCycleB.class)
	static class ShouldThrowWhenDependencyHasCycleA implements Event {}

	@EventDependsOn(ShouldThrowWhenDependencyHasCycleC.class)
	static class ShouldThrowWhenDependencyHasCycleB implements Event {}

	@EventDependsOn(ShouldThrowWhenDependencyHasCycleA.class)
	static class ShouldThrowWhenDependencyHasCycleC implements Event {}

	@Test
	void shouldThrowWhenDependencyHasCycle() {
		assertThrows(IllegalStateException.class, () ->
			BackfillPlan.planner()
				.addEvent(ShouldThrowWhenDependencyHasCycleA.class)
				.addEvent(ShouldThrowWhenDependencyHasCycleB.class)
				.addEvent(ShouldThrowWhenDependencyHasCycleC.class)
				.plan()
		);
	}
}
