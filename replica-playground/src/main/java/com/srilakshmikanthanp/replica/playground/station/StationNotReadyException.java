package com.srilakshmikanthanp.replica.playground.station;

public class StationNotReadyException extends RuntimeException {
	public StationNotReadyException() {
		super("Changes are paused while a station is being added. Try again shortly.");
	}
}
