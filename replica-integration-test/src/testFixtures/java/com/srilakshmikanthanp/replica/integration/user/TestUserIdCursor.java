package com.srilakshmikanthanp.replica.integration.user;

import com.srilakshmikanthanp.replica.outbox.backfill.Cursor;

public record TestUserIdCursor(long lastId) implements Cursor {
}
