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

import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.io.database.SqlConnectionSource;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.expression.orderable.SqlOrderable;
import net.luis.utils.io.database.query.SqlCommonTableExpression;
import net.luis.utils.io.database.query.SqlLockMode;
import net.luis.utils.io.database.query.util.SqlJoinClause;
import net.luis.utils.io.database.query.util.SqlSetOperationEntry;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.ResultSet;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * Holds the immutable state of a {@link SqlSelectQuery}.<br>
 * Each builder operation on the query produces a new configuration via the {@code with...} methods.<br>
 *
 * @author Luis-St
 *
 * @param <E> The type of the entities mapped from the result rows
 * @param table The primary table the query selects from
 * @param dialect The sql dialect used to render the query
 * @param connectionSource The connection source used to execute the query
 * @param queryTimeout The timeout applied to the query execution
 * @param rowMapper The row mapper used to map result rows to entities
 * @param selectedExpressions The expressions selected by the query
 * @param joins The join clauses applied to the query
 * @param groupByColumns The columns the result is grouped by
 * @param orderByClauses The order by clauses applied to the query
 * @param setOperations The set operations combined with this query
 * @param commonTableExpressions The common table expressions prepended to the query
 * @param limit The maximum number of rows to return, or {@code -1} for no limit
 * @param offset The number of leading rows to skip, or {@code -1} for no offset
 * @param isDistinct Whether duplicate rows are removed from the result
 * @param whereCondition The condition restricting which rows are returned, or {@code null} for no condition
 * @param whereExistsSubquery The subquery for a where exists clause, or {@code null}
 * @param havingCondition The condition applied to the grouped rows, or {@code null} for no condition
 * @param lockMode The row locking mode applied to the selected rows, or {@code null} for no locking
 * @param skipLocked Whether rows locked by other transactions are skipped
 * @param noWait Whether the query fails immediately instead of waiting for locked rows
 */
