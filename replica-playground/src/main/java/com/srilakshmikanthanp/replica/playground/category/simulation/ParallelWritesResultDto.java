package com.srilakshmikanthanp.replica.playground.category.simulation;

public record ParallelWritesResultDto(int ok, int refused, int failed, long millis) {}
