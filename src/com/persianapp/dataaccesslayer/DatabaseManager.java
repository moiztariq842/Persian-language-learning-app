package com.persianapp.dataaccesslayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Manages the MySQL/MariaDB database connection and schema for the full application.
 * Covers all four use cases: UC-01 chapters, UC-02 chapter_components (structure),
 * UC-03 component_contents, and UC-04 glossary_entries.
 *
 * Connection settings can be configured via db.properties, system properties, or environment variables:
 *   DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD
 */
public class DatabaseManager {

    private static final Properties fileProps   = loadProperties();
    private static String           dbUser      = getConfig("DB_USER",     "root");
    private static String           dbPassword  = getConfig("DB_PASSWORD", "");
    private static String           databaseUrl = buildDefaultUrl();
    private static Connection       connection;

    static {
        tryLoadDriver("com.mysql.cj.jdbc.Driver");
        tryLoadDriver("org.mariadb.jdbc.Driver");
    }

    private DatabaseManager() {}

    // Checks if the database server port is reachable AND the connection/credentials succeed.
    public static boolean isAvailable() {
        reloadConfig();
        String host = getConfig("DB_HOST", "localhost");
        int    port;
        try {
            port = Integer.parseInt(getConfig("DB_PORT", "3306"));
        } catch (NumberFormatException e) {
            port = 3306;
        }
        // 1. Fast socket connectivity check (fails in < 400ms if no DB is listening)
        try (Socket s = new Socket()) {
            s.connect(new InetSocketAddress(host, port), 400);
        } catch (Throwable t) {
            return false;
        }

        // 2. Fast credentials/login check (fails if password is wrong or database is unreachable)
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

    // Reads a configuration parameter from system properties, environment, or db.properties.
    private static String getConfig(String key, String defaultValue) {
        String val = System.getProperty(key);
        if (val != null && !val.isBlank()) return val.trim();
        val = System.getenv(key);
        if (val != null && !val.isBlank()) return val.trim();
        if (fileProps != null) {
            val = fileProps.getProperty(key);
            if (val == null) val = fileProps.getProperty(key.toLowerCase());
            if (val == null) val = fileProps.getProperty(key.toLowerCase().replace('_', '.'));
            if (val != null && !val.isBlank()) return val.trim();
        }
        return defaultValue;
    }

    // Loads properties from db.properties if present (working directory, resources, or classpath).
    private static Properties loadProperties() {
        Properties p = new Properties();
        File f = new File("db.properties");
        if (f.exists()) {
            try (FileInputStream fis = new FileInputStream(f)) {
                p.load(fis);
                return p;
            } catch (Exception ignored) {}
        }
        try (InputStream is = DatabaseManager.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (is != null) {
                p.load(is);
                return p;
            }
        } catch (Exception ignored) {}
        File res = new File("resources" + File.separator + "db.properties");
        if (res.exists()) {
            try (FileInputStream fis = new FileInputStream(res)) {
                p.load(fis);
                return p;
            } catch (Exception ignored) {}
        }
        return p;
    }

    // Reloads db.properties and refreshes active connection parameters.
    public static synchronized void reloadConfig() {
        closeConnection();
        Properties fresh = loadProperties();
        fileProps.clear();
        fileProps.putAll(fresh);
        dbUser      = getConfig("DB_USER",     "root");
        dbPassword  = getConfig("DB_PASSWORD", "");
        databaseUrl = buildDefaultUrl();
    }

    // Loads the MySQL JDBC driver class.
    private static void tryLoadDriver(String className) {
        try { Class.forName(className); } catch (ClassNotFoundException ignored) {}
    }
}
