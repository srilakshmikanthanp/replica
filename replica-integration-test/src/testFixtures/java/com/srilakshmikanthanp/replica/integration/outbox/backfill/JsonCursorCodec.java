package com.srilakshmikanthanp.replica.integration.outbox.backfill;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srilakshmikanthanp.replica.outbox.backfill.Cursor;
import com.srilakshmikanthanp.replica.outbox.backfill.CursorCodec;

public class JsonCursorCodec implements CursorCodec {
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public <T extends Cursor> T decode(String encoded, Class<T> cursorClass) {
    try {
      return objectMapper.readValue(encoded, cursorClass);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Cannot decode cursor " + encoded, e);
    }
  }

  @Override
  public String encode(Cursor cursor) {
    try {
      return objectMapper.writeValueAsString(cursor);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Cannot encode cursor", e);
    }
  }
}
