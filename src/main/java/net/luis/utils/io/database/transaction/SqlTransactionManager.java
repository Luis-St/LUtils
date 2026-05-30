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

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.transaction.SqlTransactionPropagationException;
import net.luis.utils.io.database.exception.database.transaction.SqlTransactionConnectionException;
import net.luis.utils.io.database.exception.database.transaction.SqlTransactionSavepointException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.*;

/**
 *
 * @author Luis-St
 *
 */

public class SqlTransactionManager {
	
	private static final Logger LOGGER = LogManager.getLogger(SqlTransactionManager.class);
	private static final ThreadLocal<SqlTransaction> CURRENT_TRANSACTION = new ThreadLocal<>();
	public static final Duration DEFAULT_CONNECTION_ACQUISITION_TIMEOUT = Duration.ofSeconds(10);
	private static final ExecutorService ACQUIRE_EXECUTOR = Executors.newCachedThreadPool(runnable -> {
		Thread thread = new Thread(runnable, "sql-tx-connection-acquire");
		thread.setDaemon(true);
		return thread;
	});
	private final DataSource dataSource;
	private final SqlDialect dialect;
	private final Duration queryTimeout;
	private final Duration connectionAcquisitionTimeout;
	
	public SqlTransactionManager(@NonNull DataSource dataSource, @NonNull SqlDialect dialect, @NonNull Duration queryTimeout) {
		this(dataSource, dialect, queryTimeout, DEFAULT_CONNECTION_ACQUISITION_TIMEOUT);
	}
	
	public SqlTransactionManager(@NonNull DataSource dataSource, @NonNull SqlDialect dialect, @NonNull Duration queryTimeout, @NonNull Duration connectionAcquisitionTimeout) {
		this.dataSource = Objects.requireNonNull(dataSource, "Data source must not be null");
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
		this.queryTimeout = Objects.requireNonNull(queryTimeout, "Query timeout must not be null");
		this.connectionAcquisitionTimeout = Objects.requireNonNull(connectionAcquisitionTimeout, "Connection acquisition timeout must not be null");
	}
	
	public @NonNull SqlTransaction begin(boolean readOnly, @NonNull SqlIsolationLevel isolationLevel, @NonNull SqlPropagation propagation) throws SqlException {
		SqlTransaction current = CURRENT_TRANSACTION.get();
		
		SqlTransaction tx = switch (propagation) {
			case REQUIRED -> this.resolveRequired(current, readOnly, isolationLevel);
			case REQUIRES_NEW -> this.resolveRequiresNew(current, readOnly, isolationLevel);
			case NESTED -> this.resolveNested(current, readOnly, isolationLevel);
			case SUPPORTS -> this.resolveSupports(current, readOnly, isolationLevel);
			case NOT_SUPPORTED -> this.resolveNotSupported(current, readOnly, isolationLevel);
			case MANDATORY -> this.resolveMandatory(current);
			case NEVER -> this.resolveNever(current, readOnly, isolationLevel);
		};
		
		tx.addListener(new SqlTransactionListener() {
			
			@Override
			public void afterClose() {
				SqlTransactionManager.this.restore(tx);
			}
		});
		CURRENT_TRANSACTION.set(tx);
		return tx;
	}
	
	private @NonNull SqlTransaction resolveRequired(@Nullable SqlTransaction current, boolean readOnly, @NonNull SqlIsolationLevel isolationLevel) throws SqlException {
		if (current != null && current.isActive()) {
			if (current.getIsolationLevel() != isolationLevel) {
				LOGGER.warn("Joining existing transaction with isolation level {} but caller requested {}; the existing level will be used", current.getIsolationLevel(), isolationLevel);
			}
			return this.createJoiningTransaction(current);
		}
		return this.createNewTransaction(readOnly, isolationLevel, null);
	}
	
	private @NonNull SqlTransaction resolveRequiresNew(@Nullable SqlTransaction current, boolean readOnly, @NonNull SqlIsolationLevel isolationLevel) throws SqlException {
		return this.createNewTransaction(readOnly, isolationLevel, (current != null && current.isActive()) ? current : null);
	}
	
	private @NonNull SqlTransaction resolveNested(@Nullable SqlTransaction current, boolean readOnly, @NonNull SqlIsolationLevel isolationLevel) throws SqlException {
		if (current == null || !current.isActive()) {
			throw new SqlTransactionPropagationException("NESTED propagation requires an active transaction");
		}
		
		try {
			Savepoint savepoint = current.getConnection().setSavepoint();
			SqlTransaction tx = new SqlTransaction(current.getConnection(), this.dialect, readOnly, this.queryTimeout, isolationLevel, false, false, false, current);
			tx.setNestedSavepoint(savepoint);
			return tx;
		} catch (SQLException e) {
			throw new SqlTransactionSavepointException("Failed to create savepoint for nested transaction", e);
		}
	}
	
	private @NonNull SqlTransaction resolveSupports(@Nullable SqlTransaction current, boolean readOnly, @NonNull SqlIsolationLevel isolationLevel) throws SqlException {
		if (current != null && current.isActive()) {
			return this.createJoiningTransaction(current);
		}
		return this.createNonTransactional(readOnly, isolationLevel, null);
	}
	
