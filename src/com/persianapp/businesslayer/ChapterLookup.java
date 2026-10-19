package com.persianapp.businesslayer;

/**
 * The only thing UC-02 needs to know about UC-01's chapters:
 * does a chapter with this id exist? (UC-02 precondition)
 */
public interface ChapterLookup {

    boolean exists(String chapterId);
}