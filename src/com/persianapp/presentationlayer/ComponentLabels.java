package com.persianapp.presentationlayer;

import com.persianapp.businesslayer.ComponentType;

/** Urdu display labels for chapter components (presentation concern, NFR-US-01). */
public final class ComponentLabels {

    private ComponentLabels() {
    }

    public static String labelFor(ComponentType type) {
        switch (type) {
        case LEARNING_OBJECTIVES:
            return "سیکھنے کے مقاصد";
        case PERSIAN_DIALOGUE:
            return "فارسی مکالمہ";
        case PERSIAN_READING_PASSAGE:
            return "فارسی عبارت برائے مطالعہ";
        case URDU_TRANSLATION_OR_EXPLANATION:
            return "اردو ترجمہ یا وضاحت";
        case GRAMMAR_OR_LANGUAGE_NOTE:
            return "قواعد یا زبان کا نوٹ";
        case EXAMPLES:
            return "مثالیں";
        case GLOSSARY:
            return "لغت";
        default:
            return type.name();
        }
    }
}