package com.florence.skillbridge.DTO.ErrorResponse;

import java.util.Map;

public class UserErrorDTO {
    private int status;
    private String error;
    private String message;
    private Map<String, String> errors;

    public UserErrorDTO(int status, String error, String message, Map<String, String> errors) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.errors = errors;
    }

    public UserErrorDTO(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
