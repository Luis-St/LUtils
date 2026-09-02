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
import net.luis.utils.io.database.audit.*;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.SqlFeature;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.SqlStatementBuilderException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.functions.temporal.SqlCurrentTimestampFunction;
import net.luis.utils.io.database.query.SqlQuery;
import net.luis.utils.io.database.query.util.*;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.ResultSet;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * A builder for sql {@code INSERT} statements that can be executed against a database.<br>
 * Supports plain inserts, insert-or-ignore, upsert and insert-from-select via dedicated static factory methods.<br>
 * Large batches are automatically split into multiple statements based on the bind parameter limit of the dialect.<br>
 * If the target table is audited, the configured audit columns are populated automatically on insert.<br>
 *
 * @see SqlQuery
 * @see SqlInsertQueryConfig
 *
 * @author Luis-St
 *
 * @param <E> The type of the entities to insert
 */
public class SqlInsertQuery<E> implements SqlQuery<E> {
	
	/**
	 * The immutable configuration that holds the entities and settings of this insert query.
	 */
	private final SqlInsertQueryConfig<E> config;
	
	/**
	 * Constructs a new insert query for the given entities without an audit user provider.<br>
	 *
	 * @param table The table to insert into
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @param entities The entities to insert
	 * @throws NullPointerException If any of the arguments is null
	 * @throws IllegalArgumentException If the entities list is empty
	 * @throws SqlStatementBuilderException If the insert statement could not be built for the given arguments
	 */
	public SqlInsertQuery(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull List<E> entities
	) throws SqlStatementBuilderException {
		this(table, dialect, connectionSource, queryTimeout, rowMapper, entities, null);
	}
	
	/**
	 * Constructs a new insert query for the given entities using the given audit user provider.<br>
	 * The audit user provider supplies the user recorded in the audit columns of an audited table.<br>
	 *
	 * @param table The table to insert into
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @param entities The entities to insert
	 * @param auditUserProvider The provider that resolves the audit user, or {@code null} for no audit user
	 * @throws NullPointerException If any argument except the audit user provider is null
	 * @throws IllegalArgumentException If the entities list is empty
	 * @throws SqlStatementBuilderException If the insert statement could not be built for the given arguments
	 */
	public SqlInsertQuery(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull List<E> entities,
		@Nullable SqlAuditUserProvider auditUserProvider
	) throws SqlStatementBuilderException {
		Objects.requireNonNull(entities, "Entities must not be null");
		
		this(SqlInsertQueryConfig.create(
			table, dialect, connectionSource, queryTimeout, rowMapper, List.copyOf(entities), null, null, null, false, false, false, auditUserProvider
		));
	}
	
	/**
	 * Constructs a new insert query that wraps the given configuration.<br>
	 *
	 * @param config The configuration that holds the entities and settings of the query
	 * @throws NullPointerException If the configuration is null
	 */
	SqlInsertQuery(@NonNull SqlInsertQueryConfig<E> config) {
		this.config = Objects.requireNonNull(config, "Sql insert query config must not be null");
	}
	
	/**
	 * Creates a new insert query that ignores rows conflicting on the given columns instead of failing.<br>
	 *
	 * @param table The table to insert into
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @param entities The entities to insert
	 * @param conflictColumns The columns that define a conflict to be ignored
	 * @param <E> The type of the entities to insert
	 * @return The newly created insert-or-ignore query
	 * @throws NullPointerException If any of the arguments is null
	 * @throws IllegalArgumentException If the entities list is empty
	 * @throws SqlStatementBuilderException If the conflict columns are empty
	 */
	public static <E> @NonNull SqlInsertQuery<E> insertOrIgnore(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull List<E> entities,
		@NonNull List<SqlColumn<E, ?>> conflictColumns
	) throws SqlStatementBuilderException {
		Objects.requireNonNull(entities, "List of entities must not be null");
		Objects.requireNonNull(conflictColumns, "Sql conflict columns must not be null");
		
		return new SqlInsertQuery<>(SqlInsertQueryConfig.create(
			table, dialect, connectionSource, queryTimeout, rowMapper, List.copyOf(entities), null, List.copyOf(conflictColumns), null, false, true, false, null
		));
	}
	
	/**
	 * Creates a new upsert query that updates the existing row instead of failing on a conflict on the given column.<br>
	 * Every non-conflict column is updated with its proposed value.<br>
	 *
	 * @param table The table to insert into
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @param entities The entities to insert or update
	 * @param conflictColumn The column that defines a conflict triggering the update
	 * @param <E> The type of the entities to insert
	 * @return The newly created upsert query
	 * @throws NullPointerException If any of the arguments is null
	 * @throws IllegalArgumentException If the entities list is empty
	 * @throws SqlStatementBuilderException If the conflict column is null
	 */
	public static <E> @NonNull SqlInsertQuery<E> upsert(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull List<E> entities,
		@NonNull SqlColumn<E, ?> conflictColumn
	) throws SqlStatementBuilderException {
		Objects.requireNonNull(conflictColumn, "Sql conflict column must not be null");
		
		return upsert(table, dialect, connectionSource, queryTimeout, rowMapper, entities, List.of(conflictColumn));
	}
	
	/**
	 * Creates a new upsert query that updates the existing row instead of failing on a conflict on the given columns.<br>
	 * Supports a composite conflict key. Every non-conflict column is updated with its proposed value.<br>
	 *
	 * @param table The table to insert into
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @param entities The entities to insert or update
	 * @param conflictColumns The columns that define a conflict triggering the update
	 * @param <E> The type of the entities to insert
	 * @return The newly created upsert query
	 * @throws NullPointerException If any of the arguments is null
	 * @throws IllegalArgumentException If the entities list is empty
	 * @throws SqlStatementBuilderException If the conflict columns are empty
	 */
	public static <E> @NonNull SqlInsertQuery<E> upsert(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull List<E> entities,
		@NonNull List<SqlColumn<E, ?>> conflictColumns
	) throws SqlStatementBuilderException {
		Objects.requireNonNull(entities, "List of entities must not be null");
		Objects.requireNonNull(conflictColumns, "Sql conflict columns must not be null");
		
		return new SqlInsertQuery<>(SqlInsertQueryConfig.create(
			table, dialect, connectionSource, queryTimeout, rowMapper, List.copyOf(entities), null, List.copyOf(conflictColumns), null, true, false, false, null
		));
	}
	
