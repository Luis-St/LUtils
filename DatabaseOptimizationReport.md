# Optimization Report — `net.luis.utils.io.database`

**Scope:** full read-through of the database subsystem (dialect layer, rendering, type system,
query building/execution, transactions, row mapping, migrations).
**Status:** analysis only — no source files were modified.
**Caveat:** items flagged as *correctness gaps* are derived from SQL-dialect semantics and code reading,
**not** from execution against live databases. Verify each against the target DB before acting.

---

## 1. Architecture overview

The subsystem is large but consistently structured:

- **`SqlDialect`** — a ~40-method interface combining capability probes
  (`isFeatureSupported`, `isTypeSupported`, `isIndexMethodSupported`), type-name mapping,
  and ~20 `render*` methods.
- **`AbstractSqlDialect`** — common implementation. Defaults emit ANSI-ish SQL; unsupported
  operations either return `false` from a probe **or throw `SqlDialectUnsupportedRenderingException`**
  from inside a render method.
- **6 concrete dialects** — `H2`, `MySql`, `MariaDb extends MySql`, `PostgresSql`, `Sqlite`,
  `SqlServer`, registered as singletons in `SqlDialects`.
- **Renderer mesh** — rendering is split into small renderer classes
  (`SqlNumericFunctionRenderer`, `SqlStringConditionRenderer`, `SqlTableRenderer`, …) assembled by
  `SqlDialectRenderer.Builder`. A dialect customizes behaviour by subclassing one renderer and
  registering it in `createRenderer()`. Renderers are built once per dialect instance (effectively cached).
- **Dispatch** — every renderer uses a closed `switch` over the type hierarchy with `protected renderXxx(...)`
  hooks and a `default -> throw "Unknown … type"`.
- **Query layer** — immutable builders (`SqlSelectQuery`, `SqlInsertQuery`, …) that re-render to a
  `SqlRendered` (token list + bound parameters) on execution, then run through `SqlQueryExecutor`.
- **`SqlRenderer` / `SqlRendered`** — a fluent token accumulator; `SqlRendered.sql()` joins tokens with spaces.

The design is sound. Findings below are grouped into **structural** issues (the dialect capability model),
**dialect-specific SQL gaps** (the "better native SQL" the system leaves on the table, plus several
latent correctness bugs), and **cross-cutting issues** found in the wider sweep (query/exec/txn/mapping/migration).

---

## 2. Part A — Structural opportunities (dialect capability model)

### A1. The capability model is split and implicit
Two parallel, unsynchronized mechanisms answer "does this dialect support X":

1. `SqlFeature` enum + `isFeatureSupported()` — declarative, queryable up front.
2. `throw SqlDialectUnsupportedRenderingException` buried in render methods — discoverable only by calling.

They don't cover the same surface:
- `SqliteDialect.renderLockClause` throws, but row-locking isn't a `SqlFeature` SQLite simply omits.
- `SqlServerDialect` throws for upsert / insert-or-ignore, but there is **no** `UPSERT` / `INSERT_OR_IGNORE` feature flag to probe.
- `SqliteColumnRenderer`, `SqliteMigrationOperationRenderer`, `SqliteIndexRenderer`, `SqlServerIndexRenderer`
  throw for ALTER COLUMN / ADD CONSTRAINT / RENAME INDEX — none of which appear in `SqlFeature`.

**Recommendation:** make `SqlFeature` the single source of truth. Add the missing capabilities
(`UPSERT`, `INSERT_OR_IGNORE`, `RENAME_INDEX`, `ALTER_COLUMN`, `ADD_CONSTRAINT`, `ARRAY_TYPE`,
`ROW_LOCKING`, `IS_DISTINCT_FROM`, …) and guard each render method on its feature with one consistent
message, so callers can branch *before* building SQL instead of catching exceptions.

### A2. Exception-as-control-flow
Most limitations are discoverable only by rendering and catching — expensive (stack traces) and awkward for
a builder that wants a fallback. Pair every "throw if unsupported" with a cheap boolean probe (see A1).

### A3. No emulation / fallback tier
When a feature is missing, the only outcomes are *generic SQL* or *exception*. A middle tier — dialect-specific
**emulation** — is where most value is:
- SQL Server upsert → `MERGE` (instead of throwing).
- SQLite `ALTER COLUMN` / `ADD CONSTRAINT` → the documented table-rebuild dance (the exception message even
  *names* the workaround but offers no path).
