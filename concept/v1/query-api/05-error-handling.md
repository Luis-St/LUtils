# Error Handling

## Overview

Database errors are mapped to a clear exception hierarchy. SQL vendor-specific errors are translated to consistent, semantic exceptions.

## Exception Hierarchy

```
DatabaseException (base, unchecked)
├── ConnectionException
│   ├── ConnectionAcquisitionException
│   ├── ConnectionTimeoutException
│   └── ConnectionClosedException
├── TransactionException
│   ├── TransactionTimeoutException
│   ├── TransactionRollbackException
│   └── ReadOnlyTransactionException
├── QueryException
│   ├── QuerySyntaxException
│   ├── QueryTimeoutException
│   └── InvalidParameterException
├── ConstraintViolationException
│   ├── UniqueConstraintViolationException
│   ├── ForeignKeyViolationException
│   ├── CheckConstraintViolationException
│   └── NotNullViolationException
├── DataException
│   ├── DataTruncationException
│   ├── NumericOverflowException
│   └── InvalidDataTypeException
├── EntityException
│   ├── EntityNotFoundException
│   ├── StaleEntityException
│   └── DuplicateEntityException
└── LockingException
    ├── DeadlockException
    ├── LockTimeoutException
    └── LockNotAvailableException
```

## Base Exception

```java
public class DatabaseException extends RuntimeException {

    private final String sqlState;
    private final int vendorCode;
    private final String sql;
    private final List<Object> parameters;

    public DatabaseException(String message, Throwable cause,
                            String sqlState, int vendorCode,
                            String sql, List<Object> parameters) {
        super(message, cause);
        this.sqlState = sqlState;
        this.vendorCode = vendorCode;
        this.sql = sql;
        this.parameters = parameters;
    }

    /** SQL State code (e.g., "23505" for unique violation) */
    public String getSqlState() { return sqlState; }

    /** Database-specific error code */
    public int getVendorCode() { return vendorCode; }

    /** The SQL that caused the error (if available) */
    public Optional<String> getSql() { return Optional.ofNullable(sql); }

    /** Query parameters (if available) */
    public List<Object> getParameters() {
        return parameters != null ? parameters : List.of();
    }
}
```

## Constraint Violations

```java
public class UniqueConstraintViolationException extends ConstraintViolationException {

    private final String constraintName;
    private final String tableName;
    private final List<String> columnNames;
    private final Object duplicateValue;

    /**
     * The name of the violated constraint.
     */
    public String getConstraintName() { return constraintName; }

    /**
     * The table where the constraint is defined.
     */
    public String getTableName() { return tableName; }

    /**
     * The columns involved in the unique constraint.
     */
    public List<String> getColumnNames() { return columnNames; }

    /**
     * The duplicate value that caused the violation (if extractable).
     */
    public Optional<Object> getDuplicateValue() {
        return Optional.ofNullable(duplicateValue);
    }
}

// Usage
try {
    UserTable.TABLE.insert(user);
} catch (UniqueConstraintViolationException e) {
    if (e.getColumnNames().contains("email")) {
        throw new DuplicateEmailException(user.email());
    }
    throw e;
}
```

## Foreign Key Violations

```java
public class ForeignKeyViolationException extends ConstraintViolationException {

    private final String constraintName;
    private final String tableName;
    private final String referencedTableName;
    private final Object missingKeyValue;

    /**
     * The table containing the foreign key.
     */
    public String getTableName() { return tableName; }

    /**
     * The referenced table.
     */
    public String getReferencedTableName() { return referencedTableName; }

    /**
     * The key value that doesn't exist in the referenced table.
     */
    public Optional<Object> getMissingKeyValue() {
        return Optional.ofNullable(missingKeyValue);
    }
}

// Usage
try {
    OrderTable.TABLE.insert(order);
} catch (ForeignKeyViolationException e) {
    if (e.getReferencedTableName().equals("users")) {
        throw new UserNotFoundException(order.userId());
    }
    throw e;
}
```

## Entity Not Found

```java
public class EntityNotFoundException extends EntityException {

    private final Class<?> entityType;
    private final Object identifier;

    public EntityNotFoundException(Class<?> entityType, Object identifier) {
        super(String.format("%s with id %s not found", entityType.getSimpleName(), identifier));
        this.entityType = entityType;
        this.identifier = identifier;
    }

    public Class<?> getEntityType() { return entityType; }
    public Object getIdentifier() { return identifier; }
}

// Thrown by fetchOne() when no result
try {
    User user = UserTable.TABLE.select()
        .where(UserTable.ID.equalTo(userId))
        .fetchOne();
} catch (EntityNotFoundException e) {
    // Handle not found
}

// Use fetchFirst() to get Optional instead
Optional<User> user = UserTable.TABLE.select()
    .where(UserTable.ID.equalTo(userId))
    .fetchFirst();
```