	/**
	 * Creates a new upsert query that updates the existing row using custom set clauses instead of failing on a conflict on the
	 * given columns.<br>
	 * Use {@link SqlColumn#of(net.luis.utils.io.database.query.SqlAlias) column.of(SqlAlias.EXCLUDED)} (or the dialect-specific
	 * equivalent obtained through {@link SqlDialect#upsertExcludedValue(SqlColumn)}) to reference the proposed row's value
	 * within an update clause expression, e.g. to build {@code col = LEAST(table.col, EXCLUDED.col)}.<br>
	 *
	 * @param table The table to insert into
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @param entities The entities to insert or update
	 * @param conflictColumns The columns that define a conflict triggering the update
	 * @param updateClauses The set clauses to apply on conflict
	 * @param <E> The type of the entities to insert
	 * @return The newly created upsert query
	 * @throws NullPointerException If any of the arguments is null
	 * @throws IllegalArgumentException If the entities list is empty
	 * @throws SqlStatementBuilderException If the conflict columns are empty
	 */
	public static <E> @NonNull SqlInsertQuery<E> upsert(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull List<E> entities,
		@NonNull List<SqlColumn<E, ?>> conflictColumns,
		@NonNull List<SqlSetClause<E, ?>> updateClauses
	) throws SqlStatementBuilderException {
		Objects.requireNonNull(entities, "List of entities must not be null");
		Objects.requireNonNull(conflictColumns, "Sql conflict columns must not be null");
		Objects.requireNonNull(updateClauses, "Sql update clauses must not be null");
		
		return new SqlInsertQuery<>(SqlInsertQueryConfig.create(
			table, dialect, connectionSource, queryTimeout, rowMapper, List.copyOf(entities), null, List.copyOf(conflictColumns), List.copyOf(updateClauses), true, false, false, null
		));
	}
	
	/**
	 * Creates a new insert query that inserts the rows produced by the given select query.<br>
	 * No entities are required since the inserted rows are derived from the result of the select query.<br>
	 *
	 * @param table The table to insert into
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @param fromSelect The select query that produces the rows to insert
	 * @param <E> The type of the entities to insert
	 * @return The newly created insert-from-select query
	 * @throws NullPointerException If any of the arguments is null
	 * @throws SqlStatementBuilderException If the select query is null
	 */
	public static <E> @NonNull SqlInsertQuery<E> insertFromSelect(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull SqlSelectQuery<?> fromSelect
	) throws SqlStatementBuilderException {
		return new SqlInsertQuery<>(SqlInsertQueryConfig.create(
			table, dialect, connectionSource, queryTimeout, rowMapper, List.of(), fromSelect, null, null, false, false, true, null
		));
	}
	
	/**
	 * Creates a new column-value insert builder that is not driven by an entity.<br>
	 * Columns and values are supplied per row through {@link SqlInsertColumnsBuilder#row}, allowing a subset of the table's
	 * columns to be inserted explicitly, e.g. to accommodate auto-increment or defaulted columns without an entity instance.<br>
	 * This untyped variant accepts any number of columns per row; for compile-time checked rows of up to five columns, use one
	 * of the {@link #columns} overloads instead.<br>
	 *
	 * @param table The table to insert into
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @param <E> The type of the entity mapped from a row of the table
	 * @return The newly created column-value insert builder
	 * @throws NullPointerException If any of the arguments is null
	 */
	public static <E> @NonNull SqlInsertColumnsBuilder<E> insertColumns(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper
	) {
		return new SqlInsertColumnsBuilder<>(table, dialect, connectionSource, queryTimeout, rowMapper, null, List.of());
	}
	
	/**
	 * Creates a new compile-time checked, single-column insert builder that is not driven by an entity.<br>
	 *
	 * @param table The table to insert into
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @param column1 The first column of every row
	 * @param <E> The type of the entity mapped from a row of the table
	 * @param <T1> The type of the first column
	 * @return The newly created column-value insert builder
	 * @throws NullPointerException If any of the arguments is null
	 */
	public static <E, T1> @NonNull SqlInsertColumns1<E, T1> columns(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull SqlColumn<E, T1> column1
	) {
		return new SqlInsertColumns1<>(insertColumns(table, dialect, connectionSource, queryTimeout, rowMapper), column1);
	}
	
	/**
	 * Creates a new compile-time checked, two-column insert builder that is not driven by an entity.<br>
	 *
	 * @param table The table to insert into
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @param column1 The first column of every row
	 * @param column2 The second column of every row
	 * @param <E> The type of the entity mapped from a row of the table
	 * @param <T1> The type of the first column
	 * @param <T2> The type of the second column
	 * @return The newly created column-value insert builder
	 * @throws NullPointerException If any of the arguments is null
	 */
	public static <E, T1, T2> @NonNull SqlInsertColumns2<E, T1, T2> columns(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull SqlColumn<E, T1> column1,
		@NonNull SqlColumn<E, T2> column2
	) {
		return new SqlInsertColumns2<>(insertColumns(table, dialect, connectionSource, queryTimeout, rowMapper), column1, column2);
	}
	
	/**
	 * Creates a new compile-time checked, three-column insert builder that is not driven by an entity.<br>
	 *
	 * @param table The table to insert into
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @param column1 The first column of every row
	 * @param column2 The second column of every row
	 * @param column3 The third column of every row
	 * @param <E> The type of the entity mapped from a row of the table
	 * @param <T1> The type of the first column
	 * @param <T2> The type of the second column
	 * @param <T3> The type of the third column
	 * @return The newly created column-value insert builder
	 * @throws NullPointerException If any of the arguments is null
	 */
	public static <E, T1, T2, T3> @NonNull SqlInsertColumns3<E, T1, T2, T3> columns(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull SqlColumn<E, T1> column1,
		@NonNull SqlColumn<E, T2> column2,
		@NonNull SqlColumn<E, T3> column3
	) {
		return new SqlInsertColumns3<>(insertColumns(table, dialect, connectionSource, queryTimeout, rowMapper), column1, column2, column3);
	}
	
	/**
	 * Creates a new compile-time checked, four-column insert builder that is not driven by an entity.<br>
	 *
	 * @param table The table to insert into
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @param column1 The first column of every row
	 * @param column2 The second column of every row
	 * @param column3 The third column of every row
	 * @param column4 The fourth column of every row
	 * @param <E> The type of the entity mapped from a row of the table
	 * @param <T1> The type of the first column
	 * @param <T2> The type of the second column
	 * @param <T3> The type of the third column
	 * @param <T4> The type of the fourth column
	 * @return The newly created column-value insert builder
	 * @throws NullPointerException If any of the arguments is null
	 */
	public static <E, T1, T2, T3, T4> @NonNull SqlInsertColumns4<E, T1, T2, T3, T4> columns(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull SqlColumn<E, T1> column1,
		@NonNull SqlColumn<E, T2> column2,
		@NonNull SqlColumn<E, T3> column3,
		@NonNull SqlColumn<E, T4> column4
	) {
		return new SqlInsertColumns4<>(insertColumns(table, dialect, connectionSource, queryTimeout, rowMapper), column1, column2, column3, column4);
	}
	
