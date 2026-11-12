package com.srilakshmikanthanp.replica.playground.product.replication;

import com.srilakshmikanthanp.replica.outbox.backfill.BackfillJob;
import com.srilakshmikanthanp.replica.outbox.backfill.EventBatch;
import com.srilakshmikanthanp.replica.outbox.backfill.EventSource;
import com.srilakshmikanthanp.replica.playground.product.Product;
import com.srilakshmikanthanp.replica.playground.product.ProductRepository;
import com.srilakshmikanthanp.replica.playground.product.replication.event.ProductTagAdded;
import com.srilakshmikanthanp.replica.playground.replication.Master;
import com.srilakshmikanthanp.replica.playground.replication.backfill.KeyCursor;
import com.srilakshmikanthanp.replica.playground.tag.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Master
@Component
@RequiredArgsConstructor
public class ProductTagEventSource implements EventSource<ProductTagAdded, KeyCursor> {
	private final ProductRepository products;

	@Override
	public EventBatch<ProductTagAdded, KeyCursor> getNextBatch(BackfillJob job, KeyCursor cursor, int limit) {
		String after = cursor == null ? null : cursor.last();
		List<Product> rows = after == null ? products.firstPage(PageRequest.of(0, limit)) : products.pageAfter(after, PageRequest.of(0, limit));
		List<ProductTagAdded> events = rows.stream().flatMap(row -> row.getTags().stream().map(Tag::getName).sorted().map(tagName -> new ProductTagAdded(row.getSku(), tagName))).toList();
		String last = rows.isEmpty() ? after : rows.getLast().getSku();
		return new EventBatch<>(events, new KeyCursor(last), rows.size() == limit);
	}

	@Override
	public Class<KeyCursor> getCursorClass() {
		return KeyCursor.class;
	}
}
