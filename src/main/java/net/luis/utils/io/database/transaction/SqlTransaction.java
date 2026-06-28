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

package net.luis.utils.io.database.transaction;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.luis.utils.io.database.SqlConnectionSource;
import net.luis.utils.io.database.SqlProvider;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlClientException;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.transaction.SqlTransactionStateException;
import net.luis.utils.io.database.exception.database.transaction.*;
import net.luis.utils.io.database.query.SqlQueryProvider;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.table.SqlTableProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.*;
import java.time.Duration;
import java.util.*;

/**
 * Represents a single active database transaction bound to a {@link Connection}.<br>
 * A transaction exposes schema and table access through {@link SqlProvider} while coordinating the begin,
 * commit, rollback and savepoint lifecycle of the underlying connection.<br>
 * <p>
 *     Depending on how it was created a transaction may own its connection and commit, merely join an outer
 *     transaction, run as a nested savepoint or execute non-transactionally in auto-commit mode.<br>
 *     Registered {@link SqlTransactionListener listeners} are notified after the transaction commits, rolls
 *     back and closes.
 * </p>
 * Instances are not thread-safe and must be confined to the thread that created them.<br>
 *
 * @see SqlTransactionManager
 * @see SqlTransactionListener
 * @see SqlSavepoint
 *
 * @author Luis-St
 */

// Not thread-safe - instances must be confined to a single thread
@SuppressWarnings("SqlSourceToSinkFlow")
public class SqlTransaction implements SqlProvider, AutoCloseable {
	
	/**
	 * The jdbc connection backing this transaction.
	 */
	private final Connection connection;
	/**
	 * The sql dialect used to render statements within this transaction.
	 */
	private final SqlDialect dialect;
	/**
	 * Whether this transaction is read-only.
	 */
	private final boolean readOnly;
	/**
	 * The timeout applied to queries executed within this transaction.
	 */
	private final Duration queryTimeout;
	/**
	 * The isolation level of this transaction.
	 */
	private final SqlIsolationLevel isolationLevel;
	/**
	 * Whether this transaction owns the connection and must restore and close it.
	 */
	private final boolean ownsConnection;
	/**
	 * Whether this transaction is responsible for committing or rolling back the connection.
	 */
	private final boolean ownsCommit;
	/**
	 * Whether this transaction runs in auto-commit mode without transactional semantics.
	 */
	private final boolean nonTransactional;
	/**
	 * The outer transaction that was suspended for this one, or {@code null} if none.
	 */
	private final @Nullable SqlTransaction suspended;
	/**
	 * The original auto-commit state of the connection captured for restoration.
	 */
	private final boolean originalAutoCommit;
	/**
	 * The original read-only state of the connection captured for restoration.
	 */
	private final boolean originalReadOnly;
	/**
	 * The original isolation level of the connection captured for restoration.
	 */
	private final int originalIsolationLevel;
	/**
	 * The named savepoints created within this transaction.
	 */
	private final Map<String, Savepoint> savepoints = Maps.newLinkedHashMap();
	/**
	 * The registered transaction listeners.
	 */
	private final List<SqlTransactionListener> listeners = Lists.newArrayList();
	/**
	 * The savepoint backing this transaction when it represents a nested transaction, or {@code null} otherwise.
	 */
	private @Nullable Savepoint nestedSavepoint;
	/**
	 * The current lifecycle state of this transaction.
	 */
	private SqlTransactionState state = SqlTransactionState.ACTIVE;
	
	/**
	 * Constructs a new sql transaction wrapping the given connection.<br>
	 * If the transaction owns the connection the original auto-commit, read-only and isolation settings are
	 * captured so they can be restored when the transaction is closed.<br>
	 *
	 * @param connection The jdbc connection backing this transaction
	 * @param dialect The sql dialect used to render statements
	 * @param readOnly Whether the transaction is read-only
	 * @param queryTimeout The timeout applied to queries executed within this transaction
	 * @param isolationLevel The isolation level of this transaction
	 * @param ownsConnection Whether this transaction owns the connection and must restore and close it
	 * @param ownsCommit Whether this transaction is responsible for committing or rolling back the connection
	 * @param nonTransactional Whether this transaction runs in auto-commit mode without transactional semantics
	 * @param suspended The outer transaction that was suspended for this one, or {@code null} if none
	 * @throws NullPointerException If the connection, dialect, query timeout or isolation level is null
	 * @throws SqlTransactionConnectionException If reading the original connection state fails
	 */
	public SqlTransaction(@NonNull Connection connection, @NonNull SqlDialect dialect, boolean readOnly, @NonNull Duration queryTimeout, @NonNull SqlIsolationLevel isolationLevel, boolean ownsConnection, boolean ownsCommit, boolean nonTransactional, @Nullable SqlTransaction suspended) throws SqlTransactionConnectionException {
		this.connection = Objects.requireNonNull(connection, "Connection must not be null");
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
		this.readOnly = readOnly;
		this.queryTimeout = Objects.requireNonNull(queryTimeout, "Query timeout must not be null");
		this.isolationLevel = Objects.requireNonNull(isolationLevel, "Isolation level must not be null");
		this.ownsConnection = ownsConnection;
		this.ownsCommit = ownsCommit;
		this.nonTransactional = nonTransactional;
		this.suspended = suspended;
		
		if (ownsConnection) {
			try {
				this.originalAutoCommit = connection.getAutoCommit();
				this.originalReadOnly = connection.isReadOnly();
				this.originalIsolationLevel = connection.getTransactionIsolation();
			} catch (SQLException e) {
				throw new SqlTransactionConnectionException("Failed to read original connection state", e);
			}
		} else {
			this.originalAutoCommit = true;
			this.originalReadOnly = false;
			this.originalIsolationLevel = Connection.TRANSACTION_READ_COMMITTED;
		}
	}
	
