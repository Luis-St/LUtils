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

package net.luis.utils.io.database.query.crud;

import com.google.common.collect.Lists;
import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.io.database.SqlConnectionHandle;
import net.luis.utils.io.database.SqlConnectionSource;
import net.luis.utils.io.database.audit.SqlAuditUserProvider;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.SqlFeature;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectFeatureException;
import net.luis.utils.io.database.exception.database.SqlQueryExecutionException;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.util.Pair;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.*;
import java.time.Duration;
import java.util.*;

/**
 * Internal helper that executes rendered sql statements against a {@link SqlConnectionSource}.<br>
 * It centralizes the low-level jdbc plumbing shared by the crud query types, such as preparing
 * statements, binding parameters, managing transactions and mapping result sets.<br>
 * All methods are static and translate jdbc {@link SQLException}s into the library's
 * {@link SqlException} hierarchy.<br>
 *
 * @author Luis-St
 */
@SuppressWarnings("SqlSourceToSinkFlow")
final class SqlQueryExecutor {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a utility class that should not be instantiated.<br>
	 */
	private SqlQueryExecutor() {}
	
	/**
	 * Resolves the current user name from the given audit user provider.<br>
	 *
	 * @param userProvider The audit user provider to query, may be null
	 * @return The resolved user name or {@code null} if the provider is null or supplies no user
	 */
	static @Nullable String resolveUser(@Nullable SqlAuditUserProvider userProvider) {
		if (userProvider == null) {
			return null;
		}
		
		Optional<String> user = userProvider.get();
		return user.isEmpty() ? null : user.orElse(null);
	}
	
	/**
	 * Prepares a statement for the given rendered sql without requesting generated keys.<br>
	 * This is a convenience overload that delegates to {@link #prepare(SqlDialect, Connection, SqlRendered, Duration, boolean)} with {@code returnGeneratedKeys} set to {@code false}.<br>
	 *
	 * @param dialect The sql dialect used to bind the parameters
	 * @param connection The connection to prepare the statement on
	 * @param rendered The rendered sql together with its parameters
	 * @param timeout The query timeout to apply to the statement
	 * @return The prepared statement with all parameters bound
	 * @throws NullPointerException If any of the arguments is null
	 * @throws SqlException If the statement could not be prepared
	 */
	static @NonNull PreparedStatement prepare(@NonNull SqlDialect dialect, @NonNull Connection connection, @NonNull SqlRendered rendered, @NonNull Duration timeout) throws SqlException {
		return prepare(dialect, connection, rendered, timeout, false);
	}
	
	/**
	 * Prepares a statement for the given rendered sql and binds all of its parameters.<br>
	 * <p>
	 *     The parameters of the rendered sql are bound positionally using the given dialect.<br>
	 *     The query timeout is applied in whole seconds, rounding up any non-zero sub-second
	 *     duration to one second and clamping the value to {@link Integer#MAX_VALUE}.
	 * </p>
	 *
	 * @param dialect The sql dialect used to bind the parameters
	 * @param connection The connection to prepare the statement on
	 * @param rendered The rendered sql together with its parameters
	 * @param timeout The query timeout to apply to the statement
	 * @param returnGeneratedKeys Whether the statement should make generated keys retrievable
	 * @return The prepared statement with all parameters bound
	 * @throws NullPointerException If any of the non-primitive arguments is null
	 * @throws SqlException If the statement could not be prepared
	 */
	static @NonNull PreparedStatement prepare(@NonNull SqlDialect dialect, @NonNull Connection connection, @NonNull SqlRendered rendered, @NonNull Duration timeout, boolean returnGeneratedKeys) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(connection, "Connection must not be null");
		Objects.requireNonNull(rendered, "Sql rendered must not be null");
		Objects.requireNonNull(timeout, "Query timeout must not be null");
		
