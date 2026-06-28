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

import com.google.common.collect.*;
import net.luis.utils.io.database.SqlDatabase;
import net.luis.utils.io.database.audit.SqlAuditColumn;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.SqlSchemaObjectNotFoundException;
import net.luis.utils.io.database.exception.database.SqlSchemaIntrospectionException;
import net.luis.utils.io.database.migration.operation.*;
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
 * Represents a snapshot of a database schema as observed during a migration.<br>
 * Holds the introspected tables, columns and check constraints of a single schema so that<br>
 * migration code can query the existing structure (for example whether a table or column exists)<br>
 * without issuing further database queries.<br>
 * <p>
 *     Instances are immutable and are created through the static factory methods, either by loading<br>
 *     the live schema from a database, reconstructing it from a snapshot or applying a list of<br>
 *     migration operations on top of an existing schema.
 * </p>
 *
 * @author Luis-St
 */

public final class SqlMigrationSchema {
	
	/**
	 * The name of the schema this snapshot represents.
	 */
	private final String schema;
	/**
	 * The phantom tables of the schema mapped by their table name.
	 */
	private final Map<String, SqlTable<Void>> tables;
	/**
	 * The phantom columns of the schema mapped by table name and then by column name.
	 */
	private final Map<String, Map<String, SqlColumn<Void, ?>>> columns;
	/**
	 * The check constraints of the schema mapped by their table name.
	 */
	private final Map<String, List<SqlCheckConstraintInfo>> checkConstraints;
	
	/**
	 * Constructs a new sql migration schema with the given schema name, tables, columns and check constraints.<br>
	 *
	 * @param schema The name of the schema
	 * @param tables The tables of the schema mapped by their name
	 * @param columns The columns of the schema mapped by table name and column name
	 * @param checkConstraints The check constraints of the schema mapped by their table name
	 * @throws NullPointerException If any of the arguments is null
	 */
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
	
	/**
	 * Loads the schema snapshot from the given database using the default schema {@code public}.<br>
	 *
	 * @param database The database to load the schema from
	 * @return The loaded schema snapshot
	 * @throws NullPointerException If the database is null
	 * @throws SqlException If the schema metadata could not be loaded
	 */
	public static @NonNull SqlMigrationSchema load(@NonNull SqlDatabase database) throws SqlException {
		Objects.requireNonNull(database, "Sql database must not be null");
		return load(database.getDataSource(), database.getDialect(), "public");
	}
	
	/**
	 * Loads the schema snapshot from the given database using the given schema name.<br>
	 *
	 * @param database The database to load the schema from
	 * @param schema The name of the schema to load
	 * @return The loaded schema snapshot
	 * @throws NullPointerException If the database or schema is null
	 * @throws SqlException If the schema metadata could not be loaded
	 */
	public static @NonNull SqlMigrationSchema load(@NonNull SqlDatabase database, @NonNull String schema) throws SqlException {
		Objects.requireNonNull(database, "Sql database must not be null");
		return load(database.getDataSource(), database.getDialect(), schema);
	}
	
	/**
	 * Loads the schema snapshot from the given data source using the given dialect and the default schema {@code public}.<br>
	 *
	 * @param dataSource The data source to obtain a connection from
	 * @param dialect The dialect used to introspect dialect specific metadata
	 * @return The loaded schema snapshot
	 * @throws NullPointerException If the data source or dialect is null
	 * @throws SqlException If the schema metadata could not be loaded
	 */
	public static @NonNull SqlMigrationSchema load(@NonNull DataSource dataSource, @NonNull SqlDialect dialect) throws SqlException {
		return load(dataSource, dialect, "public");
	}
	
