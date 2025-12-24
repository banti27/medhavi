package com.medhavi.qa.service.http;

import com.medhavi.qa.engine.QuestionAnsweringEngine;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/** Spring-managed adapter for {@link QaEngineCache}. */
@Service
public class InMemoryCache implements CachingService {

  private final ConcurrentHashMap<String, QuestionAnsweringEngine> cache =
      new ConcurrentHashMap<>();

  @Override
  public QuestionAnsweringEngine getOrCreate(String documentText) {
    Objects.requireNonNull(documentText, "documentText");
    String key = sha256(documentText);
    return cache.computeIfAbsent(
        key, k -> QuestionAnsweringEngine.builder().content(documentText).build());
  }

  @Override
  public boolean contains(String documentText) {
    Objects.requireNonNull(documentText, "documentText");
    return cache.containsKey(sha256(documentText));
  }

  static String sha256(String value) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(bytes);
    } catch (Exception e) {
      throw new IllegalStateException("Unable to compute SHA-256", e);
    }
  }
}
