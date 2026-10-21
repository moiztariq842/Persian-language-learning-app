package com.persianapp.dataaccesslayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages the MySQL/MariaDB connection and schema for the application.
 * Targets the MariaDB server installed with XAMPP on localhost.
 * The database is created automatically on first connect (createDatabaseIfNotExist).
 */
public class DatabaseManager {

    public static final String DEFAULT_DB_URL =
            "jdbc:mysql://localhost:3306/persian_app"
            + "?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";

    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private static String databaseUrl = DEFAULT_DB_URL;
    private static Connection connection;

    private DatabaseManager() {
        // Utility class
    }

    public static synchronized void setDatabaseUrl(String newUrl) {
        closeConnection();
        databaseUrl = (newUrl != null && !newUrl.isBlank()) ? newUrl : DEFAULT_DB_URL;
    }

    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(databaseUrl, DB_USER, DB_PASSWORD);
            initSchema(connection);
        }
        return connection;
    }

    private static void initSchema(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS chapters (
                    id          VARCHAR(100) PRIMARY KEY,
                    title       VARCHAR(255) NOT NULL,
                    created_at  BIGINT NOT NULL DEFAULT (UNIX_TIMESTAMP()),
                    CHECK (CHAR_LENGTH(TRIM(title)) > 0),
                    INDEX idx_chapters_created_at (created_at)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS chapter_components (
                    id              VARCHAR(100) PRIMARY KEY,
                    chapter_id      VARCHAR(100) NOT NULL,
                    component_type  VARCHAR(40) NOT NULL,
                    position        INT NOT NULL,
                    content         LONGTEXT NOT NULL,
                    CHECK (component_type IN ('LEARNING_OBJECTIVES','PERSIAN_DIALOGUE','PERSIAN_READING_PASSAGE',
                                              'URDU_EXPLANATION','GRAMMAR_NOTE','EXAMPLES','GLOSSARY')),
                    CHECK (position >= 0),
                    UNIQUE KEY uq_chapter_position (chapter_id, position),
                    CONSTRAINT fk_components_chapter FOREIGN KEY (chapter_id)
                        REFERENCES chapters(id) ON DELETE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);
        }
    }

    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException ignored) {
                // Nothing useful to do if close fails
            } finally {
                connection = null;
            }
        }
    }

    public static synchronized void reset() {
        closeConnection();
        databaseUrl = DEFAULT_DB_URL;
    }
}
