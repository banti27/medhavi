package com.medhavi.qa.constants;

import java.util.regex.Pattern;

/**
 * RegexConstants - Centralized repository for all regular expression patterns used in the project.
 *
 * This class provides pre-compiled Pattern objects for common text processing operations.
 * Using pre-compiled patterns improves performance by avoiding repeated compilation.
 *
 * @author Medhavi QA System
 * @version 1.0
 */
public final class RegexConstants {

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static constants.
     */
    private RegexConstants() {
        throw new UnsupportedOperationException("RegexConstants is a utility class and cannot be instantiated");
    }

    // ==========================================
    // TEXT PROCESSING PATTERNS
    // ==========================================

    /**
     * SENTENCE_PATTERN - Pattern for splitting text into sentences.
     *
     * This pattern matches sentence-ending punctuation marks followed by optional whitespace.
     * It handles periods (.), exclamation marks (!), and question marks (?) with any
     * amount of trailing whitespace.
     *
     * Pattern: [.!?]+\s*
     * - [.!?] : Matches any single punctuation: period, exclamation, or question mark
     * - +     : One or more of the above punctuation marks
     * - \s*   : Zero or more whitespace characters (spaces, tabs, newlines)
     *
     * USAGE EXAMPLES:
     * ===============
     *
     * Input: "Hello world. How are you! What's going on?"
     * Output: ["Hello world", "How are you", "What's going on"]
     *
     * Input: "This is great!!! Why not??? Let's go..."
     * Output: ["This is great", "Why not", "Let's go"]
     *
     * Input: "Dr. Smith went home.\n\nHe was tired."
     * Output: ["Dr", "Smith went home", "He was tired"]  // Note: splits on "Dr."
     *
     * LIMITATIONS:
     * ============
     * - Doesn't handle abbreviations like "Dr.", "U.S.A.", "etc."
     * - May split on decimal numbers like "version 1.0"
     * - Doesn't consider quotes or parentheses around sentences
     *
     * For more advanced sentence boundary detection, consider using:
     * - OpenNLP Sentence Detector
     * - Stanford CoreNLP
     * - Apache UIMA
     */
    public static final Pattern SENTENCE_PATTERN = Pattern.compile("[.!?]+\\s*");

    /**
     * WHITESPACE_PATTERN - Pattern for matching multiple whitespace characters.
     *
     * This pattern matches one or more whitespace characters including spaces,
     * tabs, newlines, carriage returns, and other Unicode whitespace.
     *
     * Pattern: \s+
     * - \s : Matches any whitespace character
     * - +  : One or more occurrences
     *
     * USAGE EXAMPLES:
     * ===============
     *
     * Input: "Hello   world!\n\nHow are you?"
     * Replace with single space: "Hello world! How are you?"
     *
     * Input: "Multiple\t\ttabs\t\tand spaces   here"
     * Replace with single space: "Multiple tabs and spaces here"
     */
    public static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

    /**
     * WORD_BOUNDARY_PATTERN - Pattern for splitting text into words.
     *
     * This pattern matches any sequence of non-word characters (punctuation, spaces, etc.)
     * and is commonly used to tokenize text into individual words.
     *
     * Pattern: \W+
     * - \W : Matches any non-word character (not letters, digits, or underscore)
     * - +  : One or more occurrences
     *
     * USAGE EXAMPLES:
     * ===============
     *
     * Input: "Hello, world! How are you?"
     * Split result: ["Hello", "world", "How", "are", "you"]
     *
     * Input: "word1_word2,word3.word4"
     * Split result: ["word1_word2", "word3", "word4"]  // Keeps underscores
     */
    public static final Pattern WORD_BOUNDARY_PATTERN = Pattern.compile("\\W+");

    // ==========================================
    // UTILITY METHODS
    // ==========================================

    /**
     * Validates if a regex pattern compiles successfully.
     *
     * @param pattern The regex pattern string to validate
     * @return true if the pattern is valid, false otherwise
     */
    public static boolean isValidPattern(String pattern) {
        try {
            Pattern.compile(pattern);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets a human-readable description of what a pattern does.
     *
     * @param pattern The pattern to describe
     * @return Description string
     */
    public static String getPatternDescription(Pattern pattern) {
        String regex = pattern.pattern();

        switch (regex) {
            case "[.!?]+\\s*":
                return "Sentence boundary detector - splits on punctuation followed by whitespace";
            case "\\s+":
                return "Multiple whitespace matcher - finds consecutive spaces/tabs/newlines";
            case "\\W+":
                return "Word boundary splitter - separates words from punctuation";
            default:
                return "Custom regex pattern: " + regex;
        }
    }
}
