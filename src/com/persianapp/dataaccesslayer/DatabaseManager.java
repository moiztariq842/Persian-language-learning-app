package com.persianapp.dataaccesslayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages the MySQL/MariaDB database connection and schema for the full application.
 * Covers all four use cases: UC-01 chapters, UC-02 chapter_components (structure),
 * UC-03 component_contents, and UC-04 glossary_entries.
 *
 * Connection settings can be overridden via system properties or environment variables:
 *   DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD
 */
public class DatabaseManager {

    private static String     dbUser      = getConfig("DB_USER",     "root");
    private static String     dbPassword  = getConfig("DB_PASSWORD", "");
    private static String     databaseUrl = buildDefaultUrl();
    private static Connection connection;

    static {
        tryLoadDriver("com.mysql.cj.jdbc.Driver");
        tryLoadDriver("org.mariadb.jdbc.Driver");
    }

    private DatabaseManager() {}

    // Checks if the database server is currently reachable.
    public static boolean isAvailable() {
        try {
            DriverManager.setLoginTimeout(2);
            try (Connection testConn = DriverManager.getConnection(databaseUrl, dbUser, dbPassword)) {
                return testConn != null && !testConn.isClosed();
            }
        } catch (Throwable t) {
            return false;
        }
    }

    // Sets the database URL and resets credentials from env/props.
    public static synchronized void setDatabaseUrl(String newUrl) {
        closeConnection();
        databaseUrl = (newUrl != null && !newUrl.isBlank()) ? newUrl : buildDefaultUrl();
        dbUser      = getConfig("DB_USER",     "root");
        dbPassword  = getConfig("DB_PASSWORD", "");
    }

    // Opens and returns the database connection, initialising the schema if needed.
    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(databaseUrl, dbUser, dbPassword);
            connection.setAutoCommit(true);
            initSchema(connection);
        }
        return connection;
    }

    // Closes the open database connection.
    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) connection.close();
            } catch (SQLException ignored) {
            } finally {
                connection = null;
            }
        }
    }

    // Resets the connection and credentials back to defaults.
    public static synchronized void reset() {
        closeConnection();
        databaseUrl = buildDefaultUrl();
        dbUser      = getConfig("DB_USER",     "root");
        dbPassword  = getConfig("DB_PASSWORD", "");
    }

    // Creates all tables and indexes if they do not exist (all four UCs).
    static void initSchema(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {

            // UC-01: chapters
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS chapters (" +
                "  id         VARCHAR(36)  NOT NULL," +
                "  title      VARCHAR(500) NOT NULL," +
                "  created_at BIGINT       NOT NULL DEFAULT (UNIX_TIMESTAMP())," +
                "  PRIMARY KEY (id)," +
                "  CONSTRAINT chk_chapter_title CHECK (CHAR_LENGTH(TRIM(title)) > 0)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci"
            );
            stmt.execute(
                "CREATE INDEX IF NOT EXISTS idx_chapters_created_at ON chapters(created_at ASC)"
            );

            // UC-02: chapter_components (structure — type + sort order, no content)
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS chapter_components (" +
                "  id           VARCHAR(36)  NOT NULL," +
                "  chapter_id   VARCHAR(36)  NOT NULL," +
                "  component_type ENUM(" +
                "    'LEARNING_OBJECTIVES','PERSIAN_DIALOGUE','PERSIAN_READING_PASSAGE'," +
                "    'URDU_EXPLANATION','GRAMMAR_NOTE','EXAMPLES','GLOSSARY'" +
                "  ) NOT NULL," +
                "  sort_order   INT          NOT NULL DEFAULT 1," +
                "  content      LONGTEXT     NOT NULL DEFAULT ''," +
                "  PRIMARY KEY (id)," +
                "  FOREIGN KEY (chapter_id) REFERENCES chapters(id) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci"
            );
            stmt.execute(
                "CREATE INDEX IF NOT EXISTS idx_chapter_components_chapter_seq " +
                "ON chapter_components(chapter_id, sort_order ASC)"
            );

            // UC-04: glossary_entries
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS glossary_entries (" +
                "  id           VARCHAR(36)  NOT NULL," +
                "  chapter_id   VARCHAR(36)  NOT NULL," +
                "  persian_word VARCHAR(500) NOT NULL," +
                "  urdu_meaning VARCHAR(500) NOT NULL," +
                "  created_at   BIGINT       NOT NULL DEFAULT (UNIX_TIMESTAMP())," +
                "  PRIMARY KEY (id)," +
                "  CONSTRAINT chk_persian_word CHECK (CHAR_LENGTH(TRIM(persian_word)) > 0)," +
                "  CONSTRAINT chk_urdu_meaning CHECK (CHAR_LENGTH(TRIM(urdu_meaning)) > 0)," +
                "  FOREIGN KEY (chapter_id) REFERENCES chapters(id) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci"
            );
            stmt.execute(
                "CREATE INDEX IF NOT EXISTS idx_glossary_entries_chapter_id " +
                "ON glossary_entries(chapter_id)"
            );
        }
    }

    // Builds the default MySQL connection URL from config.
    private static String buildDefaultUrl() {
        String host = getConfig("DB_HOST", "localhost");
        String port = getConfig("DB_PORT", "3306");
        String name = getConfig("DB_NAME", "persian_app");
        return "jdbc:mysql://" + host + ":" + port + "/" + name +
               "?useUnicode=true&characterEncoding=UTF-8" +
               "&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" +
               "&createDatabaseIfNotExist=true";
    }

    // Reads a configuration parameter from system properties or environment.
    private static String getConfig(String key, String defaultValue) {
        String val = System.getProperty(key);
        if (val != null && !val.isBlank()) return val;
        val = System.getenv(key);
        return (val != null && !val.isBlank()) ? val : defaultValue;
    }

    // Loads the MySQL JDBC driver class.
    private static void tryLoadDriver(String className) {
        try { Class.forName(className); } catch (ClassNotFoundException ignored) {}
    }
}
