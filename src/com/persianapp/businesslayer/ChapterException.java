package com.persianapp.businesslayer;

// Domain exception for chapter operations.
public class ChapterException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    private final ChapterErrorCode code;

    public ChapterException(ChapterErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    // Returns the error code.
    public ChapterErrorCode getCode() {
        return code;
    }
}
