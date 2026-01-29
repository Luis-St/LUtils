# Testing Support

## Overview

The query API provides testing utilities for unit tests, integration tests, and fixtures. Tests can run against real databases, in-memory databases, or mock data sources.

## Testing Approaches

| Approach | Speed | Fidelity | Use Case |
|----------|-------|----------|----------|
| Mock/Stub | Fastest | Lowest | Unit tests, isolated logic |
| In-Memory (H2) | Fast | Medium | Integration tests, CI |
| Testcontainers | Slower | Highest | Full integration, dialect-specific |
| Shared Test DB | Medium | High | Local development |

## In-Memory Database

For fast integration tests using H2:

```java
@BeforeEach
void setUp() {
    // Initialize in-memory database
    Database.initializeForTesting(
        DatabaseConfig.builder()
            .url("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
            .dialect(SqlDialect.H2)
            .build()
    );

    // Create tables
    UserTable.TABLE.createIfNotExists();
    OrderTable.TABLE.createIfNotExists();
}

@AfterEach
void tearDown() {
    // Clean up
    Database.shutdown();
}

@Test
void shouldFindActiveUsers() {
    // Given
    UserTable.TABLE.insert(User.builder().name("John").status(UserStatus.ACTIVE).build());
    UserTable.TABLE.insert(User.builder().name("Jane").status(UserStatus.INACTIVE).build());

    // When
    List<User> activeUsers = UserTable.TABLE.select()
        .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE))
        .fetch();

    // Then
    assertThat(activeUsers).hasSize(1);
    assertThat(activeUsers.get(0).name()).isEqualTo("John");
}
```

## Testcontainers Integration

For testing against real database engines:

```java
@Testcontainers
class UserRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("testdb");

    @BeforeAll
    static void setUpDatabase() {
        Database.initialize(
            DatabaseConfig.builder()
                .url(postgres.getJdbcUrl())
                .username(postgres.getUsername())
                .password(postgres.getPassword())
                .dialect(SqlDialect.POSTGRESQL)
                .build()
        );

        // Run migrations or create tables
        Database.migrate();
    }

    @AfterAll
    static void tearDownDatabase() {
        Database.shutdown();
    }

    @BeforeEach
    void cleanData() {
        // Clean tables between tests
        UserTable.TABLE.truncate();
    }

    @Test
    void shouldSupportPostgresSpecificFeatures() {
        // Test PostgreSQL-specific functionality
        List<User> users = UserTable.TABLE.select()
            .where(UserTable.EMAIL.ilike("%@EXAMPLE.com"))  // Case-insensitive LIKE
            .fetch();
    }
}
```

## Test Data Builder

Fluent builders for creating test data:

```java
public class TestData {

    public static UserBuilder user() {
        return new UserBuilder();
    }

    public static OrderBuilder order() {
        return new OrderBuilder();
    }

    public static class UserBuilder {
        private String name = "Test User";
        private String email = "test@example.com";
        private UserStatus status = UserStatus.ACTIVE;
        private int age = 25;

        public UserBuilder name(String name) { this.name = name; return this; }
        public UserBuilder email(String email) { this.email = email; return this; }
        public UserBuilder status(UserStatus status) { this.status = status; return this; }
        public UserBuilder age(int age) { this.age = age; return this; }

        public User build() {
            return User.builder()
                .id(UUID.randomUUID())
                .name(name)
                .email(email)
                .status(status)
                .age(age)
                .build();
        }

        public User insert() {
            return UserTable.TABLE.insert(build());
        }
    }
}

// Usage
@Test
void shouldFindAdultUsers() {
    TestData.user().name("Alice").age(30).insert();
    TestData.user().name("Bob").age(17).insert();

    List<User> adults = UserTable.TABLE.select()
        .where(UserTable.AGE.greaterThanOrEqual(18))
        .fetch();

    assertThat(adults).hasSize(1);
    assertThat(adults.get(0).name()).isEqualTo("Alice");
}
```

## Mock Database

For pure unit tests without a database:

```java
public class MockDatabase {

    private final Map<Class<?>, List<Object>> tables = new HashMap<>();

    public <E> void insert(Class<E> entityClass, E entity) {
        tables.computeIfAbsent(entityClass, k -> new ArrayList<>()).add(entity);
    }

    @SuppressWarnings("unchecked")
    public <E> List<E> findAll(Class<E> entityClass) {
        return (List<E>) tables.getOrDefault(entityClass, List.of());
    }

    public <E> List<E> findWhere(Class<E> entityClass, Predicate<E> predicate) {
        return findAll(entityClass).stream()
            .filter(predicate)
            .toList();
    }

    public void clear() {
        tables.clear();
    }
}

// Usage in unit test
@Test
void shouldProcessActiveUsersOnly() {
    MockDatabase db = new MockDatabase();
    db.insert(User.class, TestData.user().status(UserStatus.ACTIVE).build());
    db.insert(User.class, TestData.user().status(UserStatus.INACTIVE).build());

    UserService service = new UserService(db);
    List<User> processed = service.processActiveUsers();

    assertThat(processed).hasSize(1);
}
```

