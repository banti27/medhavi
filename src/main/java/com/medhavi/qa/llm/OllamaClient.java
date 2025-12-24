package com.medhavi.qa.llm;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Minimal Ollama client using the local REST API.
 *
 * Requires Ollama running locally (default: http://localhost:11434).
 */
public final class OllamaClient implements LLMClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final HttpClient http;
    private final URI baseUrl;
    private final String model;

    public OllamaClient(URI baseUrl, String model) {
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.baseUrl = baseUrl;
        this.model = model;
    }

    public static OllamaClient fromEnv() {
        String base = System.getenv().getOrDefault("OLLAMA_BASE_URL", "http://localhost:11434");
        String model = System.getenv().getOrDefault("OLLAMA_MODEL", "llama3.2:3b");
        return new OllamaClient(URI.create(base), model);
    }

    @Override
    public String generateAnswer(String question, List<String> contextChunks) {
        try {
            String prompt = buildPrompt(question, contextChunks);

            Map<String, Object> payload = new HashMap<>();
            payload.put("model", model);
            payload.put("prompt", prompt);
            payload.put("stream", false);
            // Keep it conservative, you can tune later.
            payload.put("temperature", 0.2);

            String body = MAPPER.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(baseUrl.resolve("/api/generate"))
                    .timeout(Duration.ofSeconds(120))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return "LLM error (HTTP " + response.statusCode() + "): " + response.body();
            }

            JsonNode root = MAPPER.readTree(response.body());
            JsonNode answer = root.get("response");
            if (answer == null || answer.isNull()) {
                return "LLM error: unexpected response from Ollama.";
            }

            return answer.asText().trim();
        } catch (Exception e) {
            return "LLM error: " + e.getMessage();
        }
    }

    private static String buildPrompt(String question, List<String> contextChunks) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a careful question-answering assistant.\n");
        sb.append("Answer ONLY using the provided context.\n");
        sb.append("If the answer is not in the context, say: \"I don't know based on the provided document.\"\n\n");

        sb.append("Context:\n");
        if (contextChunks == null || contextChunks.isEmpty()) {
            sb.append("(no context)\n");
        } else {
            for (int i = 0; i < contextChunks.size(); i++) {
                sb.append("[Chunk ").append(i + 1).append("] ");
                sb.append(contextChunks.get(i));
                sb.append("\n\n");
            }
        }

        sb.append("Question: ").append(question).append("\n");
        sb.append("Answer:\n");
        return sb.toString();
    }
}
