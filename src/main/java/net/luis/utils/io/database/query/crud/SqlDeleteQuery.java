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
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.query.SqlJoinableQuery;
import net.luis.utils.io.database.query.SqlQuery;
import net.luis.utils.io.database.query.util.SqlJoinClause;
import net.luis.utils.io.database.query.util.SqlJoinType;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class SqlDeleteQuery<E> implements SqlJoinableQuery<E> {
	
	private final SqlTable<E> table;
	private final SqlDialect dialect;
	private final Connection connection;
	private final Duration queryTimeout;
	private final ThrowableFunction<ResultSet, E, SqlException> rowMapper;
	private final List<SqlJoinClause> joins;
	private final @Nullable SqlCondition whereCondition;
	private final boolean allowAll;
	
	public SqlDeleteQuery(@NonNull SqlTable<E> table, @NonNull SqlDialect dialect, @NonNull Connection connection, @NonNull Duration queryTimeout, @NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper) {
		this(table, dialect, connection, queryTimeout, rowMapper, List.of(), null, false);
	}
	
	private SqlDeleteQuery(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull Connection connection,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull List<SqlJoinClause> joins,
		@Nullable SqlCondition whereCondition,
		boolean allowAll
	) {
		this.table = Objects.requireNonNull(table, "Sql table must not be null");
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
		this.connection = Objects.requireNonNull(connection, "Connection must not be null");
		this.queryTimeout = Objects.requireNonNull(queryTimeout, "Query timeout must not be null");
		this.rowMapper = Objects.requireNonNull(rowMapper, "Row mapper must not be null");
		this.joins = Objects.requireNonNull(joins, "Sql join clauses must not be null");
		this.whereCondition = whereCondition;
		this.allowAll = allowAll;
	}
	
	private @NonNull SqlDeleteQuery<E> withJoin(@NonNull SqlJoinClause join) {
		return new SqlDeleteQuery<>(
			this.table,
			this.dialect,
			this.connection,
			this.queryTimeout,
			this.rowMapper,
			SqlQuery.copyAndAdd(this.joins, join),
			this.whereCondition,
			this.allowAll
		);
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
	
	public @NonNull SqlDeleteQuery<E> where(@NonNull SqlCondition condition) {
		Objects.requireNonNull(condition, "Sql where condition must not be null");
		
		SqlCondition combined = this.whereCondition != null ? SqlCondition.allOf(this.whereCondition, condition) : condition;
		return new SqlDeleteQuery<>(
			this.table,
			this.dialect,
			this.connection,
			this.queryTimeout,
			this.rowMapper,
			this.joins,
			combined,
			this.allowAll
		);
	}
	
	public @NonNull SqlDeleteQuery<E> allowAll() {
		return new SqlDeleteQuery<>(
			this.table,
			this.dialect,
			this.connection,
			this.queryTimeout,
			this.rowMapper,
			this.joins,
			this.whereCondition,
			true
		);
	}
	
	public int execute() throws SqlException {
		if (this.whereCondition == null && !this.allowAll) {
			throw new SqlException("DELETE without WHERE clause would affect all rows; call allowAll() to confirm this is intentional");
		}
		return SqlQueryExecutor.executeUpdate(this.dialect, this.connection, this.toSql(this.dialect), this.queryTimeout);
	}
	
	public @NonNull List<E> returning() throws SqlException {
		return SqlQueryExecutor.executeReturningQuery(
			this.dialect, this.connection, this.toSql(this.dialect), this.dialect.renderReturning(List.copyOf(this.table.columns())), this.queryTimeout, this.rowMapper
		);
	}
	
	@Override
	public @NonNull SqlRendered toSql(@NonNull SqlDialect dialect) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.delete().from().literal(dialect.quoteIdentifier(this.table.name()));
		for (SqlJoinClause join : this.joins) {
			renderer.rendered(join.toSql(dialect));
		}
		
		if (this.whereCondition != null) {
			renderer.where().rendered(dialect.renderCondition(this.whereCondition));
		}
		return renderer.toSql();
	}
}