	/**
	 * Creates a new compile-time checked, five-column insert builder that is not driven by an entity.<br>
	 *
	 * @param table The table to insert into
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @param column1 The first column of every row
	 * @param column2 The second column of every row
	 * @param column3 The third column of every row
	 * @param column4 The fourth column of every row
	 * @param column5 The fifth column of every row
	 * @param <E> The type of the entity mapped from a row of the table
	 * @param <T1> The type of the first column
	 * @param <T2> The type of the second column
	 * @param <T3> The type of the third column
	 * @param <T4> The type of the fourth column
	 * @param <T5> The type of the fifth column
	 * @return The newly created column-value insert builder
	 * @throws NullPointerException If any of the arguments is null
	 */
	public static <E, T1, T2, T3, T4, T5> @NonNull SqlInsertColumns5<E, T1, T2, T3, T4, T5> columns(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull SqlColumn<E, T1> column1,
		@NonNull SqlColumn<E, T2> column2,
		@NonNull SqlColumn<E, T3> column3,
		@NonNull SqlColumn<E, T4> column4,
		@NonNull SqlColumn<E, T5> column5
	) {
		return new SqlInsertColumns5<>(insertColumns(table, dialect, connectionSource, queryTimeout, rowMapper), column1, column2, column3, column4, column5);
	}
	
	/**
	 * Creates a new compile-time checked, sixth-column insert builder that is not driven by an entity.<br>
	 *
	 * @param table The table to insert into
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @param column1 The first column of every row
	 * @param column2 The second column of every row
	 * @param column3 The third column of every row
	 * @param column4 The fourth column of every row
	 * @param column5 The fifth column of every row
	 * @param column6 The sixth column of every row
	 * @param <E> The type of the entity mapped from a row of the table
	 * @param <T1> The type of the first column
	 * @param <T2> The type of the second column
	 * @param <T3> The type of the third column
	 * @param <T4> The type of the fourth column
	 * @param <T5> The type of the fifth column
	 * @param <T6> The type of the sixth column
	 * @return The newly created column-value insert builder
	 * @throws NullPointerException If any of the arguments is null
	 */
	public static <E, T1, T2, T3, T4, T5, T6> @NonNull SqlInsertColumns6<E, T1, T2, T3, T4, T5, T6> columns(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull SqlColumn<E, T1> column1,
		@NonNull SqlColumn<E, T2> column2,
		@NonNull SqlColumn<E, T3> column3,
		@NonNull SqlColumn<E, T4> column4,
		@NonNull SqlColumn<E, T5> column5,
		@NonNull SqlColumn<E, T6> column6
	) {
		return new SqlInsertColumns6<>(insertColumns(table, dialect, connectionSource, queryTimeout, rowMapper), column1, column2, column3, column4, column5, column6);
	}
	
	/**
	 * Creates a new compile-time checked, seventh-column insert builder that is not driven by an entity.<br>
	 *
	 * @param table The table to insert into
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @param column1 The first column of every row
	 * @param column2 The second column of every row
	 * @param column3 The third column of every row
	 * @param column4 The fourth column of every row
	 * @param column5 The fifth column of every row
	 * @param column6 The sixth column of every row
	 * @param column7 The seventh column of every row
	 * @param <E> The type of the entity mapped from a row of the table
	 * @param <T1> The type of the first column
	 * @param <T2> The type of the second column
	 * @param <T3> The type of the third column
	 * @param <T4> The type of the fourth column
	 * @param <T5> The type of the fifth column
	 * @param <T6> The type of the sixth column
	 * @param <T7> The type of the seventh column
	 * @return The newly created column-value insert builder
	 * @throws NullPointerException If any of the arguments is null
	 */
	public static <E, T1, T2, T3, T4, T5, T6, T7> @NonNull SqlInsertColumns7<E, T1, T2, T3, T4, T5, T6, T7> columns(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull SqlColumn<E, T1> column1,
		@NonNull SqlColumn<E, T2> column2,
		@NonNull SqlColumn<E, T3> column3,
		@NonNull SqlColumn<E, T4> column4,
		@NonNull SqlColumn<E, T5> column5,
		@NonNull SqlColumn<E, T6> column6,
		@NonNull SqlColumn<E, T7> column7
	) {
		return new SqlInsertColumns7<>(insertColumns(table, dialect, connectionSource, queryTimeout, rowMapper), column1, column2, column3, column4, column5, column6, column7);
	}
	
	/**
	 * Creates a new compile-time checked, eighth-column insert builder that is not driven by an entity.<br>
	 *
	 * @param table The table to insert into
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @param column1 The first column of every row
	 * @param column2 The second column of every row
	 * @param column3 The third column of every row
	 * @param column4 The fourth column of every row
	 * @param column5 The fifth column of every row
	 * @param column6 The sixth column of every row
	 * @param column7 The seventh column of every row
	 * @param column8 The eighth column of every row
	 * @param <E> The type of the entity mapped from a row of the table
	 * @param <T1> The type of the first column
	 * @param <T2> The type of the second column
	 * @param <T3> The type of the third column
	 * @param <T4> The type of the fourth column
	 * @param <T5> The type of the fifth column
	 * @param <T6> The type of the sixth column
	 * @param <T7> The type of the seventh column
	 * @param <T8> The type of the eighth column
	 * @return The newly created column-value insert builder
	 * @throws NullPointerException If any of the arguments is null
	 */
	public static <E, T1, T2, T3, T4, T5, T6, T7, T8> @NonNull SqlInsertColumns8<E, T1, T2, T3, T4, T5, T6, T7, T8> columns(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull SqlColumn<E, T1> column1,
		@NonNull SqlColumn<E, T2> column2,
		@NonNull SqlColumn<E, T3> column3,
		@NonNull SqlColumn<E, T4> column4,
		@NonNull SqlColumn<E, T5> column5,
		@NonNull SqlColumn<E, T6> column6,
		@NonNull SqlColumn<E, T7> column7,
		@NonNull SqlColumn<E, T8> column8
	) {
		return new SqlInsertColumns8<>(
			insertColumns(table, dialect, connectionSource, queryTimeout, rowMapper), column1, column2, column3, column4, column5, column6, column7, column8
		);
	}
	
	/**
	 * Creates a new compile-time checked, ninth-column insert builder that is not driven by an entity.<br>
	 *
	 * @param table The table to insert into
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @param column1 The first column of every row
	 * @param column2 The second column of every row
	 * @param column3 The third column of every row
	 * @param column4 The fourth column of every row
	 * @param column5 The fifth column of every row
	 * @param column6 The sixth column of every row
	 * @param column7 The seventh column of every row
	 * @param column8 The eighth column of every row
	 * @param column9 The ninth column of every row
	 * @param <E> The type of the entity mapped from a row of the table
	 * @param <T1> The type of the first column
	 * @param <T2> The type of the second column
	 * @param <T3> The type of the third column
	 * @param <T4> The type of the fourth column
	 * @param <T5> The type of the fifth column
	 * @param <T6> The type of the sixth column
	 * @param <T7> The type of the seventh column
	 * @param <T8> The type of the eighth column
	 * @param <T9> The type of the ninth column
	 * @return The newly created column-value insert builder
	 * @throws NullPointerException If any of the arguments is null
	 */
	public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9> @NonNull SqlInsertColumns9<E, T1, T2, T3, T4, T5, T6, T7, T8, T9> columns(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull SqlColumn<E, T1> column1,
		@NonNull SqlColumn<E, T2> column2,
		@NonNull SqlColumn<E, T3> column3,
		@NonNull SqlColumn<E, T4> column4,
		@NonNull SqlColumn<E, T5> column5,
		@NonNull SqlColumn<E, T6> column6,
		@NonNull SqlColumn<E, T7> column7,
		@NonNull SqlColumn<E, T8> column8,
		@NonNull SqlColumn<E, T9> column9
	) {
		return new SqlInsertColumns9<>(
			insertColumns(table, dialect, connectionSource, queryTimeout, rowMapper), column1, column2, column3, column4, column5, column6, column7, column8, column9
		);
	}
	
