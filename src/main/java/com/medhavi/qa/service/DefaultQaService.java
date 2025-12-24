package com.medhavi.qa.service;

import com.medhavi.qa.engine.QuestionAnsweringEngine;
import com.medhavi.qa.model.AskRequest;
import com.medhavi.qa.model.AskResponse;
import com.medhavi.qa.service.http.CachingService;
import com.medhavi.qa.service.llm.LlmClientFactory;
import com.medhavi.qa.service.text.DocumentTextExtractor;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DefaultQaService implements QaService {

  private final CachingService cache;
  private final DocumentTextExtractor extractor;
  private final LlmClientFactory llmClientFactory;

  public DefaultQaService(
      CachingService cache, DocumentTextExtractor extractor, LlmClientFactory llmClientFactory) {
    this.cache = cache;
    this.extractor = extractor;
    this.llmClientFactory = llmClientFactory;
  }

  @Override
  public AskResponse ask(AskRequest request, String modeOverride) {
    if (request == null) {
      throw new IllegalArgumentException("request is required");
    }

    String documentText = request.documentText();
    String question = request.question();

    if (documentText == null || documentText.isBlank()) {
      throw new IllegalArgumentException("documentText is required");
    }
    if (question == null || question.isBlank()) {
      throw new IllegalArgumentException("question is required");
    }

    String effectiveMode = resolveMode(modeOverride, request.mode());

    boolean cached = cache.contains(documentText);
    QuestionAnsweringEngine engine = cache.getOrCreate(documentText);

    return toResponse(engine, question, effectiveMode, cached);
  }

  @Override
  public AskResponse askFile(MultipartFile file, String question, String modeOverride)
      throws Exception {
    if (file == null || file.isEmpty()) {
      throw new IllegalArgumentException("file is required");
    }
    if (question == null || question.isBlank()) {
      throw new IllegalArgumentException("question is required");
    }

    String effectiveMode = resolveMode(modeOverride, null);

    Path tmp = Files.createTempFile("medhavi-upload-", safeSuffix(file.getOriginalFilename()));
    try {
      file.transferTo(tmp);
      String documentText = extractor.extract(tmp.toString());

      boolean cached = cache.contains(documentText);
      QuestionAnsweringEngine engine = cache.getOrCreate(documentText);

      return toResponse(engine, question, effectiveMode, cached);
    } finally {
      try {
        Files.deleteIfExists(tmp);
      } catch (Exception ignored) {
        // ignore
      }
    }
  }

  private AskResponse toResponse(
      QuestionAnsweringEngine engine, String question, String effectiveMode, boolean cached) {
    String answer;
    String usedMode;

    if ("llm".equals(effectiveMode) || "rag".equals(effectiveMode)) {
      // Note: LLMClient is created via factory to allow swapping
      // implementations/config.
      String llmAnswer = engine.answerQuestionWithLLM(question, llmClientFactory.fromEnv());
      if (llmAnswer != null && llmAnswer.startsWith("LLM error:")) {
        answer = engine.answerQuestion(question);
        usedMode = "extractive";
      } else {
        answer = llmAnswer;
        usedMode = effectiveMode;
      }
    } else {
      answer = engine.answerQuestion(question);
      usedMode = "extractive";
    }

    return new AskResponse(answer, usedMode, cached);
  }

  private static String resolveMode(String... candidates) {
    // Priority: first non-blank candidate, else env default.
    for (String c : candidates) {
      if (c != null && !c.isBlank()) {
        return c.trim().toLowerCase();
      }
    }
    return System.getenv().getOrDefault("QA_MODE", "extractive").trim().toLowerCase();
  }

  private static String safeSuffix(String filename) {
    if (filename == null) return ".txt";
    int idx = filename.lastIndexOf('.');
    if (idx < 0) return ".txt";
    return filename.substring(idx);
  }
}
