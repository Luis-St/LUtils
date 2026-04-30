# Layer 3: CRUD Queries

## Overview
The largest layer. Implement the internal state, builder methods, `toSql()` rendering, and JDBC execution for all four query types: SELECT, INSERT, UPDATE, DELETE.

## Files
- `src/main/java/net/luis/utils/io/database/query/crud/SqlSelectQuery.java`
- `src/main/java/net/luis/utils/io/database/query/crud/SqlInsertQuery.java`
- `src/main/java/net/luis/utils/io/database/query/crud/SqlUpdateQuery.java`
- `src/main/java/net/luis/utils/io/database/query/crud/SqlDeleteQuery.java`

## Dependencies
- Layer 1 (column rendering)
- Existing `SqlDialect` render methods for LIMIT/OFFSET, LOCK, SET_OPERATION, RETURNING

## Shared Infrastructure (implement first)

### Internal types needed across queries

**`JoinClause`** — package-private record or inner class:
```java
record JoinClause(JoinType type, SqlTable<?> table, SqlCondition on) {}
enum JoinType { INNER, LEFT, RIGHT, FULL, CROSS }
```

**`SetClause`** — for UPDATE queries:
```java
record SetClause<E, V>(SqlColumn<E, V> column, Object valueOrExpression, SetType type) {}
enum SetType { DIRECT, EXPRESSION, INCREMENT, DECREMENT }
```

### Row Mapping Strategy
A mechanism to map `ResultSet` rows to entity type `E`:
- Iterate columns of the `SqlTable<E>` in index order
- For each column, use `ResultSet.getObject(columnIndex)` or typed getters based on `SqlType`
- Construct entity `E` — this likely needs a constructor/factory function on `SqlTable`, or the query receives a row mapper

### Statement Execution Helper
Reusable across all query types:
```java
// For queries that return results (SELECT)
ResultSet executeQuery(Connection conn, SqlRendered rendered, Duration timeout)

// For queries that modify data (INSERT/UPDATE/DELETE)
int executeUpdate(Connection conn, SqlRendered rendered, Duration timeout)
```

---

## 3a. `SqlSelectQuery<E>`

### Internal State (fields)
```java
SqlTable<E> table
SqlDialect dialect
Connection connection
Duration queryTimeout
List<SqlExpression<?>> projections       // empty = SELECT *
boolean isDistinct
List<JoinClause> joins
SqlCondition whereCondition              // nullable
SqlCondition whereExistsSubquery         // nullable
List<SqlColumn<E, ?>> groupByColumns
SqlCondition havingCondition             // nullable
List<SqlOrderable<?>> orderByClauses
long limitValue = -1
long offsetValue = -1
SqlLockMode lockMode                     // nullable
boolean skipLocked
boolean noWait
List<SetOperationEntry> setOperations    // record: SqlSetOperation + SqlSelectQuery
List<SqlCommonTableExpression> ctes      // for WITH clauses
```

### Builder Methods
Each method sets the corresponding field and returns `this`:
- `where(condition)` — sets `whereCondition`
- `whereExists(subquery)` — sets where-exists subquery
- `groupBy(columns...)` — sets `groupByColumns`
- `having(condition)` — sets `havingCondition`
- `orderBy(orderables...)` — sets `orderByClauses`
- `limit(int)` — sets `limitValue`
- `offset(long)` — sets `offsetValue`
- `distinct()` — sets `isDistinct = true`
- `forUpdate()` — sets `lockMode = FOR_UPDATE`
- `forShare()` — sets `lockMode = FOR_SHARE`
- `skipLocked()` — sets `skipLocked = true`
- `noWait()` — sets `noWait = true`, check `dialect.isFeatureSupported(NO_WAIT)`
- `union(other)` / `unionAll(other)` / `intersect(other)` / `except(other)` — add to `setOperations`
- Join methods (`innerJoin`, `leftJoin`, etc.) — add `JoinClause` to `joins`
- `lateralJoin(subquery, alias)` — check feature support, add lateral join clause
- `asCommonExpression(alias, recursive)` — return new `SqlCommonTableExpression(alias, this, recursive)`
- `projectInto(type)` — create new query with different target type (for result projection)

### `toSql(SqlDialect)` Rendering
Build with `SqlRenderer` in this order:
1. **CTE prefix**: `WITH [RECURSIVE] alias AS (subquery)` if CTEs present
2. **SELECT**: `SELECT` + optional `DISTINCT`
3. **Projections**: Column list from projections, or `*` if empty
4. **FROM**: `FROM "table_name"`
5. **Joins**: For each join → `{INNER|LEFT|RIGHT|FULL|CROSS} JOIN "table" ON condition.toSql()`
6. **WHERE**: `WHERE condition.toSql(dialect)` if present; `WHERE EXISTS (subquery.toSql())` variant
7. **GROUP BY**: `GROUP BY col1, col2, ...`
8. **HAVING**: `HAVING condition.toSql(dialect)`
9. **Set operations**: `UNION|INTERSECT|EXCEPT otherQuery.toSql()`
10. **ORDER BY**: `ORDER BY orderable.toSql(dialect), ...`
11. **LIMIT/OFFSET**: `dialect.renderLimitOffset(limit, offset)`
12. **Lock clause**: `dialect.renderLockClause(mode, skipLocked, noWait)` if lock mode set