	/**
	 * Loads the schema snapshot from the given data source using the given dialect and schema name.<br>
	 * A connection is obtained from the data source and closed again after the metadata has been read.<br>
	 *
	 * @param dataSource The data source to obtain a connection from
	 * @param dialect The dialect used to introspect dialect specific metadata
	 * @param schema The name of the schema to load
	 * @return The loaded schema snapshot
	 * @throws NullPointerException If the data source, dialect or schema is null
	 * @throws SqlException If the connection could not be obtained or the schema metadata could not be loaded
	 */
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
	
	/**
	 * Loads the schema snapshot from the given connection using the given dialect and schema name.<br>
	 * Reads tables, columns, primary keys, unique columns and check constraints from the connection metadata.<br>
	 * The given connection is not closed by this method.<br>
	 *
	 * @param connection The connection to read the metadata from
	 * @param dialect The dialect used to introspect dialect specific metadata
	 * @param schema The name of the schema to load
	 * @return The loaded schema snapshot
	 * @throws NullPointerException If the connection, dialect or schema is null
	 * @throws SqlException If the schema metadata could not be loaded
	 */
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
	
	/**
	 * Creates an empty schema snapshot without any tables, columns or check constraints using the default schema {@code public}.<br>
	 * @return The empty schema snapshot
	 */
	public static @NonNull SqlMigrationSchema empty() {
		return empty("public");
	}
	
	/**
	 * Creates an empty schema snapshot without any tables, columns or check constraints using the given schema name.<br>
	 *
	 * @param schema The name of the schema
	 * @return The empty schema snapshot
	 * @throws NullPointerException If the schema is null
	 */
	public static @NonNull SqlMigrationSchema empty(@NonNull String schema) {
		Objects.requireNonNull(schema, "Sql schema must not be null");
		
		return new SqlMigrationSchema(schema, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap());
	}
	
	/**
	 * Reconstructs a schema snapshot from the given column infos and check constraints using the default schema {@code public}.<br>
	 *
	 * @param columnInfos The column infos describing the columns of all tables
	 * @param checkConstraints The check constraints mapped by their table name
	 * @return The reconstructed schema snapshot
	 * @throws NullPointerException If the column infos or check constraints are null
	 * @throws SqlException If the snapshot could not be reconstructed
	 */
	public static @NonNull SqlMigrationSchema fromSnapshot(@NonNull List<SqlSchemaColumnInfo> columnInfos, @NonNull Map<String, List<SqlCheckConstraintInfo>> checkConstraints) throws SqlException {
		return fromSnapshot("public", columnInfos, checkConstraints);
	}
	
	/**
	 * Reconstructs a schema snapshot from the given column infos and check constraints using the given schema name.<br>
	 * The column infos are grouped by table name and ordered by their ordinal position to rebuild the table structure.<br>
	 *
	 * @param schema The name of the schema
	 * @param columnInfos The column infos describing the columns of all tables
	 * @param checkConstraints The check constraints mapped by their table name
	 * @return The reconstructed schema snapshot
	 * @throws NullPointerException If the schema, column infos or check constraints are null
	 * @throws SqlException If the snapshot could not be reconstructed
	 */
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
	
	/**
	 * Applies the given migration operations on top of the given base schema and returns the resulting snapshot.<br>
	 * The operations are applied in order to a mutable copy of the base schema without modifying the base schema itself.<br>
	 *
	 * @param base The base schema the operations are applied on
	 * @param operations The migration operations to apply in order
	 * @return The resulting schema snapshot after applying all operations
	 * @throws NullPointerException If the base schema or operations are null
	 * @throws SqlException If the resulting snapshot could not be built
	 */
	public static @NonNull SqlMigrationSchema applyOperations(@NonNull SqlMigrationSchema base, @NonNull List<SqlMigrationOperation> operations) throws SqlException {
		Objects.requireNonNull(base, "Base sql migration schema must not be null");
		Objects.requireNonNull(operations, "Sql migration operations must not be null");
		
		Map<String, List<SqlSchemaColumnInfo>> tables = Maps.newLinkedHashMap();
		for (SqlSchemaColumnInfo info : base.extractColumnInfos()) {
			tables.computeIfAbsent(info.tableName(), _ -> Lists.newArrayList()).add(info);
		}
		
		Map<String, List<SqlCheckConstraintInfo>> checks = Maps.newLinkedHashMap();
		for (Map.Entry<String, List<SqlCheckConstraintInfo>> entry : base.extractCheckConstraints().entrySet()) {
			checks.put(entry.getKey(), Lists.newArrayList(entry.getValue()));
		}
		
		for (SqlMigrationOperation operation : operations) {
			applyOperation(tables, checks, operation);
		}
		
		List<SqlSchemaColumnInfo> columnInfos = Lists.newArrayList();
		for (List<SqlSchemaColumnInfo> tableColumns : tables.values()) {
			int ordinal = 0;
			for (SqlSchemaColumnInfo info : tableColumns) {
				columnInfos.add(withOrdinalPosition(info, ordinal++));
			}
		}
		return fromSnapshot(base.schema, columnInfos, checks);
	}
	