		try {
			PreparedStatement statement = returnGeneratedKeys ? connection.prepareStatement(rendered.sql(), Statement.RETURN_GENERATED_KEYS) : connection.prepareStatement(rendered.sql());
			
			List<Pair<SqlType<?>, Object>> parameters = rendered.parameters();
			for (int i = 0; i < parameters.size(); i++) {
				Pair<SqlType<?>, Object> pair = parameters.get(i);
				SqlType.setValue(pair.getFirst(), dialect, statement, i + 1, pair.getSecond());
			}
			
			long seconds = timeout.toSeconds();
			if (seconds == 0 && !timeout.isZero()) {
				seconds = 1;
			}
			statement.setQueryTimeout((int) Math.min(seconds, Integer.MAX_VALUE));
			return statement;
		} catch (SQLException e) {
			throw new SqlQueryExecutionException("Failed to prepare statement: " + rendered.sql(), e, rendered.sql());
		}
	}
	
	/**
	 * Rolls back the given connection, suppressing any error that occurs during the rollback.<br>
	 * The rollback is only performed if the caller owns the surrounding transaction.<br>
	 *
	 * @param connection The connection to roll back
	 * @param ownsTransaction Whether the caller started and therefore owns the transaction
	 */
	private static void rollbackQuietly(@NonNull Connection connection, boolean ownsTransaction) {
		if (ownsTransaction) {
			try {
				connection.rollback();
			} catch (SQLException ignored) {}
		}
	}
	
	/**
	 * Runs the given action inside a transaction on the given connection.<br>
	 * <p>
	 *     If the connection is in auto-commit mode it is switched to manual commit, the action is
	 *     executed and the transaction is committed afterwards; in this case the method owns the
	 *     transaction and restores the auto-commit mode when it returns.<br>
	 *     If the connection is already in manual commit mode the action participates in the existing
	 *     transaction without committing, rolling back or changing the auto-commit mode.
	 * </p>
	 * <p>
	 *     Any failure of the action causes the owned transaction to be rolled back quietly before the
	 *     error is rethrown.
	 * </p>
	 *
	 * @param connection The connection to run the action on
	 * @param firstSql The sql attached to thrown execution exceptions for diagnostics
	 * @param beginErrorMessage The error message used if the transaction could not be started
	 * @param operationErrorMessage The error message used if the action itself fails
	 * @param action The action to execute within the transaction
	 * @param <R> The type of the result produced by the action
	 * @return The result produced by the action
	 * @throws SqlException If the transaction could not be started or the action failed
	 */
	private static <R> R inTransaction(
		@NonNull Connection connection,
		@NonNull String firstSql,
		@NonNull String beginErrorMessage,
		@NonNull String operationErrorMessage,
		@NonNull ThrowableFunction<Connection, R, Exception> action
	) throws SqlException {
		boolean ownsTransaction;
		try {
			ownsTransaction = connection.getAutoCommit();
			if (ownsTransaction) {
				connection.setAutoCommit(false);
			}
		} catch (SQLException e) {
			throw new SqlQueryExecutionException(beginErrorMessage, e, firstSql);
		}
		
		try {
			R result = action.apply(connection);
			if (ownsTransaction) {
				connection.commit();
			}
			return result;
		} catch (SqlException e) {
			rollbackQuietly(connection, ownsTransaction);
			throw e;
		} catch (SQLException e) {
			rollbackQuietly(connection, ownsTransaction);
			throw new SqlQueryExecutionException(operationErrorMessage, e, firstSql);
		} catch (Exception e) {
			rollbackQuietly(connection, ownsTransaction);
			throw new SqlException(operationErrorMessage, e);
		} finally {
			if (ownsTransaction) {
				try {
					connection.setAutoCommit(true);
				} catch (SQLException ignored) {}
			}
		}
	}
	
	/**
	 * Combines the given query with the given returning clause into a single rendered statement.<br>
	 * The parameters of both rendered fragments are preserved in order.<br>
	 *
	 * @param query The rendered query to append the returning clause to
	 * @param returning The rendered returning clause to append
	 * @return The combined rendered statement
	 */
	private static @NonNull SqlRendered combine(@NonNull SqlRendered query, @NonNull SqlRendered returning) {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(query);
		renderer.rendered(returning);
		return renderer.toSql();
	}
	
	/**
	 * Executes the given rendered query and maps its result set to a single scalar value.<br>
	 * The connection, statement and result set are opened and closed within this method.<br>
	 *
	 * @param dialect The sql dialect used to prepare the statement
	 * @param source The connection source to obtain a connection from
	 * @param rendered The rendered query to execute
	 * @param timeout The query timeout to apply
	 * @param mapper The function that maps the result set to the scalar value
	 * @param <T> The type of the mapped scalar value
	 * @return The value produced by the mapper
	 * @throws NullPointerException If any of the arguments is null
	 * @throws SqlException If the query could not be executed or the result could not be mapped
	 */
	static <T> T executeScalarQuery(
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource source,
		@NonNull SqlRendered rendered,
		@NonNull Duration timeout,
		@NonNull ThrowableFunction<ResultSet, T, Exception> mapper
	) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(source, "Sql connection source must not be null");
		Objects.requireNonNull(rendered, "Sql rendered must not be null");
		Objects.requireNonNull(timeout, "Query timeout must not be null");
		Objects.requireNonNull(mapper, "Result set mapper must not be null");
		
		try (SqlConnectionHandle handle = source.open(); PreparedStatement statement = prepare(dialect, handle.connection(), rendered, timeout); ResultSet resultSet = statement.executeQuery()) {
			return mapper.apply(resultSet);
		} catch (SqlException e) {
			throw e;
		} catch (SQLException e) {
			throw new SqlQueryExecutionException("Failed to execute scalar query: " + rendered.sql(), e, rendered.sql());
		} catch (Exception e) {
			throw new SqlException("Failed to execute scalar query: " + rendered.sql(), e);
		}
	}
	
	/**
	 * Executes the given rendered statement as an update and returns the affected row count.<br>
	 * The connection and statement are opened and closed within this method.<br>
	 *
	 * @param dialect The sql dialect used to prepare the statement
	 * @param source The connection source to obtain a connection from
	 * @param rendered The rendered statement to execute
	 * @param timeout The query timeout to apply
	 * @return The number of rows affected by the update
	 * @throws NullPointerException If any of the arguments is null
	 * @throws SqlException If the update could not be executed
	 */
	static int executeUpdate(@NonNull SqlDialect dialect, @NonNull SqlConnectionSource source, @NonNull SqlRendered rendered, @NonNull Duration timeout) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(source, "Sql connection source must not be null");
		Objects.requireNonNull(rendered, "Sql rendered must not be null");
		Objects.requireNonNull(timeout, "Query timeout must not be null");
		
		try (SqlConnectionHandle handle = source.open(); PreparedStatement statement = prepare(dialect, handle.connection(), rendered, timeout)) {
			return statement.executeUpdate();
		} catch (SQLException e) {
			throw new SqlQueryExecutionException("Failed to execute update: " + rendered.sql(), e, rendered.sql());
		}
	}
	
	/**
	 * Executes the given rendered statements as a single batched update inside one transaction.<br>
	 * The returned count is the sum of the affected row counts of all statements.<br>
	 * If the list is empty no statement is executed and {@code 0} is returned.<br>
	 *
	 * @param dialect The sql dialect used to prepare the statements
	 * @param source The connection source to obtain a connection from
	 * @param renderedList The rendered statements to execute
	 * @param timeout The query timeout to apply to each statement
	 * @return The total number of rows affected by all statements
	 * @throws NullPointerException If any of the arguments is null
	 * @throws SqlException If the transaction could not be started or any statement failed
	 */
	static int executeBatchedUpdate(@NonNull SqlDialect dialect, @NonNull SqlConnectionSource source, @NonNull List<SqlRendered> renderedList, @NonNull Duration timeout) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(source, "Sql connection source must not be null");
		Objects.requireNonNull(renderedList, "Sql rendered list must not be null");
		Objects.requireNonNull(timeout, "Query timeout must not be null");
		if (renderedList.isEmpty()) {
			return 0;
		}
		
		String firstSql = renderedList.getFirst().sql();
		try (SqlConnectionHandle handle = source.open()) {
			return inTransaction(handle.connection(), firstSql, "Failed to begin batched update transaction", "Failed to execute batched update", connection -> {
				int total = 0;
				for (SqlRendered rendered : renderedList) {
					try (PreparedStatement statement = prepare(dialect, connection, rendered, timeout)) {
						total += statement.executeUpdate();
					}
				}
				return total;
			});
		}
	}
	
	/**
	 * Executes the given rendered statements as a batched update inside one transaction and collects their generated keys.<br>
	 * Each generated key is read from the first column of the statement's generated keys result set.<br>
	 * If the list is empty no statement is executed and an empty list is returned.<br>
	 *
	 * @param dialect The sql dialect used to prepare the statements
	 * @param source The connection source to obtain a connection from
	 * @param renderedList The rendered statements to execute
	 * @param timeout The query timeout to apply to each statement
	 * @return The generated keys of all statements in execution order
	 * @throws NullPointerException If any of the arguments is null
	 * @throws SqlException If the transaction could not be started or any statement failed
	 */
	static @NonNull List<Long> executeUpdateReturningKeys(@NonNull SqlDialect dialect, @NonNull SqlConnectionSource source, @NonNull List<SqlRendered> renderedList, @NonNull Duration timeout) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(source, "Sql connection source must not be null");
		Objects.requireNonNull(renderedList, "Sql rendered list must not be null");
		Objects.requireNonNull(timeout, "Query timeout must not be null");
		if (renderedList.isEmpty()) {
			return List.of();
		}
		
		String firstSql = renderedList.getFirst().sql();
		try (SqlConnectionHandle handle = source.open()) {
			return inTransaction(handle.connection(), firstSql, "Failed to begin batched update transaction", "Failed to execute batched update returning generated keys", connection -> {
				List<Long> keys = Lists.newArrayList();
				for (SqlRendered rendered : renderedList) {
					try (PreparedStatement statement = prepare(dialect, connection, rendered, timeout, true)) {
						statement.executeUpdate();
						try (ResultSet resultSet = statement.getGeneratedKeys()) {
							while (resultSet.next()) {
								keys.add(resultSet.getLong(1));
							}
						}
					}
				}
				return keys;
			});
		}
	}
	
	/**
	 * Executes the given rendered query and maps every row of its result set to an element.<br>
	 * The row mapper is invoked once per row and the mapped elements are returned in result set order.<br>
	 *
	 * @param dialect The sql dialect used to prepare the statement
	 * @param source The connection source to obtain a connection from
	 * @param rendered The rendered query to execute
	 * @param timeout The query timeout to apply
	 * @param rowMapper The function that maps each row to an element
	 * @param <T> The type of the mapped elements
	 * @return The list of mapped elements, one per result row
	 * @throws NullPointerException If any of the arguments is null
	 * @throws SqlException If the query could not be executed or a row could not be mapped
	 */
	static <T> @NonNull List<T> executeQueryAndMap(
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource source,
		@NonNull SqlRendered rendered,
		@NonNull Duration timeout,
		@NonNull ThrowableFunction<ResultSet, T, SqlException> rowMapper
	) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(source, "Sql connection source must not be null");
		Objects.requireNonNull(rendered, "Sql rendered must not be null");
		Objects.requireNonNull(timeout, "Query timeout must not be null");
		Objects.requireNonNull(rowMapper, "Sql row mapper must not be null");
		
		List<T> results = Lists.newArrayList();
		try (SqlConnectionHandle handle = source.open(); PreparedStatement statement = prepare(dialect, handle.connection(), rendered, timeout); ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				results.add(rowMapper.apply(resultSet));
			}
			return results;
		} catch (SqlException e) {
			throw e;
		} catch (SQLException e) {
			throw new SqlQueryExecutionException("Failed to map result set", e, rendered.sql());
		} catch (Exception e) {
			throw new SqlException("Failed to map result set", e);
		}
	}
	
	/**
	 * Executes the given query combined with a returning clause and maps the returned rows.<br>
	 * The query and returning clause are combined into a single statement before execution.<br>
	 *
	 * @param dialect The sql dialect used to prepare the statement
	 * @param source The connection source to obtain a connection from
	 * @param query The rendered query to execute
	 * @param returning The rendered returning clause to append to the query
	 * @param timeout The query timeout to apply
	 * @param rowMapper The function that maps each returned row to an element
	 * @param <T> The type of the mapped elements
	 * @return The list of mapped elements, one per returned row
	 * @throws NullPointerException If any of the arguments is null
	 * @throws SqlDialectFeatureException If the dialect does not support the returning feature
	 * @throws SqlException If the query could not be executed or a row could not be mapped
	 */
	static <T> @NonNull List<T> executeReturningQuery(
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource source,
		@NonNull SqlRendered query,
		@NonNull SqlRendered returning,
		@NonNull Duration timeout,
		@NonNull ThrowableFunction<ResultSet, T, SqlException> rowMapper
	) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(source, "Sql connection source must not be null");
		Objects.requireNonNull(query, "Sql query rendered must not be null");
		Objects.requireNonNull(returning, "Sql returning rendered must not be null");
		Objects.requireNonNull(timeout, "Query timeout must not be null");
		Objects.requireNonNull(rowMapper, "Sql row mapper must not be null");
		
		if (!dialect.isFeatureSupported(SqlFeature.RETURNING)) {
			throw new SqlDialectFeatureException(SqlFeature.RETURNING, dialect);
		}
		
		return executeQueryAndMap(dialect, source, combine(query, returning), timeout, rowMapper);
	}
	
	/**
	 * Executes the given queries combined with a returning clause inside one transaction and maps all returned rows.<br>
	 * Each query is combined with the same returning clause and executed in order, with their returned rows being collected into a single list.<br>
	 * If the query list is empty no statement is executed and an empty list is returned.<br>
	 *
	 * @param dialect The sql dialect used to prepare the statements
	 * @param source The connection source to obtain a connection from
	 * @param queries The rendered queries to execute
	 * @param returning The rendered returning clause to append to each query
	 * @param timeout The query timeout to apply to each statement
	 * @param rowMapper The function that maps each returned row to an element
	 * @param <T> The type of the mapped elements
	 * @return The list of mapped elements collected from all returned rows
	 * @throws NullPointerException If any of the arguments is null
	 * @throws SqlDialectFeatureException If the dialect does not support the returning feature
	 * @throws SqlException If the transaction could not be started, a query failed or a row could not be mapped
	 */
	static <T> @NonNull List<T> executeBatchedReturningQuery(
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource source,
		@NonNull List<SqlRendered> queries,
		@NonNull SqlRendered returning,
		@NonNull Duration timeout,
		@NonNull ThrowableFunction<ResultSet, T, SqlException> rowMapper
	) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(source, "Sql connection source must not be null");
		Objects.requireNonNull(queries, "Sql query rendered list must not be null");
		Objects.requireNonNull(returning, "Sql returning rendered must not be null");
		Objects.requireNonNull(timeout, "Query timeout must not be null");
		Objects.requireNonNull(rowMapper, "Sql row mapper must not be null");
		
		if (!dialect.isFeatureSupported(SqlFeature.RETURNING)) {
			throw new SqlDialectFeatureException(SqlFeature.RETURNING, dialect);
		}
		if (queries.isEmpty()) {
			return List.of();
		}
		
		String firstSql = queries.getFirst().sql();
		try (SqlConnectionHandle handle = source.open()) {
			return inTransaction(handle.connection(), firstSql, "Failed to begin batched returning transaction", "Failed to execute batched returning query", connection -> {
				List<T> results = Lists.newArrayList();
				for (SqlRendered query : queries) {
					SqlRendered combined = combine(query, returning);
					try (PreparedStatement statement = prepare(dialect, connection, combined, timeout); ResultSet resultSet = statement.executeQuery()) {
						while (resultSet.next()) {
							results.add(rowMapper.apply(resultSet));
						}
					}
				}
				return results;
			});
		}
	}
}
