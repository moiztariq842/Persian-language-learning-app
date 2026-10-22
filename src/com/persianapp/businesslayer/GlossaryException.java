package com.persianapp.businesslayer;

/**
 * Checked exception for glossary-related persistence errors (UC-04).
 * Wraps lower-level exceptions so the business layer never imports java.sql.
 */
public class GlossaryException extends Exception {

    public GlossaryException(String message) {
        super(message);
    }

    public GlossaryException(String message, Throwable cause) {
        super(message, cause);
    }
}
