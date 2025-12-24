package com.medhavi.qa.service.llm;

import java.util.List;

/**
 * Abstraction for a text generation model.
 *
 * <p>This lets you start with a local Ollama model and later swap in a cloud LLM
 * (OpenAI/Azure/etc.) without changing the rest of the QA pipeline.
 */
public interface LLMClient {

  /**
   * Generates an answer for a question given supporting context.
   *
   * @param question user question
   * @param contextChunks top retrieved chunks (ordered best-first)
   * @return model answer text
   */
  String generateAnswer(String question, List<String> contextChunks);
}
