package com.persianapp.businesslayer;

/**
 * Business rules for component content (BR-02, BR-03 style checks, NFR-US-06 messages).
 * Text is never trimmed, so the stored value is exactly what the administrator typed (FR-CS-07).
 */
public class ContentValidator {
    private static final String FIELD = "Component content";

    public ValidationResult validate(String content) {
        if (content == null || content.isBlank()) {
            return ValidationResult.invalid(FIELD,
                    "The content is empty. Enter the Persian or Urdu text for this component.");
        }
        for (int i = 0; i < content.length(); i++) {
            char ch = content.charAt(i);
            boolean allowedWhitespace = ch == '\n' || ch == '\r' || ch == '\t';
            if (Character.getType(ch) == Character.CONTROL && !allowedWhitespace) {
                return ValidationResult.invalid(FIELD,
                        "The content contains a hidden control character at position " + (i + 1)
                                + ". Remove it and try again.");
            }
        }
        return ValidationResult.valid();
    }
}
