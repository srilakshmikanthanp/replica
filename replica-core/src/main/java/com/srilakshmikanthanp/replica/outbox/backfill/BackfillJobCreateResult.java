package com.srilakshmikanthanp.replica.outbox.backfill;

import java.util.List;

public record BackfillJobCreateResult(BackfillJob job, List<BackfillJobStep> steps) {
}
