package com.persianapp.dataaccesslayer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.persianapp.businesslayer.ChapterStructure;
import com.persianapp.businesslayer.ComponentType;
import com.persianapp.businesslayer.DataAccessException;

/**
 * Runs SqlStructureDao against a local MariaDB (XAMPP defaults: root, empty password).
 * Uses its own database, persian_app_test, which is dropped and rebuilt for every test.
 * Skipped automatically when MariaDB or its driver is not available.
 */
@DisplayName("UC-02: SqlStructureDao with a real local MariaDB")
class SqlStructureDaoTest {

    private static final String SERVER_URL = "jdbc:mariadb://localhost:3306/";
    private static final String TEST_DATABASE = "persian_app_test";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static boolean databaseAvailable;

    private SqlStructureDao dao;

    @BeforeAll
    static void checkDatabase() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
                    Statement statement = connection.createStatement()) {
                statement.execute("CREATE DATABASE IF NOT EXISTS " + TEST_DATABASE
                        + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            }
            databaseAvailable = true;
        } catch (ClassNotFoundException | SQLException e) {
            databaseAvailable = false;
        }
    }

    @BeforeEach
    void resetTables() throws SQLException {
        assumeTrue(databaseAvailable,
                "Local MariaDB (XAMPP) or the MariaDB driver is not available - skipping");
        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS chapter_components");
            statement.execute("DROP TABLE IF EXISTS chapters");
            statement.execute("CREATE TABLE chapters ("
                    + "id VARCHAR(36) NOT NULL PRIMARY KEY, "
                    + "title VARCHAR(255) NOT NULL) ENGINE=InnoDB");
            StructureSchema.create(connection);
            statement.execute("INSERT INTO chapters (id, title) VALUES "
                    + "('ch-1', 'Chapter 1'), ('ch-2', 'Chapter 2')");
        }
        dao = new SqlStructureDao(SqlStructureDaoTest::connect);
    }

    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(SERVER_URL + TEST_DATABASE, USER, PASSWORD);
    }

    private static int countRows(String chapterId) throws SQLException {
        try (Connection connection = connect();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT COUNT(*) FROM chapter_components WHERE chapter_id = ?")) {
            statement.setString(1, chapterId);
            try (ResultSet rows = statement.executeQuery()) {
                rows.next();
                return rows.getInt(1);
            }
        }
    }

    @Test
    @DisplayName("Finds nothing for a chapter without a saved structure")
    void findsNothingWhenNothingSaved() {
        assertFalse(dao.findByChapterId("ch-1").isPresent());
    }

    @Test
    @DisplayName("A saved structure is read back with the same components in the same order")
    void roundTripKeepsOrder() {
        ChapterStructure saved = new ChapterStructure("ch-1", Arrays.asList(
                ComponentType.GLOSSARY, ComponentType.LEARNING_OBJECTIVES, ComponentType.EXAMPLES));
        dao.save(saved);

        Optional<ChapterStructure> found = dao.findByChapterId("ch-1");

        assertEquals(saved, found.get());
    }

    @Test
    @DisplayName("All seven components can be stored in order")
    void storesAllSevenComponents() {
        ChapterStructure saved = new ChapterStructure("ch-1", Arrays.asList(ComponentType.values()));
        dao.save(saved);
        assertEquals(saved, dao.findByChapterId("ch-1").get());
    }

    @Test
    @DisplayName("Saving again replaces the previous structure")
    void saveReplacesPreviousStructure() throws SQLException {
        dao.save(new ChapterStructure("ch-1", Arrays.asList(
                ComponentType.EXAMPLES, ComponentType.GLOSSARY, ComponentType.PERSIAN_DIALOGUE)));
        ChapterStructure replacement = new ChapterStructure("ch-1", Arrays.asList(
                ComponentType.GLOSSARY, ComponentType.EXAMPLES));

        dao.save(replacement);

        assertEquals(replacement, dao.findByChapterId("ch-1").get());
        assertEquals(2, countRows("ch-1"));
    }

    @Test
    @DisplayName("Different chapters keep different structures (FR-CS-05)")
    void chaptersAreIndependent() {
        ChapterStructure first = new ChapterStructure("ch-1", Arrays.asList(ComponentType.EXAMPLES));
        ChapterStructure second = new ChapterStructure("ch-2", Arrays.asList(ComponentType.GLOSSARY));
        dao.save(first);
        dao.save(second);

        assertEquals(first, dao.findByChapterId("ch-1").get());
        assertEquals(second, dao.findByChapterId("ch-2").get());
    }

    @Test
    @DisplayName("Deleting a chapter removes its components (no orphans)")
    void deletingChapterRemovesComponents() throws SQLException {
        dao.save(new ChapterStructure("ch-1", Arrays.asList(
                ComponentType.EXAMPLES, ComponentType.GLOSSARY)));

        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM chapters WHERE id = 'ch-1'");
        }

        assertEquals(0, countRows("ch-1"));
        assertFalse(dao.findByChapterId("ch-1").isPresent());
    }

    @Test
    @DisplayName("A failed save stores nothing and leaves other structures untouched")
    void failedSaveStoresNothing() throws SQLException {
        ChapterStructure existing = new ChapterStructure("ch-1", Arrays.asList(ComponentType.GLOSSARY));
        dao.save(existing);

        assertThrows(DataAccessException.class, () -> dao.save(
                new ChapterStructure("missing", Arrays.asList(ComponentType.EXAMPLES))));

        assertEquals(0, countRows("missing"));
        assertEquals(existing, dao.findByChapterId("ch-1").get());
    }
}