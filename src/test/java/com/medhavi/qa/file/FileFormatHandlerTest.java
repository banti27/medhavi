package com.medhavi.qa.file;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FileFormatHandler.
 * Tests file format detection and validation.
 */
@DisplayName("FileFormatHandler Tests")
class FileFormatHandlerTest {

    @Test
    @DisplayName("Should detect TXT file format")
    void testDetectTxtFormat() {
        FileFormatHandler.FileFormat format = FileFormatHandler.detectFileFormat("document.txt");
        assertEquals(FileFormatHandler.FileFormat.TXT, format);
    }

    @Test
    @DisplayName("Should detect PDF file format")
    void testDetectPdfFormat() {
        FileFormatHandler.FileFormat format = FileFormatHandler.detectFileFormat("document.pdf");
        assertEquals(FileFormatHandler.FileFormat.PDF, format);
    }

    @Test
    @DisplayName("Should detect file format with uppercase extension")
    void testDetectFormatCaseInsensitive() {
        FileFormatHandler.FileFormat format1 = FileFormatHandler.detectFileFormat("document.TXT");
        FileFormatHandler.FileFormat format2 = FileFormatHandler.detectFileFormat("document.PDF");
        
        assertEquals(FileFormatHandler.FileFormat.TXT, format1);
        assertEquals(FileFormatHandler.FileFormat.PDF, format2);
    }

    @Test
    @DisplayName("Should throw exception for unsupported file format")
    void testUnsupportedFormat() {
        assertThrows(IllegalArgumentException.class, () -> {
            FileFormatHandler.detectFileFormat("document.docx");
        });
    }

    @Test
    @DisplayName("Should throw exception for file without extension")
    void testFileWithoutExtension() {
        assertThrows(IllegalArgumentException.class, () -> {
            FileFormatHandler.detectFileFormat("document");
        });
    }

    @Test
    @DisplayName("Should return true for supported file format")
    void testIsSupportedFormat() {
        assertTrue(FileFormatHandler.isSupported("document.txt"));
        assertTrue(FileFormatHandler.isSupported("document.pdf"));
    }

    @Test
    @DisplayName("Should return false for unsupported file format")
    void testIsUnsupportedFormat() {
        assertFalse(FileFormatHandler.isSupported("document.docx"));
        assertFalse(FileFormatHandler.isSupported("document.xlsx"));
    }

    @Test
    @DisplayName("Should return supported formats description")
    void testGetSupportedFormats() {
        String formats = FileFormatHandler.getSupportedFormats();
        
        assertTrue(formats.contains("TXT"));
        assertTrue(formats.contains("PDF"));
        assertTrue(formats.contains("Supported file formats:"));
    }

    @Test
    @DisplayName("FileFormat enum should have correct properties")
    void testFileFormatProperties() {
        FileFormatHandler.FileFormat txt = FileFormatHandler.FileFormat.TXT;
        FileFormatHandler.FileFormat pdf = FileFormatHandler.FileFormat.PDF;
        
        assertEquals("txt", txt.getExtension());
        assertEquals("Plain Text", txt.getDescription());
        
        assertEquals("pdf", pdf.getExtension());
        assertEquals("Portable Document Format", pdf.getDescription());
    }

    @Test
    @DisplayName("Should convert extension string to FileFormat enum")
    void testFromExtension() {
        assertEquals(FileFormatHandler.FileFormat.TXT, 
            FileFormatHandler.FileFormat.fromExtension("txt"));
        assertEquals(FileFormatHandler.FileFormat.TXT, 
            FileFormatHandler.FileFormat.fromExtension(".txt"));
        assertEquals(FileFormatHandler.FileFormat.PDF, 
            FileFormatHandler.FileFormat.fromExtension("PDF"));
    }

    @Test
    @DisplayName("Should throw exception for invalid extension in fromExtension")
    void testFromExtensionInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            FileFormatHandler.FileFormat.fromExtension("docx");
        });
    }
}
