# SQL Migration API - Design Issues

## Core Problem

The migration API requires `SqlTable<?>` and `SqlColumn<?, ?>` instances for all operations, but in a real project only the **current** schema definitions exist in code. Migrations describe transitions between schema versions, so they inherently reference schema states that no longer (or don't yet) exist as Java objects.

## Affected Methods

### renameTable(SqlTable<?> from, SqlTable<?> to)

Requires both old and new `SqlTable` instances. In practice, only the current table definition exists in code.

**up()**: `renameTable(PERSON_TABLE, USER_TABLE)` — `PERSON_TABLE` doesn't exist in code, only `USER_TABLE` does.

**down()**: `renameTable(USER_TABLE, PERSON_TABLE)` — now `PERSON_TABLE` is the target, but it still doesn't exist in code.

Both directions always need a reference that the codebase doesn't have.

### renameColumn(SqlColumn<?, ?> from, SqlColumn<?, ?> to)

Same issue as `renameTable`. The old column definition (`FIRST_NAME`) no longer exists in code after it was renamed to `USERNAME`.

Additionally, `SqlColumn` is bound to a specific `SqlTable` via `bindTo()`. After a table rename, columns from the old table reference the wrong table name, and columns from the new table were never part of the old table.

### dropColumn(SqlColumn<?, ?> column)

The column being dropped no longer exists in the current schema. There is no `SqlColumn` instance to pass in.

### dropTable(SqlTable<?> table)

The table being dropped no longer exists in the current schema. There is no `SqlTable` instance to pass in.

### alterColumn(SqlColumn<?, C> column, Consumer<SqlMigrationColumnAlter<C>> changes)

After a `renameTable`, which `SqlColumn` should be used? The column instances are bound to their owning table. Using `USER_EMAIL` (bound to `USER_TABLE`) to alter a column on what was just renamed from `PERSON_TABLE` creates a mismatch — the column's `getOwningTable().getName()` may not reflect the table's current name within the migration.

The generic type parameter `C` also prevents type changes: `alterColumn(USER_VERSION, col -> col.setType(SqlTypes.INTEGER))` fails to compile because `USER_VERSION` is `SqlColumn<User, Long>` and `setType` expects `SqlType<Long>`, not `SqlType<Integer>`.

### Methods referencing tables after rename

After `renameTable(PERSON_TABLE, USER_TABLE)` within a migration, subsequent operations like `addColumn`, `createIndex`, `addCheckConstraint`, etc. need to reference the table. But:
- `PERSON_TABLE` has the old name
- `USER_TABLE` is a completely separate `SqlTable` instance with different columns bound to it

There is no way to reference "the table that was just renamed" with the correct current name.

## Deeper Structural Issue

`SqlTable` and `SqlColumn` represent the **current schema** — they are built once in a static initializer and are immutable. Migrations, however, describe **schema transitions** where the schema is in an intermediate state that matches neither the old nor the new Java definitions.

The renderer (`SqlMigrationRenderer`) already works with strings internally:
```java
case SqlRenameTableOperation op -> renderer.renderRenameTable(op.from().getName(), op.to().getName());
case SqlDropColumnOperation op -> renderer.renderDropColumn(op.column().getOwningTable().getName(), op.column().getName());
case SqlRenameColumnOperation op -> renderer.renderRenameColumn(op.from().getOwningTable().getName(), op.from().getName(), op.to().getName());
```

The typed objects are only used to extract `.getName()`, providing no additional type safety benefit in the migration context.

## Summary of Affected API Surface

| Method | Issue |
|---|---|
| `renameTable(SqlTable, SqlTable)` | Both old and new SqlTable needed, only current exists |
| `renameColumn(SqlColumn, SqlColumn)` | Both old and new SqlColumn needed, only current exists |
| `dropTable(SqlTable)` | Dropped table no longer exists in code |
| `dropColumn(SqlColumn)` | Dropped column no longer exists in code |
| `alterColumn(SqlColumn, ...)` | Column may be bound to wrong table after rename; generic prevents type changes |
| `addColumn(SqlColumn, SqlType)` | Column bound to current table, but table may have been renamed |
| `createIndex(SqlTable, ...)` | Table reference may be stale after rename |
| `dropIndex(SqlTable, ...)` | Table reference may be stale after rename |
| `renameIndex(SqlTable, ...)` | Table reference may be stale after rename |
| `addUniqueConstraint(SqlTable, ...)` | Table reference may be stale after rename |
| `addForeignKey(SqlTable, ..., SqlTable, ...)` | Both tables may have stale references |
| `addCheckConstraint(SqlTable, ...)` | Table reference may be stale after rename |
| `addCompositePrimaryKey(SqlTable, ...)` | Table reference may be stale after rename |
| `dropConstraint(SqlTable, ...)` | Table reference may be stale after rename |
