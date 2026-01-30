# Entity Mapping

## Overview

Entities are mapped from database result sets using generated mapper classes. The mapping is explicit and type-safe - no reflection or magic at runtime. In v3, three entity variants are generated for entities with relationships: base, FullJoined, and PartialJoined.

## Generated Entity Structure

From the YAML schema, entity records and their mappers are generated:

```java
// Base entity - raw FK values only
public record User(
    UUID id,
    String name,
    String email,
    int age,
    UserStatus status,
    Instant createdAt,
    @Nullable Instant lastLogin,
    @Nullable UUID addressId        // Raw FK value
) {
    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public UserBuilder toBuilder() {
        return new UserBuilder(this);
    }
}

// FullJoined - loaded relationships
public record UserFullJoined(
    UUID id,
    String name,
    String email,
    int age,
    UserStatus status,
    Instant createdAt,
    @Nullable Instant lastLogin,
    @Nullable Address address,      // Loaded entity
    List<Order> orders              // Loaded collection
) {}

// PartialJoined - SqlForeignKey wrappers
public record UserPartialJoined(
    UUID id,
    String name,
    String email,
    int age,
    UserStatus status,
    Instant createdAt,
    @Nullable Instant lastLogin,
    SqlForeignKey<UUID, Address> addressKey,
    SqlForeignKey<UUID, List<Order>> ordersKey
) {}
```

## Generated Mapper for Base Entity

```java
public final class UserMapper implements SqlEntityMapper<User> {

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
        ps.setObject(startIndex + 7, entity.addressId());
    }

    @Override
    public List<String> getColumnNames() {
        return List.of("id", "name", "email", "age", "status",
                       "created_at", "last_login", "address_id");
    }
}
```

## Generated Mapper for FullJoined Entity

```java
public final class UserFullJoinedMapper implements SqlEntityMapper<UserFullJoined> {

    public static final UserFullJoinedMapper INSTANCE = new UserFullJoinedMapper();

    @Override
    public UserFullJoined mapRow(ResultSet rs) throws SQLException {
        return mapRow(rs, Map.of());
    }

    public UserFullJoined mapRow(ResultSet rs, Map<String, SqlEntityMapper<?>> relationMappers)
            throws SQLException {
        // Map base fields
        UUID id = rs.getObject("id", UUID.class);
        String name = rs.getString("name");
        String email = rs.getString("email");
        int age = rs.getInt("age");
        UserStatus status = UserStatus.valueOf(rs.getString("status"));
        Instant createdAt = rs.getObject("created_at", Instant.class);
        Instant lastLogin = rs.getObject("last_login", Instant.class);

        // Map relationships if loaders provided
        Address address = null;
        if (relationMappers.containsKey("address")) {
            @SuppressWarnings("unchecked")
            SqlEntityMapper<Address> addressMapper =
                (SqlEntityMapper<Address>) relationMappers.get("address");
            address = addressMapper.mapRow(rs);
        }

        List<Order> orders = List.of();  // Loaded via separate query for one-to-many

        return new UserFullJoined(id, name, email, age, status, createdAt, lastLogin,
            address, orders);
    }

    @Override
    public List<String> getColumnNames() {
        return List.of("id", "name", "email", "age", "status",
                       "created_at", "last_login");
    }
}
```

## Generated Mapper for PartialJoined Entity

