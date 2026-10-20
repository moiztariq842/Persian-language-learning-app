package com.persianapp.dataaccesslayer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.persianapp.businesslayer.ChapterStructure;
import com.persianapp.businesslayer.ComponentType;
import com.persianapp.businesslayer.DataAccessException;

@DisplayName("UC-02: SqlStructureDao failure handling (no database needed)")
class SqlStructureDaoFailureTest {

    private static final ConnectionProvider UNAVAILABLE = () -> {
        throw new SQLException("database down");
    };

    private static ChapterStructure sampleStructure() {
        return new ChapterStructure("ch-1",
                Arrays.asList(ComponentType.EXAMPLES, ComponentType.GLOSSARY));
    }

    @Test
    @DisplayName("Requires a connection provider")
    void requiresConnectionProvider() {
        assertThrows(IllegalArgumentException.class, () -> new SqlStructureDao(null));
    }

    @Test
    @DisplayName("Reading wraps a database failure in DataAccessException")
    void findWrapsSqlException() {
        SqlStructureDao dao = new SqlStructureDao(UNAVAILABLE);
        assertThrows(DataAccessException.class, () -> dao.findByChapterId("ch-1"));
    }

    @Test
    @DisplayName("Saving wraps a database failure and keeps the original cause")
    void saveWrapsSqlExceptionAndKeepsCause() {
        SqlStructureDao dao = new SqlStructureDao(UNAVAILABLE);
        DataAccessException error = assertThrows(DataAccessException.class,
                () -> dao.save(sampleStructure()));
        assertEquals(SQLException.class, error.getCause().getClass());
    }

    @Test
    @DisplayName("A successful save runs in one transaction and commits")
    void saveCommits() {
        RecordingConnection recording = new RecordingConnection(false);
        SqlStructureDao dao = new SqlStructureDao(recording::connection);

        dao.save(sampleStructure());

        assertEquals(Arrays.asList(
                "setAutoCommit:false", "prepare", "prepare", "commit", "setAutoCommit:true", "close"),
                recording.calls);
    }

    @Test
    @DisplayName("A failed insert rolls the transaction back and never commits")
    void failedSaveRollsBack() {
        RecordingConnection recording = new RecordingConnection(true);
        SqlStructureDao dao = new SqlStructureDao(recording::connection);

        assertThrows(DataAccessException.class, () -> dao.save(sampleStructure()));

        assertEquals(Arrays.asList(
                "setAutoCommit:false", "prepare", "prepare", "rollback", "setAutoCommit:true", "close"),
                recording.calls);
    }

    @Test
    @DisplayName("The connection is closed even when reading fails")
    void connectionClosedAfterFailedRead() {
        RecordingConnection recording = new RecordingConnection(false);
        SqlStructureDao dao = new SqlStructureDao(recording::connection);

        assertThrows(DataAccessException.class, () -> dao.findByChapterId("ch-1"));

        assertEquals("close", recording.calls.get(recording.calls.size() - 1));
    }

    // ---- hand-written fake connection (no mocking framework) ----

    private static class RecordingConnection implements InvocationHandler {

        private final List<String> calls = new ArrayList<>();
        private final boolean failOnBatch;

        RecordingConnection(boolean failOnBatch) {
            this.failOnBatch = failOnBatch;
        }

        Connection connection() {
            return (Connection) Proxy.newProxyInstance(
                    SqlStructureDaoFailureTest.class.getClassLoader(),
                    new Class<?>[] { Connection.class }, this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            String name = method.getName();
            switch (name) {
            case "getAutoCommit":
                return Boolean.TRUE;
            case "setAutoCommit":
                calls.add("setAutoCommit:" + args[0]);
                return null;
            case "prepareStatement":
                calls.add("prepare");
                return statement();
            case "commit":
            case "rollback":
            case "close":
                calls.add(name);
                return null;
            default:
                throw new UnsupportedOperationException(name);
            }
        }

        private PreparedStatement statement() {
            return (PreparedStatement) Proxy.newProxyInstance(
                    SqlStructureDaoFailureTest.class.getClassLoader(),
                    new Class<?>[] { PreparedStatement.class },
                    (proxy, method, args) -> {
                        switch (method.getName()) {
                        case "executeUpdate":
                            return Integer.valueOf(0);
                        case "executeBatch":
                            if (failOnBatch) {
                                throw new SQLException("insert failed");
                            }
                            return new int[0];
                        case "executeQuery":
                            throw new SQLException("query failed");
                        default:
                            return null;
                        }
                    });
        }
    }
}