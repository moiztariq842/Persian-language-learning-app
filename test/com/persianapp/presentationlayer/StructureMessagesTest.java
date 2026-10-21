package com.persianapp.presentationlayer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.persianapp.businesslayer.StructureErrorCode;

@DisplayName("UC-02: StructureMessages")
class StructureMessagesTest {

    @Test
    @DisplayName("Every error code has an Urdu message")
    void everyCodeHasUrduMessage() {
        for (StructureErrorCode code : StructureErrorCode.values()) {
            assertIsUrdu(StructureMessages.forCode(code));
        }
    }

    @Test
    @DisplayName("The success, fallback, storage and confirmation messages are Urdu")
    void fixedMessagesAreUrdu() {
        assertIsUrdu(StructureMessages.SAVED);
        assertIsUrdu(StructureMessages.UNEXPECTED);
        assertIsUrdu(StructureMessages.STORAGE_FAILED);
        assertIsUrdu(StructureMessages.CONFIRM_REMOVE_ALL);
        assertIsUrdu(StructureMessages.CONFIRM_TITLE);
        assertIsUrdu(StructureMessages.CONFIRM_YES);
        assertIsUrdu(StructureMessages.CONFIRM_NO);
        assertIsUrdu(StructureMessages.SAVE_CANCELLED);
    }

    private static void assertIsUrdu(String text) {
        assertFalse(text.trim().isEmpty());
        boolean hasArabicScript = false;
        for (char c : text.toCharArray()) {
            if (c >= '\u0600' && c <= '\u06FF') {
                hasArabicScript = true;
            }
            assertFalse((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'),
                    "Latin letter found in: " + text);
        }
        assertTrue(hasArabicScript, "No Urdu text in: " + text);
    }
}