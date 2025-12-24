package com.medhavi.qa.service.text;

import java.io.IOException;
import java.util.List;

/** Abstraction for text processing operations used by the QA engine. */
public interface TextProcessingService {

  String readTextFile(String filePath) throws IOException;

  List<String> splitIntoSentences(String text);

  int countSentences(String text);

  String cleanText(String text);

  List<String> extractKeywords(String text);
}
