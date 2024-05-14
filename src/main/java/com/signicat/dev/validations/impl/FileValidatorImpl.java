package com.signicat.dev.validations.impl;

import com.signicat.dev.exception.FairUsageLimitExpiredException;
import com.signicat.dev.exception.FileInputNotValidException;
import com.signicat.dev.validations.FileValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.signicat.dev.constants.ApplicationConstants.*;
import static com.signicat.dev.enums.FileExtension.isUnsafeFileExtension;

@Component
public class FileValidatorImpl implements FileValidator {

    @Value("${archive.service.fair.usage.limit}")
    private int fairUsageLimit;

    /**
     * @param files
     */
    public void validateFileInput(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new FileInputNotValidException(EMPTY_FILES_MESSAGE);
        } else if (files.size() > fairUsageLimit) {
            throw new FairUsageLimitExpiredException(
                    String.format(FAIR_USAGE_LIMIT_EXCEEDED, fairUsageLimit));
        } else {
            files.forEach(file -> {
                if (isUnsafeFileExtension(file.getOriginalFilename()) ||
                        file.isEmpty()) {
                    throw new FileInputNotValidException(
                            EMPTY_FILE_OR_UNSAFE_EXTENSION_ERROR_MESSAGE);
                }
            });
        }
    }
}
