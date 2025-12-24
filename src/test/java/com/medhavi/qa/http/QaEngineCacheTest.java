package com.medhavi.qa.http;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import com.medhavi.qa.engine.QuestionAnsweringEngine;

class QaEngineCacheTest {

    @Test
    void getOrCreate_reusesEngineForSameDocument() {
        QaEngineCache cache = new QaEngineCache();
        String doc = "Hello world. This is a document.";

        QuestionAnsweringEngine e1 = cache.getOrCreate(doc);
        QuestionAnsweringEngine e2 = cache.getOrCreate(doc);

        assertNotNull(e1);
        assertSame(e1, e2);
    }
}
