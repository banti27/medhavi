package com.medhavi.qa.service.text;

import java.io.IOException;

/** Service abstraction for extracting text from a document. */
public interface DocumentTextExtractor {

  String extract(String filePath) throws IOException;
}
