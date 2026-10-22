package com.persianapp.businesslayer;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link GlossaryService} (UC-04).
 *
 * <p>Uses {@link InMemoryGlossaryEntryDAO} — a hand-written stub with no
 * Mockito (NFR-MT-03). Covers acceptance criteria 8 and 9 from SRS §13.</p>
 */
@DisplayName("UC-04: GlossaryService Tests")
public class GlossaryServiceTest {

    private InMemoryGlossaryEntryDAO dao;
    private GlossaryService service;

    private static final String CHAPTER_ID = "chapter-001";

    @BeforeEach
    void setUp() {
        dao = new InMemoryGlossaryEntryDAO();
        dao.addChapter(CHAPTER_ID);
        service = new GlossaryService(dao);
    }

    // ── FR-GL-02: Add valid entry ────────────────────────────

    @Test
    @DisplayName("Should add a valid glossary entry with Persian word and Urdu meaning")
    void testAddValidEntry() throws GlossaryException {
        GlossaryEntry entry = service.addEntry(CHAPTER_ID, "سلام", "السلام علیکم");

        assertNotNull(entry);
        assertNotNull(entry.getId());
        assertFalse(entry.getId().isBlank());
        assertEquals(CHAPTER_ID, entry.getChapterId());
        assertEquals("سلام", entry.getPersianWord());
        assertEquals("السلام علیکم", entry.getUrduMeaning());
        assertEquals(1, dao.size());
    }

    @Test
    @DisplayName("Should trim leading/trailing spaces from Persian word and Urdu meaning")
    void testAddEntryTrimsWhitespace() throws GlossaryException {
        GlossaryEntry entry = service.addEntry(CHAPTER_ID, "  سلام  ", "  السلام علیکم  ");

        assertEquals("سلام", entry.getPersianWord());
        assertEquals("السلام علیکم", entry.getUrduMeaning());
    }

    // ── BR-03: Missing Persian word ──────────────────────────

