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
 * Manages the lifecycle of {@link SqlTransaction}s and applies the requested propagation behavior.<br>
 * The manager acquires connections from a {@link DataSource}, configures their read-only and isolation
 * settings and tracks the active transaction for the current thread.<br>
 * <p>
 *     When a transaction begins the configured {@link SqlPropagation} decides whether a new transaction is
 *     started, an existing one is joined, a nested savepoint is opened or the work runs without transactional
 *     semantics.<br>
 *     Transactions that were suspended in favor of a new one are restored automatically once the transaction
 *     that replaced them is closed.
 * </p>
 *
 * @see SqlTransaction
 * @see SqlPropagation
 * @see SqlIsolationLevel
 *
 * @author Luis-St
 */
public class SqlTransactionManager {
	
	/**
	 * The logger of this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(SqlTransactionManager.class);
	/**
	 * The transaction currently bound to the calling thread.
	 */
	private static final ThreadLocal<SqlTransaction> CURRENT_TRANSACTION = new ThreadLocal<>();
	/**
	 * The default timeout used when acquiring a connection while another transaction is suspended.
	 */
	public static final Duration DEFAULT_CONNECTION_ACQUISITION_TIMEOUT = Duration.ofSeconds(10);
	/**
	 * The data source connections are acquired from.
	 */
	private final DataSource dataSource;
	/**
	 * The sql dialect used by created transactions.
	 */
	private final SqlDialect dialect;
	/**
	 * The query timeout applied to created transactions.
	 */
	private final Duration queryTimeout;
	/**
	 * The timeout for acquiring a connection while another transaction is suspended.
	 */
	private final Duration connectionAcquisitionTimeout;
	
	/**
	 * Constructs a new sql transaction manager with the {@link #DEFAULT_CONNECTION_ACQUISITION_TIMEOUT default connection acquisition timeout}.<br>
	 * @param dataSource The data source connections are acquired from
	 * @param dialect The sql dialect used by created transactions
	 * @param queryTimeout The query timeout applied to created transactions
	 * @throws NullPointerException If the data source, dialect or query timeout is null
	 */
	public SqlTransactionManager(@NonNull DataSource dataSource, @NonNull SqlDialect dialect, @NonNull Duration queryTimeout) {
		this(dataSource, dialect, queryTimeout, DEFAULT_CONNECTION_ACQUISITION_TIMEOUT);
	}
	
	/**
	 * Constructs a new sql transaction manager.<br>
	 * @param dataSource The data source connections are acquired from
	 * @param dialect The sql dialect used by created transactions
	 * @param queryTimeout The query timeout applied to created transactions
	 * @param connectionAcquisitionTimeout The timeout for acquiring a connection while another transaction is suspended
	 * @throws NullPointerException If the data source, dialect, query timeout or connection acquisition timeout is null
	 */
	public SqlTransactionManager(@NonNull DataSource dataSource, @NonNull SqlDialect dialect, @NonNull Duration queryTimeout, @NonNull Duration connectionAcquisitionTimeout) {
		this.dataSource = Objects.requireNonNull(dataSource, "Data source must not be null");
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
		this.queryTimeout = Objects.requireNonNull(queryTimeout, "Query timeout must not be null");
		this.connectionAcquisitionTimeout = Objects.requireNonNull(connectionAcquisitionTimeout, "Connection acquisition timeout must not be null");
	}
	
	/**
	 * Returns the shared executor used to acquire connections with a bounded timeout.<br>
	 * @return The connection acquisition executor
	 */
	private static @NonNull ExecutorService acquireExecutor() {
		return AcquireExecutorHolder.INSTANCE;
	}
	