### Terminal Methods

| Method | Implementation |
|--------|---------------|
| `fetch()` | Execute query, map all `ResultSet` rows to `List<E>` |
| `fetchFirst()` | Internally set `LIMIT 1` if not already set, return `Optional.ofNullable(firstRow)` |
| `fetchOne()` | Fetch results, throw if not exactly 1 row |
| `fetchOneOrNull()` | Fetch results, return null if empty, throw if >1 |
| `count()` | Wrap query as `SELECT COUNT(*) FROM (originalQuery) AS __count`, execute, return long |
| `exists()` | Use `count() > 0` or wrap as `SELECT EXISTS(query)` |
| `stream()` | Return a `Stream` backed by lazy `ResultSet` iteration with proper `onClose` to release resources |
| `fetchPage(page, pageSize)` | Set limit=pageSize, offset=page*pageSize, execute fetch + count for totalElements, return `SimpleSqlPage` |

---

## 3b. `SqlInsertQuery<E>`

### Internal State
```java
SqlTable<E> table
SqlDialect dialect
Connection connection
Duration queryTimeout
List<E> entities
int batchSize = 0                           // 0 = no batching
SqlSelectQuery<?> fromSelect                // for INSERT...SELECT
SqlColumn<E, ?> conflictColumn              // for upsert
Function<E, E> onConflict                   // for upsert merge function
List<SqlColumn<E, ?>> conflictColumns       // for insertOrIgnore
boolean isUpsert, isInsertOrIgnore, isInsertFromSelect
```

### `toSql()` Rendering
1. `INSERT INTO "table"` + column list in parens: `("col1", "col2", ...)`
2. **Standard insert**: `VALUES (?, ?, ...) [, (?, ?, ...) ...]` for each entity
3. **Upsert**: append `ON CONFLICT ("column") DO UPDATE SET col1 = EXCLUDED.col1, ...`
4. **InsertOrIgnore**: append `ON CONFLICT (...) DO NOTHING`
5. **InsertFromSelect**: `INSERT INTO "table" (columns) SELECT ...` using `fromSelect.toSql()`

### Terminal Methods
| Method | Implementation |
|--------|---------------|
| `execute()` | Execute update, return affected row count. Use batch execution if `batchSize > 0` |
| `returning()` | Check `dialect.isFeatureSupported(RETURNING)`, append `dialect.renderReturning(columns)`, execute and map results |
| `fetchInserted()` | Same as `returning()` — use `Statement.RETURN_GENERATED_KEYS` as fallback for dialects without RETURNING |

---

## 3c. `SqlUpdateQuery<E>`

### Internal State
```java
SqlTable<E> table
SqlDialect dialect
Connection connection
Duration queryTimeout
List<SetClause<E, ?>> setClauses
List<JoinClause> joins
SqlCondition whereCondition
int batchSize
```

### Builder Methods
- `set(column, value)` — add `SetClause(column, value, DIRECT)`
- `set(column, expression)` — add `SetClause(column, expression, EXPRESSION)`
- `increment(column, value)` — add `SetClause(column, value, INCREMENT)`
- `increment(column, expression)` — add `SetClause(column, expression, INCREMENT)`
- `decrement(column, value)` — add `SetClause(column, value, DECREMENT)`
- `decrement(column, expression)` — add `SetClause(column, expression, DECREMENT)`
- `where(condition)` — sets `whereCondition`
- `batchSize(int)` — sets batch size
- Join methods — add to `joins` list

### `toSql()` Rendering
1. `UPDATE "table"`
2. Joins (if any — dialect-specific)
3. `SET` clause: for each `SetClause`:
   - `DIRECT`: `"col" = ?`
   - `EXPRESSION`: `"col" = expression.toSql()`
   - `INCREMENT`: `"col" = "col" + ?` (or `+ expression.toSql()`)
   - `DECREMENT`: `"col" = "col" - ?` (or `- expression.toSql()`)
4. `WHERE condition.toSql(dialect)`

### Terminal Methods
- `execute()` — returns affected row count
- `returning()` — appends RETURNING clause, returns updated entities

---

## 3d. `SqlDeleteQuery<E>`

### Internal State
```java
SqlTable<E> table
SqlDialect dialect
Connection connection
Duration queryTimeout
List<JoinClause> joins
SqlCondition whereCondition
int batchSize
```

### Builder Methods
- `where(condition)` — sets whereCondition
- `batchSize(int)` — sets batch size
- Join methods — add to joins list

### `toSql()` Rendering
1. `DELETE FROM "table"`
2. Joins (if any — dialect-specific)
3. `WHERE condition.toSql(dialect)`

### Terminal Methods
- `execute()` — returns affected row count
- `returning()` — appends RETURNING clause, returns deleted entities

---

## Implementation Order
1. Shared types (`JoinClause`, `SetClause`, row mapping helper)
2. `SqlSelectQuery` (most complex, most useful)
3. `SqlInsertQuery`
4. `SqlUpdateQuery`
5. `SqlDeleteQuery`

## Estimated Effort
~2-4 days depending on row mapping complexity
