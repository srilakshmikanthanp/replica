package com.srilakshmikanthanp.replica.spring.outbox.backfill;

import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJob;
import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.support.TransactionOperations;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
public class BackfillJobSpringRepository implements BackfillJobRepository {
	private final BackfillJobJpaRepository jpaRepository;
	private final TransactionOperations transactionOperations;

  private static BackfillJobEntity toEntity(BackfillJob job) {
    BackfillJobEntity entity = new BackfillJobEntity();
    entity.setDestinationId(job.getDestinationId());
    return entity;
  }

	private static BackfillJob toModel(BackfillJobEntity entity) {
		BackfillJob job = new BackfillJob();
		job.setId(entity.getId());
		job.setDestinationId(entity.getDestinationId());
		return job;
	}

	@Override
	public BackfillJob getById(long jobId) {
		if (jobId <= 0) {
			throw new IllegalArgumentException("jobId must be greater than 0");
		}

		return jpaRepository.findById(jobId)
			.map(BackfillJobSpringRepository::toModel)
			.orElseThrow(() -> new NoSuchElementException("No BackfillJob found with id: " + jobId));
	}

	@Override
	public List<BackfillJob> findAll() {
		return jpaRepository
			.findAllByOrderByIdAsc()
			.stream()
			.map(BackfillJobSpringRepository::toModel)
			.toList();
	}

	@Override
	public BackfillJob save(BackfillJob job) {
		BackfillJobEntity entity = toEntity(job);
		transactionOperations.executeWithoutResult(status -> jpaRepository.save(entity));
		job.setId(entity.getId());
		return job;
	}
}
