package com.medhavi.qa.service.llm;

/**
 * Factory abstraction to create/configure an {@link LLMClient}.
 *
 * <p>This avoids calling static env helpers directly from business logic.
 */
public interface LlmClientFactory {

  LLMClient fromEnv();
}
