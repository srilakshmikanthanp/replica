package com.srilakshmikanthanp.replica.outbox.backfill;

import com.srilakshmikanthanp.replica.event.Event;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BackfillPlanTest {
  static class ShouldSortComplexDependencyGraphA implements Event {
  }

  @BackfillDependsOn(ShouldSortComplexDependencyGraphA.class)
  static class ShouldSortComplexDependencyGraphB implements Event {
  }

  @BackfillDependsOn(ShouldSortComplexDependencyGraphA.class)
  static class ShouldSortComplexDependencyGraphC implements Event {
  }

  @BackfillDependsOn({ShouldSortComplexDependencyGraphB.class, ShouldSortComplexDependencyGraphC.class})
  static class ShouldSortComplexDependencyGraphD implements Event {
  }

  @BackfillDependsOn(ShouldSortComplexDependencyGraphB.class)
  static class ShouldSortComplexDependencyGraphE implements Event {
  }

  @BackfillDependsOn({ShouldSortComplexDependencyGraphD.class, ShouldSortComplexDependencyGraphE.class})
  static class ShouldSortComplexDependencyGraphF implements Event {
  }

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

  @BackfillDependsOn(ShouldThrowWhenDependencyHasCycleB.class)
  static class ShouldThrowWhenDependencyHasCycleA implements Event {
  }

  @BackfillDependsOn(ShouldThrowWhenDependencyHasCycleC.class)
  static class ShouldThrowWhenDependencyHasCycleB implements Event {
  }

  @BackfillDependsOn(ShouldThrowWhenDependencyHasCycleA.class)
  static class ShouldThrowWhenDependencyHasCycleC implements Event {
  }

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
