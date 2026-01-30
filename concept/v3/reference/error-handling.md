# Error Handling

## Overview

Database errors are mapped to a clear exception hierarchy. SQL vendor-specific errors are translated to consistent, semantic exceptions that can be handled programmatically.

## Exception Hierarchy

```
SqlDatabaseException (base, unchecked)
+-- SqlConnectionException
|   +-- SqlConnectionAcquisitionException
|   +-- SqlConnectionTimeoutException
|   +-- SqlConnectionClosedException
+-- SqlTransactionException
|   +-- SqlTransactionTimeoutException
|   +-- SqlTransactionRollbackException
|   +-- SqlReadOnlyTransactionException
+-- SqlQueryException
|   +-- SqlQuerySyntaxException
|   +-- SqlQueryTimeoutException
|   +-- SqlInvalidParameterException
+-- SqlConstraintViolationException
|   +-- SqlUniqueConstraintViolationException
|   +-- SqlForeignKeyViolationException
|   +-- SqlCheckConstraintViolationException
|   +-- SqlNotNullViolationException
+-- SqlDataException
|   +-- SqlDataTruncationException
|   +-- SqlNumericOverflowException
|   +-- SqlInvalidDataTypeException
+-- SqlEntityException
|   +-- SqlEntityNotFoundException
|   +-- SqlStaleEntityException
|   +-- SqlDuplicateEntityException
|   +-- SqlRelationshipNotLoadedException
+-- SqlLockingException
    +-- SqlDeadlockException
    +-- SqlLockTimeoutException
    +-- SqlLockNotAvailableException
```

## Base Exception

```java
public class SqlDatabaseException extends RuntimeException {

    private final String sqlState;
    private final int vendorCode;
    private final String sql;
    private final List<Object> parameters;

    public SqlDatabaseException(String message, Throwable cause,
                            String sqlState, int vendorCode,
                            String sql, List<Object> parameters) {
        super(message, cause);
        this.sqlState = sqlState;
        this.vendorCode = vendorCode;
        this.sql = sql;
        this.parameters = parameters;
    }

    /**
     * SQL State code (e.g., "23505" for unique violation).<br>
     */
    public String getSqlState() {
        return sqlState;
    }

    /**
     * Database-specific error code.<br>
     */
    public int getVendorCode() {
        return vendorCode;
    }

    /**
     * The SQL that caused the error (if available).<br>
     */
    public Optional<String> getSql() {
        return Optional.ofNullable(sql);
    }

    /**
     * Query parameters (if available).<br>
     */
    public List<Object> getParameters() {
        return parameters != null ? parameters : List.of();
    }
}
```

## Constraint Violations

### Unique Constraint Violation

```java
public class SqlUniqueConstraintViolationException extends SqlConstraintViolationException {

    private final String constraintName;
    private final String tableName;
    private final List<String> columnNames;
    private final Object duplicateValue;

    /**
     * The name of the violated constraint.<br>
     */
    public String getConstraintName() {
        return constraintName;
    }

    /**
     * The table where the constraint is defined.<br>
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * The columns involved in the unique constraint.<br>
     */
    public List<String> getColumnNames() {
        return columnNames;
    }

    /**
     * The duplicate value that caused the violation (if extractable).<br>
     */
    public Optional<Object> getDuplicateValue() {
        return Optional.ofNullable(duplicateValue);
    }
}

// Usage
try {
    UserTable.TABLE.insert(user);
} catch (SqlUniqueConstraintViolationException e) {
    if (e.getColumnNames().contains("email")) {
        throw new DuplicateEmailException(user.email());
    }
    throw e;
}
```

### Foreign Key Violation

