package com.florence.skillbridge.Exception;

import java.util.Map;

public class DTOValidationException extends RuntimeException {
    private final Map<String, String> errors;

    public DTOValidationException(Map<String, String> errors) {
        super("Validation failed");
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
