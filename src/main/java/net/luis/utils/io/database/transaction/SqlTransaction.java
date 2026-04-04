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

import net.luis.utils.io.database.SqlProvider;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.transaction.*;
import net.luis.utils.io.database.query.SqlQueryProvider;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.table.SqlTableProvider;
import org.jetbrains.annotations.NotNull;
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

public class SqlTransaction implements SqlProvider, AutoCloseable {
	
	private final Connection connection;
	private final boolean readOnly;
	private final Duration timeout;
	private final SqlIsolationLevel isolationLevel;
	private final boolean ownsConnection;
	private final boolean ownsCommit;
	private final boolean nonTransactional;
	private final @Nullable SqlTransaction suspended;
	private final Map<String, Savepoint> savepoints = new LinkedHashMap<>();
	private @Nullable Savepoint nestedSavepoint;
	private @Nullable Runnable onClose;
	private SqlTransactionState state = SqlTransactionState.ACTIVE;
	
	public SqlTransaction(
		@NonNull Connection connection,
		boolean readOnly,
		@NonNull Duration timeout,
		@NonNull SqlIsolationLevel isolationLevel,
		boolean ownsConnection,
		boolean ownsCommit,
		boolean nonTransactional,
		@Nullable SqlTransaction suspended
	) {
		this.connection = Objects.requireNonNull(connection, "Connection must not be null");
		this.readOnly = readOnly;
		this.timeout = Objects.requireNonNull(timeout, "Timeout must not be null");
		this.isolationLevel = Objects.requireNonNull(isolationLevel, "Isolation level must not be null");
		this.ownsConnection = ownsConnection;
		this.ownsCommit = ownsCommit;
		this.nonTransactional = nonTransactional;
		this.suspended = suspended;
	}
	
	void setNestedSavepoint(@NonNull Savepoint nestedSavepoint) {
		this.nestedSavepoint = Objects.requireNonNull(nestedSavepoint, "Nested savepoint must not be null");
	}
	
	public void setOnClose(@Nullable Runnable onClose) {
		this.onClose = onClose;
	}
	
	public @NonNull Connection getConnection() {
		return this.connection;
	}
	
	public boolean isReadOnly() {
		return this.readOnly;
	}
	
	public @NonNull Duration getTimeout() {
		return this.timeout;
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
	
	public void commit() throws SqlTransactionException {
		if (!this.isActive()) {
			throw new SqlTransactionException("Transaction is not active");
		}
		if (this.nonTransactional) {
			return;
		}
		
		if (this.nestedSavepoint != null) {
			try {
				this.connection.releaseSavepoint(this.nestedSavepoint);
			} catch (SQLException e) {
				throw new SqlTransactionSavepointException("Failed to release nested savepoint", e);
			}
			return;
		}
		
		if (!this.ownsCommit) {
			return;
		}
		
		try {
			this.connection.commit();
			this.state = SqlTransactionState.COMMITTED;
		} catch (SQLException e) {
			throw new SqlTransactionCommitException("Failed to commit transaction", e);
		}
	}
	
	public void rollback() throws SqlTransactionException {
		if (!this.isActive()) {
			throw new SqlTransactionException("Transaction is not active");
		}
		if (this.nonTransactional) {
			return;
		}
		
		if (this.nestedSavepoint != null) {
			try {
				this.connection.rollback(this.nestedSavepoint);
				this.state = SqlTransactionState.ROLLED_BACK;
			} catch (SQLException e) {
				throw new SqlTransactionRollbackException("Failed to rollback to nested savepoint", e);
			}
			return;
		}
		
		if (!this.ownsCommit) {
			throw new SqlTransactionException("Joining transaction must not rollback the outer transaction");
		}
		
		try {
			this.connection.rollback();
			this.state = SqlTransactionState.ROLLED_BACK;
		} catch (SQLException e) {
			throw new SqlTransactionRollbackException("Failed to rollback transaction", e);
		}
	}
	
	public void rollbackTo(@NonNull SqlSavepoint savepoint) throws SqlTransactionException {
		Objects.requireNonNull(savepoint, "Savepoint must not be null");
		if (!this.isActive()) {
			throw new SqlTransactionException("Transaction is not active");
		}
		
		Savepoint jdbcSavepoint = this.savepoints.get(savepoint.name());
		if (jdbcSavepoint == null) {
			throw new SqlTransactionSavepointException("Savepoint '" + savepoint.name() + "' not found");
		}
		
		try {
			this.connection.rollback(jdbcSavepoint);
		} catch (SQLException e) {
			throw new SqlTransactionRollbackException("Failed to rollback to savepoint '" + savepoint.name() + "'", e);
		}
	}
	
	public @NonNull SqlSavepoint savepoint(@NonNull String name) throws SqlTransactionException {
		Objects.requireNonNull(name, "Savepoint name must not be null");
		if (!this.isActive()) {
			throw new SqlTransactionException("Transaction is not active");
		}
		if (this.savepoints.containsKey(name)) {
			throw new SqlTransactionSavepointException("Savepoint with name '" + name + "' already exists");
		}
		if (this.nonTransactional) {
			throw new SqlTransactionSavepointException("Cannot create savepoint in non-transactional execution");
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
	public void createSchema(@NotNull String name) throws SqlException {
	
	}
	
	@Override
	public void createSchemaIfNotExists(@NotNull String name) throws SqlException {
	
	}
	
	@Override
	public boolean existsSchema(@NotNull String name) throws SqlException {
		return false;
	}
	
	@Override
	public void dropSchema(@NotNull String name, boolean cascade) throws SqlException {
	
	}
	
	@Override
	public @NonNull <T> SqlTableProvider<T> table(@NonNull SqlTable<T> table) {
		return null;
	}
	
	@Override
	public @NonNull <T> SqlQueryProvider<T> from(@NonNull SqlTable<T> table) {
		return null;
	}
	
	@Override
	public void close() throws SqlTransactionException {
		try {
			if (this.isActive() && this.ownsCommit && !this.nonTransactional) {
				this.rollback();
			}
			
			if (this.nestedSavepoint != null) {
				try {
					this.connection.releaseSavepoint(this.nestedSavepoint);
				} catch (SQLException _) {}
			}
			
			if (this.ownsConnection) {
				this.connection.close();
			}
		} catch (SQLException e) {
			throw new SqlTransactionException("Failed to close transaction", e);
		} finally {
			if (this.onClose != null) {
				this.onClose.run();
			}
		}
	}
	
	private enum SqlTransactionState {
		
		ACTIVE,
		COMMITTED,
		ROLLED_BACK
	}
}
