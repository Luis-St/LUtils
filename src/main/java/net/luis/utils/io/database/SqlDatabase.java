/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package net.luis.utils.io.database;

import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.function.throwable.ThrowableSupplier;
import net.luis.utils.io.database.audit.SqlAuditUserProvider;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.database.SqlConnectionException;
import net.luis.utils.io.database.query.SqlQueryProvider;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.table.SqlTableProvider;
import net.luis.utils.io.database.transaction.*;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.io.Closeable;
import java.sql.*;
import java.time.Duration;
import java.util.Objects;

/**
 * Represents a fully configured database that acts as the central entry point for all database operations.<br>
 * A database wraps a {@link DataSource}, an {@link SqlDialect} and a set of default behaviors and exposes
 * schema management, table and query access, transactions and sessions through these.<br>
 * Instances are created through the {@link SqlDatabaseBuilder} obtained from {@link #builder(DataSource, SqlDialect)}.<br>
 *
 * @see SqlDatabaseBuilder
 * @see SqlProvider
 * @see SqlSession
 * @see SqlTransaction
 *
 * @author Luis-St
 */
@SuppressWarnings("SqlSourceToSinkFlow")
public class SqlDatabase implements SqlProvider, AutoCloseable {
	
	/**
	 * The data source used to obtain connections to the underlying database.<br>
	 */
	private final DataSource dataSource;
	/**
	 * The sql dialect used to render statements for the underlying database.<br>
	 */
	private final SqlDialect dialect;
	/**
	 * The timeout applied to individual queries executed against the database.<br>
	 */
	private final Duration queryTimeout;
	/**
	 * The default isolation level used for transactions started without an explicit isolation level.<br>
	 */
	private final SqlIsolationLevel defaultTransactionIsolationLevel;
	/**
	 * The default propagation behavior used for transactions started without an explicit propagation.<br>
	 */
	private final SqlPropagation defaultTransactionPropagation;
	/**
	 * Whether the data source should be closed when this database is closed.<br>
	 */
	private final boolean autoCloseDataSource;
	/**
	 * The audit user provider used to determine the current user for audited operations.<br>
	 */
	private final SqlAuditUserProvider auditUserProvider;
	/**
	 * The transaction manager used to create and manage transactions for this database.<br>
	 */
	private final SqlTransactionManager transactionManager;
	
	/**
	 * Constructs a new sql database with the given configuration.<br>
	 * This constructor is package-private; instances are created through the {@link SqlDatabaseBuilder}.<br>
	 *
	 * @param dataSource The data source used to obtain connections to the underlying database
	 * @param dialect The sql dialect used to render statements
	 * @param queryTimeout The timeout applied to individual queries
	 * @param connectionAcquisitionTimeout The timeout applied when acquiring a connection from the data source
	 * @param defaultTransactionIsolationLevel The default isolation level for transactions
	 * @param defaultTransactionPropagation The default propagation behavior for transactions
	 * @param autoCloseDataSource Whether the data source should be closed when this database is closed
	 * @param auditUserProvider The audit user provider used for audited operations
	 * @throws NullPointerException If any of the non-primitive arguments is null
	 * @throws SqlConnectionException If the connection to the underlying database can not be established
	 */
	SqlDatabase(
		@NonNull DataSource dataSource,
		@NonNull SqlDialect dialect,
		@NonNull Duration queryTimeout,
		@NonNull Duration connectionAcquisitionTimeout,
		@NonNull SqlIsolationLevel defaultTransactionIsolationLevel,
		@NonNull SqlPropagation defaultTransactionPropagation,
		boolean autoCloseDataSource,
		@NonNull SqlAuditUserProvider auditUserProvider
	) throws SqlConnectionException {
		this.dataSource = Objects.requireNonNull(dataSource, "Data source must not be null");
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
		this.queryTimeout = Objects.requireNonNull(queryTimeout, "Query timeout must not be null");
		this.defaultTransactionIsolationLevel = Objects.requireNonNull(defaultTransactionIsolationLevel, "Default transaction isolation level must not be null");
		this.defaultTransactionPropagation = Objects.requireNonNull(defaultTransactionPropagation, "Default transaction propagation behavior must not be null");
		this.autoCloseDataSource = autoCloseDataSource;
		this.auditUserProvider = Objects.requireNonNull(auditUserProvider, "Audit user provider must not be null");
		this.transactionManager = new SqlTransactionManager(dataSource, dialect, queryTimeout, connectionAcquisitionTimeout);
	}
	
