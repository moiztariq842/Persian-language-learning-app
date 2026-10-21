package com.persianapp.businesslayer;

public final class ValidationResult {
    private static final ValidationResult VALID = new ValidationResult(null, null);

    private final String field;
    private final String message;

    private ValidationResult(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public static ValidationResult valid() {
        return VALID;
    }

    public static ValidationResult invalid(String field, String message) {
        return new ValidationResult(field, message);
    }

    public boolean isValid() { return message == null; }
    public String getField() { return field; }
    public String getMessage() { return message; }
}
