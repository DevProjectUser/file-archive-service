package com.signicat.dev.validations.impl;

import com.signicat.dev.exception.FileInputNotValidException;
import com.signicat.dev.validations.FileValidator;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.signicat.dev.enums.FileExtension.isUnsafeFileExtension;

@Component
public class FileValidatorImpl implements FileValidator {
    /**
     * @param files
     */
    public void validateFileInput(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new FileInputNotValidException("Can't process request. Files are empty");
        } else {
            files.forEach(file -> {
                if (isUnsafeFileExtension(file.getOriginalFilename()) ||
                        file.isEmpty()) {
                    throw new FileInputNotValidException("Can't process request. One of the files is empty or has a unsafe extension");
                }
            });
        }
    }
}
