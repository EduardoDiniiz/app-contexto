package com.contexto.common.exception;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class ApiErrorResponse {
    int status;
    String message;
    Map<String, String> errors;
    LocalDateTime timestamp;

    public ApiErrorResponse(int status, String message) {
        this(status, message, null, LocalDateTime.now());
    }

    public ApiErrorResponse(int status, String message, Map<String, String> errors) {
        this(status, message, errors, LocalDateTime.now());
    }
}
