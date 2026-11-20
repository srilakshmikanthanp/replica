package com.srilakshmikanthanp.replica.playground.product;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {
  List<Product> findAllByOrderBySkuAsc();

  List<Product> findByCategoryCodeOrderBySkuAsc(String categoryCode);

  @Query("SELECT p FROM Product p JOIN p.tags t WHERE t.name = :name ORDER BY p.sku")
  List<Product> findByTag(@Param("name") String name);

  @Query("SELECT p FROM Product p ORDER BY p.sku")
  List<Product> firstPage(Pageable pageable);

  @Query("SELECT p FROM Product p WHERE p.sku > :after ORDER BY p.sku")
  List<Product> pageAfter(@Param("after") String after, Pageable pageable);
}
