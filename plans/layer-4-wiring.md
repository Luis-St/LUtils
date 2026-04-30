# Layer 4: Wiring (`SqlQueryProvider`, `SqlProvider` implementations)

## Overview
Connect the query and table provider implementations to `SqlDatabase` and `SqlTransaction` via the `SqlProvider` interface. This is largely mechanical — instantiate the correct objects with the right connection and dialect.

## Files
- `src/main/java/net/luis/utils/io/database/query/SqlQueryProvider.java`
- `src/main/java/net/luis/utils/io/database/SqlDatabase.java`
- `src/main/java/net/luis/utils/io/database/transaction/SqlTransaction.java`

## Dependencies
- Layer 2 (`SqlTableProvider` implementation)
- Layer 3 (all CRUD query implementations)

---

## 4a. `SqlQueryProvider<E>`

### Internal State
```java
private final SqlTable<E> table;
private final SqlDialect dialect;
private final Connection connection;
private final Duration queryTimeout;
```

### Factory Method Implementations

**Select overloads:**
- `select()` → `new SqlSelectQuery<>(table, dialect, connection, queryTimeout)` with all columns as projections
- `select(SqlExpression<E1> e1)` → `new SqlSelectQuery<>` with `e1` as projection
- `select(e1, e2)` through `select(e1...e16)` → same pattern, each with the corresponding projections list, returning `SqlSelectQuery<SqlRowN<...>>`
- `select(SqlExpression<?>... expressions)` → varargs version, returns `SqlSelectQuery<?>`
- `subquery(expressions...)` → same as `select(expressions...)` but marks the query as a subquery (no direct execution, only used as sub-select)

**Insert overloads:**
- `insert(E entity)` → `new SqlInsertQuery<>(table, dialect, connection, queryTimeout, List.of(entity))`
- `insert(E... entities)` → same with `List.of(entities)`
- `insert(Collection<E>)` → same with `List.copyOf(entities)`
- `insert(Collection<E>, batchSize)` → same + set batch size
- `upsert(entity, conflictColumn, onConflict)` → `SqlInsertQuery` with upsert flag + conflict config
- `insertOrIgnore(entity, conflictColumns...)` → `SqlInsertQuery` with insertOrIgnore flag
- `insertFromSelect(query)` → `SqlInsertQuery` with fromSelect flag

**Update/Delete:**
- `update()` → `new SqlUpdateQuery<>(table, dialect, connection, queryTimeout)`
- `delete()` → `new SqlDeleteQuery<>(table, dialect, connection, queryTimeout)`

---

## 4b. `SqlDatabase` — `SqlProvider` Methods

The database acquires a **new connection** from `dataSource` for each operation (outside of transactions).

### Schema Operations

| Method | Implementation |
|--------|---------------|
| `createSchema(name)` | Acquire connection, render `CREATE SCHEMA "name"` via `SqlRenderer`, execute, close connection |
| `createSchemaIfNotExists(name)` | Same with `IF NOT EXISTS` (check `dialect.isFeatureSupported(SCHEMAS)` first) |
| `existsSchema(name)` | Acquire connection, use `connection.getMetaData().getSchemas()`, filter by name, return boolean, close connection |
| `dropSchema(name, cascade)` | Render `DROP SCHEMA "name" [CASCADE]`, execute, close connection |

### Provider Methods

| Method | Implementation |
|--------|---------------|
| `table(SqlTable<T>)` | `new SqlTableProvider<>(table, dialect, dataSource.getConnection(), queryTimeout)` |
| `from(SqlTable<T>)` | `new SqlQueryProvider<>(table, dialect, dataSource.getConnection(), queryTimeout)` |

### Connection Management
- Each call to `table()` or `from()` gets a fresh connection from the `DataSource`
- The connection should be managed/closed by the returned provider (or by the caller)
- Consider whether the provider should close the connection after terminal operations

### `health()` and `ping()` Methods (bonus, currently stubbed)
- `health()` → try `dataSource.getConnection().isValid(timeout)`, return true/false
- `ping()` → execute `SELECT 1` and return true if successful

---

## 4c. `SqlTransaction` — `SqlProvider` Methods

Same as `SqlDatabase` but uses `this.connection` (the transaction's existing connection) instead of acquiring a new one. This ensures transactional consistency.

### Schema Operations
Identical logic to `SqlDatabase` but operating on `this.connection`:
- `createSchema(name)` → render and execute on `this.connection`
- `createSchemaIfNotExists(name)` → same
- `existsSchema(name)` → `this.connection.getMetaData().getSchemas()`
- `dropSchema(name, cascade)` → render and execute on `this.connection`

### Provider Methods
| Method | Implementation |
|--------|---------------|
| `table(SqlTable<T>)` | `new SqlTableProvider<>(table, dialect, this.connection, queryTimeout)` |
| `from(SqlTable<T>)` | `new SqlQueryProvider<>(table, dialect, this.connection, queryTimeout)` |

**Key difference:** The transaction's providers must NOT close the connection — the connection lifecycle is owned by the transaction.

---

## Design Considerations

### Connection ownership
- `SqlDatabase` providers own their connections (should close on completion)
- `SqlTransaction` providers borrow the transaction connection (must not close it)
- Consider a boolean `ownsConnection` flag in `SqlTableProvider` and `SqlQueryProvider`

### Dialect access
- The `SqlTransaction` currently has no reference to the `SqlDialect` — it needs one
- Either pass it through the constructor (from `SqlTransactionManager`) or store it on the `SqlDatabase` and pass it when creating transactions

### Query timeout
- `SqlTransaction` also has no reference to `queryTimeout` — needs to be passed through, or the transaction uses its own `timeout` field for queries

## Estimated Effort
~1 day
