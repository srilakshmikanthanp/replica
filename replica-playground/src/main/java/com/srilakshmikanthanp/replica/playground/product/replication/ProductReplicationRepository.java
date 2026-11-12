package com.srilakshmikanthanp.replica.playground.product.replication;

import com.srilakshmikanthanp.replica.playground.product.Product;
import com.srilakshmikanthanp.replica.playground.product.ProductRepository;
import com.srilakshmikanthanp.replica.playground.product.replication.event.ProductDeleted;
import com.srilakshmikanthanp.replica.playground.product.replication.event.ProductTagAdded;
import com.srilakshmikanthanp.replica.playground.product.replication.event.ProductTagRemoved;
import com.srilakshmikanthanp.replica.playground.product.replication.event.ProductUpserted;
import com.srilakshmikanthanp.replica.playground.replication.Master;
import com.srilakshmikanthanp.replica.playground.replication.outbox.ReplicationWriter;
import com.srilakshmikanthanp.replica.playground.tag.Tag;
import com.srilakshmikanthanp.replica.playground.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.NoSuchElementException;

@Master
@Repository
@RequiredArgsConstructor
public class ProductReplicationRepository {
  private final ProductRepository productRepository;
  private final TagRepository tagRepository;
  private final ReplicationWriter replicationWriter;

  public Product save(Product product) {
    return replicationWriter.write(
      () -> {
        Product toSave = new Product();
        toSave.setSku(product.getSku());
        toSave.setCategory(product.getCategory());
        toSave.setName(product.getName());
        toSave.setPrice(product.getPrice());
        productRepository.findById(product.getSku()).ifPresent(existing -> toSave.setTags(new HashSet<>(existing.getTags())));
        return productRepository.saveAndFlush(toSave);
      },
      saved -> new ProductUpserted(
        saved.getSku(),
        saved.getCategory().getCode(),
        saved.getName(),
        saved.getPrice()
      )
    );
  }

  public void delete(String sku) {
    replicationWriter.write(
      () -> {
        productRepository.deleteById(sku);
        productRepository.flush();
        return sku;
      },
      ProductDeleted::new
    );
  }

  public void addTag(String sku, String tagName) {
    replicationWriter.write(
      () -> {
        Product product = productRepository.findById(sku).orElseThrow(() -> new NoSuchElementException("No product " + sku));
        Tag tag = tagRepository.findById(tagName).orElseThrow(() -> new NoSuchElementException("No tag " + tagName));
        product.getTags().add(tag);
        return productRepository.saveAndFlush(product);
      },
      saved -> new ProductTagAdded(sku, tagName)
    );
  }

  public void removeTag(String sku, String tagName) {
    replicationWriter.write(
      () -> {
        Product product = productRepository.findById(sku).orElseThrow(() -> new NoSuchElementException("No product " + sku));
        product.getTags().removeIf(tag -> tag.getName().equals(tagName));
        return productRepository.saveAndFlush(product);
      },
      saved -> new ProductTagRemoved(sku, tagName)
    );
  }
}
