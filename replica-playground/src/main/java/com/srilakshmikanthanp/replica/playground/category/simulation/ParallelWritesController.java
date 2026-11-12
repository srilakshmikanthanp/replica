package com.srilakshmikanthanp.replica.playground.category.simulation;

import com.srilakshmikanthanp.replica.playground.replication.Master;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Master
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class ParallelWritesController {
	private final ParallelWritesService simulation;

	@PostMapping("/{code}/simulations/parallel-writes")
	ParallelWritesResultDto parallelWrites(@PathVariable String code, @RequestBody ParallelWritesRequestDto request) throws InterruptedException {
		if (request.writes() < 1 || request.writes() > 10_000 || request.parallel() < 1 || request.parallel() > 100) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "1 to 10000 writes and 1 to 100 in parallel are required");
		}

		return simulation.writeInParallel(code, request.writes(), request.parallel());
	}
}
