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
import net.luis.utils.io.database.audit.SqlAuditConfig;
import net.luis.utils.io.database.audit.SqlAuditUserProvider;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.SqlStatementBuilderException;
import net.luis.utils.io.database.query.SqlQuery;
import net.luis.utils.io.database.query.util.SqlColumnValue;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.ResultSet;
import java.time.Duration;
import java.util.*;

/**
 * A builder for sql {@code INSERT} statements that are not driven by an entity.<br>
 * Every row is built from explicit {@link SqlColumnValue column values}, letting a caller insert a subset of a table's
 * columns without needing a fully populated entity instance, e.g. to accommodate auto-increment or defaulted columns.<br>
 * All rows added to one builder must specify the same set of columns.<br>
 * The builder is immutable, every {@link #row} call returns a new instance leaving this builder unchanged.<br>
 *
 * @see SqlColumnValue
 * @see SqlInsertQuery#insertColumns
 *
 * @author Luis-St
 *
 * @param <E> The type of the entity mapped from a row of the table
 */
public final class SqlInsertColumnsBuilder<E> implements SqlQuery<E> {
	
	/**
	 * The table to insert rows into.
	 */
	private final SqlTable<E> table;
	/**
	 * The sql dialect used to render the query.
	 */
	private final SqlDialect dialect;
	/**
	 * The connection source used to execute the query.
	 */
	private final SqlConnectionSource connectionSource;
	/**
	 * The timeout applied to the executed query.
	 */
	private final Duration queryTimeout;
	/**
	 * The mapper that converts a result set row into an entity.
	 */
	private final ThrowableFunction<ResultSet, E, SqlException> rowMapper;
	/**
	 * The provider that resolves the audit user, or {@code null} for no audit user.
	 */
	private final SqlAuditUserProvider auditUserProvider;
	/**
	 * The rows added to this builder so far, each a list of column values sharing the same columns.
	 */
	private final List<List<SqlColumnValue<E, ?>>> rows;
	
	/**
	 * Constructs a new column-value insert builder.<br>
	 *
	 * @param table The table to insert into
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @param auditUserProvider The provider that resolves the audit user, or {@code null} for no audit user
	 * @param rows The rows added to this builder so far
	 * @throws NullPointerException If any argument except the audit user provider is null
	 */
	SqlInsertColumnsBuilder(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@Nullable SqlAuditUserProvider auditUserProvider,
		@NonNull List<List<SqlColumnValue<E, ?>>> rows
	) {
		this.table = Objects.requireNonNull(table, "Sql table must not be null");
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
		this.connectionSource = Objects.requireNonNull(connectionSource, "Sql connection source must not be null");
		this.queryTimeout = Objects.requireNonNull(queryTimeout, "Query timeout must not be null");
		this.rowMapper = Objects.requireNonNull(rowMapper, "Row mapper must not be null");
		this.auditUserProvider = auditUserProvider;
		this.rows = Objects.requireNonNull(rows, "Sql rows must not be null");
	}
	
	/**
	 * Collects the column names referenced by the given row, in order.<br>
	 *
	 * @param values The row to collect the column names of
	 * @return The column names referenced by the given row
	 */
	private static <E> @NonNull Set<String> columnNames(@NonNull List<SqlColumnValue<E, ?>> values) {
		Set<String> names = new LinkedHashSet<>();
		for (SqlColumnValue<E, ?> value : values) {
			names.add(value.column().name());
		}
		return names;
	}
	
