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
