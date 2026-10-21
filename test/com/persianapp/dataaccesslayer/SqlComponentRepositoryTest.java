package com.persianapp.dataaccesslayer;

import com.persianapp.businesslayer.ChapterComponent;
import com.persianapp.businesslayer.ComponentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.persianapp.testsupport.TestDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UC-03: SqlComponentRepository")
class SqlComponentRepositoryTest {
    private static final String CHAPTER = "chapter-1";

    private SqlComponentRepository repo;

    @BeforeEach
    void setUp() throws SQLException {
        TestDatabase.start();
        repo = new SqlComponentRepository();
        insertChapter(CHAPTER, "Chapter 1");
    }

    @AfterEach
    void tearDown() {
        DatabaseManager.reset();
    }

    @Test
    @DisplayName("Persian and Urdu content survives a save and reload")
    void persianAndUrduRoundTrip() {
        String text = "سلام، ۱۲ سوال | خط دوم\nیہ اردو ہے۔";
        repo.add(new ChapterComponent("c1", CHAPTER, ComponentType.PERSIAN_DIALOGUE, 0, ""));
        repo.updateContent("c1", text);

        assertEquals(text, repo.findById("c1").orElseThrow().getContent());
    }

    @Test
    @DisplayName("Components load only for their chapter, in position order")
    void orderedByPositionPerChapter() throws SQLException {
        insertChapter("chapter-2", "Chapter 2");
        repo.add(new ChapterComponent("b", CHAPTER, ComponentType.GRAMMAR_NOTE, 2, "ب"));
        repo.add(new ChapterComponent("a", CHAPTER, ComponentType.EXAMPLES, 0, "الف"));
        repo.add(new ChapterComponent("z", "chapter-2", ComponentType.GLOSSARY, 0, "ز"));

        List<ChapterComponent> loaded = repo.findByChapter(CHAPTER);

        assertEquals(List.of("a", "b"), loaded.stream().map(ChapterComponent::getId).toList());
    }

    @Test
    @DisplayName("Updating content changes only the target component")
    void updateTouchesOnlyTarget() {
        repo.add(new ChapterComponent("c1", CHAPTER, ComponentType.PERSIAN_DIALOGUE, 0, "اول"));
        repo.add(new ChapterComponent("c2", CHAPTER, ComponentType.URDU_EXPLANATION, 1, "دوم"));

        repo.updateContent("c2", "نیا");

        assertEquals("اول", repo.findById("c1").orElseThrow().getContent());
        assertEquals("نیا", repo.findById("c2").orElseThrow().getContent());
    }

    @Test
    @DisplayName("Updating an unknown component fails")
    void updateUnknownFails() {
        assertThrows(NoSuchElementException.class, () -> repo.updateContent("missing", "متن"));
    }

    @Test
    @DisplayName("Deleting a chapter also deletes its components (FR-CH-07)")
    void deletingChapterCascades() throws SQLException {
        repo.add(new ChapterComponent("c1", CHAPTER, ComponentType.PERSIAN_DIALOGUE, 0, "سلام"));

        Connection conn = DatabaseManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM chapters WHERE id = ?")) {
            ps.setString(1, CHAPTER);
            ps.executeUpdate();
        }

        assertTrue(repo.findByChapter(CHAPTER).isEmpty());
    }

    @Test
    @DisplayName("Component without an existing chapter is rejected")
    void rejectsOrphanComponent() {
        assertThrows(DataAccessException.class,
                () -> repo.add(new ChapterComponent("c9", "no-such-chapter", ComponentType.GLOSSARY, 0, "x")));
    }

    private void insertChapter(String id, String title) throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO chapters (id, title) VALUES (?, ?)")) {
            ps.setString(1, id);
            ps.setString(2, title);
            ps.executeUpdate();
        }
    }
}