	/**
	 * Renders a single {@code INSERT} statement for the given rows of column values.<br>
	 *
	 * @param table The table to insert into
	 * @param dialect The sql dialect used to render the query
	 * @param auditUserProvider The provider that resolves the audit user, or {@code null} for no audit user
	 * @param rows The rows to render the value tuples for
	 * @return The rendered insert statement
	 * @throws SqlStatementBuilderException If the given rows are empty
	 * @throws SqlException If an error occurs while rendering the query
	 */
	private static <E> @NonNull SqlRendered renderInsert(@NonNull SqlTable<E> table, @NonNull SqlDialect dialect, @Nullable SqlAuditUserProvider auditUserProvider, @NonNull List<List<SqlColumnValue<E, ?>>> rows) throws SqlException {
		if (rows.isEmpty()) {
			throw new SqlStatementBuilderException("A column-value insert must have at least one row");
		}
		List<SqlColumnValue<E, ?>> firstRow = rows.getFirst();
		SqlAuditConfig auditConfig = table.auditConfig().orElse(null);
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.insert().into().literal(dialect.quoteIdentifier(table.name()));
		
		renderer.openingBracket();
		for (int i = 0; i < firstRow.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			renderer.literal(dialect.quoteIdentifier(firstRow.get(i).column().name()));
		}
		if (auditConfig != null) {
			for (var column : auditConfig.auditColumns()) {
				renderer.comma().literal(dialect.quoteIdentifier(column.name()));
			}
		}
		renderer.closingBracket();
		
		renderer.values();
		for (int r = 0; r < rows.size(); r++) {
			if (r > 0) {
				renderer.comma();
			}
			
			List<SqlColumnValue<E, ?>> row = rows.get(r);
			renderer.openingBracket();
			for (int i = 0; i < row.size(); i++) {
				if (i > 0) {
					renderer.comma();
				}
				renderer.rendered(dialect.renderExpression(row.get(i).expression()));
			}
			if (auditConfig != null) {
				SqlInsertQuery.renderAuditColumnValues(renderer, auditConfig, auditUserProvider, dialect);
			}
			renderer.closingBracket();
		}
		return renderer.toSql();
	}
	
