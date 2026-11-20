package com.srilakshmikanthanp.replica.integration.outbox;

import com.srilakshmikanthanp.replica.outbox.backfill.Cursor;
import com.srilakshmikanthanp.replica.outbox.backfill.CursorCodec;

class NoCursorCodec implements CursorCodec {
  @Override
  public <T extends Cursor> T decode(String encoded, Class<T> cursorClass) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String encode(Cursor cursor) {
    throw new UnsupportedOperationException();
  }
}
