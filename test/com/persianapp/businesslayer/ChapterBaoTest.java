package com.persianapp.businesslayer;

import com.persianapp.businesslayer.stubs.InMemoryChapterDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ChapterBao Tests")
class ChapterBaoTest {

    private ChapterBao bao;

    @BeforeEach
    void setUp() {
        bao = new ChapterBao(new InMemoryChapterDao());
    }

    @Test
    @DisplayName("Valid title creates chapter with ID")
    void createChapter_validTitle_returnsSavedChapter() {
        Chapter c = bao.createChapter("درس اول");
        assertNotNull(c.getId());
        assertEquals("درس اول", c.getTitle());
    }

    @Test
    @DisplayName("Null title throws BLANK_TITLE error")
    void createChapter_nullTitle_throws() {
        ChapterException ex = assertThrows(ChapterException.class, () -> bao.createChapter(null));
        assertEquals(ChapterErrorCode.BLANK_TITLE, ex.getCode());
    }

    @Test
    @DisplayName("Blank title throws BLANK_TITLE error")
    void createChapter_blankTitle_throws() {
        ChapterException ex = assertThrows(ChapterException.class, () -> bao.createChapter("  "));
        assertEquals(ChapterErrorCode.BLANK_TITLE, ex.getCode());
    }

    @Test
    @DisplayName("Two chapters get distinct IDs")
    void createChapter_twoCalls_distinctIds() {
        Chapter a = bao.createChapter("Chapter A");
        Chapter b = bao.createChapter("Chapter B");
        assertNotEquals(a.getId(), b.getId());
    }

    @Test
    @DisplayName("Returns all chapters in creation order")
    void getAllChapters_returnsInOrder() {
        bao.createChapter("First");
        bao.createChapter("Second");
        List<Chapter> list = bao.getAllChapters();
        assertEquals(2, list.size());
        assertEquals("First",  list.get(0).getTitle());
        assertEquals("Second", list.get(1).getTitle());
    }

    @Test
    @DisplayName("Returns chapter by valid ID")
    void findChapter_validId_returnsChapter() {
        Chapter c = bao.createChapter("درس دوم");
        Chapter found = bao.findChapter(c.getId());
        assertEquals(c.getId(), found.getId());
        assertEquals("درس دوم", found.getTitle());
    }

    @Test
    @DisplayName("Unknown ID throws CHAPTER_NOT_FOUND error")
    void findChapter_unknownId_throws() {
        ChapterException ex = assertThrows(ChapterException.class, () -> bao.findChapter("no-such-id"));
        assertEquals(ChapterErrorCode.CHAPTER_NOT_FOUND, ex.getCode());
    }

    @Test
    @DisplayName("Valid title updates chapter title")
    void renameChapter_validTitle_updatesTitle() {
        Chapter c = bao.createChapter("Old");
        bao.renameChapter(c.getId(), "New");
        assertEquals("New", bao.findChapter(c.getId()).getTitle());
    }

    @Test
    @DisplayName("Blank title leaves existing title unchanged")
    void renameChapter_blankTitle_throws() {
        Chapter c = bao.createChapter("Stable");
        assertThrows(ChapterException.class, () -> bao.renameChapter(c.getId(), "  "));
        assertEquals("Stable", bao.findChapter(c.getId()).getTitle());
    }

    @Test
    @DisplayName("Delete removes chapter from store")
    void deleteChapter_existingId_removes() {
        Chapter c = bao.createChapter("To Delete");
        bao.deleteChapter(c.getId());
        assertEquals(0, bao.getAllChapters().size());
    }

    @Test
    @DisplayName("Null DAO in constructor throws exception")
    void constructor_nullDao_throws() {
        assertThrows(IllegalArgumentException.class, () -> new ChapterBao(null));
    }
}
