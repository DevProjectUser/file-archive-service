package com.signicat.dev.strategy;

import com.signicat.dev.enums.ArchiveType;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;

public interface ArchivalStrategy {
    ByteArrayOutputStream archiveFiles(List<MultipartFile> files) throws Exception;
    ArchiveType getArchiveType();
}
