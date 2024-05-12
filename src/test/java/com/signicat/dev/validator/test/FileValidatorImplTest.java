package com.signicat.dev.validator.test;

import com.signicat.dev.exception.FileInputNotValidException;
import com.signicat.dev.validations.FileValidator;
import com.signicat.dev.validations.impl.FileValidatorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileValidatorImplTest {

    private FileValidator fileValidator;

    @BeforeEach
    void setUp() {
        fileValidator = new FileValidatorImpl();
    }

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
}