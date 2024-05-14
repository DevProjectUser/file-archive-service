package com.signicat.dev.controller.test;

import com.signicat.dev.controller.FileArchiveController;
import com.signicat.dev.exception.FileInputNotValidException;
import com.signicat.dev.validations.FileValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileArchiveController.class)
public class FileArchiveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileValidator fileValidator;

    private MockMultipartFile file;

    @BeforeEach
    public void setup() {
        file = new MockMultipartFile("files", "test.txt", MediaType.TEXT_PLAIN_VALUE, "test data".getBytes());
    }

    @Test
    public void shouldReturnOkWhenFilesAreValid() throws Exception {
        doNothing().when(fileValidator).validateFileInput(List.of(file));

        mockMvc.perform(multipart("/api/files/archive")
                        .file(file)
                        .param("format", "zip"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnBadRequestWhenFilesAreInvalid() throws Exception {
        doThrow(FileInputNotValidException.class).when(fileValidator).validateFileInput(List.of(file));

        mockMvc.perform(multipart("/api/files/archive")
                        .file(file)
                        .param("format", "zip"))
                .andExpect(status().isBadRequest());
    }

}