	/**
	 * Begins a transaction applying the given propagation behavior.<br>
	 * Based on the current thread-bound transaction and the propagation a transaction is created that starts a
	 * new transaction, joins the existing one, opens a nested savepoint or runs non-transactionally.<br>
	 * The returned transaction becomes the active transaction for the current thread until it is closed.<br>
	 *
	 * @param readOnly Whether the transaction should be read-only
	 * @param isolationLevel The isolation level to use for the transaction
	 * @param propagation The propagation behavior that decides how the transaction relates to an existing one
	 * @return The transaction that was begun
	 * @throws NullPointerException If the isolation level or propagation is null
	 * @throws SqlTransactionPropagationException If the propagation requirements are not met by the current transaction state
	 * @throws SqlException If the transaction cannot be created
	 */
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
	
	/**
	 * Resolves a transaction for {@link SqlPropagation#REQUIRED} propagation.<br>
	 * Joins the current transaction if one is active, otherwise starts a new transaction.<br>
	 *
	 * @param current The currently active transaction or {@code null} if none
	 * @param readOnly Whether a new transaction should be read-only
	 * @param isolationLevel The isolation level to use for a new transaction
	 * @return The joining or newly created transaction
	 * @throws SqlException If the transaction cannot be created
	 */
	private @NonNull SqlTransaction resolveRequired(@Nullable SqlTransaction current, boolean readOnly, @NonNull SqlIsolationLevel isolationLevel) throws SqlException {
		if (current != null && current.isActive()) {
			if (current.getIsolationLevel() != isolationLevel) {
				LOGGER.warn("Joining existing transaction with isolation level {} but caller requested {}; the existing level will be used", current.getIsolationLevel(), isolationLevel);
			}
			return this.createJoiningTransaction(current);
		}
		return this.createNewTransaction(readOnly, isolationLevel, null);
	}
	
	/**
	 * Resolves a transaction for {@link SqlPropagation#REQUIRES_NEW} propagation.<br>
	 * Always starts a new transaction, suspending the current one if it is active.<br>
	 *
	 * @param current The currently active transaction or {@code null} if none
	 * @param readOnly Whether the new transaction should be read-only
	 * @param isolationLevel The isolation level to use for the new transaction
	 * @return The newly created transaction
	 * @throws SqlException If the transaction cannot be created
	 */
	private @NonNull SqlTransaction resolveRequiresNew(@Nullable SqlTransaction current, boolean readOnly, @NonNull SqlIsolationLevel isolationLevel) throws SqlException {
		return this.createNewTransaction(readOnly, isolationLevel, (current != null && current.isActive()) ? current : null);
	}
	
	/**
	 * Resolves a transaction for {@link SqlPropagation#NESTED} propagation.<br>
	 * Creates a savepoint on the current transaction's connection and returns a nested transaction backed by it.<br>
	 *
	 * @param current The currently active transaction or {@code null} if none
	 * @param readOnly Whether the nested transaction should be read-only
	 * @param isolationLevel The isolation level to use for the nested transaction
	 * @return The nested transaction
	 * @throws SqlTransactionPropagationException If no active transaction exists
	 * @throws SqlTransactionSavepointException If creating the savepoint fails
	 */
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
	
	/**
	 * Resolves a transaction for {@link SqlPropagation#SUPPORTS} propagation.<br>
	 * Joins the current transaction if one is active, otherwise runs non-transactionally.<br>
	 *
	 * @param current The currently active transaction or {@code null} if none
	 * @param readOnly Whether the non-transactional execution should be read-only
	 * @param isolationLevel The isolation level to use
	 * @return The joining or non-transactional transaction
	 * @throws SqlException If the transaction cannot be created
	 */
	private @NonNull SqlTransaction resolveSupports(@Nullable SqlTransaction current, boolean readOnly, @NonNull SqlIsolationLevel isolationLevel) throws SqlException {
		if (current != null && current.isActive()) {
			return this.createJoiningTransaction(current);
		}
		return this.createNonTransactional(readOnly, isolationLevel, null);
	}
	
