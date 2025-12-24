package com.medhavi.qa.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PdfToTextConverter {

    public File convertPdfToTextFile(File pdfFile, File outputTextFile) {
        String text = convertPdfToText(pdfFile);
        try {
            Files.writeString(outputTextFile.toPath(), text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputTextFile;
    }

    public String convertPdfToText(File pdfFile) {
        StringBuilder text = new StringBuilder();

        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            text.append(pdfStripper.getText(document));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text.toString();
    }

}
