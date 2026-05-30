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
 *
 * @author Luis-St
 *
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
	
	@NonNull SqlSelectQueryConfig<E> withJoins(@NonNull List<SqlJoinClause> joins) {
		return new SqlSelectQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.selectedExpressions, joins, this.groupByColumns, this.orderByClauses, this.setOperations, this.commonTableExpressions, this.limit, this.offset, this.isDistinct, this.whereCondition, this.whereExistsSubquery, this.havingCondition, this.lockMode, this.skipLocked, this.noWait);
	}
	
	@NonNull SqlSelectQueryConfig<E> withGroupByColumns(@NonNull List<SqlColumn<?, ?>> groupByColumns) {
		return new SqlSelectQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.selectedExpressions, this.joins, groupByColumns, this.orderByClauses, this.setOperations, this.commonTableExpressions, this.limit, this.offset, this.isDistinct, this.whereCondition, this.whereExistsSubquery, this.havingCondition, this.lockMode, this.skipLocked, this.noWait);
	}
	
	@NonNull SqlSelectQueryConfig<E> withOrderByClauses(@NonNull List<SqlOrderable<?>> orderByClauses) {
		return new SqlSelectQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.selectedExpressions, this.joins, this.groupByColumns, orderByClauses, this.setOperations, this.commonTableExpressions, this.limit, this.offset, this.isDistinct, this.whereCondition, this.whereExistsSubquery, this.havingCondition, this.lockMode, this.skipLocked, this.noWait);
	}
	
	@NonNull SqlSelectQueryConfig<E> withSetOperations(@NonNull List<SqlSetOperationEntry<E>> setOperations) {
		return new SqlSelectQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.selectedExpressions, this.joins, this.groupByColumns, this.orderByClauses, setOperations, this.commonTableExpressions, this.limit, this.offset, this.isDistinct, this.whereCondition, this.whereExistsSubquery, this.havingCondition, this.lockMode, this.skipLocked, this.noWait);
	}
	
	@NonNull SqlSelectQueryConfig<E> withCommonTableExpressions(@NonNull List<SqlCommonTableExpression> commonTableExpressions) {
		return new SqlSelectQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.selectedExpressions, this.joins, this.groupByColumns, this.orderByClauses, this.setOperations, commonTableExpressions, this.limit, this.offset, this.isDistinct, this.whereCondition, this.whereExistsSubquery, this.havingCondition, this.lockMode, this.skipLocked, this.noWait);
	}
	
	@NonNull SqlSelectQueryConfig<E> withLimit(long limit) {
		return new SqlSelectQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.selectedExpressions, this.joins, this.groupByColumns, this.orderByClauses, this.setOperations, this.commonTableExpressions, limit, this.offset, this.isDistinct, this.whereCondition, this.whereExistsSubquery, this.havingCondition, this.lockMode, this.skipLocked, this.noWait);
	}
	
	@NonNull SqlSelectQueryConfig<E> withOffset(long offset) {
		return new SqlSelectQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.selectedExpressions, this.joins, this.groupByColumns, this.orderByClauses, this.setOperations, this.commonTableExpressions, this.limit, offset, this.isDistinct, this.whereCondition, this.whereExistsSubquery, this.havingCondition, this.lockMode, this.skipLocked, this.noWait);
	}
	
	@NonNull SqlSelectQueryConfig<E> withDistinct() {
		return new SqlSelectQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.selectedExpressions, this.joins, this.groupByColumns, this.orderByClauses, this.setOperations, this.commonTableExpressions, this.limit, this.offset, true, this.whereCondition, this.whereExistsSubquery, this.havingCondition, this.lockMode, this.skipLocked, this.noWait);
	}
	
	@NonNull SqlSelectQueryConfig<E> withWhereCondition(@Nullable SqlCondition whereCondition) {
		return new SqlSelectQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.selectedExpressions, this.joins, this.groupByColumns, this.orderByClauses, this.setOperations, this.commonTableExpressions, this.limit, this.offset, this.isDistinct, whereCondition, this.whereExistsSubquery, this.havingCondition, this.lockMode, this.skipLocked, this.noWait);
	}
	
	@NonNull SqlSelectQueryConfig<E> withWhereExistsSubquery(@Nullable SqlSelectQuery<?> whereExistsSubquery) {
		return new SqlSelectQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.selectedExpressions, this.joins, this.groupByColumns, this.orderByClauses, this.setOperations, this.commonTableExpressions, this.limit, this.offset, this.isDistinct, this.whereCondition, whereExistsSubquery, this.havingCondition, this.lockMode, this.skipLocked, this.noWait);
	}
	
	@NonNull SqlSelectQueryConfig<E> withHavingCondition(@Nullable SqlCondition havingCondition) {
		return new SqlSelectQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.selectedExpressions, this.joins, this.groupByColumns, this.orderByClauses, this.setOperations, this.commonTableExpressions, this.limit, this.offset, this.isDistinct, this.whereCondition, this.whereExistsSubquery, havingCondition, this.lockMode, this.skipLocked, this.noWait);
	}
	
	@NonNull SqlSelectQueryConfig<E> withLockMode(@Nullable SqlLockMode lockMode) {
		return new SqlSelectQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.selectedExpressions, this.joins, this.groupByColumns, this.orderByClauses, this.setOperations, this.commonTableExpressions, this.limit, this.offset, this.isDistinct, this.whereCondition, this.whereExistsSubquery, this.havingCondition, lockMode, this.skipLocked, this.noWait);
	}
	
	@NonNull SqlSelectQueryConfig<E> withSkipLocked() {
		return new SqlSelectQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.selectedExpressions, this.joins, this.groupByColumns, this.orderByClauses, this.setOperations, this.commonTableExpressions, this.limit, this.offset, this.isDistinct, this.whereCondition, this.whereExistsSubquery, this.havingCondition, this.lockMode, true, this.noWait);
	}
	
	@NonNull SqlSelectQueryConfig<E> withNoWait() {
		return new SqlSelectQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.selectedExpressions, this.joins, this.groupByColumns, this.orderByClauses, this.setOperations, this.commonTableExpressions, this.limit, this.offset, this.isDistinct, this.whereCondition, this.whereExistsSubquery, this.havingCondition, this.lockMode, this.skipLocked, true);
	}
}
