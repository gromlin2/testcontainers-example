package com.github.gromlin2.testcontainersexample;

import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DbQueries {

  public interface ConnectionProvider {

    Connection getConnection() throws SQLException;
  }

  private final ConnectionProvider connectionProvider;

  public DbQueries(ConnectionProvider connectionProvider) {
    requireNonNull(connectionProvider, "connectionProvider is null");
    this.connectionProvider = connectionProvider;
  }

  public void createSchema() throws SQLException {
    try (Connection connection = connectionProvider.getConnection();
        Statement statement = connection.createStatement()) {
      statement.execute("CREATE SCHEMA IF NOT EXISTS example_schema;");
    }
  }

  public void createTable(String tableName) throws SQLException {

    try (Connection connection = connectionProvider.getConnection();
        Statement statement = connection.createStatement()) {
      statement.execute(
          """
          CREATE TABLE IF NOT EXISTS example_schema.%s (
            id SERIAL PRIMARY KEY,
            name VARCHAR(255) NOT NULL
          );
          """
              .formatted(tableName));
    }
  }
}
