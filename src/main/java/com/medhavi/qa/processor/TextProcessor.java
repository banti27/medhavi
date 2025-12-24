package com.medhavi.qa.processor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.medhavi.qa.constants.RegexConstants;

/**
 * Immutable text file processor for document analysis operations.
 * 
 * This class is thread-safe and immutable. All instances must be created through the Builder pattern.
 * Once constructed, the state cannot be modified.
 * 
 * @author Medhavi QA System
 * @version 1.0
 */
public final class TextProcessor {

    private static final Logger log = LoggerFactory.getLogger(TextProcessor.class);

    /**
     * Immutable list of stop words for keyword extraction
     */
    private final List<String> stopWords;

    /**
     * Minimum word length for keyword extraction
     */
    private final int minKeywordLength;

    /**
     * Private constructor - use Builder to create instances.
     * 
     * @param builder The builder with configuration
     */
    private TextProcessor(Builder builder) {
        this.stopWords = List.copyOf(builder.stopWords);  // Make immutable copy
        this.minKeywordLength = builder.minKeywordLength;
        log.debug("TextProcessor initialized with {} stop words and minimum keyword length: {}",
                stopWords.size(), minKeywordLength);
    }

    /**
     * Reads a text file and returns its content.
     * 
     * @param filePath Path to the text file
     * @return Content of the file as a String
     * @throws IOException If file cannot be read
     */
    public String readTextFile(String filePath) throws IOException {
        File file = new File(filePath);
        return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    }

    /**
     * Splits text into sentences.
     * 
     * @param text The text to split
     * @return List of sentences
     */
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

    /**
     * Counts the number of sentences in the text.
     * 
     * @param text The text to analyze
     * @return Number of sentences
     */
    public int countSentences(String text) {
        return splitIntoSentences(text).size();
    }

    /**
     * Cleans and normalizes text.
     * 
     * @param text The text to clean
     * @return Cleaned text
     */
    public String cleanText(String text) {
        // Remove multiple spaces
        text = text.replaceAll(RegexConstants.WHITESPACE_PATTERN.pattern(), " ");
        // Remove leading/trailing whitespace
        text = text.trim();
        return text;
    }

    /**
     * Extracts keywords from text (simple implementation).
     * 
     * @param text The text to analyze
     * @return List of keywords
     */
    public List<String> extractKeywords(String text) {
        // Convert to lowercase and split into words
        String[] words = text.toLowerCase().split("\\W+");
        List<String> keywords = new ArrayList<>();

        for (String word : words) {
            if (word.length() > minKeywordLength && !stopWords.contains(word)) {
                keywords.add(word);
            }
        }

        return List.copyOf(keywords);  // Return immutable copy
    }

    /**
     * Gets the builder for creating TextProcessor instances.
     * 
     * @return A new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for immutable TextProcessor instances.
     * 
     * This class provides a fluent API for configuring and creating TextProcessor instances.
     */
    public static class Builder {
        private List<String> stopWords;
        private int minKeywordLength;

        /**
         * Constructs a new Builder with default configuration.
         */
        public Builder() {
            // Default stop words list
            this.stopWords = new ArrayList<>(List.of(
                    "the", "a", "an", "and", "or", "but",
                    "is", "are", "was", "were", "in", "on", "at", "to", "for", "of",
                    "with", "by", "from", "as", "it", "that", "this"));
            this.minKeywordLength = 3;  // Default minimum keyword length
        }

        /**
         * Sets the stop words list.
         * 
         * @param stopWords List of stop words to exclude from keywords
         * @return This builder instance for method chaining
         */
        public Builder stopWords(List<String> stopWords) {
            if (stopWords == null) {
                throw new IllegalArgumentException("stopWords cannot be null");
            }
            this.stopWords = new ArrayList<>(stopWords);
            return this;
        }

        /**
         * Adds a stop word to the stop words list.
         * 
         * @param stopWord The stop word to add
         * @return This builder instance for method chaining
         */
        public Builder addStopWord(String stopWord) {
            if (stopWord == null || stopWord.trim().isEmpty()) {
                throw new IllegalArgumentException("stopWord cannot be null or empty");
            }
            this.stopWords.add(stopWord.toLowerCase());
            return this;
        }

        /**
         * Sets the minimum keyword length.
         * 
         * @param minKeywordLength Minimum length for extracted keywords
         * @return This builder instance for method chaining
         */
        public Builder minKeywordLength(int minKeywordLength) {
            if (minKeywordLength < 1) {
                throw new IllegalArgumentException("minKeywordLength must be at least 1");
            }
            this.minKeywordLength = minKeywordLength;
            return this;
        }

        /**
         * Builds and returns an immutable TextProcessor instance.
         * 
         * @return A new immutable TextProcessor instance
         */
        public TextProcessor build() {
            log.debug("Building TextProcessor with custom configuration");
            return new TextProcessor(this);
        }
    }
}

