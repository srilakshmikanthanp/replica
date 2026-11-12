package com.srilakshmikanthanp.replica.playground.category;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, String> {
	List<Category> findAllByOrderByCodeAsc();

	@Lock(LockModeType.PESSIMISTIC_READ)
	@Query("SELECT c FROM Category c ORDER BY c.code")
	List<Category> firstPage(Pageable pageable);

	@Lock(LockModeType.PESSIMISTIC_READ)
	@Query("SELECT c FROM Category c WHERE c.code > :after ORDER BY c.code")
	List<Category> pageAfter(@Param("after") String after, Pageable pageable);
}
