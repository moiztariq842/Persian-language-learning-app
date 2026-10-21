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

/**
 * Integration tests for DatabaseManager.
 * Verifies schema correctness for all 4 UC tables using an in-memory SQLite DB.
 */
@DisplayName("DatabaseManager — Schema Tests (UC-01 to UC-04)")
public class DatabaseManagerTest {

    @BeforeEach
    void setUp() {
        // Fresh in-memory database per test; nothing persists between tests
        DatabaseManager.setDatabaseUrl("jdbc:sqlite::memory:");
    }

    @AfterEach
    void tearDown() {
        DatabaseManager.reset();
    }

    // ------------------------------------------------------------------
    // UC-01: chapters table
    // ------------------------------------------------------------------

    @Test
    @DisplayName("UC-01 — Should create chapters table and insert a valid chapter")
    void testInsertValidChapter() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        String id = UUID.randomUUID().toString();

        insert(conn, "INSERT INTO chapters (id, title) VALUES (?, ?)", id, "درس اول");

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT title FROM chapters WHERE id = ?")) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            assertTrue(rs.next(), "Chapter should exist");
            assertEquals("درس اول", rs.getString("title"));
        }
    }

    @Test
    @DisplayName("UC-01 — Should reject blank chapter title (BR-01)")
    void testBlankTitleRejected() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        assertThrows(SQLException.class, () ->
                insert(conn, "INSERT INTO chapters (id, title) VALUES (?, ?)",
                        UUID.randomUUID().toString(), "   "));
    }

    // ------------------------------------------------------------------
    // UC-02: chapter_components table
    // ------------------------------------------------------------------

    @Test
    @DisplayName("UC-02 — Should insert a DIALOGUE component linked to a chapter")
    void testInsertChapterComponent() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        String chapterId   = UUID.randomUUID().toString();
        String componentId = UUID.randomUUID().toString();

        insert(conn, "INSERT INTO chapters (id, title) VALUES (?, ?)", chapterId, "درس دوم");
        insert(conn, "INSERT INTO chapter_components (id, chapter_id, type, sequence) VALUES (?, ?, ?, ?)",
                componentId, chapterId, "DIALOGUE", "1");

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT type FROM chapter_components WHERE id = ?")) {
            ps.setString(1, componentId);
            ResultSet rs = ps.executeQuery();
            assertTrue(rs.next());
            assertEquals("DIALOGUE", rs.getString("type"));
        }
    }

    @Test
    @DisplayName("UC-02 — Should reject an invalid component type")
    void testInvalidComponentTypeRejected() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        String chapterId = UUID.randomUUID().toString();
        insert(conn, "INSERT INTO chapters (id, title) VALUES (?, ?)", chapterId, "Test");

        assertThrows(SQLException.class, () ->
                insert(conn, "INSERT INTO chapter_components (id, chapter_id, type, sequence) VALUES (?, ?, ?, ?)",
                        UUID.randomUUID().toString(), chapterId, "INVALID_TYPE", "1"));
    }

    // ------------------------------------------------------------------
    // UC-03: component_contents table
    // ------------------------------------------------------------------

    @Test
    @DisplayName("UC-03 — Should insert Persian and Urdu content for a component")
    void testInsertComponentContent() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        String chapterId   = UUID.randomUUID().toString();
        String componentId = UUID.randomUUID().toString();
        String contentId   = UUID.randomUUID().toString();

        insert(conn, "INSERT INTO chapters (id, title) VALUES (?, ?)", chapterId, "درس سوم");
        insert(conn, "INSERT INTO chapter_components (id, chapter_id, type, sequence) VALUES (?, ?, ?, ?)",
                componentId, chapterId, "READING_PASSAGE", "1");
        insert(conn, "INSERT INTO component_contents (id, component_id, persian_text, urdu_text) VALUES (?, ?, ?, ?)",
                contentId, componentId, "این کتاب است", "یہ کتاب ہے");

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT persian_text, urdu_text FROM component_contents WHERE id = ?")) {
            ps.setString(1, contentId);
            ResultSet rs = ps.executeQuery();
            assertTrue(rs.next());
            assertEquals("این کتاب است", rs.getString("persian_text"));
            assertEquals("یہ کتاب ہے", rs.getString("urdu_text"));
        }
    }

    // ------------------------------------------------------------------
    // UC-04: glossary_entries table
    // ------------------------------------------------------------------

    @Test
    @DisplayName("UC-04 — Should insert a valid glossary entry")
    void testInsertGlossaryEntry() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        String chapterId  = UUID.randomUUID().toString();
        String glossaryId = UUID.randomUUID().toString();

        insert(conn, "INSERT INTO chapters (id, title) VALUES (?, ?)", chapterId, "درس چهارم");
        insert(conn, "INSERT INTO glossary_entries (id, chapter_id, persian_word, urdu_meaning) VALUES (?, ?, ?, ?)",
                glossaryId, chapterId, "سلام", "سلام / ہیلو");

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT persian_word, urdu_meaning FROM glossary_entries WHERE id = ?")) {
            ps.setString(1, glossaryId);
            ResultSet rs = ps.executeQuery();
            assertTrue(rs.next());
            assertEquals("سلام",      rs.getString("persian_word"));
            assertEquals("سلام / ہیلو", rs.getString("urdu_meaning"));
        }
    }

    @Test
    @DisplayName("UC-04 — Should reject blank Persian word (BR-03)")
    void testBlankPersianWordRejected() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        String chapterId = UUID.randomUUID().toString();
        insert(conn, "INSERT INTO chapters (id, title) VALUES (?, ?)", chapterId, "Test");

        assertThrows(SQLException.class, () ->
                insert(conn, "INSERT INTO glossary_entries (id, chapter_id, persian_word, urdu_meaning) VALUES (?, ?, ?, ?)",
                        UUID.randomUUID().toString(), chapterId, "  ", "meaning"));
    }

    // ------------------------------------------------------------------
    // Cascade Delete — FR-CH-07
    // ------------------------------------------------------------------

    @Test
    @DisplayName("FR-CH-07 — Deleting a chapter cascades to components, contents and glossary")
    void testCascadeDeleteChapter() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        String chapterId   = UUID.randomUUID().toString();
        String componentId = UUID.randomUUID().toString();
        String contentId   = UUID.randomUUID().toString();
        String glossaryId  = UUID.randomUUID().toString();

        // Build full chain
        insert(conn, "INSERT INTO chapters (id, title) VALUES (?, ?)", chapterId, "Cascade Test");
        insert(conn, "INSERT INTO chapter_components (id, chapter_id, type, sequence) VALUES (?, ?, ?, ?)",
                componentId, chapterId, "GRAMMAR_NOTE", "1");
        insert(conn, "INSERT INTO component_contents (id, component_id, persian_text, urdu_text) VALUES (?, ?, ?, ?)",
                contentId, componentId, "است", "ہے");
        insert(conn, "INSERT INTO glossary_entries (id, chapter_id, persian_word, urdu_meaning) VALUES (?, ?, ?, ?)",
                glossaryId, chapterId, "کتاب", "کتاب");

        // Delete the chapter
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM chapters WHERE id = ?")) {
            ps.setString(1, chapterId);
            assertEquals(1, ps.executeUpdate());
        }

        // Verify all dependents are gone
        assertEquals(0, count(conn, "chapter_components",   "id", componentId));
        assertEquals(0, count(conn, "component_contents",   "id", contentId));
        assertEquals(0, count(conn, "glossary_entries",     "id", glossaryId));
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /** Generic INSERT with String parameters. */
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

    /** Returns the row count where a column equals a given value. */
    private int count(Connection conn, String table, String col, String val) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM " + table + " WHERE " + col + " = ?")) {
            ps.setString(1, val);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
