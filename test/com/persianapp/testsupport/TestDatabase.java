package com.persianapp.testsupport;

import com.persianapp.dataaccesslayer.DatabaseManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Points tests at the persian_app_test database and empties its tables,
 * so every test starts from the same state and does not depend on other tests.
 */
public final class TestDatabase {

    public static final String URL =
            "jdbc:mysql://localhost:3306/persian_app_test"
            + "?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";

    private TestDatabase() {
        // Utility class
    }

    /** Switches to the test database and clears its tables. Call from @BeforeEach. */
    public static void start() throws SQLException {
        DatabaseManager.setDatabaseUrl(URL);
        clearTables();
    }

    public static void clearTables() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM chapter_components");
            stmt.execute("DELETE FROM chapters");
        }
    }
}
