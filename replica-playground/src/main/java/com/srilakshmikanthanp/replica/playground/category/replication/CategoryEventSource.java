package com.srilakshmikanthanp.replica.playground.category.replication;

import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJob;
import com.srilakshmikanthanp.replica.outbox.backfill.EventBatch;
import com.srilakshmikanthanp.replica.outbox.backfill.EventSource;
import com.srilakshmikanthanp.replica.playground.category.Category;
import com.srilakshmikanthanp.replica.playground.category.CategoryRepository;
import com.srilakshmikanthanp.replica.playground.category.replication.event.CategoryUpserted;
import com.srilakshmikanthanp.replica.playground.replication.Master;
import com.srilakshmikanthanp.replica.playground.replication.backfill.KeyCursor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Master
@Component
@RequiredArgsConstructor
public class CategoryEventSource implements EventSource<CategoryUpserted, KeyCursor> {
	private final CategoryRepository categories;

	@Override
	public EventBatch<CategoryUpserted, KeyCursor> getNextBatch(BackfillJob job, KeyCursor cursor, int limit) {
		String after = cursor == null ? null : cursor.last();
		List<Category> rows = after == null ? categories.firstPage(PageRequest.of(0, limit)) : categories.pageAfter(after, PageRequest.of(0, limit));
		List<CategoryUpserted> events = rows.stream().map(row -> new CategoryUpserted(row.getCode(), row.getName())).toList();
		String last = rows.isEmpty() ? after : rows.getLast().getCode();
		return new EventBatch<>(events, new KeyCursor(last), rows.size() == limit);
	}

	@Override
	public Class<KeyCursor> getCursorClass() {
		return KeyCursor.class;
	}
}