	/**
	 * Applies the given migration operation to the given mutable table and check constraint maps.<br>
	 * The maps are modified in place to reflect the structural changes described by the operation.<br>
	 *
	 * @param tables The mutable column infos of all tables mapped by their table name
	 * @param checks The mutable check constraints mapped by their table name
	 * @param operation The migration operation to apply
	 * @throws NullPointerException If the tables, checks or operation is null
	 */
	private static void applyOperation(
		@NonNull Map<String, List<SqlSchemaColumnInfo>> tables,
		@NonNull Map<String, List<SqlCheckConstraintInfo>> checks,
		@NonNull SqlMigrationOperation operation
	) {
		switch (operation) {
			case SqlCreateTableOperation op -> {
				String tableName = op.table().name();
				Set<String> primaryKeyNames = Sets.newLinkedHashSet();
				for (SqlColumn<?, ?> column : op.primaryKeyColumns()) {
					primaryKeyNames.add(column.name());
				}
				
				List<SqlSchemaColumnInfo> columns = Lists.newArrayList();
				int ordinal = 0;
				for (SqlColumnDefinition definition : op.columns()) {
					boolean primaryKey = primaryKeyNames.contains(definition.column().name());
					columns.add(columnInfo(tableName, definition.column().name(), definition.type(), !definition.options().notNull(), definition.options().autoIncrement(), primaryKey, definition.options().unique(), ordinal++));
				}
				tables.put(tableName, columns);
				checks.putIfAbsent(tableName, Lists.newArrayList());
			}
			case SqlDropTableOperation op -> {
				tables.remove(op.table().name());
				checks.remove(op.table().name());
			}
			case SqlRenameTableOperation op -> {
				String from = op.from().name();
				String to = op.to().name();
				
				List<SqlSchemaColumnInfo> columns = tables.remove(from);
				if (columns != null) {
					List<SqlSchemaColumnInfo> renamed = Lists.newArrayList();
					for (SqlSchemaColumnInfo info : columns) {
						renamed.add(withTableName(info, to));
					}
					tables.put(to, renamed);
				}
				
				List<SqlCheckConstraintInfo> tableChecks = checks.remove(from);
				if (tableChecks != null) {
					checks.put(to, tableChecks);
				}
			}
			case SqlAddColumnOperation op -> {
				String tableName = op.column().owningTable().name();
				List<SqlSchemaColumnInfo> columns = tables.get(tableName);
				if (columns != null) {
					columns.add(columnInfo(tableName, op.column().name(), op.type(), !op.options().notNull(), op.options().autoIncrement(), false, op.options().unique(), columns.size()));
				}
			}
			case SqlDropColumnOperation op -> {
				List<SqlSchemaColumnInfo> columns = tables.get(op.column().owningTable().name());
				if (columns != null) {
					columns.removeIf(info -> info.columnName().equals(op.column().name()));
				}
			}
			case SqlRenameColumnOperation op -> {
				List<SqlSchemaColumnInfo> columns = tables.get(op.from().owningTable().name());
				if (columns != null) {
					for (int i = 0; i < columns.size(); i++) {
						if (columns.get(i).columnName().equals(op.from().name())) {
							columns.set(i, withColumnName(columns.get(i), op.to().name()));
							break;
						}
					}
				}
			}
			case SqlAlterColumnOperation op -> {
				List<SqlSchemaColumnInfo> columns = tables.get(op.column().owningTable().name());
				if (columns != null) {
					for (int i = 0; i < columns.size(); i++) {
						SqlSchemaColumnInfo info = columns.get(i);
						if (info.columnName().equals(op.column().name())) {
							for (SqlColumnAlteration alteration : op.alterations()) {
								info = applyAlteration(info, alteration);
							}
							columns.set(i, info);
							break;
						}
					}
				}
			}
			case SqlAddCheckConstraintOperation op -> checks.computeIfAbsent(op.table().name(), _ -> Lists.newArrayList()).add(new SqlCheckConstraintInfo(op.name(), op.condition().toString()));
			case SqlDropConstraintOperation op -> {
				List<SqlCheckConstraintInfo> tableChecks = checks.get(op.table().name());
				if (tableChecks != null) {
					tableChecks.removeIf(info -> info.constraintName().equals(op.name()));
				}
			}
			case SqlEnableAuditingOperation op -> {
				String tableName = op.table().name();
				List<SqlSchemaColumnInfo> columns = tables.get(tableName);
				if (columns != null) {
					for (SqlAuditColumn auditColumn : op.config().auditColumns()) {
						columns.add(columnInfo(tableName, auditColumn.name(), auditColumn.type(), auditColumn.nullable(), false, false, false, columns.size()));
					}
				}
			}
			case SqlDisableAuditingOperation op -> {
				List<SqlSchemaColumnInfo> columns = tables.get(op.table().name());
				if (columns != null) {
					Set<String> auditNames = new LinkedHashSet<>();
					for (SqlAuditColumn auditColumn : op.config().auditColumns()) {
						auditNames.add(auditColumn.name());
					}
					columns.removeIf(info -> auditNames.contains(info.columnName()));
				}
			}
			case SqlAddCompositePrimaryKeyOperation op -> setColumnFlags(tables, op.table().name(), op.columns(), true, false);
			case SqlAddUniqueConstraintOperation op -> setColumnFlags(tables, op.table().name(), op.columns(), false, true);
			case SqlAddForeignKeyOperation _, SqlCreateIndexOperation _, SqlDropIndexOperation _, SqlRenameIndexOperation _, SqlExecuteDataOperation _ -> {}
		}
	}
	
