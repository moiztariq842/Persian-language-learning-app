package com.persianapp.businesslayer;

public class ContentValidationException extends RuntimeException {
    private final ValidationResult result;

    public ContentValidationException(ValidationResult result) {
        super(result.getMessage());
        this.result = result;
    }

    public ValidationResult getResult() {
        return result;
    }
}
