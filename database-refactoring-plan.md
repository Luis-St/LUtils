# Database Dialect System Refactoring Plan

## Context

The current SQL dialect system in `net.luis.utils.io.database` has several design problems:
- `SqlDialect<T, C>` is a "god class" mixing identity, type mapping, and SQL rendering -- all 17 methods throw `UnsupportedOperationException`
- All 5 dialect implementations (Default, Postgres, MySQL, MariaDB, SQLite) are empty shells with identical `extends SqlDialect<SqlTable<?>, SqlColumn<?>>`
- The generic parameters `<T, C>` on SqlDialect were meant to enable dialect-specific table/column return types but are never actually used
- `SqlTable` extends `SqlQueryProvider`, conflating schema definition with query building
- SQL generation is coupled to `SqlRenderable#toSql(SqlDialect<?, ?>)` where the dialect is just a bag of helper methods
- The YML schema / code generation approach is being dropped in favor of pure Java entity definitions

This refactoring creates a clean, extensible dialect system with `SqlDefaultDialect` + `PostgresDialect`, dialect-specific table/column/query types, and a separate `SqlRenderer` hierarchy for SQL generation.

---

## Design Decisions (from user)

1. **`PostgresTable<Person>`** -- dialect-specific table subclasses bound at declaration time
2. **Full dialect extensions** -- both DDL and DML differ per dialect (Postgres adds `onConflictDoNothing`, `RETURNING`, `DISTINCT ON`, etc.)
3. **SqlRenderer per dialect (base + override)** -- `SqlDefaultRenderer` handles standard SQL, `PostgresSqlRenderer extends SqlDefaultRenderer` overrides Postgres-specific parts
4. **Remove MySQL/MariaDB/SQLite** -- focus on SqlDefaultDialect + PostgresDialect only
5. **Mutable builder** queries -- `query.where(cond)` mutates and returns `this`

---

## Phase 1: Foundation -- Create SqlRenderer hierarchy

### Step 1.1: Create `SqlRenderer` interface
- **New file:** `src/main/java/net/luis/utils/io/database/renderer/SqlRenderer.java`
- Methods moved from `SqlDialect`: `quoteIdentifier()`, `nowFunction()`, `uuidFunction()`, `autoIncrementSyntax()`, `createTableSuffix()`, `supportsIfNotExists()`, `limitOffsetSyntax()`, `upsertSyntax()`, `returningSyntax()`, `booleanLiteral()`, `stringConcatOperator()`, `parameterPlaceholder()`
- Add: `getDialect()` returning `SqlDialect`

### Step 1.2: Create `SqlDefaultRenderer`
- **New file:** `src/main/java/net/luis/utils/io/database/renderer/SqlDefaultRenderer.java`
- `class SqlDefaultRenderer implements SqlRenderer`
- Constructor takes `SqlDialect`
- Provides ANSI SQL default implementations for all rendering methods

---

## Phase 2: Refactor SqlDialect -- remove generics and rendering

### Step 2.1: Simplify `SqlDialect`
- **Modify:** `src/main/java/net/luis/utils/io/database/dialect/SqlDialect.java`
- Remove generic parameters `<T, C>`
- Remove all 12 rendering/syntax methods (moved to `SqlRenderer`)
- Keep: `getId()`, `getName()`, `getFeatures()`, `mapColumnType()`, `mapJavaType()`
- Add: `createRenderer()` returning `SqlRenderer`

### Step 2.2: Update `SqlDefaultDialect`
- **Modify:** `src/main/java/net/luis/utils/io/database/dialect/SqlDefaultDialect.java`
- Remove generic params from `extends SqlDialect`
- Implement abstract methods (id, name, features, type mapping, createRenderer -> SqlDefaultRenderer)

### Step 2.3: Update `PostgresDialect`
- **Modify:** `src/main/java/net/luis/utils/io/database/dialect/PostgresDialect.java`
- Change to extend `SqlDefaultDialect` (inherits defaults, overrides Postgres specifics)
- Implement: Postgres-specific type mapping, features, createRenderer -> PostgresSqlRenderer

### Step 2.4: Delete removed dialects and registry
- **Delete:** `dialect/MysqlDialect.java`
- **Delete:** `dialect/MariaDialect.java`
- **Delete:** `dialect/SqliteDialect.java`
- **Delete:** `dialect/SqlDialectRegistry.java`

