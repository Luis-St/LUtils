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
import net.luis.utils.io.database.audit.*;
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
	
	private final SqlSelectQueryConfig<E> config;
	
	public SqlSelectQuery(
		@NonNull SqlTable<?> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper
	) {
		this(new SqlSelectQueryConfig<>(table, dialect, connectionSource, queryTimeout, rowMapper, List.of()));
	}
	
	public SqlSelectQuery(
		@NonNull SqlTable<?> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull List<SqlExpression<?>> selectedExpressions
	) {
		this(new SqlSelectQueryConfig<>(table, dialect, connectionSource, queryTimeout, rowMapper, selectedExpressions));
	}
	
	SqlSelectQuery(@NonNull SqlSelectQueryConfig<E> config) {
		this.config = Objects.requireNonNull(config, "Sql select query config must not be null");
	}
	
	public @NonNull SqlSelectQuery<E> forUpdate() throws SqlException {
		if (!this.config.dialect().isFeatureSupported(SqlFeature.FOR_UPDATE)) {
			throw new SqlDialectFeatureException(SqlFeature.FOR_UPDATE, this.config.dialect());
		}
		return new SqlSelectQuery<>(this.config.withLockMode(SqlLockMode.FOR_UPDATE));
	}
	
	public @NonNull SqlSelectQuery<E> forShare() throws SqlException {
		if (!this.config.dialect().isFeatureSupported(SqlFeature.FOR_SHARE)) {
			throw new SqlDialectFeatureException(SqlFeature.FOR_SHARE, this.config.dialect());
		}
		return new SqlSelectQuery<>(this.config.withLockMode(SqlLockMode.FOR_SHARE));
	}
	
	public @NonNull SqlSelectQuery<E> skipLocked() throws SqlException {
		if (this.config.lockMode() == null) {
			throw new SqlStatementBuilderException("skipLocked() requires a lock mode to be set first (e.g. forUpdate() or forShare())");
		}
		if (!this.config.dialect().isFeatureSupported(SqlFeature.SKIP_LOCKED)) {
			throw new SqlDialectFeatureException(SqlFeature.SKIP_LOCKED, this.config.dialect());
		}
		
		return new SqlSelectQuery<>(this.config.withSkipLocked());
	}
	
	public @NonNull SqlSelectQuery<E> noWait() throws SqlException {
		if (this.config.lockMode() == null) {
			throw new SqlStatementBuilderException("noWait() requires a lock mode to be set first (e.g. forUpdate() or forShare())");
		}
		if (!this.config.dialect().isFeatureSupported(SqlFeature.NO_WAIT)) {
			throw new SqlDialectFeatureException(SqlFeature.NO_WAIT, this.config.dialect());
		}
		
		return new SqlSelectQuery<>(this.config.withNoWait());
	}
	
	private @NonNull SqlSelectQuery<E> withJoin(@NonNull SqlJoinClause join) {
		return new SqlSelectQuery<>(this.config.withJoins(SqlQuery.copyAndAdd(this.config.joins(), join)));
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
		if (!this.config.dialect().isFeatureSupported(SqlFeature.LATERAL_JOIN)) {
			throw new SqlDialectFeatureException(SqlFeature.LATERAL_JOIN, this.config.dialect());
		}
		return this.withJoin(new SqlJoinClause(type, subquery, alias));
	}
	
	public @NonNull SqlSelectQuery<E> where(@NonNull SqlCondition condition) {
		Objects.requireNonNull(condition, "Sql where condition must not be null");
		
		SqlCondition existing = this.config.whereCondition();
		SqlCondition combined = existing != null ? SqlCondition.allOf(existing, condition) : condition;
		return new SqlSelectQuery<>(this.config.withWhereCondition(combined));
	}
	
	public @NonNull SqlSelectQuery<E> whereExists(@NonNull SqlSelectQuery<?> subquery) {
		Objects.requireNonNull(subquery, "Sql subquery must not be null");
		return new SqlSelectQuery<>(this.config.withWhereExistsSubquery(subquery));
	}
	
	public final @NonNull SqlSelectQuery<E> groupBy(SqlColumn<?, ?> @NonNull ... columns) {
		Objects.requireNonNull(columns, "Group by sql columns must not be null");
		
		return new SqlSelectQuery<>(this.config.withGroupByColumns(SqlQuery.copyAndAddAll(this.config.groupByColumns(), Arrays.asList(columns))));
	}
	
	public @NonNull SqlSelectQuery<E> having(@NonNull SqlCondition condition) {
		Objects.requireNonNull(condition, "Sql having condition must not be null");
		return new SqlSelectQuery<>(this.config.withHavingCondition(condition));
	}
	
	public @NonNull SqlSelectQuery<E> orderBy(SqlOrderable<?> @NonNull ... orderables) {
		Objects.requireNonNull(orderables, "Sql order by clauses must not be null");
		
		return new SqlSelectQuery<>(this.config.withOrderByClauses(SqlQuery.copyAndAddAll(this.config.orderByClauses(), Arrays.asList(orderables))));
	}
	
	public @NonNull SqlSelectQuery<E> limit(long limit) {
		if (limit < 0) {
			throw new IllegalArgumentException("Limit must be non-negative");
		}
		
		return new SqlSelectQuery<>(this.config.withLimit(limit));
	}
	
	public @NonNull SqlSelectQuery<E> offset(long offset) {
		if (offset < 0) {
			throw new IllegalArgumentException("Offset must be non-negative");
		}
		
		return new SqlSelectQuery<>(this.config.withOffset(offset));
	}
	
	public @NonNull SqlSelectQuery<E> distinct() {
		return new SqlSelectQuery<>(this.config.withDistinct());
	}
	
	public @NonNull SqlSelectQuery<E> union(@NonNull SqlSelectQuery<E> other) {
		return new SqlSelectQuery<>(this.config.withSetOperations(SqlQuery.copyAndAdd(this.config.setOperations(), new SqlSetOperationEntry<>(SqlSetOperation.UNION, other))));
	}
	
	public @NonNull SqlSelectQuery<E> unionAll(@NonNull SqlSelectQuery<E> other) {
		return new SqlSelectQuery<>(this.config.withSetOperations(SqlQuery.copyAndAdd(this.config.setOperations(), new SqlSetOperationEntry<>(SqlSetOperation.UNION_ALL, other))));
	}
	
	public @NonNull SqlSelectQuery<E> intersect(@NonNull SqlSelectQuery<E> other) {
		return new SqlSelectQuery<>(this.config.withSetOperations(SqlQuery.copyAndAdd(this.config.setOperations(), new SqlSetOperationEntry<>(SqlSetOperation.INTERSECT, other))));
	}
	
	public @NonNull SqlSelectQuery<E> except(@NonNull SqlSelectQuery<E> other) {
		return new SqlSelectQuery<>(this.config.withSetOperations(SqlQuery.copyAndAdd(this.config.setOperations(), new SqlSetOperationEntry<>(SqlSetOperation.EXCEPT, other))));
	}
	
	public <R> @NonNull SqlSelectQuery<R> projectInto(@NonNull Class<R> type) throws SqlException {
		Objects.requireNonNull(type, "Sql projection type must not be null");
		if (this.config.selectedExpressions().isEmpty()) {
			throw new SqlStatementBuilderException("Cannot project into " + type.getSimpleName() + ": No expressions selected");
		}
		
		ThrowableFunction<ResultSet, R, SqlException> projectionMapper = SqlRowMapper.forProjection(type, this.config.selectedExpressions(), this.config.dialect());
		return new SqlSelectQuery<>(new SqlSelectQueryConfig<>(
			this.config.table(),
			this.config.dialect(),
			this.config.connectionSource(),
			this.config.queryTimeout(),
			projectionMapper,
			this.config.selectedExpressions(),
			this.config.joins(),
			this.config.groupByColumns(),
			this.config.orderByClauses(),
			List.of(),
			this.config.commonTableExpressions(),
			this.config.limit(),
			this.config.offset(),
			this.config.isDistinct(),
			this.config.whereCondition(),
			this.config.whereExistsSubquery(),
			this.config.havingCondition(),
			this.config.lockMode(),
			this.config.skipLocked(),
			this.config.noWait()
		));
	}
	
	public @NonNull SqlCommonTableExpression asCommonExpression(@NonNull SqlAlias alias, boolean recursive) {
		return new SqlCommonTableExpression(alias, this, recursive);
	}
	
	public @NonNull SqlSelectQuery<E> with(@NonNull SqlCommonTableExpression commonTableExpression) {
		Objects.requireNonNull(commonTableExpression, "Sql common table expression must not be null");
		
		return new SqlSelectQuery<>(this.config.withCommonTableExpressions(SqlQuery.copyAndAdd(this.config.commonTableExpressions(), commonTableExpression)));
	}
	
	public @NonNull SqlSelectQuery<SqlAudited<E>> withAudit() throws SqlException {
		SqlAuditConfig auditConfig = this.config.table().auditConfig().orElseThrow(() -> new SqlStatementBuilderException("withAudit() requires an audited sql table"));
		if (!this.config.selectedExpressions().isEmpty()) {
			throw new SqlStatementBuilderException("withAudit() is only supported for full-entity selects, not projections");
		}
		
		int entityColumnCount = this.config.table().columns().size();
		ThrowableFunction<ResultSet, E, SqlException> entityMapper = this.config.rowMapper();
		ThrowableFunction<ResultSet, SqlAudited<E>, SqlException> auditMapper = resultSet -> {
			E entity = entityMapper.apply(resultSet);
			return SqlAudited.of(entity, SqlAuditMetadata.readFrom(this.config.dialect(), resultSet, entityColumnCount + 1, auditConfig));
		};
		return new SqlSelectQuery<>(new SqlSelectQueryConfig<>(
			this.config.table(),
			this.config.dialect(),
			this.config.connectionSource(),
			this.config.queryTimeout(),
			auditMapper,
			this.config.selectedExpressions(),
			this.config.joins(),
			this.config.groupByColumns(),
			this.config.orderByClauses(),
			List.of(),
			this.config.commonTableExpressions(),
			this.config.limit(),
			this.config.offset(),
			this.config.isDistinct(),
			this.config.whereCondition(),
			this.config.whereExistsSubquery(),
			this.config.havingCondition(),
			this.config.lockMode(),
			this.config.skipLocked(),
			this.config.noWait()
		));
	}
	
	private @NonNull List<E> executeAndMap() throws SqlException {
		return SqlQueryExecutor.executeQueryAndMap(this.config.dialect(), this.config.connectionSource(), this.toSql(this.config.dialect()), this.config.queryTimeout(), this.config.rowMapper());
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
		SqlDialect dialect = this.config.dialect();
		SqlSelectQueryConfig<E> stripped = this.config.withOrderByClauses(List.of()).withLimit(-1).withOffset(-1);
		boolean needsWrap = stripped.isDistinct() || !stripped.groupByColumns().isEmpty() || stripped.havingCondition() != null || !stripped.setOperations().isEmpty();
		
		SqlRendered countSql;
		if (needsWrap) {
			SqlRenderer renderer = SqlRenderer.empty();
			renderer.select().literal("COUNT(*)").from().openingBracket();
			renderer.rendered(this.render(stripped, dialect, false));
			renderer.closingBracket().as().literal(dialect.quoteIdentifier("__count"));
			countSql = renderer.toSql();
		} else {
			countSql = this.render(stripped, dialect, true);
		}
		return SqlQueryExecutor.executeScalarQuery(dialect, this.config.connectionSource(), countSql, this.config.queryTimeout(), resultSet -> resultSet.next() ? resultSet.getLong(1) : 0L);
	}
	
	public boolean exists() throws SqlException {
		SqlDialect dialect = this.config.dialect();
		SqlSelectQueryConfig<E> core = this.config.withOrderByClauses(List.of()).withLimit(1);
		return SqlQueryExecutor.executeScalarQuery(dialect, this.config.connectionSource(), this.render(core, dialect, false), this.config.queryTimeout(), ResultSet::next);
	}
	
	public @NonNull SqlPage<E> fetchPage(int page, int pageSize) throws SqlException {
		if (page < 0) {
			throw new IllegalArgumentException("Page index must be non-negative");
		}
		if (pageSize <= 0) {
			throw new IllegalArgumentException("Page size must be positive");
		}
		if (this.config.orderByClauses().isEmpty()) {
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
		return this.render(this.config, dialect, false);
	}
	
	private @NonNull SqlRendered render(@NonNull SqlSelectQueryConfig<?> cfg, @NonNull SqlDialect dialect, boolean countStar) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		
		if (!cfg.commonTableExpressions().isEmpty()) {
			renderer.with();
			
			if (cfg.commonTableExpressions().stream().anyMatch(SqlCommonTableExpression::recursive)) {
				renderer.recursive();
			}
			
			for (int i = 0; i < cfg.commonTableExpressions().size(); i++) {
				if (i > 0) {
					renderer.comma();
				}
				
				SqlCommonTableExpression commonTableExpression = cfg.commonTableExpressions().get(i);
				renderer.literal(dialect.quoteIdentifier(commonTableExpression.alias().get()));
				renderer.as().openingBracket();
				renderer.rendered(commonTableExpression.query().toSql(dialect));
				renderer.closingBracket();
			}
		}
		
		renderer.select();
		if (countStar) {
			renderer.literal("COUNT(*)");
		} else {
			if (cfg.isDistinct()) {
				renderer.distinct();
			}
			
			if (cfg.selectedExpressions().isEmpty()) {
				renderer.literal("*");
			} else {
				for (int i = 0; i < cfg.selectedExpressions().size(); i++) {
					if (i > 0) {
						renderer.comma();
					}
					renderer.rendered(dialect.renderExpression(cfg.selectedExpressions().get(i)));
				}
			}
		}
		
		renderer.from().literal(dialect.quoteIdentifier(cfg.table().name()));
		
		for (SqlJoinClause join : cfg.joins()) {
			renderer.rendered(join.toSql(dialect));
		}
		
		if (cfg.whereCondition() != null) {
			renderer.where().rendered(dialect.renderCondition(cfg.whereCondition()));
		}
		
		if (cfg.whereExistsSubquery() != null) {
			if (cfg.whereCondition() != null) {
				renderer.and();
			} else {
				renderer.where();
			}
			
			renderer.exists().openingBracket();
			renderer.rendered(cfg.whereExistsSubquery().toSql(dialect));
			renderer.closingBracket();
		}
		
		if (!cfg.groupByColumns().isEmpty()) {
			renderer.groupBy();
			
			for (int i = 0; i < cfg.groupByColumns().size(); i++) {
				if (i > 0) {
					renderer.comma();
				}
				
				renderer.rendered(cfg.groupByColumns().get(i).toSql(dialect));
			}
		}
		
		if (cfg.havingCondition() != null) {
			renderer.having().rendered(dialect.renderCondition(cfg.havingCondition()));
		}
		
		for (SqlSetOperationEntry<?> entry : cfg.setOperations()) {
			renderer.rendered(dialect.renderSetOperation(entry.operation()));
			renderer.rendered(entry.query().toSql(dialect));
		}
		
		if (countStar) {
			return renderer.toSql();
		}
		
		if (!cfg.orderByClauses().isEmpty()) {
			renderer.orderBy();
			
			for (int i = 0; i < cfg.orderByClauses().size(); i++) {
				SqlOrderable<?> orderable = cfg.orderByClauses().get(i);
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
		
		if (cfg.limit() >= 0 || cfg.offset() >= 0) {
			renderer.rendered(dialect.renderLimitOffset(
				cfg.limit(),
				cfg.offset() >= 0 ? cfg.offset() : 0,
				!cfg.orderByClauses().isEmpty()
			));
		}
		
		if (cfg.lockMode() != null) {
			if (cfg.skipLocked() && cfg.lockMode() == SqlLockMode.FOR_SHARE) {
				throw new SqlStatementBuilderException("Skip locked is only allowed with FOR UPDATE lock mode");
			}
			renderer.rendered(dialect.renderLockClause(cfg.lockMode(), cfg.skipLocked(), cfg.noWait()));
		}
		return renderer.toSql();
	}
}
