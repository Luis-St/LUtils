# Layer 5: Supporting Types (`SqlPage`, `SqlCommonTableExpression`)

## Overview
Implement the two remaining supporting types that are referenced by the query layer but not yet functional.

## Files
- `src/main/java/net/luis/utils/io/database/SqlPage.java` (interface — already defined)
- New file: `src/main/java/net/luis/utils/io/database/SimpleSqlPage.java` (concrete implementation)
- `src/main/java/net/luis/utils/io/database/query/SqlCommonTableExpression.java`

## Dependencies
- Layer 3 (`SqlSelectQuery` uses `SqlPage` in `fetchPage()` and creates `SqlCommonTableExpression` in `asCommonExpression()`)

---

## 5a. `SimpleSqlPage<T>` — Concrete Page Implementation

### Design
Implement `SqlPage<T>` as a record:

```java
public record SimpleSqlPage<T>(
    List<T> content,
    int page,
    int pageSize,
    long totalElements
) implements SqlPage<T>
```

### Method Implementations

| Method | Implementation |
|--------|---------------|
| `content()` | Return `List.copyOf(content)` (defensive copy in constructor) |
| `page()` | Direct field access |
| `pageSize()` | Direct field access |
| `totalElements()` | Direct field access |
| `totalPages()` | `(int) Math.ceil((double) totalElements / pageSize)` |
| `hasNext()` | `page < totalPages() - 1` |
| `hasPrevious()` | `page > 0` |

### Validation (in constructor)
- `page >= 0`
- `pageSize > 0`
- `totalElements >= 0`
- `content` must not be null

### Usage
Created by `SqlSelectQuery.fetchPage(page, pageSize)`:
```java
public SqlPage<E> fetchPage(int page, int pageSize) {
    long total = this.count();
    List<E> content = this.limit(pageSize).offset((long) page * pageSize).fetch();
    return new SimpleSqlPage<>(content, page, pageSize, total);
}
```

---

## 5b. `SqlCommonTableExpression` — WITH Clause Support

### Internal State
```java
private final SqlAlias alias;
private final SqlSelectQuery<?> query;
private final boolean recursive;
```

### Constructor
```java
public SqlCommonTableExpression(SqlAlias alias, SqlSelectQuery<?> query, boolean recursive)
```
- Validate all arguments non-null
- Store immutably

### Rendering (`toSql` or a dedicated render method)
A single CTE renders as:
```sql
"alias" AS (SELECT ...)
```

The full WITH clause (with multiple CTEs) is assembled by the consuming `SqlSelectQuery`:
```sql
WITH [RECURSIVE] cte1 AS (...), cte2 AS (...)
SELECT ...
```

So the CTE itself only needs to render: `"alias" AS (query.toSql(dialect))`

### Integration with `SqlSelectQuery`
- `SqlSelectQuery` should accept a `List<SqlCommonTableExpression>` (or a single CTE is added via `with()`)
- In `SqlSelectQuery.toSql()`, prepend the WITH clause before SELECT:
  1. `WITH` keyword
  2. If any CTE is recursive → add `RECURSIVE`
  3. Comma-separated list of `cte.toSql(dialect)`
  4. Then the main SELECT

### API on `SqlSelectQuery`
```java
public SqlSelectQuery<E> with(SqlCommonTableExpression... ctes)
```

### Feature Check
- Before rendering, check `dialect.isFeatureSupported(SqlFeature.CTE)`
- For recursive CTEs, check `dialect.isFeatureSupported(SqlFeature.RECURSIVE_CTE)`
- Throw `SqlDialectUnsupportedRenderingException` if not supported

---

## Estimated Effort
- `SimpleSqlPage`: ~30 minutes
- `SqlCommonTableExpression`: ~1-2 hours (including `SqlSelectQuery` integration)