	/**
	 * Marks the given columns of the given table as primary key and/or unique.<br>
	 * For each matching column info the primary key and unique flags are combined with the given values<br>
	 * by replacing the column info in place; columns of unknown tables are ignored.<br>
	 *
	 * @param tables The mutable column infos of all tables mapped by their table name
	 * @param tableName The name of the table whose columns are updated
	 * @param columns The columns to mark
	 * @param primaryKey Whether the matching columns should be marked as primary key
	 * @param unique Whether the matching columns should be marked as unique
	 * @throws NullPointerException If the tables, table name or columns is null
	 */
	private static void setColumnFlags(@NonNull Map<String, List<SqlSchemaColumnInfo>> tables, @NonNull String tableName, @NonNull List<SqlColumn<?, ?>> columns, boolean primaryKey, boolean unique) {
		List<SqlSchemaColumnInfo> tableColumns = tables.get(tableName);
		if (tableColumns == null) {
			return;
		}
		
		Set<String> names = new LinkedHashSet<>();
		for (SqlColumn<?, ?> column : columns) {
			names.add(column.name());
		}
		
		for (int i = 0; i < tableColumns.size(); i++) {
			SqlSchemaColumnInfo info = tableColumns.get(i);
			if (names.contains(info.columnName())) {
				tableColumns.set(i, new SqlSchemaColumnInfo(
					info.tableName(), info.columnName(), info.jdbcType(), info.parameter(), info.nullable(), info.autoIncrement(), primaryKey || info.primaryKey(), unique || info.unique(), info.ordinalPosition()
				));
			}
		}
	}
	
