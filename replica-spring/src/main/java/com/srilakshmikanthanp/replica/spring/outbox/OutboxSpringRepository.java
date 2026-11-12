package com.srilakshmikanthanp.replica.spring.outbox;

import com.srilakshmikanthanp.replica.crypto.PayloadCryptography;
import com.srilakshmikanthanp.replica.outbox.Outbox;
import com.srilakshmikanthanp.replica.outbox.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.support.TransactionOperations;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class OutboxSpringRepository implements OutboxRepository {
	private final OutboxJpaRepository jpaRepository;
	private final TransactionOperations transactionOperations;
	private final PayloadCryptography payloadCryptography;

  private OutboxEntity toEntity(Outbox message, long createdAt) {
    OutboxEntity entity = new OutboxEntity();
    entity.setDestinationId(message.getDestinationId());
    entity.setEventType(message.getEventType());
    entity.setPayload(payloadCryptography.encrypt(message.getPayload()));
    entity.setCreatedAt(createdAt);
    entity.setPublishedAt(message.getPublishedAt().orElse(null));
    return entity;
  }

	private Outbox toModel(OutboxEntity entity) {
		Outbox message = new Outbox();
		message.setId(entity.getId());
		message.setDestinationId(entity.getDestinationId());
		message.setEventType(entity.getEventType());
		message.setPayload(payloadCryptography.decrypt(entity.getPayload()));
		message.setCreatedAt(entity.getCreatedAt());
		message.setPublishedAt(entity.getPublishedAt());
		return message;
	}

	@Override
	public List<Outbox> findUnpublished(long destinationId, int batchSize) {
		if (batchSize <= 0) {
			throw new IllegalArgumentException("batchSize must be greater than 0");
		}

		return jpaRepository
			.findUnpublished(destinationId, PageRequest.of(0, batchSize))
			.stream()
			.map(this::toModel)
			.toList();
	}

	@Override
	public List<Outbox> saveAll(List<Outbox> messages) {
		long now = System.currentTimeMillis();
		List<OutboxEntity> entities = messages.stream().map(message -> toEntity(message, now)).toList();

		transactionOperations.executeWithoutResult(status -> jpaRepository.saveAll(entities));

		for (int i = 0; i < messages.size(); i++) {
			messages.get(i).setId(entities.get(i).getId());
			messages.get(i).setCreatedAt(now);
		}

		return messages;
	}

	@Override
	public List<Outbox> markAsPublished(List<Outbox> messages) {
		long publishedAt = System.currentTimeMillis();

		return transactionOperations.execute(status -> {
			List<Outbox> published = new ArrayList<>();

			for (Outbox message : messages) {
				if (jpaRepository.markAsPublished(message.getId(), publishedAt) > 0) published.add(message);
			}

			for (Outbox message : published) {
				message.setPublishedAt(publishedAt);
			}

			return published;
		});
	}
}
