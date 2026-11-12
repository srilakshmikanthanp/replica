package com.srilakshmikanthanp.replica.playground.replication.backfill;

import com.srilakshmikanthanp.replica.outbox.backfill.Cursor;

public record KeyCursor(String last) implements Cursor {}
