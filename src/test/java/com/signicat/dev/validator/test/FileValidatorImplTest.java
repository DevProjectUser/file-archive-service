package com.signicat.dev.validator.test;

import com.signicat.dev.exception.FairUsageLimitExpiredException;
import com.signicat.dev.exception.FileInputNotValidException;
import com.signicat.dev.validations.FileValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class FileValidatorImplTest {

    @Autowired
    private FileValidator fileValidator;

    @Test
    void validateFileInput_withValidFiles_shouldNotThrowException() {
        MultipartFile file1 = new MockMultipartFile("file1", "file1.txt", "text/plain", "some content".getBytes());
        MultipartFile file2 = new MockMultipartFile("file2", "file2.txt", "text/plain", "some content".getBytes());
        List<MultipartFile> files = List.of(file1, file2);

        assertDoesNotThrow(() -> fileValidator.validateFileInput(files));
    }

    @Test
    void validateFileInput_withEmptyFile_shouldThrowException() {
        MultipartFile file1 = new MockMultipartFile("file1", "file1.txt", "text/plain", "".getBytes());
        MultipartFile file2 = new MockMultipartFile("file2", "file2.txt", "text/plain", "some content".getBytes());
        List<MultipartFile> files = List.of(file1, file2);

        assertThrows(FileInputNotValidException.class, () -> fileValidator.validateFileInput(files));
    }

    @Test
    void validateFileInput_withMaliciousFileExtension_shouldThrowException() {
        MultipartFile file1 = new MockMultipartFile("file1", "file1.exe", "text/plain", "some content".getBytes());
        MultipartFile file2 = new MockMultipartFile("file2", "file2.txt", "text/plain", "some content".getBytes());
        List<MultipartFile> files = List.of(file1, file2);

        assertThrows(FileInputNotValidException.class, () -> fileValidator.validateFileInput(files));
    }

    @Test
    void validateFileInput_withMoreFilesThanLimit_shouldThrowException() {
        MultipartFile file1 = new MockMultipartFile("file1", "file1.txt", "text/plain", "some content".getBytes());
        MultipartFile file2 = new MockMultipartFile("file2", "file2.txt", "text/plain", "some content".getBytes());
        MultipartFile file3 = new MockMultipartFile("file3", "file3.txt", "text/plain", "some content".getBytes());
        MultipartFile file4 = new MockMultipartFile("file4", "file4.txt", "text/plain", "some content".getBytes());

        List<MultipartFile> files = List.of(file1, file2, file3, file4);

        assertThrows(FairUsageLimitExpiredException.class, () -> fileValidator.validateFileInput(files));
    }

}