### Step 2.5: Trim `SqlDialectFeatures`
- **Modify:** `src/main/java/net/luis/utils/io/database/dialect/SqlDialectFeatures.java`
- Remove features for deleted databases: `supportsOutputClause()`, `supportsRowId()`, `supportsTopClause()`, `supportsConnectBy()`

---

## Phase 3: Update SqlRenderable cascade

### Step 3.1: Update `SqlRenderable`
- **Modify:** `src/main/java/net/luis/utils/io/database/SqlRenderable.java`
- Change: `toSql(SqlDialect<?, ?> dialect)` -> `toSql(SqlRenderer renderer)`

### Step 3.2: Update all `toSql` overrides (14 files)
Change `toSql(@NonNull SqlDialect<?, ?> dialect)` to `toSql(@NonNull SqlRenderer renderer)` in:
- `query/SqlSelectQueryBase.java` (line 328)
- `query/SqlInsertQuery.java` (line 78)
- `query/SqlUpdateQuery.java` (line 121)
- `query/SqlDeleteQuery.java` (line 63)
- `migration/SqlMigration.java` (line 71)

These extend `SqlRenderable` and inherit the change automatically (no explicit `toSql` override to change):
- `condition/SqlCondition.java`
- `condition/SqlOrderable.java`
- `function/window/SqlWindowClause.java`
- `function/window/SqlWindowFrame.java`
- `function/window/SqlFrameBound.java`
- `migration/SqlSchemaDiff.java`

---

## Phase 4: Decouple SqlTable from SqlQueryProvider

### Step 4.1: Refactor `SqlTable`
- **Modify:** `src/main/java/net/luis/utils/io/database/table/SqlTable.java`
- Remove `extends SqlQueryProvider<T>`
- Remove `getDialect()`
- Move DDL methods to `SqlQueryProvider`: `create()`, `createIfNotExists()`, `drop()`, `dropIfExists()`, `truncate()`, `exists()`
- Move index/sequence methods to `SqlQueryProvider`: `createIndex()`, `dropIndex()`, `listIndexes()`, `createSequence()`, `nextSequenceValue()`
- Keep: `of()` factory, `column()`, `foreignColumn()`, `versionColumn()`, `addListener()`, `removeListener()`
- Add: `getName()`, `getType()`

### Step 4.2: Refactor `SqlColumn`
- **Modify:** `src/main/java/net/luis/utils/io/database/table/SqlColumn.java`
- Remove `getDialect()` method (lines 46-51)

### Step 4.3: Refactor `SqlQueryProvider`
- **Modify:** `src/main/java/net/luis/utils/io/database/query/SqlQueryProvider.java`
- Remove `from(SqlDatabase)` (moves to SqlDatabase)
- Remove `dialect(SqlDialect<TT, CC>)` (replaced by typed tables)
- Add DDL methods absorbed from SqlTable
- Add index/sequence methods absorbed from SqlTable

### Step 4.4: Update `SqlDatabase`
- **Modify:** `src/main/java/net/luis/utils/io/database/SqlDatabase.java`
- Change `SqlDialect<?, ?> getDialect()` -> `SqlDialect getDialect()`
- Add: `SqlRenderer getRenderer()`
- Add: `static SqlDatabase of(SqlDatabaseConfig config)` factory
- Add: `<T> SqlQueryProvider<T> from(SqlTable<T> table)`

---

## Phase 5: Create Postgres-specific types

All new files go in: `src/main/java/net/luis/utils/io/database/dialect/postgres/`

### Step 5.1: `PostgresColumn<T>` (extends `SqlColumn<T>`)
- Postgres-specific conditions: `ilike()`, `similarTo()`
- JSONB operations: `jsonField()`, `jsonPath()`
- Array operations: `arrayContains()`, `arrayOverlaps()`
- Full-text search: `tsMatch()`

### Step 5.2: `PostgresTable<T>` (extends `SqlTable<T>`)
- Override `column()` -> returns `PostgresColumn<C>`
- Add: `jsonbColumn()`, `arrayColumn()`
- Add: `from(SqlDatabase)` -> returns `PostgresQueryProvider<T>`
- Static factory: `PostgresTable.of(name, type)`

