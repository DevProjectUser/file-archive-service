package com.signicat.dev.exception.handler;

import com.signicat.dev.exception.ArchivalStrategyNotFoundException;
import com.signicat.dev.exception.FairUsageLimitExpiredException;
import com.signicat.dev.exception.FileInputNotValidException;
import com.signicat.dev.exception.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static com.signicat.dev.constants.ApplicationConstants.*;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> handleMaxSizeException(MaxUploadSizeExceededException exception) {
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(
                        ErrorResponseDTO.builder()
                                .message(UPLOADED_FILE_EXCEED_UPLOAD_LIMIT)
                                .details(exception.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(FileInputNotValidException.class)
    public ResponseEntity<Object> handleFileInputNotValidException(FileInputNotValidException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorResponseDTO.builder()
                                .message(FILE_INPUT_NOT_VALID)
                                .details(exception.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(ArchivalStrategyNotFoundException.class)
    public ResponseEntity<Object> handleArchivalStrategyNotFoundException(ArchivalStrategyNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorResponseDTO.builder()
                                .message(ARCHIVAL_STRATEGY_NOT_FOUND)
                                .details(exception.getMessage())
                                .build());
    }

    @ExceptionHandler(FairUsageLimitExpiredException.class)
    public ResponseEntity<Object> handleFairUsageLimitExpiredException(FairUsageLimitExpiredException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorResponseDTO.builder()
                                .message(FAIR_USAGE_LIMIT_EXPIRED)
                                .details(exception.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ErrorResponseDTO.builder()
                                .message(APPLICATION_ERROR)
                                .details(exception.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ErrorResponseDTO.builder()
                                .message(APPLICATION_ERROR)
                                .details(exception.getMessage())
                                .build()
                );
    }
}