## Locking Exceptions

```java
public class DeadlockException extends LockingException {

    private final List<String> involvedTransactions;
    private final String victimTransaction;

    /**
     * IDs of transactions involved in the deadlock.
     */
    public List<String> getInvolvedTransactions() { return involvedTransactions; }

    /**
     * The transaction that was chosen as the deadlock victim.
     */
    public String getVictimTransaction() { return victimTransaction; }
}

public class LockNotAvailableException extends LockingException {

    private final String tableName;
    private final Object rowIdentifier;

    /**
     * Thrown when NOWAIT is used and the lock is not available.
     */
    public LockNotAvailableException(String tableName, Object rowIdentifier) {
        super(String.format("Lock not available for %s row %s", tableName, rowIdentifier));
        this.tableName = tableName;
        this.rowIdentifier = rowIdentifier;
    }
}

// Usage
try {
    User user = UserTable.TABLE.select()
        .where(UserTable.ID.equalTo(userId))
        .forUpdate().noWait()
        .fetchOne();
} catch (LockNotAvailableException e) {
    // Row is locked by another transaction
    throw new ResourceBusyException("User is being modified by another process");
}
```

## Error Translation

SQL errors are translated based on SQLState codes and vendor-specific codes:

```java
public interface SqlExceptionTranslator {

    /**
     * Translates a SQLException to the appropriate DatabaseException subclass.
     */
    DatabaseException translate(SQLException e, String sql, List<Object> params);
}

// PostgreSQL translator example
public class PostgreSqlExceptionTranslator implements SqlExceptionTranslator {

    @Override
    public DatabaseException translate(SQLException e, String sql, List<Object> params) {
        String sqlState = e.getSQLState();

        if (sqlState == null) {
            return new DatabaseException("Database error", e, null, e.getErrorCode(), sql, params);
        }

        return switch (sqlState) {
            // Class 23: Integrity Constraint Violation
            case "23505" -> parseUniqueViolation(e, sql, params);
            case "23503" -> parseForeignKeyViolation(e, sql, params);
            case "23502" -> parseNotNullViolation(e, sql, params);
            case "23514" -> parseCheckViolation(e, sql, params);

            // Class 40: Transaction Rollback
            case "40001" -> new DeadlockException(e, sql, params);
            case "40P01" -> new DeadlockException(e, sql, params);

            // Class 55: Object Not In Prerequisite State
            case "55P03" -> new LockNotAvailableException(e, sql, params);

            // Class 57: Operator Intervention
            case "57014" -> new QueryTimeoutException(e, sql, params);

            default -> new DatabaseException("Database error", e, sqlState, e.getErrorCode(), sql, params);
        };
    }
}
```

## Retry Support

Some exceptions support automatic retry:

```java
public interface RetryableException {
    /**
     * Returns true if the operation can be safely retried.
     */
    boolean isRetryable();

    /**
     * Suggested delay before retry (if applicable).
     */
    Optional<Duration> getRetryAfter();
}

// DeadlockException is retryable
public class DeadlockException extends LockingException implements RetryableException {
    @Override
    public boolean isRetryable() { return true; }

    @Override
    public Optional<Duration> getRetryAfter() {
        return Optional.of(Duration.ofMillis(100));
    }
}

// Usage with retry
User user = Retry.withBackoff(3, Duration.ofMillis(100))
    .retryOn(DeadlockException.class)
    .execute(() -> {
        try (Transaction tx = Database.beginTransaction()) {
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

## Error Logging

```java
// Configure error logging
DatabaseConfig.builder()
    .errorHandler(new ErrorHandler() {
        @Override
        public void onError(DatabaseException e) {
            log.error("Database error: {} SQL: {} Params: {}",
                e.getMessage(), e.getSql().orElse("N/A"), e.getParameters());
        }

        @Override
        public void onSlowQuery(String sql, Duration duration) {
            log.warn("Slow query ({} ms): {}", duration.toMillis(), sql);
        }
    })
    .slowQueryThreshold(Duration.ofSeconds(1))
    .build();
```

## Open Questions

- **SQLState coverage**: Which SQLState codes should be mapped for each database dialect?
- **Batch error handling**: How should errors in batch operations be reported (all-or-nothing vs partial)?
- **Validation errors**: Should there be pre-execution validation that throws before hitting the database?
- **Async exceptions**: How should exceptions in CompletableFuture chains be wrapped?