	/**
	 * Applies the given column alteration to the given column info and returns the resulting info.<br>
	 * Type and nullability alterations produce an updated copy, while default value alterations leave the info unchanged.<br>
	 *
	 * @param info The column info to alter
	 * @param alteration The alteration to apply
	 * @return The altered column info
	 * @throws NullPointerException If the info or alteration is null
	 */
	private static @NonNull SqlSchemaColumnInfo applyAlteration(@NonNull SqlSchemaColumnInfo info, @NonNull SqlColumnAlteration alteration) {
		return switch (alteration) {
			case SqlSetTypeAlteration setType -> {
				SqlParameter parameter = setType.type().baseType() instanceof ParameterizedSqlType<?, ?> parameterized ? parameterized.parameter() : null;
				yield new SqlSchemaColumnInfo(info.tableName(), info.columnName(), setType.type().jdbcType(), parameter, info.nullable(), info.autoIncrement(), info.primaryKey(), info.unique(), info.ordinalPosition());
			}
			case SqlSetNullableAlteration setNullable -> new SqlSchemaColumnInfo(
				info.tableName(), info.columnName(), info.jdbcType(), info.parameter(), setNullable.nullable(), info.autoIncrement(), info.primaryKey(), info.unique(), info.ordinalPosition()
			);
			case SqlSetDefaultAlteration _, SqlDropDefaultAlteration _ -> info;
		};
	}
	
	/**
	 * Creates a new column info from the given values.<br>
	 * The sql parameter is derived from the type if it is a {@link ParameterizedSqlType}, otherwise it is {@code null}.<br>
	 *
	 * @param tableName The name of the table the column belongs to
	 * @param columnName The name of the column
	 * @param type The sql type of the column
	 * @param nullable Whether the column is nullable
	 * @param autoIncrement Whether the column is auto incremented
	 * @param primaryKey Whether the column is part of the primary key
	 * @param unique Whether the column is unique
	 * @param ordinal The ordinal position of the column within the table
	 * @return The created column info
	 * @throws NullPointerException If the table name, column name or type is null
	 */
	private static @NonNull SqlSchemaColumnInfo columnInfo(@NonNull String tableName, @NonNull String columnName, @NonNull SqlType<?> type, boolean nullable, boolean autoIncrement, boolean primaryKey, boolean unique, int ordinal) {
		SqlParameter parameter = type.baseType() instanceof ParameterizedSqlType<?, ?> parameterized ? parameterized.parameter() : null;
		return new SqlSchemaColumnInfo(tableName, columnName, type.jdbcType(), parameter, nullable, autoIncrement, primaryKey, unique, ordinal);
	}
	
	/**
	 * Returns a copy of the given column info with the table name replaced by the given table name.<br>
	 *
	 * @param info The column info to copy
	 * @param tableName The new table name
	 * @return The copied column info with the new table name
	 * @throws NullPointerException If the info or table name is null
	 */
	private static @NonNull SqlSchemaColumnInfo withTableName(@NonNull SqlSchemaColumnInfo info, @NonNull String tableName) {
		return new SqlSchemaColumnInfo(tableName, info.columnName(), info.jdbcType(), info.parameter(), info.nullable(), info.autoIncrement(), info.primaryKey(), info.unique(), info.ordinalPosition());
	}
	
