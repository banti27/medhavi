package com.medhavi.qa.service.text;

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
import org.springframework.stereotype.Service;

/**
 * Default implementation for extracting text from documents.
 *
 * <p>Supports TXT and PDF.
 */
@Service
public class DefaultDocumentTextExtractor implements DocumentTextExtractor {

  private static final Logger log = LoggerFactory.getLogger(DefaultDocumentTextExtractor.class);

  /** Supported file formats. */
  enum FileFormat {
    TXT("txt", "Plain Text"),
    PDF("pdf", "Portable Document Format");

    private final String extension;
    private final String description;

    FileFormat(String extension, String description) {
      this.extension = extension.toLowerCase();
      this.description = description;
    }

    static FileFormat fromExtension(String extension) {
      String cleanExtension = extension.toLowerCase().replace(".", "");

      for (FileFormat format : FileFormat.values()) {
        if (format.extension.equals(cleanExtension)) {
          return format;
        }
      }

      throw new IllegalArgumentException(
          String.format(
              "Unsupported file format: .%s. Supported formats: TXT, PDF", cleanExtension));
    }

    String getExtension() {
      return extension;
    }

    String getDescription() {
      return description;
    }
  }

  @Override
  public String extract(String filePath) throws IOException {
    Path path = Paths.get(filePath);
    if (!Files.exists(path)) {
      throw new IOException("File not found: " + filePath);
    }

    FileFormat format = detectFileFormat(filePath);
    log.info(
        "Detected file format: {} ({})",
        format.getExtension().toUpperCase(),
        format.getDescription());

    return switch (format) {
      case TXT -> readTextFile(filePath);
      case PDF -> readPdfFile(filePath);
    };
  }

  private static FileFormat detectFileFormat(String filePath) {
    String fileName = new File(filePath).getName();
    String extension = getFileExtension(fileName);
    return FileFormat.fromExtension(extension);
  }

  private static String getFileExtension(String fileName) {
    int lastDotIndex = fileName.lastIndexOf('.');
    if (lastDotIndex <= 0) {
      throw new IllegalArgumentException("File has no extension: " + fileName);
    }
    return fileName.substring(lastDotIndex + 1);
  }

  private static String readTextFile(String filePath) throws IOException {
    log.debug("Reading plain text file: {}", filePath);

    File file = new File(filePath);
    String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

    log.debug("Successfully read {} characters from text file", content.length());
    return content;
  }

  private static String readPdfFile(String filePath) throws IOException {
    log.debug("Reading PDF file: {}", filePath);

    try (PDDocument document = PDDocument.load(new File(filePath))) {
      if (document.isEncrypted()) {
        log.warn("PDF file is encrypted. Attempting to read with empty password...");
      }

      PDFTextStripper textStripper = new PDFTextStripper();
      textStripper.setLineSeparator("\n");

      String content = textStripper.getText(document);
      log.debug(
          "Successfully extracted {} characters from {} page(s)",
          content.length(),
          document.getNumberOfPages());

      return content;
    } catch (IOException e) {
      log.error("Failed to read PDF file: {}", filePath, e);
      throw e;
    }
  }
}
