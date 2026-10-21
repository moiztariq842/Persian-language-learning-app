package com.persianapp.dataaccesslayer;

import com.persianapp.businesslayer.ChapterComponent;
import com.persianapp.businesslayer.ComponentType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * SQL implementation (plain JDBC) for MySQL/MariaDB. It uses the shared connection from DatabaseManager and must not
 * close it, because DatabaseManager owns that connection for the whole application.
 */
public class SqlComponentRepository implements ComponentRepository {

    private static final String SELECT_COLUMNS =
            "SELECT id, chapter_id, component_type, position, content FROM chapter_components";

    @Override
    public List<ChapterComponent> findByChapter(String chapterId) {
        try {
            Connection conn = DatabaseManager.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(
                    SELECT_COLUMNS + " WHERE chapter_id = ? ORDER BY position ASC")) {
                ps.setString(1, chapterId);
                List<ChapterComponent> result = new ArrayList<>();
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(map(rs));
                    }
                }
                return result;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not load components for chapter " + chapterId, e);
        }
    }

    @Override
    public Optional<ChapterComponent> findById(String componentId) {
        try {
            Connection conn = DatabaseManager.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE id = ?")) {
                ps.setString(1, componentId);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? Optional.of(map(rs)) : Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not load component " + componentId, e);
        }
    }

    @Override
    public void add(ChapterComponent component) {
        try {
            Connection conn = DatabaseManager.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO chapter_components (id, chapter_id, component_type, position, content) "
                            + "VALUES (?, ?, ?, ?, ?)")) {
                ps.setString(1, component.getId());
                ps.setString(2, component.getChapterId());
                ps.setString(3, component.getType().name());
                ps.setInt(4, component.getPosition());
                ps.setString(5, component.getContent());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not add component " + component.getId(), e);
        }
    }

    @Override
    public void updateContent(String componentId, String newContent) {
        try {
            Connection conn = DatabaseManager.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE chapter_components SET content = ? WHERE id = ?")) {
                ps.setString(1, newContent);
                ps.setString(2, componentId);
                if (ps.executeUpdate() == 0) {
                    throw new NoSuchElementException("No component with id " + componentId);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not update component " + componentId, e);
        }
    }

    private static ChapterComponent map(ResultSet rs) throws SQLException {
        return new ChapterComponent(
                rs.getString("id"),
                rs.getString("chapter_id"),
                ComponentType.valueOf(rs.getString("component_type")),
                rs.getInt("position"),
                rs.getString("content"));
    }
}
