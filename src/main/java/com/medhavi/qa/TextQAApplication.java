package com.medhavi.qa;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.medhavi.qa.console.JLineConsole;
import com.medhavi.qa.engine.QuestionAnsweringEngine;
import com.medhavi.qa.file.FileFormatHandler;
import com.medhavi.qa.llm.OllamaClient;
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
        try (JLineConsole console = new JLineConsole()) {
            displayApplicationBanner(console);
            String filePath = getFilePathFromUser(console);
            if (filePath == null || filePath.isBlank()) {
                console.println("No file path provided. Exiting.");
                return;
            }
            String fileContent = loadAndValidateFile(filePath);
            QuestionAnsweringEngine qaEngine = initializeQAEngine(fileContent);
            runInteractiveQALoop(console, qaEngine);
        } catch (Exception e) {
            log.error("An error occurred: {}", e.getMessage(), e);
        }
    }

    /**
     * Displays the application banner at startup.
     */
    private static void displayApplicationBanner(JLineConsole console) {
        // Use raw printing at startup (no prompt active yet).
        console.rawPrintln("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        console.rawPrintln("  Medhavi Text Question Answering (DeepLearning4j)");
        console.rawPrintln("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        console.rawPrintln("");
    }

    /**
     * Prompts the user to enter a file path and returns the input.
     * 
     * @param scanner The Scanner instance for user input
     * @return The file path entered by the user
     */
    private static String getFilePathFromUser(JLineConsole console) {
        return console.readLine("📄 File path › ");
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
    static void runInteractiveQALoop(JLineConsole console, com.medhavi.qa.engine.QuestionAnsweringEngine qaEngine) {
    displayQALoopInstructions(console);
        
        while (true) {
            String question = getUserQuestion(console);
            if (question == null) {
        console.println("Session ended.");
                break;
            }
            
            if (isExitCommand(question)) {
        console.println("Thank you for using Medhavi QA!");
                break;
            }
            
            if (isValidQuestion(question)) {
                processAndDisplayAnswer(console, qaEngine, question);
            }
        }
    }

    /**
     * Displays instructions for the Q&A loop.
     */
    private static void displayQALoopInstructions(JLineConsole console) {
        console.println("✓ Document processed. Ask questions about it.");
        console.println("Tip: type 'exit' or 'quit' to end.");
        console.blankLine();
    }

    /**
     * Prompts the user to enter a question.
     * 
     * @param scanner The Scanner instance for user input
     * @return The question entered by the user
     */
    private static String getUserQuestion(JLineConsole console) {
        return console.readLine("❓ Question › ");
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
    private static void processAndDisplayAnswer(JLineConsole console, QuestionAnsweringEngine qaEngine, String question) {
        try {
            String mode = System.getenv().getOrDefault("QA_MODE", "extractive").trim().toLowerCase();

            String answer;
            if ("llm".equals(mode) || "rag".equals(mode)) {
                String llmAnswer = qaEngine.answerQuestionWithLLM(question, OllamaClient.fromEnv());

                // If Ollama isn't running/reachable, fall back to the extractive engine.
                if (llmAnswer != null && llmAnswer.startsWith("LLM error:")) {
                    console.println("(LLM mode requested but Ollama isn't reachable. Falling back to extractive answers.)");
                    console.println("Tip: start Ollama and set OLLAMA_MODEL, then retry.");
                    console.blankLine();
                    answer = qaEngine.answerQuestion(question);
                } else {
                    answer = llmAnswer;
                }
            } else {
                answer = qaEngine.answerQuestion(question);
            }

            if (answer == null) {
                answer = "(no answer)";
            }

            console.blankLine();
            console.println("Answer:");
            for (String line : answer.split("\\R")) {
                console.println(line);
            }
            console.blankLine();
            log.debug("Answer: {}", answer);
        } catch (Exception e) {
            log.error("Error processing question: {}", e.getMessage(), e);
        }
    }
}
