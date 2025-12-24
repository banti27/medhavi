package com.medhavi.qa.service.http;

import com.medhavi.qa.engine.QuestionAnsweringEngine;

/**
 * Port interface for engine caching.
 *
 * <p>Allows swapping caching strategy (in-memory, redis, disk, etc.).
 */
public interface CachingService {

  QuestionAnsweringEngine getOrCreate(String documentText);

  boolean contains(String documentText);
}
