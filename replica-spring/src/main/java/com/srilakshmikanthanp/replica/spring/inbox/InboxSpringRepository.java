package com.srilakshmikanthanp.replica.spring.inbox;

import com.srilakshmikanthanp.replica.crypto.PayloadCryptography;
import com.srilakshmikanthanp.replica.inbox.Inbox;
import com.srilakshmikanthanp.replica.inbox.InboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.support.TransactionOperations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class InboxSpringRepository implements InboxRepository {
	private final InboxJpaRepository jpaRepository;
	private final TransactionOperations transactionOperations;
	private final PayloadCryptography payloadCryptography;

  private InboxEntity toEntity(Inbox message) {
    InboxEntity entity = new InboxEntity();
    entity.setSourceId(message.getSourceId());
    entity.setMessageId(message.getMessageId());
    entity.setEventType(message.getEventType());
    entity.setPayload(payloadCryptography.encrypt(message.getPayload()));
    entity.setCreatedAt(message.getCreatedAt());
    entity.setReceivedAt(message.getReceivedAt());
    entity.setProcessedAt(message.getProcessedAt().orElse(null));
    return entity;
  }

	private Inbox toModel(InboxEntity entity) {
		Inbox message = new Inbox();
		message.setId(entity.getId());
		message.setSourceId(entity.getSourceId());
		message.setMessageId(entity.getMessageId());
		message.setEventType(entity.getEventType());
		message.setPayload(payloadCryptography.decrypt(entity.getPayload()));
		message.setCreatedAt(entity.getCreatedAt());
		message.setReceivedAt(entity.getReceivedAt());
		message.setProcessedAt(entity.getProcessedAt());
		return message;
	}

	@Override
	public List<Inbox> findUnprocessed(long sourceId, int limit) {
		if (limit <= 0) {
			throw new IllegalArgumentException("limit must be greater than 0");
		}

		return jpaRepository
			.findUnprocessed(sourceId, PageRequest.of(0, limit))
			.stream()
			.map(this::toModel)
			.toList();
	}

	@Override
	public List<Inbox> saveAll(List<Inbox> messages) {
		return transactionOperations.execute(status -> {
			List<Inbox> inserted = new ArrayList<>();

			messages.stream().collect(Collectors.groupingBy(Inbox::getSourceId)).forEach((sourceId, group) -> {
				Set<Long> known = new HashSet<>(jpaRepository.findMessageIds(sourceId, group.stream().map(Inbox::getMessageId).toList()));
				group.stream().filter(message -> known.add(message.getMessageId())).forEach(inserted::add);
			});

			List<InboxEntity> entities = inserted.stream().map(this::toEntity).toList();
			jpaRepository.saveAll(entities);

			for (int i = 0; i < inserted.size(); i++) {
				inserted.get(i).setId(entities.get(i).getId());
			}

			return inserted;
		});
	}

	@Override
	public boolean markAsProcessed(Inbox message) {
		long processedAt = System.currentTimeMillis();

		Integer updated = transactionOperations.execute(status -> jpaRepository.markAsProcessed(message.getId(), processedAt));

		if (updated == null || updated == 0) {
			return false;
		}

		message.setProcessedAt(processedAt);
		return true;
	}
}
