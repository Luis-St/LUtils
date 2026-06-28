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
import net.luis.utils.io.database.SqlConnectionSource;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.SqlFeature;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.SqlStatementBuilderException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectFeatureException;
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

import java.sql.ResultSet;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * A query that updates rows in a table.<br>
 * Column assignments are added through the {@code set}, {@code increment} and {@code decrement} methods and can be
 * restricted to specific rows using a where condition.<br>
 * The query is immutable, every builder method returns a new instance leaving this query unchanged.<br>
 *
 * @see SqlJoinableQuery
 *
 * @author Luis-St
 *
 * @param <E> The type of the entities mapped from the affected rows
 */
public class SqlUpdateQuery<E> implements SqlJoinableQuery<E> {
	
	/**
	 * The immutable configuration backing this update query.
	 */
	private final SqlUpdateQueryConfig<E> config;
	
	/**
	 * Constructs a new update query for the given table without any set clauses, joins or where condition.<br>
	 *
	 * @param table The table to update rows in
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the query execution
	 * @param rowMapper The row mapper used to map updated rows to entities
	 * @throws NullPointerException If the table, dialect, connection source, query timeout or row mapper is null
	 */
	public SqlUpdateQuery(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper
	) {
		this(new SqlUpdateQueryConfig<>(table, dialect, connectionSource, queryTimeout, rowMapper));
	}
	
	/**
	 * Constructs a new update query from the given configuration.<br>
	 *
	 * @param config The configuration backing this query
	 * @throws NullPointerException If the config is null
	 */
	SqlUpdateQuery(@NonNull SqlUpdateQueryConfig<E> config) {
		this.config = Objects.requireNonNull(config, "Sql update query config must not be null");
	}
	
