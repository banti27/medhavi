package com.medhavi.qa.service.llm;

import org.springframework.stereotype.Service;

@Service
public class DefaultLlmClientFactory implements LlmClientFactory {

  @Override
  public LLMClient fromEnv() {
    return OllamaClient.fromEnv();
  }
}