record SqlSelectQueryConfig<E>(
	@NonNull SqlTable<?> table,
	@NonNull SqlDialect dialect,
	@NonNull SqlConnectionSource connectionSource,
	@NonNull Duration queryTimeout,
	@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
	@NonNull List<SqlExpression<?>> selectedExpressions,
	@NonNull List<SqlJoinClause> joins,
	@NonNull List<SqlColumn<?, ?>> groupByColumns,
	@NonNull List<SqlOrderable<?>> orderByClauses,
	@NonNull List<SqlSetOperationEntry<E>> setOperations,
	@NonNull List<SqlCommonTableExpression> commonTableExpressions,
	long limit,
	long offset,
	boolean isDistinct,
	@Nullable SqlCondition whereCondition,
	@Nullable SqlSelectQuery<?> whereExistsSubquery,
	@Nullable SqlCondition havingCondition,
	@Nullable SqlLockMode lockMode,
	boolean skipLocked,
	boolean noWait
) {
	
	/**
	 * Constructs a new select query configuration with the given core components and default empty clauses.<br>
	 * The limit and offset are set to {@code -1}, all clause lists are empty and all optional values are {@code null}.<br>
	 *
	 * @param table The primary table the query selects from
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the query execution
	 * @param rowMapper The row mapper used to map result rows to entities
	 * @param selectedExpressions The expressions selected by the query
	 */
	SqlSelectQueryConfig(
		@NonNull SqlTable<?> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull List<SqlExpression<?>> selectedExpressions
	) {
		this(table, dialect, connectionSource, queryTimeout, rowMapper, selectedExpressions, List.of(), List.of(), List.of(), List.of(), List.of(), -1, -1, false, null, null, null, null, false, false);
	}
	
	/**
	 * Constructs a new select query configuration validating the required components.<br>
	 *
	 * @throws NullPointerException If the table, dialect, connection source, query timeout, row mapper, selected expressions, joins, group by columns, order by clauses, set operations or common table expressions are null
	 * @throws IllegalArgumentException If the limit or offset is less than {@code -1}
	 */
	SqlSelectQueryConfig {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(connectionSource, "Sql connection source must not be null");
		Objects.requireNonNull(queryTimeout, "Query timeout must not be null");
		Objects.requireNonNull(rowMapper, "Row mapper must not be null");
		Objects.requireNonNull(selectedExpressions, "Selected expressions must not be null");
		Objects.requireNonNull(joins, "Sql join clauses must not be null");
		Objects.requireNonNull(groupByColumns, "Sql group by columns must not be null");
		Objects.requireNonNull(orderByClauses, "Sql order by clauses must not be null");
		Objects.requireNonNull(setOperations, "Sql set operations must not be null");
		Objects.requireNonNull(commonTableExpressions, "Sql common table expressions must not be null");
		
		if (limit < -1) {
			throw new IllegalArgumentException("Limit must be non-negative or -1 for no limit");
		}
		if (offset < -1) {
			throw new IllegalArgumentException("Offset must be non-negative or -1 for no offset");
		}
	}
	
	/**
	 * Creates a copy of this configuration with the given join clauses.<br>
	 *
	 * @param joins The join clauses to apply
	 * @return A new configuration with the updated joins
	 */
	@NonNull SqlSelectQueryConfig<E> withJoins(@NonNull List<SqlJoinClause> joins) {
		return new SqlSelectQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.selectedExpressions, joins, this.groupByColumns, this.orderByClauses, this.setOperations, this.commonTableExpressions, this.limit, this.offset, this.isDistinct, this.whereCondition, this.whereExistsSubquery, this.havingCondition, this.lockMode, this.skipLocked, this.noWait);
	}
	
	/**
	 * Creates a copy of this configuration with the given group by columns.<br>
	 *
	 * @param groupByColumns The columns the result is grouped by
	 * @return A new configuration with the updated group by columns
	 */
	@NonNull SqlSelectQueryConfig<E> withGroupByColumns(@NonNull List<SqlColumn<?, ?>> groupByColumns) {
		return new SqlSelectQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.selectedExpressions, this.joins, groupByColumns, this.orderByClauses, this.setOperations, this.commonTableExpressions, this.limit, this.offset, this.isDistinct, this.whereCondition, this.whereExistsSubquery, this.havingCondition, this.lockMode, this.skipLocked, this.noWait);
	}
	
	/**
	 * Creates a copy of this configuration with the given order by clauses.<br>
	 *
	 * @param orderByClauses The order by clauses to apply
	 * @return A new configuration with the updated order by clauses
	 */
	@NonNull SqlSelectQueryConfig<E> withOrderByClauses(@NonNull List<SqlOrderable<?>> orderByClauses) {
		return new SqlSelectQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.selectedExpressions, this.joins, this.groupByColumns, orderByClauses, this.setOperations, this.commonTableExpressions, this.limit, this.offset, this.isDistinct, this.whereCondition, this.whereExistsSubquery, this.havingCondition, this.lockMode, this.skipLocked, this.noWait);
	}
	
	/**
	 * Creates a copy of this configuration with the given set operations.<br>
	 *
	 * @param setOperations The set operations to combine with this query
	 * @return A new configuration with the updated set operations
	 */
	@NonNull SqlSelectQueryConfig<E> withSetOperations(@NonNull List<SqlSetOperationEntry<E>> setOperations) {
		return new SqlSelectQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.selectedExpressions, this.joins, this.groupByColumns, this.orderByClauses, setOperations, this.commonTableExpressions, this.limit, this.offset, this.isDistinct, this.whereCondition, this.whereExistsSubquery, this.havingCondition, this.lockMode, this.skipLocked, this.noWait);
	}
	
	/**
	 * Creates a copy of this configuration with the given common table expressions.<br>
	 *
	 * @param commonTableExpressions The common table expressions to prepend to the query
	 * @return A new configuration with the updated common table expressions
	 */
	@NonNull SqlSelectQueryConfig<E> withCommonTableExpressions(@NonNull List<SqlCommonTableExpression> commonTableExpressions) {
		return new SqlSelectQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.selectedExpressions, this.joins, this.groupByColumns, this.orderByClauses, this.setOperations, commonTableExpressions, this.limit, this.offset, this.isDistinct, this.whereCondition, this.whereExistsSubquery, this.havingCondition, this.lockMode, this.skipLocked, this.noWait);
	}
	
	/**
	 * Creates a copy of this configuration with the given row limit.<br>
	 *
	 * @param limit The maximum number of rows to return, or {@code -1} for no limit
	 * @return A new configuration with the updated limit
	 */
	@NonNull SqlSelectQueryConfig<E> withLimit(long limit) {
		return new SqlSelectQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.selectedExpressions, this.joins, this.groupByColumns, this.orderByClauses, this.setOperations, this.commonTableExpressions, limit, this.offset, this.isDistinct, this.whereCondition, this.whereExistsSubquery, this.havingCondition, this.lockMode, this.skipLocked, this.noWait);
	}
	
	/**
	 * Creates a copy of this configuration with the given row offset.<br>
	 *
	 * @param offset The number of leading rows to skip, or {@code -1} for no offset
	 * @return A new configuration with the updated offset
	 */
	@NonNull SqlSelectQueryConfig<E> withOffset(long offset) {
		return new SqlSelectQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.selectedExpressions, this.joins, this.groupByColumns, this.orderByClauses, this.setOperations, this.commonTableExpressions, this.limit, offset, this.isDistinct, this.whereCondition, this.whereExistsSubquery, this.havingCondition, this.lockMode, this.skipLocked, this.noWait);
	}
	
	/**
	 * Creates a copy of this configuration that removes duplicate rows from the result.<br>
	 * @return A new configuration with the distinct flag enabled
	 */
	@NonNull SqlSelectQueryConfig<E> withDistinct() {
		return new SqlSelectQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.selectedExpressions, this.joins, this.groupByColumns, this.orderByClauses, this.setOperations, this.commonTableExpressions, this.limit, this.offset, true, this.whereCondition, this.whereExistsSubquery, this.havingCondition, this.lockMode, this.skipLocked, this.noWait);
	}
	
	/**
	 * Creates a copy of this configuration with the given where condition.<br>
	 *
	 * @param whereCondition The condition restricting which rows are returned, or {@code null} for no condition
	 * @return A new configuration with the updated where condition
	 */
	@NonNull SqlSelectQueryConfig<E> withWhereCondition(@Nullable SqlCondition whereCondition) {
		return new SqlSelectQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.selectedExpressions, this.joins, this.groupByColumns, this.orderByClauses, this.setOperations, this.commonTableExpressions, this.limit, this.offset, this.isDistinct, whereCondition, this.whereExistsSubquery, this.havingCondition, this.lockMode, this.skipLocked, this.noWait);
	}
	
	/**
	 * Creates a copy of this configuration with the given where exists subquery.<br>
	 *
	 * @param whereExistsSubquery The subquery for a where exists clause, or {@code null} for none
	 * @return A new configuration with the updated where exists subquery
	 */
	@NonNull SqlSelectQueryConfig<E> withWhereExistsSubquery(@Nullable SqlSelectQuery<?> whereExistsSubquery) {
		return new SqlSelectQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.selectedExpressions, this.joins, this.groupByColumns, this.orderByClauses, this.setOperations, this.commonTableExpressions, this.limit, this.offset, this.isDistinct, this.whereCondition, whereExistsSubquery, this.havingCondition, this.lockMode, this.skipLocked, this.noWait);
	}
	
	/**
	 * Creates a copy of this configuration with the given having condition.<br>
	 *
	 * @param havingCondition The condition applied to the grouped rows, or {@code null} for no condition
	 * @return A new configuration with the updated having condition
	 */
	@NonNull SqlSelectQueryConfig<E> withHavingCondition(@Nullable SqlCondition havingCondition) {
		return new SqlSelectQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.selectedExpressions, this.joins, this.groupByColumns, this.orderByClauses, this.setOperations, this.commonTableExpressions, this.limit, this.offset, this.isDistinct, this.whereCondition, this.whereExistsSubquery, havingCondition, this.lockMode, this.skipLocked, this.noWait);
	}
	
	/**
	 * Creates a copy of this configuration with the given row locking mode.<br>
	 *
	 * @param lockMode The row locking mode applied to the selected rows, or {@code null} for no locking
	 * @return A new configuration with the updated lock mode
	 */
	@NonNull SqlSelectQueryConfig<E> withLockMode(@Nullable SqlLockMode lockMode) {
		return new SqlSelectQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.selectedExpressions, this.joins, this.groupByColumns, this.orderByClauses, this.setOperations, this.commonTableExpressions, this.limit, this.offset, this.isDistinct, this.whereCondition, this.whereExistsSubquery, this.havingCondition, lockMode, this.skipLocked, this.noWait);
	}
	
	/**
	 * Creates a copy of this configuration that skips rows locked by other transactions.<br>
	 * @return A new configuration with the skip-locked flag enabled
	 */
	@NonNull SqlSelectQueryConfig<E> withSkipLocked() {
		return new SqlSelectQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.selectedExpressions, this.joins, this.groupByColumns, this.orderByClauses, this.setOperations, this.commonTableExpressions, this.limit, this.offset, this.isDistinct, this.whereCondition, this.whereExistsSubquery, this.havingCondition, this.lockMode, true, this.noWait);
	}
	
	/**
	 * Creates a copy of this configuration that fails immediately instead of waiting for locked rows.<br>
	 * @return A new configuration with the no-wait flag enabled
	 */
	@NonNull SqlSelectQueryConfig<E> withNoWait() {
		return new SqlSelectQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.selectedExpressions, this.joins, this.groupByColumns, this.orderByClauses, this.setOperations, this.commonTableExpressions, this.limit, this.offset, this.isDistinct, this.whereCondition, this.whereExistsSubquery, this.havingCondition, this.lockMode, this.skipLocked, true);
	}
}