	/**
	 * Creates a copy of this query with the given join clause appended.<br>
	 *
	 * @param join The join clause to append
	 * @return A new update query with the additional join
	 */
	private @NonNull SqlUpdateQuery<E> withJoin(@NonNull SqlJoinClause join) {
		return new SqlUpdateQuery<>(this.config.withJoins(SqlQuery.copyAndAdd(this.config.joins(), join)));
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
	
	/**
	 * Adds a set clause that assigns {@code null} to the given column.<br>
	 *
	 * @param column The column to set to {@code null}
	 * @return A new update query with the added set clause
	 * @param <V> The value type of the column
	 * @throws NullPointerException If the column is null
	 */
	public <V> @NonNull SqlUpdateQuery<E> setNull(@NonNull SqlColumn<E, V> column) {
		Objects.requireNonNull(column, "Sql column must not be null");
		
		return new SqlUpdateQuery<>(this.config.withSetClauses(SqlQuery.copyAndAdd(this.config.setClauses(), new SqlSetClause<>(column, null, SqlSetType.NULL))));
	}
	
	/**
	 * Adds a set clause that assigns the given value to the column.<br>
	 * If the value is {@code null}, the column is set to {@code null} as if {@link #setNull(SqlColumn)} was called.<br>
	 *
	 * @param column The column to assign
	 * @param value The value to assign, or {@code null} to set the column to {@code null}
	 * @return A new update query with the added set clause
	 * @param <V> The value type of the column
	 * @throws NullPointerException If the column is null
	 */
	public <V> @NonNull SqlUpdateQuery<E> set(@NonNull SqlColumn<E, V> column, @Nullable V value) {
		Objects.requireNonNull(column, "Sql column must not be null");
		
		return value == null ? this.setNull(column) : this.set(column, Sql.of(value, column.type()));
	}
	
	/**
	 * Adds a set clause that assigns the given expression to the column.<br>
	 *
	 * @param column The column to assign
	 * @param expression The expression to assign to the column
	 * @return A new update query with the added set clause
	 * @param <V> The value type of the column
	 * @throws NullPointerException If the column or expression is null
	 */
	public <V> @NonNull SqlUpdateQuery<E> set(@NonNull SqlColumn<E, V> column, @NonNull SqlExpression<V> expression) {
		return new SqlUpdateQuery<>(this.config.withSetClauses(SqlQuery.copyAndAdd(this.config.setClauses(), new SqlSetClause<>(column, expression, SqlSetType.EXPRESSION))));
	}
	
	/**
	 * Adds a set clause that increments the column by the given numeric value.<br>
	 *
	 * @param column The numeric column to increment
	 * @param incrementBy The value to add to the column
	 * @return A new update query with the added set clause
	 * @param <V> The numeric value type of the column
	 * @throws NullPointerException If the column is null
	 */
	public <V extends Number> @NonNull SqlUpdateQuery<E> increment(@NonNull SqlColumn<E, V> column, @NonNull V incrementBy) {
		Objects.requireNonNull(column, "Sql column must not be null");
		
		return this.increment(column, Sql.of(incrementBy, column.type()));
	}
	
	/**
	 * Adds a set clause that increments the column by the given numeric expression.<br>
	 *
	 * @param column The numeric column to increment
	 * @param incrementByExpression The expression to add to the column
	 * @return A new update query with the added set clause
	 * @param <V> The numeric value type of the column
	 * @throws NullPointerException If the column or expression is null
	 */
	public <V extends Number> @NonNull SqlUpdateQuery<E> increment(@NonNull SqlColumn<E, V> column, @NonNull SqlExpression<V> incrementByExpression) {
		return new SqlUpdateQuery<>(this.config.withSetClauses(SqlQuery.copyAndAdd(this.config.setClauses(), new SqlSetClause<>(column, incrementByExpression, SqlSetType.INCREMENT))));
	}
	
	/**
	 * Adds a set clause that decrements the column by the given numeric value.<br>
	 *
	 * @param column The numeric column to decrement
	 * @param decrementBy The value to subtract from the column
	 * @return A new update query with the added set clause
	 * @param <V> The numeric value type of the column
	 * @throws NullPointerException If the column is null
	 */
	public <V extends Number> @NonNull SqlUpdateQuery<E> decrement(@NonNull SqlColumn<E, V> column, @NonNull V decrementBy) {
		Objects.requireNonNull(column, "Sql column must not be null");
		
		return this.decrement(column, Sql.of(decrementBy, column.type()));
	}
	
	/**
	 * Adds a set clause that decrements the column by the given numeric expression.<br>
	 *
	 * @param column The numeric column to decrement
	 * @param decrementByExpression The expression to subtract from the column
	 * @return A new update query with the added set clause
	 * @param <V> The numeric value type of the column
	 * @throws NullPointerException If the column or expression is null
	 */
	public <V extends Number> @NonNull SqlUpdateQuery<E> decrement(@NonNull SqlColumn<E, V> column, @NonNull SqlExpression<V> decrementByExpression) {
		return new SqlUpdateQuery<>(this.config.withSetClauses(SqlQuery.copyAndAdd(this.config.setClauses(), new SqlSetClause<>(column, decrementByExpression, SqlSetType.DECREMENT))));
	}
	
	/**
	 * Adds a where condition restricting which rows are updated.<br>
	 * If a where condition is already present, both are combined using a logical conjunction.<br>
	 *
	 * @param condition The condition to add
	 * @return A new update query with the added where condition
	 * @throws NullPointerException If the condition is null
	 */
	public @NonNull SqlUpdateQuery<E> where(@NonNull SqlCondition condition) {
		Objects.requireNonNull(condition, "Sql where condition must not be null");
		
		SqlCondition existing = this.config.whereCondition();
		SqlCondition combined = existing != null ? SqlCondition.allOf(existing, condition) : condition;
		return new SqlUpdateQuery<>(this.config.withWhereCondition(combined));
	}
	
	/**
	 * Confirms that this query may update all rows even without a where condition.<br>
	 * @return A new update query that is allowed to update all rows
	 */
	public @NonNull SqlUpdateQuery<E> allowAll() {
		return new SqlUpdateQuery<>(this.config.withAllowAll());
	}
	
	/**
	 * Executes this update query and returns the number of affected rows.<br>
	 *
	 * @return The number of updated rows
	 * @throws SqlStatementBuilderException If the query has no where condition and {@link #allowAll()} was not called
	 * @throws SqlException If an error occurs while executing the query
	 */
	public int execute() throws SqlException {
		if (this.config.whereCondition() == null && !this.config.allowAll()) {
			throw new SqlStatementBuilderException("UPDATE without WHERE clause would affect all rows, call allowAll() to confirm this is intentional");
		}
		return SqlQueryExecutor.executeUpdate(this.config.dialect(), this.config.connectionSource(), this.toSql(this.config.dialect()), this.config.queryTimeout());
	}
	
	/**
	 * Executes this update query and returns the updated rows mapped to entities.<br>
	 *
	 * @return The list of updated entities
	 * @throws SqlDialectFeatureException If the dialect does not support returning rows from an update
	 * @throws SqlException If an error occurs while executing the query
	 */
	public @NonNull List<E> returning() throws SqlException {
		if (!this.config.dialect().isFeatureSupported(SqlFeature.UPDATE_RETURNING)) {
			throw new SqlDialectFeatureException(SqlFeature.UPDATE_RETURNING, this.config.dialect());
		}
		
		return SqlQueryExecutor.executeReturningQuery(
			this.config.dialect(), this.config.connectionSource(), this.toSql(this.config.dialect()), this.config.dialect().renderReturning(List.copyOf(this.config.table().columns())), this.config.queryTimeout(), this.config.rowMapper()
		);
	}
	
	@Override
	public @NonNull SqlRendered toSql(@NonNull SqlDialect dialect) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		if (this.config.setClauses().isEmpty()) {
			throw new SqlStatementBuilderException("Sql update query must have at least one SET clause");
		}
		if (!this.config.joins().isEmpty() && !dialect.isFeatureSupported(SqlFeature.JOINED_DML)) {
			throw new SqlDialectFeatureException(SqlFeature.JOINED_DML, dialect);
		}
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.update().literal(dialect.quoteIdentifier(this.config.table().name()));
		for (SqlJoinClause join : this.config.joins()) {
			renderer.rendered(join.toSql(dialect));
		}
		
		renderer.set();
		for (int i = 0; i < this.config.setClauses().size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			
			renderer.rendered(this.config.setClauses().get(i).toSql(dialect));
		}
		
		if (this.config.whereCondition() != null) {
			renderer.where().rendered(dialect.renderCondition(this.config.whereCondition()));
		}
		return renderer.toSql();
	}
}
