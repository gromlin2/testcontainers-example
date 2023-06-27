package com.github.gromlin2.testcontainersexample;

import static com.github.gromlin2.testcontainersexample.Assertions.assertSchemaCount;
import static com.github.gromlin2.testcontainersexample.Assertions.assertTablesInExampleSchemaCount;

import com.github.gromlin2.testcontainersexample.DbQueries.ConnectionProvider;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

class DbQueriesTestcontainerTest {

  private static PostgreSQLContainer<?> postgreSQLContainer =
      new PostgreSQLContainer("postgres:15.3");

  private static DbQueries dbQueries;
  private static ConnectionProvider connectionProvider;

  @BeforeAll
  static void setUp() {
    postgreSQLContainer.start();
    connectionProvider = () -> postgreSQLContainer.createConnection("");
    dbQueries = new DbQueries(connectionProvider);
  }

  @AfterAll
  static void tearDown() {
    postgreSQLContainer.stop();
  }

  @BeforeEach
  void cleanup() throws SQLException {
    postgreSQLContainer
        .createConnection("")
        .createStatement()
        .execute("DROP SCHEMA IF EXISTS example_schema CASCADE;");
  }

  @Test
  void mustCreateSchema() throws SQLException {
    assertSchemaCount(connectionProvider, 0);
    dbQueries.createSchema();
    assertSchemaCount(connectionProvider, 1);
  }

  @Test
  void mustCreateTableInSchema() throws SQLException {
    dbQueries.createSchema();
    assertTablesInExampleSchemaCount(connectionProvider, 0);

    dbQueries.createTable("example_table");
    assertTablesInExampleSchemaCount(connectionProvider, 1);

    dbQueries.createTable("example_table2");
    assertTablesInExampleSchemaCount(connectionProvider, 2);
  }
}
