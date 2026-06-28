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
import net.luis.utils.io.database.dialect.SqlFeature;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.SqlStatementBuilderException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectFeatureException;
import net.luis.utils.io.database.query.SqlJoinableQuery;
import net.luis.utils.io.database.query.SqlQuery;
import net.luis.utils.io.database.query.util.SqlJoinClause;
import net.luis.utils.io.database.query.util.SqlJoinType;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;

import java.sql.ResultSet;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * A query that deletes rows from a table.<br>
 * Supports join clauses and a where condition and can return the deleted rows on dialects that allow it.<br>
 * The query is immutable, every builder method returns a new instance leaving this query unchanged.<br>
 *
 * @see SqlJoinableQuery
 *
 * @author Luis-St
 *
 * @param <E> The type of the entities mapped from the deleted rows
 */
public class SqlDeleteQuery<E> implements SqlJoinableQuery<E> {
	
	/**
	 * The immutable configuration backing this delete query.
	 */
	private final SqlDeleteQueryConfig<E> config;
	
	/**
	 * Constructs a new delete query for the given table without any joins or where condition.<br>
	 *
	 * @param table The table to delete rows from
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the query execution
	 * @param rowMapper The row mapper used to map deleted rows to entities
	 * @throws NullPointerException If the table, dialect, connection source, query timeout or row mapper is null
	 */
	public SqlDeleteQuery(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper
	) {
		this(new SqlDeleteQueryConfig<>(table, dialect, connectionSource, queryTimeout, rowMapper, List.of(), null, false));
	}
	
	/**
	 * Constructs a new delete query from the given configuration.<br>
	 * @param config The configuration backing this query
	 * @throws NullPointerException If the config is null
	 */
	SqlDeleteQuery(@NonNull SqlDeleteQueryConfig<E> config) {
		this.config = Objects.requireNonNull(config, "Sql delete query config must not be null");
	}
	
	/**
	 * Creates a copy of this query with the given join clause appended.<br>
	 * @param join The join clause to append
	 * @return A new delete query with the additional join
	 */
	private @NonNull SqlDeleteQuery<E> withJoin(@NonNull SqlJoinClause join) {
		return new SqlDeleteQuery<>(this.config.withJoins(SqlQuery.copyAndAdd(this.config.joins(), join)));
	}
	
	@Override
	public @NonNull SqlDeleteQuery<E> innerJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return this.withJoin(new SqlJoinClause(SqlJoinType.INNER, table, on));
	}
	
	@Override
	public @NonNull SqlDeleteQuery<E> leftJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return this.withJoin(new SqlJoinClause(SqlJoinType.LEFT, table, on));
	}
	
	@Override
	public @NonNull SqlDeleteQuery<E> rightJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return this.withJoin(new SqlJoinClause(SqlJoinType.RIGHT, table, on));
	}
	
	@Override
	public @NonNull SqlDeleteQuery<E> fullJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return this.withJoin(new SqlJoinClause(SqlJoinType.FULL, table, on));
	}
	
	@Override
	public @NonNull SqlDeleteQuery<E> crossJoin(@NonNull SqlTable<?> table) {
		return this.withJoin(new SqlJoinClause(SqlJoinType.CROSS, table, null));
	}
	
	/**
	 * Adds a where condition restricting which rows are deleted.<br>
	 * If a where condition is already present, both are combined using a logical conjunction.<br>
	 *
	 * @param condition The condition to add
	 * @return A new delete query with the added where condition
	 * @throws NullPointerException If the condition is null
	 */
	public @NonNull SqlDeleteQuery<E> where(@NonNull SqlCondition condition) {
		Objects.requireNonNull(condition, "Sql where condition must not be null");
		
		SqlCondition existing = this.config.whereCondition();
		SqlCondition combined = existing != null ? SqlCondition.allOf(existing, condition) : condition;
		return new SqlDeleteQuery<>(this.config.withWhereCondition(combined));
	}
	
	/**
	 * Confirms that this query may delete all rows even without a where condition.<br>
	 * @return A new delete query that is allowed to delete all rows
	 */
	public @NonNull SqlDeleteQuery<E> allowAll() {
		return new SqlDeleteQuery<>(this.config.withAllowAll());
	}
	
	/**
	 * Executes this delete query and returns the number of affected rows.<br>
	 *
	 * @return The number of deleted rows
	 * @throws SqlStatementBuilderException If the query has no where condition and {@link #allowAll()} was not called
	 * @throws SqlException If an error occurs while executing the query
	 */
	public int execute() throws SqlException {
		if (this.config.whereCondition() == null && !this.config.allowAll()) {
			throw new SqlStatementBuilderException("DELETE without WHERE clause would affect all rows, call allowAll() to confirm this is intentional");
		}
		return SqlQueryExecutor.executeUpdate(this.config.dialect(), this.config.connectionSource(), this.toSql(this.config.dialect()), this.config.queryTimeout());
	}
	
	/**
	 * Executes this delete query and returns the deleted rows mapped to entities.<br>
	 *
	 * @return The list of deleted entities
	 * @throws SqlException If an error occurs while executing the query
	 */
	public @NonNull List<E> returning() throws SqlException {
		return SqlQueryExecutor.executeReturningQuery(
			this.config.dialect(),
			this.config.connectionSource(),
			this.toSql(this.config.dialect()),
			this.config.dialect().renderReturning(List.copyOf(this.config.table().columns())),
			this.config.queryTimeout(),
			this.config.rowMapper()
		);
	}
	
	@Override
	public @NonNull SqlRendered toSql(@NonNull SqlDialect dialect) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		if (!this.config.joins().isEmpty() && !dialect.isFeatureSupported(SqlFeature.JOINED_DML)) {
			throw new SqlDialectFeatureException(SqlFeature.JOINED_DML, dialect);
		}
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.delete().from().literal(dialect.quoteIdentifier(this.config.table().name()));
		for (SqlJoinClause join : this.config.joins()) {
			renderer.rendered(join.toSql(dialect));
		}
		
		if (this.config.whereCondition() != null) {
			renderer.where().rendered(dialect.renderCondition(this.config.whereCondition()));
		}
		return renderer.toSql();
	}
}
