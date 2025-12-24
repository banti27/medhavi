package com.medhavi.qa.http.dto;

/**
 * Response payload for POST /api/ask.
 */
public record AskResponse(
        String answer,
        String mode,
        boolean cached
) {
}