```java
public class SqlForeignKeyViolationException extends SqlConstraintViolationException {

    private final String constraintName;
    private final String tableName;
    private final String referencedTableName;
    private final Object missingKeyValue;

    /**
     * The table containing the foreign key.<br>
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * The referenced table.<br>
     */
    public String getReferencedTableName() {
        return referencedTableName;
    }

    /**
     * The key value that doesn't exist in the referenced table.<br>
     */
    public Optional<Object> getMissingKeyValue() {
        return Optional.ofNullable(missingKeyValue);
    }
}

// Usage
try {
    OrderTable.TABLE.insert(order);
} catch (SqlForeignKeyViolationException e) {
    if (e.getReferencedTableName().equals("users")) {
        throw new UserNotFoundException(order.customerId());
    }
    throw e;
}
```

### Other Constraint Violations

```java
// Check constraint violation
public class SqlCheckConstraintViolationException extends SqlConstraintViolationException {
    private final String constraintName;
    private final String expression;  // The check expression if available
}

// Not null violation
public class SqlNotNullViolationException extends SqlConstraintViolationException {
    private final String columnName;
}
```

## Entity Exceptions

### Entity Not Found

```java
public class SqlEntityNotFoundException extends SqlEntityException {

    private final Class<?> entityType;
    private final Object identifier;

    public SqlEntityNotFoundException(Class<?> entityType, Object identifier) {
        super(String.format("%s with id %s not found",
            entityType.getSimpleName(), identifier));
        this.entityType = entityType;
        this.identifier = identifier;
    }

    public Class<?> getEntityType() {
        return entityType;
    }

    public Object getIdentifier() {
        return identifier;
    }
}

// Thrown by fetchOne() when no result
try {
    User user = UserTable.TABLE.select()
        .where(UserTable.ID.equalTo(userId))
        .fetchOne();
} catch (SqlEntityNotFoundException e) {
    // Handle not found
    log.warn("User {} not found", e.getIdentifier());
}

// Use fetchFirst() to get Optional instead
Optional<User> user = UserTable.TABLE.select()
    .where(UserTable.ID.equalTo(userId))
    .fetchFirst();
```

### Relationship Not Loaded

```java
public class SqlRelationshipNotLoadedException extends SqlEntityException {

    private final String relationshipName;
    private final Class<?> entityType;

    public SqlRelationshipNotLoadedException(String relationshipName, Class<?> entityType) {
        super(String.format("Relationship '%s' not loaded on %s. " +
            "Use FullJoined entity or explicit join.",
            relationshipName, entityType.getSimpleName()));
        this.relationshipName = relationshipName;
        this.entityType = entityType;
    }
}

// Thrown by SqlForeignKey.requireValue() when not loaded
try {
    UserPartialJoined user = ...;
    Address address = user.addressKey().requireValue();  // Throws if not loaded
} catch (SqlRelationshipNotLoadedException e) {
    log.warn("Address not loaded for user");
}
```

### Stale Entity

```java
public class SqlStaleEntityException extends SqlEntityException {

    private final Object entityId;
    private final long expectedVersion;
    private final long actualVersion;

    // Thrown when optimistic locking fails
}

// Usage with optimistic locking
try {
    UserTable.TABLE.update(user);
} catch (SqlStaleEntityException e) {
    // Entity was modified by another transaction
    User fresh = UserTable.TABLE.select()
        .where(UserTable.ID.equalTo(e.getEntityId()))
        .fetchOne();
    // Retry or report conflict
}
```

## Locking Exceptions

```java
public class SqlDeadlockException extends SqlLockingException {

    private final List<String> involvedTransactions;
    private final String victimTransaction;

    /**
     * IDs of transactions involved in the deadlock.<br>
     */
    public List<String> getInvolvedTransactions() {
        return involvedTransactions;
    }

    /**
     * The transaction chosen as the deadlock victim.<br>
     */
    public String getVictimTransaction() {
        return victimTransaction;
    }
}

public class SqlLockNotAvailableException extends SqlLockingException {

    private final String tableName;
    private final Object rowIdentifier;

    /**
     * Thrown when NOWAIT is used and the lock is not available.<br>
     */
}

// Usage
try {
    User user = UserTable.TABLE.select()
        .where(UserTable.ID.equalTo(userId))
        .forUpdate().noWait()
        .fetchOne();
} catch (SqlLockNotAvailableException e) {
    // Row is locked by another transaction
    throw new ResourceBusyException("User is being modified by another process");
}
```

