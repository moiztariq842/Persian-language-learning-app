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
-- SQLite Database Schema for UC-04: Glossary Management
-- ========================================================

CREATE TABLE IF NOT EXISTS glossary_entries (
    id           TEXT PRIMARY KEY,
    chapter_id   TEXT NOT NULL,
    persian_word TEXT NOT NULL CHECK(length(trim(persian_word)) > 0),
    urdu_meaning TEXT NOT NULL CHECK(length(trim(urdu_meaning)) > 0),
    FOREIGN KEY (chapter_id) REFERENCES chapters(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_glossary_chapter_id ON glossary_entries(chapter_id);
