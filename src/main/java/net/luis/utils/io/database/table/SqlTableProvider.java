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

package net.luis.utils.io.database.table;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.luis.utils.io.database.SqlConnectionHandle;
import net.luis.utils.io.database.SqlConnectionSource;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.SqlExceptions;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectException;
import net.luis.utils.io.database.exception.database.SqlQueryExecutionException;
import net.luis.utils.io.database.exception.database.SqlSchemaIntrospectionException;
import net.luis.utils.io.database.index.SqlIndex;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.util.Pair;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.*;
import java.time.Duration;
import java.util.*;

/**
 * Provides table-level database operations for a single {@link SqlTable table definition}.<br>
 * The provider binds a table definition to a {@link SqlDialect dialect} and a {@link SqlConnectionSource connection source}
 * and acts as the entry point for executing schema operations against the database.<br>
 * <p>
 *     The provider can create, truncate and drop the table, check whether it exists and manage its indexes.<br>
 *     Statements are rendered through the dialect and executed using connections obtained from the connection source.<br>
 *     Every execution honors the configured query timeout.
 * </p>
 *
 * @see SqlTable
 * @see SqlDialect
 * @see SqlConnectionSource
 *
 * @author Luis-St
 *
 * @param <E> The type of the entity the bound table maps to
 */
public class SqlTableProvider<E> {
	
	/**
	 * The table definition this provider operates on.
	 */
	private final SqlTable<E> table;
	/**
	 * The dialect used to render the sql statements.
	 */
	private final SqlDialect dialect;
	/**
	 * The connection source used to obtain database connections.
	 */
	private final SqlConnectionSource connectionSource;
	/**
	 * The timeout applied to each executed statement.
	 */
	private final Duration queryTimeout;
	
	/**
	 * Constructs a new table provider for the given table, dialect, connection source and query timeout.<br>
	 *
	 * @param table The table definition this provider operates on
	 * @param dialect The dialect used to render the sql statements
	 * @param connectionSource The connection source used to obtain database connections
	 * @param queryTimeout The timeout applied to each executed statement
	 * @throws NullPointerException If the table, dialect, connection source or query timeout is null
	 */
	public SqlTableProvider(@NonNull SqlTable<E> table, @NonNull SqlDialect dialect, @NonNull SqlConnectionSource connectionSource, @NonNull Duration queryTimeout) {
		this.table = Objects.requireNonNull(table, "Sql table must not be null");
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
		this.connectionSource = Objects.requireNonNull(connectionSource, "Sql connection source must not be null");
		this.queryTimeout = Objects.requireNonNull(queryTimeout, "Query timeout must not be null");
	}
	
	/**
	 * Executes the given rendered statement against the database.<br>
	 * <p>
	 *     A connection is obtained from the connection source, the rendered parameters are bound to the
	 *     prepared statement, the configured query timeout is applied and the statement is executed.<br>
	 *     A non-zero timeout that rounds down to zero seconds is clamped to one second.
	 * </p>
	 *
	 * @param rendered The rendered statement to execute
	 * @throws NullPointerException If the rendered statement is null
	 * @throws SqlException If the statement could not be executed
	 */
	private void executeStatement(@NonNull SqlRendered rendered) throws SqlException {
		Objects.requireNonNull(rendered, "Sql rendered must not be null");
		
		try (SqlConnectionHandle handle = this.connectionSource.open(); PreparedStatement statement = handle.connection().prepareStatement(rendered.sql())) {
			List<Pair<SqlType<?>, Object>> parameters = rendered.parameters();
			
			for (int i = 0; i < parameters.size(); i++) {
				Pair<SqlType<?>, Object> pair = parameters.get(i);
				SqlType.setValue(pair.getFirst(), this.dialect, statement, i + 1, pair.getSecond());
			}
			
			long seconds = this.queryTimeout.toSeconds();
			if (seconds == 0 && !this.queryTimeout.isZero()) {
				seconds = 1;
			}
			statement.setQueryTimeout((int) Math.min(seconds, Integer.MAX_VALUE));
			statement.execute();
		} catch (SQLException e) {
			throw new SqlQueryExecutionException("Failed to execute statement: " + rendered.sql(), e, rendered.sql());
		}
	}
	