	/**
	 * Returns a copy of the given column info with the column name replaced by the given column name.<br>
	 *
	 * @param info The column info to copy
	 * @param columnName The new column name
	 * @return The copied column info with the new column name
	 * @throws NullPointerException If the info or column name is null
	 */
	private static @NonNull SqlSchemaColumnInfo withColumnName(@NonNull SqlSchemaColumnInfo info, @NonNull String columnName) {
		return new SqlSchemaColumnInfo(info.tableName(), columnName, info.jdbcType(), info.parameter(), info.nullable(), info.autoIncrement(), info.primaryKey(), info.unique(), info.ordinalPosition());
	}
	
	/**
	 * Returns a copy of the given column info with the ordinal position replaced by the given ordinal position.<br>
	 *
	 * @param info The column info to copy
	 * @param ordinalPosition The new ordinal position
	 * @return The copied column info with the new ordinal position
	 * @throws NullPointerException If the info is null
	 */
	private static @NonNull SqlSchemaColumnInfo withOrdinalPosition(@NonNull SqlSchemaColumnInfo info, int ordinalPosition) {
		return new SqlSchemaColumnInfo(info.tableName(), info.columnName(), info.jdbcType(), info.parameter(), info.nullable(), info.autoIncrement(), info.primaryKey(), info.unique(), ordinalPosition);
	}
	
	/**
	 * Discovers the names of all tables of the given schema from the given database metadata.<br>
	 *
	 * @param meta The database metadata to read the table names from
	 * @param schema The name of the schema to discover the tables of
	 * @return The names of all tables of the schema
	 * @throws NullPointerException If the metadata or schema is null
	 * @throws SQLException If the table names could not be read from the metadata
	 */
	private static @NonNull List<String> discoverTableNames(@NonNull DatabaseMetaData meta, @NonNull String schema) throws SQLException {
		try (ResultSet rs = meta.getTables(null, schema, "%", new String[] { "TABLE" })) {
			List<String> tableNames = Lists.newArrayList();
			
			while (rs.next()) {
				tableNames.add(rs.getString("TABLE_NAME"));
			}
			
			return tableNames;
		}
	}
	
	/**
	 * Loads the names of the primary key columns of the given table from the given database metadata.<br>
	 *
	 * @param meta The database metadata to read the primary key columns from
	 * @param schema The name of the schema the table belongs to
	 * @param tableName The name of the table to load the primary key columns of
	 * @return The names of the primary key columns of the table
	 * @throws NullPointerException If the metadata, schema or table name is null
	 * @throws SQLException If the primary key columns could not be read from the metadata
	 */
	private static @NonNull Set<String> loadPrimaryKeyColumns(@NonNull DatabaseMetaData meta, @NonNull String schema, @NonNull String tableName) throws SQLException {
		try (ResultSet pkRs = meta.getPrimaryKeys(null, schema, tableName)) {
			Set<String> pkColumns = new LinkedHashSet<>();
			
			while (pkRs.next()) {
				pkColumns.add(pkRs.getString("COLUMN_NAME"));
			}
			
			return pkColumns;
		}
	}
	
	/**
	 * Loads the names of the unique columns of the given table from the given database metadata.<br>
	 * Only single column unique indexes are considered, multi column unique indexes are ignored.<br>
	 *
	 * @param meta The database metadata to read the unique indexes from
	 * @param schema The name of the schema the table belongs to
	 * @param tableName The name of the table to load the unique columns of
	 * @return The names of the columns that are covered by a single column unique index
	 * @throws NullPointerException If the metadata, schema or table name is null
	 * @throws SQLException If the unique indexes could not be read from the metadata
	 */
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
	
	/**
	 * Extracts the column infos of all tables in this schema snapshot.<br>
	 * The columns are ordered per table by their position within the table.<br>
	 *
	 * @return An immutable list of the column infos of all tables
	 */
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
	
	/**
	 * Extracts the check constraints of all tables in this schema snapshot.<br>
	 * @return The check constraints mapped by their table name
	 */
	public @NonNull Map<String, List<SqlCheckConstraintInfo>> extractCheckConstraints() {
		return this.checkConstraints;
	}
	
