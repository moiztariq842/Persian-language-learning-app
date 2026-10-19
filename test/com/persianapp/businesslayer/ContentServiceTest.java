package com.persianapp.businesslayer;

import com.persianapp.testsupport.StubComponentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UC-03: ContentService use case")
class ContentServiceTest {
    private static final String CHAPTER = "chapter-1";

    private static ChapterComponent dialogue() {
        return new ChapterComponent("c1", CHAPTER, ComponentType.PERSIAN_DIALOGUE, 0, "سلام");
    }

    private static ChapterComponent explanation() {
        return new ChapterComponent("c2", CHAPTER, ComponentType.URDU_EXPLANATION, 1, "تشریح");
    }

    private static ContentService serviceWith(StubComponentRepository repo) {
        return new ContentService(repo, new ContentValidator());
    }

    @Test
    @DisplayName("Valid edit replaces content and persists it")
    void validEditPersists() {
        StubComponentRepository repo = new StubComponentRepository(dialogue());
        ChapterComponent updated = serviceWith(repo).saveContent("c1", "خداحافظ");

        assertEquals("خداحافظ", updated.getContent());
        assertEquals("خداحافظ", repo.peek("c1").getContent());
        assertEquals(1, repo.getUpdateCount());
    }

    @Test
    @DisplayName("Blank edit is rejected and last valid content is kept (BR-06)")
    void blankEditKeepsLastValidContent() {
        StubComponentRepository repo = new StubComponentRepository(dialogue());

        assertThrows(ContentValidationException.class, () -> serviceWith(repo).saveContent("c1", "   "));

        assertEquals("سلام", repo.peek("c1").getContent());
        assertEquals(0, repo.getUpdateCount());
    }

    @Test
    @DisplayName("Editing one component leaves the others unchanged")
    void editingOneLeavesOthersAlone() {
        StubComponentRepository repo = new StubComponentRepository(dialogue(), explanation());
        serviceWith(repo).saveContent("c2", "نیا تشریح");

        assertEquals("سلام", repo.peek("c1").getContent());
        assertEquals("نیا تشریح", repo.peek("c2").getContent());
    }

    @Test
    @DisplayName("Components are returned for one chapter in position order")
    void loadsOrderedComponentsForChapter() {
        ChapterComponent other = new ChapterComponent("x1", "chapter-2", ComponentType.EXAMPLES, 0, "مثال");
        StubComponentRepository repo = new StubComponentRepository(explanation(), dialogue(), other);

        List<ChapterComponent> loaded = serviceWith(repo).loadComponents(CHAPTER);

        assertEquals(2, loaded.size());
        assertEquals("c1", loaded.get(0).getId());
        assertEquals("c2", loaded.get(1).getId());
    }

    @Test
    @DisplayName("Unknown component cannot be loaded or saved")
    void unknownComponentFails() {
        StubComponentRepository repo = new StubComponentRepository(dialogue());
        ContentService service = serviceWith(repo);

        assertThrows(NoSuchElementException.class, () -> service.loadComponent("missing"));
        assertThrows(NoSuchElementException.class, () -> service.saveContent("missing", "متن"));
        assertEquals(0, repo.getUpdateCount());
    }

    @Test
    @DisplayName("Persian and Urdu text is preserved exactly")
    void preservesTextExactly() {
        String mixed = "یہ ایک مثال ہے۔ ۱۲ سوال — ۳ نکات";
        StubComponentRepository repo = new StubComponentRepository(dialogue());
        serviceWith(repo).saveContent("c1", mixed);

        assertEquals(mixed, repo.peek("c1").getContent());
    }
}
