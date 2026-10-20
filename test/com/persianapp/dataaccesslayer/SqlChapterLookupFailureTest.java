package com.persianapp.dataaccesslayer;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.persianapp.businesslayer.DataAccessException;

@DisplayName("UC-02: SqlChapterLookup failure handling (no database needed)")
class SqlChapterLookupFailureTest {

    private static final ConnectionProvider UNAVAILABLE = () -> {
        throw new SQLException("database down");
    };

    @Test
    @DisplayName("Requires a connection provider")
    void requiresConnectionProvider() {
        assertThrows(IllegalArgumentException.class, () -> new SqlChapterLookup(null));
    }

    @Test
    @DisplayName("Wraps a database failure in DataAccessException")
    void wrapsDatabaseFailure() {
        SqlChapterLookup lookup = new SqlChapterLookup(UNAVAILABLE);
        assertThrows(DataAccessException.class, () -> lookup.exists("ch-1"));
    }
}