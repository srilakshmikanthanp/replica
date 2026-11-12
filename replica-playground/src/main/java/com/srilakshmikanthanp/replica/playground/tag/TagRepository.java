package com.srilakshmikanthanp.replica.playground.tag;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, String> {
	List<Tag> findAllByOrderByNameAsc();

	/**
	 * The first rows by name, locked for sharing until the transaction ends.
	 */
	@Lock(LockModeType.PESSIMISTIC_READ)
	@Query("SELECT t FROM Tag t ORDER BY t.name")
	List<Tag> firstPage(Pageable pageable);

	/**
	 * The rows after the given name, locked for sharing until the transaction ends.
	 */
	@Lock(LockModeType.PESSIMISTIC_READ)
	@Query("SELECT t FROM Tag t WHERE t.name > :after ORDER BY t.name")
	List<Tag> pageAfter(@Param("after") String after, Pageable pageable);
}
