package com.medhavi.qa.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for FileFormatHandler with real files.
 */
@DisplayName("FileFormatHandler Integration Tests")
class FileFormatHandlerIntegrationTest {

    @Test
    @DisplayName("Should read TXT file successfully")
    void testReadTxtFile() throws IOException {
        // Create a test TXT file
        Path testFile = Files.createTempFile("test", ".txt");
        Files.writeString(testFile, "This is a test document. It contains multiple sentences!");
        
        try {
            String content = FileFormatHandler.readFile(testFile.toString());
            
            assertNotNull(content);
            assertTrue(content.contains("test document"));
            assertTrue(content.length() > 0);
        } finally {
            Files.delete(testFile);
        }
    }

    @Test
    @DisplayName("Should throw IOException for non-existent file")
    void testReadNonExistentFile() {
        assertThrows(IOException.class, () -> {
            FileFormatHandler.readFile("/non/existent/path/file.txt");
        });
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for unsupported format")
    void testReadUnsupportedFormat() throws IOException {
        // Create a test file with unsupported extension
        Path testFile = Files.createTempFile("test", ".docx");
        
        try {
            assertThrows(IllegalArgumentException.class, () -> {
                FileFormatHandler.readFile(testFile.toString());
            });
        } finally {
            Files.delete(testFile);
        }
    }

    @Test
    @DisplayName("Should handle PDF file if it exists")
    void testPdfFileHandling() {
        String pdfPath = "/Users/vansh/Downloads/IndiaConstitutionEnglish.pdf";
        
        // Check if the PDF file exists
        if (Files.exists(Paths.get(pdfPath))) {
            try {
                String content = FileFormatHandler.readFile(pdfPath);
                
                assertNotNull(content);
                assertTrue(content.length() > 0);
                System.out.println("Successfully extracted " + content.length() + 
                    " characters from PDF file");
            } catch (IOException e) {
                fail("Failed to read PDF file: " + e.getMessage(), e);
            }
        } else {
            System.out.println("PDF file not found at: " + pdfPath);
            System.out.println("Skipping PDF test...");
        }
    }
}
