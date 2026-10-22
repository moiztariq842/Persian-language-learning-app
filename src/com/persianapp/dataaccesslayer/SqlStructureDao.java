package com.persianapp.dataaccesslayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.persianapp.businesslayer.ChapterStructure;
import com.persianapp.businesslayer.ComponentType;
import com.persianapp.businesslayer.DataAccessException;
import com.persianapp.businesslayer.StructureDao;

/**
 * JDBC implementation of StructureDao for MySQL/MariaDB (UC-02).
 * Uses the chapter_components table with sort_order column.
 */
public class SqlStructureDao implements StructureDao {

    private static final String SELECT_SQL =
            "SELECT component_type FROM chapter_components WHERE chapter_id = ? ORDER BY sort_order";
    private static final String DELETE_SQL =
            "DELETE FROM chapter_components WHERE chapter_id = ?";
    private static final String INSERT_SQL =
            "INSERT INTO chapter_components (id, chapter_id, sort_order, component_type, content) " +
            "VALUES (UUID(), ?, ?, ?, '')";

    private final ConnectionProvider connections;

    public SqlStructureDao(ConnectionProvider connections) {
        if (connections == null) {
            throw new IllegalArgumentException("Connection provider is required.");
        }
        this.connections = connections;
    }

    @Override
    public Optional<ChapterStructure> findByChapterId(String chapterId) {
        List<ComponentType> components = new ArrayList<>();
        try (Connection connection = connections.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_SQL)) {
            statement.setString(1, chapterId);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    components.add(ComponentType.valueOf(rows.getString(1)));
                }
            }
        } catch (SQLException | IllegalArgumentException e) {
            throw new DataAccessException("Could not read the structure of chapter " + chapterId, e);
        }
        if (components.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new ChapterStructure(chapterId, components));
    }

    /** Replaces the stored structure of the chapter in one transaction. */
    @Override
    public void save(ChapterStructure structure) {
        String chapterId = structure.getChapterId();
        try (Connection connection = connections.getConnection()) {
            boolean previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                deleteRows(connection, chapterId);
                insertRows(connection, structure);
                connection.commit();
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(previousAutoCommit);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not save the structure of chapter " + chapterId, e);
        }
    }

    private void deleteRows(Connection connection, String chapterId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setString(1, chapterId);
            statement.executeUpdate();
        }
    }

    private void insertRows(Connection connection, ChapterStructure structure) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            List<ComponentType> components = structure.getComponents();
            for (int i = 0; i < components.size(); i++) {
                statement.setString(1, structure.getChapterId());
                statement.setInt(2, i + 1);
                statement.setString(3, components.get(i).name());
                statement.executeUpdate();
            }
        }
    }
}