	/**
	 * Creates a new compile-time checked, tenth-column insert builder that is not driven by an entity.<br>
	 *
	 * @param table The table to insert into
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @param column1 The first column of every row
	 * @param column2 The second column of every row
	 * @param column3 The third column of every row
	 * @param column4 The fourth column of every row
	 * @param column5 The fifth column of every row
	 * @param column6 The sixth column of every row
	 * @param column7 The seventh column of every row
	 * @param column8 The eighth column of every row
	 * @param column9 The ninth column of every row
	 * @param column10 The tenth column of every row
	 * @param <E> The type of the entity mapped from a row of the table
	 * @param <T1> The type of the first column
	 * @param <T2> The type of the second column
	 * @param <T3> The type of the third column
	 * @param <T4> The type of the fourth column
	 * @param <T5> The type of the fifth column
	 * @param <T6> The type of the sixth column
	 * @param <T7> The type of the seventh column
	 * @param <T8> The type of the eighth column
	 * @param <T9> The type of the ninth column
	 * @param <T10> The type of the tenth column
	 * @return The newly created column-value insert builder
	 * @throws NullPointerException If any of the arguments is null
	 */
	public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> @NonNull SqlInsertColumns10<E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> columns(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull SqlColumn<E, T1> column1,
		@NonNull SqlColumn<E, T2> column2,
		@NonNull SqlColumn<E, T3> column3,
		@NonNull SqlColumn<E, T4> column4,
		@NonNull SqlColumn<E, T5> column5,
		@NonNull SqlColumn<E, T6> column6,
		@NonNull SqlColumn<E, T7> column7,
		@NonNull SqlColumn<E, T8> column8,
		@NonNull SqlColumn<E, T9> column9,
		@NonNull SqlColumn<E, T10> column10
	) {
		return new SqlInsertColumns10<>(
			insertColumns(table, dialect, connectionSource, queryTimeout, rowMapper), column1, column2, column3, column4, column5, column6, column7, column8, column9, column10
		);
	}
	
	/**
	 * Creates a new compile-time checked, eleventh-column insert builder that is not driven by an entity.<br>
	 *
	 * @param table The table to insert into
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @param column1 The first column of every row
	 * @param column2 The second column of every row
	 * @param column3 The third column of every row
	 * @param column4 The fourth column of every row
	 * @param column5 The fifth column of every row
	 * @param column6 The sixth column of every row
	 * @param column7 The seventh column of every row
	 * @param column8 The eighth column of every row
	 * @param column9 The ninth column of every row
	 * @param column10 The tenth column of every row
	 * @param column11 The eleventh column of every row
	 * @param <E> The type of the entity mapped from a row of the table
	 * @param <T1> The type of the first column
	 * @param <T2> The type of the second column
	 * @param <T3> The type of the third column
	 * @param <T4> The type of the fourth column
	 * @param <T5> The type of the fifth column
	 * @param <T6> The type of the sixth column
	 * @param <T7> The type of the seventh column
	 * @param <T8> The type of the eighth column
	 * @param <T9> The type of the ninth column
	 * @param <T10> The type of the tenth column
	 * @param <T11> The type of the eleventh column
	 * @return The newly created column-value insert builder
	 * @throws NullPointerException If any of the arguments is null
	 */
	public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> @NonNull SqlInsertColumns11<E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> columns(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull SqlColumn<E, T1> column1,
		@NonNull SqlColumn<E, T2> column2,
		@NonNull SqlColumn<E, T3> column3,
		@NonNull SqlColumn<E, T4> column4,
		@NonNull SqlColumn<E, T5> column5,
		@NonNull SqlColumn<E, T6> column6,
		@NonNull SqlColumn<E, T7> column7,
		@NonNull SqlColumn<E, T8> column8,
		@NonNull SqlColumn<E, T9> column9,
		@NonNull SqlColumn<E, T10> column10,
		@NonNull SqlColumn<E, T11> column11
	) {
		return new SqlInsertColumns11<>(
			insertColumns(table, dialect, connectionSource, queryTimeout, rowMapper), column1, column2, column3, column4, column5, column6, column7, column8, column9, column10, column11
		);
	}
	
	/**
	 * Creates a new compile-time checked, twelfth-column insert builder that is not driven by an entity.<br>
	 *
	 * @param table The table to insert into
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @param column1 The first column of every row
	 * @param column2 The second column of every row
	 * @param column3 The third column of every row
	 * @param column4 The fourth column of every row
	 * @param column5 The fifth column of every row
	 * @param column6 The sixth column of every row
	 * @param column7 The seventh column of every row
	 * @param column8 The eighth column of every row
	 * @param column9 The ninth column of every row
	 * @param column10 The tenth column of every row
	 * @param column11 The eleventh column of every row
	 * @param column12 The twelfth column of every row
	 * @param <E> The type of the entity mapped from a row of the table
	 * @param <T1> The type of the first column
	 * @param <T2> The type of the second column
	 * @param <T3> The type of the third column
	 * @param <T4> The type of the fourth column
	 * @param <T5> The type of the fifth column
	 * @param <T6> The type of the sixth column
	 * @param <T7> The type of the seventh column
	 * @param <T8> The type of the eighth column
	 * @param <T9> The type of the ninth column
	 * @param <T10> The type of the tenth column
	 * @param <T11> The type of the eleventh column
	 * @param <T12> The type of the twelfth column
	 * @return The newly created column-value insert builder
	 * @throws NullPointerException If any of the arguments is null
	 */
	public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> @NonNull SqlInsertColumns12<E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> columns(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull SqlColumn<E, T1> column1,
		@NonNull SqlColumn<E, T2> column2,
		@NonNull SqlColumn<E, T3> column3,
		@NonNull SqlColumn<E, T4> column4,
		@NonNull SqlColumn<E, T5> column5,
		@NonNull SqlColumn<E, T6> column6,
		@NonNull SqlColumn<E, T7> column7,
		@NonNull SqlColumn<E, T8> column8,
		@NonNull SqlColumn<E, T9> column9,
		@NonNull SqlColumn<E, T10> column10,
		@NonNull SqlColumn<E, T11> column11,
		@NonNull SqlColumn<E, T12> column12
	) {
		return new SqlInsertColumns12<>(
			insertColumns(table, dialect, connectionSource, queryTimeout, rowMapper),
			column1, column2, column3, column4, column5, column6, column7, column8, column9, column10, column11, column12
		);
	}
	
