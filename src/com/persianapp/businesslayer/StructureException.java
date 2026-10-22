package com.persianapp.businesslayer;

/**
 * Rejection of an invalid UC-02 request, carrying an error code.
 * Extends IllegalArgumentException so existing callers and tests keep working.
 */
public class StructureException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    private final StructureErrorCode code;

    public StructureException(StructureErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public StructureErrorCode getCode() { return code; }
}
