# Query API - Extended Documentation

## Overview

This directory contains detailed elaborations of the Query API concept. These documents expand on areas that were undefined or briefly mentioned in the main [03-query-api.md](../03-query-api.md) document.

## Documents

| Document | Description |
|----------|-------------|
| [01-transaction-api.md](01-transaction-api.md) | Explicit transaction management with begin/commit/rollback, savepoints, isolation levels |
| [02-async-api.md](02-async-api.md) | CompletableFuture-based async operations for all query types |
| [03-connection-management.md](03-connection-management.md) | Connection pooling, multiple data sources, read replicas, health checks |
| [04-entity-mapping.md](04-entity-mapping.md) | Result set to entity mapping, type converters, projections, tuples |
| [05-error-handling.md](05-error-handling.md) | Exception hierarchy, SQL error translation, constraint violations |
| [06-relationship-loading.md](06-relationship-loading.md) | Explicit join-based loading strategies, avoiding N+1 queries |
| [07-query-execution.md](07-query-execution.md) | Prepared statement caching, timeouts, streaming, batching |
| [08-auditing.md](08-auditing.md) | Automatic timestamps, user tracking, audit trails |
| [09-testing.md](09-testing.md) | Test utilities, fixtures, mocks, Testcontainers integration |
| [10-dialect-system.md](10-dialect-system.md) | Database-specific SQL generation, type mappings, feature detection |

## Design Decisions Summary

Based on the elaboration process, the following design decisions were made:

### Transactions
- **Explicit API** - Transactions use `try (Transaction tx = Database.beginTransaction())` pattern
- No annotation-based (`@Transactional`) magic
- Savepoints supported for partial rollback

### Async Operations
- **CompletableFuture** for all async operations
- Naming convention: `fetch()` → `fetchAsync()`
- Virtual threads recommended for Java 21+

### Relationship Loading
- **Explicit only** - No lazy loading proxies
- Relationships loaded via explicit joins or separate batch queries
- Developer controls all SQL queries executed

### Optimistic Locking
- **Not supported** - Users handle concurrency manually
- Simplifies the API and entity design

## Open Questions

Each document contains an "Open Questions" section with unresolved design decisions. Key questions include:

1. **Transactions**: Connection binding strategy, distributed transaction support?
2. **Async**: Backpressure handling, reactive interop (Mono/Flux)?
3. **Connections**: HikariCP integration vs custom pool, sharding support?
4. **Entity Mapping**: Immutable-only or mutable classes, embedded types?
5. **Errors**: Batch error handling (all-or-nothing vs partial)?
6. **Relationships**: Deep graph loading, identity map?
7. **Execution**: Server-side cursors, parallel query hints?
8. **Auditing**: Field-level change tracking, async audit logging?
9. **Testing**: Parallel test execution with shared database?
10. **Dialects**: Version-specific features, cross-dialect compatibility mode?

## Related Main Documents

- [00-overview.md](../00-overview.md) - Architecture overview
- [01-yaml-schema.md](../01-yaml-schema.md) - Schema definitions
- [02-code-generation.md](../02-code-generation.md) - Code generation pipeline
- [03-query-api.md](../03-query-api.md) - Main query API document (this elaboration's source)
- [04-dialect-system.md](../04-dialect-system.md) - Original dialect document
- [05-orm-features.md](../05-orm-features.md) - ORM and relationship handling