	/**
	 * Creates a new compile-time checked, thirteenth-column insert builder that is not driven by an entity.<br>
	 *
	 * @param table The table to insert into
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @param column1 The first column of every row
	 * @param column2 The second column of every row
	 * @param column3 The third column of every row
	 * @param column4 The fourth column of every row
	 * @param column5 The fifth column of every row
	 * @param column6 The sixth column of every row
	 * @param column7 The seventh column of every row
	 * @param column8 The eighth column of every row
	 * @param column9 The ninth column of every row
	 * @param column10 The tenth column of every row
	 * @param column11 The eleventh column of every row
	 * @param column12 The twelfth column of every row
	 * @param column13 The thirteenth column of every row
	 * @param <E> The type of the entity mapped from a row of the table
	 * @param <T1> The type of the first column
	 * @param <T2> The type of the second column
	 * @param <T3> The type of the third column
	 * @param <T4> The type of the fourth column
	 * @param <T5> The type of the fifth column
	 * @param <T6> The type of the sixth column
	 * @param <T7> The type of the seventh column
	 * @param <T8> The type of the eighth column
	 * @param <T9> The type of the ninth column
	 * @param <T10> The type of the tenth column
	 * @param <T11> The type of the eleventh column
	 * @param <T12> The type of the twelfth column
	 * @param <T13> The type of the thirteenth column
	 * @return The newly created column-value insert builder
	 * @throws NullPointerException If any of the arguments is null
	 */
	public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> @NonNull SqlInsertColumns13<E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> columns(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull SqlColumn<E, T1> column1,
		@NonNull SqlColumn<E, T2> column2,
		@NonNull SqlColumn<E, T3> column3,
		@NonNull SqlColumn<E, T4> column4,
		@NonNull SqlColumn<E, T5> column5,
		@NonNull SqlColumn<E, T6> column6,
		@NonNull SqlColumn<E, T7> column7,
		@NonNull SqlColumn<E, T8> column8,
		@NonNull SqlColumn<E, T9> column9,
		@NonNull SqlColumn<E, T10> column10,
		@NonNull SqlColumn<E, T11> column11,
		@NonNull SqlColumn<E, T12> column12,
		@NonNull SqlColumn<E, T13> column13
	) {
		return new SqlInsertColumns13<>(
			insertColumns(table, dialect, connectionSource, queryTimeout, rowMapper),
			column1, column2, column3, column4, column5, column6, column7, column8, column9, column10, column11, column12, column13
		);
	}
	
	/**
	 * Creates a new compile-time checked, fourteenth-column insert builder that is not driven by an entity.<br>
	 *
	 * @param table The table to insert into
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @param column1 The first column of every row
	 * @param column2 The second column of every row
	 * @param column3 The third column of every row
	 * @param column4 The fourth column of every row
	 * @param column5 The fifth column of every row
	 * @param column6 The sixth column of every row
	 * @param column7 The seventh column of every row
	 * @param column8 The eighth column of every row
	 * @param column9 The ninth column of every row
	 * @param column10 The tenth column of every row
	 * @param column11 The eleventh column of every row
	 * @param column12 The twelfth column of every row
	 * @param column13 The thirteenth column of every row
	 * @param column14 The fourteenth column of every row
	 * @param <E> The type of the entity mapped from a row of the table
	 * @param <T1> The type of the first column
	 * @param <T2> The type of the second column
	 * @param <T3> The type of the third column
	 * @param <T4> The type of the fourth column
	 * @param <T5> The type of the fifth column
	 * @param <T6> The type of the sixth column
	 * @param <T7> The type of the seventh column
	 * @param <T8> The type of the eighth column
	 * @param <T9> The type of the ninth column
	 * @param <T10> The type of the tenth column
	 * @param <T11> The type of the eleventh column
	 * @param <T12> The type of the twelfth column
	 * @param <T13> The type of the thirteenth column
	 * @param <T14> The type of the fourteenth column
	 * @return The newly created column-value insert builder
	 * @throws NullPointerException If any of the arguments is null
	 */
	public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> @NonNull SqlInsertColumns14<E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> columns(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull SqlColumn<E, T1> column1,
		@NonNull SqlColumn<E, T2> column2,
		@NonNull SqlColumn<E, T3> column3,
		@NonNull SqlColumn<E, T4> column4,
		@NonNull SqlColumn<E, T5> column5,
		@NonNull SqlColumn<E, T6> column6,
		@NonNull SqlColumn<E, T7> column7,
		@NonNull SqlColumn<E, T8> column8,
		@NonNull SqlColumn<E, T9> column9,
		@NonNull SqlColumn<E, T10> column10,
		@NonNull SqlColumn<E, T11> column11,
		@NonNull SqlColumn<E, T12> column12,
		@NonNull SqlColumn<E, T13> column13,
		@NonNull SqlColumn<E, T14> column14
	) {
		return new SqlInsertColumns14<>(
			insertColumns(table, dialect, connectionSource, queryTimeout, rowMapper),
			column1, column2, column3, column4, column5, column6, column7, column8, column9, column10, column11, column12, column13, column14
		);
	}
	
	/**
	 * Creates a new compile-time checked, fifteenth-column insert builder that is not driven by an entity.<br>
	 *
	 * @param table The table to insert into
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @param column1 The first column of every row
	 * @param column2 The second column of every row
	 * @param column3 The third column of every row
	 * @param column4 The fourth column of every row
	 * @param column5 The fifth column of every row
	 * @param column6 The sixth column of every row
	 * @param column7 The seventh column of every row
	 * @param column8 The eighth column of every row
	 * @param column9 The ninth column of every row
	 * @param column10 The tenth column of every row
	 * @param column11 The eleventh column of every row
	 * @param column12 The twelfth column of every row
	 * @param column13 The thirteenth column of every row
	 * @param column14 The fourteenth column of every row
	 * @param column15 The fifteenth column of every row
	 * @param <E> The type of the entity mapped from a row of the table
	 * @param <T1> The type of the first column
	 * @param <T2> The type of the second column
	 * @param <T3> The type of the third column
	 * @param <T4> The type of the fourth column
	 * @param <T5> The type of the fifth column
	 * @param <T6> The type of the sixth column
	 * @param <T7> The type of the seventh column
	 * @param <T8> The type of the eighth column
	 * @param <T9> The type of the ninth column
	 * @param <T10> The type of the tenth column
	 * @param <T11> The type of the eleventh column
	 * @param <T12> The type of the twelfth column
	 * @param <T13> The type of the thirteenth column
	 * @param <T14> The type of the fourteenth column
	 * @param <T15> The type of the fifteenth column
	 * @return The newly created column-value insert builder
	 * @throws NullPointerException If any of the arguments is null
	 */
	public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> @NonNull SqlInsertColumns15<E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> columns(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull SqlColumn<E, T1> column1,
		@NonNull SqlColumn<E, T2> column2,
		@NonNull SqlColumn<E, T3> column3,
		@NonNull SqlColumn<E, T4> column4,
		@NonNull SqlColumn<E, T5> column5,
		@NonNull SqlColumn<E, T6> column6,
		@NonNull SqlColumn<E, T7> column7,
		@NonNull SqlColumn<E, T8> column8,
		@NonNull SqlColumn<E, T9> column9,
		@NonNull SqlColumn<E, T10> column10,
		@NonNull SqlColumn<E, T11> column11,
		@NonNull SqlColumn<E, T12> column12,
		@NonNull SqlColumn<E, T13> column13,
		@NonNull SqlColumn<E, T14> column14,
		@NonNull SqlColumn<E, T15> column15
	) {
		return new SqlInsertColumns15<>(
			insertColumns(table, dialect, connectionSource, queryTimeout, rowMapper),
			column1, column2, column3, column4, column5, column6, column7, column8, column9, column10, column11, column12, column13, column14, column15
		);
	}
	
