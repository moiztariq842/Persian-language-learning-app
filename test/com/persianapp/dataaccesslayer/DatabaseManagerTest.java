package com.persianapp.dataaccesslayer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UC-01: DatabaseManager Tests")
public class DatabaseManagerTest {

    private static final String TEST_DB_URL = "jdbc:sqlite::memory:";

    @BeforeEach
    void setUp() {
        DatabaseManager.setDatabaseUrl(TEST_DB_URL);
    }

    @AfterEach
    void tearDown() {
        DatabaseManager.reset();
    }

    @Test
    @DisplayName("Should establish connection and initialize chapters table")
    void testConnectionAndSchema() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        assertNotNull(conn);
        assertFalse(conn.isClosed());
    }

    @Test
    @DisplayName("Should insert and query a valid chapter record")
    void testInsertValidChapter() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        String chapterId = UUID.randomUUID().toString();
        String title = "Chapter 1: Persian Alphabet";

        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO chapters (id, title) VALUES (?, ?)")) {
            ps.setString(1, chapterId);
            ps.setString(2, title);
            int rows = ps.executeUpdate();
            assertEquals(1, rows);
        }

        try (PreparedStatement ps = conn.prepareStatement("SELECT id, title FROM chapters WHERE id = ?")) {
            ps.setString(1, chapterId);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(title, rs.getString("title"));
            }
        }
    }

    @Test
    @DisplayName("Should reject blank chapter title via CHECK constraint (BR-01)")
    void testRejectBlankTitle() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        String chapterId = UUID.randomUUID().toString();

        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO chapters (id, title) VALUES (?, ?)")) {
            ps.setString(1, chapterId);
            ps.setString(2, "   ");
            assertThrows(SQLException.class, ps::executeUpdate);
        }
    }
}
