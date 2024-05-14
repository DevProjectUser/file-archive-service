package com.signicat.dev.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ErrorResponseDTO {
    private String message;
    private String details;
}
