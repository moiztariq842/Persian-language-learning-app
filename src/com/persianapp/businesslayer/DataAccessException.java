package com.persianapp.businesslayer;

/**
 * Unchecked failure of the storage layer (database unreachable, statement failed, ...).
 * Lives in the business layer because StructureDao implementations throw it.
 */
public class DataAccessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}