package com.srilakshmikanthanp.replica.outbox.backfill;

import com.srilakshmikanthanp.replica.database.transaction.TransactionExecutor;
import com.srilakshmikanthanp.replica.event.Event;
import com.srilakshmikanthanp.replica.event.EventRegistry;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class BackfillJobService {
	public final record CreateResult(BackfillJob job, List<BackfillJobStep> steps) {}

	private final TransactionExecutor transactionExecutor;
	private final BackfillJobRepository backfillJobRepository;
	private final BackfillJobStepRepository backfillJobStepRepository;
	private final EventRegistry eventRegistry;

	public CreateResult create(long destinationId, BackfillPlan plan) {
		if (plan == null) {
			throw new IllegalArgumentException("plan cannot be null");
		}

		BackfillJob job = new BackfillJob();
		job.setDestinationId(destinationId);

		List<BackfillJobStep> steps = new ArrayList<>();
		int ordinal = 1;

		for (Class<? extends Event> event : plan.orderedEvents()) {
			BackfillJobStep step = new BackfillJobStep();
			step.setOrdinal(ordinal++);
			step.setEventType(eventRegistry.getEventTypeByEventClass(event));
			steps.add(step);
		}

		return transactionExecutor.execute(() -> {
			backfillJobRepository.save(job);
			steps.forEach(step -> step.setJobId(job.getId()));
			backfillJobStepRepository.saveAll(steps);
			return new CreateResult(job, steps);
		});
	}

	public List<BackfillJob> findAll() {
		return backfillJobRepository.findAll();
	}

	public BackfillJob getById(long id) {
		return backfillJobRepository.getById(id);
	}
}
