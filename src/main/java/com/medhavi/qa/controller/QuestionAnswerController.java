package com.medhavi.qa.controller;

import com.medhavi.qa.model.AskRequest;
import com.medhavi.qa.model.AskResponse;
import com.medhavi.qa.service.QaService;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class QuestionAnswerController {

  private final QaService qaService;

  public QuestionAnswerController(QaService qaService) {
    this.qaService = qaService;
  }

  @GetMapping("/health")
  public Map<String, Object> health() {
    return Map.of(
        "ok", true,
        "service", "medhavi",
        "message", "Hello from Spring Boot backend");
  }

  @PostMapping(
      value = "/ask",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public AskResponse ask(
      @RequestParam(required = false) String mode, @RequestBody AskRequest body) {
    return qaService.ask(body, mode);
  }

  @PostMapping(
      value = "/askFile",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public AskResponse askFile(
      @RequestParam("file") MultipartFile file,
      @RequestParam("question") String question,
      @RequestParam(name = "mode", required = false) String mode)
      throws Exception {
    return qaService.askFile(file, question, mode);
  }
}
