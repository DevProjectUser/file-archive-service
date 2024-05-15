package com.signicat.dev.service.test;

import com.signicat.dev.domain.entity.UploadStatistic;
import com.signicat.dev.exception.ArchivalStrategyNotFoundException;
import com.signicat.dev.exception.FairUsageLimitExpiredException;
import com.signicat.dev.exception.FileInputNotValidException;
import com.signicat.dev.repository.UploadStatisticRepository;
import com.signicat.dev.service.archival.FileArchivalService;
import com.signicat.dev.validations.FileValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.signicat.dev.constants.ApplicationConstants.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@SpringBootTest
public class FileArchivalServiceTest {

    @Autowired
    private FileArchivalService fileArchivalService;

    @Autowired
    private FileValidator fileValidator;

    @MockBean
    private MultipartFile mockFile;

    @MockBean
    private UploadStatisticRepository mockUploadStatisticRepository;

    @Test
    public void testArchiveFiles_ValidInputs() throws IOException {
        when(mockFile.getOriginalFilename())
                .thenReturn(VALID_FILE_NAME);
        when(mockFile.getInputStream())
                .thenReturn(new ByteArrayInputStream(VALID_FILE_CONTENT.getBytes()));

        List<MultipartFile> files = List.of(mockFile);

        assertDoesNotThrow(() ->
                fileArchivalService.archiveFiles(files, ZIP_ARCHIVE_FORMAT, IP_ADDRESS));
    }

    @Test
    public void testArchiveFiles_InvalidInputs() throws IOException {
        when(mockFile.getOriginalFilename())
                .thenReturn(VALID_FILE_NAME);
        when(mockFile.getInputStream())
                .thenReturn(new ByteArrayInputStream(VALID_FILE_CONTENT.getBytes()));

        List<MultipartFile> files = List.of(mockFile);

        assertThrows(ArchivalStrategyNotFoundException.class,
                () -> fileArchivalService.archiveFiles(files, INVALID_ARCHIVE_FORMAT, IP_ADDRESS));
    }

    @Test
    public void testValidateFileInput_ValidInputs() throws IOException {
        when(mockFile.getOriginalFilename())
                .thenReturn(VALID_FILE_NAME);
        when(mockFile.getInputStream())
                .thenReturn(new ByteArrayInputStream(VALID_FILE_CONTENT.getBytes()));

        List<MultipartFile> files = List.of(mockFile);
        assertDoesNotThrow(() -> fileValidator.validateFileInput(files));
    }

    @Test
    public void testValidateFileInput_InvalidInputs() throws IOException {
        when(mockFile.getOriginalFilename())
                .thenReturn(INVALID_FILE_NAME);
        when(mockFile.getInputStream())
                .thenReturn(new ByteArrayInputStream(VALID_FILE_CONTENT.getBytes()));

        List<MultipartFile> files = List.of(mockFile);

        assertThrows(FileInputNotValidException.class,
                () -> fileValidator.validateFileInput(files));
    }

    @Test
    public void testArchiveFiles_FairUsageLimitExceeded() throws IOException {
        when(mockFile.getOriginalFilename())
                .thenReturn(VALID_FILE_NAME);
        when(mockFile.getInputStream())
                .thenReturn(new ByteArrayInputStream(VALID_FILE_CONTENT.getBytes()));
        when(mockUploadStatisticRepository.findByIpAddressAndDate(anyString(), any()))
                .thenReturn(Optional.of(new UploadStatistic(1L, IP_ADDRESS, LocalDate.now(), 10)));


        List<MultipartFile> files = List.of(mockFile);

        assertThrows(FairUsageLimitExpiredException.class,
                () -> fileArchivalService.archiveFiles(files, ZIP_ARCHIVE_FORMAT, IP_ADDRESS));

        verify(mockUploadStatisticRepository, times(0))
                .save(any(UploadStatistic.class));
    }

    @Test
    public void testArchiveFiles_ThrowsExceptionOnDatabaseError() throws IOException {

        when(mockFile.getOriginalFilename())
                .thenReturn(VALID_FILE_NAME);
        when(mockFile.getInputStream())
                .thenReturn(new ByteArrayInputStream(VALID_FILE_CONTENT.getBytes()));

        doThrow(new DataIntegrityViolationException("Database error"))
                .when(mockUploadStatisticRepository)
                .findByIpAddressAndDate(anyString(), any());


        List<MultipartFile> files = List.of(mockFile);

        assertThrows(RuntimeException.class,
                () -> fileArchivalService.archiveFiles(files, ZIP_ARCHIVE_FORMAT, IP_ADDRESS));

        verify(mockUploadStatisticRepository, times(0))
                .save(any(UploadStatistic.class));
    }

    @Test
    public void testArchiveFiles_CheckRecordSavedInDatabase() throws IOException {

        when(mockFile.getOriginalFilename())
                .thenReturn(VALID_FILE_NAME);
        when(mockFile.getInputStream())
                .thenReturn(new ByteArrayInputStream(VALID_FILE_CONTENT.getBytes()));
        when(mockUploadStatisticRepository.findByIpAddressAndDate(anyString(), any()))
                .thenReturn(Optional.empty());


        List<MultipartFile> files = List.of(mockFile);

        assertDoesNotThrow(() -> fileArchivalService.archiveFiles(files, ZIP_ARCHIVE_FORMAT, IP_ADDRESS));

        verify(mockUploadStatisticRepository, times(1))
                .save(any(UploadStatistic.class));
    }

    @Test
    public void testArchiveFiles_ExceptionDuringFileProcessing() throws IOException {
        when(mockFile.getOriginalFilename())
                .thenReturn(null);
        when(mockFile.getInputStream())
                .thenReturn(new ByteArrayInputStream(VALID_FILE_CONTENT.getBytes()));

        List<MultipartFile> files = List.of(mockFile);

        assertThrows(RuntimeException.class,
                () -> fileArchivalService.archiveFiles(files, ZIP_ARCHIVE_FORMAT, IP_ADDRESS));
    }
}
