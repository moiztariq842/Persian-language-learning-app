package com.persianapp.dataaccesslayer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-01: DatabaseManager connection tests.
 *
 * <p>These tests require a running MariaDB/MySQL instance via XAMPP
 * with the {@code persian_app} database created (see {@code resources/schema.sql}).
 * If the database is not available, the tests will fail with a connection error.</p>
 */
@DisplayName("UC-01: DatabaseManager Tests")
public class DatabaseManagerTest {

    @BeforeEach
    void setUp() {
        // Uses db.properties from classpath (resources/db.properties)
        // Make sure XAMPP MariaDB is running and persian_app DB is created
    }

    @AfterEach
    void tearDown() {
        DatabaseManager.reset();
    }

    @Test
    @DisplayName("Should establish connection to MariaDB")
    void testConnectionEstablished() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        assertNotNull(conn);
        assertFalse(conn.isClosed());
    }

    @Test
    @DisplayName("Should return the same connection on subsequent calls")
    void testConnectionReused() throws SQLException {
        Connection conn1 = DatabaseManager.getConnection();
        Connection conn2 = DatabaseManager.getConnection();
        assertSame(conn1, conn2);
    }

    @Test
    @DisplayName("Should close connection successfully")
    void testCloseConnection() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        assertFalse(conn.isClosed());

        DatabaseManager.closeConnection();
        assertTrue(conn.isClosed());
    }

    @Test
    @DisplayName("Should reconnect after connection is closed")
    void testReconnectAfterClose() throws SQLException {
        Connection conn1 = DatabaseManager.getConnection();
        DatabaseManager.closeConnection();
        assertTrue(conn1.isClosed());

        Connection conn2 = DatabaseManager.getConnection();
        assertNotNull(conn2);
        assertFalse(conn2.isClosed());
        assertNotSame(conn1, conn2);
    }

    @Test
    @DisplayName("Should allow overriding connection info")
    void testSetConnectionInfo() throws SQLException {
        // Override with the same settings — just verifying the API works
        DatabaseManager.setConnectionInfo(
                "jdbc:mariadb://localhost:3306/persian_app?useUnicode=true&characterEncoding=utf8mb4",
                "root", "");
        Connection conn = DatabaseManager.getConnection();
        assertNotNull(conn);
        assertFalse(conn.isClosed());
    }
}
