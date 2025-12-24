package com.medhavi.qa.model;

/** Response payload for POST /api/ask. */
public record AskResponse(String answer, String mode, boolean cached) {}
