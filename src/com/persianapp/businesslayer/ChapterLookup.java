package com.persianapp.businesslayer;

// Contract for looking up chapter existence (used across use cases).
public interface ChapterLookup {

    // Returns true if a chapter with the given ID exists.
    boolean exists(String chapterId);
}