    @Test
    @DisplayName("Should reject entry with null Persian word (BR-03)")
    void testRejectNullPersianWord() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addEntry(CHAPTER_ID, null, "معنی"));
    }

    @Test
    @DisplayName("Should reject entry with empty Persian word (BR-03)")
    void testRejectEmptyPersianWord() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addEntry(CHAPTER_ID, "", "معنی"));
    }

    @Test
    @DisplayName("Should reject entry with whitespace-only Persian word (BR-03)")
    void testRejectWhitespaceOnlyPersianWord() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addEntry(CHAPTER_ID, "   ", "معنی"));
    }

    // ── BR-03: Missing Urdu meaning ─────────────────────────

    @Test
    @DisplayName("Should reject entry with null Urdu meaning (BR-03)")
    void testRejectNullUrduMeaning() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addEntry(CHAPTER_ID, "سلام", null));
    }

    @Test
    @DisplayName("Should reject entry with empty Urdu meaning (BR-03)")
    void testRejectEmptyUrduMeaning() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addEntry(CHAPTER_ID, "سلام", ""));
    }

    @Test
    @DisplayName("Should reject entry with whitespace-only Urdu meaning (BR-03)")
    void testRejectWhitespaceOnlyUrduMeaning() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addEntry(CHAPTER_ID, "سلام", "   "));
    }

    // ── FR-GL-06: Chapter association ────────────────────────

    @Test
    @DisplayName("Should reject entry with non-existent chapter ID (FR-GL-06)")
    void testRejectNonExistentChapter() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addEntry("non-existent-chapter", "سلام", "ہیلو"));
    }

    @Test
    @DisplayName("Should reject entry with null chapter ID")
    void testRejectNullChapterId() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addEntry(null, "سلام", "ہیلو"));
    }

    @Test
    @DisplayName("Should reject entry with blank chapter ID")
    void testRejectBlankChapterId() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addEntry("   ", "سلام", "ہیلو"));
    }

    // ── FR-GL-03: Retrieve entries ──────────────────────────

    @Test
    @DisplayName("Should retrieve all entries for a chapter")
    void testGetEntries() throws GlossaryException {
        service.addEntry(CHAPTER_ID, "سلام", "ہیلو");
        service.addEntry(CHAPTER_ID, "خدا حافظ", "الوداع");

        List<GlossaryEntry> entries = service.getEntries(CHAPTER_ID);
        assertEquals(2, entries.size());
    }

    @Test
    @DisplayName("Should return empty list for chapter with no entries")
    void testGetEntriesEmpty() throws GlossaryException {
        List<GlossaryEntry> entries = service.getEntries(CHAPTER_ID);
        assertTrue(entries.isEmpty());
    }

    // ── FR-GL-04: Edit entry ────────────────────────────────

    @Test
    @DisplayName("Should update an existing entry's Persian word and Urdu meaning")
    void testUpdateEntry() throws GlossaryException {
        GlossaryEntry original = service.addEntry(CHAPTER_ID, "سلام", "ہیلو");

        GlossaryEntry updated = new GlossaryEntry(
                original.getId(), CHAPTER_ID, "درود", "خوش آمدید");
        service.updateEntry(updated);

        GlossaryEntry fetched = service.getEntryById(original.getId());
        assertEquals("درود", fetched.getPersianWord());
        assertEquals("خوش آمدید", fetched.getUrduMeaning());
    }

    @Test
    @DisplayName("Should trim values on update without mutating caller's object")
    void testUpdateEntryTrimsWithoutMutation() throws GlossaryException {
        GlossaryEntry original = service.addEntry(CHAPTER_ID, "سلام", "ہیلو");

        GlossaryEntry toUpdate = new GlossaryEntry(
                original.getId(), CHAPTER_ID, "  درود  ", "  خوش آمدید  ");
        service.updateEntry(toUpdate);

        // Caller's object should NOT be mutated
        assertEquals("  درود  ", toUpdate.getPersianWord());
        assertEquals("  خوش آمدید  ", toUpdate.getUrduMeaning());

        // But stored value should be trimmed
        GlossaryEntry fetched = service.getEntryById(original.getId());
        assertEquals("درود", fetched.getPersianWord());
        assertEquals("خوش آمدید", fetched.getUrduMeaning());
    }

    @Test
    @DisplayName("Should reject update with empty Persian word (BR-06)")
    void testUpdateRejectsEmptyPersian() throws GlossaryException {
        GlossaryEntry original = service.addEntry(CHAPTER_ID, "سلام", "ہیلو");

        GlossaryEntry invalid = new GlossaryEntry(
                original.getId(), CHAPTER_ID, "", "خوش آمدید");
        assertThrows(IllegalArgumentException.class,
                () -> service.updateEntry(invalid));

        // BR-06: original data is not replaced
        GlossaryEntry fetched = service.getEntryById(original.getId());
        assertEquals("سلام", fetched.getPersianWord());
    }

    @Test
    @DisplayName("Should reject update with empty Urdu meaning (BR-06)")
    void testUpdateRejectsEmptyUrdu() throws GlossaryException {
        GlossaryEntry original = service.addEntry(CHAPTER_ID, "سلام", "ہیلو");

        GlossaryEntry invalid = new GlossaryEntry(
                original.getId(), CHAPTER_ID, "درود", "");
        assertThrows(IllegalArgumentException.class,
                () -> service.updateEntry(invalid));

        // BR-06: original data is not replaced
        GlossaryEntry fetched = service.getEntryById(original.getId());
        assertEquals("ہیلو", fetched.getUrduMeaning());
    }

    @Test
    @DisplayName("Should throw when updating a non-existent entry")
    void testUpdateNonExistentEntry() {
        GlossaryEntry ghost = new GlossaryEntry(
                "non-existent-id", CHAPTER_ID, "لفظ", "معنی");
        assertThrows(IllegalArgumentException.class,
                () -> service.updateEntry(ghost));
    }

    @Test
    @DisplayName("Should reject update with null entry")
    void testUpdateNullEntry() {
        assertThrows(IllegalArgumentException.class,
                () -> service.updateEntry(null));
    }

    // ── FR-GL-05: Delete entry ──────────────────────────────

    @Test
    @DisplayName("Should delete an existing entry")
    void testDeleteEntry() throws GlossaryException {
        GlossaryEntry entry = service.addEntry(CHAPTER_ID, "سلام", "ہیلو");
        assertEquals(1, dao.size());

        service.deleteEntry(entry.getId());
        assertEquals(0, dao.size());
        assertNull(service.getEntryById(entry.getId()));
    }

    @Test
    @DisplayName("Should reject delete with blank ID")
    void testDeleteBlankId() {
        assertThrows(IllegalArgumentException.class,
                () -> service.deleteEntry(""));
    }

    @Test
    @DisplayName("Should reject delete with null ID")
    void testDeleteNullId() {
        assertThrows(IllegalArgumentException.class,
                () -> service.deleteEntry(null));
    }

    // ── FR-CH-07: Delete entries by chapter ──────────────────

    @Test
    @DisplayName("Should delete all entries for a chapter")
    void testDeleteEntriesByChapterId() throws GlossaryException {
        service.addEntry(CHAPTER_ID, "سلام", "ہیلو");
        service.addEntry(CHAPTER_ID, "خدا حافظ", "الوداع");
        assertEquals(2, dao.size());

        service.deleteEntriesByChapterId(CHAPTER_ID);
        assertEquals(0, dao.size());
    }

    // ── Persian / Urdu round-trip tests ─────────────────────

    @Test
    @DisplayName("Should preserve Persian characters through add and retrieve")
    void testPersianRoundTrip() throws GlossaryException {
        String persian = "آب";   // water
        String urdu = "پانی";

        GlossaryEntry entry = service.addEntry(CHAPTER_ID, persian, urdu);
        GlossaryEntry fetched = service.getEntryById(entry.getId());

        assertEquals(persian, fetched.getPersianWord());
        assertEquals(urdu, fetched.getUrduMeaning());
    }

    @Test
    @DisplayName("Should preserve Urdu characters with diacritics through round-trip")
    void testUrduDiacriticsRoundTrip() throws GlossaryException {
        String persian = "کِتاب";
        String urdu = "کتاب";

        GlossaryEntry entry = service.addEntry(CHAPTER_ID, persian, urdu);
        GlossaryEntry fetched = service.getEntryById(entry.getId());

        assertEquals(persian, fetched.getPersianWord());
        assertEquals(urdu, fetched.getUrduMeaning());
    }

    @Test
    @DisplayName("Should handle long Persian and Urdu text")
    void testLongTextRoundTrip() throws GlossaryException {
        String persian = "این یک جمله طولانی فارسی است";
        String urdu = "یہ ایک طویل اردو جملہ ہے جو بہت سے حروف پر مشتمل ہے";

        GlossaryEntry entry = service.addEntry(CHAPTER_ID, persian, urdu);
        GlossaryEntry fetched = service.getEntryById(entry.getId());

        assertEquals(persian, fetched.getPersianWord());
        assertEquals(urdu, fetched.getUrduMeaning());
    }

    // ── Boundary / edge cases ───────────────────────────────

    @Test
    @DisplayName("Should add entry with single-character Persian word")
    void testSingleCharPersian() throws GlossaryException {
        GlossaryEntry entry = service.addEntry(CHAPTER_ID, "آ", "الف");
        assertNotNull(entry);
        assertEquals("آ", entry.getPersianWord());
    }

    @Test
    @DisplayName("Should add entry with single-character Urdu meaning")
    void testSingleCharUrdu() throws GlossaryException {
        GlossaryEntry entry = service.addEntry(CHAPTER_ID, "سلام", "ہ");
        assertNotNull(entry);
        assertEquals("ہ", entry.getUrduMeaning());
    }

    @Test
    @DisplayName("Should retrieve entry by ID")
    void testGetEntryById() throws GlossaryException {
        GlossaryEntry added = service.addEntry(CHAPTER_ID, "سلام", "ہیلو");
        GlossaryEntry fetched = service.getEntryById(added.getId());

        assertNotNull(fetched);
        assertEquals(added.getId(), fetched.getId());
        assertEquals("سلام", fetched.getPersianWord());
    }

    @Test
    @DisplayName("Should return null for non-existent entry ID")
    void testGetNonExistentEntryById() throws GlossaryException {
        assertNull(service.getEntryById("non-existent-id"));
    }

    @Test
    @DisplayName("Should reject getEntryById with blank ID")
    void testGetEntryByBlankId() {
        assertThrows(IllegalArgumentException.class,
                () -> service.getEntryById(""));
    }
}
