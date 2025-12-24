package com.medhavi.qa.controller;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.medhavi.qa.engine.QuestionAnsweringEngine;
import com.medhavi.qa.file.FileFormatHandler;
import com.medhavi.qa.http.QaEngineCache;
import com.medhavi.qa.http.dto.AskRequest;
import com.medhavi.qa.http.dto.AskResponse;
import com.medhavi.qa.llm.OllamaClient;

@RestController
@RequestMapping("/api")
public class QaController {

    private final QaEngineCache cache = new QaEngineCache();

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "ok", true,
                "service", "medhavi",
                "message", "Hello from Spring Boot backend"
        );
    }

    @PostMapping(value = "/ask", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public AskResponse ask(@RequestParam(required = false) String mode, @RequestBody AskRequest body) {
        String documentText = body.documentText();
        String question = body.question();

        String effectiveMode = (body.mode() == null || body.mode().isBlank())
                ? (mode == null || mode.isBlank() ? System.getenv().getOrDefault("QA_MODE", "extractive") : mode)
                : body.mode();
        effectiveMode = effectiveMode.trim().toLowerCase();

        if (documentText == null || documentText.isBlank()) {
            throw new IllegalArgumentException("documentText is required");
        }
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("question is required");
        }

        boolean cached = cache.contains(documentText);
        QuestionAnsweringEngine engine = cache.getOrCreate(documentText);

        String answer;
        String usedMode;
        if ("llm".equals(effectiveMode) || "rag".equals(effectiveMode)) {
            String llmAnswer = engine.answerQuestionWithLLM(question, OllamaClient.fromEnv());
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

    @PostMapping(value = "/askFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public AskResponse askFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("question") String question,
            @RequestParam(name = "mode", required = false) String mode
    ) throws Exception {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file is required");
        }
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("question is required");
        }

        String effectiveMode = (mode == null || mode.isBlank())
                ? System.getenv().getOrDefault("QA_MODE", "extractive")
                : mode;
        effectiveMode = effectiveMode.trim().toLowerCase();

        java.nio.file.Path tmp = java.nio.file.Files.createTempFile("medhavi-upload-", safeSuffix(file.getOriginalFilename()));
        try {
            file.transferTo(tmp);
            String documentText = FileFormatHandler.readFile(tmp.toString());

            boolean cached = cache.contains(documentText);
            QuestionAnsweringEngine engine = cache.getOrCreate(documentText);

            String answer;
            String usedMode;
            if ("llm".equals(effectiveMode) || "rag".equals(effectiveMode)) {
                String llmAnswer = engine.answerQuestionWithLLM(question, OllamaClient.fromEnv());
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
        } finally {
            try {
                java.nio.file.Files.deleteIfExists(tmp);
            } catch (Exception ignored) {
                // ignore
            }
        }
    }

    private static String safeSuffix(String filename) {
        if (filename == null) return ".txt";
        int idx = filename.lastIndexOf('.');
        if (idx < 0) return ".txt";
        return filename.substring(idx);
    }
}