	/**
	 * Creates a new builder for a sql database using the given data source and dialect.<br>
	 *
	 * @param dataSource The data source used to obtain connections to the underlying database
	 * @param dialect The sql dialect used to render statements
	 * @return A new database builder configured with the given data source and dialect
	 * @throws NullPointerException If the data source or dialect is null
	 */
	public static @NonNull SqlDatabaseBuilder builder(@NonNull DataSource dataSource, @NonNull SqlDialect dialect) {
		return new SqlDatabaseBuilder(dataSource, dialect);
	}
	
	/**
	 * Returns the data source used by this database to obtain connections.<br>
	 * @return The data source
	 */
	public @NonNull DataSource getDataSource() {
		return this.dataSource;
	}
	
	/**
	 * Returns the sql dialect used by this database to render statements.<br>
	 * @return The sql dialect
	 */
	public @NonNull SqlDialect getDialect() {
		return this.dialect;
	}
	
	/**
	 * Returns the audit user provider used by this database for audited operations.<br>
	 * @return The audit user provider
	 */
	public @NonNull SqlAuditUserProvider getAuditUserProvider() {
		return this.auditUserProvider;
	}
	
	/**
	 * Checks the health of this database by validating a connection with a timeout of five seconds.<br>
	 * @return {@code true} if a valid connection could be obtained, {@code false} otherwise
	 */
	public boolean health() {
		try (Connection connection = this.dataSource.getConnection()) {
			return connection.isValid(5);
		} catch (SQLException e) {
			return false;
		}
	}
	
	/**
	 * Pings this database by validating a connection with a timeout of one second.<br>
	 * @return {@code true} if a valid connection could be obtained, {@code false} otherwise
	 */
	public boolean ping() {
		try (Connection connection = this.dataSource.getConnection()) {
			return connection.isValid(1);
		} catch (SQLException e) {
			return false;
		}
	}
	
	@Override
	public void createSchema(@NonNull String name) throws SqlException {
		Objects.requireNonNull(name, "Sql schema name must not be null");
		
		try (Connection connection = this.dataSource.getConnection(); Statement statement = connection.createStatement()) {
			statement.execute(this.dialect.schemaRenderer().renderCreateSchema(name, false).sql());
		} catch (SQLException e) {
			throw new SqlException("Failed to create sql schema '" + name + "'", e);
		}
	}
	
	@Override
	public void createSchemaIfNotExists(@NonNull String name) throws SqlException {
		Objects.requireNonNull(name, "Sql schema name must not be null");
		
		try (Connection connection = this.dataSource.getConnection(); Statement statement = connection.createStatement()) {
			statement.execute(this.dialect.schemaRenderer().renderCreateSchema(name, true).sql());
		} catch (SQLException e) {
			throw new SqlException("Failed to create sql schema '" + name + "'", e);
		}
	}
	
	@Override
	public boolean existsSchema(@NonNull String name) throws SqlException {
		Objects.requireNonNull(name, "Sql schema name must not be null");
		
		try (Connection connection = this.dataSource.getConnection(); ResultSet resultSet = connection.getMetaData().getSchemas()) {
			while (resultSet.next()) {
				if (name.equals(resultSet.getString("TABLE_SCHEM"))) {
					return true;
				}
			}
			return false;
		} catch (SQLException e) {
			throw new SqlException("Failed to check if sql schema '" + name + "' exists", e);
		}
	}
	
	@Override
	public void dropSchema(@NonNull String name, boolean cascade) throws SqlException {
		Objects.requireNonNull(name, "Sql schema name must not be null");
		
		try (Connection connection = this.dataSource.getConnection(); Statement statement = connection.createStatement()) {
			statement.execute(this.dialect.schemaRenderer().renderDropSchema(name, false, cascade).sql());
		} catch (SQLException e) {
			throw new SqlException("Failed to drop sql schema '" + name + "'", e);
		}
	}
	
