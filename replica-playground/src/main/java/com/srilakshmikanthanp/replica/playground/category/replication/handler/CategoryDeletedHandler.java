package com.srilakshmikanthanp.replica.playground.category.replication.handler;

import com.srilakshmikanthanp.replica.event.EventHandler;
import com.srilakshmikanthanp.replica.playground.category.CategoryRepository;
import com.srilakshmikanthanp.replica.playground.category.replication.event.CategoryDeleted;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryDeletedHandler implements EventHandler<CategoryDeleted> {
	private final CategoryRepository categories;

	@Override
	public void handle(CategoryDeleted event) {
		categories.deleteById(event.code());
	}
}
