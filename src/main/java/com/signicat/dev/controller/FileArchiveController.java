package com.signicat.dev.controller;

import com.signicat.dev.enums.ArchiveType;
import com.signicat.dev.service.archival.FileArchivalService;
import com.signicat.dev.validations.FileValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static com.signicat.dev.constants.ApplicationConstants.FILE_ARCHIVE_CONTENT_DISPOSITION;
import static com.signicat.dev.constants.ApplicationConstants.FILE_ARCHIVE_MEDIA_TYPE;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileArchiveController {

    private final FileValidator fileValidator;
    private final FileArchivalService fileArchivalService;

    @PostMapping("/archive")
    public ResponseEntity<?> archiveFiles(@RequestParam("files") List<MultipartFile> files, @RequestParam("format") String archiveFormat, HttpServletRequest request) throws Exception {
        fileValidator.validateFileInput(files);
        if (archiveFormat == null || archiveFormat.isEmpty()) {
            archiveFormat = ArchiveType.ZIP.getExtension();
        }
        ByteArrayOutputStream byteArrayOutputStream = fileArchivalService.archiveFiles(files, archiveFormat, request.getRemoteAddr());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        FILE_ARCHIVE_CONTENT_DISPOSITION + archiveFormat)
                .contentType(MediaType.parseMediaType(FILE_ARCHIVE_MEDIA_TYPE))
                .body(byteArrayOutputStream.toByteArray());
    }
}
