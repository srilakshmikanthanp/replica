package com.srilakshmikanthanp.replica.outbox.backfill;

import com.srilakshmikanthanp.replica.event.Event;

import java.util.*;

public record BackfillPlan(List<Class<? extends Event>> orderedEvents) {
	public static BackfillPlanner planner() {
		return new BackfillPlanner();
	}
}
