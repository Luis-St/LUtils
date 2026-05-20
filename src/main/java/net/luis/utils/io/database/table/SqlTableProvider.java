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
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
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
 *
 * @author Luis-St
 *
 */

public class SqlTableProvider<E> implements AutoCloseable {
	
	private final SqlTable<E> table;
	private final SqlDialect dialect;
	private final Connection connection;
	private final Duration queryTimeout;
	private final boolean ownsConnection;
	
	public SqlTableProvider(@NonNull SqlTable<E> table, @NonNull SqlDialect dialect, @NonNull Connection connection, @NonNull Duration queryTimeout) {
		this(table, dialect, connection, queryTimeout, true);
	}
	
	public SqlTableProvider(@NonNull SqlTable<E> table, @NonNull SqlDialect dialect, @NonNull Connection connection, @NonNull Duration queryTimeout, boolean ownsConnection) {
		this.table = Objects.requireNonNull(table, "Sql table must not be null");
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
		this.connection = Objects.requireNonNull(connection, "Connection must not be null");
		this.queryTimeout = Objects.requireNonNull(queryTimeout, "Query timeout must not be null");
		this.ownsConnection = ownsConnection;
	}
	
	private void executeStatement(@NonNull SqlRendered rendered) throws SqlException {
		Objects.requireNonNull(rendered, "Sql rendered must not be null");
		
		try (PreparedStatement statement = this.connection.prepareStatement(rendered.sql())) {
			List<Pair<SqlType<?>, Object>> parameters = rendered.parameters();
			
			for (int i = 0; i < parameters.size(); i++) {
				Pair<SqlType<?>, Object> pair = parameters.get(i);
				SqlType.setUnsafe(pair.getFirst(), this.dialect, statement, i + 1, pair.getSecond());
			}
			
			long seconds = this.queryTimeout.toSeconds();
			if (seconds == 0 && !this.queryTimeout.isZero()) {
				seconds = 1;
			}
			statement.setQueryTimeout((int) Math.min(seconds, Integer.MAX_VALUE));
			statement.execute();
		} catch (SQLException e) {
			throw new SqlException("Failed to execute statement: " + rendered.sql(), e);
		}
	}
	
	public void create() throws SqlException {
		this.executeStatement(this.dialect.tableRenderer().renderCreateTable(this.table, false));
	}
	
	public void createIfNotExists() throws SqlException {
		this.executeStatement(this.dialect.tableRenderer().renderCreateTable(this.table, true));
	}
	
	public boolean exists() throws SqlException {
		try (ResultSet resultSet = this.connection.getMetaData().getTables(null, this.table.getSchema(), this.table.getName(), new String[] { "TABLE" })) {
			return resultSet.next();
		} catch (SQLException e) {
			throw new SqlException("Failed to check if table " + this.table.getName() + " exists", e);
		}
	}
	
	public void truncate() throws SqlException {
		this.executeStatement(this.dialect.tableRenderer().renderTruncateTable(this.table));
	}
	
	public void drop() throws SqlException {
		this.executeStatement(this.dialect.tableRenderer().renderDropTable(this.table, false));
	}
	
	public void dropIfExists() throws SqlException {
		this.executeStatement(this.dialect.tableRenderer().renderDropTable(this.table, true));
	}
	
	public void createIndex(@NonNull String name, @NonNull List<SqlColumn<E, ?>> columns, @NonNull SqlIndexMethod method) throws SqlException {
		this.createIndex(name, columns, false, null, method);
	}
	
	public void createIndex(@NonNull String name, @NonNull List<SqlColumn<E, ?>> columns, boolean unique, @NonNull SqlIndexMethod method) throws SqlException {
		this.createIndex(name, columns, unique, null, method);
	}
	
	public void createIndex(@NonNull String name, @NonNull List<SqlColumn<E, ?>> columns, boolean unique, @Nullable SqlCondition whereCondition, @NonNull SqlIndexMethod method) throws SqlException {
		Objects.requireNonNull(name, "Index name must not be null");
		Objects.requireNonNull(columns, "Index columns must not be null");
		Objects.requireNonNull(method, "Index method must not be null");
		if (columns.isEmpty()) {
			throw new IllegalArgumentException("Index columns must not be empty");
		}
		
		for (SqlColumn<E, ?> column : columns) {
			if (column.getOwningTable() != this.table) {
				throw new IllegalArgumentException("Column " + column.getName() + " does not belong to table " + this.table.getName());
			}
		}
		
		if (!this.dialect.isIndexMethodSupported(method)) {
			throw new SqlException("Index method " + method + " is not supported by dialect " + this.dialect.name());
		}
		
		SqlIndex index = new SqlIndex(name, List.copyOf(columns), unique, whereCondition, method);
		this.executeStatement(this.dialect.indexRenderer().renderCreateIndex(index));
	}
	
	public @NonNull @Unmodifiable List<SqlIndex> getIndexes() throws SqlException {
		try (ResultSet resultSet = this.connection.getMetaData().getIndexInfo(null, this.table.getSchema(), this.table.getName(), false, false)) {
			Map<String, List<SqlColumn<?, ?>>> indexColumns = new LinkedHashMap<>();
			Map<String, Boolean> indexUnique = new LinkedHashMap<>();
			
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
					for (SqlColumn<E, ?> column : this.table.getColumns()) {
						if (column.getName().equals(columnName)) {
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
		} catch (SQLException e) {
			throw new SqlException("Failed to get indexes for table " + this.table.getName(), e);
		}
	}
	
	public void dropIndex(@NonNull String name) throws SqlException {
		Objects.requireNonNull(name, "Index name must not be null");
		this.executeStatement(this.dialect.indexRenderer().renderDropIndex(this.table, name));
	}
	
	@Override
	public void close() throws SqlException {
		if (!this.ownsConnection) {
			return;
		}
		try {
			this.connection.close();
		} catch (SQLException e) {
			throw new SqlException("Failed to close connection for table " + this.table.getName(), e);
		}
	}
}
