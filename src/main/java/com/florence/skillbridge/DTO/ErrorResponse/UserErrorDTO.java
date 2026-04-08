package com.florence.skillbridge.DTO.ErrorResponse;

import java.util.Map;

public class UserErrorDTO {
    private int status;
    private String error;
    private Map<String, String> errors;

    public UserErrorDTO(int status, String error, Map<String, String> errors) {
        this.status = status;
        this.error = error;
        this.errors = errors;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setError(String error) {
        this.error = error;
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

    public Map<String, String> getErrors() {
        return errors;
    }
}
