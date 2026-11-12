package com.srilakshmikanthanp.replica.playground.http.cors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
  @Value("${playground.cors-origins:*}")
  private String[] origins;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
      .allowedOriginPatterns(origins)
      .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
      .exposedHeaders("Retry-After");
  }
}
