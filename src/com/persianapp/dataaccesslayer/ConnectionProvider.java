package com.persianapp.dataaccesslayer;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Supplies database connections to DAO classes (dependency injection).
 * The caller closes the connection it receives, so each call must return
 * a connection that is safe to close.
 */
@FunctionalInterface
public interface ConnectionProvider {

    Connection getConnection() throws SQLException;
}