## Error Translation

SQL errors are translated based on SQLState codes and vendor-specific codes:

```java
public interface SqlExceptionTranslator {

    /**
     * Translates a SQLException to the appropriate SqlDatabaseException subclass.<br>
     */
    SqlDatabaseException translate(SQLException e, String sql, List<Object> params);
}

// PostgreSQL translator
public class PostgreSqlExceptionTranslator implements SqlExceptionTranslator {

    @Override
    public SqlDatabaseException translate(SQLException e, String sql, List<Object> params) {
        String sqlState = e.getSQLState();

        if (sqlState == null) {
            return new SqlDatabaseException("Database error", e, null,
                e.getErrorCode(), sql, params);
        }

        return switch (sqlState) {
            // Class 23: Integrity Constraint Violation
            case "23505" -> parseUniqueViolation(e, sql, params);
            case "23503" -> parseForeignKeyViolation(e, sql, params);
            case "23502" -> parseNotNullViolation(e, sql, params);
            case "23514" -> parseCheckViolation(e, sql, params);

            // Class 40: Transaction Rollback
            case "40001", "40P01" -> new SqlDeadlockException(e, sql, params);

            // Class 55: Object Not In Prerequisite State
            case "55P03" -> new SqlLockNotAvailableException(e, sql, params);

            // Class 57: Operator Intervention
            case "57014" -> new SqlQueryTimeoutException(e, sql, params);

            // Class 08: Connection Exception
            case "08000", "08003", "08006" ->
                new SqlConnectionException(e.getMessage(), e, sqlState);

            default -> new SqlDatabaseException("Database error", e, sqlState,
                e.getErrorCode(), sql, params);
        };
    }

    private SqlUniqueConstraintViolationException parseUniqueViolation(
            SQLException e, String sql, List<Object> params) {
        // Parse PostgreSQL error message for details
        // "duplicate key value violates unique constraint \"users_email_key\""
        // "Detail: Key (email)=(test@example.com) already exists."
        String message = e.getMessage();
        String constraintName = extractConstraintName(message);
        String columnName = extractColumnName(message);
        Object value = extractDuplicateValue(message);

        return new SqlUniqueConstraintViolationException(
            e, sql, params, constraintName, null,
            columnName != null ? List.of(columnName) : List.of(), value
        );
    }
}
```

## Retry Support

Some exceptions support automatic retry:

```java
public interface SqlRetryableException {

    /**
     * Returns true if the operation can be safely retried.<br>
     */
    boolean isRetryable();

    /**
     * Suggested delay before retry (if applicable).<br>
     */
    Optional<Duration> getRetryAfter();
}

// SqlDeadlockException is retryable
public class SqlDeadlockException extends SqlLockingException implements SqlRetryableException {

    @Override
    public boolean isRetryable() {
        return true;
    }

    @Override
    public Optional<Duration> getRetryAfter() {
        return Optional.of(Duration.ofMillis(100));
    }
}

// SqlQueryTimeoutException may be retryable
public class SqlQueryTimeoutException extends SqlQueryException implements SqlRetryableException {

    @Override
    public boolean isRetryable() {
        return true;  // Transient timeout
    }

    @Override
    public Optional<Duration> getRetryAfter() {
        return Optional.of(Duration.ofSeconds(1));
    }
}

// Usage with retry
User user = SqlRetry.withBackoff(3, Duration.ofMillis(100))
    .retryOn(SqlDeadlockException.class, SqlQueryTimeoutException.class)
    .execute(() -> {
        try (SqlTransaction tx = database.beginTransaction()) {
            User u = UserTable.TABLE.select()
                .where(UserTable.ID.equalTo(userId))
                .forUpdate()
                .fetchOne();
            u = u.withLoginCount(u.loginCount() + 1);
            UserTable.TABLE.update(u);
            tx.commit();
            return u;
        }
    });
```

