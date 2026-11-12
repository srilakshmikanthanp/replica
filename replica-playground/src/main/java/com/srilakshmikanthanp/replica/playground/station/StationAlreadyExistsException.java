package com.srilakshmikanthanp.replica.playground.station;

public class StationAlreadyExistsException extends RuntimeException {
	public StationAlreadyExistsException(String name) {
		super("Station " + name + " already exists");
	}
}
