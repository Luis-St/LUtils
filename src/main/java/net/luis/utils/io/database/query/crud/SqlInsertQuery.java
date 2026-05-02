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

import com.google.common.collect.Lists;
import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.SqlFeature;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.query.SqlQuery;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.*;
import java.time.Duration;
import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class SqlInsertQuery<E> implements SqlQuery<E> {
	
	private final SqlTable<E> table;
	private final SqlDialect dialect;
	private final Connection connection;
	private final Duration queryTimeout;
	private final ThrowableFunction<ResultSet, E, SqlException> rowMapper;
	private final List<E> entities;
	private final int batchSize;
	private final @Nullable SqlSelectQuery<?> fromSelect;
	private final @Nullable SqlColumn<E, ?> conflictColumn;
	private final @Nullable List<SqlColumn<E, ?>> conflictColumns;
	private final boolean isUpsert;
	private final boolean isInsertOrIgnore;
	private final boolean isInsertFromSelect;
	
	public SqlInsertQuery(
		@NonNull SqlTable<E> table, @NonNull SqlDialect dialect, @NonNull Connection connection, @NonNull Duration queryTimeout, @NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper, @NonNull List<E> entities
	) {
		Objects.requireNonNull(entities, "Entities must not be null");
		this(table, dialect, connection, queryTimeout, rowMapper, List.copyOf(entities), 0, null, null, null, false, false, false);
	}
	
	private SqlInsertQuery(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull Connection connection,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull List<E> entities,
		int batchSize,
		@Nullable SqlSelectQuery<?> fromSelect,
		@Nullable SqlColumn<E, ?> conflictColumn,
		@Nullable List<SqlColumn<E, ?>> conflictColumns,
		boolean isUpsert,
		boolean isInsertOrIgnore,
		boolean isInsertFromSelect
	) {
		this.table = Objects.requireNonNull(table, "Sql table must not be null");
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
		this.connection = Objects.requireNonNull(connection, "Connection must not be null");
		this.queryTimeout = Objects.requireNonNull(queryTimeout, "Query timeout must not be null");
		this.rowMapper = Objects.requireNonNull(rowMapper, "Row mapper must not be null");
		this.entities = entities;
		this.batchSize = batchSize;
		this.fromSelect = fromSelect;
		this.conflictColumn = conflictColumn;
		this.conflictColumns = conflictColumns;
		this.isUpsert = isUpsert;
		this.isInsertOrIgnore = isInsertOrIgnore;
		this.isInsertFromSelect = isInsertFromSelect;
		
		if (entities.isEmpty() && !isInsertFromSelect) {
			throw new IllegalArgumentException("Entities list must not be empty for insert queries");
		}
		if (batchSize < 1) {
			throw new IllegalArgumentException("Batch size must be at least 1");
		}
		
		if (isUpsert == isInsertOrIgnore) {
			if (isUpsert) {
				throw new IllegalArgumentException("Upsert must be enabled without insert or ignore");
			} else {
				throw new IllegalArgumentException("Insert or ignore must be enabled without upsert");
			}
		}
		
		if (isUpsert && conflictColumn == null) {
			throw new IllegalArgumentException("Conflict column must be specified for upsert queries");
		}
		
		if (isInsertOrIgnore) {
			if (conflictColumns == null || conflictColumns.isEmpty()) {
				throw new IllegalArgumentException("Conflict columns must be specified for insert or ignore queries");
			}
		}
		
		if (isInsertFromSelect && fromSelect == null) {
			throw new IllegalArgumentException("From select query must be specified for insert from select queries");
		}
	}
	
	public int execute() throws SqlException {
		if (this.batchSize > 0 && this.entities.size() > this.batchSize) {
			return this.executeBatched();
		}
		return SqlQueryExecutor.executeUpdate(this.connection, this.toSql(this.dialect), this.queryTimeout);
	}
	
	private int executeBatched() throws SqlException {
		int totalAffected = 0;
		for (int i = 0; i < this.entities.size(); i += this.batchSize) {
			List<E> batch = List.copyOf(this.entities.subList(i, Math.min(i + this.batchSize, this.entities.size())));
			SqlInsertQuery<E> batchQuery = new SqlInsertQuery<>(
				this.table, this.dialect, this.connection, this.queryTimeout, this.rowMapper,
				batch, this.batchSize, this.fromSelect, this.conflictColumn,
				this.conflictColumns, this.isUpsert, this.isInsertOrIgnore, this.isInsertFromSelect
			);
			totalAffected += SqlQueryExecutor.executeUpdate(this.connection, batchQuery.toSql(this.dialect), this.queryTimeout);
		}
		return totalAffected;
	}
	
	public @NonNull List<E> returning() throws SqlException {
		return SqlQueryExecutor.executeReturningQuery(
			this.dialect, this.connection, this.toSql(this.dialect), this.dialect.renderReturning(List.copyOf(this.table.getColumns())), this.queryTimeout, this.rowMapper
		);
	}
	
	public @NonNull List<E> fetchInserted() throws SqlException {
		if (this.dialect.isFeatureSupported(SqlFeature.RETURNING)) {
			return this.returning();
		}
		
		SqlRendered rendered = this.toSql(this.dialect);
		List<E> results = Lists.newArrayList();
		try (PreparedStatement statement = SqlQueryExecutor.prepare(this.connection, rendered, this.queryTimeout, true)) {
			statement.executeUpdate();
			try (ResultSet keys = statement.getGeneratedKeys()) {
				while (keys.next()) {
					results.add(this.rowMapper.apply(keys));
				}
			}
		} catch (SqlException e) {
			throw e;
		} catch (Exception e) {
			throw new SqlException("Failed to fetch inserted entities", e);
		}
		return Collections.unmodifiableList(results);
	}
	
	@Override
	public @NonNull SqlRendered toSql(@NonNull SqlDialect dialect) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		SqlRenderer renderer = SqlRenderer.empty();
		
		List<SqlColumn<E, ?>> columns = this.table.getColumns();
		renderer.insert().into().literal(dialect.quoteIdentifier(this.table.getName()));
		
		renderer.openingBracket();
		for (int i = 0; i < columns.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			
			renderer.literal(dialect.quoteIdentifier(columns.get(i).getName()));
		}
		renderer.closingBracket();
		
		if (this.isInsertFromSelect) {
			if (this.fromSelect == null) {
				throw new SqlException("From select query must be specified for insert from select queries");
			}
			
			renderer.rendered(this.fromSelect.toSql(dialect));
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
					
					Object value = columns.get(i).getGetter().apply(entity);
					if (value == null) {
						renderer.null_();
					} else {
						renderer.parameter(value);
					}
				}
				
				renderer.closingBracket();
			}
		}
		
		if (this.isUpsert) {
			if (this.conflictColumn == null) {
				throw new SqlException("Conflict column must be specified for upsert queries");
			}
			
			renderer.on().literal("CONFLICT");
			renderer.openingBracket().literal(dialect.quoteIdentifier(this.conflictColumn.getName())).closingBracket();
			renderer.literal("DO").update().set();
			
			for (int i = 0; i < columns.size(); i++) {
				if (i > 0) {
					renderer.comma();
				}
				
				String quotedName = dialect.quoteIdentifier(columns.get(i).getName());
				renderer.literal(quotedName).literal("=").literal("EXCLUDED." + quotedName);
			}
		}
		
		if (this.isInsertOrIgnore) {
			if (this.conflictColumns == null || this.conflictColumns.isEmpty()) {
				throw new SqlException("Conflict columns must be specified for insert or ignore queries");
			}
			
			renderer.on().literal("CONFLICT");
			renderer.openingBracket();
			
			for (int i = 0; i < this.conflictColumns.size(); i++) {
				if (i > 0) {
					renderer.comma();
				}
				
				renderer.literal(dialect.quoteIdentifier(this.conflictColumns.get(i).getName()));
			}
			
			renderer.closingBracket();
			renderer.literal("DO").literal("NOTHING");
		}
		return renderer.toSql();
	}
}
