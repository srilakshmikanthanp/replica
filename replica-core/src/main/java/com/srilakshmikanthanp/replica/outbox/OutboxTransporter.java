package com.srilakshmikanthanp.replica.outbox;

import java.util.List;

public interface OutboxTransporter {
	void send(List<Outbox> messages) throws OutboxTransportException;
}