	/**
	 * Sets the savepoint that backs this transaction when it represents a nested transaction.<br>
	 *
	 * @param nestedSavepoint The savepoint to roll back to or release for this nested transaction
	 * @throws NullPointerException If the nested savepoint is null
	 */
	void setNestedSavepoint(@NonNull Savepoint nestedSavepoint) {
		this.nestedSavepoint = Objects.requireNonNull(nestedSavepoint, "Nested savepoint must not be null");
	}
	
	/**
	 * Returns the jdbc connection backing this transaction.<br>
	 * @return The connection
	 */
	public @NonNull Connection getConnection() {
		return this.connection;
	}
	
	/**
	 * Returns the sql dialect used to render statements within this transaction.<br>
	 * @return The sql dialect
	 */
	public @NonNull SqlDialect getDialect() {
		return this.dialect;
	}
	
	/**
	 * Returns whether this transaction is read-only.<br>
	 * @return True if the transaction is read-only, false otherwise
	 */
	public boolean isReadOnly() {
		return this.readOnly;
	}
	
	/**
	 * Returns the timeout applied to queries executed within this transaction.<br>
	 * @return The query timeout
	 */
	public @NonNull Duration getQueryTimeout() {
		return this.queryTimeout;
	}
	
	/**
	 * Returns the isolation level of this transaction.<br>
	 * @return The isolation level
	 */
	public @NonNull SqlIsolationLevel getIsolationLevel() {
		return this.isolationLevel;
	}
	
	/**
	 * Returns the outer transaction that was suspended in favor of this one.<br>
	 * @return The suspended transaction or {@code null} if none was suspended
	 */
	public @Nullable SqlTransaction getSuspended() {
		return this.suspended;
	}
	
	/**
	 * Returns whether this transaction is still active and has neither been committed nor rolled back.<br>
	 * @return True if the transaction is active, false otherwise
	 */
	public boolean isActive() {
		return this.state == SqlTransactionState.ACTIVE;
	}
	
	/**
	 * Returns whether this transaction has been committed.<br>
	 * @return True if the transaction has been committed, false otherwise
	 */
	public boolean isCommitted() {
		return this.state == SqlTransactionState.COMMITTED;
	}
	
	/**
	 * Returns whether this transaction has been rolled back.<br>
	 * @return True if the transaction has been rolled back, false otherwise
	 */
	public boolean isRolledBack() {
		return this.state == SqlTransactionState.ROLLED_BACK;
	}
	
	/**
	 * Registers a listener that is notified after this transaction commits, rolls back or closes.<br>
	 *
	 * @param listener The listener to register
	 * @throws NullPointerException If the listener is null
	 */
	public void addListener(@NonNull SqlTransactionListener listener) {
		this.listeners.add(Objects.requireNonNull(listener, "Sql transaction listener must not be null"));
	}
	
