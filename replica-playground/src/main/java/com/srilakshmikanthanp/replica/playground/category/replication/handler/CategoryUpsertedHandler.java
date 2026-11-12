package com.srilakshmikanthanp.replica.playground.category.replication.handler;

import com.srilakshmikanthanp.replica.event.EventHandler;
import com.srilakshmikanthanp.replica.playground.category.Category;
import com.srilakshmikanthanp.replica.playground.category.CategoryRepository;
import com.srilakshmikanthanp.replica.playground.category.replication.event.CategoryUpserted;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryUpsertedHandler implements EventHandler<CategoryUpserted> {
	private final CategoryRepository categories;

	@Override
	public void handle(CategoryUpserted event) {
		Category category = categories.findById(event.code()).orElseGet(Category::new);
		category.setCode(event.code());
		category.setName(event.name());
		categories.save(category);
	}
}
