package com.signicat.dev.controller.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;

import static com.signicat.dev.constants.ApplicationConstants.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FileArchivalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @LocalServerPort
    private int port;

    @Test
    public void testArchiveFilesEndpoint_InvalidArchiveFormat() throws Exception {

        MockMultipartFile file = new MockMultipartFile("files",
                VALID_FILE_NAME,
                MediaType.TEXT_PLAIN_VALUE,
                VALID_FILE_CONTENT.getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart(FILE_ARCHIVE_SERVICE_URL)
                        .file(file)
                        .param("format", INVALID_ARCHIVE_FORMAT)
                        .param("ipAddress", IP_ADDRESS))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(ARCHIVAL_STRATEGY_NOT_FOUND));

    }

    @Test
    public void testArchiveFilesEndpoint_MaxFileSizeExceeded() throws Exception {
        byte[] data = new byte[3 * 1024 * 1024];
        Arrays.fill(data, (byte) 0xFF);

        MockMultipartFile file = new MockMultipartFile("files",
                VALID_FILE_NAME,
                MediaType.TEXT_PLAIN_VALUE,
                data);

        RestAssured.given()
                .multiPart("file", file.getBytes())
                .param("format", ZIP_ARCHIVE_FORMAT)
                .param("ipAddress", IP_ADDRESS)
                .port(port)
                .when()
                .post(FILE_ARCHIVE_SERVICE_URL)
                .then()
                .statusCode(HttpStatus.PAYLOAD_TOO_LARGE.value())
                .extract()
                .response()
                .jsonPath()
                .getString("message")
                .equals(UPLOADED_FILE_EXCEED_UPLOAD_LIMIT);

    }

    @Test
    public void testArchiveFilesEndpoint_FairUsageLimitExceeded() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile("files",
                VALID_FILE_NAME + 1,
                MediaType.TEXT_PLAIN_VALUE,
                VALID_FILE_CONTENT.getBytes());

        MockMultipartFile file2 = new MockMultipartFile("files",
                VALID_FILE_NAME + 2,
                MediaType.TEXT_PLAIN_VALUE,
                VALID_FILE_CONTENT.getBytes());

        MockMultipartFile file3 = new MockMultipartFile("files",
                VALID_FILE_NAME + 3,
                MediaType.TEXT_PLAIN_VALUE,
                VALID_FILE_CONTENT.getBytes());

        MockMultipartFile file4 = new MockMultipartFile("files",
                VALID_FILE_NAME + 4,
                MediaType.TEXT_PLAIN_VALUE,
                VALID_FILE_CONTENT.getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart(FILE_ARCHIVE_SERVICE_URL)
                        .file(file1)
                        .file(file2)
                        .file(file3)
                        .file(file4)
                        .param("format", ZIP_ARCHIVE_FORMAT)
                        .param("ipAddress", IP_ADDRESS))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(FAIR_USAGE_LIMIT_EXPIRED));

    }

    @Test
    public void testArchiveFilesEndpoint_InvalidFileInput() throws Exception {
        MockMultipartFile file = new MockMultipartFile("files",
                INVALID_FILE_NAME,
                MediaType.TEXT_PLAIN_VALUE,
                VALID_FILE_CONTENT.getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart(FILE_ARCHIVE_SERVICE_URL)
                        .file(file)
                        .param("format", ZIP_ARCHIVE_FORMAT)
                        .param("ipAddress", IP_ADDRESS))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(FILE_INPUT_NOT_VALID));
    }

    @Test
    public void testArchiveFilesEndpoint_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("files",
                VALID_FILE_NAME,
                MediaType.TEXT_PLAIN_VALUE,
                VALID_FILE_CONTENT.getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart(FILE_ARCHIVE_SERVICE_URL)
                        .file(file)
                        .param("format", ZIP_ARCHIVE_FORMAT)
                        .param("ipAddress", IP_ADDRESS))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
