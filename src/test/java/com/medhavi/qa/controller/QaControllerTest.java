package com.medhavi.qa.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class QaControllerTest {

  @Autowired private MockMvc mvc;

  @Test
  void healthWorks() throws Exception {
    mvc.perform(get("/api/health"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.ok").value(true));
  }

  @Test
  void askFileWorks() throws Exception {
    MockMultipartFile file =
        new MockMultipartFile(
            "file", "doc.txt", "text/plain", "Java is a programming language.".getBytes());

    mvc.perform(multipart("/api/askFile").file(file).param("question", "What is Java?"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.answer").exists())
        .andExpect(jsonPath("$.mode").value("extractive"));
  }
}
