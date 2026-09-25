package com.contexto.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " não encontrado com id: " + id, HttpStatus.NOT_FOUND);
    }
}
