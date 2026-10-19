-- ========================================================
-- SQLite Database Schema for UC-01: Manage Chapters
-- ========================================================

PRAGMA foreign_keys = ON;

-- Chapters Table (UC-01 Core Entity)
CREATE TABLE IF NOT EXISTS chapters (
    id          TEXT PRIMARY KEY,
    title       TEXT NOT NULL CHECK(length(trim(title)) > 0),
    created_at  INTEGER NOT NULL DEFAULT (strftime('%s', 'now'))
);

CREATE INDEX IF NOT EXISTS idx_chapters_created_at ON chapters(created_at ASC);

-- ========================================================
-- SQLite Schema for UC-03: Manage Chapter Instructional Content
-- Components are created by UC-02; UC-03 edits their content.
-- ========================================================

CREATE TABLE IF NOT EXISTS chapter_components (
    id              TEXT PRIMARY KEY,
    chapter_id      TEXT NOT NULL REFERENCES chapters(id) ON DELETE CASCADE,
    component_type  TEXT NOT NULL CHECK(component_type IN ('LEARNING_OBJECTIVES','PERSIAN_DIALOGUE','PERSIAN_READING_PASSAGE','URDU_EXPLANATION','GRAMMAR_NOTE','EXAMPLES','GLOSSARY')),
    position        INTEGER NOT NULL CHECK(position >= 0),
    content         TEXT NOT NULL DEFAULT '',
    UNIQUE (chapter_id, position)
);

CREATE INDEX IF NOT EXISTS idx_components_chapter ON chapter_components(chapter_id, position);