	/**
	 * Creates a new compile-time checked, sixteenth-column insert builder that is not driven by an entity.<br>
	 *
	 * @param table The table to insert into
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @param column1 The first column of every row
	 * @param column2 The second column of every row
	 * @param column3 The third column of every row
	 * @param column4 The fourth column of every row
	 * @param column5 The fifth column of every row
	 * @param column6 The sixth column of every row
	 * @param column7 The seventh column of every row
	 * @param column8 The eighth column of every row
	 * @param column9 The ninth column of every row
	 * @param column10 The tenth column of every row
	 * @param column11 The eleventh column of every row
	 * @param column12 The twelfth column of every row
	 * @param column13 The thirteenth column of every row
	 * @param column14 The fourteenth column of every row
	 * @param column15 The fifteenth column of every row
	 * @param column16 The sixteenth column of every row
	 * @param <E> The type of the entity mapped from a row of the table
	 * @param <T1> The type of the first column
	 * @param <T2> The type of the second column
	 * @param <T3> The type of the third column
	 * @param <T4> The type of the fourth column
	 * @param <T5> The type of the fifth column
	 * @param <T6> The type of the sixth column
	 * @param <T7> The type of the seventh column
	 * @param <T8> The type of the eighth column
	 * @param <T9> The type of the ninth column
	 * @param <T10> The type of the tenth column
	 * @param <T11> The type of the eleventh column
	 * @param <T12> The type of the twelfth column
	 * @param <T13> The type of the thirteenth column
	 * @param <T14> The type of the fourteenth column
	 * @param <T15> The type of the fifteenth column
	 * @param <T16> The type of the sixteenth column
	 * @return The newly created column-value insert builder
	 * @throws NullPointerException If any of the arguments is null
	 */
	public static <E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> @NonNull SqlInsertColumns16<E, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> columns(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull SqlColumn<E, T1> column1,
		@NonNull SqlColumn<E, T2> column2,
		@NonNull SqlColumn<E, T3> column3,
		@NonNull SqlColumn<E, T4> column4,
		@NonNull SqlColumn<E, T5> column5,
		@NonNull SqlColumn<E, T6> column6,
		@NonNull SqlColumn<E, T7> column7,
		@NonNull SqlColumn<E, T8> column8,
		@NonNull SqlColumn<E, T9> column9,
		@NonNull SqlColumn<E, T10> column10,
		@NonNull SqlColumn<E, T11> column11,
		@NonNull SqlColumn<E, T12> column12,
		@NonNull SqlColumn<E, T13> column13,
		@NonNull SqlColumn<E, T14> column14,
		@NonNull SqlColumn<E, T15> column15,
		@NonNull SqlColumn<E, T16> column16
	) {
		return new SqlInsertColumns16<>(
			insertColumns(table, dialect, connectionSource, queryTimeout, rowMapper),
			column1, column2, column3, column4, column5, column6, column7, column8, column9, column10, column11, column12, column13, column14, column15, column16
		);
	}
	
	/**
	 * Adds the given value as a typed bind parameter to the given renderer.<br>
	 * The value is cast to the type expected by the given sql type.<br>
	 *
	 * @param renderer The renderer to add the parameter to
	 * @param type The sql type of the parameter
	 * @param value The value of the parameter, may be {@code null}
	 * @param <T> The java type associated with the sql type
	 * @throws NullPointerException If the renderer is null
	 */
	@SuppressWarnings("unchecked")
	private static <T> void addParameter(@NonNull SqlRenderer renderer, @NonNull SqlType<T> type, @Nullable Object value) {
		Objects.requireNonNull(renderer, "Sql renderer must not be null");
		
		renderer.parameter(type, (T) value);
	}
	
	/**
	 * Builds the default upsert set clause for the given column, assigning it the value proposed by the conflicting insert.<br>
	 *
	 * @param column The column to build the default set clause for
	 * @param dialect The sql dialect used to reference the proposed value
	 * @return The default set clause for the given column
	 * @throws NullPointerException If the column or dialect is null
	 * @throws SqlException If the dialect does not support referencing the proposed row
	 */
	@SuppressWarnings("unchecked")
	private static <E, V> @NonNull SqlSetClause<E, V> defaultUpsertSetClause(@NonNull SqlColumn<E, V> column, @NonNull SqlDialect dialect) throws SqlException {
		Objects.requireNonNull(column, "Sql column must not be null");
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		
		return new SqlSetClause<>(column, (SqlExpression<V>) dialect.upsertExcludedValue(column), SqlSetType.EXPRESSION);
	}
	
