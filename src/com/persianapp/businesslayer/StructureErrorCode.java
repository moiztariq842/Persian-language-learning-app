package com.persianapp.businesslayer;

/** Reasons a UC-02 request can be rejected. Display text belongs to the presentation layer. */
public enum StructureErrorCode {
    BLANK_CHAPTER_ID,
    CHAPTER_NOT_FOUND,
    NULL_COMPONENT_LIST,
    NULL_COMPONENT,
    DUPLICATE_COMPONENT,
    EMPTY_SELECTION,
    COMPONENT_NOT_SELECTED,
    POSITION_OUT_OF_RANGE
}