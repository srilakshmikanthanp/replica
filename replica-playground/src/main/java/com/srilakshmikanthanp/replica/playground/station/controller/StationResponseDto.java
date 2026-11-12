package com.srilakshmikanthanp.replica.playground.station.controller;

public record StationResponseDto(long id, String name, String url, boolean ready, long waiting) {}
