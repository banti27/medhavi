package com.medhavi.qa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.medhavi.qa.processor.TextProcessor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TextProcessor
 */
class TextProcessorTest {
    
    private TextProcessor textProcessor;
    
    @BeforeEach
    void setUp() {
        textProcessor = TextProcessor.builder().build();
    }
    
    @Test
    void testSplitIntoSentences() {
        String text = "This is the first sentence. This is the second! And this is the third?";
        List<String> sentences = textProcessor.splitIntoSentences(text);
        
        assertEquals(3, sentences.size());
        assertEquals("This is the first sentence", sentences.get(0));
    }
    
    @Test
    void testCountSentences() {
        String text = "One. Two. Three.";
        int count = textProcessor.countSentences(text);
        
        assertEquals(3, count);
    }
    
    @Test
    void testCleanText() {
        String text = "  Multiple   spaces   here  ";
        String cleaned = textProcessor.cleanText(text);
        
        assertEquals("Multiple spaces here", cleaned);
    }
    
    @Test
    void testExtractKeywords() {
        String text = "Artificial intelligence is the future of technology";
        List<String> keywords = textProcessor.extractKeywords(text);
        
        assertTrue(keywords.contains("artificial"));
        assertTrue(keywords.contains("intelligence"));
        assertTrue(keywords.contains("future"));
        assertTrue(keywords.contains("technology"));
        // Stop words should be filtered out
        assertFalse(keywords.contains("the"));
        assertFalse(keywords.contains("is"));
    }
}
