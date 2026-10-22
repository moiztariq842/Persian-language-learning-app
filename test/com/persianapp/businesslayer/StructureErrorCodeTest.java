package com.persianapp.businesslayer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

@DisplayName("UC-02: structure error codes")
class StructureErrorCodeTest {

    private static StructureErrorCode codeOf(Executable action) {
        return assertThrows(StructureException.class, action).getCode();
    }

    private static ChapterStructureBao baoWithChapter(String existingId) {
        return new ChapterStructureBao(new NoOpDao(), id -> existingId.equals(id));
    }

    @Test
    @DisplayName("Blank chapter id in a structure gives BLANK_CHAPTER_ID")
    void blankChapterIdInStructure() {
        assertEquals(StructureErrorCode.BLANK_CHAPTER_ID,
                codeOf(() -> new ChapterStructure("  ", Collections.<ComponentType>emptyList())));
    }

    @Test
    @DisplayName("Null component list gives NULL_COMPONENT_LIST")
    void nullComponentList() {
        assertEquals(StructureErrorCode.NULL_COMPONENT_LIST,
                codeOf(() -> new ChapterStructure("ch-1", null)));
    }

    @Test
    @DisplayName("Null component inside the list gives NULL_COMPONENT")
    void nullComponent() {
        assertEquals(StructureErrorCode.NULL_COMPONENT,
                codeOf(() -> new ChapterStructure("ch-1", Arrays.asList(ComponentType.EXAMPLES, null))));
    }

    @Test
    @DisplayName("Duplicate component gives DUPLICATE_COMPONENT")
    void duplicateComponent() {
        assertEquals(StructureErrorCode.DUPLICATE_COMPONENT,
                codeOf(() -> new ChapterStructure("ch-1",
                        Arrays.asList(ComponentType.EXAMPLES, ComponentType.EXAMPLES))));
    }

    @Test
    @DisplayName("Removing a component that is not selected gives COMPONENT_NOT_SELECTED")
    void removeMissingComponent() {
        ChapterStructure structure = ChapterStructure.empty("ch-1");
        assertEquals(StructureErrorCode.COMPONENT_NOT_SELECTED,
                codeOf(() -> structure.withRemoved(ComponentType.EXAMPLES)));
    }

    @Test
    @DisplayName("Moving a component that is not selected gives COMPONENT_NOT_SELECTED")
    void moveMissingComponent() {
        ChapterStructure structure = ChapterStructure.empty("ch-1");
        assertEquals(StructureErrorCode.COMPONENT_NOT_SELECTED,
                codeOf(() -> structure.withMoved(ComponentType.EXAMPLES, 0)));
    }

    @Test
    @DisplayName("Moving to a position out of range gives POSITION_OUT_OF_RANGE")
    void moveOutOfRange() {
        ChapterStructure structure = new ChapterStructure("ch-1", Arrays.asList(ComponentType.EXAMPLES));
        assertEquals(StructureErrorCode.POSITION_OUT_OF_RANGE,
                codeOf(() -> structure.withMoved(ComponentType.EXAMPLES, 5)));
    }

    @Test
    @DisplayName("Saving for an unknown chapter gives CHAPTER_NOT_FOUND")
    void unknownChapter() {
        assertEquals(StructureErrorCode.CHAPTER_NOT_FOUND,
                codeOf(() -> baoWithChapter("ch-1").saveStructure("missing", Arrays.asList(ComponentType.EXAMPLES))));
    }

    @Test
    @DisplayName("Saving with a blank chapter id gives BLANK_CHAPTER_ID")
    void blankChapterIdInBao() {
        assertEquals(StructureErrorCode.BLANK_CHAPTER_ID,
                codeOf(() -> baoWithChapter("ch-1").saveStructure("  ", Arrays.asList(ComponentType.EXAMPLES))));
    }

    private static class NoOpDao implements StructureDao {
        @Override
        public Optional<ChapterStructure> findByChapterId(String chapterId) {
            return Optional.empty();
        }

        @Override
        public void save(ChapterStructure structure) {
        }
    }
}