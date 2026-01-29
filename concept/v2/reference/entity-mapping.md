# Entity Mapping

## Overview

Entities are mapped from database result sets using generated mapper classes. The mapping is explicit and type-safe - no reflection or magic at runtime.

## Generated Entity Structure

From the YAML schema, an entity record and its mapper are generated:

```java
// Generated entity (immutable record)
public record User(
    UUID id,
    String name,
    String email,
    int age,
    UserStatus status,
    Instant createdAt,
    @Nullable Instant lastLogin,
    SqlKey<UUID, Address> address
) {
    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public UserBuilder toBuilder() {
        return new UserBuilder(this);
    }
}
```

## Generated Mapper

```java
public final class UserMapper implements EntityMapper<User> {

    public static final UserMapper INSTANCE = new UserMapper();

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
            .addressId(rs.getObject("address_id", UUID.class))
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
        ps.setObject(startIndex + 7,
            entity.address() != null ? entity.address().key() : null);
    }

    @Override
    public List<String> getColumnNames() {
        return List.of("id", "name", "email", "age", "status",
                       "created_at", "last_login", "address_id");
    }
}
```

## EntityMapper Interface

```java
public interface EntityMapper<E> {

    /**
     * Maps a single row from the ResultSet to an entity.<br>
     * The ResultSet cursor should be positioned on the row to map.
     */
    E mapRow(ResultSet rs) throws SQLException;

    /**
     * Binds entity values to a PreparedStatement for insert/update.<br>
     */
    void bindParameters(PreparedStatement ps, E entity, int startIndex) throws SQLException;

    /**
     * Returns the list of column names in order.<br>
     */
    List<String> getColumnNames();

    /**
     * Maps all rows from the ResultSet to a list of entities.<br>
     */
    default List<E> mapAll(ResultSet rs) throws SQLException {
        List<E> results = new ArrayList<>();
        while (rs.next()) {
            results.add(mapRow(rs));
        }
        return results;
    }

    /**
     * Maps rows lazily as a Stream.<br>
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
            // Handle nullable columns - getObject returns null for SQL NULL
            .lastLogin(rs.getObject("last_login", Instant.class))
            // For primitives, check wasNull()
            .age(rs.getInt("age"))  // Returns 0 for NULL
            .build();
    }
}

// Entity with nullable fields
public record User(
    UUID id,
    String name,
    @Nullable Instant lastLogin,      // Annotated as nullable
    Optional<String> middleName       // Or use Optional
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

// Configure globally in _config.yaml
// naming:
//   column: snake_case
//   field: camelCase
```

## Custom Column Aliases

When selecting specific columns or using aliases:

```java
// Select with aliases
List<Row2<String, Integer>> results = UserTable.TABLE
    .select(UserTable.NAME.as("user_name"), UserTable.AGE.as("user_age"))
    .fetch();

// The mapper handles aliased columns automatically
```

## Row Type Mapping

For projections (selecting specific columns), use Row types:

```java
// Row2 for 2 columns
List<Row2<UUID, String>> results = UserTable.TABLE
    .select(UserTable.ID, UserTable.EMAIL)
    .fetch();

// Access values
for (Row2<UUID, String> row : results) {
    UUID id = row.first();
    String email = row.second();
}

// Row types are records
public record Row2<T1, T2>(T1 first, T2 second) {}
public record Row3<T1, T2, T3>(T1 first, T2 second, T3 third) {}
// ... up to Row8 by default
```

## Projection Interface Mapping

For named access, define a projection interface:

```java
// Define projection interface
public interface UserSummary {
    UUID id();
    String email();
    int age();
}

// Fetch as projection
List<UserSummary> summaries = UserTable.TABLE
    .select(UserTable.ID, UserTable.EMAIL, UserTable.AGE)
    .fetchAs(UserSummary.class);

// Generated or dynamic proxy mapper
public final class UserSummaryMapper implements EntityMapper<UserSummary> {

    @Override
    public UserSummary mapRow(ResultSet rs) throws SQLException {
        UUID id = rs.getObject("id", UUID.class);
        String email = rs.getString("email");
        int age = rs.getInt("age");

        // Return anonymous implementation
        return new UserSummary() {
            @Override public UUID id() { return id; }
            @Override public String email() { return email; }
            @Override public int age() { return age; }
        };
    }
}
```

