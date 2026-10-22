package com.persianapp.dataaccesslayer;

import com.persianapp.businesslayer.Chapter;
import com.persianapp.businesslayer.DataAccessException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// JDBC implementation of ChapterDao for MySQL/MariaDB (UC-01).
public class SqlChapterDao implements ChapterDao {

    private static final String INSERT_SQL =
        "INSERT INTO chapters (id, title, created_at) VALUES (?, ?, UNIX_TIMESTAMP())";
    private static final String SELECT_ALL_SQL =
        "SELECT id, title FROM chapters ORDER BY created_at ASC";
    private static final String SELECT_BY_ID_SQL =
        "SELECT id, title FROM chapters WHERE id = ?";
    private static final String UPDATE_SQL =
        "UPDATE chapters SET title = ? WHERE id = ?";
    private static final String DELETE_SQL =
        "DELETE FROM chapters WHERE id = ?";

    private final ConnectionProvider connections;

    public SqlChapterDao(ConnectionProvider connections) {
        if (connections == null) {
            throw new IllegalArgumentException("ConnectionProvider is required.");
        }
        this.connections = connections;
    }

    @Override
    public void insert(Chapter chapter) {
        try (Connection conn = connections.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
            ps.setString(1, chapter.getId());
            ps.setString(2, chapter.getTitle());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not insert chapter: " + chapter.getId(), e);
        }
    }

    @Override
    public List<Chapter> selectAll() {
        List<Chapter> list = new ArrayList<>();
        try (Connection conn = connections.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(SELECT_ALL_SQL)) {
            while (rs.next()) {
                list.add(new Chapter(rs.getString("id"), rs.getString("title")));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not select all chapters.", e);
        }
        return list;
    }

    @Override
    public Optional<Chapter> selectById(String chapterId) {
        try (Connection conn = connections.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setString(1, chapterId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Chapter(rs.getString("id"), rs.getString("title")));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not find chapter: " + chapterId, e);
        }
        return Optional.empty();
    }

    @Override
    public void updateTitle(Chapter chapter) {
        try (Connection conn = connections.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
            ps.setString(1, chapter.getTitle());
            ps.setString(2, chapter.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not update chapter: " + chapter.getId(), e);
        }
    }

    @Override
    public void deleteById(String chapterId) {
        try (Connection conn = connections.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {
            ps.setString(1, chapterId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not delete chapter: " + chapterId, e);
        }
    }
}
