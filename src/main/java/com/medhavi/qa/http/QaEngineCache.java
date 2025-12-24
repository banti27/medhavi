package com.medhavi.qa.http;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.medhavi.qa.engine.QuestionAnsweringEngine;

/**
 * In-memory cache to avoid retraining Word2Vec on every request.
 *
 * Keyed by SHA-256 of the document text.
 */
public final class QaEngineCache {

    private final ConcurrentHashMap<String, QuestionAnsweringEngine> cache = new ConcurrentHashMap<>();

    public QuestionAnsweringEngine getOrCreate(String documentText) {
        Objects.requireNonNull(documentText, "documentText");
        String key = sha256(documentText);
        return cache.computeIfAbsent(key, k -> QuestionAnsweringEngine.builder().content(documentText).build());
    }

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
