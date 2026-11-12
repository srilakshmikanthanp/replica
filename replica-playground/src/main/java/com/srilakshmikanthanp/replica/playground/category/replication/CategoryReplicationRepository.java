package com.srilakshmikanthanp.replica.playground.category.replication;

import com.srilakshmikanthanp.replica.playground.category.Category;
import com.srilakshmikanthanp.replica.playground.category.CategoryRepository;
import com.srilakshmikanthanp.replica.playground.category.replication.event.CategoryDeleted;
import com.srilakshmikanthanp.replica.playground.category.replication.event.CategoryUpserted;
import com.srilakshmikanthanp.replica.playground.replication.Master;
import com.srilakshmikanthanp.replica.playground.replication.outbox.ReplicationWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Master
@Repository
@RequiredArgsConstructor
public class CategoryReplicationRepository {
  private final CategoryRepository categories;
  private final ReplicationWriter replication;

  public Category save(Category category) {
    return replication.write(
      () -> categories.saveAndFlush(category),
      saved -> new CategoryUpserted(saved.getCode(), saved.getName())
    );
  }

  public void delete(String code) {
    replication.write(
      () -> {
        categories.deleteById(code);
        categories.flush();
        return code;
      },
      CategoryDeleted::new
    );
  }
}