	/**
	 * Resolves a transaction for {@link SqlPropagation#NOT_SUPPORTED} propagation.<br>
	 * Always runs non-transactionally, suspending the current transaction if it is active.<br>
	 *
	 * @param current The currently active transaction or {@code null} if none
	 * @param readOnly Whether the non-transactional execution should be read-only
	 * @param isolationLevel The isolation level to use
	 * @return The non-transactional transaction
	 * @throws SqlException If the transaction cannot be created
	 */
	private @NonNull SqlTransaction resolveNotSupported(@Nullable SqlTransaction current, boolean readOnly, @NonNull SqlIsolationLevel isolationLevel) throws SqlException {
		return this.createNonTransactional(readOnly, isolationLevel, (current != null && current.isActive()) ? current : null);
	}
	
	/**
	 * Resolves a transaction for {@link SqlPropagation#MANDATORY} propagation.<br>
	 * Joins the current transaction and fails if none is active.<br>
	 *
	 * @param current The currently active transaction or {@code null} if none
	 * @return The joining transaction
	 * @throws SqlTransactionPropagationException If no active transaction exists
	 * @throws SqlException If the transaction cannot be created
	 */
	private @NonNull SqlTransaction resolveMandatory(@Nullable SqlTransaction current) throws SqlException {
		if (current == null || !current.isActive()) {
			throw new SqlTransactionPropagationException("MANDATORY propagation requires an active transaction");
		}
		return this.createJoiningTransaction(current);
	}
	
	/**
	 * Resolves a transaction for {@link SqlPropagation#NEVER} propagation.<br>
	 * Runs non-transactionally and fails if a transaction is currently active.<br>
	 *
	 * @param current The currently active transaction or {@code null} if none
	 * @param readOnly Whether the non-transactional execution should be read-only
	 * @param isolationLevel The isolation level to use
	 * @return The non-transactional transaction
	 * @throws SqlTransactionPropagationException If a transaction is currently active
	 * @throws SqlException If the transaction cannot be created
	 */
	private @NonNull SqlTransaction resolveNever(@Nullable SqlTransaction current, boolean readOnly, @NonNull SqlIsolationLevel isolationLevel) throws SqlException {
		if (current != null && current.isActive()) {
			throw new SqlTransactionPropagationException("NEVER propagation forbids an active transaction");
		}
		return this.createNonTransactional(readOnly, isolationLevel, null);
	}
	
	/**
	 * Creates a new owning transaction with auto-commit disabled on a freshly acquired connection.<br>
	 *
	 * @param readOnly Whether the transaction should be read-only
	 * @param isolationLevel The isolation level to use for the transaction
	 * @param suspended The transaction to suspend for the duration of this one, or {@code null} if none
	 * @return The newly created transaction
	 * @throws SqlTransactionConnectionException If acquiring or configuring the connection fails
	 * @throws SqlException If the transaction cannot be created
	 */
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
	
	/**
	 * Creates a transaction that joins the given active transaction and shares its connection.<br>
	 * The joining transaction neither owns the connection nor commits or rolls it back.<br>
	 *
	 * @param current The active transaction to join
	 * @return The joining transaction
	 * @throws SqlException If the transaction cannot be created
	 */
	private @NonNull SqlTransaction createJoiningTransaction(@NonNull SqlTransaction current) throws SqlException {
		return new SqlTransaction(current.getConnection(), this.dialect, current.isReadOnly(), this.queryTimeout, current.getIsolationLevel(), false, false, false, null);
	}
	
	/**
	 * Creates a non-transactional execution with auto-commit enabled on a freshly acquired connection.<br>
	 *
	 * @param readOnly Whether the execution should be read-only
	 * @param isolationLevel The isolation level to use
	 * @param suspended The transaction to suspend for the duration of this one, or {@code null} if none
	 * @return The non-transactional transaction
	 * @throws SqlTransactionConnectionException If acquiring or configuring the connection fails
	 * @throws SqlException If the transaction cannot be created
	 */
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
	
