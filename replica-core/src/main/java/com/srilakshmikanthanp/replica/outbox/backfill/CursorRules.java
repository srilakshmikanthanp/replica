package com.srilakshmikanthanp.replica.outbox.backfill;

import com.srilakshmikanthanp.replica.event.Event;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class CursorRules {
  private final Map<Class<? extends Event>, Map<Class<? extends Event>, CursorEventPredicate<Cursor, Event>>> affected = new LinkedHashMap<>();

  boolean hasBackfill(Class<? extends Event> backfilledClass) {
    return affected.containsKey(backfilledClass);
  }

  Map<Class<? extends Event>, CursorEventPredicate<Cursor, Event>> addBackfill(Class<? extends Event> backfilledClass) {
    Map<Class<? extends Event>, CursorEventPredicate<Cursor, Event>> predicates = new LinkedHashMap<>();
    affected.put(backfilledClass, predicates);
    return predicates;
  }

  List<Class<? extends Event>> findBackfillEventsAffectedBy(Class<? extends Event> eventClass) {
    return affected.entrySet().stream().filter(entry -> entry.getValue().containsKey(eventClass)).<Class<? extends Event>>map(Map.Entry::getKey).toList();
  }

  Optional<CursorEventPredicate<Cursor, Event>> findPredicate(Class<? extends Event> backfilledClass, Class<? extends Event> eventClass) {
    return Optional.ofNullable(affected.getOrDefault(backfilledClass, Map.of()).get(eventClass));
  }
}
