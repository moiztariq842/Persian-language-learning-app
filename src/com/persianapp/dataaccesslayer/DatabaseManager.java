package com.persianapp.dataaccesslayer;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages MariaDB database connections for the application.
 *
 * <p>Reads connection settings from {@code db.properties} on the classpath.
 * No schema is created here – run {@code resources/schema.sql} once through
 * XAMPP / phpMyAdmin to initialise the tables.</p>
 *
 * <p>The connection can be overridden via {@link #setConnectionInfo} for
 * testing purposes.</p>
 */
public class DatabaseManager {

    private static final String PROPERTIES_FILE = "db.properties";

    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;
    private static Connection connection;
    private static boolean configured = false;

    private DatabaseManager() {
        // Utility class — no instantiation
    }

    // ── Configuration ────────────────────────────────────────

    /**
     * Loads connection settings from {@code db.properties} on the classpath.
     * Called automatically on first {@link #getConnection()} if not already
     * configured.
     */
    private static void loadProperties() {
        Properties props = new Properties();
        try (InputStream in = DatabaseManager.class.getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE)) {
            if (in == null) {
                throw new RuntimeException(
                        "Configuration file '" + PROPERTIES_FILE + "' not found on classpath. "
                        + "Make sure resources/ is on the classpath.");
            }
            props.load(in);
            dbUrl      = props.getProperty("db.url");
            dbUser     = props.getProperty("db.user", "root");
            dbPassword = props.getProperty("db.password", "");
            configured = true;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database configuration.", e);
        }
    }

    /**
     * Allows overriding the connection info programmatically (e.g., for tests).
     *
     * @param url      JDBC URL
     * @param user     database user
     * @param password database password
     */
    public static synchronized void setConnectionInfo(String url, String user, String password) {
        closeConnection();
        dbUrl      = url;
        dbUser     = user;
        dbPassword = password;
        configured = true;
    }

    // ── Connection ───────────────────────────────────────────

    /**
     * Returns a shared database connection. Creates one if necessary.
     *
     * @return a live {@link Connection}
     * @throws SQLException if a connection cannot be established
     */
    public static synchronized Connection getConnection() throws SQLException {
        if (!configured) {
            loadProperties();
        }
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        }
        return connection;
    }

    /**
     * Closes the current connection, if any.
     */
    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException ignored) {
                // best-effort close
            } finally {
                connection = null;
            }
        }
    }

    /**
     * Resets the manager to its initial state (closes connection, clears config).
     * Primarily for test tear-down.
     */
    public static synchronized void reset() {
        closeConnection();
        dbUrl      = null;
        dbUser     = null;
        dbPassword = null;
        configured = false;
    }
}
