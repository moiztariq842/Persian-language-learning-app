package com.persianapp.dataaccesslayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// Manages MySQL database connections and schema initialization.
public class DatabaseManager {

    private static String     dbUser      = getConfig("DB_USER",     "root");
    private static String     dbPassword  = getConfig("DB_PASSWORD", "");
    private static String     databaseUrl = buildDefaultUrl();
    private static Connection connection;

    static {
        tryLoadDriver("com.mysql.cj.jdbc.Driver");
    }

    private DatabaseManager() {}

    // Sets the MySQL database URL and credentials.
    public static synchronized void setDatabaseUrl(String newUrl) {
        closeConnection();
        databaseUrl = (newUrl != null && !newUrl.isBlank()) ? newUrl : buildDefaultUrl();
        dbUser      = getConfig("DB_USER",     "root");
        dbPassword  = getConfig("DB_PASSWORD", "");
    }

    // Opens and returns the MySQL database connection.
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

    // Creates tables and constraints in MySQL if they do not exist.
    static void initSchema(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
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
                "CREATE TABLE IF NOT EXISTS chapter_components (" +
                "  id         VARCHAR(36) NOT NULL," +
                "  chapter_id VARCHAR(36) NOT NULL," +
                "  type       ENUM('LEARNING_OBJECTIVES','DIALOGUE','READING_PASSAGE'," +
                "             'URDU_EXPLANATION','GRAMMAR_NOTE','EXAMPLES','GLOSSARY') NOT NULL," +
                "  sequence   INT         NOT NULL DEFAULT 1 CHECK(sequence >= 1)," +
                "  PRIMARY KEY (id)," +
                "  FOREIGN KEY (chapter_id) REFERENCES chapters(id) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS component_contents (" +
                "  id           VARCHAR(36) NOT NULL," +
                "  component_id VARCHAR(36) NOT NULL UNIQUE," +
                "  persian_text MEDIUMTEXT," +
                "  urdu_text    MEDIUMTEXT," +
                "  updated_at   BIGINT      NOT NULL DEFAULT (UNIX_TIMESTAMP())," +
                "  PRIMARY KEY (id)," +
                "  FOREIGN KEY (component_id) REFERENCES chapter_components(id) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS glossary_entries (" +
                "  id           VARCHAR(36)  NOT NULL," +
                "  chapter_id   VARCHAR(36)  NOT NULL," +
                "  persian_word VARCHAR(500) NOT NULL," +
                "  urdu_meaning VARCHAR(500) NOT NULL," +
                "  PRIMARY KEY (id)," +
                "  CONSTRAINT chk_persian_word CHECK (CHAR_LENGTH(TRIM(persian_word)) > 0)," +
                "  CONSTRAINT chk_urdu_meaning CHECK (CHAR_LENGTH(TRIM(urdu_meaning)) > 0)," +
                "  FOREIGN KEY (chapter_id) REFERENCES chapters(id) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci"
            );
        }
    }

    // Builds the default MySQL connection URL.
    private static String buildDefaultUrl() {
        String host = getConfig("DB_HOST", "localhost");
        String port = getConfig("DB_PORT", "3306");
        String name = getConfig("DB_NAME", "persian_app");
        return "jdbc:mysql://" + host + ":" + port + "/" + name +
               "?useUnicode=true&characterEncoding=UTF-8" +
               "&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
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
