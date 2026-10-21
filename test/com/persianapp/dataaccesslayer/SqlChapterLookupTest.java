package com.persianapp.dataaccesslayer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Runs SqlChapterLookup against a local MariaDB (XAMPP defaults: root, empty password).
 * Uses its own database, persian_app_test. Skipped when MariaDB or its driver is not available.
 */
@DisplayName("UC-02: SqlChapterLookup with a real local MariaDB")
class SqlChapterLookupTest {

    private static final String SERVER_URL = "jdbc:mariadb://localhost:3306/";
    private static final String TEST_DATABASE = "persian_app_test";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static boolean databaseAvailable;

    private SqlChapterLookup lookup;

    @BeforeAll
    static void checkDatabase() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
                    Statement statement = connection.createStatement()) {
                statement.execute("CREATE DATABASE IF NOT EXISTS " + TEST_DATABASE
                        + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            }
            databaseAvailable = true;
        } catch (ClassNotFoundException | SQLException e) {
            databaseAvailable = false;
        }
    }

    @BeforeEach
    void resetTables() throws SQLException {
        assumeTrue(databaseAvailable,
                "Local MariaDB (XAMPP) or the MariaDB driver is not available - skipping");
        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS chapter_components");
            statement.execute("DROP TABLE IF EXISTS chapters");
            statement.execute("CREATE TABLE chapters ("
                    + "id VARCHAR(36) NOT NULL PRIMARY KEY, "
                    + "title VARCHAR(255) NOT NULL) ENGINE=InnoDB");
            statement.execute("INSERT INTO chapters (id, title) VALUES ('ch-1', 'Chapter 1')");
        }
        lookup = new SqlChapterLookup(SqlChapterLookupTest::connect);
    }

    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(SERVER_URL + TEST_DATABASE, USER, PASSWORD);
    }

    @Test
    @DisplayName("Finds a chapter that exists")
    void findsExistingChapter() {
        assertTrue(lookup.exists("ch-1"));
    }

    @Test
    @DisplayName("Reports a chapter that does not exist")
    void reportsMissingChapter() {
        assertFalse(lookup.exists("missing"));
    }

    @Test
    @DisplayName("A deleted chapter no longer exists")
    void deletedChapterNoLongerExists() throws SQLException {
        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM chapters WHERE id = 'ch-1'");
        }
        assertFalse(lookup.exists("ch-1"));
    }
}