- Case-insensitive equality → `ILIKE`/`citext` on Postgres instead of `UPPER()=UPPER()`.

### A4. The type system has no native-type extension point
`SqlTypes` is a fixed catalogue of JDBC-backed scalar/parameterized types, and `getTypeName` switches on
`java.sql.Types`. There's no way to express dialect-native types — **UUID, JSON/JSONB, INET, ENUM, geometry**,
or arrays outside Postgres. `isTypeSupported` is a yes/no with no negotiation: a dialect can't say
"no native UUID — store as `CHAR(36)`."
**Recommendation:** a per-dialect type-mapping registry with declared fallbacks, so a logical type degrades
gracefully instead of being all-or-nothing. This is the most direct answer to the original
"all dialects use the same base types" observation.

### A5. Closed dispatch switches conflate two error cases
Every renderer's `default -> throw new SqlDialectUnsupportedRenderingException("Unknown … type …")` mixes
(a) a genuinely unknown/extension type (programming error) with (b) a known type this dialect can't render
(capability gap) — same exception class. Adding a new function type also means editing every base `switch`.
A `Map<Class<? extends SqlFunction>, renderer-fn>` registry would let a dialect override/add a single
function without subclassing, and separate "unknown" from "unsupported here."
**Trade-off:** the current `switch` is faster and exhaustiveness-checkable — a flexibility-vs-speed call,
not a clear win.

### A6. Duplication flagged by the code itself
Several spots carry `@SuppressWarnings("DuplicatedCode")`:
- `renderReturning` is copy-pasted in `PostgresSql`, `Sqlite`, and `MariaDb` (identical bodies).
- `renderDropIndex` is duplicated in `MySql` and `SqlServer`.

Shared `protected renderStandardReturning(...)` / `renderStandardDropIndex(...)` helpers in the base would
remove three+ copies. `MariaDbDialect extends MySqlDialect` yet restates its whole feature `Set` and
re-implements RETURNING.

### A7. Empty-string sentinel for check constraints
`SqliteDialect.getCheckConstraintsQueryString()` returns `""`. `AbstractSqlDialect.getCheckConstraints`
passes the `requireNonNull` check (an empty string is non-null) and then calls `prepareStatement("")` —
which **will throw at runtime** for SQLite introspection rather than returning "no constraints."
Guard it with a capability flag / `Optional`, or return early.

---

## 3. Part B — Dialect-specific SQL gaps & latent correctness bugs

These are the cases where the common default emits generic but suboptimal — or outright wrong — SQL while a
dialect has a better/native construct.

