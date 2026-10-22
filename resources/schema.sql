-- ========================================================
-- MariaDB Database Schema
-- Persian Language Learning App
-- ========================================================

-- Create the database with utf8mb4 so Persian and Urdu text survive (FR-CS-07).
CREATE DATABASE IF NOT EXISTS persian_app
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE persian_app;

-- ========================================================
-- UC-01: Chapters Table (owned by UC-01 member)
-- ========================================================

CREATE TABLE IF NOT EXISTS chapters (
    id          CHAR(36)     PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    created_at  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================================
-- UC-04: Glossary Management Table
-- ========================================================

CREATE TABLE IF NOT EXISTS glossary_entries (
    id           CHAR(36)     PRIMARY KEY,
    chapter_id   CHAR(36)     NOT NULL,
    persian_word VARCHAR(255) NOT NULL,
    urdu_meaning VARCHAR(500) NOT NULL,
    created_at   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_gloss_chapter
        FOREIGN KEY (chapter_id) REFERENCES chapters(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_glossary_chapter_id ON glossary_entries(chapter_id);