	/**
	 * Creates the table in the database.<br>
	 * @throws SqlException If the table could not be created, for example if it already exists
	 */
	public void create() throws SqlException {
		this.executeStatement(this.dialect.tableRenderer().renderCreateTable(this.table, false));
	}
	
	/**
	 * Creates the table in the database only if it does not already exist.<br>
	 * @throws SqlException If the statement could not be executed
	 */
	public void createIfNotExists() throws SqlException {
		this.executeStatement(this.dialect.tableRenderer().renderCreateTable(this.table, true));
	}
	
	/**
	 * Checks whether the table exists in the database.<br>
	 * @return {@code true} if the table exists, otherwise {@code false}
	 * @throws SqlException If the existence check could not be performed
	 */
	public boolean exists() throws SqlException {
		try (SqlConnectionHandle handle = this.connectionSource.open()) {
			String schema = handle.connection().getSchema();
			
			try (ResultSet resultSet = handle.connection().getMetaData().getTables(null, schema, this.table.name(), new String[] { "TABLE" })) {
				return resultSet.next();
			}
		} catch (SQLException e) {
			throw SqlExceptions.translate("Failed to check if table '" + this.table.name() + "' exists", e);
		}
	}
	
	/**
	 * Removes all rows from the table without dropping it.<br>
	 * @throws SqlException If the table could not be truncated
	 */
	public void truncate() throws SqlException {
		this.executeStatement(this.dialect.tableRenderer().renderTruncateTable(this.table));
	}
	
	/**
	 * Drops the table from the database.<br>
	 * @throws SqlException If the table could not be dropped, for example if it does not exist
	 */
	public void drop() throws SqlException {
		this.executeStatement(this.dialect.tableRenderer().renderDropTable(this.table, false));
	}
	
	/**
	 * Drops the table from the database only if it exists.<br>
	 * @throws SqlException If the statement could not be executed
	 */
	public void dropIfExists() throws SqlException {
		this.executeStatement(this.dialect.tableRenderer().renderDropTable(this.table, true));
	}
	
	/**
	 * Creates a non-unique index over the given columns of the table using the given index method.<br>
	 *
	 * @param name The name of the index
	 * @param columns The columns the index is created over
	 * @param method The index method to use
	 * @throws NullPointerException If the name, columns or method is null
	 * @throws IllegalArgumentException If the columns are empty or a column does not belong to the table
	 * @throws SqlDialectException If the index method is not supported by the dialect
	 * @throws SqlException If the index could not be created
	 */
	public void createIndex(@NonNull String name, @NonNull List<SqlColumn<E, ?>> columns, @NonNull SqlIndexMethod method) throws SqlException {
		this.createIndex(name, columns, false, null, method);
	}
	
	/**
	 * Creates an index over the given columns of the table using the given index method.<br>
	 *
	 * @param name The name of the index
	 * @param columns The columns the index is created over
	 * @param unique Whether the index enforces uniqueness
	 * @param method The index method to use
	 * @throws NullPointerException If the name, columns or method is null
	 * @throws IllegalArgumentException If the columns are empty or a column does not belong to the table
	 * @throws SqlDialectException If the index method is not supported by the dialect
	 * @throws SqlException If the index could not be created
	 */
	public void createIndex(@NonNull String name, @NonNull List<SqlColumn<E, ?>> columns, boolean unique, @NonNull SqlIndexMethod method) throws SqlException {
		this.createIndex(name, columns, unique, null, method);
	}
	
