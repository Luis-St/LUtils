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
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.SqlStatementBuilderException;
import net.luis.utils.io.database.query.SqlQuery;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlType;
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

public class SqlInsertQuery<E> implements SqlQuery<E> {
	
	private final SqlTable<E> table;
	private final SqlDialect dialect;
	private final SqlConnectionSource connectionSource;
	private final Duration queryTimeout;
	private final ThrowableFunction<ResultSet, E, SqlException> rowMapper;
	private final List<E> entities;
	private final @Nullable SqlSelectQuery<?> fromSelect;
	private final @Nullable SqlColumn<E, ?> conflictColumn;
	private final @Nullable List<SqlColumn<E, ?>> conflictColumns;
	private final boolean isUpsert;
	private final boolean isInsertOrIgnore;
	private final boolean isInsertFromSelect;
	
	public SqlInsertQuery(
		@NonNull SqlTable<E> table, @NonNull SqlDialect dialect, @NonNull SqlConnectionSource connectionSource, @NonNull Duration queryTimeout, @NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper, @NonNull List<E> entities
	) throws SqlStatementBuilderException {
		Objects.requireNonNull(entities, "Entities must not be null");
		this(table, dialect, connectionSource, queryTimeout, rowMapper, List.copyOf(entities), null, null, null, false, false, false);
	}
	
	private SqlInsertQuery(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull List<E> entities,
		@Nullable SqlSelectQuery<?> fromSelect,
		@Nullable SqlColumn<E, ?> conflictColumn,
		@Nullable List<SqlColumn<E, ?>> conflictColumns,
		boolean isUpsert,
		boolean isInsertOrIgnore,
		boolean isInsertFromSelect
	) throws SqlStatementBuilderException {
		this.table = Objects.requireNonNull(table, "Sql table must not be null");
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
		this.connectionSource = Objects.requireNonNull(connectionSource, "Sql connection source must not be null");
		this.queryTimeout = Objects.requireNonNull(queryTimeout, "Query timeout must not be null");
		this.rowMapper = Objects.requireNonNull(rowMapper, "Row mapper must not be null");
		this.entities = entities;
		this.fromSelect = fromSelect;
		this.conflictColumn = conflictColumn;
		this.conflictColumns = conflictColumns;
		this.isUpsert = isUpsert;
		this.isInsertOrIgnore = isInsertOrIgnore;
		this.isInsertFromSelect = isInsertFromSelect;
		
		if (entities.isEmpty() && !isInsertFromSelect) {
			throw new IllegalArgumentException("Entities list must not be empty for insert queries");
		}
		
		if (isUpsert && isInsertOrIgnore) {
			throw new SqlStatementBuilderException("Upsert and insert or ignore are mutually exclusive");
		}
		
		if (isUpsert && conflictColumn == null) {
			throw new SqlStatementBuilderException("Conflict column must be specified for upsert queries");
		}
		
		if (isInsertOrIgnore) {
			if (conflictColumns == null || conflictColumns.isEmpty()) {
				throw new SqlStatementBuilderException("Conflict columns must be specified for insert or ignore queries");
			}
		}
		
		if (isInsertFromSelect && fromSelect == null) {
			throw new SqlStatementBuilderException("From select query must be specified for insert from select queries");
		}
	}
	
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
		
		return new SqlInsertQuery<>(table, dialect, connectionSource, queryTimeout, rowMapper, List.copyOf(entities), null, null, List.copyOf(conflictColumns), false, true, false);
	}
	
	public static <E> @NonNull SqlInsertQuery<E> upsert(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull List<E> entities,
		@NonNull SqlColumn<E, ?> conflictColumn
	) throws SqlStatementBuilderException {
		Objects.requireNonNull(entities, "List of entities must not be null");
		return new SqlInsertQuery<>(table, dialect, connectionSource, queryTimeout, rowMapper, List.copyOf(entities), null, conflictColumn, null, true, false, false);
	}
	
	public static <E> @NonNull SqlInsertQuery<E> insertFromSelect(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull SqlSelectQuery<?> fromSelect
	) throws SqlStatementBuilderException {
		return new SqlInsertQuery<>(table, dialect, connectionSource, queryTimeout, rowMapper, List.of(), fromSelect, null, null, false, false, true);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> void addParameter(@NonNull SqlRenderer renderer, @NonNull SqlType<T> type, @NonNull Object value) {
		Objects.requireNonNull(renderer, "Sql renderer must not be null");
		
		renderer.parameter(type, (T) value);
	}
	
	public int execute() throws SqlException {
		return SqlQueryExecutor.executeUpdate(this.dialect, this.connectionSource, this.toSql(this.dialect), this.queryTimeout);
	}
	
	public @NonNull List<E> returning() throws SqlException {
		return SqlQueryExecutor.executeReturningQuery(
			this.dialect, this.connectionSource, this.toSql(this.dialect), this.dialect.renderReturning(List.copyOf(this.table.columns())), this.queryTimeout, this.rowMapper
		);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public @NonNull SqlRendered toSql(@NonNull SqlDialect dialect) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		SqlRenderer renderer = SqlRenderer.empty();
		
		List<SqlColumn<E, ?>> columns = this.table.columns();
		renderer.insert();
		
		if (this.isInsertOrIgnore) {
			SqlRendered modifier = dialect.renderInsertOrIgnoreModifier();
			if (!modifier.sql().isEmpty()) {
				renderer.rendered(modifier);
			}
		}
		
		renderer.into().literal(dialect.quoteIdentifier(this.table.name()));
		
		renderer.openingBracket();
		for (int i = 0; i < columns.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			
			renderer.literal(dialect.quoteIdentifier(columns.get(i).name()));
		}
		renderer.closingBracket();
		
		if (this.isInsertFromSelect) {
			renderer.rendered(Objects.requireNonNull(this.fromSelect, "From select query must not be null").toSql(dialect));
		} else {
			renderer.values();
			for (int e = 0; e < this.entities.size(); e++) {
				if (e > 0) {
					renderer.comma();
				}
				
				E entity = this.entities.get(e);
				renderer.openingBracket();
				for (int i = 0; i < columns.size(); i++) {
					if (i > 0) {
						renderer.comma();
					}
					
					SqlColumn<E, ?> column = columns.get(i);
					Object value = column.getter().apply(entity);
					if (value == null) {
						renderer.null_();
					} else {
						addParameter(renderer, column.type(), value);
					}
				}
				
				renderer.closingBracket();
			}
		}
		
		if (this.isUpsert) {
			renderer.rendered(dialect.renderUpsertClause(Objects.requireNonNull(this.conflictColumn, "Conflict column must not be null"), (List<SqlColumn<?, ?>>) (List<?>) columns));
		}
		
		if (this.isInsertOrIgnore) {
			SqlRendered suffix = dialect.renderInsertOrIgnoreSuffix((List<SqlColumn<?, ?>>) (List<?>) Objects.requireNonNull(this.conflictColumns, "Conflict columns must not be null"));
			if (!suffix.sql().isEmpty()) {
				renderer.rendered(suffix);
			}
		}
		return renderer.toSql();
	}
}