| # | Location (base default) | Generic output | Problem on dialect | Suggested fix |
|---|---|---|---|---|
| B1 | `SqlNumericFunctionRenderer.renderTruncate` | `TRUNCATE(x, 0)` | **PostgreSQL has no `TRUNCATE()` function** (it's `TRUNC`); SQLite has neither. Only MySQL has `TRUNCATE`. SQL Server already overrides to `ROUND(x,0,1)`. | Postgres/H2/SQLite override → `TRUNC(...)` or equivalent |
| B2 | `SqlNumericFunctionRenderer.renderLog` | `LOG(base, x)` | **SQL Server `LOG` takes the base as the *second* arg** (`LOG(x, base)`) → reversed args, wrong results | SQL Server override with swapped args |
| B3 | `SqlNumericFunctionRenderer.renderMod` | `MOD(x, y)` | **SQL Server has no `MOD` function** — uses the `%` operator | SQL Server override → infix `%` |
| B4 | `SqlComparisonConditionRenderer.renderIsDistinctFrom` | `a IS DISTINCT FROM b` | **Unsupported on SQL Server < 2022** and on **MySQL** (needs `NOT (a <=> b)`) | dialect overrides; add `IS_DISTINCT_FROM` feature |
| B5 | `SqlStringConditionRenderer.renderEqualsIgnoreCase` | `UPPER(a)=UPPER(b)` | Correct but non-sargable (defeats indexes). Postgres `ILIKE`/`citext` is better | Postgres override → `ILIKE` |
| B6 | `renderContains/StartsWith/EndsWith` | `LIKE '%'||x||'%'` | LIKE case-sensitivity **differs by dialect/collation** (Postgres case-sensitive, MySQL usually not) — portability footgun | document; optionally native `position()`/`LOCATE` |
| B7 | `AbstractSqlDialect.renderUpsertClause` (SQL Server throws) | — | SQL Server can't upsert via INSERT today | implement via `MERGE` (see A3) |
| B8 | Type system | ANSI names only | No native `UUID`, `JSONB`, `INET`, `ENUM`; Postgres arrays are special-cased but the mechanism isn't general | per-dialect native type mapping (see A4) |
| B9 | `SqlServerDialect.renderLimitOffset` | `OFFSET … FETCH …` | SQL Server `OFFSET/FETCH` **requires `ORDER BY`** — silent failure if absent | validate/inject ordering, or document |

**Good examples already done right** (use as the template): `renderRandom`
(`RANDOM()` → `RAND()` on MySQL/SQL Server), `renderBooleanLiteral` (`TRUE/FALSE` → `1/0` on SQL Server),
Postgres array types and `BYTEA`, and the per-dialect temporal renderers.

---

## 4. Part C — Cross-cutting findings (wider sweep)

### Query building & execution

**C1. Bulk insert uses no JDBC batching and risks the parameter limit.**
`SqlInsertQuery.toSql` renders `insert(Collection)` as a **single multi-row `VALUES` statement** with every
value inlined as a bound parameter. There is no use of `PreparedStatement.addBatch()/executeBatch()` anywhere
in the query layer (only `SqlMigrationSchemaStore` batches). Consequences:
- Large batches can exceed driver parameter limits (**SQL Server ≈ 2100**, **PostgreSQL 65535**) → hard failure.
- Worse throughput than a true batch.
**Recommendation:** add a batched execution path (chunked `addBatch`/`executeBatch`), or chunk the multi-row
VALUES to stay under a per-dialect parameter cap.

**C2. No generated-keys retrieval for non-RETURNING dialects.**
`SqlQueryExecutor.prepare` calls `connection.prepareStatement(sql)` — never
`prepareStatement(sql, RETURN_GENERATED_KEYS)` — and nothing calls `getGeneratedKeys()`. The only way to read
back inserted rows is `returning()`, which requires `SqlFeature.RETURNING` (absent on **MySQL** and
**SQL Server**). So auto-increment IDs are unobtainable on those dialects. Note the misleading message in
`prepare`: *"Failed to prepare statement with generated keys"* — generated keys are never requested.
**Recommendation:** add a generated-keys path for dialects lacking RETURNING.

**C3. `count()` / `exists()` wrap the full query including `ORDER BY`.**
Both build `SELECT … FROM (<inner query>)`. The inner query still carries its `ORDER BY` (and, for `count()`,
no stripping of `limit`). **`ORDER BY` in a derived table is rejected by SQL Server** (without `TOP`/`OFFSET`)
and is meaningless/ignored elsewhere. Strip `ORDER BY` (and `limit` for `count`) before wrapping.

**C4. `forUpdate()` / `forShare()` skip eager feature checks.**
`skipLocked()` and `noWait()` check `isFeatureSupported(...)` and fail fast, but `forUpdate()`/`forShare()`
do not — so `forShare()` on H2 or `forUpdate()` on SQLite only fails later at render time. Inconsistent;
add the same eager probe.

**C5. `SqlSelectQuery` immutable `copy(...)` takes 14 positional args.**
Every builder method re-passes all fields positionally (multiple `boolean`/`long` in a row). This is highly
transposition-prone and hard to extend. Consider a private `toBuilder()`/wither so each method changes one field.

### Row mapping

**C6. Interface/`SqlRow` column→method mapping can be non-deterministic.**
`SqlRowMapper.createProxyMapper` orders accessor methods by `ORDINAL_NAMES` (`first`..`sixteenth`) and, for
any method not in that set, falls back to `getMethods()` order — which **the JVM does not guarantee**. For row
interfaces whose accessors aren't the ordinal names, the column-to-accessor assignment is effectively
undefined → silent wrong-column reads. **Correctness risk.** Prefer explicit ordering (declaration order via
`getDeclaredMethods` is also unspecified; an explicit index/annotation or name match is safer).

**C7. Per-row `Proxy.newProxyInstance` allocation.**
Interface and `SqlRow` projections allocate a JDK proxy + `InvocationHandler` **per row**. For large result
sets this is meaningful overhead; record projection (`forRecordProjection`) is the faster path. Consider
caching/avoiding the proxy, or documenting records as the preferred projection target.

**C8. `getObject(idx, Class)` relies on driver type conversion.**
`SqlType.get` uses `resultSet.getObject(columnIndex, javaType())`. Driver support for converting to
`java.time` types and others varies; weak drivers may fail or mis-map. Portability note — consider
type-specific getters for the temporal types.

### Transactions & migrations

**C9. "Transactional migration" guarantee only holds on some dialects.**
`executeAndSave`/`executeAndUpdate` run DDL + bookkeeping in one transaction with rollback on failure. But
**MySQL/MariaDB/SQL Server implicitly commit on DDL** (CREATE/ALTER), so a mid-migration failure can't be
rolled back there — the atomicity contract is real only on **PostgreSQL/SQLite/H2**. Document this clearly,
or detect non-transactional-DDL dialects and warn.

**C10. Schema snapshot is saved *after* the migration commits.**
In `applyMigration`, the migration commits first, then `schemaStore.save(...)` runs as a separate operation.
If the snapshot save fails, the migration is marked applied but has no snapshot → the next migration's
`renderMigration` throws *"Schema snapshot not found"*. Partial-state window; consider folding the snapshot
write into the same transaction (where DDL is transactional) or making recovery idempotent.

**C11. `REQUIRES_NEW` / `NESTED` acquire an extra pooled connection while holding the suspended one.**
With small pools this can deadlock. Inherent to the propagation model — worth a documentation note and/or a
pool-sizing guard.

### Rendering

**C12. Token join produces verbose SQL and re-computes each call.**
`SqlRendered.sql()` does `String.join(" ", statements)`, so punctuation tokens become spaced:
`COUNT ( * )`, `CAST ( x AS INT )`, `( a , b )`. Valid, but noisy in logs and slightly larger. Also `sql()`
recomputes the join on every call. Minor — consider tighter joining for brackets/commas and/or memoizing.

**C13. No memoization of rendered output.**
Each execution re-renders the entire query tree through the dialect. Fine for one-shot queries; wasteful when
the same immutable query object is executed repeatedly. An optional cached `SqlRendered` (keyed by dialect)
on immutable queries would help hot paths.

**C14. Large `IN (list)` conditions.**
`renderInList` inlines all options as parameters; big lists hit the same parameter limits as C1. Consider
chunking, or array/temp-table strategies on dialects that support them.

---

## 5. Suggested priority order

1. **Verify & fix correctness gaps first:** B1–B4 (numeric `TRUNCATE`/`LOG`/`MOD`, `IS DISTINCT FROM`),
   **C6** (row-mapping order), **A7** (SQLite check-constraint introspection), **C3** (ORDER BY in
   `count`/`exists` subqueries). These look like real bugs, not just missed optimizations.
2. **Bulk-insert batching + parameter-limit safety (C1)** and **generated-keys support (C2)** — high-impact
   for real workloads.
3. **Unify the capability model (A1/A2)** — add the missing `SqlFeature` values and exception-free probes;
   wire eager checks into the builders (C4). Highest-leverage structural change.
4. **Emulation tier (A3)** — start with SQL Server `MERGE` upsert (B7) and Postgres `ILIKE` (B5).
5. **Native-type mapping with fallbacks (A4/B8)** — biggest "dialect-specific types" win; largest design effort.
6. **Migration atomicity clarity (C9/C10)** — document the DDL-commit reality; tighten snapshot timing.
7. **Cleanup & ergonomics (A6, C5, C12/C13)** — dedupe renderers, safer query copy, tidier SQL output.
8. **Reconsider dispatch (A5)** only if third-party extension of function/condition types is a real goal —
   otherwise the current `switch` is fine.

---

## 6. Verification notes

Before implementing the Part B / C correctness items, confirm against the actual target databases — ideally
with a small test per item (the project already uses H2/SQLite for tests). Specifically worth a live check:
B1 (`TRUNC` vs `TRUNCATE` on Postgres), B2/B3 (`LOG`/`%` on SQL Server), B4 (`IS DISTINCT FROM` on the SQL
Server version in use), C3 (ORDER-BY-in-subquery rejection), and C6 (interface row mapping with non-ordinal
accessor names).
