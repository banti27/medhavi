package com.medhavi.qa.file;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles reading content from different file formats (TXT, PDF).
 * 
 * This class provides a unified interface for extracting text from various document types.
 * It automatically detects the file format based on file extension and uses the appropriate
 * text extraction method.
 * 
 * Supported formats:
 * - TXT (Plain text files)
 * - PDF (Portable Document Format)
 * 
 * @author Medhavi QA System
 * @version 1.0
 */
public final class FileFormatHandler {

    private static final Logger log = LoggerFactory.getLogger(FileFormatHandler.class);

    /**
     * Supported file formats
     */
    public enum FileFormat {
        TXT("txt", "Plain Text"),
        PDF("pdf", "Portable Document Format");

        private final String extension;
        private final String description;

        FileFormat(String extension, String description) {
            this.extension = extension.toLowerCase();
            this.description = description;
        }

        /**
         * Gets the file format from a file extension.
         * 
         * @param extension The file extension (with or without dot)
         * @return The FileFormat enum value
         * @throws IllegalArgumentException If the extension is not supported
         */
        public static FileFormat fromExtension(String extension) {
            String cleanExtension = extension.toLowerCase().replace(".", "");
            
            for (FileFormat format : FileFormat.values()) {
                if (format.extension.equals(cleanExtension)) {
                    return format;
                }
            }
            
            throw new IllegalArgumentException(
                String.format("Unsupported file format: .%s. Supported formats: TXT, PDF", cleanExtension)
            );
        }

        public String getExtension() {
            return extension;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Private constructor - this is a utility class.
     */
    private FileFormatHandler() {
        throw new UnsupportedOperationException("FileFormatHandler is a utility class and cannot be instantiated");
    }

    /**
     * Reads and extracts text content from a file.
     * Automatically detects the file format and uses the appropriate extraction method.
     * 
     * @param filePath The path to the file
     * @return The extracted text content
     * @throws IOException If the file cannot be read or processed
     * @throws IllegalArgumentException If the file format is not supported
     */
    public static String readFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        
        if (!Files.exists(path)) {
            throw new IOException("File not found: " + filePath);
        }
        
        FileFormat format = detectFileFormat(filePath);
        log.info("Detected file format: {} ({})", format.getExtension().toUpperCase(), format.getDescription());
        
        switch (format) {
            case TXT:
                return readTextFile(filePath);
            case PDF:
                return readPdfFile(filePath);
            default:
                throw new IllegalArgumentException("Unsupported file format: " + format);
        }
    }

    /**
     * Detects the file format based on the file extension.
     * 
     * @param filePath The path to the file
     * @return The detected FileFormat
     * @throws IllegalArgumentException If the file extension is not recognized
     */
    public static FileFormat detectFileFormat(String filePath) {
        String fileName = new File(filePath).getName();
        String extension = getFileExtension(fileName);
        
        return FileFormat.fromExtension(extension);
    }

    /**
     * Extracts the file extension from a filename.
     * 
     * @param fileName The filename
     * @return The file extension (without the dot)
     */
    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        
        if (lastDotIndex <= 0) {
            throw new IllegalArgumentException("File has no extension: " + fileName);
        }
        
        return fileName.substring(lastDotIndex + 1);
    }

    /**
     * Reads a plain text file.
     * 
     * @param filePath The path to the text file
     * @return The file content as a string
     * @throws IOException If the file cannot be read
     */
    private static String readTextFile(String filePath) throws IOException {
        log.debug("Reading plain text file: {}", filePath);
        
        File file = new File(filePath);
        String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        
        log.debug("Successfully read {} characters from text file", content.length());
        return content;
    }

    /**
     * Reads a PDF file and extracts all text content.
     * 
     * This method uses Apache PDFBox to:
     * 1. Open the PDF document
     * 2. Create a text stripper
     * 3. Extract all text from all pages
     * 4. Clean up resources
     * 
     * @param filePath The path to the PDF file
     * @return The extracted text from the PDF
     * @throws IOException If the file cannot be read or processed
     */
    private static String readPdfFile(String filePath) throws IOException {
        log.debug("Reading PDF file: {}", filePath);
        
        try (PDDocument document = PDDocument.load(new File(filePath))) {
            
            if (document.isEncrypted()) {
                log.warn("PDF file is encrypted. Attempting to read with empty password...");
            }
            
            PDFTextStripper textStripper = new PDFTextStripper();
            
            // Optional: Set additional properties
            textStripper.setLineSeparator("\n");  // Use newlines between lines
            
            String content = textStripper.getText(document);
            
            log.debug("Successfully extracted {} characters from {} page(s)",
                content.length(),
                document.getNumberOfPages());
            
            return content;
            
        } catch (IOException e) {
            log.error("Failed to read PDF file: {}", filePath, e);
            throw e;
        }
    }

    /**
     * Validates if a file is supported by checking its extension.
     * 
     * @param filePath The path to the file
     * @return True if the file format is supported
     */
    public static boolean isSupported(String filePath) {
        try {
            detectFileFormat(filePath);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Gets a list of supported file extensions.
     * 
     * @return A string describing supported formats
     */
    public static String getSupportedFormats() {
        StringBuilder sb = new StringBuilder("Supported file formats: ");
        
        for (int i = 0; i < FileFormat.values().length; i++) {
            FileFormat format = FileFormat.values()[i];
            sb.append(String.format(".%s (%s)", format.getExtension().toUpperCase(), format.getDescription()));
            
            if (i < FileFormat.values().length - 1) {
                sb.append(", ");
            }
        }
        
        return sb.toString();
    }
}
