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
import net.luis.utils.io.database.Sql;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.query.SqlJoinableQuery;
import net.luis.utils.io.database.query.SqlQuery;
import net.luis.utils.io.database.query.util.*;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlColumn;
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

public class SqlUpdateQuery<E> implements SqlJoinableQuery<E> {
	
	private final SqlTable<E> table;
	private final SqlDialect dialect;
	private final Connection connection;
	private final Duration queryTimeout;
	private final ThrowableFunction<ResultSet, E, SqlException> rowMapper;
	private final List<SqlSetClause<E, ?>> setClauses;
	private final List<SqlJoinClause> joins;
	private final @Nullable SqlCondition whereCondition;
	private final boolean allowAll;
	
	public SqlUpdateQuery(@NonNull SqlTable<E> table, @NonNull SqlDialect dialect, @NonNull Connection connection, @NonNull Duration queryTimeout, @NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper) {
		this(table, dialect, connection, queryTimeout, rowMapper, List.of(), List.of(), null, false);
	}
	
	private SqlUpdateQuery(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull Connection connection,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull List<SqlSetClause<E, ?>> setClauses,
		@NonNull List<SqlJoinClause> joins,
		@Nullable SqlCondition whereCondition,
		boolean allowAll
	) {
		this.table = Objects.requireNonNull(table, "Sql table must not be null");
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
		this.connection = Objects.requireNonNull(connection, "Connection must not be null");
		this.queryTimeout = Objects.requireNonNull(queryTimeout, "Query timeout must not be null");
		this.rowMapper = Objects.requireNonNull(rowMapper, "Row mapper must not be null");
		this.setClauses = Objects.requireNonNull(setClauses, "Sql set clauses must not be null");
		this.joins = Objects.requireNonNull(joins, "Sql join clauses must not be null");
		this.whereCondition = whereCondition;
		this.allowAll = allowAll;
	}
	
	private @NonNull SqlUpdateQuery<E> withJoin(@NonNull SqlJoinClause join) {
		return new SqlUpdateQuery<>(
			this.table,
			this.dialect,
			this.connection,
			this.queryTimeout,
			this.rowMapper,
			this.setClauses,
			SqlQuery.copyAndAdd(this.joins, join),
			this.whereCondition,
			this.allowAll
		);
	}
	
