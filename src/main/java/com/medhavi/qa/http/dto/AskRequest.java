package com.medhavi.qa.http.dto;

/**
 * Request payload for POST /api/ask.
 */
public record AskRequest(
        String documentText,
        String question,
        String mode
) {
}
