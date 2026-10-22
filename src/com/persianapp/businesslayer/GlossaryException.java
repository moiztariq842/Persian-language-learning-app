package com.persianapp.businesslayer;

/**
 * Checked exception for glossary-related errors.
 *
 * <p>Wraps lower-level exceptions (e.g. {@link java.sql.SQLException}) so that
 * the business and presentation layers never import {@code java.sql}.
 * This satisfies the Dependency Inversion Principle — upper layers depend
 * only on abstractions defined in the business layer.</p>
 */
public class GlossaryException extends Exception {

    /**
     * Constructs a GlossaryException with a user-facing message.
     *
     * @param message descriptive error message (may be in Urdu per NFR-US-06)
     */
    public GlossaryException(String message) {
        super(message);
    }

    /**
     * Constructs a GlossaryException wrapping a root cause.
     *
     * @param message descriptive error message
     * @param cause   the underlying exception
     */
    public GlossaryException(String message, Throwable cause) {
        super(message, cause);
    }
}