	/**
	 * Creates an index over the given columns of the table using the given index method and an optional partial index condition.<br>
	 *
	 * @param name The name of the index
	 * @param columns The columns the index is created over
	 * @param unique Whether the index enforces uniqueness
	 * @param whereCondition The condition that restricts the index to matching rows or {@code null} for a full index
	 * @param method The index method to use
	 * @throws NullPointerException If the name, columns or method is null
	 * @throws IllegalArgumentException If the columns are empty or a column does not belong to the table
	 * @throws SqlDialectException If the index method is not supported by the dialect
	 * @throws SqlException If the index could not be created
	 */
	public void createIndex(@NonNull String name, @NonNull List<SqlColumn<E, ?>> columns, boolean unique, @Nullable SqlCondition whereCondition, @NonNull SqlIndexMethod method) throws SqlException {
		Objects.requireNonNull(name, "Index name must not be null");
		Objects.requireNonNull(columns, "Index columns must not be null");
		Objects.requireNonNull(method, "Index method must not be null");
		if (columns.isEmpty()) {
			throw new IllegalArgumentException("Index columns must not be empty");
		}
		
		for (SqlColumn<E, ?> column : columns) {
			if (column.owningTable() != this.table) {
				throw new IllegalArgumentException("Sql column '" + column.name() + "' does not belong to table '" + this.table.name() + "'");
			}
		}
		
		if (!this.dialect.isIndexMethodSupported(method)) {
			throw new SqlDialectException("Sql index method '" + method + "' is not supported by dialect '" + this.dialect.name() + "'");
		}
		
		SqlIndex index = new SqlIndex(name, List.copyOf(columns), unique, whereCondition, method);
		this.executeStatement(this.dialect.indexRenderer().renderCreateIndex(index));
	}
	
	/**
	 * Retrieves the indexes that currently exist on the table by introspecting the database metadata.<br>
	 * Only columns that are part of the table definition are included, and the index method is reported as {@link SqlIndexMethod#BTREE}.<br>
	 *
	 * @return An unmodifiable list of the indexes on the table
	 * @throws SqlSchemaIntrospectionException If the index metadata could not be retrieved
	 */
	public @NonNull @Unmodifiable List<SqlIndex> getIndexes() throws SqlException {
		try (SqlConnectionHandle handle = this.connectionSource.open()) {
			String schema = handle.connection().getSchema();
			
			try (ResultSet resultSet = handle.connection().getMetaData().getIndexInfo(null, schema, this.table.name(), false, false)) {
				Map<String, List<SqlColumn<?, ?>>> indexColumns = Maps.newLinkedHashMap();
				Map<String, Boolean> indexUnique = Maps.newLinkedHashMap();
				
				while (resultSet.next()) {
					String indexName = resultSet.getString("INDEX_NAME");
					if (indexName == null) {
						continue;
					}
					
					String columnName = resultSet.getString("COLUMN_NAME");
					boolean nonUnique = resultSet.getBoolean("NON_UNIQUE");
					indexColumns.computeIfAbsent(indexName, k -> Lists.newArrayList());
					indexUnique.putIfAbsent(indexName, !nonUnique);
					
					if (columnName != null) {
						for (SqlColumn<E, ?> column : this.table.columns()) {
							if (column.name().equals(columnName)) {
								indexColumns.get(indexName).add(column);
								break;
							}
						}
					}
				}
				
				List<SqlIndex> indexes = Lists.newArrayList();
				for (Map.Entry<String, List<SqlColumn<?, ?>>> entry : indexColumns.entrySet()) {
					indexes.add(new SqlIndex(entry.getKey(), List.copyOf(entry.getValue()), indexUnique.getOrDefault(entry.getKey(), false), SqlIndexMethod.BTREE));
				}
				return Collections.unmodifiableList(indexes);
			}
		} catch (SQLException e) {
			throw new SqlSchemaIntrospectionException("Failed to get indexes for sql table '" + this.table.name() + "'", e);
		}
	}
	
	/**
	 * Drops the index with the given name from the table.<br>
	 *
	 * @param name The name of the index to drop
	 * @throws NullPointerException If the name is null
	 * @throws SqlException If the index could not be dropped
	 */
	public void dropIndex(@NonNull String name) throws SqlException {
		Objects.requireNonNull(name, "Index name must not be null");
		this.executeStatement(this.dialect.indexRenderer().renderDropIndex(this.table, name));
	}
}
