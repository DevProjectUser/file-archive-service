package com.signicat.dev.validations;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileValidator {
    void validateFileInput(List<MultipartFile> files);
}