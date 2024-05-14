package com.signicat.dev.service.archival;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public interface FileArchivalService {
    ByteArrayOutputStream archiveFiles(List<MultipartFile> files, String archiveFormat, String ipAddress) throws IOException;
}
