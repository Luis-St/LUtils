# Database Concept — Suggestions

1. **`returning()` clause on mutation queries** — Add a `returning()` method to insert/update/delete queries (the renderer already has `returningSyntax()`), avoiding a separate select after mutation.

2. **`SqlCondition.none()` and `SqlCondition.always()`** — Add identity elements for dynamic condition building (e.g., starting with `always()` and chaining `.and(...)` conditionally).

3. **JSON path extraction** — Add a path-based extraction method to `SqlJson` (e.g., `extract(SqlExpression<?> json, String path)`), since JSON path access is the most common JSON operation in queries.

4. **Transaction propagation semantics** — Consider whether propagation (REQUIRED, REQUIRES_NEW, NESTED) is needed. If intentionally omitted to keep things simple, document it as a deliberate decision.

5. **Enum-based dialect features** — Replace `SqlDialectFeatures` boolean methods with an enum approach (`boolean supports(SqlFeature feature)`) for easier extensibility. Alternatively, use default methods returning `false` to allow adding features without breaking existing implementations.

6. **Relationship loading strategy** — `SqlForeignColumn` and `SqlRelationshipNotLoadedException` imply lazy loading. Define the loading strategy (eager vs. lazy, join fetch API) explicitly, as it affects entity modeling and query building.

7. **Pluggable naming strategy** — Make `SqlColumnMapping` naming conversion (`snake_case` ↔ `camelCase`) pluggable, since not all projects use the same convention.

8. **Record type bound on `SqlRecordMapper<T>`** — If `T` must be a Java record, enforce it with a type bound to make the contract explicit.
