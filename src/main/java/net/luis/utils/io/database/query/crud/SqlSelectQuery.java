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
 * A fluent, immutable builder for sql {@code SELECT} statements that can be executed against a database.<br>
 * Each builder method returns a new query instance with the additional clause applied, leaving the original query unchanged.<br>
 * The query is rendered into dialect specific sql when one of the terminal operations (such as {@link #fetch()}, {@link #count()} or {@link #exists()}) is invoked.<br>
 *
 * @see SqlJoinableQuery
 * @see SqlSelectQueryConfig
 *
 * @author Luis-St
 *
 * @param <E> The type of the entities produced by the row mapper
 */
public class SqlSelectQuery<E> implements SqlJoinableQuery<E> {
	
	/**
	 * The immutable configuration that holds all clauses and execution settings of this query.
	 */
	private final SqlSelectQueryConfig<E> config;
	
	/**
	 * Constructs a new select query that selects all columns of the given table.<br>
	 *
	 * @param table The table to select from
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @throws NullPointerException If any of the arguments is null
	 */
	public SqlSelectQuery(
		@NonNull SqlTable<?> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper
	) {
		this(new SqlSelectQueryConfig<>(table, dialect, connectionSource, queryTimeout, rowMapper, List.of()));
	}
	
	/**
	 * Constructs a new select query that selects the given expressions of the given table.<br>
	 *
	 * @param table The table to select from
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @param selectedExpressions The expressions to select instead of all columns of the table
	 * @throws NullPointerException If any of the arguments is null
	 */
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
	
	/**
	 * Constructs a new select query that wraps the given configuration.<br>
	 *
	 * @param config The configuration that holds all clauses and execution settings of the query
	 * @throws NullPointerException If the configuration is null
	 */
	SqlSelectQuery(@NonNull SqlSelectQueryConfig<E> config) {
		this.config = Objects.requireNonNull(config, "Sql select query config must not be null");
	}
	
	/**
	 * Adds a {@code FOR UPDATE} lock to this query, locking the selected rows for writing.<br>
	 *
	 * @return A new query with the {@code FOR UPDATE} lock mode applied
	 * @throws SqlDialectFeatureException If the dialect does not support the {@code FOR UPDATE} feature
	 */
	public @NonNull SqlSelectQuery<E> forUpdate() throws SqlException {
		if (!this.config.dialect().isFeatureSupported(SqlFeature.FOR_UPDATE)) {
			throw new SqlDialectFeatureException(SqlFeature.FOR_UPDATE, this.config.dialect());
		}
		return new SqlSelectQuery<>(this.config.withLockMode(SqlLockMode.FOR_UPDATE));
	}
	
	/**
	 * Adds a {@code FOR SHARE} lock to this query, locking the selected rows for reading.<br>
	 *
	 * @return A new query with the {@code FOR SHARE} lock mode applied
	 * @throws SqlDialectFeatureException If the dialect does not support the {@code FOR SHARE} feature
	 */
	public @NonNull SqlSelectQuery<E> forShare() throws SqlException {
		if (!this.config.dialect().isFeatureSupported(SqlFeature.FOR_SHARE)) {
			throw new SqlDialectFeatureException(SqlFeature.FOR_SHARE, this.config.dialect());
		}
		return new SqlSelectQuery<>(this.config.withLockMode(SqlLockMode.FOR_SHARE));
	}
	
	/**
	 * Adds a {@code SKIP LOCKED} hint to this query, skipping rows that are currently locked by other transactions.<br>
	 * A lock mode must already be set via {@link #forUpdate()} or {@link #forShare()} before calling this method.<br>
	 *
	 * @return A new query with the skip-locked hint applied
	 * @throws SqlStatementBuilderException If no lock mode has been set yet
	 * @throws SqlDialectFeatureException If the dialect does not support the skip-locked feature
	 */
	public @NonNull SqlSelectQuery<E> skipLocked() throws SqlException {
		if (this.config.lockMode() == null) {
			throw new SqlStatementBuilderException("skipLocked() requires a lock mode to be set first (e.g. forUpdate() or forShare())");
		}
		if (!this.config.dialect().isFeatureSupported(SqlFeature.SKIP_LOCKED)) {
			throw new SqlDialectFeatureException(SqlFeature.SKIP_LOCKED, this.config.dialect());
		}
		
		return new SqlSelectQuery<>(this.config.withSkipLocked());
	}
	
	/**
	 * Adds a {@code NOWAIT} hint to this query, failing immediately instead of waiting for locked rows.<br>
	 * A lock mode must already be set via {@link #forUpdate()} or {@link #forShare()} before calling this method.<br>
	 *
	 * @return A new query with the no-wait hint applied
	 * @throws SqlStatementBuilderException If no lock mode has been set yet
	 * @throws SqlDialectFeatureException If the dialect does not support the no-wait feature
	 */
	public @NonNull SqlSelectQuery<E> noWait() throws SqlException {
		if (this.config.lockMode() == null) {
			throw new SqlStatementBuilderException("noWait() requires a lock mode to be set first (e.g. forUpdate() or forShare())");
		}
		if (!this.config.dialect().isFeatureSupported(SqlFeature.NO_WAIT)) {
			throw new SqlDialectFeatureException(SqlFeature.NO_WAIT, this.config.dialect());
		}
		
		return new SqlSelectQuery<>(this.config.withNoWait());
	}
	
	/**
	 * Creates a new query with the given join clause appended to the existing joins.<br>
	 *
	 * @param join The join clause to add
	 * @return A new query with the join clause applied
	 * @throws NullPointerException If the join clause is null
	 */
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
	
	/**
	 * Adds a lateral cross join to the given subquery using the given alias.<br>
	 * A lateral join allows the subquery to reference columns of the preceding tables in the {@code FROM} clause.<br>
	 *
	 * @param subquery The subquery to join laterally
	 * @param alias The alias under which the subquery is referenced
	 * @return A new query with the lateral join applied
	 * @throws NullPointerException If the subquery or alias is null
	 * @throws SqlDialectFeatureException If the dialect does not support lateral joins
	 */
	public @NonNull SqlSelectQuery<E> lateralJoin(@NonNull SqlSelectQuery<?> subquery, @NonNull SqlAlias alias) throws SqlException {
		return this.lateralJoin(SqlJoinType.CROSS, subquery, alias);
	}
	
	/**
	 * Adds a lateral join of the given type to the given subquery using the given alias.<br>
	 * A lateral join allows the subquery to reference columns of the preceding tables in the {@code FROM} clause.<br>
	 *
	 * @param type The type of the join
	 * @param subquery The subquery to join laterally
	 * @param alias The alias under which the subquery is referenced
	 * @return A new query with the lateral join applied
	 * @throws NullPointerException If the join type, subquery or alias is null
	 * @throws SqlDialectFeatureException If the dialect does not support lateral joins
	 */
	public @NonNull SqlSelectQuery<E> lateralJoin(@NonNull SqlJoinType type, @NonNull SqlSelectQuery<?> subquery, @NonNull SqlAlias alias) throws SqlException {
		if (!this.config.dialect().isFeatureSupported(SqlFeature.LATERAL_JOIN)) {
			throw new SqlDialectFeatureException(SqlFeature.LATERAL_JOIN, this.config.dialect());
		}
		return this.withJoin(new SqlJoinClause(type, subquery, alias));
	}
	
	/**
	 * Adds the given condition to the {@code WHERE} clause of this query.<br>
	 * If a where condition already exists, the given condition is combined with it using a logical conjunction.<br>
	 *
	 * @param condition The condition to add to the where clause
	 * @return A new query with the where condition applied
	 * @throws NullPointerException If the condition is null
	 */
	public @NonNull SqlSelectQuery<E> where(@NonNull SqlCondition condition) {
		Objects.requireNonNull(condition, "Sql where condition must not be null");
		
		SqlCondition existing = this.config.whereCondition();
		SqlCondition combined = existing != null ? SqlCondition.allOf(existing, condition) : condition;
		return new SqlSelectQuery<>(this.config.withWhereCondition(combined));
	}
	
	/**
	 * Adds an {@code EXISTS} subquery to the {@code WHERE} clause of this query.<br>
	 * The query matches rows for which the given subquery returns at least one row.<br>
	 *
	 * @param subquery The subquery to test for existence
	 * @return A new query with the exists subquery applied
	 * @throws NullPointerException If the subquery is null
	 */
	public @NonNull SqlSelectQuery<E> whereExists(@NonNull SqlSelectQuery<?> subquery) {
		Objects.requireNonNull(subquery, "Sql subquery must not be null");
		return new SqlSelectQuery<>(this.config.withWhereExistsSubquery(subquery));
	}
	
	/**
	 * Adds the given columns to the {@code GROUP BY} clause of this query.<br>
	 * The columns are appended to any columns added by previous calls.<br>
	 *
	 * @param columns The columns to group by
	 * @return A new query with the group by columns applied
	 * @throws NullPointerException If the columns array is null
	 */
	public final @NonNull SqlSelectQuery<E> groupBy(SqlColumn<?, ?> @NonNull ... columns) {
		Objects.requireNonNull(columns, "Group by sql columns must not be null");
		
		return new SqlSelectQuery<>(this.config.withGroupByColumns(SqlQuery.copyAndAddAll(this.config.groupByColumns(), Arrays.asList(columns))));
	}
	
	/**
	 * Sets the given condition as the {@code HAVING} clause of this query.<br>
	 * The having clause filters the grouped rows produced by a {@code GROUP BY} clause.<br>
	 *
	 * @param condition The condition to use as the having clause
	 * @return A new query with the having condition applied
	 * @throws NullPointerException If the condition is null
	 */
	public @NonNull SqlSelectQuery<E> having(@NonNull SqlCondition condition) {
		Objects.requireNonNull(condition, "Sql having condition must not be null");
		return new SqlSelectQuery<>(this.config.withHavingCondition(condition));
	}
	
	/**
	 * Adds the given orderables to the {@code ORDER BY} clause of this query.<br>
	 * The orderables are appended to any orderables added by previous calls.<br>
	 *
	 * @param orderables The expressions or ordered expressions to order by
	 * @return A new query with the order by clauses applied
	 * @throws NullPointerException If the orderables array is null
	 */
	public @NonNull SqlSelectQuery<E> orderBy(SqlOrderable<?> @NonNull ... orderables) {
		Objects.requireNonNull(orderables, "Sql order by clauses must not be null");
		
		return new SqlSelectQuery<>(this.config.withOrderByClauses(SqlQuery.copyAndAddAll(this.config.orderByClauses(), Arrays.asList(orderables))));
	}
	
	/**
	 * Sets the maximum number of rows returned by this query.<br>
	 *
	 * @param limit The maximum number of rows to return
	 * @return A new query with the limit applied
	 * @throws IllegalArgumentException If the limit is negative
	 */
	public @NonNull SqlSelectQuery<E> limit(long limit) {
		if (limit < 0) {
			throw new IllegalArgumentException("Limit must be non-negative");
		}
		
		return new SqlSelectQuery<>(this.config.withLimit(limit));
	}
	
	/**
	 * Sets the number of leading rows skipped by this query.<br>
	 *
	 * @param offset The number of rows to skip
	 * @return A new query with the offset applied
	 * @throws IllegalArgumentException If the offset is negative
	 */
	public @NonNull SqlSelectQuery<E> offset(long offset) {
		if (offset < 0) {
			throw new IllegalArgumentException("Offset must be non-negative");
		}
		
		return new SqlSelectQuery<>(this.config.withOffset(offset));
	}
	
	/**
	 * Marks this query as {@code DISTINCT}, removing duplicate rows from the result.<br>
	 * @return A new query with the distinct flag applied
	 */
	public @NonNull SqlSelectQuery<E> distinct() {
		return new SqlSelectQuery<>(this.config.withDistinct());
	}
	
	/**
	 * Combines this query with the given query using a {@code UNION} set operation, removing duplicate rows.<br>
	 *
	 * @param other The query to combine with this query
	 * @return A new query with the union set operation applied
	 * @throws NullPointerException If the other query is null
	 */
	public @NonNull SqlSelectQuery<E> union(@NonNull SqlSelectQuery<E> other) {
		return new SqlSelectQuery<>(this.config.withSetOperations(SqlQuery.copyAndAdd(this.config.setOperations(), new SqlSetOperationEntry<>(SqlSetOperation.UNION, other))));
	}
	
	/**
	 * Combines this query with the given query using a {@code UNION ALL} set operation, keeping duplicate rows.<br>
	 *
	 * @param other The query to combine with this query
	 * @return A new query with the union all set operation applied
	 * @throws NullPointerException If the other query is null
	 */
	public @NonNull SqlSelectQuery<E> unionAll(@NonNull SqlSelectQuery<E> other) {
		return new SqlSelectQuery<>(this.config.withSetOperations(SqlQuery.copyAndAdd(this.config.setOperations(), new SqlSetOperationEntry<>(SqlSetOperation.UNION_ALL, other))));
	}
	
	/**
	 * Combines this query with the given query using an {@code INTERSECT} set operation, keeping only rows present in both.<br>
	 *
	 * @param other The query to combine with this query
	 * @return A new query with the intersect set operation applied
	 * @throws NullPointerException If the other query is null
	 */
	public @NonNull SqlSelectQuery<E> intersect(@NonNull SqlSelectQuery<E> other) {
		return new SqlSelectQuery<>(this.config.withSetOperations(SqlQuery.copyAndAdd(this.config.setOperations(), new SqlSetOperationEntry<>(SqlSetOperation.INTERSECT, other))));
	}
	
	/**
	 * Combines this query with the given query using an {@code EXCEPT} set operation, keeping only rows absent from the other query.<br>
	 *
	 * @param other The query to subtract from this query
	 * @return A new query with the except set operation applied
	 * @throws NullPointerException If the other query is null
	 */
	public @NonNull SqlSelectQuery<E> except(@NonNull SqlSelectQuery<E> other) {
		return new SqlSelectQuery<>(this.config.withSetOperations(SqlQuery.copyAndAdd(this.config.setOperations(), new SqlSetOperationEntry<>(SqlSetOperation.EXCEPT, other))));
	}
	
	/**
	 * Projects the selected expressions of this query into instances of the given type.<br>
	 * The row mapper is replaced by a mapper that constructs the projection type from the selected expressions.<br>
	 *
	 * @param type The type to project the selected expressions into
	 * @param <R> The type of the projection
	 * @return A new query that maps each row into an instance of the projection type
	 * @throws NullPointerException If the type is null
	 * @throws SqlStatementBuilderException If no expressions have been selected
	 */
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
	
	/**
	 * Wraps this query into a common table expression using the given alias and recursive flag.<br>
	 * The resulting expression can be added to another query via {@link #with(SqlCommonTableExpression)}.<br>
	 *
	 * @param alias The alias under which the common table expression is referenced
	 * @param recursive Whether the common table expression is recursive
	 * @return A new common table expression backed by this query
	 * @throws NullPointerException If the alias is null
	 */
	public @NonNull SqlCommonTableExpression asCommonExpression(@NonNull SqlAlias alias, boolean recursive) {
		return new SqlCommonTableExpression(alias, this, recursive);
	}
	
	/**
	 * Adds the given common table expression to the {@code WITH} clause of this query.<br>
	 * The expression is appended to any common table expressions added by previous calls.<br>
	 *
	 * @param commonTableExpression The common table expression to add
	 * @return A new query with the common table expression applied
	 * @throws NullPointerException If the common table expression is null
	 */
	public @NonNull SqlSelectQuery<E> with(@NonNull SqlCommonTableExpression commonTableExpression) {
		Objects.requireNonNull(commonTableExpression, "Sql common table expression must not be null");
		
		return new SqlSelectQuery<>(this.config.withCommonTableExpressions(SqlQuery.copyAndAdd(this.config.commonTableExpressions(), commonTableExpression)));
	}
	
	/**
	 * Includes the audit columns of the table in the result and wraps each entity together with its audit metadata.<br>
	 * This is only supported for full-entity selects on audited tables, not for projections.<br>
	 *
	 * @return A new query that maps each row into an audited entity
	 * @throws SqlStatementBuilderException If the table is not audited or the query selects projected expressions
	 */
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
	
	/**
	 * Renders this query, executes it and maps the resulting rows into entities.<br>
	 *
	 * @return The mutable list of mapped entities
	 * @throws SqlException If an error occurs while executing the query
	 */
	private @NonNull List<E> executeAndMap() throws SqlException {
		return SqlQueryExecutor.executeQueryAndMap(this.config.dialect(), this.config.connectionSource(), this.toSql(this.config.dialect()), this.config.queryTimeout(), this.config.rowMapper());
	}
	
	/**
	 * Executes this query and returns all matching rows as entities.<br>
	 *
	 * @return An unmodifiable list with all mapped entities
	 * @throws SqlException If an error occurs while executing the query
	 */
	public @NonNull List<E> fetch() throws SqlException {
		return Collections.unmodifiableList(this.executeAndMap());
	}
	
	/**
	 * Executes this query and returns the first matching row as an entity.<br>
	 *
	 * @return An optional with the first mapped entity or an empty optional if no row matches
	 * @throws SqlException If an error occurs while executing the query
	 */
	public @NonNull Optional<E> fetchFirst() throws SqlException {
		List<E> results = this.limit(1).executeAndMap();
		return results.isEmpty() ? Optional.empty() : Optional.ofNullable(results.getFirst());
	}
	
	/**
	 * Executes this query and returns the single matching row as an entity.<br>
	 *
	 * @return The single mapped entity
	 * @throws SqlException If an error occurs while executing the query
	 * @throws SqlResultCountException If the query returns no rows or more than one row
	 */
	public @NonNull E fetchOne() throws SqlException {
		List<E> results = this.limit(2).executeAndMap();
		
		if (results.size() != 1) {
			throw new SqlResultCountException("Expected exactly one result but got " + (results.size() >= 2 ? "more than 1" : "0"), 1, results.size());
		}
		return results.getFirst();
	}
	
	/**
	 * Executes this query and returns the single matching row as an entity or {@code null} if no row matches.<br>
	 *
	 * @return The single mapped entity or {@code null} if no row matches
	 * @throws SqlException If an error occurs while executing the query
	 * @throws SqlResultCountException If the query returns more than one row
	 */
	public @Nullable E fetchOneOrNull() throws SqlException {
		List<E> results = this.limit(2).executeAndMap();
		
		if (results.size() > 1) {
			throw new SqlResultCountException("Expected at most one result but got more than 1", 1, results.size());
		}
		return results.isEmpty() ? null : results.getFirst();
	}
	
	/**
	 * Executes a {@code COUNT} query derived from this query and returns the number of matching rows.<br>
	 * The order by, limit and offset clauses are stripped before counting, and the query is wrapped in a subquery if it uses distinct, grouping, having or set operations.<br>
	 *
	 * @return The number of matching rows
	 * @throws SqlException If an error occurs while executing the query
	 */
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
	
	/**
	 * Executes this query limited to a single row and returns whether at least one row matches.<br>
	 *
	 * @return {@code true} if at least one row matches, {@code false} otherwise
	 * @throws SqlException If an error occurs while executing the query
	 */
	public boolean exists() throws SqlException {
		SqlDialect dialect = this.config.dialect();
		SqlSelectQueryConfig<E> core = this.config.withOrderByClauses(List.of()).withLimit(1);
		return SqlQueryExecutor.executeScalarQuery(dialect, this.config.connectionSource(), this.render(core, dialect, false), this.config.queryTimeout(), ResultSet::next);
	}
	
	/**
	 * Executes this query for the given page index and page size and returns the requested page of results.<br>
	 * One additional row beyond the page size is fetched internally to determine whether a next page exists.<br>
	 * The query must define an {@code ORDER BY} clause to guarantee deterministic pagination.<br>
	 *
	 * @param page The zero-based page index
	 * @param pageSize The number of rows per page
	 * @return The requested page of mapped entities
	 * @throws IllegalArgumentException If the page index is negative or the page size is not positive
	 * @throws SqlStatementBuilderException If the query has no order by clause
	 * @throws SqlException If an error occurs while executing the query
	 */
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
	
	/**
	 * Renders the given configuration into dialect specific sql.<br>
	 * Assembles the optional {@code WITH}, {@code SELECT}, {@code FROM}, join, {@code WHERE}, {@code GROUP BY}, {@code HAVING}, set operation, {@code ORDER BY}, limit/offset and lock clauses in order.<br>
	 *
	 * @param cfg The configuration to render
	 * @param dialect The sql dialect used to render the query
	 * @param countStar Whether to render a {@code COUNT(*)} projection and omit the order by, limit and offset clauses
	 * @return The rendered sql
	 * @throws NullPointerException If the configuration or dialect is null
	 * @throws SqlStatementBuilderException If skip locked is combined with the {@code FOR SHARE} lock mode
	 * @throws SqlException If an error occurs while rendering the query
	 */
	private @NonNull SqlRendered render(@NonNull SqlSelectQueryConfig<?> cfg, @NonNull SqlDialect dialect, boolean countStar) throws SqlException {
		Objects.requireNonNull(cfg, "Sql select query config must not be null");
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		
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
				SqlTable<?> table = cfg.table();
				String tableName = dialect.quoteIdentifier(table.name());
				
				List<? extends SqlColumn<?, ?>> columns = table.columns().stream().sorted(Comparator.comparingInt(SqlColumn::index)).toList();
				for (int i = 0; i < columns.size(); i++) {
					if (i > 0) {
						renderer.comma();
					}
					renderer.literal(tableName + "." + dialect.quoteIdentifier(columns.get(i).name()));
				}
				
				table.auditConfig().ifPresent(auditConfig -> {
					for (SqlAuditColumn auditColumn : auditConfig.auditColumns()) {
						renderer.comma().literal(tableName + "." + dialect.quoteIdentifier(auditColumn.name()));
					}
				});
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
		if (cfg.lockMode() != null) {
			SqlRendered lockHint = dialect.renderLockHint(cfg.lockMode(), cfg.skipLocked(), cfg.noWait());
			if (!lockHint.sql().isEmpty()) {
				renderer.rendered(lockHint);
			}
		}
		
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
			
			SqlRendered lockClause = dialect.renderLockClause(cfg.lockMode(), cfg.skipLocked(), cfg.noWait());
			if (!lockClause.sql().isEmpty()) {
				renderer.rendered(lockClause);
			}
		}
		return renderer.toSql();
	}
}
