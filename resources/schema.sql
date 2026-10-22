-- =========================================================
-- Persian Language Learning App
-- MySQL / MariaDB Database Schema
-- Covers: UC-01 (chapters), UC-02 (structure), UC-03 (content), UC-04 (glossary)
-- =========================================================

CREATE DATABASE IF NOT EXISTS persian_app
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE persian_app;

-- =========================================================
-- TABLE 1: chapters (UC-01)
-- =========================================================
CREATE TABLE IF NOT EXISTS chapters (
    id         VARCHAR(36)  NOT NULL,
    title      VARCHAR(500) NOT NULL,
    created_at BIGINT       NOT NULL DEFAULT (UNIX_TIMESTAMP()),
    PRIMARY KEY (id),
    CONSTRAINT chk_chapter_title CHECK (CHAR_LENGTH(TRIM(title)) > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX IF NOT EXISTS idx_chapters_created_at ON chapters (created_at ASC);

-- =========================================================
-- TABLE 2: chapter_components (UC-02 structure + UC-03 content)
-- sort_order : display/edit order (UC-02)
-- content    : editable text (UC-03)
-- =========================================================
CREATE TABLE IF NOT EXISTS chapter_components (
    id             VARCHAR(36) NOT NULL,
    chapter_id     VARCHAR(36) NOT NULL,
    component_type ENUM(
        'LEARNING_OBJECTIVES',
        'PERSIAN_DIALOGUE',
        'PERSIAN_READING_PASSAGE',
        'URDU_EXPLANATION',
        'GRAMMAR_NOTE',
        'EXAMPLES',
        'GLOSSARY'
    ) NOT NULL,
    sort_order     INT         NOT NULL DEFAULT 1,
    content        LONGTEXT    NOT NULL DEFAULT '',
    PRIMARY KEY (id),
    FOREIGN KEY (chapter_id) REFERENCES chapters(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX IF NOT EXISTS idx_chapter_components_chapter_seq
    ON chapter_components (chapter_id, sort_order ASC);

-- =========================================================
-- TABLE 3: glossary_entries (UC-04)
-- =========================================================
CREATE TABLE IF NOT EXISTS glossary_entries (
    id           VARCHAR(36)  NOT NULL,
    chapter_id   VARCHAR(36)  NOT NULL,
    persian_word VARCHAR(500) NOT NULL,
    urdu_meaning VARCHAR(500) NOT NULL,
    created_at   BIGINT       NOT NULL DEFAULT (UNIX_TIMESTAMP()),
    PRIMARY KEY (id),
    CONSTRAINT chk_persian_word CHECK (CHAR_LENGTH(TRIM(persian_word)) > 0),
    CONSTRAINT chk_urdu_meaning CHECK (CHAR_LENGTH(TRIM(urdu_meaning)) > 0),
    FOREIGN KEY (chapter_id) REFERENCES chapters(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX IF NOT EXISTS idx_glossary_entries_chapter_id
    ON glossary_entries (chapter_id);
