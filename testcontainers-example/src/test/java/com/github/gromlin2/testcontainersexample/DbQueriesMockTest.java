package com.github.gromlin2.testcontainersexample;

import static com.github.gromlin2.testcontainersexample.Assertions.assertSchemaCount;
import static com.github.gromlin2.testcontainersexample.Assertions.assertTablesInExampleSchemaCount;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.gromlin2.testcontainersexample.DbQueries.ConnectionProvider;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DbQueriesMockTest {

  private DbQueries dbQueries;
  private ConnectionProvider connectionProvider;
  private Connection connection;
  private Statement statement;
  private ResultSet resultSet;

  @BeforeEach
  void setUp() throws SQLException {
    connectionProvider = mock(ConnectionProvider.class);
    connection = mock(Connection.class);
    statement = mock(Statement.class);
    resultSet = mock(ResultSet.class);

    when(connectionProvider.getConnection()).thenReturn(connection);
    when(connection.createStatement()).thenReturn(statement);
    when(statement.getResultSet()).thenReturn(resultSet);

    dbQueries = new DbQueries(connectionProvider);
  }

  @Test
  void mustCreateSchema() throws SQLException {
    when(resultSet.next()).thenReturn(true).thenReturn(false);
    when(resultSet.getInt("schema_count")).thenReturn(0);
    assertSchemaCount(connectionProvider, 0);

    dbQueries.createSchema();

    verify(statement).execute(contains("CREATE SCHEMA IF NOT EXISTS example_schema;"));
    when(resultSet.next()).thenReturn(true).thenReturn(false);
    when(resultSet.getInt("schema_count")).thenReturn(1);
    assertSchemaCount(connectionProvider, 1);
  }

  @Test
  void mustCreateTableInSchema() throws SQLException {
    dbQueries.createSchema();

    when(resultSet.getInt("table_count")).thenReturn(0);
    assertTablesInExampleSchemaCount(connectionProvider, 0);

    dbQueries.createTable("example_table");
    assertTablesInExampleSchemaCount(connectionProvider, 1);

    dbQueries.createTable("example_table2");
    assertTablesInExampleSchemaCount(connectionProvider, 2);
  }
}
