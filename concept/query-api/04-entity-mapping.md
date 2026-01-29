# Entity Mapping

## Overview

Entities are mapped from database result sets using generated mapper classes. The mapping is explicit and type-safe - no reflection or magic at runtime.

## Generated Entity Structure

From the YAML schema, an entity class and its mapper are generated:

```java
// Generated entity (immutable record or class)
public record User(
    UUID id,
    String name,
    String email,
    int age,
    UserStatus status,
    Instant createdAt,
    Instant lastLogin
) {
    // Builder for construction
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID id;
        private String name;
        // ... other fields

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        // ... other setters

        public User build() {
            return new User(id, name, email, age, status, createdAt, lastLogin);
        }
    }
}
```

## Generated Mapper

```java
// Generated mapper class
public final class UserMapper implements EntityMapper<User> {

    private static final UserMapper INSTANCE = new UserMapper();

    public static UserMapper instance() { return INSTANCE; }

    @Override
    public User mapRow(ResultSet rs) throws SQLException {
        return User.builder()
            .id(rs.getObject("id", UUID.class))
            .name(rs.getString("name"))
            .email(rs.getString("email"))
            .age(rs.getInt("age"))
            .status(UserStatus.valueOf(rs.getString("status")))
            .createdAt(rs.getObject("created_at", Instant.class))
            .lastLogin(rs.getObject("last_login", Instant.class))
            .build();
    }

    @Override
    public void bindParameters(PreparedStatement ps, User entity, int startIndex)
            throws SQLException {
        ps.setObject(startIndex, entity.id());
        ps.setString(startIndex + 1, entity.name());
        ps.setString(startIndex + 2, entity.email());
        ps.setInt(startIndex + 3, entity.age());
        ps.setString(startIndex + 4, entity.status().name());
        ps.setObject(startIndex + 5, entity.createdAt());
        ps.setObject(startIndex + 6, entity.lastLogin());
    }

    @Override
    public List<String> getColumnNames() {
        return List.of("id", "name", "email", "age", "status", "created_at", "last_login");
    }
}
```

## EntityMapper Interface

```java
public interface EntityMapper<E> {

    /**
     * Maps a single row from the ResultSet to an entity.
     * The ResultSet cursor should be positioned on the row to map.
     */
    E mapRow(ResultSet rs) throws SQLException;

    /**
     * Binds entity values to a PreparedStatement for insert/update.
     */
    void bindParameters(PreparedStatement ps, E entity, int startIndex) throws SQLException;

    /**
     * Returns the list of column names in order.
     */
    List<String> getColumnNames();

    /**
     * Maps all rows from the ResultSet to a list of entities.
     */
    default List<E> mapAll(ResultSet rs) throws SQLException {
        List<E> results = new ArrayList<>();
        while (rs.next()) {
            results.add(mapRow(rs));
        }
        return results;
    }

    /**
     * Maps rows lazily as a Stream.
     */
    default Stream<E> stream(ResultSet rs) {
        return StreamSupport.stream(
            new ResultSetSpliterator<>(rs, this::mapRow),
            false
        );
    }
}
```

## Null Handling

```java
public final class UserMapper implements EntityMapper<User> {

    @Override
    public User mapRow(ResultSet rs) throws SQLException {
        return User.builder()
            .id(rs.getObject("id", UUID.class))
            .name(rs.getString("name"))
            // Handle nullable columns
            .lastLogin(rs.wasNull() ? null : rs.getObject("last_login", Instant.class))
            // Or use Optional for nullable fields
            .middleName(Optional.ofNullable(rs.getString("middle_name")))
            .build();
    }
}

// Entity with nullable fields
public record User(
    UUID id,
    String name,
    @Nullable Instant lastLogin,  // Annotated as nullable
    Optional<String> middleName   // Or use Optional
) {}
```

## Column Name Mapping

Column names are mapped from Java field names using a configurable strategy:

```java
public enum NamingStrategy {

    /** user_name -> userName */
    SNAKE_TO_CAMEL,

    /** userName -> user_name */
    CAMEL_TO_SNAKE,

    /** As-is, no transformation */
    IDENTITY
}

// Configure globally
DatabaseConfig.builder()
    .namingStrategy(NamingStrategy.SNAKE_TO_CAMEL)
    .build();
```

## Custom Column Aliases

When selecting specific columns or using aliases:

```java
// Select with aliases
List<Tuple2<String, Integer>> results = UserTable.TABLE
    .select(UserTable.NAME.as("user_name"), UserTable.AGE.as("user_age"))
    .fetch();

// The mapper handles aliased columns
public interface SelectExpression<T> {
    SelectExpression<T> as(String alias);
    String getAlias();
}
```

