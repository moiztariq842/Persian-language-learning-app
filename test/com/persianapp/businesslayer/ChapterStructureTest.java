package com.persianapp.businesslayer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("UC-02: ChapterStructure")
class ChapterStructureTest {

    private static final String ID = "ch-1";

    @Test
    @DisplayName("Keeps the selected components in the given order")
    void keepsOrder() {
        ChapterStructure s = new ChapterStructure(ID, Arrays.asList(
                ComponentType.GLOSSARY, ComponentType.LEARNING_OBJECTIVES, ComponentType.EXAMPLES));
        assertEquals(Arrays.asList(
                ComponentType.GLOSSARY, ComponentType.LEARNING_OBJECTIVES, ComponentType.EXAMPLES),
                s.getComponents());
        assertEquals(ID, s.getChapterId());
    }

    @Test
    @DisplayName("empty() creates a structure with no components")
    void emptyStructure() {
        ChapterStructure s = ChapterStructure.empty(ID);
        assertTrue(s.isEmpty());
        assertEquals(0, s.getComponents().size());
    }

    @Test
    @DisplayName("Rejects a blank or null chapter id")
    void rejectsBlankChapterId() {
        List<ComponentType> none = Collections.emptyList();
        assertThrows(IllegalArgumentException.class, () -> new ChapterStructure(null, none));
        assertThrows(IllegalArgumentException.class, () -> new ChapterStructure("   ", none));
    }

    @Test
    @DisplayName("Rejects a null component list")
    void rejectsNullList() {
        assertThrows(IllegalArgumentException.class, () -> new ChapterStructure(ID, null));
    }

    @Test
    @DisplayName("Rejects a null component inside the list")
    void rejectsNullElement() {
        assertThrows(IllegalArgumentException.class, () -> new ChapterStructure(ID,
                Arrays.asList(ComponentType.EXAMPLES, null)));
    }

    @Test
    @DisplayName("Rejects the same component selected twice")
    void rejectsDuplicates() {
        assertThrows(IllegalArgumentException.class, () -> new ChapterStructure(ID,
                Arrays.asList(ComponentType.EXAMPLES, ComponentType.EXAMPLES)));
    }

    @Test
    @DisplayName("withAdded appends a component and leaves the original unchanged")
    void withAddedAppends() {
        ChapterStructure original = new ChapterStructure(ID,
                Arrays.asList(ComponentType.LEARNING_OBJECTIVES));
        ChapterStructure updated = original.withAdded(ComponentType.GLOSSARY);
        assertEquals(Arrays.asList(ComponentType.LEARNING_OBJECTIVES, ComponentType.GLOSSARY),
                updated.getComponents());
        assertEquals(1, original.getComponents().size());
    }

    @Test
    @DisplayName("withAdded rejects a component that is already selected")
    void withAddedRejectsDuplicate() {
        ChapterStructure s = new ChapterStructure(ID, Arrays.asList(ComponentType.EXAMPLES));
        assertThrows(IllegalArgumentException.class, () -> s.withAdded(ComponentType.EXAMPLES));
    }

    @Test
    @DisplayName("withRemoved removes a selected component")
    void withRemovedRemoves() {
        ChapterStructure s = new ChapterStructure(ID,
                Arrays.asList(ComponentType.EXAMPLES, ComponentType.GLOSSARY));
        ChapterStructure updated = s.withRemoved(ComponentType.EXAMPLES);
        assertEquals(Arrays.asList(ComponentType.GLOSSARY), updated.getComponents());
        assertFalse(updated.contains(ComponentType.EXAMPLES));
    }

    @Test
    @DisplayName("withRemoved rejects a component that is not selected")
    void withRemovedRejectsMissing() {
        ChapterStructure s = ChapterStructure.empty(ID);
        assertThrows(IllegalArgumentException.class, () -> s.withRemoved(ComponentType.EXAMPLES));
    }

    @Test
    @DisplayName("withMoved changes the position of a component")
    void withMovedReorders() {
        ChapterStructure s = new ChapterStructure(ID, Arrays.asList(
                ComponentType.LEARNING_OBJECTIVES, ComponentType.PERSIAN_DIALOGUE, ComponentType.GLOSSARY));
        ChapterStructure moved = s.withMoved(ComponentType.GLOSSARY, 0);
        assertEquals(Arrays.asList(
                ComponentType.GLOSSARY, ComponentType.LEARNING_OBJECTIVES, ComponentType.PERSIAN_DIALOGUE),
                moved.getComponents());
    }

    @Test
    @DisplayName("withMoved rejects positions out of range and unselected components")
    void withMovedRejectsInvalid() {
        ChapterStructure s = new ChapterStructure(ID, Arrays.asList(
                ComponentType.LEARNING_OBJECTIVES, ComponentType.GLOSSARY));
        assertThrows(IllegalArgumentException.class, () -> s.withMoved(ComponentType.GLOSSARY, -1));
        assertThrows(IllegalArgumentException.class, () -> s.withMoved(ComponentType.GLOSSARY, 2));
        assertThrows(IllegalArgumentException.class, () -> s.withMoved(ComponentType.EXAMPLES, 0));
    }

    @Test
    @DisplayName("The returned component list cannot be modified")
    void listIsUnmodifiable() {
        ChapterStructure s = new ChapterStructure(ID, Arrays.asList(ComponentType.EXAMPLES));
        assertThrows(UnsupportedOperationException.class,
                () -> s.getComponents().add(ComponentType.GLOSSARY));
    }

    @Test
    @DisplayName("Changing the source list afterwards does not change the structure")
    void keepsDefensiveCopy() {
        List<ComponentType> source = new ArrayList<>(Arrays.asList(ComponentType.EXAMPLES));
        ChapterStructure s = new ChapterStructure(ID, source);
        source.add(ComponentType.GLOSSARY);
        assertEquals(1, s.getComponents().size());
    }

    @Test
    @DisplayName("Structures with the same id and order are equal")
    void equalsByIdAndOrder() {
        ChapterStructure a = new ChapterStructure(ID, Arrays.asList(
                ComponentType.EXAMPLES, ComponentType.GLOSSARY));
        ChapterStructure b = new ChapterStructure(ID, Arrays.asList(
                ComponentType.EXAMPLES, ComponentType.GLOSSARY));
        ChapterStructure otherOrder = new ChapterStructure(ID, Arrays.asList(
                ComponentType.GLOSSARY, ComponentType.EXAMPLES));
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertFalse(a.equals(otherOrder));
    }
}