	/**
	 * Commits this transaction and notifies all registered listeners afterwards.<br>
	 * The exact behavior depends on how the transaction was created:<br>
	 * <ul>
	 *     <li>A non-transactional transaction only notifies its listeners.</li>
	 *     <li>A nested transaction releases its savepoint instead of committing the connection.</li>
	 *     <li>A joining transaction marks itself committed without committing the outer connection.</li>
	 *     <li>An owning transaction commits the underlying connection.</li>
	 * </ul>
	 *
	 * @throws SqlTransactionStateException If the transaction is not active
	 * @throws SqlTransactionSavepointException If releasing the nested savepoint fails
	 * @throws SqlTransactionCommitException If committing the underlying connection fails
	 */
	public void commit() throws SqlException {
		if (!this.isActive()) {
			throw new SqlTransactionStateException("Sql transaction is not active");
		}
		if (this.nonTransactional) {
			this.fireAfterCommit();
			return;
		}
		
		if (this.nestedSavepoint != null) {
			try {
				this.connection.releaseSavepoint(this.nestedSavepoint);
				this.state = SqlTransactionState.COMMITTED;
			} catch (SQLException e) {
				throw new SqlTransactionSavepointException("Failed to release nested savepoint", e);
			}
			this.fireAfterCommit();
			return;
		}
		
		if (!this.ownsCommit) {
			this.state = SqlTransactionState.COMMITTED;
			this.fireAfterCommit();
			return;
		}
		
		try {
			this.connection.commit();
			this.state = SqlTransactionState.COMMITTED;
		} catch (SQLException e) {
			throw new SqlTransactionCommitException("Failed to commit transaction", e);
		}
		this.fireAfterCommit();
	}
	
	/**
	 * Rolls back this transaction and notifies all registered listeners afterwards.<br>
	 * The exact behavior depends on how the transaction was created:<br>
	 * <ul>
	 *     <li>A non-transactional transaction only notifies its listeners.</li>
	 *     <li>A nested transaction rolls back to its savepoint instead of the connection.</li>
	 *     <li>An owning transaction rolls back the underlying connection.</li>
	 * </ul>
	 *
	 * @throws SqlTransactionStateException If the transaction is not active or is a joining transaction
	 * @throws SqlTransactionRollbackException If rolling back the connection or savepoint fails
	 */
	public void rollback() throws SqlException {
		if (!this.isActive()) {
			throw new SqlTransactionStateException("Sql transaction is not active");
		}
		if (this.nonTransactional) {
			this.fireAfterRollback();
			return;
		}
		
		if (this.nestedSavepoint != null) {
			try {
				this.connection.rollback(this.nestedSavepoint);
				this.state = SqlTransactionState.ROLLED_BACK;
			} catch (SQLException e) {
				throw new SqlTransactionRollbackException("Failed to rollback to nested savepoint", e);
			}
			this.fireAfterRollback();
			return;
		}
		
		if (!this.ownsCommit) {
			throw new SqlTransactionStateException("Joining transaction must not rollback the outer transaction");
		}
		
		try {
			this.connection.rollback();
			this.state = SqlTransactionState.ROLLED_BACK;
		} catch (SQLException e) {
			throw new SqlTransactionRollbackException("Failed to rollback transaction", e);
		}
		this.fireAfterRollback();
	}
	
	/**
	 * Rolls back this transaction to the given previously created savepoint.<br>
	 * Changes made after the savepoint are discarded while the transaction remains active.<br>
	 *
	 * @param savepoint The savepoint to roll back to
	 * @throws NullPointerException If the savepoint is null
	 * @throws SqlTransactionStateException If the transaction is not active or the savepoint is unknown
	 * @throws SqlTransactionRollbackException If rolling back to the savepoint fails
	 */
	public void rollbackTo(@NonNull SqlSavepoint savepoint) throws SqlException {
		Objects.requireNonNull(savepoint, "Sql savepoint must not be null");
		if (!this.isActive()) {
			throw new SqlTransactionStateException("Sql transaction is not active");
		}
		
		Savepoint jdbcSavepoint = this.savepoints.get(savepoint.name());
		if (jdbcSavepoint == null) {
			throw new SqlTransactionStateException("Sql savepoint '" + savepoint.name() + "' not found");
		}
		
		try {
			this.connection.rollback(jdbcSavepoint);
		} catch (SQLException e) {
			throw new SqlTransactionRollbackException("Failed to rollback to savepoint '" + savepoint.name() + "'", e);
		}
	}
	
	/**
	 * Creates a new savepoint with the given name within this transaction.<br>
	 * The savepoint can later be used with {@link #rollbackTo(SqlSavepoint)} to discard changes made after it.<br>
	 *
	 * @param name The unique name of the savepoint
	 * @return The created savepoint
	 * @throws NullPointerException If the name is null
	 * @throws SqlTransactionStateException If the transaction is not active, a savepoint with the name already exists or the execution is non-transactional
	 * @throws SqlTransactionSavepointException If creating the savepoint fails
	 */
	public @NonNull SqlSavepoint savepoint(@NonNull String name) throws SqlException {
		Objects.requireNonNull(name, "Sql savepoint name must not be null");
		if (!this.isActive()) {
			throw new SqlTransactionStateException("Sql transaction is not active");
		}
		if (this.savepoints.containsKey(name)) {
			throw new SqlTransactionStateException("Sql savepoint with name '" + name + "' already exists");
		}
		if (this.nonTransactional) {
			throw new SqlTransactionStateException("Cannot create savepoint in non-transactional execution");
		}
		
		try {
			Savepoint jdbcSavepoint = this.connection.setSavepoint(name);
			this.savepoints.put(name, jdbcSavepoint);
			return new SqlSavepoint(name);
		} catch (SQLException e) {
			throw new SqlTransactionSavepointException("Failed to create savepoint '" + name + "'", e);
		}
	}
	
