package com.srilakshmikanthanp.replica.database.transaction;

import java.util.function.Supplier;

public interface TransactionExecutor {
	/**
	 * Runs the action in the current transaction, or in a new one if none is open. The transaction commits
	 * if this call started it and the action returns, and rolls back if the action throws.
	 */
	<T> T execute(Supplier<T> action);

  default void run(Runnable action) {
    execute(() -> {
      action.run();
      return null;
    });
  }

	/**
	 * Runs the action in a new transaction.
	 *
	 * @throws IllegalStateException if a transaction is already open on this thread
	 */
	<T> T executeInNewTransaction(Supplier<T> action);

	default void runInNewTransaction(Runnable action) {
		executeInNewTransaction(() -> {
			action.run();
			return null;
		});
	}
}
