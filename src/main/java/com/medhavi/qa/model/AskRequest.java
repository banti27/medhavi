package com.medhavi.qa.model;

/** Request payload for POST /api/ask. */
public record AskRequest(String documentText, String question, String mode) {}
