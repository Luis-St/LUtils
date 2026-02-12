# MariaDB Dialect Extension Guide

## Current State

Summary of current interfaces including inherited MySQL methods.

## MariaColumn Extensions (beyond MySQL)

### Temporal Queries (on system-versioned columns)

#### asOf
```java
@NonNull SqlCondition asOf(@NonNull String timestamp);
```
Generates SQL: `FOR SYSTEM_TIME AS OF 'timestamp'`

Queries the system-versioned table as it existed at the specified point in time.

#### fromTo
```java
@NonNull SqlCondition fromTo(@NonNull String start, @NonNull String end);
```
Generates SQL: `FOR SYSTEM_TIME FROM 'start' TO 'end'`

Queries all versions of rows that were active during the specified period.

#### betweenSystemTime
```java
@NonNull SqlCondition betweenSystemTime(@NonNull String start, @NonNull String end);
```
Generates SQL: `FOR SYSTEM_TIME BETWEEN 'start' AND 'end'`

Queries all versions of rows that overlapped with the specified period (inclusive on both ends).

---

## Supporting Types

### MariaSystemTimeMode (new enum)
```java
public enum MariaSystemTimeMode {
    AS_OF,
    FROM_TO,
    BETWEEN,
    ALL
}
```

### Note on MariaDB-specific engines
MariaDB supports additional engines beyond MySQL:
- **Aria** - crash-safe MyISAM replacement
- **ColumnStore** - columnar analytical engine
- **S3** - read-only tables in Amazon S3
- **Spider** - distributed/sharded engine
- **CONNECT** - access external data sources
- **Mroonga** - full-text search engine

Consider extending `MysqlEngine` or creating a `MariaEngine` enum:
```java
public enum MariaEngine {
    INNODB,
    ARIA,
    MYISAM,
    MEMORY,
    CSV,
    ARCHIVE,
    COLUMNSTORE,
    S3,
    SPIDER,
    CONNECT,
    MROONGA,
    BLACKHOLE,
    FEDERATED,
    OQGRAPH
}
```