## Retry Helper

```java
public final class SqlRetry {

    public static SqlRetryBuilder withBackoff(int maxAttempts, Duration initialDelay) {
        return new SqlRetryBuilder(maxAttempts, initialDelay);
    }

    public static class SqlRetryBuilder {
        private final int maxAttempts;
        private final Duration initialDelay;
        private double multiplier = 2.0;
        private Duration maxDelay = Duration.ofSeconds(30);
        private Set<Class<? extends Exception>> retryOn = new HashSet<>();

        public SqlRetryBuilder multiplier(double multiplier) {
            this.multiplier = multiplier;
            return this;
        }

        public SqlRetryBuilder maxDelay(Duration maxDelay) {
            this.maxDelay = maxDelay;
            return this;
        }

        @SafeVarargs
        public final SqlRetryBuilder retryOn(Class<? extends Exception>... exceptions) {
            this.retryOn.addAll(Arrays.asList(exceptions));
            return this;
        }

        public <T> T execute(Supplier<T> action) {
            int attempt = 0;
            Duration delay = initialDelay;

            while (true) {
                try {
                    return action.get();
                } catch (Exception e) {
                    attempt++;
                    if (attempt >= maxAttempts || !shouldRetry(e)) {
                        throw e;
                    }

                    try {
                        Thread.sleep(delay.toMillis());
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry interrupted", ie);
                    }

                    delay = Duration.ofMillis(
                        Math.min((long)(delay.toMillis() * multiplier), maxDelay.toMillis())
                    );
                }
            }
        }

        private boolean shouldRetry(Exception e) {
            if (e instanceof SqlRetryableException re && re.isRetryable()) {
                return true;
            }
            return retryOn.stream().anyMatch(cls -> cls.isInstance(e));
        }
    }
}
```

## Error Logging

```java
// Configure error logging
SqlDatabaseConfig.builder()
    .errorHandler(new SqlErrorHandler() {
        @Override
        public void onError(SqlDatabaseException e) {
            log.error("Database error: {} SQL: {} Params: {}",
                e.getMessage(),
                e.getSql().orElse("N/A"),
                maskSensitiveParams(e.getParameters()));
        }

        @Override
        public void onSlowQuery(String sql, Duration duration) {
            log.warn("Slow query ({} ms): {}", duration.toMillis(), sql);
        }
    })
    .slowQueryThreshold(Duration.ofSeconds(1))
    .build();

// Mask sensitive parameters
private List<Object> maskSensitiveParams(List<Object> params) {
    return params.stream()
        .map(p -> {
            if (p instanceof String s && s.length() > 50) {
                return s.substring(0, 10) + "...[TRUNCATED]";
            }
            return p;
        })
        .toList();
}
```

## Async Exception Handling

```java
// Exceptions in CompletableFuture are wrapped
CompletableFuture<User> user = UserTable.TABLE.select()
    .where(UserTable.ID.equalTo(userId))
    .fetchOneAsync()
    .exceptionally(e -> {
        // Unwrap CompletionException
        Throwable cause = e.getCause();
        if (cause instanceof SqlEntityNotFoundException) {
            return User.anonymous();
        }
        if (cause instanceof SqlDatabaseException dbEx) {
            log.error("Database error: {}", dbEx.getSql().orElse("unknown"));
        }
        throw new CompletionException(cause);
    });

// Handle specific types
CompletableFuture<Result<User>> result = UserTable.TABLE.select()
    .where(UserTable.ID.equalTo(userId))
    .fetchOneAsync()
    .handle((user, error) -> {
        if (error != null) {
            Throwable cause = error.getCause();
            if (cause instanceof SqlEntityNotFoundException) {
                return Result.notFound();
            }
            return Result.error(cause);
        }
        return Result.success(user);
    });
```

## Related Documents

- [transaction-api.md](transaction-api.md) - Transaction exception handling
- [async-api.md](async-api.md) - Async exception handling
- [dialect-system.md](dialect-system.md) - Dialect-specific error translation
