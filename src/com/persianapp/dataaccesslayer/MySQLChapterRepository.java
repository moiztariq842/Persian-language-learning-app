package com.persianapp.dataaccesslayer;

import com.persianapp.businesslayer.Chapter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MySQLChapterRepository implements ChapterRepository {

    // Saves a chapter to the database.
    @Override
    public void save(Chapter chapter) {
        String sql = "INSERT INTO chapters (id, title) VALUES (?, ?)";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, chapter.getId());
            ps.setString(2, chapter.getTitle());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save chapter: " + e.getMessage(), e);
        }
    }

    // Returns all chapters from the database.
    @Override
    public List<Chapter> findAll() {
        List<Chapter> result = new ArrayList<>();
        String sql = "SELECT id, title FROM chapters ORDER BY created_at ASC";
        try (Statement stmt = DatabaseManager.getConnection().createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.add(new Chapter(rs.getString("id"), rs.getString("title")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list chapters: " + e.getMessage(), e);
        }
        return result;
    }

    // Finds a chapter by its ID.
    @Override
    public Optional<Chapter> findById(String chapterId) {
        String sql = "SELECT id, title FROM chapters WHERE id = ?";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, chapterId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Chapter(rs.getString("id"), rs.getString("title")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find chapter: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    // Updates an existing chapter in the database.
    @Override
    public void update(Chapter chapter) {
        String sql = "UPDATE chapters SET title = ? WHERE id = ?";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, chapter.getTitle());
            ps.setString(2, chapter.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update chapter: " + e.getMessage(), e);
        }
    }

    // Deletes a chapter by its ID from the database.
    @Override
    public void deleteById(String chapterId) {
        String sql = "DELETE FROM chapters WHERE id = ?";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, chapterId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete chapter: " + e.getMessage(), e);
        }
    }
}
