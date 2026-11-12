package com.srilakshmikanthanp.replica.outbox.backfill;

public interface CursorCodec {
	<T extends Cursor> T decode(String encoded, Class<T> cursorClass);
	String encode(Cursor cursor);
}