## Type Converters

Custom types need type converters:

```java
public interface TypeConverter<J, D> {

    /**
     * Convert from Java type to database type.<br>
     */
    D toDatabase(J value);

    /**
     * Convert from database type to Java type.<br>
     */
    J fromDatabase(D value);

    /**
     * The Java type this converter handles.<br>
     */
    Class<J> getJavaType();

    /**
     * The database type used for storage.<br>
     */
    Class<D> getDatabaseType();
}

// Example: Money type stored as BigDecimal
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

// Configure in YAML for ordinal storage
// status:
//   type: UserStatus
//   enum:
//     storage: ordinal

// Custom enum converter with codes
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

## JSON Mapping

For JSONB/JSON columns:

```java
// In YAML
// metadata:
//   type: Json
//   columnType: JSONB

// Generated field uses JsonElement or custom type
public record Product(
    UUID id,
    String name,
    JsonElement metadata      // Generic JSON
) {}

// Or with typed JSON
public record Product(
    UUID id,
    String name,
    ProductMetadata metadata  // Custom class
) {}

// JSON converter
public class JsonConverter<T> implements TypeConverter<T, String> {
    private final Class<T> type;
    private final ObjectMapper mapper;

    @Override
    public String toDatabase(T value) {
        return mapper.writeValueAsString(value);
    }

    @Override
    public T fromDatabase(String value) {
        return mapper.readValue(value, type);
    }
}
```

## Embedded Types

Embedded types map to multiple columns:

```java
// Embeddable definition
@Embeddable
public record Address(
    String street,
    String city,
    String postalCode,
    String country
) {}

// Entity with embedded
public record Customer(
    UUID id,
    String name,
    Address billingAddress,   // Maps to billing_street, billing_city, etc.
    Address shippingAddress   // Maps to shipping_street, shipping_city, etc.
) {}

// Mapper handles expansion
@Override
public Customer mapRow(ResultSet rs) throws SQLException {
    return Customer.builder()
        .id(rs.getObject("id", UUID.class))
        .name(rs.getString("name"))
        .billingAddress(new Address(
            rs.getString("billing_street"),
            rs.getString("billing_city"),
            rs.getString("billing_postal_code"),
            rs.getString("billing_country")
        ))
        .shippingAddress(new Address(
            rs.getString("shipping_street"),
            rs.getString("shipping_city"),
            rs.getString("shipping_postal_code"),
            rs.getString("shipping_country")
        ))
        .build();
}
```

## SqlKey Mapping

For relationships using `SqlKey<K, V>`:

```java
// Entity with relationship
public record Order(
    UUID id,
    BigDecimal total,
    SqlKey<UUID, User> customer
) {}

// Mapper creates SqlKey with just the key
@Override
public Order mapRow(ResultSet rs) throws SQLException {
    return Order.builder()
        .id(rs.getObject("id", UUID.class))
        .total(rs.getBigDecimal("total"))
        .customer(SqlKey.of(rs.getObject("customer_id", UUID.class)))
        .build();
}

// When loaded via join, value is populated
@Override
public Order mapRowWithRelationships(ResultSet rs, Map<String, EntityMapper<?>> mappers)
        throws SQLException {
    UUID customerId = rs.getObject("customer_id", UUID.class);
    User customer = mappers.get("customer") != null
        ? ((EntityMapper<User>) mappers.get("customer")).mapRow(rs)
        : null;

    return Order.builder()
        .id(rs.getObject("id", UUID.class))
        .total(rs.getBigDecimal("total"))
        .customer(SqlKey.of(customerId, customer))
        .build();
}
```

## Related Documents

- [../core/04-code-generation.md](../core/04-code-generation.md) - How mappers are generated
- [relationship-loading.md](relationship-loading.md) - SqlKey pattern details
- [../core/02-yaml-schema.md](../core/02-yaml-schema.md) - Type definitions
