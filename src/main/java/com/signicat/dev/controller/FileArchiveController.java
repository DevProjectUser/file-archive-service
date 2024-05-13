package com.signicat.dev.controller;

import com.signicat.dev.validations.FileValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileArchiveController {

    private final FileValidator fileValidator;

    @PostMapping("/archive")
    public ResponseEntity<?> archiveFiles(@RequestParam("files") List<MultipartFile> files, @RequestParam("format") String archiveFormat, HttpServletRequest request) {
        fileValidator.validateFileInput(files);
        return ResponseEntity
                .ok()
                .body("Files archived successfully");
    }
}
