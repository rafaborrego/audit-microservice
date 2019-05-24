package com.rafaborrego.audit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class auditNotFoundException extends RuntimeException {

    public auditNotFoundException(Long auditId) {
        super(String.format("The audit %d was not found", auditId));
    }
}
