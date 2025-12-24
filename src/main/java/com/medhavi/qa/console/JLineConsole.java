package com.medhavi.qa.console;

import java.io.PrintWriter;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

/**
 * Minimal JLine 3 wrapper for basic interactive input.
 *
 * Benefits over Scanner:
 * - line editing
 * - input history (up/down)
 * - better terminal behavior
 */
public final class JLineConsole implements AutoCloseable {

    private final Terminal terminal;
    private final LineReader reader;
    private final PrintWriter out;

    public JLineConsole() {
    // JLine sometimes logs warnings to stdout/stderr via java.util.logging.
    // Disable it to keep the interactive console clean.
    java.util.logging.Logger.getLogger("org.jline").setLevel(java.util.logging.Level.OFF);

        try {
            this.terminal = TerminalBuilder.builder()
                    .system(true)
            // If the environment doesn't allow creating a system terminal (common when
            // running under Gradle or redirected streams), fall back without noisy logs.
            .dumb(true)
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize terminal", e);
        }

        this.reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .build();

    this.out = terminal.writer();
    }

    /**
     * Reads a line from the terminal.
     *
     * @param prompt prompt to display
     * @return user input, trimmed; returns null when user interrupts/exits (Ctrl+C/Ctrl+D)
     */
    public String readLine(String prompt) {
        try {
            String line = reader.readLine(prompt);
            return line == null ? null : line.trim();
        } catch (UserInterruptException | EndOfFileException e) {
            return null;
        }
    }

    /**
     * Prints a line in a JLine-friendly way.
     *
     * If a prompt is currently active, this will print above it so the prompt doesn't get corrupted.
     */
    public void println(String message) {
        if (message == null) {
            message = "";
        }
        reader.printAbove(message);
        flush();
    }

    /**
     * Prints multiple lines (each line displayed above the active prompt).
     */
    public void printlnLines(Iterable<String> lines) {
        if (lines == null) {
            return;
        }
        for (String line : lines) {
            println(line);
        }
    }

    /**
     * Prints a blank line above the prompt.
     */
    public void blankLine() {
        println("");
    }

    /**
     * Low-level print to the terminal writer (useful at startup before any prompt).
     */
    public void rawPrintln(String message) {
        out.println(message == null ? "" : message);
        flush();
    }

    public void flush() {
        out.flush();
        try {
            terminal.flush();
        } catch (Exception ignored) {
            // best-effort
        }
    }

    @Override
    public void close() {
        try {
            terminal.close();
        } catch (Exception ignored) {
            // best-effort
        }
    }
}
