package com.srilakshmikanthanp.replica.outbox.backfill;

import com.srilakshmikanthanp.replica.event.Event;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BackfillPlanTest {
  record A() implements Event {
  }

  record B() implements Event {
  }

  record C() implements Event {
  }

  record D() implements Event {
  }

  record E() implements Event {
  }

  record F() implements Event {
  }

  @Test
  void shouldSortComplexDependencyGraph() {
    List<Class<? extends Event>> ordered = BackfillPlan.planner()
      .addEvent(F.class, D.class, E.class)
      .addEvent(C.class, A.class)
      .addEvent(D.class, B.class, C.class)
      .addEvent(A.class)
      .addEvent(E.class, B.class)
      .addEvent(B.class, A.class)
      .plan()
      .orderedEvents();

    assertTrue(ordered.indexOf(A.class) < ordered.indexOf(B.class));
    assertTrue(ordered.indexOf(A.class) < ordered.indexOf(C.class));
    assertTrue(ordered.indexOf(B.class) < ordered.indexOf(D.class));
    assertTrue(ordered.indexOf(C.class) < ordered.indexOf(D.class));
    assertTrue(ordered.indexOf(B.class) < ordered.indexOf(E.class));
    assertTrue(ordered.indexOf(D.class) < ordered.indexOf(F.class));
    assertTrue(ordered.indexOf(E.class) < ordered.indexOf(F.class));
  }

  @Test
  void shouldThrowWhenDependencyHasCycle() {
    assertThrows(IllegalStateException.class, () ->
      BackfillPlan.planner()
        .addEvent(A.class, B.class)
        .addEvent(B.class, C.class)
        .addEvent(C.class, A.class)
        .plan()
    );
  }
}