	@Override
	public @NonNull <T> SqlTableProvider<T> table(@NonNull SqlTable<T> table) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		return new SqlTableProvider<>(table, this.dialect, SqlConnectionSource.pooled(this.dataSource), this.queryTimeout);
	}
	
	@Override
	public @NonNull <T> SqlQueryProvider<T> from(@NonNull SqlTable<T> table) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		return new SqlQueryProvider<>(table, this.dialect, SqlConnectionSource.pooled(this.dataSource), this.queryTimeout, this.auditUserProvider, null);
	}
	
	/**
	 * Begins a new read-write transaction using the default isolation level and propagation behavior of this database.<br>
	 *
	 * @return The started transaction
	 * @throws SqlException If the transaction can not be started
	 */
	public @NonNull SqlTransaction beginTransaction() throws SqlException {
		return this.transactionManager.begin(false, this.defaultTransactionIsolationLevel, this.defaultTransactionPropagation);
	}
	
	/**
	 * Begins a new read-write transaction using the given isolation level and propagation behavior.<br>
	 *
	 * @param isolationLevel The isolation level to use for the transaction
	 * @param propagation The propagation behavior to use for the transaction
	 * @return The started transaction
	 * @throws NullPointerException If the isolation level or propagation behavior is null
	 * @throws SqlException If the transaction can not be started
	 */
	public @NonNull SqlTransaction beginTransaction(@NonNull SqlIsolationLevel isolationLevel, @NonNull SqlPropagation propagation) throws SqlException {
		return this.transactionManager.begin(false, isolationLevel, propagation);
	}
	
	/**
	 * Begins a new read-only transaction using the default isolation level and propagation behavior of this database.<br>
	 *
	 * @return The started transaction
	 * @throws SqlException If the transaction can not be started
	 */
	public @NonNull SqlTransaction beginReadOnlyTransaction() throws SqlException {
		return this.transactionManager.begin(true, this.defaultTransactionIsolationLevel, this.defaultTransactionPropagation);
	}
	
	/**
	 * Begins a new read-only transaction using the given isolation level and propagation behavior.<br>
	 *
	 * @param isolationLevel The isolation level to use for the transaction
	 * @param propagation The propagation behavior to use for the transaction
	 * @return The started transaction
	 * @throws NullPointerException If the isolation level or propagation behavior is null
	 * @throws SqlException If the transaction can not be started
	 */
	public @NonNull SqlTransaction beginReadOnlyTransaction(@NonNull SqlIsolationLevel isolationLevel, @NonNull SqlPropagation propagation) throws SqlException {
		return this.transactionManager.begin(true, isolationLevel, propagation);
	}
	
	/**
	 * Executes the given action inside a newly started transaction and returns its result.<br>
	 * The transaction is committed if the action completes normally and rolled back if it throws.<br>
	 *
	 * @param action The action to execute within the transaction
	 * @param <T> The type of the result produced by the action
	 * @return The result produced by the action
	 * @throws NullPointerException If the action is null
	 * @throws SqlException If the action or the commit fails
	 */
	public <T> @UnknownNullability T inTransaction(@NonNull ThrowableFunction<SqlTransaction, T, SqlException> action) throws SqlException {
		try (SqlTransaction tx = this.beginTransaction()) {
			return this.inTransaction(tx, action);
		}
	}
	
	/**
	 * Executes the given action inside the given transaction and returns its result.<br>
	 * The transaction is committed if the action completes normally and rolled back if it throws.<br>
	 *
	 * @param transaction The transaction to execute the action within
	 * @param action The action to execute within the transaction
	 * @param <T> The type of the result produced by the action
	 * @return The result produced by the action
	 * @throws NullPointerException If the transaction or action is null
	 * @throws SqlException If the action or the commit fails
	 */
	public <T> @UnknownNullability T inTransaction(@NonNull SqlTransaction transaction, @NonNull ThrowableSupplier<T, SqlException> action) throws SqlException {
		Objects.requireNonNull(action, "Sql transaction action must not be null");
		return this.inTransaction(transaction, _ -> action.get());
	}
	
	/**
	 * Executes the given action inside the given transaction and returns its result.<br>
	 * The action receives the transaction it is executed within.<br>
	 * The transaction is committed if the action completes normally and rolled back if it throws.<br>
	 *
	 * @param transaction The transaction to execute the action within
	 * @param action The action to execute within the transaction
	 * @param <T> The type of the result produced by the action
	 * @return The result produced by the action
	 * @throws NullPointerException If the transaction or action is null
	 * @throws SqlException If the action or the commit fails
	 */
	public <T> @UnknownNullability T inTransaction(@NonNull SqlTransaction transaction, @NonNull ThrowableFunction<SqlTransaction, T, SqlException> action) throws SqlException {
		try {
			T result = action.apply(transaction);
			transaction.commit();
			return result;
		} catch (Exception e) {
			try {
				transaction.rollback();
			} catch (SqlException rollbackEx) {
				e.addSuppressed(rollbackEx);
			}
			
			if (e instanceof SqlException sqlEx) {
				throw sqlEx;
			}
			
			if (e instanceof RuntimeException rte) {
				throw rte;
			}
			throw new SqlException("Sql transaction failed", e);
		}
	}
	
	/**
	 * Opens a new session backed by a pooled connection from the data source of this database.<br>
	 * The session uses the audit user provider configured for this database.<br>
	 *
	 * @return The opened session
	 */
	public @NonNull SqlSession openSession() {
		return new SqlSession(this, SqlConnectionSource.pooled(this.dataSource), this.queryTimeout, null);
	}
	
	/**
	 * Opens a new session backed by a pooled connection from the data source of this database.<br>
	 * The session uses the given audit user provider instead of the one configured for this database.<br>
	 *
	 * @param auditUserProvider The audit user provider to use for the session
	 * @return The opened session
	 * @throws NullPointerException If the audit user provider is null
	 */
	public @NonNull SqlSession openSession(@NonNull SqlAuditUserProvider auditUserProvider) {
		Objects.requireNonNull(auditUserProvider, "Sql audit user provider must not be null");
		return new SqlSession(this, SqlConnectionSource.pooled(this.dataSource), this.queryTimeout, auditUserProvider);
	}
	
	/**
	 * Opens a new session bound to the connection of the given transaction.<br>
	 * The session uses the audit user provider configured for this database.<br>
	 *
	 * @param transaction The transaction whose connection backs the session
	 * @return The opened session
	 * @throws NullPointerException If the transaction is null
	 */
	public @NonNull SqlSession openSession(@NonNull SqlTransaction transaction) {
		Objects.requireNonNull(transaction, "Sql transaction must not be null");
		return new SqlSession(this, SqlConnectionSource.fixed(transaction.getConnection()), this.queryTimeout, null, transaction);
	}
	
	/**
	 * Opens a new session bound to the connection of the given transaction.<br>
	 * The session uses the given audit user provider instead of the one configured for this database.<br>
	 *
	 * @param transaction The transaction whose connection backs the session
	 * @param auditUserProvider The audit user provider to use for the session
	 * @return The opened session
	 * @throws NullPointerException If the transaction or audit user provider is null
	 */
	public @NonNull SqlSession openSession(@NonNull SqlTransaction transaction, @NonNull SqlAuditUserProvider auditUserProvider) {
		Objects.requireNonNull(transaction, "Sql transaction must not be null");
		Objects.requireNonNull(auditUserProvider, "Audit user provider must not be null");
		
		return new SqlSession(this, SqlConnectionSource.fixed(transaction.getConnection()), this.queryTimeout, auditUserProvider, transaction);
	}
	
	@Override
	public void close() throws SqlException {
		if (this.autoCloseDataSource) {
			if (this.dataSource instanceof Closeable closeable) {
				try {
					closeable.close();
				} catch (Exception e) {
					throw new SqlException("Failed to close data source", e);
				}
			} else if (this.dataSource instanceof AutoCloseable autoCloseable) {
				try {
					autoCloseable.close();
				} catch (Exception e) {
					throw new SqlException("Failed to close data source", e);
				}
			}
		}
	}
}
