package com.signicat.dev.archival.test;

import com.signicat.dev.enums.ArchiveType;
import com.signicat.dev.strategy.impl.ZipArchivalStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ZipArchivalStrategyTest {

    private final ZipArchivalStrategy zipArchivalStrategy = new ZipArchivalStrategy();

    @Mock
    private MultipartFile mockFile;

    @Test
    public void testArchiveFiles() throws IOException {

        when(mockFile.getOriginalFilename()).thenReturn("test.txt");
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("test data".getBytes()));

        List<MultipartFile> files = List.of(mockFile);

        ByteArrayOutputStream outputStream = zipArchivalStrategy.archiveFiles(files);

        verify(mockFile, times(1)).getInputStream();
        assertNotEquals(0, outputStream.size());
    }

    @Test
    public void testArchiveFiles_IOException() throws IOException {

        when(mockFile.getOriginalFilename()).thenReturn("test.txt");
        when(mockFile.getInputStream()).thenThrow(new IOException("Test exception"));

        List<MultipartFile> files = List.of(mockFile);

        assertThrows(IOException.class, () -> zipArchivalStrategy.archiveFiles(files));
    }

    @Test
    public void testGetArchiveType() {
        ArchiveType archiveType = zipArchivalStrategy.getArchiveType();
        assertEquals(ArchiveType.ZIP, archiveType);
    }
}