	/**
	 * Returns the table with the given name from this schema snapshot.<br>
	 *
	 * @param name The name of the table
	 * @return The table with the given name
	 * @throws NullPointerException If the name is null
	 * @throws SqlSchemaObjectNotFoundException If no table with the given name exists in this schema
	 */
	public @NonNull SqlTable<Void> table(@NonNull String name) throws SqlSchemaObjectNotFoundException {
		Objects.requireNonNull(name, "Sql table name must not be null");
		
		SqlTable<Void> table = this.tables.get(name);
		if (table == null) {
			throw new SqlSchemaObjectNotFoundException("Sql table '" + name + "' not found in schema " + this.schema);
		}
		return table;
	}
	
	/**
	 * Checks whether a table with the given name exists in this schema snapshot.<br>
	 *
	 * @param name The name of the table
	 * @return {@code true} if a table with the given name exists, otherwise {@code false}
	 * @throws NullPointerException If the name is null
	 */
	public boolean hasTable(@NonNull String name) {
		Objects.requireNonNull(name, "Table name must not be null");
		return this.tables.containsKey(name);
	}
	
	/**
	 * Returns the names of all tables in this schema snapshot.<br>
	 * @return An unmodifiable set of the table names
	 */
	public @NonNull @Unmodifiable Set<String> tableNames() {
		return Collections.unmodifiableSet(this.tables.keySet());
	}
	
	/**
	 * Returns the column with the given name from the given table in this schema snapshot.<br>
	 *
	 * @param tableName The name of the table the column belongs to
	 * @param columnName The name of the column
	 * @return The column with the given name
	 * @throws NullPointerException If the table name or column name is null
	 * @throws SqlSchemaObjectNotFoundException If the table or the column does not exist in this schema
	 */
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
	
	/**
	 * Returns the column with the given name from the given table, typed to the given Java type.<br>
	 * The Java type of the column must be assignable to the requested type.<br>
	 *
	 * @param tableName The name of the table the column belongs to
	 * @param columnName The name of the column
	 * @param type The expected Java type of the column
	 * @return The typed column with the given name
	 * @throws NullPointerException If the table name, column name or type is null
	 * @throws SqlSchemaObjectNotFoundException If the table or column does not exist or its Java type does not match the requested type
	 */
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
	
	/**
	 * Checks whether a column with the given name exists in the given table of this schema snapshot.<br>
	 *
	 * @param tableName The name of the table the column belongs to
	 * @param columnName The name of the column
	 * @return {@code true} if the table exists and contains a column with the given name, otherwise {@code false}
	 * @throws NullPointerException If the table name or column name is null
	 */
	public boolean hasColumn(@NonNull String tableName, @NonNull String columnName) {
		Objects.requireNonNull(tableName, "Sql table name must not be null");
		Objects.requireNonNull(columnName, "Sql column name must not be null");
		
		Map<String, SqlColumn<Void, ?>> tableColumns = this.columns.get(tableName);
		return tableColumns != null && tableColumns.containsKey(columnName);
	}
	
	/**
	 * Returns the check constraints of the table with the given name in this schema snapshot.<br>
	 *
	 * @param tableName The name of the table
	 * @return An unmodifiable list of the check constraints of the table
	 * @throws NullPointerException If the table name is null
	 * @throws SqlSchemaObjectNotFoundException If no table with the given name exists in this schema
	 */
	public @NonNull @Unmodifiable List<SqlCheckConstraintInfo> checkConstraints(@NonNull String tableName) throws SqlSchemaObjectNotFoundException {
		Objects.requireNonNull(tableName, "Sql table name must not be null");
		
		List<SqlCheckConstraintInfo> constraints = this.checkConstraints.get(tableName);
		if (constraints == null) {
			throw new SqlSchemaObjectNotFoundException("Sql table '" + tableName + "' not found in schema " + this.schema);
		}
		return constraints;
	}
}