	@Override
	public void createSchema(@NonNull String name) throws SqlException {
		Objects.requireNonNull(name, "Sql schema name must not be null");
		
		try (Statement statement = this.connection.createStatement()) {
			statement.execute(this.dialect.schemaRenderer().renderCreateSchema(name, false).sql());
		} catch (SQLException e) {
			throw new SqlException("Failed to create sql schema '" + name + "'", e);
		}
	}
	
	@Override
	public void createSchemaIfNotExists(@NonNull String name) throws SqlException {
		Objects.requireNonNull(name, "Sql schema name must not be null");
		
		try (Statement statement = this.connection.createStatement()) {
			statement.execute(this.dialect.schemaRenderer().renderCreateSchema(name, true).sql());
		} catch (SQLException e) {
			throw new SqlException("Failed to create sql schema '" + name + "'", e);
		}
	}
	
	@Override
	public boolean existsSchema(@NonNull String name) throws SqlException {
		Objects.requireNonNull(name, "Sql schema name must not be null");
		
		try (ResultSet resultSet = this.connection.getMetaData().getSchemas()) {
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
		
		try (Statement statement = this.connection.createStatement()) {
			statement.execute(this.dialect.schemaRenderer().renderDropSchema(name, false, cascade).sql());
		} catch (SQLException e) {
			throw new SqlException("Failed to drop sql schema '" + name + "'", e);
		}
	}
	
	@Override
	public @NonNull <T> SqlTableProvider<T> table(@NonNull SqlTable<T> table) {
		Objects.requireNonNull(table, "Sql table must not be null");
		return new SqlTableProvider<>(table, this.dialect, SqlConnectionSource.fixed(this.connection), this.queryTimeout);
	}
	
	@Override
	public @NonNull <T> SqlQueryProvider<T> from(@NonNull SqlTable<T> table) {
		Objects.requireNonNull(table, "Sql table must not be null");
		return new SqlQueryProvider<>(table, this.dialect, SqlConnectionSource.fixed(this.connection), this.queryTimeout);
	}
	
	@Override
	public void close() throws SqlException {
		try {
			try {
				if (this.isActive() && this.ownsCommit && !this.nonTransactional) {
					this.rollback();
				}
			} finally {
				if (this.nestedSavepoint != null) {
					try {
						this.connection.releaseSavepoint(this.nestedSavepoint);
					} catch (SQLException _) {}
				}
				
				if (this.ownsConnection) {
					try {
						this.connection.setAutoCommit(this.originalAutoCommit);
						this.connection.setReadOnly(this.originalReadOnly);
						this.connection.setTransactionIsolation(this.originalIsolationLevel);
					} finally {
						this.connection.close();
					}
				}
			}
		} catch (SQLException e) {
			throw new SqlTransactionConnectionException("Failed to close transaction", e);
		} finally {
			try {
				this.fireAfterClose();
			} catch (RuntimeException e) {
				throw new SqlClientException("afterClose listener failed", e);
			}
		}
	}
	
	//region Listeners helper methods
	
	/**
	 * Notifies all registered listeners that this transaction has committed.<br>
	 */
	private void fireAfterCommit() {
		for (SqlTransactionListener listener : this.listeners) {
			listener.afterCommit();
		}
	}
	
	/**
	 * Notifies all registered listeners that this transaction has been rolled back.<br>
	 */
	private void fireAfterRollback() {
		for (SqlTransactionListener listener : this.listeners) {
			listener.afterRollback();
		}
	}
	
	/**
	 * Notifies all registered listeners that this transaction has been closed.<br>
	 */
	private void fireAfterClose() {
		for (SqlTransactionListener listener : this.listeners) {
			listener.afterClose();
		}
	}
	//endregion
	
	/**
	 * Represents the lifecycle state of a {@link SqlTransaction}.<br>
	 *
	 * @author Luis-St
	 */
	private enum SqlTransactionState {
		
		/**
		 * The transaction is active and has neither been committed nor rolled back.
		 */
		ACTIVE,
		/**
		 * The transaction has been committed.
		 */
		COMMITTED,
		/**
		 * The transaction has been rolled back.
		 */
		ROLLED_BACK
	}
}