	/**
	 * Creates a copy of this builder using the given audit user provider.<br>
	 *
	 * @param auditUserProvider The provider that resolves the audit user, or {@code null} for no audit user
	 * @return A new builder using the given audit user provider
	 */
	public @NonNull SqlInsertColumnsBuilder<E> withAuditUser(@Nullable SqlAuditUserProvider auditUserProvider) {
		return new SqlInsertColumnsBuilder<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, auditUserProvider, this.rows);
	}
	
	/**
	 * Creates a copy of this builder with the given row of column values appended.<br>
	 *
	 * @param values The column values of the row to add
	 * @return A new builder with the additional row
	 * @throws NullPointerException If the values is null
	 * @throws SqlStatementBuilderException If the values is empty, or the row's columns differ from a previously added row's columns
	 */
	@SafeVarargs
	public final @NonNull SqlInsertColumnsBuilder<E> row(@NonNull SqlColumnValue<E, ?>... values) throws SqlStatementBuilderException {
		Objects.requireNonNull(values, "Sql column values must not be null");
		return this.row(List.of(values));
	}
	
	/**
	 * Creates a copy of this builder with the given row of column values appended.<br>
	 *
	 * @param values The column values of the row to add
	 * @return A new builder with the additional row
	 * @throws NullPointerException If the values is null
	 * @throws SqlStatementBuilderException If the values is empty, or the row's columns differ from a previously added row's columns
	 */
	public @NonNull SqlInsertColumnsBuilder<E> row(@NonNull List<SqlColumnValue<E, ?>> values) throws SqlStatementBuilderException {
		Objects.requireNonNull(values, "Sql column values must not be null");
		if (values.isEmpty()) {
			throw new SqlStatementBuilderException("A row must specify at least one column value");
		}
		
		if (!this.rows.isEmpty()) {
			Set<String> expectedColumns = columnNames(this.rows.getFirst());
			Set<String> actualColumns = columnNames(values);
			if (!expectedColumns.equals(actualColumns)) {
				throw new SqlStatementBuilderException("All rows of a column-value insert must specify the same columns, expected " + expectedColumns + " but got " + actualColumns);
			}
		}
		
		List<List<SqlColumnValue<E, ?>>> newRows = new ArrayList<>(this.rows);
		newRows.add(List.copyOf(values));
		return new SqlInsertColumnsBuilder<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.auditUserProvider, newRows);
	}
	
	/**
	 * Executes this insert query and returns the number of affected rows.<br>
	 * If the rows exceed the bind parameter limit of the dialect, the insert is executed as a batched update.<br>
	 *
	 * @return The number of affected rows
	 * @throws SqlException If an error occurs while executing the query
	 */
	public int execute() throws SqlException {
		List<SqlRendered> chunks = this.renderChunks();
		if (chunks.size() == 1) {
			return SqlQueryExecutor.executeUpdate(this.dialect, this.connectionSource, chunks.getFirst(), this.queryTimeout);
		}
		return SqlQueryExecutor.executeBatchedUpdate(this.dialect, this.connectionSource, chunks, this.queryTimeout);
	}
	
	/**
	 * Executes this insert query and returns the generated keys of the inserted rows.<br>
	 *
	 * @return The list of generated keys
	 * @throws SqlException If an error occurs while executing the query
	 */
	public @NonNull List<Long> executeReturningKeys() throws SqlException {
		List<SqlRendered> chunks = this.renderChunks();
		return SqlQueryExecutor.executeUpdateReturningKeys(this.dialect, this.connectionSource, chunks, this.queryTimeout);
	}
	
	/**
	 * Executes this insert query and returns the inserted rows mapped back into entities.<br>
	 * All columns of the target table are returned and mapped using the configured row mapper.<br>
	 *
	 * @return The list of inserted entities
	 * @throws SqlException If an error occurs while executing the query
	 */
	public @NonNull List<E> returning() throws SqlException {
		List<SqlRendered> chunks = this.renderChunks();
		SqlRendered returning = this.dialect.renderReturning(List.copyOf(this.table.columns()));
		
		if (chunks.size() == 1) {
			return SqlQueryExecutor.executeReturningQuery(this.dialect, this.connectionSource, chunks.getFirst(), returning, this.queryTimeout, this.rowMapper);
		}
		return SqlQueryExecutor.executeBatchedReturningQuery(this.dialect, this.connectionSource, chunks, returning, this.queryTimeout, this.rowMapper);
	}
	
	@Override
	public @NonNull SqlRendered toSql(@NonNull SqlDialect dialect) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		return renderInsert(this.table, dialect, this.auditUserProvider, this.rows);
	}
	
	/**
	 * Renders this insert query into one or more sql statements.<br>
	 * The rows are split into multiple chunks if their combined bind parameters exceed the limit of the dialect.<br>
	 *
	 * @return The list of rendered statements
	 * @throws SqlStatementBuilderException If no row has been added to this builder
	 * @throws SqlException If an error occurs while rendering the query
	 */
	private @NonNull List<SqlRendered> renderChunks() throws SqlException {
		if (this.rows.isEmpty()) {
			throw new SqlStatementBuilderException("A column-value insert must have at least one row");
		}
		
		int columnsPerRow = this.rows.getFirst().size();
		SqlAuditConfig auditConfig = this.table.auditConfig().orElse(null);
		int paramsPerRow = columnsPerRow + (auditConfig != null ? auditConfig.auditColumns().size() : 0);
		
		int maxRows = Math.max(1, this.dialect.maxBindParameters() / Math.max(1, paramsPerRow));
		if (this.rows.size() <= maxRows) {
			return List.of(renderInsert(this.table, this.dialect, this.auditUserProvider, this.rows));
		}
		
		List<SqlRendered> chunks = new ArrayList<>();
		for (int start = 0; start < this.rows.size(); start += maxRows) {
			int end = Math.min(start + maxRows, this.rows.size());
			chunks.add(renderInsert(this.table, this.dialect, this.auditUserProvider, this.rows.subList(start, end)));
		}
		return chunks;
	}
}
