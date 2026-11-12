package com.srilakshmikanthanp.replica.outbox.backfill;

import com.srilakshmikanthanp.replica.event.Event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BackfillDependsOn {
  Class<? extends Event>[] value();
}