	/**
	 * Renders the audit column values for a single value tuple into the given renderer.<br>
	 * The version, created-at and created-by columns are populated based on the audit configuration, while the updated-at and
	 * updated-by columns are set to {@code null}.<br>
	 * Each rendered value is preceded by a comma.<br>
	 *
	 * @param renderer The renderer to add the audit column values to
	 * @param auditConfig The audit configuration of the table
	 * @param auditUserProvider The provider supplying the current user for audit columns, or {@code null} for no audit user
	 * @param dialect The sql dialect used to render the query
	 * @throws NullPointerException If the renderer, audit config or dialect is null
	 * @throws SqlException If an error occurs while rendering an audit column value
	 */
	static void renderAuditColumnValues(@NonNull SqlRenderer renderer, @NonNull SqlAuditConfig auditConfig, @Nullable SqlAuditUserProvider auditUserProvider, @NonNull SqlDialect dialect) throws SqlException {
		Objects.requireNonNull(renderer, "Sql renderer must not be null");
		Objects.requireNonNull(auditConfig, "Sql audit config must not be null");
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		
		String auditUser = SqlQueryExecutor.resolveUser(auditUserProvider);
		boolean databaseSource = auditConfig.valueSource() == SqlAuditValueSource.DATABASE;
		LocalDateTime now = LocalDateTime.now(auditConfig.clock());
		
		for (SqlAuditColumn column : auditConfig.auditColumns()) {
			renderer.comma();
			switch (column.role()) {
				case VERSION -> {
					if (databaseSource) {
						renderer.literal("1");
					} else {
						renderer.parameter(auditConfig.versionType(), 1L);
					}
				}
				case CREATED_AT -> {
					if (databaseSource) {
						renderer.rendered(dialect.renderFunction(new SqlCurrentTimestampFunction<>(auditConfig.timestampType())));
					} else {
						renderer.parameter(auditConfig.timestampType(), now);
					}
				}
				case CREATED_BY -> {
					if (auditUser == null) {
						renderer.null_();
					} else {
						renderer.parameter(auditConfig.userType(), auditUser);
					}
				}
				case UPDATED_AT, UPDATED_BY -> renderer.null_();
			}
		}
	}
	
	/**
	 * Creates a copy of this insert query that additionally omits the given columns from the rendered value tuple.<br>
	 * Auto-increment columns are always omitted regardless of this setting.<br>
	 *
	 * @param columns The columns to omit
	 * @return A new insert query with the additional omitted columns
	 * @throws NullPointerException If the columns is null
	 */
	@SafeVarargs
	public final @NonNull SqlInsertQuery<E> omitting(@NonNull SqlColumn<E, ?>... columns) {
		Objects.requireNonNull(columns, "Sql columns must not be null");
		
		List<SqlColumn<E, ?>> merged = new ArrayList<>(this.config.omittedColumns());
		merged.addAll(List.of(columns));
		return new SqlInsertQuery<>(this.config.withOmittedColumns(List.copyOf(merged)));
	}
	
	/**
	 * Creates a copy of this insert query that computes the value of the given column from the given expression instead of the
	 * entity getter.<br>
	 * If the column already has an override, it is replaced.<br>
	 *
	 * @param column The column to override
	 * @param expression The expression to compute the column's value from
	 * @return A new insert query with the additional override
	 * @throws NullPointerException If the column or expression is null
	 * @param <C> The type of the value held by the column
	 */
	public <C> @NonNull SqlInsertQuery<E> override(@NonNull SqlColumn<E, C> column, @NonNull SqlExpression<C> expression) {
		Objects.requireNonNull(column, "Sql column must not be null");
		Objects.requireNonNull(expression, "Sql expression must not be null");
		
		List<SqlColumnValue<E, ?>> merged = new ArrayList<>(this.config.overrides());
		merged.removeIf(override -> override.column().equals(column));
		merged.add(SqlColumnValue.of(column, expression));
		return new SqlInsertQuery<>(this.config.withOverrides(List.copyOf(merged)));
	}
	
	/**
	 * Creates a copy of this insert query that records the user supplied by the given provider in the audit columns.<br>
	 * Has no effect if the target table is not audited.<br>
	 *
	 * @param auditUserProvider The provider that resolves the audit user, or {@code null} for no audit user
	 * @return A new insert query using the given audit user provider
	 */
	public @NonNull SqlInsertQuery<E> auditedBy(@Nullable SqlAuditUserProvider auditUserProvider) {
		return new SqlInsertQuery<>(this.config.withAuditUserProvider(auditUserProvider));
	}
	
	/**
	 * Finds the override configured for the given column, if any.<br>
	 *
	 * @param column The column to find the override for
	 * @return The override for the given column, or {@code null} if none is configured
	 */
	private @Nullable SqlColumnValue<E, ?> findOverride(@NonNull SqlColumn<E, ?> column) {
		for (SqlColumnValue<E, ?> override : this.config.overrides()) {
			if (override.column().equals(column)) {
				return override;
			}
		}
		return null;
	}
	
	/**
	 * Executes this insert query and returns the number of affected rows.<br>
	 * If the entities exceed the bind parameter limit of the dialect, the insert is executed as a batched update.<br>
	 *
	 * @return The number of affected rows
	 * @throws SqlException If an error occurs while executing the query
	 */
	public int execute() throws SqlException {
		List<SqlRendered> chunks = this.renderChunks(this.config.dialect());
		if (chunks.size() == 1) {
			return SqlQueryExecutor.executeUpdate(this.config.dialect(), this.config.connectionSource(), chunks.getFirst(), this.config.queryTimeout());
		}
		return SqlQueryExecutor.executeBatchedUpdate(this.config.dialect(), this.config.connectionSource(), chunks, this.config.queryTimeout());
	}
	
	/**
	 * Executes this insert query and returns the generated keys of the inserted rows.<br>
	 *
	 * @return The list of generated keys
	 * @throws SqlException If an error occurs while executing the query
	 */
	public @NonNull List<Long> executeReturningKeys() throws SqlException {
		List<SqlRendered> chunks = this.renderChunks(this.config.dialect());
		return SqlQueryExecutor.executeUpdateReturningKeys(this.config.dialect(), this.config.connectionSource(), chunks, this.config.queryTimeout());
	}
	
	/**
	 * Executes this insert query and returns the inserted rows mapped back into entities.<br>
	 * All columns of the target table are returned and mapped using the configured row mapper.<br>
	 *
	 * @return The list of inserted entities
	 * @throws SqlException If an error occurs while executing the query
	 */
	public @NonNull List<E> returning() throws SqlException {
		SqlDialect dialect = this.config.dialect();
		List<SqlRendered> chunks = this.renderChunks(dialect);
		SqlRendered returning = dialect.renderReturning(List.copyOf(this.config.table().columns()));
		
		if (chunks.size() == 1) {
			return SqlQueryExecutor.executeReturningQuery(dialect, this.config.connectionSource(), chunks.getFirst(), returning, this.config.queryTimeout(), this.config.rowMapper());
		}
		return SqlQueryExecutor.executeBatchedReturningQuery(dialect, this.config.connectionSource(), chunks, returning, this.config.queryTimeout(), this.config.rowMapper());
	}
	