## Projection Mapping

For projections (selecting a subset of columns):

```java
// Define projection interface
public interface UserSummary {
    UUID id();
    String email();
}

// Fetch as projection
List<UserSummary> summaries = UserTable.TABLE
    .select(UserTable.ID, UserTable.EMAIL)
    .fetchAs(UserSummary.class);

// Generated projection mapper (or runtime proxy)
public final class UserSummaryMapper implements EntityMapper<UserSummary> {

    @Override
    public UserSummary mapRow(ResultSet rs) throws SQLException {
        UUID id = rs.getObject("id", UUID.class);
        String email = rs.getString("email");

        // Return anonymous implementation or generated class
        return new UserSummary() {
            @Override public UUID id() { return id; }
            @Override public String email() { return email; }
        };
    }
}
```

## Tuple Mapping

For ad-hoc multi-column selects:

```java
// Built-in tuple types
List<Tuple2<UUID, String>> results = UserTable.TABLE
    .select(UserTable.ID, UserTable.EMAIL)
    .fetch();

// Tuple2 is a simple record
public record Tuple2<T1, T2>(T1 _1, T2 _2) {
    public T1 first() { return _1; }
    public T2 second() { return _2; }
}

// Up to Tuple10 is provided
public record Tuple3<T1, T2, T3>(T1 _1, T2 _2, T3 _3) {}
// ...
```

## Type Converters

Custom types need type converters:

```java
public interface TypeConverter<J, D> {

    /**
     * Convert from Java type to database type.
     */
    D toDatabase(J value);

    /**
     * Convert from database type to Java type.
     */
    J fromDatabase(D value);

    /**
     * The Java type this converter handles.
     */
    Class<J> getJavaType();

    /**
     * The database type used for storage.
     */
    Class<D> getDatabaseType();
}

// Example: Money type
public class MoneyConverter implements TypeConverter<Money, BigDecimal> {

    @Override
    public BigDecimal toDatabase(Money value) {
        return value == null ? null : value.getAmount();
    }

    @Override
    public Money fromDatabase(BigDecimal value) {
        return value == null ? null : Money.of(value);
    }

    @Override
    public Class<Money> getJavaType() { return Money.class; }

    @Override
    public Class<BigDecimal> getDatabaseType() { return BigDecimal.class; }
}

// Register converter
TypeConverters.register(new MoneyConverter());
```

## Enum Mapping

```java
// By default, enums are stored as strings
public enum UserStatus { ACTIVE, PENDING, BANNED, DELETED }

// Column stores: "ACTIVE", "PENDING", etc.

// Or configure ordinal storage
@EnumType(EnumStorageType.ORDINAL)
public enum Priority { LOW, MEDIUM, HIGH }

// Or use a custom converter for codes
public class StatusCodeConverter implements TypeConverter<UserStatus, String> {
    @Override
    public String toDatabase(UserStatus value) {
        return switch (value) {
            case ACTIVE -> "A";
            case PENDING -> "P";
            case BANNED -> "B";
            case DELETED -> "D";
        };
    }

    @Override
    public UserStatus fromDatabase(String value) {
        return switch (value) {
            case "A" -> UserStatus.ACTIVE;
            case "P" -> UserStatus.PENDING;
            case "B" -> UserStatus.BANNED;
            case "D" -> UserStatus.DELETED;
            default -> throw new IllegalArgumentException("Unknown status: " + value);
        };
    }
}
```

## Join Result Mapping

When joining tables, results can be mapped to tuples or combined entities:

```java
// Map to tuple of entities
List<Tuple2<User, Address>> results = UserTable.TABLE
    .innerJoin(AddressTable.TABLE).on(UserTable.ADDRESS)
    .select()
    .fetch();

// Each tuple contains independently mapped entities
Tuple2<User, Address> result = results.get(0);
User user = result.first();
Address address = result.second();

// Or map with relationship populated
List<User> users = UserTable.TABLE
    .leftJoin(AddressTable.TABLE).on(UserTable.ADDRESS)
    .selectEntity(UserTable.TABLE)
    .withLoaded(UserTable.ADDRESS)  // Populates user.address()
    .fetch();
```

## Open Questions

- **Immutable vs mutable entities**: Should entities always be immutable records, or support mutable classes too?
- **Lombok integration**: Should there be Lombok annotations support for entity generation?
- **Null handling strategy**: Should nullable fields use `@Nullable`, `Optional<T>`, or both options?
- **Embedded types**: How should embedded/composite types be handled (e.g., Address embedded in User)?
