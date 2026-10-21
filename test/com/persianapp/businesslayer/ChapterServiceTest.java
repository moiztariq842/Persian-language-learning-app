package com.persianapp.businesslayer;

import com.persianapp.businesslayer.stubs.InMemoryChapterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ChapterService — UC-01 Operations")
class ChapterServiceTest {

    private InMemoryChapterRepository repo;
    private ChapterService service;

    @BeforeEach
    void setUp() {
        repo    = new InMemoryChapterRepository();
        service = new ChapterService(repo);
    }

    // ---- create --------------------------------------------------------

    @Test
    @DisplayName("Valid title: chapter is persisted and returned with a generated ID")
    void createChapter_validTitle_persistsAndReturns() {
        Chapter c = service.createChapter("Chapter 1");

        assertNotNull(c.getId());
        assertEquals("Chapter 1", c.getTitle());
        assertEquals(1, repo.size());
    }

    @Test
    @DisplayName("Two chapters get distinct IDs (FR-CH-02)")
    void createChapter_twoChapters_uniqueIds() {
        Chapter a = service.createChapter("First");
        Chapter b = service.createChapter("Second");

        assertNotEquals(a.getId(), b.getId());
    }

    @Test
    @DisplayName("Blank title: throws and nothing is persisted (BR-01, NFR-RL-02)")
    void createChapter_blankTitle_throwsAndDoesNotPersist() {
        assertThrows(IllegalArgumentException.class, () -> service.createChapter("  "));
        assertEquals(0, repo.size());
    }

    @Test
    @DisplayName("Null title: throws and nothing is persisted (BR-01)")
    void createChapter_nullTitle_throwsAndDoesNotPersist() {
        assertThrows(IllegalArgumentException.class, () -> service.createChapter(null));
        assertEquals(0, repo.size());
    }

    // ---- list ----------------------------------------------------------

    @Test
    @DisplayName("Returns all chapters in insertion order (FR-CH-03)")
    void listChapters_returnsAllInOrder() {
        service.createChapter("A");
        service.createChapter("B");
        service.createChapter("C");

        List<Chapter> list = service.listChapters();

        assertEquals(3, list.size());
        assertEquals("A", list.get(0).getTitle());
        assertEquals("B", list.get(1).getTitle());
        assertEquals("C", list.get(2).getTitle());
    }

    // ---- open ----------------------------------------------------------

    @Test
    @DisplayName("Valid ID: correct chapter is returned (FR-CH-04)")
    void openChapter_existingId_returnsChapter() {
        Chapter created = service.createChapter("Greetings");
        Chapter found   = service.openChapter(created.getId());

        assertEquals(created.getId(), found.getId());
    }

    @Test
    @DisplayName("Unknown ID: throws IllegalArgumentException")
    void openChapter_unknownId_throws() {
        assertThrows(IllegalArgumentException.class,
            () -> service.openChapter("nonexistent-id"));
    }

    // ---- rename --------------------------------------------------------

    @Test
    @DisplayName("Valid new title: stored title is updated (FR-CH-05)")
    void renameChapter_validTitle_updatesStoredTitle() {
        Chapter c = service.createChapter("Old Name");
        service.renameChapter(c.getId(), "New Name");

        assertEquals("New Name", repo.findById(c.getId()).orElseThrow().getTitle());
    }

    @Test
    @DisplayName("Blank new title: throws and existing title is unchanged (BR-06)")
    void renameChapter_blankNewTitle_doesNotChangeExisting() {
        Chapter c = service.createChapter("Valid Title");

        assertThrows(IllegalArgumentException.class,
            () -> service.renameChapter(c.getId(), ""));

        assertEquals("Valid Title", repo.findById(c.getId()).orElseThrow().getTitle());
    }

    // ---- delete --------------------------------------------------------

    @Test
    @DisplayName("Existing chapter is removed after deletion (FR-CH-06)")
    void deleteChapter_removesChapter() {
        Chapter c = service.createChapter("To Delete");
        service.deleteChapter(c.getId());

        assertTrue(repo.findById(c.getId()).isEmpty());
        assertEquals(0, repo.size());
    }
}
