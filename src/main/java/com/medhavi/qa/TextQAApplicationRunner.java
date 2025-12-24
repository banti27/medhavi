package com.medhavi.qa;

import java.util.Scanner;

/**
 * Standalone runner for the Text QA Application.
 * 
 * This class provides an alternative entry point for testing the application
 * with a direct file path instead of interactive input.
 * 
 * Usage:
 *   java -cp build/libs/text-qa-system-1.0-SNAPSHOT.jar \
 *        com.medhavi.qa.TextQAApplicationRunner \
 *        /path/to/document.txt
 * 
 * Or with gradle:
 *   ./gradlew runApp --args="/path/to/document.pdf"
 */
public class TextQAApplicationRunner {

    /**
     * Main entry point for running the QA application with a file path argument.
     * 
     * @param args Command line arguments - should contain the file path
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: TextQAApplicationRunner <file_path>");
            System.err.println("Example: TextQAApplicationRunner /Users/vansh/Downloads/IndiaConstitutionEnglish.pdf");
            System.exit(1);
        }

        String filePath = args[0];
        System.out.println("Processing file: " + filePath);
        
        // Create a mock scanner that returns the file path
        try (Scanner scanner = new Scanner(System.in)) {
            // Directly pass the file path from arguments
            processApplication(filePath, scanner);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Processes the application with the given file path.
     * 
     * @param filePath The path to the document
     * @param scanner The Scanner for user input
     * @throws Exception If processing fails
     */
    private static void processApplication(String filePath, Scanner scanner) throws Exception {
        // Display banner (reuse from main application)
        displayBanner();
        
        // Process the file
        String fileContent = TextQAApplication.loadAndValidateFile(filePath);
        
        // Initialize QA engine
        com.medhavi.qa.engine.QuestionAnsweringEngine qaEngine = 
            TextQAApplication.initializeQAEngine(fileContent);

        // Run interactive Q&A loop
        TextQAApplication.runInteractiveQALoop(scanner, qaEngine);
    }

    /**
     * Displays the application banner.
     */
    private static void displayBanner() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("  Text Question Answering System with DeepLearning4j");
        System.out.println("═══════════════════════════════════════════════════════");
    }
}
