# Layer 2: DDL Execution (`SqlTableProvider`)

## Overview
Implement all DDL operations in `SqlTableProvider` — table creation, dropping, truncation, and index management. The rendering logic already exists in `AbstractSqlDialect`; this layer executes the rendered SQL via JDBC.

## Files
- `src/main/java/net/luis/utils/io/database/table/SqlTableProvider.java`

## Dependencies
- Layer 1 (column rendering is used transitively by `renderCreateTable`)

## Internal State (fields to add)

```java
private final SqlTable<E> table;
private final SqlDialect dialect;
private final Connection connection;
private final Duration queryTimeout;
```

These are passed in via the constructor (created by `SqlDatabase.table()` or `SqlTransaction.table()`).

## Helper: Statement Execution

Create a private helper method to avoid repetition across all DDL methods:

```java
private void executeStatement(SqlRendered rendered) throws SqlException
```

This method should:
1. Create a `PreparedStatement` from `rendered.sql()`
2. Bind `rendered.parameters()` positionally (index starting at 1)
3. Set the query timeout via `statement.setQueryTimeout()`
4. Execute the statement
5. Close the statement (use try-with-resources)
6. Wrap any `SQLException` in an `SqlException`

## Tasks

### 2a. Table Lifecycle Methods

| Method | Implementation |
|--------|---------------|
| `create()` | `executeStatement(dialect.renderCreateTable(table, false))` |
| `createIfNotExists()` | `executeStatement(dialect.renderCreateTable(table, true))` |
| `exists()` | Use `connection.getMetaData().getTables(null, table.getSchema(), table.getName(), new String[]{"TABLE"})` — return `resultSet.next()` |
| `truncate()` | `executeStatement(dialect.renderTruncateTable(table))` |
| `drop()` | `executeStatement(dialect.renderDropTable(table, false))` |
| `dropIfExists()` | `executeStatement(dialect.renderDropTable(table, true))` |

### 2b. Index Operations

| Method | Implementation |
|--------|---------------|
| `createIndex(name, columns, method)` | Build `new SqlIndex(name, columns, false, null, method)`, then `executeStatement(dialect.renderCreateIndex(index))` |
| `createIndex(name, columns, unique, method)` | Build `new SqlIndex(name, columns, unique, null, method)`, then execute |
| `createIndex(name, columns, unique, where, method)` | Build `new SqlIndex(name, columns, unique, where, method)`, then execute |
| `getIndexes()` | Use `connection.getMetaData().getIndexInfo(null, schema, tableName, false, false)` — iterate ResultSet, group by index name, map to `SqlIndex` records. Return unmodifiable list. |
| `dropIndex(name)` | Build a minimal `SqlIndex` with the name and table's first column (for table context), render via `dialect.renderDropIndex(index)`, execute |

### 2c. Validation
- All methods should check that the connection is not closed before executing
- `createIndex` should validate that all columns belong to the table
- `createIndex` should check `dialect.isIndexMethodSupported(method)` and throw early if unsupported

## Estimated Effort
~2-3 hours
