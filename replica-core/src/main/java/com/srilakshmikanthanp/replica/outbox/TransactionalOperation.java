package com.srilakshmikanthanp.replica.outbox;

import com.srilakshmikanthanp.replica.event.Event;

@FunctionalInterface
public interface TransactionalOperation<R, E extends Event> {
	TransactionalOperationResult<R, E> execute();
}
