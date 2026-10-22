package com.persianapp.businesslayer;

// Validates chapter data (UC-01).
public final class ChapterValidator {

    private ChapterValidator() {}

    // Validates that the chapter title is not null or blank.
    public static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Chapter title must not be blank.");
        }
    }
}
