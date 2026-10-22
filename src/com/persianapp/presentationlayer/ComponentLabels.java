package com.persianapp.presentationlayer;

import com.persianapp.businesslayer.ComponentType;

/** Urdu display labels for chapter components (presentation concern, NFR-US-01). */
public final class ComponentLabels {

    private ComponentLabels() {}

    public static String labelFor(ComponentType type) {
        return switch (type) {
            case LEARNING_OBJECTIVES     -> "سیکھنے کے مقاصد";
            case PERSIAN_DIALOGUE        -> "فارسی مکالمہ";
            case PERSIAN_READING_PASSAGE -> "فارسی عبارت برائے مطالعہ";
            case URDU_EXPLANATION        -> "اردو ترجمہ یا وضاحت";
            case GRAMMAR_NOTE            -> "قواعد یا زبان کا نوٹ";
            case EXAMPLES                -> "مثالیں";
            case GLOSSARY                -> "لغت";
        };
    }
}
