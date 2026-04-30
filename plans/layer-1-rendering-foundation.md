# Layer 1: Rendering Foundation (`SqlColumn`)

## Overview
Implement the two missing methods on `SqlColumn` that form the base for all SQL rendering of column references.

## Files
- `src/main/java/net/luis/utils/io/database/table/SqlColumn.java`

## Dependencies
None — only uses existing `SqlDialect`, `SqlRenderer`, `SqlRendered`.

## Tasks

### 1a. `SqlColumn.toSql(SqlDialect)` (line 177)
Render a qualified column reference (e.g. `"users"."name"`).

**Approach:**
- Use `dialect.quoteIdentifier()` for both the owning table name and the column name
- If the column has an owning table bound (via `bindTo()`), render as `"table_name"."column_name"`
- If no owning table is bound yet, render just the quoted column name
- Use `SqlRendered.of(quotedTable + "." + quotedColumn)` — matches the style of `AbstractSqlDialect.renderQualifiedName()`

**Example output:**
```sql
"users"."email"
```

### 1b. `SqlColumn.of(SqlAlias)` (line 173)
Create an expression that references a column through an alias, used for subqueries, CTEs, and joins.

**Approach:**
- Return a new `SqlAliasedExpression<C>` (or a dedicated internal expression class) that renders as `"alias"."column_name"` instead of `"table"."column_name"`
- The simplest approach: create a lightweight expression that overrides `toSql()` to use the alias name in place of the table name
- Alternatively, reuse `SqlAliasedExpression` if the dialect rendering path already handles alias-qualified column references

**Example output:**
```sql
"u"."email"   -- where "u" is the alias
```

## Estimated Effort
~30 minutes
