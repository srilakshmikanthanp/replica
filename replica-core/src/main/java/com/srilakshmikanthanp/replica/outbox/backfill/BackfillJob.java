package com.srilakshmikanthanp.replica.outbox.backfill;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BackfillJob {
	private long id;
	private long destinationId;
}