	@Override
	public @NonNull SqlRendered toSql(@NonNull SqlDialect dialect) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		return this.renderInsert(dialect, this.config.entities());
	}
	
	/**
	 * Renders this insert query into one or more sql statements.<br>
	 * The entities are split into multiple chunks if their combined bind parameters exceed the limit of the dialect.<br>
	 *
	 * @param dialect The sql dialect used to render the query
	 * @return The list of rendered statements
	 * @throws NullPointerException If the dialect is null
	 * @throws SqlException If an error occurs while rendering the query
	 */
	private @NonNull List<SqlRendered> renderChunks(@NonNull SqlDialect dialect) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		if (this.config.isInsertFromSelect()) {
			return List.of(this.renderInsert(dialect, this.config.entities()));
		}
		
		int paramsPerRow = this.config.table().columns().size();
		SqlAuditConfig auditConfig = this.config.table().auditConfig().orElse(null);
		if (auditConfig != null) {
			paramsPerRow += auditConfig.auditColumns().size();
		}
		
		int maxRows = Math.max(1, dialect.maxBindParameters() / Math.max(1, paramsPerRow));
		if (this.config.entities().size() <= maxRows) {
			return List.of(this.renderInsert(dialect, this.config.entities()));
		}
		
		List<SqlRendered> chunks = new ArrayList<>();
		for (int start = 0; start < this.config.entities().size(); start += maxRows) {
			int end = Math.min(start + maxRows, this.config.entities().size());
			chunks.add(this.renderInsert(dialect, this.config.entities().subList(start, end)));
		}
		return chunks;
	}
	
	/**
	 * Renders a single {@code INSERT} statement for the given rows.<br>
	 * Handles the column list, the value tuples or the embedded select query as well as the upsert and insert-or-ignore clauses depending on the configuration.<br>
	 *
	 * @param dialect The sql dialect used to render the query
	 * @param rows The rows to render the value tuples for
	 * @return The rendered insert statement
	 * @throws NullPointerException If the dialect or rows is null
	 * @throws SqlException If an error occurs while rendering the query
	 */
	@SuppressWarnings("unchecked")
	private @NonNull SqlRendered renderInsert(@NonNull SqlDialect dialect, @NonNull List<E> rows) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(rows, "Sql rows must not be null");
		
		SqlTable<E> table = this.config.table();
		List<SqlColumn<E, ?>> conflictColumnsForFiltering = this.config.conflictColumns();
		List<SqlColumn<E, ?>> columns = table.columns().stream()
			.filter(column -> (conflictColumnsForFiltering != null && conflictColumnsForFiltering.contains(column)) || !column.autoIncrement())
			.filter(column -> (conflictColumnsForFiltering != null && conflictColumnsForFiltering.contains(column)) || !this.config.omittedColumns().contains(column))
			.toList();
		
		if (this.config.isUpsert() && !dialect.isFeatureSupported(SqlFeature.UPSERT_SUFFIX)) {
			List<SqlColumn<E, ?>> conflictColumns = Objects.requireNonNull(this.config.conflictColumns(), "Conflict columns must not be null");
			
			SqlRenderer valueTuples = SqlRenderer.empty();
			for (int e = 0; e < rows.size(); e++) {
				if (e > 0) {
					valueTuples.comma();
				}
				this.renderValueTuple(valueTuples, rows.get(e), columns, table.auditConfig().orElse(null), dialect);
			}
			return dialect.renderUpsertStatement(table, (List<SqlColumn<?, ?>>) (List<?>) columns, (List<SqlColumn<?, ?>>) (List<?>) conflictColumns, valueTuples.toSql());
		}
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.insert();
		
		if (this.config.isInsertOrIgnore() && dialect.usesInsertOrIgnoreModifier()) {
			SqlRendered modifier = dialect.renderInsertOrIgnoreModifier();
			if (!modifier.sql().isEmpty()) {
				renderer.rendered(modifier);
			}
		}
		
		renderer.into().literal(dialect.quoteIdentifier(table.name()));
		
		renderer.openingBracket();
		for (int i = 0; i < columns.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			
			renderer.literal(dialect.quoteIdentifier(columns.get(i).name()));
		}
		
		SqlAuditConfig auditConfig = table.auditConfig().orElse(null);
		if (auditConfig != null && !this.config.isInsertFromSelect()) {
			for (SqlAuditColumn column : auditConfig.auditColumns()) {
				renderer.comma().literal(dialect.quoteIdentifier(column.name()));
			}
		}
		renderer.closingBracket();
		
		if (this.config.isInsertFromSelect()) {
			renderer.rendered(Objects.requireNonNull(this.config.fromSelect(), "From select query must not be null").toSql(dialect));
		} else {
			renderer.values();
			for (int e = 0; e < rows.size(); e++) {
				if (e > 0) {
					renderer.comma();
				}
				this.renderValueTuple(renderer, rows.get(e), columns, auditConfig, dialect);
			}
		}
		
		if (this.config.isUpsert()) {
			List<SqlColumn<E, ?>> conflictColumns = Objects.requireNonNull(this.config.conflictColumns(), "Conflict columns must not be null");
			List<SqlSetClause<E, ?>> updateClauses = this.config.upsertUpdateClauses();
			if (updateClauses == null) {
				updateClauses = new ArrayList<>(columns.size());
				for (SqlColumn<E, ?> column : columns) {
					updateClauses.add(defaultUpsertSetClause(column, dialect));
				}
			}
			renderer.rendered(dialect.renderUpsertClause((List<SqlColumn<?, ?>>) (List<?>) conflictColumns, (List<SqlSetClause<?, ?>>) (List<?>) updateClauses));
		}
		
		if (this.config.isInsertOrIgnore() && !dialect.usesInsertOrIgnoreModifier()) {
			List<SqlColumn<E, ?>> conflictColumns = Objects.requireNonNull(this.config.conflictColumns(), "Conflict columns must not be null");
			SqlRendered suffix = dialect.renderInsertOrIgnoreSuffix((List<SqlColumn<?, ?>>) (List<?>) conflictColumns);
			
			if (!suffix.sql().isEmpty()) {
				renderer.rendered(suffix);
			}
		}
		return renderer.toSql();
	}
	
	/**
	 * Renders the value tuple of a single entity into the given renderer.<br>
	 * Each column value is added as a bind parameter, followed by the audit column values if the table is audited.<br>
	 * The audit values for the version, created-at and created-by columns are populated based on the audit configuration, while the updated-at and updated-by columns are set to {@code null}.<br>
	 *
	 * @param renderer The renderer to add the value tuple to
	 * @param entity The entity whose values are rendered
	 * @param columns The columns of the table in render order
	 * @param auditConfig The audit configuration of the table, or {@code null} if the table is not audited
	 * @param dialect The sql dialect used to render the query
	 * @throws SqlException If an error occurs while rendering the value tuple
	 */
	private void renderValueTuple(@NonNull SqlRenderer renderer, @NonNull E entity, @NonNull List<SqlColumn<E, ?>> columns, @Nullable SqlAuditConfig auditConfig, @NonNull SqlDialect dialect) throws SqlException {
		renderer.openingBracket();
		for (int i = 0; i < columns.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			
			SqlColumn<E, ?> column = columns.get(i);
			SqlColumnValue<E, ?> override = this.findOverride(column);
			if (override != null) {
				renderer.rendered(dialect.renderExpression(override.expression()));
			} else {
				Object value = column.getter().apply(entity);
				addParameter(renderer, column.type(), value);
			}
		}
		
		if (auditConfig != null) {
			renderAuditColumnValues(renderer, auditConfig, this.config.auditUserProvider(), dialect);
		}
		
		renderer.closingBracket();
	}
}
