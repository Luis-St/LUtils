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
import net.luis.utils.io.database.SqlPage;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.SqlFeature;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.SqlResultCountException;
import net.luis.utils.io.database.exception.client.SqlStatementBuilderException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectFeatureException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.expression.orderable.OrderedSqlExpression;
import net.luis.utils.io.database.expression.orderable.SqlOrderable;
import net.luis.utils.io.database.query.*;
import net.luis.utils.io.database.query.row.SqlRowMapper;
import net.luis.utils.io.database.query.util.*;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.ResultSet;
import java.time.Duration;
import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class SqlSelectQuery<E> implements SqlJoinableQuery<E> {
	
	private final SqlTable<?> table;
	private final SqlDialect dialect;
	private final SqlConnectionSource connectionSource;
	private final Duration queryTimeout;
	private final ThrowableFunction<ResultSet, E, SqlException> rowMapper;
	private final List<SqlExpression<?>> selectedExpressions;
	private final List<SqlJoinClause> joins;
	private final List<SqlColumn<?, ?>> groupByColumns;
	private final List<SqlOrderable<?>> orderByClauses;
	private final List<SqlSetOperationEntry<E>> setOperations;
	private final List<SqlCommonTableExpression> commonTableExpressions;
	private final long limit;
	private final long offset;
	private final boolean isDistinct;
	private final @Nullable SqlCondition whereCondition;
	private final @Nullable SqlSelectQuery<?> whereExistsSubquery;
	private final @Nullable SqlCondition havingCondition;
	private final @Nullable SqlLockMode lockMode;
	private final boolean skipLocked;
	private final boolean noWait;
	
	public SqlSelectQuery(@NonNull SqlTable<?> table, @NonNull SqlDialect dialect, @NonNull SqlConnectionSource connectionSource, @NonNull Duration queryTimeout, @NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper) {
		this(table, dialect, connectionSource, queryTimeout, rowMapper, List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), -1, -1, false, null, null, null, null, false, false);
	}
	
	public SqlSelectQuery(@NonNull SqlTable<?> table, @NonNull SqlDialect dialect, @NonNull SqlConnectionSource connectionSource, @NonNull Duration queryTimeout, @NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper, @NonNull List<SqlExpression<?>> selectedExpressions) {
		this(table, dialect, connectionSource, queryTimeout, rowMapper, selectedExpressions, List.of(), List.of(), List.of(), List.of(), List.of(), -1, -1, false, null, null, null, null, false, false);
	}
	
	private SqlSelectQuery(
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
		boolean skipLocked, boolean noWait
	) {
		this.table = Objects.requireNonNull(table, "Sql table must not be null");
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
		this.connectionSource = Objects.requireNonNull(connectionSource, "Sql connection source must not be null");
		this.queryTimeout = Objects.requireNonNull(queryTimeout, "Query timeout must not be null");
		this.rowMapper = Objects.requireNonNull(rowMapper, "Row mapper must not be null");
		this.selectedExpressions = Objects.requireNonNull(selectedExpressions, "Selected expressions must not be null");
		this.joins = Objects.requireNonNull(joins, "Sql join clauses must not be null");
		this.groupByColumns = Objects.requireNonNull(groupByColumns, "Sql group by columns must not be null");
		this.orderByClauses = Objects.requireNonNull(orderByClauses, "Sql order by clauses must not be null");
		this.setOperations = Objects.requireNonNull(setOperations, "Sql set operations must not be null");
		this.commonTableExpressions = Objects.requireNonNull(commonTableExpressions, "Sql common table expressions must not be null");
		this.limit = limit;
		this.offset = offset;
		this.isDistinct = isDistinct;
		this.whereCondition = whereCondition;
		this.whereExistsSubquery = whereExistsSubquery;
		this.havingCondition = havingCondition;
		this.lockMode = lockMode;
		this.skipLocked = skipLocked;
		this.noWait = noWait;
		
		if (limit < -1) {
			throw new IllegalArgumentException("Limit must be non-negative or -1 for no limit");
		}
		if (offset < -1) {
			throw new IllegalArgumentException("Offset must be non-negative or -1 for no offset");
		}
	}
	
	private @NonNull SqlSelectQuery<E> copy(
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
		return new SqlSelectQuery<>(
			this.table,
			this.dialect,
			this.connectionSource,
			this.queryTimeout,
			this.rowMapper,
			this.selectedExpressions,
			joins,
			groupByColumns,
			orderByClauses,
			setOperations,
			commonTableExpressions,
			limit,
			offset,
			isDistinct,
			whereCondition,
			whereExistsSubquery,
			havingCondition,
			lockMode,
			skipLocked,
			noWait
		);
	}
	
	private @NonNull SqlSelectQuery<E> withLockMode(@NonNull SqlLockMode lockMode) {
		return this.copy(
			this.joins,
			this.groupByColumns,
			this.orderByClauses,
			this.setOperations,
			this.commonTableExpressions,
			this.limit,
			this.offset,
			this.isDistinct,
			this.whereCondition,
			this.whereExistsSubquery,
			this.havingCondition,
			lockMode,
			this.skipLocked,
			this.noWait
		);
	}
	
	public @NonNull SqlSelectQuery<E> forUpdate() {
		return this.withLockMode(SqlLockMode.FOR_UPDATE);
	}
	
	public @NonNull SqlSelectQuery<E> skipLocked() throws SqlException {
		if (this.lockMode == null) {
			throw new SqlStatementBuilderException("skipLocked() requires a lock mode to be set first (e.g. forUpdate() or forShare())");
		}
		if (!this.dialect.isFeatureSupported(SqlFeature.SKIP_LOCKED)) {
			throw new SqlDialectFeatureException(SqlFeature.SKIP_LOCKED, this.dialect);
		}
		
		return this.copy(
			this.joins,
			this.groupByColumns,
			this.orderByClauses,
			this.setOperations,
			this.commonTableExpressions,
			this.limit,
			this.offset,
			this.isDistinct,
			this.whereCondition,
			this.whereExistsSubquery,
			this.havingCondition,
			this.lockMode,
			true,
			this.noWait
		);
	}
	
	public @NonNull SqlSelectQuery<E> forShare() {
		return this.withLockMode(SqlLockMode.FOR_SHARE);
	}
	
	public @NonNull SqlSelectQuery<E> noWait() throws SqlException {
		if (this.lockMode == null) {
			throw new SqlStatementBuilderException("noWait() requires a lock mode to be set first (e.g. forUpdate() or forShare())");
		}
		if (!this.dialect.isFeatureSupported(SqlFeature.NO_WAIT)) {
			throw new SqlDialectFeatureException(SqlFeature.NO_WAIT, this.dialect);
		}
		
		return this.copy(
			this.joins,
			this.groupByColumns,
			this.orderByClauses,
			this.setOperations,
			this.commonTableExpressions,
			this.limit,
			this.offset,
			this.isDistinct,
			this.whereCondition,
			this.whereExistsSubquery,
			this.havingCondition,
			this.lockMode,
			this.skipLocked,
			true
		);
	}
	
	private @NonNull SqlSelectQuery<E> withJoin(@NonNull SqlJoinClause join) {
		return this.copy(
			SqlQuery.copyAndAdd(this.joins, join),
			this.groupByColumns,
			this.orderByClauses,
			this.setOperations,
			this.commonTableExpressions,
			this.limit,
			this.offset,
			this.isDistinct,
			this.whereCondition,
			this.whereExistsSubquery,
			this.havingCondition,
			this.lockMode,
			this.skipLocked,
			this.noWait
		);
	}
	
	@Override
	public @NonNull SqlSelectQuery<E> innerJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return this.withJoin(new SqlJoinClause(SqlJoinType.INNER, table, on));
	}
	
	@Override
	public @NonNull SqlSelectQuery<E> leftJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return this.withJoin(new SqlJoinClause(SqlJoinType.LEFT, table, on));
	}
	
	@Override
	public @NonNull SqlSelectQuery<E> rightJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return this.withJoin(new SqlJoinClause(SqlJoinType.RIGHT, table, on));
	}
	
	@Override
	public @NonNull SqlSelectQuery<E> fullJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return this.withJoin(new SqlJoinClause(SqlJoinType.FULL, table, on));
	}
	
	@Override
	public @NonNull SqlSelectQuery<E> crossJoin(@NonNull SqlTable<?> table) {
		return this.withJoin(new SqlJoinClause(SqlJoinType.CROSS, table, null));
	}
	
	public @NonNull SqlSelectQuery<E> lateralJoin(@NonNull SqlSelectQuery<?> subquery, @NonNull SqlAlias alias) throws SqlException {
		return this.lateralJoin(SqlJoinType.CROSS, subquery, alias);
	}
	
	public @NonNull SqlSelectQuery<E> lateralJoin(@NonNull SqlJoinType type, @NonNull SqlSelectQuery<?> subquery, @NonNull SqlAlias alias) throws SqlException {
		if (!this.dialect.isFeatureSupported(SqlFeature.LATERAL_JOIN)) {
			throw new SqlDialectFeatureException(SqlFeature.LATERAL_JOIN, this.dialect);
		}
		return this.withJoin(new SqlJoinClause(type, subquery, alias));
	}
	
	public @NonNull SqlSelectQuery<E> where(@NonNull SqlCondition condition) {
		Objects.requireNonNull(condition, "Sql where condition must not be null");
		
		SqlCondition combined = this.whereCondition != null ? SqlCondition.allOf(this.whereCondition, condition) : condition;
		return this.copy(
			this.joins,
			this.groupByColumns,
			this.orderByClauses,
			this.setOperations,
			this.commonTableExpressions,
			this.limit,
			this.offset,
			this.isDistinct,
			combined,
			this.whereExistsSubquery,
			this.havingCondition,
			this.lockMode,
			this.skipLocked,
			this.noWait
		);
	}
	
	public @NonNull SqlSelectQuery<E> whereExists(@NonNull SqlSelectQuery<?> subquery) {
		return this.copy(
			this.joins,
			this.groupByColumns,
			this.orderByClauses,
			this.setOperations,
			this.commonTableExpressions,
			this.limit,
			this.offset,
			this.isDistinct,
			this.whereCondition,
			Objects.requireNonNull(subquery, "Sql subquery must not be null"),
			this.havingCondition,
			this.lockMode,
			this.skipLocked,
			this.noWait
		);
	}
	
	public final @NonNull SqlSelectQuery<E> groupBy(SqlColumn<?, ?> @NonNull ... columns) {
		Objects.requireNonNull(columns, "Group by sql columns must not be null");
		
		return this.copy(
			this.joins,
			SqlQuery.copyAndAddAll(this.groupByColumns, Arrays.asList(columns)),
			this.orderByClauses,
			this.setOperations,
			this.commonTableExpressions,
			this.limit,
			this.offset,
			this.isDistinct,
			this.whereCondition,
			this.whereExistsSubquery,
			this.havingCondition,
			this.lockMode,
			this.skipLocked,
			this.noWait
		);
	}
	
	public @NonNull SqlSelectQuery<E> having(@NonNull SqlCondition condition) {
		return this.copy(
			this.joins,
			this.groupByColumns,
			this.orderByClauses,
			this.setOperations,
			this.commonTableExpressions,
			this.limit,
			this.offset,
			this.isDistinct,
			this.whereCondition,
			this.whereExistsSubquery,
			Objects.requireNonNull(condition, "Sql having condition must not be null"),
			this.lockMode,
			this.skipLocked,
			this.noWait
		);
	}
	
	public @NonNull SqlSelectQuery<E> orderBy(SqlOrderable<?> @NonNull ... orderables) {
		Objects.requireNonNull(orderables, "Sql order by clauses must not be null");
		
		return this.copy(
			this.joins,
			this.groupByColumns,
			SqlQuery.copyAndAddAll(this.orderByClauses, Arrays.asList(orderables)),
			this.setOperations,
			this.commonTableExpressions,
			this.limit,
			this.offset,
			this.isDistinct,
			this.whereCondition,
			this.whereExistsSubquery,
			this.havingCondition,
			this.lockMode,
			this.skipLocked,
			this.noWait
		);
	}
	
	public @NonNull SqlSelectQuery<E> limit(long limit) {
		if (limit < 0) {
			throw new IllegalArgumentException("Limit must be non-negative");
		}
		
		return this.copy(
			this.joins,
			this.groupByColumns,
			this.orderByClauses,
			this.setOperations,
			this.commonTableExpressions,
			limit,
			this.offset,
			this.isDistinct,
			this.whereCondition,
			this.whereExistsSubquery,
			this.havingCondition,
			this.lockMode,
			this.skipLocked,
			this.noWait
		);
	}
	
	public @NonNull SqlSelectQuery<E> offset(long offset) {
		if (offset < 0) {
			throw new IllegalArgumentException("Offset must be non-negative");
		}
		
		return this.copy(
			this.joins,
			this.groupByColumns,
			this.orderByClauses,
			this.setOperations,
			this.commonTableExpressions,
			this.limit,
			offset,
			this.isDistinct,
			this.whereCondition,
			this.whereExistsSubquery,
			this.havingCondition,
			this.lockMode,
			this.skipLocked,
			this.noWait
		);
	}
	
	public @NonNull SqlSelectQuery<E> distinct() {
		return this.copy(
			this.joins,
			this.groupByColumns,
			this.orderByClauses,
			this.setOperations,
			this.commonTableExpressions,
			this.limit,
			this.offset,
			true,
			this.whereCondition,
			this.whereExistsSubquery,
			this.havingCondition,
			this.lockMode,
			this.skipLocked,
			this.noWait
		);
	}
	
	public @NonNull SqlSelectQuery<E> union(@NonNull SqlSelectQuery<E> other) {
		return this.copy(
			this.joins,
			this.groupByColumns,
			this.orderByClauses,
			SqlQuery.copyAndAdd(this.setOperations, new SqlSetOperationEntry<>(SqlSetOperation.UNION, other)),
			this.commonTableExpressions,
			this.limit,
			this.offset,
			this.isDistinct,
			this.whereCondition,
			this.whereExistsSubquery,
			this.havingCondition,
			this.lockMode,
			this.skipLocked,
			this.noWait
		);
	}
	
	public @NonNull SqlSelectQuery<E> unionAll(@NonNull SqlSelectQuery<E> other) {
		return this.copy(
			this.joins,
			this.groupByColumns,
			this.orderByClauses,
			SqlQuery.copyAndAdd(this.setOperations, new SqlSetOperationEntry<>(SqlSetOperation.UNION_ALL, other)),
			this.commonTableExpressions,
			this.limit,
			this.offset,
			this.isDistinct,
			this.whereCondition,
			this.whereExistsSubquery,
			this.havingCondition,
			this.lockMode,
			this.skipLocked,
			this.noWait
		);
	}
	
	public @NonNull SqlSelectQuery<E> intersect(@NonNull SqlSelectQuery<E> other) {
		return this.copy(
			this.joins,
			this.groupByColumns,
			this.orderByClauses,
			SqlQuery.copyAndAdd(this.setOperations, new SqlSetOperationEntry<>(SqlSetOperation.INTERSECT, other)),
			this.commonTableExpressions,
			this.limit,
			this.offset,
			this.isDistinct,
			this.whereCondition,
			this.whereExistsSubquery,
			this.havingCondition,
			this.lockMode,
			this.skipLocked,
			this.noWait
		);
	}
	
	public @NonNull SqlSelectQuery<E> except(@NonNull SqlSelectQuery<E> other) {
		return this.copy(
			this.joins,
			this.groupByColumns,
			this.orderByClauses,
			SqlQuery.copyAndAdd(this.setOperations, new SqlSetOperationEntry<>(SqlSetOperation.EXCEPT, other)),
			this.commonTableExpressions,
			this.limit,
			this.offset,
			this.isDistinct,
			this.whereCondition,
			this.whereExistsSubquery,
			this.havingCondition,
			this.lockMode,
			this.skipLocked,
			this.noWait
		);
	}
	
	public <R> @NonNull SqlSelectQuery<R> projectInto(@NonNull Class<R> type) throws SqlException {
		Objects.requireNonNull(type, "Sql projection type must not be null");
		if (this.selectedExpressions.isEmpty()) {
			throw new SqlStatementBuilderException("Cannot project into " + type.getSimpleName() + ": No expressions selected");
		}
		
		ThrowableFunction<ResultSet, R, SqlException> projectionMapper = SqlRowMapper.forProjection(type, this.selectedExpressions);
		return new SqlSelectQuery<>(
			this.table,
			this.dialect,
			this.connectionSource,
			this.queryTimeout,
			projectionMapper,
			this.selectedExpressions,
			this.joins,
			this.groupByColumns,
			this.orderByClauses,
			List.of(),
			this.commonTableExpressions,
			this.limit,
			this.offset,
			this.isDistinct,
			this.whereCondition,
			this.whereExistsSubquery,
			this.havingCondition,
			this.lockMode,
			this.skipLocked,
			this.noWait
		);
	}
	
	public @NonNull SqlCommonTableExpression asCommonExpression(@NonNull SqlAlias alias, boolean recursive) {
		return new SqlCommonTableExpression(alias, this, recursive);
	}
	
	public @NonNull SqlSelectQuery<E> with(@NonNull SqlCommonTableExpression commonTableExpression) {
		Objects.requireNonNull(commonTableExpression, "Sql common table expression must not be null");
		
		return this.copy(
			this.joins,
			this.groupByColumns,
			this.orderByClauses,
			this.setOperations,
			SqlQuery.copyAndAdd(this.commonTableExpressions, commonTableExpression),
			this.limit,
			this.offset,
			this.isDistinct,
			this.whereCondition,
			this.whereExistsSubquery,
			this.havingCondition,
			this.lockMode,
			this.skipLocked,
			this.noWait
		);
	}
	
	private @NonNull List<E> executeAndMap() throws SqlException {
		return SqlQueryExecutor.executeQueryAndMap(this.dialect, this.connectionSource, this.toSql(this.dialect), this.queryTimeout, this.rowMapper);
	}
	
	public @NonNull List<E> fetch() throws SqlException {
		return Collections.unmodifiableList(this.executeAndMap());
	}
	
	public @NonNull Optional<E> fetchFirst() throws SqlException {
		List<E> results = this.limit(1).executeAndMap();
		return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
	}
	
	public @NonNull E fetchOne() throws SqlException {
		List<E> results = this.limit(2).executeAndMap();
		
		if (results.size() != 1) {
			throw new SqlResultCountException("Expected exactly one result but got " + (results.size() >= 2 ? "more than 1" : "0"), 1, results.size());
		}
		return results.getFirst();
	}
	
	public @Nullable E fetchOneOrNull() throws SqlException {
		List<E> results = this.limit(2).executeAndMap();
		
		if (results.size() > 1) {
			throw new SqlResultCountException("Expected at most one result but got more than 1", 1, results.size());
		}
		return results.isEmpty() ? null : results.getFirst();
	}
	
	public long count() throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.select().literal("COUNT(*)").from().openingBracket();
		renderer.rendered(this.toSql(this.dialect));
		renderer.closingBracket().as().literal(this.dialect.quoteIdentifier("__count"));
		
		return SqlQueryExecutor.executeScalarQuery(this.dialect, this.connectionSource, renderer.toSql(), this.queryTimeout, resultSet -> resultSet.next() ? resultSet.getLong(1) : 0L);
	}
	
	public boolean exists() throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.select().literal("1").from().openingBracket();
		renderer.rendered(this.limit(1).toSql(this.dialect));
		renderer.closingBracket().as().literal(this.dialect.quoteIdentifier("__exists"));
		
		return SqlQueryExecutor.executeScalarQuery(this.dialect, this.connectionSource, renderer.toSql(), this.queryTimeout, ResultSet::next);
	}
	
	public @NonNull SqlPage<E> fetchPage(int page, int pageSize) throws SqlException {
		if (page < 0) {
			throw new IllegalArgumentException("Page index must be non-negative");
		}
		if (pageSize <= 0) {
			throw new IllegalArgumentException("Page size must be positive");
		}
		if (this.orderByClauses.isEmpty()) {
			throw new SqlStatementBuilderException("Pagination requires an ORDER BY clause for deterministic results");
		}
		
		List<E> results = this.offset((long) page * pageSize).limit(pageSize + 1).executeAndMap();
		boolean hasNext = results.size() > pageSize;
		List<E> content = hasNext ? results.subList(0, pageSize) : results;
		return new SqlPage<>(content, page, pageSize, hasNext, page > 0);
	}
	
	@Override
	public @NonNull SqlRendered toSql(@NonNull SqlDialect dialect) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		SqlRenderer renderer = SqlRenderer.empty();
		
		if (!this.commonTableExpressions.isEmpty()) {
			renderer.with();
			
			if (this.commonTableExpressions.stream().anyMatch(SqlCommonTableExpression::recursive)) {
				renderer.recursive();
			}
			
			for (int i = 0; i < this.commonTableExpressions.size(); i++) {
				if (i > 0) {
					renderer.comma();
				}
				
				SqlCommonTableExpression commonTableExpression = this.commonTableExpressions.get(i);
				renderer.literal(dialect.quoteIdentifier(commonTableExpression.alias().get()));
				renderer.as().openingBracket();
				renderer.rendered(commonTableExpression.query().toSql(dialect));
				renderer.closingBracket();
			}
		}
		
		renderer.select();
		if (this.isDistinct) {
			renderer.distinct();
		}
		
		if (this.selectedExpressions.isEmpty()) {
			renderer.literal("*");
		} else {
			for (int i = 0; i < this.selectedExpressions.size(); i++) {
				if (i > 0) {
					renderer.comma();
				}
				renderer.rendered(dialect.renderExpression(this.selectedExpressions.get(i)));
			}
		}
		
		renderer.from().literal(dialect.quoteIdentifier(this.table.name()));
		
		for (SqlJoinClause join : this.joins) {
			renderer.rendered(join.toSql(dialect));
		}
		
		if (this.whereCondition != null) {
			renderer.where().rendered(dialect.renderCondition(this.whereCondition));
		}
		
		if (this.whereExistsSubquery != null) {
			if (this.whereCondition != null) {
				renderer.and();
			} else {
				renderer.where();
			}
			
			renderer.exists().openingBracket();
			renderer.rendered(this.whereExistsSubquery.toSql(dialect));
			renderer.closingBracket();
		}
		
		if (!this.groupByColumns.isEmpty()) {
			renderer.groupBy();
			
			for (int i = 0; i < this.groupByColumns.size(); i++) {
				if (i > 0) {
					renderer.comma();
				}
				
				renderer.rendered(this.groupByColumns.get(i).toSql(dialect));
			}
		}
		
		if (this.havingCondition != null) {
			renderer.having().rendered(dialect.renderCondition(this.havingCondition));
		}
		
		for (SqlSetOperationEntry<E> entry : this.setOperations) {
			renderer.rendered(dialect.renderSetOperation(entry.operation()));
			renderer.rendered(entry.query().toSql(dialect));
		}
		
		if (!this.orderByClauses.isEmpty()) {
			renderer.orderBy();
			
			for (int i = 0; i < this.orderByClauses.size(); i++) {
				SqlOrderable<?> orderable = this.orderByClauses.get(i);
				if (i > 0) {
					renderer.comma();
				}
				
				if (orderable instanceof OrderedSqlExpression<?> ordered) {
					renderer.rendered(dialect.renderExpression(ordered.expression()));
					renderer.rendered(dialect.renderOrdering(ordered.ordering(), ordered.nullOrdering()));
				} else if (orderable instanceof SqlExpression<?> expression) {
					renderer.rendered(dialect.renderExpression(expression));
				}
			}
		}
		
		if (this.limit >= 0 || this.offset >= 0) {
			renderer.rendered(dialect.renderLimitOffset(
				this.limit,
				this.offset >= 0 ? this.offset : 0
			));
		}
		
		if (this.lockMode != null) {
			if (this.skipLocked && this.lockMode == SqlLockMode.FOR_SHARE) {
				throw new SqlStatementBuilderException("Skip locked is only allowed with FOR UPDATE lock mode");
			}
			renderer.rendered(dialect.renderLockClause(this.lockMode, this.skipLocked, this.noWait));
		}
		return renderer.toSql();
	}
}
