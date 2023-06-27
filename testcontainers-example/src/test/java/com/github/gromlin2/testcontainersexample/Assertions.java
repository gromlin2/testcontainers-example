package com.github.gromlin2.testcontainersexample;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.gromlin2.testcontainersexample.DbQueries.ConnectionProvider;
import java.sql.SQLException;

public final class Assertions {
  private Assertions() {}

  public static void assertSchemaCount(ConnectionProvider connectionProvider, int n)
      throws SQLException {
    try (var connection = connectionProvider.getConnection();
        var statement = connection.createStatement()) {
      statement.execute(
          """
                SELECT count(*) as schema_count
                FROM pg_catalog.pg_namespace
                WHERE nspname = 'example_schema';
                """);

      final var resultSet = statement.getResultSet();

      assertTrue(resultSet.next());
      assertEquals(n, resultSet.getInt("schema_count"));
      assertFalse(resultSet.next());
    }
  }

  public static void assertTablesInExampleSchemaCount(ConnectionProvider connectionProvider, int n)
      throws SQLException {
    try (var connection = connectionProvider.getConnection();
        var statement = connection.createStatement()) {
      statement.execute(
          """
                SELECT count(*) as example_table_count
                FROM pg_catalog.pg_tables
                WHERE schemaname like 'example_schema';
                """);

      final var resultSet = statement.getResultSet();

      assertTrue(resultSet.next());
      assertEquals(n, resultSet.getInt("example_table_count"));
      assertFalse(resultSet.next());
    }
  }
}
