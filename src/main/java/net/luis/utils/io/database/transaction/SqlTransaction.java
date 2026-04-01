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
import net.luis.utils.io.database.exception.SqlTransactionException;
import net.luis.utils.io.database.query.SqlQueryProvider;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.table.SqlTableProvider;
import net.luis.utils.io.databasev1.transaction.SqlPropagation;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class SqlTransaction implements SqlProvider, AutoCloseable {
	
	private final boolean readOnly;
	private final Duration timeout;
	private final SqlIsolationLevel isolationLevel;
	private final SqlPropagation propagationBehavior;
	private SqlTransactionState state = SqlTransactionState.ACTIVE;
	
	public SqlTransaction() {
		this(false, Duration.ofSeconds(30), SqlIsolationLevel.READ_COMMITTED, SqlPropagation.REQUIRED);
	}
	
	public SqlTransaction(boolean readOnly, @NonNull Duration timeout, @NonNull SqlIsolationLevel isolationLevel, @NonNull SqlPropagation propagationBehavior) {
		this.readOnly = readOnly;
		this.timeout = Objects.requireNonNull(timeout, "Timeout must not be null");
		this.isolationLevel = Objects.requireNonNull(isolationLevel, "Isolation level must not be null");
		this.propagationBehavior = Objects.requireNonNull(propagationBehavior, "Propagation behavior must not be null");
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
	
	public @NonNull SqlPropagation getPropagationBehavior() {
		return this.propagationBehavior;
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
	
	}
	
	public void rollback() throws SqlTransactionException {
	
	}
	
	public void rollbackTo(@NonNull SqlSavepoint savepoint) throws SqlTransactionException {
	
	}
	
	public @NonNull SqlSavepoint savepoint(@NonNull String name) throws SqlTransactionException {
		return null;
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
	
	}
	
	private enum SqlTransactionState {
		
		ACTIVE,
		COMMITTED,
		ROLLED_BACK
	}
}
