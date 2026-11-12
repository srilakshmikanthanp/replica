package com.srilakshmikanthanp.replica.crypto;

enum NoPayloadCryptography implements PayloadCryptography {
  INSTANCE;

  @Override
  public String encrypt(String payload) {
    return payload;
  }

  @Override
  public String decrypt(String payload) {
    return payload;
  }
}
