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

// Tests MySQL schema creation and table constraints.
@DisplayName("DatabaseManager — MySQL Tests")
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
    @DisplayName("Should insert a chapter component linked to a chapter")
    void testInsertChapterComponent() throws SQLException {
        String chapterId   = UUID.randomUUID().toString();
        String componentId = UUID.randomUUID().toString();

        insert(conn, "INSERT INTO chapters (id, title) VALUES (?, ?)", chapterId, "درس دوم");
        try {
            insert(conn, "INSERT INTO chapter_components (id, chapter_id, type, sequence) VALUES (?, ?, ?, ?)",
                   componentId, chapterId, "DIALOGUE", "1");

            try (PreparedStatement ps = conn.prepareStatement("SELECT type FROM chapter_components WHERE id = ?")) {
                ps.setString(1, componentId);
                ResultSet rs = ps.executeQuery();
                assertTrue(rs.next());
                assertEquals("DIALOGUE", rs.getString("type"));
            }
        } finally {
            delete(conn, "DELETE FROM chapters WHERE id = ?", chapterId);
        }
    }

    @Test
    @DisplayName("Should reject an invalid component type in MySQL")
    void testInvalidComponentTypeRejected() throws SQLException {
        String chapterId = UUID.randomUUID().toString();
        insert(conn, "INSERT INTO chapters (id, title) VALUES (?, ?)", chapterId, "Test");

        try {
            assertThrows(SQLException.class, () ->
                insert(conn, "INSERT INTO chapter_components (id, chapter_id, type, sequence) VALUES (?, ?, ?, ?)",
                       UUID.randomUUID().toString(), chapterId, "INVALID_TYPE", "1"));
        } finally {
            delete(conn, "DELETE FROM chapters WHERE id = ?", chapterId);
        }
    }

    @Test
    @DisplayName("Deleting a chapter cascades in MySQL")
    void testCascadeDeleteChapter() throws SQLException {
        String chapterId   = UUID.randomUUID().toString();
        String componentId = UUID.randomUUID().toString();
        String contentId   = UUID.randomUUID().toString();
        String glossaryId  = UUID.randomUUID().toString();

        insert(conn, "INSERT INTO chapters (id, title) VALUES (?, ?)", chapterId, "Cascade Test");
        insert(conn, "INSERT INTO chapter_components (id, chapter_id, type, sequence) VALUES (?, ?, ?, ?)",
               componentId, chapterId, "GRAMMAR_NOTE", "1");
        insert(conn, "INSERT INTO component_contents (id, component_id, persian_text, urdu_text) VALUES (?, ?, ?, ?)",
               contentId, componentId, "است", "ہے");
        insert(conn, "INSERT INTO glossary_entries (id, chapter_id, persian_word, urdu_meaning) VALUES (?, ?, ?, ?)",
               glossaryId, chapterId, "کتاب", "کتاب");

        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM chapters WHERE id = ?")) {
            ps.setString(1, chapterId);
            assertEquals(1, ps.executeUpdate());
        }

        assertEquals(0, count(conn, "chapter_components", "id", componentId));
        assertEquals(0, count(conn, "component_contents", "id", contentId));
        assertEquals(0, count(conn, "glossary_entries", "id", glossaryId));
    }

    private void insert(Connection conn, String sql, String... params) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                if (params[i].matches("\\d+")) {
                    ps.setInt(i + 1, Integer.parseInt(params[i]));
                } else {
                    ps.setString(i + 1, params[i]);
                }
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

    private int count(Connection conn, String table, String col, String val) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM " + table + " WHERE " + col + " = ?")) {
            ps.setString(1, val);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
