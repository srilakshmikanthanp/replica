package com.srilakshmikanthanp.replica.playground.tag.replication;

import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJob;
import com.srilakshmikanthanp.replica.outbox.backfill.EventBatch;
import com.srilakshmikanthanp.replica.outbox.backfill.EventSource;
import com.srilakshmikanthanp.replica.playground.replication.Master;
import com.srilakshmikanthanp.replica.playground.replication.backfill.KeyCursor;
import com.srilakshmikanthanp.replica.playground.tag.Tag;
import com.srilakshmikanthanp.replica.playground.tag.TagRepository;
import com.srilakshmikanthanp.replica.playground.tag.replication.event.TagUpserted;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Master
@Component
@RequiredArgsConstructor
public class TagEventSource implements EventSource<TagUpserted, KeyCursor> {
	private final TagRepository tags;

	@Override
	public EventBatch<TagUpserted, KeyCursor> getNextBatch(BackfillJob job, KeyCursor cursor, int limit) {
		String after = cursor == null ? null : cursor.last();
		List<Tag> rows = after == null ? tags.firstPage(PageRequest.of(0, limit)) : tags.pageAfter(after, PageRequest.of(0, limit));
		List<TagUpserted> events = rows.stream().map(row -> new TagUpserted(row.getName())).toList();
		String last = rows.isEmpty() ? after : rows.getLast().getName();
		return new EventBatch<>(events, new KeyCursor(last), rows.size() == limit);
	}

	@Override
	public Class<KeyCursor> getCursorClass() {
		return KeyCursor.class;
	}
}
