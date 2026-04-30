# Implementation Order

## Critical Path
```
Layer 1 → Layer 3 (SelectQuery) → Layer 4 → end-to-end working read path
```

## Full Order

| Order | Layer | File | Estimated Effort | Rationale |
|-------|-------|------|-----------------|-----------|
| 1 | [Layer 1](layer-1-rendering-foundation.md) | `SqlColumn.toSql()` + `SqlColumn.of(alias)` | ~30 min | Foundation — everything else renders column references |
| 2 | [Layer 5a](layer-5-supporting-types.md) | `SimpleSqlPage<T>` | ~30 min | Trivial, needed before SelectQuery terminals |
| 3 | [Layer 3](layer-3-crud-queries.md) | `SqlSelectQuery` | ~1-2 days | Core work — unlocks the most value |
| 4 | [Layer 3](layer-3-crud-queries.md) | `SqlInsertQuery` | ~0.5-1 day | Second most useful query type |
| 5 | [Layer 3](layer-3-crud-queries.md) | `SqlUpdateQuery` | ~0.5 day | Simpler than SELECT/INSERT |
| 6 | [Layer 3](layer-3-crud-queries.md) | `SqlDeleteQuery` | ~0.5 day | Simplest query type |
| 7 | [Layer 4](layer-4-wiring.md) | `SqlQueryProvider` + `SqlDatabase`/`SqlTransaction` provider methods | ~1 day | Wires everything together — end-to-end works after this |
| 8 | [Layer 2](layer-2-ddl-execution.md) | `SqlTableProvider` | ~2-3 hours | DDL execution, mostly delegates to existing dialect renders |
| 9 | [Layer 5b](layer-5-supporting-types.md) | `SqlCommonTableExpression` | ~1-2 hours | CTE support, requires working SelectQuery |
| 10 | [Layer 6](layer-6-migrations.md) | Migration interfaces + runner | ~3-5 days | Most design-heavy, depends on everything above |

## Milestones

### Milestone 1: First query runs (Layers 1 + 3a + 4)
After implementing Layer 1, `SqlSelectQuery`, and the wiring in Layer 4, you can execute:
```java
database.from(usersTable).select().where(...).fetch();
```

### Milestone 2: Full CRUD (Layers 3b-3d)
All four query types work — SELECT, INSERT, UPDATE, DELETE.

### Milestone 3: Complete DDL (Layer 2)
Tables can be created, dropped, truncated, and indexed programmatically.

### Milestone 4: Advanced queries (Layer 5b)
CTEs and pagination are fully supported.

### Milestone 5: Schema evolution (Layer 6)
Versioned migrations with automatic history tracking.

## Total Estimated Effort
~7-11 days of focused implementation