### Step 5.3: Postgres query interfaces
- **`PostgresSelectQuery<T>`** extends `SqlSelectQuery<T>` -- narrows return types for chaining
- **`PostgresInsertQuery<T>`** extends `SqlInsertQuery<T>` -- adds `onConflictDoNothing()`, `onConflict(columns)`, enhanced `returning(expressions...)`
- **`PostgresUpdateQuery<T>`** extends `SqlUpdateQuery<T>` -- adds `from(SqlTable)` clause, enhanced `returning(expressions...)`
- **`PostgresDeleteQuery<T>`** extends `SqlDeleteQuery<T>` -- adds `using(SqlTable)`, `returning()`, `returning(expressions...)`

### Step 5.4: `PostgresQueryProvider<T>` (extends `SqlQueryProvider<T>`)
- Narrows return types: `select()` -> `PostgresSelectQuery<T>`, `insert()` -> `PostgresInsertQuery<T>`, etc.
- Add: `truncateCascade()`

### Step 5.5: `PostgresSqlRenderer` (extends `SqlDefaultRenderer`)
- Override Postgres-specific rendering: identifier quoting with `"`, `gen_random_uuid()`, `GENERATED ALWAYS AS IDENTITY`, `ON CONFLICT DO UPDATE`, `$1/$2` parameter placeholders, etc.

---

## Phase 6: Module and config updates

### Step 6.1: Update `module-info.java`
- Add: `exports net.luis.utils.io.database.renderer;`
- Add: `exports net.luis.utils.io.database.dialect.postgres;`

### Step 6.2: Update `DatabaseTest.java`
- Rewrite to demonstrate both generic and Postgres-specific APIs using the new types

---

## Files Summary

### Delete (4 files)
- `dialect/MysqlDialect.java`
- `dialect/MariaDialect.java`
- `dialect/SqliteDialect.java`
- `dialect/SqlDialectRegistry.java`

### Create (10 new files)
- `renderer/SqlRenderer.java`
- `renderer/SqlDefaultRenderer.java`
- `dialect/postgres/PostgresSqlRenderer.java`
- `dialect/postgres/PostgresTable.java`
- `dialect/postgres/PostgresColumn.java`
- `dialect/postgres/PostgresQueryProvider.java`
- `dialect/postgres/PostgresSelectQuery.java`
- `dialect/postgres/PostgresInsertQuery.java`
- `dialect/postgres/PostgresUpdateQuery.java`
- `dialect/postgres/PostgresDeleteQuery.java`

### Modify (12 files)
- `dialect/SqlDialect.java` -- remove generics + rendering methods, add `createRenderer()`
- `dialect/SqlDefaultDialect.java` -- implement abstract methods
- `dialect/PostgresDialect.java` -- extend SqlDefaultDialect, implement methods
- `dialect/SqlDialectFeatures.java` -- remove 4 dead features
- `SqlRenderable.java` -- change `toSql` param to `SqlRenderer`
- `SqlDatabase.java` -- add `from()`, `getRenderer()`, `of()`
- `table/SqlTable.java` -- remove `extends SqlQueryProvider`, remove dialect, add `getName()`/`getType()`
- `table/SqlColumn.java` -- remove `getDialect()`
- `query/SqlQueryProvider.java` -- absorb DDL, remove `from()`/`dialect()`
- `query/SqlInsertQuery.java`, `query/SqlUpdateQuery.java`, `query/SqlDeleteQuery.java`, `query/SqlSelectQueryBase.java` -- update `toSql` signature
- `migration/SqlMigration.java` -- update `toSql` signature
- `module-info.java` -- add new package exports
- `DatabaseTest.java` -- rewrite with new API

---

## Verification

1. **Compile check** -- `mvn compile` or `gradle build` should pass (all files are interfaces/stubs)
2. **API usage** -- `DatabaseTest.java` demonstrates both generic and Postgres-specific flows:
   ```java
   // Generic
   SqlTable<Person> table = SqlTable.of("person", Person.class);
   db.from(table).select().where(id.equalTo(1)).fetch();

   // Postgres-specific
   PostgresTable<Person> pgTable = PostgresTable.of("person", Person.class);
   pgTable.from(db).insert(person).onConflictDoNothing().execute();
   pgTable.from(db).select().distinctOn(name).fetch();
   ```
3. **Extensibility** -- adding a new dialect (e.g., MySQL) requires:
   - `MysqlDialect extends SqlDefaultDialect`
   - `MysqlSqlRenderer extends SqlDefaultRenderer`
   - `MysqlTable<T> extends SqlTable<T>`
   - `MysqlColumn<T> extends SqlColumn<T>`
   - `MysqlQueryProvider<T> extends SqlQueryProvider<T>`
   - Dialect-specific query subinterfaces as needed
