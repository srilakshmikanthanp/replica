package com.srilakshmikanthanp.replica.crypto;

/**
 * Optional encryption of event payloads at rest. The repositories encrypt a payload before it is written to
 * the inbox or outbox table and decrypt it when it is read. Register your own implementation when building
 * the repositories; the default is {@link #none()}, which stores payloads as they are.
 */
public interface PayloadCryptography {
	String encrypt(String payload);
	String decrypt(String payload);

	static PayloadCryptography none() {
		return NoCryptography.INSTANCE;
	}

	enum NoCryptography implements PayloadCryptography {
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
}
