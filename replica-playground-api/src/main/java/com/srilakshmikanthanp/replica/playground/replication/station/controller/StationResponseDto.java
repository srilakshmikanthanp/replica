package com.srilakshmikanthanp.replica.playground.replication.station.controller;

public record StationResponseDto(long id, String name, String url, boolean ready, long waiting) {
}
