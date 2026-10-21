package com.persianapp.businesslayer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UC-03: ContentValidator rules")
class ContentValidatorTest {
    private final ContentValidator validator = new ContentValidator();

    @Test
    void acceptsPersianText() {
        assertTrue(validator.validate("سلام، حال شما چطور است؟").isValid());
    }

    @Test
    void acceptsUrduText() {
        assertTrue(validator.validate("یہ ایک مثال ہے۔").isValid());
    }

    @Test
    void rejectsNull() {
        assertFalse(validator.validate(null).isValid());
    }

    @Test
    void rejectsEmptyString() {
        assertFalse(validator.validate("").isValid());
    }

    @Test
    void rejectsWhitespaceOnly() {
        assertFalse(validator.validate("   \n\t  ").isValid());
    }

    @Test
    void rejectsHiddenControlCharacter() {
        assertFalse(validator.validate("سلام\u0000").isValid());
    }

    @Test
    void allowsLineBreaksAndTabs() {
        assertTrue(validator.validate("خط اول\nخط دوم\tنکته").isValid());
    }

    @Test
    void invalidResultNamesFieldAndExplainsFix() {
        ValidationResult result = validator.validate("");
        assertEquals("Component content", result.getField());
        assertTrue(result.getMessage().contains("Enter"));
    }
}
