package com.srilakshmikanthanp.replica.inbox;

import com.srilakshmikanthanp.replica.database.transaction.TransactionExecutor;
import com.srilakshmikanthanp.replica.event.*;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Applies a source's inbox messages in MESSAGE_ID order. Safe to call many times at once, from one
 * server or several: only one call applies each message.
 * <p>
 * Each message is applied in its own transaction, whose first statement claims it:
 * {@code UPDATE ... SET PROCESSED_AT = ? WHERE ID = ? AND PROCESSED_AT IS NULL}. The update locks the
 * row until commit, so another call on the same message waits on it, then updates 0 rows and skips it.
 * <p>
 * Order holds because every call reads the lowest unprocessed messages and claims them one by one.
 * None can get past a message another call is still applying. If the handler throws, the claim rolls
 * back with it and the message is retried on the next call.
 * <p>
 * Call it outside a transaction: {@code runInNewTransaction} throws if one is already open.
 */
@RequiredArgsConstructor
public class PollingInboxProcessor implements Callable<Void> {
	private final TransactionExecutor transactionExecutor;
	private final InboxRepository inboxRepository;
	private final EventRegistry eventRegistry;
	private final EventDeserializer eventDeserializer;
	private final EventHandlerRegistry eventHandlerRegistry;
	private final int batchSize;
	private final long sourceId;

	private <E extends Event> void dispatch(Class<E> eventClass, String payload) {
		EventHandler<E> eventHandler = eventHandlerRegistry.getEventHandlerByEventClass(eventClass);
		E event = eventDeserializer.deserialize(payload, eventClass);
		eventHandler.handle(event);
	}

	private void process(Inbox message) {
		transactionExecutor.runInNewTransaction(() -> {
			if (!inboxRepository.markAsProcessed(message)) return;
			Class<? extends Event> eventClass = eventRegistry.getEventClassByEventType(message.getEventType());
			this.dispatch(eventClass, message.getPayload());
		});
	}

	@Override
	public Void call() {
		while (true) {
			List<Inbox> messages = inboxRepository.findUnprocessed(sourceId, batchSize);
			if (messages.isEmpty()) break;
			messages.forEach(this::process);
		}
		return null;
	}
}