	@Override
	public @NonNull SqlUpdateQuery<E> innerJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return this.withJoin(new SqlJoinClause(SqlJoinType.INNER, table, on));
	}
	
	@Override
	public @NonNull SqlUpdateQuery<E> leftJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return this.withJoin(new SqlJoinClause(SqlJoinType.LEFT, table, on));
	}
	
	@Override
	public @NonNull SqlUpdateQuery<E> rightJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return this.withJoin(new SqlJoinClause(SqlJoinType.RIGHT, table, on));
	}
	
	@Override
	public @NonNull SqlUpdateQuery<E> fullJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on) {
		return this.withJoin(new SqlJoinClause(SqlJoinType.FULL, table, on));
	}
	
	@Override
	public @NonNull SqlUpdateQuery<E> crossJoin(@NonNull SqlTable<?> table) {
		return this.withJoin(new SqlJoinClause(SqlJoinType.CROSS, table, null));
	}
	
	public <V> @NonNull SqlUpdateQuery<E> set(@NonNull SqlColumn<E, V> column, @NonNull V value) {
		Objects.requireNonNull(column, "Sql column must not be null");
		
		return this.set(column, Sql.of(value, column.getType()));
	}
	
	public <V> @NonNull SqlUpdateQuery<E> set(@NonNull SqlColumn<E, V> column, @NonNull SqlExpression<V> expression) {
		return new SqlUpdateQuery<>(
			this.table,
			this.dialect,
			this.connection,
			this.queryTimeout,
			this.rowMapper,
			SqlQuery.copyAndAdd(this.setClauses, new SqlSetClause<>(column, expression, SqlSetType.EXPRESSION)),
			this.joins,
			this.whereCondition,
			this.allowAll
		);
	}
	
	public <V extends Number> @NonNull SqlUpdateQuery<E> increment(@NonNull SqlColumn<E, V> column, @NonNull V incrementBy) {
		Objects.requireNonNull(column, "Sql column must not be null");
		
		return this.increment(column, Sql.of(incrementBy, column.getType()));
	}
	
	public <V extends Number> @NonNull SqlUpdateQuery<E> increment(@NonNull SqlColumn<E, V> column, @NonNull SqlExpression<V> incrementByExpression) {
		return new SqlUpdateQuery<>(
			this.table,
			this.dialect,
			this.connection,
			this.queryTimeout,
			this.rowMapper,
			SqlQuery.copyAndAdd(this.setClauses, new SqlSetClause<>(column, incrementByExpression, SqlSetType.INCREMENT)),
			this.joins,
			this.whereCondition,
			this.allowAll
		);
	}
	
	public <V extends Number> @NonNull SqlUpdateQuery<E> decrement(@NonNull SqlColumn<E, V> column, @NonNull V decrementBy) {
		Objects.requireNonNull(column, "Sql column must not be null");
		
		return this.decrement(column, Sql.of(decrementBy, column.getType()));
	}
	
	public <V extends Number> @NonNull SqlUpdateQuery<E> decrement(@NonNull SqlColumn<E, V> column, @NonNull SqlExpression<V> decrementByExpression) {
		return new SqlUpdateQuery<>(
			this.table,
			this.dialect,
			this.connection,
			this.queryTimeout,
			this.rowMapper,
			SqlQuery.copyAndAdd(this.setClauses, new SqlSetClause<>(column, decrementByExpression, SqlSetType.DECREMENT)),
			this.joins,
			this.whereCondition,
			this.allowAll
		);
	}
	
	public @NonNull SqlUpdateQuery<E> where(@NonNull SqlCondition condition) {
		Objects.requireNonNull(condition, "Sql where condition must not be null");
		SqlCondition combined = this.whereCondition != null ? SqlCondition.allOf(this.whereCondition, condition) : condition;
		return new SqlUpdateQuery<>(
			this.table,
			this.dialect,
			this.connection,
			this.queryTimeout,
			this.rowMapper,
			this.setClauses,
			this.joins,
			combined,
			this.allowAll
		);
	}
	
	public @NonNull SqlUpdateQuery<E> allowAll() {
		return new SqlUpdateQuery<>(
			this.table,
			this.dialect,
			this.connection,
			this.queryTimeout,
			this.rowMapper,
			this.setClauses,
			this.joins,
			this.whereCondition,
			true
		);
	}
	
	public int execute() throws SqlException {
		if (this.whereCondition == null && !this.allowAll) {
			throw new SqlException("UPDATE without WHERE clause would affect all rows; call allowAll() to confirm this is intentional");
		}
		return SqlQueryExecutor.executeUpdate(this.dialect, this.connection, this.toSql(this.dialect), this.queryTimeout);
	}
	
	public @NonNull List<E> returning() throws SqlException {
		return SqlQueryExecutor.executeReturningQuery(
			this.dialect, this.connection, this.toSql(this.dialect), this.dialect.renderReturning(List.copyOf(this.table.getColumns())), this.queryTimeout, this.rowMapper
		);
	}
	
	@Override
	public @NonNull SqlRendered toSql(@NonNull SqlDialect dialect) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		if (this.setClauses.isEmpty()) {
			throw new SqlException("Sql update query must have at least one SET clause");
		}
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.update().literal(dialect.quoteIdentifier(this.table.getName()));
		for (SqlJoinClause join : this.joins) {
			renderer.rendered(join.toSql(dialect));
		}
		
		renderer.set();
		for (int i = 0; i < this.setClauses.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			
			renderer.rendered(this.setClauses.get(i).toSql(dialect));
		}
		
		if (this.whereCondition != null) {
			renderer.where().rendered(dialect.renderCondition(this.whereCondition));
		}
		return renderer.toSql();
	}
}
