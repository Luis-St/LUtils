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

import net.luis.utils.io.database.exception.transaction.SqlTransactionException;
import net.luis.utils.io.database.exception.transaction.SqlTransactionPropagationException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Duration;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class SqlTransactionManager {
	
	private static final ThreadLocal<SqlTransaction> CURRENT_TRANSACTION = new ThreadLocal<>();
	private final DataSource dataSource;
	
	public SqlTransactionManager(@NonNull DataSource dataSource) {
		this.dataSource = Objects.requireNonNull(dataSource, "Data source must not be null");
	}
	
	public @NonNull SqlTransaction begin(boolean readOnly, @NonNull Duration timeout, @NonNull SqlIsolationLevel isolationLevel, @NonNull SqlPropagation propagation) throws SqlTransactionException {
		SqlTransaction current = CURRENT_TRANSACTION.get();
		
		SqlTransaction tx = switch (propagation) {
			case REQUIRED -> this.resolveRequired(current, readOnly, timeout, isolationLevel);
			case REQUIRES_NEW -> this.resolveRequiresNew(current, readOnly, timeout, isolationLevel);
			case NESTED -> this.resolveNested(current, readOnly, timeout, isolationLevel);
			case SUPPORTS -> this.resolveSupports(current, readOnly, timeout, isolationLevel);
			case NOT_SUPPORTED -> this.resolveNotSupported(current, readOnly, timeout, isolationLevel);
			case MANDATORY -> this.resolveMandatory(current);
			case NEVER -> this.resolveNever(current, readOnly, timeout, isolationLevel);
		};
		
		tx.setOnClose(() -> this.restore(tx));
		CURRENT_TRANSACTION.set(tx);
		return tx;
	}
	
	private @NonNull SqlTransaction resolveRequired(@Nullable SqlTransaction current, boolean readOnly, @NonNull Duration timeout, @NonNull SqlIsolationLevel isolationLevel) throws SqlTransactionException {
		if (current != null && current.isActive()) {
			return this.createJoiningTransaction(current);
		}
		return this.createNewTransaction(readOnly, timeout, isolationLevel, null);
	}
	
	private @NonNull SqlTransaction resolveRequiresNew(@Nullable SqlTransaction current, boolean readOnly, @NonNull Duration timeout, @NonNull SqlIsolationLevel isolationLevel) throws SqlTransactionException {
		return this.createNewTransaction(readOnly, timeout, isolationLevel, (current != null && current.isActive()) ? current : null);
	}
	
	private @NonNull SqlTransaction resolveNested(@Nullable SqlTransaction current, boolean readOnly, @NonNull Duration timeout, @NonNull SqlIsolationLevel isolationLevel) throws SqlTransactionException {
		if (current == null || !current.isActive()) {
			throw new SqlTransactionPropagationException("NESTED propagation requires an active transaction");
		}
		
		try {
			Savepoint savepoint = current.getConnection().setSavepoint();
			SqlTransaction tx = new SqlTransaction(current.getConnection(), readOnly, timeout, isolationLevel, false, false, false, null);
			tx.setNestedSavepoint(savepoint);
			return tx;
		} catch (SQLException e) {
			throw new SqlTransactionException("Failed to create savepoint for nested transaction", e);
		}
	}
	
	private @NonNull SqlTransaction resolveSupports(@Nullable SqlTransaction current, boolean readOnly, @NonNull Duration timeout, @NonNull SqlIsolationLevel isolationLevel) throws SqlTransactionException {
		if (current != null && current.isActive()) {
			return this.createJoiningTransaction(current);
		}
		return this.createNonTransactional(readOnly, timeout, isolationLevel, null);
	}
	
	private @NonNull SqlTransaction resolveNotSupported(@Nullable SqlTransaction current, boolean readOnly, @NonNull Duration timeout, @NonNull SqlIsolationLevel isolationLevel) throws SqlTransactionException {
		return this.createNonTransactional(readOnly, timeout, isolationLevel, (current != null && current.isActive()) ? current : null);
	}
	
	private @NonNull SqlTransaction resolveMandatory(@Nullable SqlTransaction current) throws SqlTransactionException {
		if (current == null || !current.isActive()) {
			throw new SqlTransactionPropagationException("MANDATORY propagation requires an active transaction");
		}
		return this.createJoiningTransaction(current);
	}
	
	private @NonNull SqlTransaction resolveNever(@Nullable SqlTransaction current, boolean readOnly, @NonNull Duration timeout, @NonNull SqlIsolationLevel isolationLevel) throws SqlTransactionException {
		if (current != null && current.isActive()) {
			throw new SqlTransactionPropagationException("NEVER propagation forbids an active transaction");
		}
		return this.createNonTransactional(readOnly, timeout, isolationLevel, null);
	}
	
	private @NonNull SqlTransaction createNewTransaction(boolean readOnly, @NonNull Duration timeout, @NonNull SqlIsolationLevel isolationLevel, @Nullable SqlTransaction suspended) throws SqlTransactionException {
		Connection connection = this.acquireConnection(readOnly, isolationLevel);
		
		try {
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			this.closeConnectionQuietly(connection);
			throw new SqlTransactionException("Failed to disable auto-commit", e);
		}
		return new SqlTransaction(connection, readOnly, timeout, isolationLevel, true, true, false, suspended);
	}
	
	private @NonNull SqlTransaction createJoiningTransaction(@NonNull SqlTransaction current) {
		return new SqlTransaction(current.getConnection(), current.isReadOnly(), current.getTimeout(), current.getIsolationLevel(), false, false, false, null);
	}
	
	private @NonNull SqlTransaction createNonTransactional(boolean readOnly, @NonNull Duration timeout, @NonNull SqlIsolationLevel isolationLevel, @Nullable SqlTransaction suspended) throws SqlTransactionException {
		Connection connection = this.acquireConnection(readOnly, isolationLevel);
		
		try {
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			this.closeConnectionQuietly(connection);
			throw new SqlTransactionException("Failed to enable auto-commit", e);
		}
		return new SqlTransaction(connection, readOnly, timeout, isolationLevel, true, false, true, suspended);
	}
	
	@SuppressWarnings("MagicConstant")
	private @NonNull Connection acquireConnection(boolean readOnly, @NonNull SqlIsolationLevel isolationLevel) throws SqlTransactionException {
		Objects.requireNonNull(isolationLevel, "Transaction isolation level must not be null");
		
		try {
			Connection connection = this.dataSource.getConnection();
			connection.setReadOnly(readOnly);
			connection.setTransactionIsolation(isolationLevel.jdbcLevel());
			return connection;
		} catch (SQLException e) {
			throw new SqlTransactionException("Failed to acquire connection from data source", e);
		}
	}
	
	private void closeConnectionQuietly(@NonNull Connection connection) {
		try {
			connection.close();
		} catch (SQLException _) {}
	}
	
	private void restore(@NonNull SqlTransaction transaction) {
		SqlTransaction suspended = transaction.getSuspended();
		
		if (suspended != null) {
			CURRENT_TRANSACTION.set(suspended);
		} else {
			CURRENT_TRANSACTION.remove();
		}
	}
}
