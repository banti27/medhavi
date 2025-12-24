package com.medhavi.qa.service.text;

import com.medhavi.qa.constants.RegexConstants;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link TextProcessingService}.
 *
 * <p>This class is intentionally stateless/immutable after construction so a single Spring
 * singleton bean can safely serve concurrent requests.
 */
@Service
public class DefaultTextProcessingService implements TextProcessingService {

  private static final Logger log = LoggerFactory.getLogger(DefaultTextProcessingService.class);

  private final List<String> stopWords;
  private final int minKeywordLength;

  public DefaultTextProcessingService() {
    // Keep defaults aligned with the original TextProcessor.Builder.
    this.stopWords =
        List.copyOf(
            List.of(
                "the", "a", "an", "and", "or", "but", "is", "are", "was", "were", "in", "on", "at",
                "to", "for", "of", "with", "by", "from", "as", "it", "that", "this"));
    this.minKeywordLength = 3;
    log.debug(
        "DefaultTextProcessingService initialized with {} stop words and minimum keyword length: {}",
        stopWords.size(),
        minKeywordLength);
  }

  @Override
  public String readTextFile(String filePath) throws IOException {
    File file = new File(filePath);
    return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
  }

  @Override
  public List<String> splitIntoSentences(String text) {
    String[] sentences = RegexConstants.SENTENCE_PATTERN.split(text);
    List<String> result = new ArrayList<>();

    for (String sentence : sentences) {
      String trimmed = sentence.trim();
      if (!trimmed.isEmpty()) {
        result.add(trimmed);
      }
    }

    return result;
  }

  @Override
  public int countSentences(String text) {
    return splitIntoSentences(text).size();
  }

  @Override
  public String cleanText(String text) {
    // Remove multiple spaces
    text = text.replaceAll(RegexConstants.WHITESPACE_PATTERN.pattern(), " ");
    // Remove leading/trailing whitespace
    text = text.trim();
    return text;
  }

  @Override
  public List<String> extractKeywords(String text) {
    String[] words = text.toLowerCase().split("\\W+");
    List<String> keywords = new ArrayList<>();

    for (String word : words) {
      if (word.length() > minKeywordLength && !stopWords.contains(word)) {
        keywords.add(word);
      }
    }

    return List.copyOf(keywords);
  }
}
