package com.persianapp.businesslayer;

/**
 * The chapter components an administrator can include in a chapter (FR-CS-01).
 * Display labels (Urdu/English) belong to the presentation layer, not here.
 */
public enum ComponentType {
    LEARNING_OBJECTIVES,
    PERSIAN_DIALOGUE,
    PERSIAN_READING_PASSAGE,
    URDU_TRANSLATION_OR_EXPLANATION,
    GRAMMAR_OR_LANGUAGE_NOTE,
    EXAMPLES,
    GLOSSARY
}