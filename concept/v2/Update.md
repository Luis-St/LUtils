# Changes from v2 to v3:

## Class Naming
1. All class files should be prefixed with Sql: SqlDatabase, SqlTable, SqlQueryBuilder, ...
2. SqlRelationship should be renamed to SqlForeignColumn


## Relationship handling
By default, the generated entity classes should not include types from relationships:

```java
record User(int id, String name, UUID addressId) {}

record Address(UUID id, String street, String city) {}
```

then there should be classes generated for relationships:

```java
record UserFullJoined(int id, String name, Address address) {}

record UserPartialJoined(int id, String name, SqlForeignKey<UUID, Address> addressKey) {}
```

then the user can choose to use those classes when querying with joins.

## SqlForeignKey
The SqlForeignKey class should be introduced to represent foreign key relationships without loading the entire related entity.
It should encapsulate the foreign key value and provide methods to fetch the related entity if needed.

## Many to Many Relationships
Many to many relationships should be dropped, the user should handle them manually using join tables.

## DDL
it should not be possible to modify a table after creation, tables and columns should be created and generated only from the yaml schema.

## Dialects
The dialects should be included when creating the query builders, for example:
```java
// Select returns a 
UserTable.TABLE.dialect(SqlDialect.POSTGRES).select()
    .where(UserTable.NAME.dialect(SqlDialect.POSTGRES).ilike("John"))
    .build();
```

the dialect should be optional, if not provided the default dialect should be used.
The default dialect contains all common methods and functions for all databases.

When calling `.dialect()` on a table or column, the returned builder is now specific to that dialect, enabling dialect-specific methods.
Like for example `.ilike()` for Postgres or for timescaleDB `.timeBucket()`.
