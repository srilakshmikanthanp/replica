package com.srilakshmikanthanp.replica.playground.replication.crypto;

import com.srilakshmikanthanp.replica.crypto.PayloadCryptography;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CryptoConfig {
  @Bean
  PayloadCryptography payloadCryptography() {
    return PayloadCryptography.none();
  }
}
