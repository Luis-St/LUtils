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

package net.luis.utils.io.database.migration;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.luis.utils.io.database.SqlDatabase;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.SqlSchemaObjectNotFoundException;
import net.luis.utils.io.database.exception.database.SqlSchemaIntrospectionException;
import net.luis.utils.io.database.table.*;
import net.luis.utils.io.database.type.ParameterizedSqlType;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public final class SqlMigrationSchema {
	
	private final String schema;
	private final Map<String, SqlTable<Void>> tables;
	private final Map<String, Map<String, SqlColumn<Void, ?>>> columns;
	private final Map<String, List<SqlCheckConstraintInfo>> checkConstraints;
	
	private SqlMigrationSchema(
		@NonNull String schema,
		@NonNull Map<String, SqlTable<Void>> tables,
		@NonNull Map<String, Map<String, SqlColumn<Void, ?>>> columns,
		@NonNull Map<String, List<SqlCheckConstraintInfo>> checkConstraints
	) {
		this.schema = Objects.requireNonNull(schema, "Schema must not be null");
		this.tables = Objects.requireNonNull(tables, "Tables must not be null");
		this.columns = Objects.requireNonNull(columns, "Columns must not be null");
		this.checkConstraints = Objects.requireNonNull(checkConstraints, "Check constraints must not be null");
	}
	
	public static @NonNull SqlMigrationSchema load(@NonNull SqlDatabase database) throws SqlException {
		Objects.requireNonNull(database, "Sql database must not be null");
		return load(database.getDataSource(), database.getDialect(), "public");
	}
	
	public static @NonNull SqlMigrationSchema load(@NonNull SqlDatabase database, @NonNull String schema) throws SqlException {
		Objects.requireNonNull(database, "Sql database must not be null");
		return load(database.getDataSource(), database.getDialect(), schema);
	}
	
	public static @NonNull SqlMigrationSchema load(@NonNull DataSource dataSource, @NonNull SqlDialect dialect) throws SqlException {
		return load(dataSource, dialect, "public");
	}
	
	public static @NonNull SqlMigrationSchema load(@NonNull DataSource dataSource, @NonNull SqlDialect dialect, @NonNull String schema) throws SqlException {
		Objects.requireNonNull(dataSource, "Data source must not be null");
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(schema, "Sql schema must not be null");

		try (Connection connection = dataSource.getConnection()) {
			return load(connection, dialect, schema);
		} catch (SQLException e) {
			throw new SqlSchemaIntrospectionException("Failed to load schema metadata for schema " + schema, e);
		}
	}

	public static @NonNull SqlMigrationSchema load(@NonNull Connection connection, @NonNull SqlDialect dialect, @NonNull String schema) throws SqlException {
		Objects.requireNonNull(connection, "Connection must not be null");
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(schema, "Sql schema must not be null");

		Map<String, SqlTable<Void>> tables = Maps.newLinkedHashMap();
		Map<String, Map<String, SqlColumn<Void, ?>>> columns = Maps.newLinkedHashMap();
		Map<String, List<SqlCheckConstraintInfo>> checkConstraints = Maps.newLinkedHashMap();

		try {
			DatabaseMetaData meta = connection.getMetaData();

			List<String> tableNames = discoverTableNames(meta, schema);
			
			for (String tableName : tableNames) {
				Set<String> primaryKeyColumns = loadPrimaryKeyColumns(meta, schema, tableName);
				Set<String> uniqueColumns = loadUniqueColumns(meta, schema, tableName);
				
				SqlTable<Void> table = SqlTable.create(Void.class, tableName, schema);
				Map<String, SqlColumn<Void, ?>> tableColumns = Maps.newLinkedHashMap();
				
				try (ResultSet colRs = meta.getColumns(null, schema, tableName, "%")) {
					while (colRs.next()) {
						String colName = colRs.getString("COLUMN_NAME");
						int dataType = colRs.getInt("DATA_TYPE");
						int colSize = colRs.getInt("COLUMN_SIZE");
						int decDigits = colRs.getInt("DECIMAL_DIGITS");
						int nullable = colRs.getInt("NULLABLE");
						String isAutoInc = colRs.getString("IS_AUTOINCREMENT");
						
						boolean isPrimaryKey = primaryKeyColumns.contains(colName);
						boolean isUnique = uniqueColumns.contains(colName);
						boolean isNullable = nullable == DatabaseMetaData.columnNullable;
						boolean isAutoIncrement = "YES".equals(isAutoInc);
						
						SqlType<?> sqlType = SqlJdbcTypeMapper.mapJdbcType(dataType, colSize, decDigits);
						SqlColumn<Void, ?> column = buildPhantomColumn(table, colName, sqlType, isNullable, isAutoIncrement, isPrimaryKey, isUnique);
						tableColumns.put(colName, column);
					}
				}
				
				if (primaryKeyColumns.size() > 1) {
					table.generateCompositePrimaryKey();
				}
				
				tables.put(tableName, table);
				columns.put(tableName, Collections.unmodifiableMap(tableColumns));
				
				List<SqlCheckConstraintInfo> tableCheckConstraints = dialect.getCheckConstraints(connection, schema, tableName);
				checkConstraints.put(tableName, List.copyOf(tableCheckConstraints));
			}
		} catch (SQLException e) {
			throw new SqlSchemaIntrospectionException("Failed to load schema metadata for schema " + schema, e);
		}
		
		return new SqlMigrationSchema(schema, Collections.unmodifiableMap(tables), Collections.unmodifiableMap(columns), Collections.unmodifiableMap(checkConstraints));
	}
	
	public static @NonNull SqlMigrationSchema empty() {
		return empty("public");
	}
	
	public static @NonNull SqlMigrationSchema empty(@NonNull String schema) {
		Objects.requireNonNull(schema, "Sql schema must not be null");
		
		return new SqlMigrationSchema(schema, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap());
	}
	
	public static @NonNull SqlMigrationSchema fromSnapshot(@NonNull List<SqlSchemaColumnInfo> columnInfos, @NonNull Map<String, List<SqlCheckConstraintInfo>> checkConstraints) throws SqlException {
		return fromSnapshot("public", columnInfos, checkConstraints);
	}
	
	public static @NonNull SqlMigrationSchema fromSnapshot(@NonNull String schema, @NonNull List<SqlSchemaColumnInfo> columnInfos, @NonNull Map<String, List<SqlCheckConstraintInfo>> checkConstraints) throws SqlException {
		Objects.requireNonNull(schema, "Sql schema must not be null");
		Objects.requireNonNull(columnInfos, "Sql column infos must not be null");
		Objects.requireNonNull(checkConstraints, "Sql check constraints must not be null");
		
		Map<String, SqlTable<Void>> tables = Maps.newLinkedHashMap();
		Map<String, Map<String, SqlColumn<Void, ?>>> columns = Maps.newLinkedHashMap();
		
		Map<String, List<SqlSchemaColumnInfo>> grouped = Maps.newLinkedHashMap();
		for (SqlSchemaColumnInfo info : columnInfos) {
			grouped.computeIfAbsent(info.tableName(), _ -> Lists.newArrayList()).add(info);
		}
		for (List<SqlSchemaColumnInfo> group : grouped.values()) {
			group.sort(Comparator.comparingInt(SqlSchemaColumnInfo::ordinalPosition));
		}
		
		for (Map.Entry<String, List<SqlSchemaColumnInfo>> entry : grouped.entrySet()) {
			String tableName = entry.getKey();
			List<SqlSchemaColumnInfo> tableColumnInfos = entry.getValue();
			
			SqlTable<Void> table = SqlTable.create(Void.class, tableName, schema);
			Map<String, SqlColumn<Void, ?>> tableColumns = Maps.newLinkedHashMap();
			
			int primaryKeyCount = 0;
			for (SqlSchemaColumnInfo info : tableColumnInfos) {
				SqlType<?> sqlType = SqlJdbcTypeMapper.reconstructType(info.jdbcType(), info.parameter());
				SqlColumn<Void, ?> column = buildPhantomColumn(table, info.columnName(), sqlType, info.nullable(), info.autoIncrement(), info.primaryKey(), info.unique());
				tableColumns.put(info.columnName(), column);
				if (info.primaryKey()) {
					primaryKeyCount++;
				}
			}
			
			if (primaryKeyCount > 1) {
				table.generateCompositePrimaryKey();
			}
			
			tables.put(tableName, table);
			columns.put(tableName, Collections.unmodifiableMap(tableColumns));
		}
		
		Map<String, List<SqlCheckConstraintInfo>> constraintsCopy = Maps.newLinkedHashMap();
		for (Map.Entry<String, List<SqlCheckConstraintInfo>> entry : checkConstraints.entrySet()) {
			constraintsCopy.put(entry.getKey(), List.copyOf(entry.getValue()));
		}
		
		return new SqlMigrationSchema(schema, Collections.unmodifiableMap(tables), Collections.unmodifiableMap(columns), Collections.unmodifiableMap(constraintsCopy));
	}
	
	private static @NonNull List<String> discoverTableNames(@NonNull DatabaseMetaData meta, @NonNull String schema) throws SQLException {
		try (ResultSet rs = meta.getTables(null, schema, "%", new String[] { "TABLE" })) {
			List<String> tableNames = Lists.newArrayList();
			
			while (rs.next()) {
				tableNames.add(rs.getString("TABLE_NAME"));
			}
			
			return tableNames;
		}
	}
	
	private static @NonNull Set<String> loadPrimaryKeyColumns(@NonNull DatabaseMetaData meta, @NonNull String schema, @NonNull String tableName) throws SQLException {
		try (ResultSet pkRs = meta.getPrimaryKeys(null, schema, tableName)) {
			Set<String> pkColumns = new LinkedHashSet<>();
			
			while (pkRs.next()) {
				pkColumns.add(pkRs.getString("COLUMN_NAME"));
			}
			
			return pkColumns;
		}
	}
	
	private static @NonNull Set<String> loadUniqueColumns(@NonNull DatabaseMetaData meta, @NonNull String schema, @NonNull String tableName) throws SQLException {
		Set<String> uniqueColumns = new LinkedHashSet<>();
		Map<String, List<String>> indexColumns = Maps.newLinkedHashMap();
		
		try (ResultSet idxRs = meta.getIndexInfo(null, schema, tableName, true, false)) {
			while (idxRs.next()) {
				String indexName = idxRs.getString("INDEX_NAME");
				String colName = idxRs.getString("COLUMN_NAME");
				
				if (indexName != null && colName != null) {
					indexColumns.computeIfAbsent(indexName, _ -> Lists.newArrayList()).add(colName);
				}
			}
		}
		
		for (List<String> cols : indexColumns.values()) {
			if (cols.size() == 1) {
				uniqueColumns.add(cols.getFirst());
			}
		}
		return uniqueColumns;
	}
	
	@SuppressWarnings({ "unchecked", "ReturnOfNull" })
	private static <C> @NonNull SqlColumn<Void, C> buildPhantomColumn(
		@NonNull SqlTable<Void> table,
		@NonNull String name,
		@NonNull SqlType<?> type,
		boolean nullable,
		boolean autoIncrement,
		boolean primaryKey,
		boolean unique
	) {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(name, "Column name must not be null");
		Objects.requireNonNull(type, "Sql type must not be null");
		
		SqlType<C> typedType = (SqlType<C>) type;
		if (primaryKey || autoIncrement || unique) {
			return table.column(name, typedType, _ -> null, col -> {
				if (!nullable) col.notNull();
				if (autoIncrement) col.autoIncrement();
				if (primaryKey) col.primaryKey();
				if (unique) col.unique();
				return col;
			});
		}
		
		if (!nullable) {
			return table.column(name, typedType, _ -> null, SqlColumnBuilder::notNull);
		}
		return table.column(name, typedType, _ -> null);
	}
	
	public @NonNull List<SqlSchemaColumnInfo> extractColumnInfos() {
		List<SqlSchemaColumnInfo> infos = Lists.newArrayList();
		for (Map.Entry<String, Map<String, SqlColumn<Void, ?>>> tableEntry : this.columns.entrySet()) {
			int ordinal = 0;
			String tableName = tableEntry.getKey();
			
			for (Map.Entry<String, SqlColumn<Void, ?>> colEntry : tableEntry.getValue().entrySet()) {
				SqlColumn<Void, ?> column = colEntry.getValue();
				SqlParameter parameter = column.type().baseType() instanceof ParameterizedSqlType<?, ?> parameterized ? parameterized.parameter() : null;
				
				infos.add(new SqlSchemaColumnInfo(
					tableName,
					column.name(),
					column.type().jdbcType(),
					parameter,
					column.nullable(),
					column.autoIncrement(),
					column.primaryKey(),
					column.unique(),
					ordinal++
				));
			}
		}
		return List.copyOf(infos);
	}
	
	public @NonNull Map<String, List<SqlCheckConstraintInfo>> extractCheckConstraints() {
		return this.checkConstraints;
	}
	
	public @NonNull SqlTable<Void> table(@NonNull String name) throws SqlSchemaObjectNotFoundException {
		Objects.requireNonNull(name, "Sql table name must not be null");
		
		SqlTable<Void> table = this.tables.get(name);
		if (table == null) {
			throw new SqlSchemaObjectNotFoundException("Sql table '" + name + "' not found in schema " + this.schema);
		}
		return table;
	}
	
	public boolean hasTable(@NonNull String name) {
		Objects.requireNonNull(name, "Table name must not be null");
		return this.tables.containsKey(name);
	}
	
	public @NonNull @Unmodifiable Set<String> tableNames() {
		return Collections.unmodifiableSet(this.tables.keySet());
	}
	
	public @NonNull SqlColumn<Void, ?> column(@NonNull String tableName, @NonNull String columnName) throws SqlSchemaObjectNotFoundException {
		Objects.requireNonNull(tableName, "Sql table name must not be null");
		Objects.requireNonNull(columnName, "Sql column name must not be null");
		
		Map<String, SqlColumn<Void, ?>> tableColumns = this.columns.get(tableName);
		if (tableColumns == null) {
			throw new SqlSchemaObjectNotFoundException("Table '" + tableName + "' not found in schema " + this.schema);
		}
		
		SqlColumn<Void, ?> column = tableColumns.get(columnName);
		if (column == null) {
			throw new SqlSchemaObjectNotFoundException("Column '" + columnName + "' not found in table " + tableName);
		}
		return column;
	}
	
	@SuppressWarnings("unchecked")
	public <C> @NonNull SqlColumn<Void, C> column(@NonNull String tableName, @NonNull String columnName, @NonNull Class<C> type) throws SqlSchemaObjectNotFoundException {
		Objects.requireNonNull(type, "Type must not be null");
		
		SqlColumn<Void, ?> column = this.column(tableName, columnName);
		if (!type.isAssignableFrom(column.type().javaType())) {
			throw new SqlSchemaObjectNotFoundException(
				"Sql column '" + columnName + "' in table " + tableName + " has Java type " + column.type().javaType().getName() + " but requested type was " + type.getName()
			);
		}
		return (SqlColumn<Void, C>) column;
	}
	
	public boolean hasColumn(@NonNull String tableName, @NonNull String columnName) {
		Objects.requireNonNull(tableName, "Sql table name must not be null");
		Objects.requireNonNull(columnName, "Sql column name must not be null");
		
		Map<String, SqlColumn<Void, ?>> tableColumns = this.columns.get(tableName);
		return tableColumns != null && tableColumns.containsKey(columnName);
	}
	
	public @NonNull @Unmodifiable List<SqlCheckConstraintInfo> checkConstraints(@NonNull String tableName) throws SqlSchemaObjectNotFoundException {
		Objects.requireNonNull(tableName, "Sql table name must not be null");
		
		List<SqlCheckConstraintInfo> constraints = this.checkConstraints.get(tableName);
		if (constraints == null) {
			throw new SqlSchemaObjectNotFoundException("Sql table '" + tableName + "' not found in schema " + this.schema);
		}
		return constraints;
	}
}
