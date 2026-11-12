package com.srilakshmikanthanp.replica.playground.category.simulation;

import com.srilakshmikanthanp.replica.playground.category.Category;
import com.srilakshmikanthanp.replica.playground.category.replication.CategoryReplicationRepository;
import com.srilakshmikanthanp.replica.playground.replication.Master;
import com.srilakshmikanthanp.replica.playground.station.StationNotReadyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Slf4j
@Master
@Service
@RequiredArgsConstructor
public class ParallelWritesService {
	private final CategoryReplicationRepository categories;
  private enum Outcome { OK, REFUSED, FAILED }

  private static int count(List<Outcome> outcomes, Outcome wanted) {
    return (int) outcomes.stream().filter(outcome -> outcome == wanted).count();
  }

  private Outcome write(String code, int index) {
    try {
      Category category = new Category();
      category.setCode(code);
      category.setName("write-" + index);
      categories.save(category);
      return Outcome.OK;
    } catch (StationNotReadyException | PessimisticLockingFailureException e) {
      return Outcome.REFUSED;
    } catch (RuntimeException e) {
      log.warn("Simulated write {} failed", index, e);
      return Outcome.FAILED;
    }
  }

	public ParallelWritesResultDto writeInParallel(String code, int writes, int parallel) throws InterruptedException {
		List<Callable<Outcome>> tasks = IntStream.range(0, writes).<Callable<Outcome>>mapToObj(index -> () -> write(code, index)).toList();
		long started = System.nanoTime();

		try (ExecutorService pool = Executors.newFixedThreadPool(parallel)) {
			List<Outcome> outcomes = pool.invokeAll(tasks).stream().map(future -> {
				try {
					return future.get();
				} catch (Exception e) {
					return Outcome.FAILED;
				}
			}).toList();

			return new ParallelWritesResultDto(
        count(outcomes, Outcome.OK),
        count(outcomes, Outcome.REFUSED),
        count(outcomes, Outcome.FAILED),
        (System.nanoTime() - started) / 1_000_000
      );
		}
	}
}