	/**
	 * Acquires a connection from the data source and applies the read-only and isolation settings.<br>
	 * A bounded acquisition with a timeout is used when another transaction is suspended and still holds a connection.<br>
	 *
	 * @param readOnly Whether the connection should be read-only
	 * @param isolationLevel The isolation level to apply to the connection
	 * @param bounded Whether the connection must be acquired with the acquisition timeout
	 * @return The acquired and configured connection
	 * @throws NullPointerException If the isolation level is null
	 * @throws SqlTransactionConnectionException If acquiring or configuring the connection fails
	 */
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
	
	/**
	 * Acquires a connection directly from the data source without a timeout.<br>
	 * @return The acquired connection
	 * @throws SqlTransactionConnectionException If acquiring the connection fails
	 */
	private @NonNull Connection acquireConnectionDirect() throws SqlException {
		try {
			return this.dataSource.getConnection();
		} catch (SQLException e) {
			throw new SqlTransactionConnectionException("Failed to acquire connection from data source", e);
		}
	}
	
	/**
	 * Acquires a connection from the data source bounded by the {@link #connectionAcquisitionTimeout connection acquisition timeout}.<br>
	 * If the timeout elapses or the thread is interrupted any connection that arrives late is released to avoid leaking it.<br>
	 *
	 * @return The acquired connection
	 * @throws SqlTransactionConnectionException If the acquisition times out, is interrupted or otherwise fails
	 */
	private @NonNull Connection acquireConnectionBounded() throws SqlException {
		CompletableFuture<Connection> future = CompletableFuture.supplyAsync(() -> {
			try {
				return this.dataSource.getConnection();
			} catch (SQLException e) {
				throw new CompletionException(e);
			}
		}, acquireExecutor());
		
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
	
	/**
	 * Releases a connection that completes after its bounded acquisition has already timed out or been interrupted.<br>
	 * @param future The pending connection acquisition whose result is released once it completes
	 */
	private void releaseLateConnection(@NonNull CompletableFuture<Connection> future) {
		future.whenComplete((connection, throwable) -> {
			if (connection != null) {
				this.closeConnectionQuietly(connection);
			}
		});
	}
	
	/**
	 * Closes the given connection ignoring any error that occurs while closing.<br>
	 * @param connection The connection to close
	 */
	private void closeConnectionQuietly(@NonNull Connection connection) {
		try {
			connection.close();
		} catch (SQLException _) {}
	}
	
	/**
	 * Restores the thread-bound transaction after the given transaction has been closed.<br>
	 * The transaction it suspended becomes active again, or the thread is left without an active transaction.<br>
	 *
	 * @param transaction The transaction that was closed
	 */
	private void restore(@NonNull SqlTransaction transaction) {
		SqlTransaction suspended = transaction.getSuspended();
		
		if (suspended != null) {
			CURRENT_TRANSACTION.set(suspended);
		} else {
			CURRENT_TRANSACTION.remove();
		}
	}
	
	/**
	 * Holder that lazily initializes the shared executor used for bounded connection acquisition.<br>
	 *
	 * @author Luis-St
	 */
	private static final class AcquireExecutorHolder {
		
		/**
		 * The shared connection acquisition executor.
		 */
		private static final ExecutorService INSTANCE = create();
		
		/**
		 * Creates the shared connection acquisition executor backed by daemon threads.<br>
		 * A shutdown hook is registered to stop the executor when the runtime shuts down.<br>
		 *
		 * @return The created executor service
		 */
		private static @NonNull ExecutorService create() {
			ExecutorService executor = Executors.newCachedThreadPool(runnable -> {
				Thread thread = new Thread(runnable, "sql-tx-connection-acquire");
				thread.setDaemon(true);
				return thread;
			});
			
			Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdownNow, "sql-tx-connection-acquire-shutdown"));
			return executor;
		}
	}
}