```java
public final class UserPartialJoinedMapper implements SqlEntityMapper<UserPartialJoined> {

    public static final UserPartialJoinedMapper INSTANCE = new UserPartialJoinedMapper();

    @Override
    public UserPartialJoined mapRow(ResultSet rs) throws SQLException {
        return mapRow(rs, Map.of());
    }

    public UserPartialJoined mapRow(ResultSet rs, Map<String, SqlEntityMapper<?>> relationMappers)
            throws SQLException {
        // Map base fields
        UUID id = rs.getObject("id", UUID.class);
        String name = rs.getString("name");
        String email = rs.getString("email");
        int age = rs.getInt("age");
        UserStatus status = UserStatus.valueOf(rs.getString("status"));
        Instant createdAt = rs.getObject("created_at", Instant.class);
        Instant lastLogin = rs.getObject("last_login", Instant.class);

        // Map FK and optionally loaded value
        UUID addressId = rs.getObject("address_id", UUID.class);
        Address addressValue = null;
        if (relationMappers.containsKey("address")) {
            @SuppressWarnings("unchecked")
            SqlEntityMapper<Address> addressMapper =
                (SqlEntityMapper<Address>) relationMappers.get("address");
            addressValue = addressMapper.mapRow(rs);
        }
        SqlForeignKey<UUID, Address> addressKey = SqlForeignKey.of(addressId, addressValue);

        // One-to-many: key is user's own ID, value loaded via separate query
        SqlForeignKey<UUID, List<Order>> ordersKey = SqlForeignKey.of(id, null);

        return new UserPartialJoined(id, name, email, age, status, createdAt, lastLogin,
            addressKey, ordersKey);
    }

    @Override
    public List<String> getColumnNames() {
        return List.of("id", "name", "email", "age", "status",
                       "created_at", "last_login", "address_id");
    }
}
```

## SqlEntityMapper Interface

```java
public interface SqlEntityMapper<E> {

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
            new SqlResultSetSpliterator<>(rs, this::mapRow),
            false
        );
    }
}
```

## Null Handling

```java
public final class UserMapper implements SqlEntityMapper<User> {

    @Override
    public User mapRow(ResultSet rs) throws SQLException {
        return User.builder()
            .id(rs.getObject("id", UUID.class))
            .name(rs.getString("name"))
            // Handle nullable columns - getObject returns null for SQL NULL
            .lastLogin(rs.getObject("last_login", Instant.class))
            .addressId(rs.getObject("address_id", UUID.class))  // Nullable FK
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
    Optional<String> middleName,       // Or use Optional
    @Nullable UUID addressId           // Nullable FK
) {}
```

## Column Name Mapping

Column names are mapped from Java field names using a configurable strategy:

```java
public enum SqlNamingStrategy {

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
public final class UserSummaryMapper implements SqlEntityMapper<UserSummary> {

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
public interface SqlTypeConverter<J, D> {

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
public class MoneyConverter implements SqlTypeConverter<Money, BigDecimal> {

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
SqlTypeConverters.register(new MoneyConverter());
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
public class StatusCodeConverter implements SqlTypeConverter<UserStatus, String> {
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
public class SqlJsonConverter<T> implements SqlTypeConverter<T, String> {
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
@SqlEmbeddable
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

## SqlForeignKey Mapping

For PartialJoined entities with `SqlForeignKey<K, V>`:

```java
// PartialJoined entity with SqlForeignKey
public record OrderPartialJoined(
    UUID id,
    BigDecimal total,
    SqlForeignKey<UUID, User> customerKey
) {}

// Mapper creates SqlForeignKey with key always, value if joined
@Override
public OrderPartialJoined mapRow(ResultSet rs, Map<String, SqlEntityMapper<?>> relationMappers)
        throws SQLException {

    UUID customerId = rs.getObject("customer_id", UUID.class);

    // Value is populated only if relationship was joined
    User customer = null;
    if (relationMappers.containsKey("customer")) {
        @SuppressWarnings("unchecked")
        SqlEntityMapper<User> customerMapper =
            (SqlEntityMapper<User>) relationMappers.get("customer");
        customer = customerMapper.mapRow(rs);
    }

    return new OrderPartialJoined(
        rs.getObject("id", UUID.class),
        rs.getBigDecimal("total"),
        SqlForeignKey.of(customerId, customer)
    );
}
```

## Related Documents

- [../core/04-code-generation.md](../core/04-code-generation.md) - How mappers are generated
- [relationship-loading.md](relationship-loading.md) - SqlForeignKey pattern details
- [../core/02-yaml-schema.md](../core/02-yaml-schema.md) - Type definitions