## Query Verification

Verify that queries are constructed correctly:

```java
@Test
void shouldBuildCorrectQuery() {
    // Build query without executing
    SelectQuery<User> query = UserTable.TABLE.select()
        .where(UserTable.STATUS.equalTo(UserStatus.ACTIVE)
            .and(UserTable.AGE.greaterThan(18)))
        .orderBy(UserTable.CREATED_AT.desc())
        .limit(10);

    // Verify SQL
    assertThat(query.toSql())
        .isEqualTo("SELECT * FROM users WHERE status = ? AND age > ? ORDER BY created_at DESC LIMIT ?");

    // Verify parameters
    assertThat(query.getParameters())
        .containsExactly(UserStatus.ACTIVE, 18, 10);
}

@Test
void shouldBuildCorrectUpdateQuery() {
    UpdateQuery<User> query = UserTable.TABLE.update()
        .set(UserTable.STATUS, UserStatus.BANNED)
        .where(UserTable.ID.equalTo(UUID.fromString("123e4567-e89b-12d3-a456-426614174000")));

    assertThat(query.toSql())
        .isEqualTo("UPDATE users SET status = ? WHERE id = ?");
}
```

## Fixtures

Load predefined test data from files:

```java
// fixtures/users.json
[
    {"id": "550e8400-e29b-41d4-a716-446655440001", "name": "Alice", "status": "ACTIVE"},
    {"id": "550e8400-e29b-41d4-a716-446655440002", "name": "Bob", "status": "ACTIVE"}
]
```

```java
public class Fixtures {

    public static void load(String... fixtureNames) {
        for (String name : fixtureNames) {
            loadFixture(name);
        }
    }

    private static void loadFixture(String name) {
        String json = readResource("fixtures/" + name + ".json");
        Class<?> entityClass = inferEntityClass(name);
        List<?> entities = parseJson(json, entityClass);

        for (Object entity : entities) {
            insertEntity(entity);
        }
    }
}

// Usage
@BeforeEach
void setUp() {
    Fixtures.load("users", "orders", "products");
}
```

## Transaction Rollback

Automatically rollback after each test:

```java
@ExtendWith(DatabaseExtension.class)
class UserServiceTest {

    @Test
    @Transactional  // Rolls back after test
    void shouldCreateUser() {
        User user = UserTable.TABLE.insert(TestData.user().build());
        assertThat(user.id()).isNotNull();
        // Transaction is rolled back - no cleanup needed
    }
}

// JUnit 5 Extension
public class DatabaseExtension implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        if (hasTransactionalAnnotation(context)) {
            Transaction tx = Database.beginTransaction();
            store(context, tx);
        }
    }

    @Override
    public void afterEach(ExtensionContext context) {
        Transaction tx = retrieve(context);
        if (tx != null && tx.isActive()) {
            tx.rollback();
        }
    }
}
```

## Assertion Helpers

Custom assertions for database testing:

```java
public class DatabaseAssertions {

    public static <E> TableAssert<E> assertThat(SqlTable<E> table) {
        return new TableAssert<>(table);
    }

    public static class TableAssert<E> {

        private final SqlTable<E> table;

        public TableAssert<E> hasRowCount(int expected) {
            long actual = table.select().count();
            if (actual != expected) {
                throw new AssertionError("Expected " + expected + " rows but found " + actual);
            }
            return this;
        }

        public TableAssert<E> isEmpty() {
            return hasRowCount(0);
        }

        public TableAssert<E> containsExactly(E... entities) {
            List<E> actual = table.select().fetch();
            // Compare entities...
            return this;
        }

        public TableAssert<E> hasRowMatching(Condition<E> condition) {
            boolean exists = table.select().where(condition).exists();
            if (!exists) {
                throw new AssertionError("No row matching condition found");
            }
            return this;
        }
    }
}

// Usage
@Test
void shouldDeleteInactiveUsers() {
    TestData.user().status(UserStatus.ACTIVE).insert();
    TestData.user().status(UserStatus.INACTIVE).insert();
    TestData.user().status(UserStatus.INACTIVE).insert();

    userService.deleteInactiveUsers();

    assertThat(UserTable.TABLE)
        .hasRowCount(1)
        .hasRowMatching(UserTable.STATUS.equalTo(UserStatus.ACTIVE));
}
```

## SQL Capture

Capture executed SQL for verification:

```java
@Test
void shouldExecuteOptimizedQuery() {
    try (SqlCapture capture = SqlCapture.start()) {
        userService.findActiveUsersWithOrders();

        List<String> executedSql = capture.getCapturedSql();

        // Verify only 2 queries executed (not N+1)
        assertThat(executedSql).hasSize(2);

        // Verify join was used
        assertThat(executedSql.get(0)).contains("JOIN");
    }
}
```

## Open Questions

- **Database reset strategy**: What's the best approach for resetting database state between tests?
- **Parallel test execution**: How to handle parallel tests with shared database state?
- **Schema validation**: Should there be validation that entity matches actual database schema?
- **Performance baselines**: Should we support performance regression testing for queries?
