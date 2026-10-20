package com.persianapp.dataaccesslayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages SQLite database connection and schema initialization for UC-01.
 */
public class DatabaseManager {

    public static final String DEFAULT_DB_URL = "jdbc:sqlite:persian_app.db";
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
            connection = DriverManager.getConnection(databaseUrl);
            initSchema(connection);
        }
        return connection;
    }

    private static void initSchema(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS chapters (
                    id          TEXT PRIMARY KEY,
                    title       TEXT NOT NULL CHECK(length(trim(title)) > 0),
                    created_at  INTEGER NOT NULL DEFAULT (strftime('%s', 'now'))
                );
            """);
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_chapters_created_at ON chapters(created_at ASC);");

            // UC-04: Glossary Management table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS glossary_entries (
                    id           TEXT PRIMARY KEY,
                    chapter_id   TEXT NOT NULL,
                    persian_word TEXT NOT NULL CHECK(length(trim(persian_word)) > 0),
                    urdu_meaning TEXT NOT NULL CHECK(length(trim(urdu_meaning)) > 0),
                    FOREIGN KEY (chapter_id) REFERENCES chapters(id) ON DELETE CASCADE
                );
            """);
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_glossary_chapter_id ON glossary_entries(chapter_id);");
        }
    }

    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException ignored) {
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
