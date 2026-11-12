package com.srilakshmikanthanp.replica.spring.database.transaction;

import com.srilakshmikanthanp.replica.database.transaction.TransactionExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

/**
 * {@link TransactionExecutor} on Spring's transaction management. Pass the {@link TransactionTemplate} to
 * set a timeout or isolation level; its propagation should stay {@code REQUIRED}.
 */
public class SpringTransactionExecutor implements TransactionExecutor {
	private final TransactionTemplate transactionTemplate;

  public SpringTransactionExecutor(PlatformTransactionManager transactionManager) {
    this(new TransactionTemplate(transactionManager));
  }

	public SpringTransactionExecutor(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	@Override
	public <T> T execute(Supplier<T> action) {
		return transactionTemplate.execute(status -> action.get());
	}

	@Override
	public <T> T executeInNewTransaction(Supplier<T> action) {
		if (TransactionSynchronizationManager.isActualTransactionActive()) {
			throw new IllegalStateException("A transaction is already open on this thread");
		}

		return execute(action);
	}
}
