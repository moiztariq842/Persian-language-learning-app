package com.persianapp.businesslayer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ChapterValidator — Title Validation (BR-01)")
class ChapterValidatorTest {

    @Test
    @DisplayName("Valid non-blank title does not throw")
    void validTitle_doesNotThrow() {
        assertDoesNotThrow(() -> ChapterValidator.validateTitle("Lesson 1"));
    }

    @Test
    @DisplayName("Title with surrounding spaces is accepted")
    void titleWithSurroundingSpaces_doesNotThrow() {
        assertDoesNotThrow(() -> ChapterValidator.validateTitle("  Hello  "));
    }

    @Test
    @DisplayName("Persian-script title is accepted")
    void persianTitle_doesNotThrow() {
        assertDoesNotThrow(() -> ChapterValidator.validateTitle("درس اول"));
    }

    @Test
    @DisplayName("Null title throws IllegalArgumentException")
    void nullTitle_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
            () -> ChapterValidator.validateTitle(null));
    }

    @Test
    @DisplayName("Empty title throws IllegalArgumentException")
    void emptyTitle_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
            () -> ChapterValidator.validateTitle(""));
    }

    @Test
    @DisplayName("Whitespace-only title throws IllegalArgumentException")
    void blankTitle_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
            () -> ChapterValidator.validateTitle("   "));
    }

    @Test
    @DisplayName("Tab-only title throws IllegalArgumentException")
    void tabTitle_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
            () -> ChapterValidator.validateTitle("\t"));
    }
}
