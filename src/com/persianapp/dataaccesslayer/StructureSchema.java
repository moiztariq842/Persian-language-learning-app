package com.persianapp.dataaccesslayer;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Table definition for UC-02 (MariaDB / MySQL, InnoDB).
 * Requires the chapters table (UC-01) to exist, with id VARCHAR(36).
 * The table deliberately inherits the database's default character set and collation,
 * so the foreign key to chapters(id) stays compatible with UC-01's table.
 * ON DELETE CASCADE removes a chapter's components with the chapter (FR-CH-07, NFR-RL-03).
 */
public final class StructureSchema {

    public static final String CREATE_CHAPTER_COMPONENTS =
            "CREATE TABLE IF NOT EXISTS chapter_components ("
            + " chapter_id VARCHAR(36) NOT NULL,"
            + " sort_order INT NOT NULL,"
            + " component_type VARCHAR(50) NOT NULL,"
            + " PRIMARY KEY (chapter_id, sort_order),"
            + " UNIQUE KEY uq_chapter_component (chapter_id, component_type),"
            + " CONSTRAINT fk_chapter_components_chapter FOREIGN KEY (chapter_id)"
            + " REFERENCES chapters (id) ON DELETE CASCADE"
            + ") ENGINE=InnoDB";

    private StructureSchema() {
    }

    public static void create(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(CREATE_CHAPTER_COMPONENTS);
        }
    }
}