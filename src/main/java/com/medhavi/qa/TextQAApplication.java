package com.medhavi.qa;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.medhavi.qa.engine.QuestionAnsweringEngine;
import com.medhavi.qa.file.FileFormatHandler;
import com.medhavi.qa.processor.TextProcessor;

/**
 * Main application class for the Text Question Answering System.
 * 
 * This application allows users to:
 * 1. Load and process text files
 * 2. Train a Word2Vec model on the document
 * 3. Ask questions about the document content
 * 
 * The main method orchestrates the workflow by delegating to focused helper methods.
 */
public class TextQAApplication {
    
    private static final Logger log = LoggerFactory.getLogger(TextQAApplication.class);
    
    /**
     * Main entry point for the Text QA Application.
     * Orchestrates the workflow: display banner → load file → initialize QA engine → run Q&A loop
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        displayApplicationBanner();
        
        try (Scanner scanner = new Scanner(System.in)) {
            String filePath = getFilePathFromUser(scanner);
            String fileContent = loadAndValidateFile(filePath);
            QuestionAnsweringEngine qaEngine = initializeQAEngine(fileContent);
            runInteractiveQALoop(scanner, qaEngine);
        } catch (Exception e) {
            log.error("An error occurred: {}", e.getMessage(), e);
        }
    }

    /**
     * Displays the application banner at startup.
     */
    private static void displayApplicationBanner() {
        log.info("═══════════════════════════════════════════════════════");
        log.info("  Text Question Answering System with DeepLearning4j");
        log.info("═══════════════════════════════════════════════════════");
    }

    /**
     * Prompts the user to enter a file path and returns the input.
     * 
     * @param scanner The Scanner instance for user input
     * @return The file path entered by the user
     */
    private static String getFilePathFromUser(Scanner scanner) {
        System.out.print("Enter the path to your text file: ");
        return scanner.nextLine().trim();
    }

    /**
     * Loads the text file and validates its existence.
     * Supports both TXT and PDF file formats.
     * 
     * @param filePath The path to the text file (TXT or PDF)
     * @return The content of the file
     * @throws Exception If file doesn't exist or cannot be read
     */
    static String loadAndValidateFile(String filePath) throws Exception {
        Path path = Paths.get(filePath);
        
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("File not found at path: " + filePath);
        }
        
        // Check if file format is supported
        if (!FileFormatHandler.isSupported(filePath)) {
            String message = String.format("Unsupported file format. %s", 
                FileFormatHandler.getSupportedFormats());
            throw new IllegalArgumentException(message);
        }
        
        log.info("Loading document and initializing QA system...");
        
        // Read file content using FileFormatHandler (handles both TXT and PDF)
        String content = FileFormatHandler.readFile(filePath);
        
        logFileStatistics(content);
        return content;
    }

    /**
     * Logs statistics about the loaded file.
     * 
     * @param content The file content
     */
    private static void logFileStatistics(String content) {
        TextProcessor processor = TextProcessor.builder().build();
        
        log.info("Document loaded successfully!");
        log.info("  File size: {} characters", content.length());
        log.info("  Sentences: {}", processor.countSentences(content));
    }

    /**
     * Initializes the Question Answering Engine with the document content.
     * This method trains the Word2Vec model on the document.
     * 
     * @param content The document content
     * @return An initialized QuestionAnsweringEngine instance
     */
    static com.medhavi.qa.engine.QuestionAnsweringEngine initializeQAEngine(String content) {
        log.info("Training Word2Vec model on document...");
        QuestionAnsweringEngine qaEngine = QuestionAnsweringEngine.builder()
                .content(content)
                .build();
        
        log.info("Document processed and ready for questions!");
        return qaEngine;
    }

    /**
     * Runs the interactive Q&A loop.
     * Continuously accepts user questions and provides answers until the user exits.
     * 
     * @param scanner The Scanner instance for user input
     * @param qaEngine The QuestionAnsweringEngine instance
     */
    static void runInteractiveQALoop(Scanner scanner, com.medhavi.qa.engine.QuestionAnsweringEngine qaEngine) {
        displayQALoopInstructions();
        
        while (true) {
            String question = getUserQuestion(scanner);
            
            if (isExitCommand(question)) {
                log.info("Thank you for using the Text QA System!");
                break;
            }
            
            if (isValidQuestion(question)) {
                processAndDisplayAnswer(qaEngine, question);
            }
        }
    }

    /**
     * Displays instructions for the Q&A loop.
     */
    private static void displayQALoopInstructions() {
        log.info("You can now ask questions about the text.");
        log.info("Type 'exit' or 'quit' to end the session.");
    }

    /**
     * Prompts the user to enter a question.
     * 
     * @param scanner The Scanner instance for user input
     * @return The question entered by the user
     */
    private static String getUserQuestion(Scanner scanner) {
        System.out.print("❓ Your question: ");
        return scanner.nextLine().trim();
    }

    /**
     * Checks if the user input is an exit command.
     * 
     * @param input The user input
     * @return True if the input is "exit" or "quit" (case-insensitive)
     */
    private static boolean isExitCommand(String input) {
        return input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit");
    }

    /**
     * Validates that the question is non-empty.
     * 
     * @param question The question to validate
     * @return True if the question is valid (non-empty)
     */
    private static boolean isValidQuestion(String question) {
        if (question.isEmpty()) {
            log.warn("Please enter a valid question.");
            return false;
        }
        return true;
    }

    /**
     * Processes the user's question and displays the answer.
     * 
     * @param qaEngine The QuestionAnsweringEngine instance
     * @param question The user's question
     */
    private static void processAndDisplayAnswer(QuestionAnsweringEngine qaEngine, String question) {
        try {
            String answer = qaEngine.answerQuestion(question);
            log.info("Answer: {}", answer);
        } catch (Exception e) {
            log.error("Error processing question: {}", e.getMessage(), e);
        }
    }
}
