package com.persianapp.businesslayer;

// Contract for looking up chapter existence (used across UC-01, UC-02, UC-04).
public interface ChapterLookup {

    // Returns true if a chapter with the given ID exists.
    boolean exists(String chapterId);
}
