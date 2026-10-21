package com.persianapp.dataaccesslayer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

// Tests MySQL chapters table schema and constraints for UC-01.
@DisplayName("DatabaseManager — UC-01 MySQL Tests")
public class DatabaseManagerTest {

    private Connection conn;

    @BeforeEach
    void setUp() {
        try {
            conn = DatabaseManager.getConnection();
        } catch (SQLException e) {
            Assumptions.abort("MySQL connection not available with current credentials: " + e.getMessage());
        }
    }

    @AfterEach
    void tearDown() {
        DatabaseManager.closeConnection();
    }

    @Test
    @DisplayName("Should insert and retrieve a chapter from MySQL")
    void testInsertValidChapter() throws SQLException {
        String id = UUID.randomUUID().toString();
        insert(conn, "INSERT INTO chapters (id, title) VALUES (?, ?)", id, "درس اول");

        try (PreparedStatement ps = conn.prepareStatement("SELECT title FROM chapters WHERE id = ?")) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            assertTrue(rs.next());
            assertEquals("درس اول", rs.getString("title"));
        } finally {
            delete(conn, "DELETE FROM chapters WHERE id = ?", id);
        }
    }

    @Test
    @DisplayName("Should reject blank chapter title in MySQL")
    void testBlankTitleRejected() {
        assertThrows(SQLException.class, () ->
            insert(conn, "INSERT INTO chapters (id, title) VALUES (?, ?)",
                   UUID.randomUUID().toString(), "   "));
    }

    @Test
    @DisplayName("Should update chapter title in MySQL")
    void testUpdateChapterTitle() throws SQLException {
        String id = UUID.randomUUID().toString();
        insert(conn, "INSERT INTO chapters (id, title) VALUES (?, ?)", id, "Original");

        try {
            try (PreparedStatement ps = conn.prepareStatement("UPDATE chapters SET title = ? WHERE id = ?")) {
                ps.setString(1, "Updated");
                ps.setString(2, id);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement("SELECT title FROM chapters WHERE id = ?")) {
                ps.setString(1, id);
                ResultSet rs = ps.executeQuery();
                assertTrue(rs.next());
                assertEquals("Updated", rs.getString("title"));
            }
        } finally {
            delete(conn, "DELETE FROM chapters WHERE id = ?", id);
        }
    }

    @Test
    @DisplayName("Should delete chapter from MySQL")
    void testDeleteChapter() throws SQLException {
        String id = UUID.randomUUID().toString();
        insert(conn, "INSERT INTO chapters (id, title) VALUES (?, ?)", id, "To Delete");

        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM chapters WHERE id = ?")) {
            ps.setString(1, id);
            assertEquals(1, ps.executeUpdate());
        }

        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM chapters WHERE id = ?")) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            assertTrue(rs.next());
            assertEquals(0, rs.getInt(1));
        }
    }

    private void insert(Connection conn, String sql, String... params) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }
            ps.executeUpdate();
        }
    }

    private void delete(Connection conn, String sql, String id) {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException ignored) {}
    }
}
