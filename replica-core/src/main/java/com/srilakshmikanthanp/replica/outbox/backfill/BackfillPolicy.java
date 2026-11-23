package com.srilakshmikanthanp.replica.outbox.backfill;

public non-sealed interface BackfillPolicy extends LiveEventFilter, NewJobValidator {
}
