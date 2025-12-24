package com.medhavi.qa.service;

import com.medhavi.qa.model.AskRequest;
import com.medhavi.qa.model.AskResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * Abstraction for question answering.
 *
 * <p>Controllers depend on this interface to allow swapping implementations (e.g., local extractive
 * engine vs. remote LLM vs. fully RAG).
 */
public interface QaService {

  AskResponse ask(AskRequest request, String modeOverride);

  AskResponse askFile(MultipartFile file, String question, String modeOverride) throws Exception;
}
