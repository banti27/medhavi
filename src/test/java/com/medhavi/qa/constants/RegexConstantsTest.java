package com.medhavi.qa.constants;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for RegexConstants.
 * Demonstrates usage and validates the regex patterns.
 */
public class RegexConstantsTest {

    @Test
    public void testSentencePattern() {
        // Test basic sentence splitting
        String text = "Hello world. How are you! What's going on?";
        String[] expected = {"Hello world", "How are you", "What's going on"};

        String[] result = RegexConstants.SENTENCE_PATTERN.split(text);
        assertArrayEquals(expected, result);
    }

    @Test
    public void testSentencePatternWithMultiplePunctuation() {
        // Test with multiple punctuation marks
        String text = "This is great!!! Why not??? Let's go...";
        String[] expected = {"This is great", "Why not", "Let's go"};

        String[] result = RegexConstants.SENTENCE_PATTERN.split(text);
        assertArrayEquals(expected, result);
    }

    @Test
    public void testWhitespacePattern() {
        // Test whitespace normalization
        String text = "Hello   world!\n\nHow are you?\t\tGood.";
        String expected = "Hello world! How are you? Good.";

        String result = RegexConstants.WHITESPACE_PATTERN.matcher(text).replaceAll(" ");
        assertEquals(expected, result);
    }

    @Test
    public void testWordBoundaryPattern() {
        // Test word splitting
        String text = "Hello, world! How are you?";
        String[] expected = {"Hello", "world", "How", "are", "you"};

        String[] result = RegexConstants.WORD_BOUNDARY_PATTERN.split(text);
        assertArrayEquals(expected, result);
    }

    @Test
    public void testPatternValidation() {
        // Test valid patterns
        assertTrue(RegexConstants.isValidPattern("[.!?]+\\s*"));
        assertTrue(RegexConstants.isValidPattern("\\s+"));
        assertTrue(RegexConstants.isValidPattern("\\W+"));

        // Test invalid pattern
        assertFalse(RegexConstants.isValidPattern("[unclosed"));
    }

    @Test
    public void testPatternDescriptions() {
        // Test that descriptions are returned
        String desc1 = RegexConstants.getPatternDescription(RegexConstants.SENTENCE_PATTERN);
        assertTrue(desc1.contains("Sentence boundary"));

        String desc2 = RegexConstants.getPatternDescription(RegexConstants.WHITESPACE_PATTERN);
        assertTrue(desc2.contains("whitespace"));

        String desc3 = RegexConstants.getPatternDescription(RegexConstants.WORD_BOUNDARY_PATTERN);
        assertTrue(desc3.contains("Word boundary"));
    }

    @Test
    public void testConstantsAreNotNull() {
        // Ensure all constants are properly initialized
        assertNotNull(RegexConstants.SENTENCE_PATTERN);
        assertNotNull(RegexConstants.WHITESPACE_PATTERN);
        assertNotNull(RegexConstants.WORD_BOUNDARY_PATTERN);
    }

    @Test
    public void testUtilityClassCannotBeInstantiated() {
        // Test that the class cannot be instantiated
        assertThrows(UnsupportedOperationException.class, () -> {
            // This should fail to compile in normal usage, but we can test the runtime behavior
            try {
                java.lang.reflect.Constructor<RegexConstants> constructor =
                    RegexConstants.class.getDeclaredConstructor();
                constructor.setAccessible(true);
                constructor.newInstance();
            } catch (Exception e) {
                throw new UnsupportedOperationException();
            }
        });
    }
}
