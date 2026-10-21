package com.persianapp.businesslayer;

public final class ChapterValidator {

    private ChapterValidator() {}

    // Validates that the chapter title is not null or blank.
    public static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Chapter title must not be blank.");
        }
    }
}