	private @NonNull SqlTransaction resolveNotSupported(@Nullable SqlTransaction current, boolean readOnly, @NonNull SqlIsolationLevel isolationLevel) throws SqlException {
		return this.createNonTransactional(readOnly, isolationLevel, (current != null && current.isActive()) ? current : null);
	}
	
	private @NonNull SqlTransaction resolveMandatory(@Nullable SqlTransaction current) throws SqlException {
		if (current == null || !current.isActive()) {
			throw new SqlTransactionPropagationException("MANDATORY propagation requires an active transaction");
		}
		return this.createJoiningTransaction(current);
	}
	
	private @NonNull SqlTransaction resolveNever(@Nullable SqlTransaction current, boolean readOnly, @NonNull SqlIsolationLevel isolationLevel) throws SqlException {
		if (current != null && current.isActive()) {
			throw new SqlTransactionPropagationException("NEVER propagation forbids an active transaction");
		}
		return this.createNonTransactional(readOnly, isolationLevel, null);
	}
	
	private @NonNull SqlTransaction createNewTransaction(boolean readOnly, @NonNull SqlIsolationLevel isolationLevel, @Nullable SqlTransaction suspended) throws SqlException {
		Connection connection = this.acquireConnection(readOnly, isolationLevel, suspended != null);
		
		try {
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			this.closeConnectionQuietly(connection);
			throw new SqlTransactionConnectionException("Failed to disable auto-commit", e);
		}
		return new SqlTransaction(connection, this.dialect, readOnly, this.queryTimeout, isolationLevel, true, true, false, suspended);
	}
	
	private @NonNull SqlTransaction createJoiningTransaction(@NonNull SqlTransaction current) {
		return new SqlTransaction(current.getConnection(), this.dialect, current.isReadOnly(), this.queryTimeout, current.getIsolationLevel(), false, false, false, null);
	}
	
	private @NonNull SqlTransaction createNonTransactional(boolean readOnly, @NonNull SqlIsolationLevel isolationLevel, @Nullable SqlTransaction suspended) throws SqlException {
		Connection connection = this.acquireConnection(readOnly, isolationLevel, suspended != null);
		
		try {
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			this.closeConnectionQuietly(connection);
			throw new SqlTransactionConnectionException("Failed to enable auto-commit", e);
		}
		return new SqlTransaction(connection, this.dialect, readOnly, this.queryTimeout, isolationLevel, true, false, true, suspended);
	}
	
	@SuppressWarnings("MagicConstant")
	private @NonNull Connection acquireConnection(boolean readOnly, @NonNull SqlIsolationLevel isolationLevel, boolean bounded) throws SqlException {
		Objects.requireNonNull(isolationLevel, "Transaction isolation level must not be null");
		
		Connection connection = bounded ? this.acquireConnectionBounded() : this.acquireConnectionDirect();
		try {
			connection.setReadOnly(readOnly);
			connection.setTransactionIsolation(isolationLevel.jdbcLevel());
			return connection;
		} catch (SQLException e) {
			this.closeConnectionQuietly(connection);
			throw new SqlTransactionConnectionException("Failed to configure acquired connection", e);
		}
	}
	
	private @NonNull Connection acquireConnectionDirect() throws SqlException {
		try {
			return this.dataSource.getConnection();
		} catch (SQLException e) {
			throw new SqlTransactionConnectionException("Failed to acquire connection from data source", e);
		}
	}
	
	private @NonNull Connection acquireConnectionBounded() throws SqlException {
		CompletableFuture<Connection> future = CompletableFuture.supplyAsync(() -> {
			try {
				return this.dataSource.getConnection();
			} catch (SQLException e) {
				throw new CompletionException(e);
			}
		}, ACQUIRE_EXECUTOR);
		
		try {
			return future.get(this.connectionAcquisitionTimeout.toMillis(), TimeUnit.MILLISECONDS);
		} catch (TimeoutException e) {
			this.releaseLateConnection(future);
			throw new SqlTransactionConnectionException(
				"Timed out after " + this.connectionAcquisitionTimeout + " acquiring a connection while a suspended transaction still holds one (REQUIRES_NEW/NOT_SUPPORTED), " +
					"the connection pool is likely too small for the transaction nesting depth", new SQLException(e));
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if (cause instanceof SQLException sqlException) {
				throw new SqlTransactionConnectionException("Failed to acquire connection from data source", sqlException);
			}
			throw new SqlTransactionConnectionException("Failed to acquire connection from data source", new SQLException(cause));
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			this.releaseLateConnection(future);
			throw new SqlTransactionConnectionException("Interrupted while acquiring a connection from data source", new SQLException(e));
		}
	}
	
	private void releaseLateConnection(@NonNull CompletableFuture<Connection> future) {
		future.whenComplete((connection, throwable) -> {
			if (connection != null) {
				this.closeConnectionQuietly(connection);
			}
		});
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
