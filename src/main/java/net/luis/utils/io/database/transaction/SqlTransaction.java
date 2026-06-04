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
 *
 * @author Luis-St
 *
 */

// Not thread-safe - instances must be confined to a single thread
@SuppressWarnings("SqlSourceToSinkFlow")
public class SqlTransaction implements SqlProvider, AutoCloseable {
	
	private final Connection connection;
	private final SqlDialect dialect;
	private final boolean readOnly;
	private final Duration queryTimeout;
	private final SqlIsolationLevel isolationLevel;
	private final boolean ownsConnection;
	private final boolean ownsCommit;
	private final boolean nonTransactional;
	private final @Nullable SqlTransaction suspended;
	private final boolean originalAutoCommit;
	private final boolean originalReadOnly;
	private final int originalIsolationLevel;
	private final Map<String, Savepoint> savepoints = Maps.newLinkedHashMap();
	private final List<SqlTransactionListener> listeners = Lists.newArrayList();
	private @Nullable Savepoint nestedSavepoint;
	private SqlTransactionState state = SqlTransactionState.ACTIVE;
	
	public SqlTransaction(
		@NonNull Connection connection,
		@NonNull SqlDialect dialect,
		boolean readOnly,
		@NonNull Duration queryTimeout,
		@NonNull SqlIsolationLevel isolationLevel,
		boolean ownsConnection,
		boolean ownsCommit,
		boolean nonTransactional,
		@Nullable SqlTransaction suspended
	) throws SqlTransactionConnectionException {
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
	
	void setNestedSavepoint(@NonNull Savepoint nestedSavepoint) {
		this.nestedSavepoint = Objects.requireNonNull(nestedSavepoint, "Nested savepoint must not be null");
	}
	
	public @NonNull Connection getConnection() {
		return this.connection;
	}
	
	public @NonNull SqlDialect getDialect() {
		return this.dialect;
	}
	
	public boolean isReadOnly() {
		return this.readOnly;
	}
	
	public @NonNull Duration getQueryTimeout() {
		return this.queryTimeout;
	}
	
	public @NonNull SqlIsolationLevel getIsolationLevel() {
		return this.isolationLevel;
	}
	
	public @Nullable SqlTransaction getSuspended() {
		return this.suspended;
	}
	
	public boolean isActive() {
		return this.state == SqlTransactionState.ACTIVE;
	}
	
	public boolean isCommitted() {
		return this.state == SqlTransactionState.COMMITTED;
	}
	
	public boolean isRolledBack() {
		return this.state == SqlTransactionState.ROLLED_BACK;
	}
	
	public void addListener(@NonNull SqlTransactionListener listener) {
		this.listeners.add(Objects.requireNonNull(listener, "Sql transaction listener must not be null"));
	}
	
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
	
	private void fireAfterCommit() {
		for (SqlTransactionListener listener : this.listeners) {
			listener.afterCommit();
		}
	}
	
	private void fireAfterRollback() {
		for (SqlTransactionListener listener : this.listeners) {
			listener.afterRollback();
		}
	}
	
	private void fireAfterClose() {
		for (SqlTransactionListener listener : this.listeners) {
			listener.afterClose();
		}
	}
	//endregion
	
	private enum SqlTransactionState {
		
		ACTIVE,
		COMMITTED,
		ROLLED_BACK
	}
}
