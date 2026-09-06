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

package net.luis.utils.io.database.migration.store;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.database.SqlMigrationExecutionException;
import net.luis.utils.io.database.migration.*;
import net.luis.utils.io.database.migration.operation.SqlColumnOptions;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.type.parameter.*;
import net.luis.utils.util.Version;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * Storage for schema snapshots taken during migrations.<br>
 * Stores and loads the columns and check constraints that make up a schema snapshot for a given version.<br>
 *
 * @author Luis-St
 */

public class SqlMigrationSchemaStore {
	
	/**
	 * The data source used to obtain database connections.
	 */
	private final DataSource dataSource;
	/**
	 * The dialect used to render and quote the sql statements.
	 */
	private final SqlDialect dialect;
	
	/**
	 * Constructs a new sql migration schema store with the given data source and dialect.<br>
	 *
	 * @param dataSource The data source used to obtain database connections
	 * @param dialect The dialect used to render and quote the sql statements
	 * @throws NullPointerException If the data source or dialect is null
	 */
	public SqlMigrationSchemaStore(@NonNull DataSource dataSource, @NonNull SqlDialect dialect) {
		this.dataSource = Objects.requireNonNull(dataSource, "Sql data source must not be null");
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
	//region Parameter decomposition
	
	/**
	 * Decomposes the length value from the given parameter.<br>
	 *
	 * @param parameter The parameter to decompose the length from
	 * @return The length if the parameter is a length parameter, otherwise {@code null}
	 */
	private static @Nullable Integer decomposeLength(@Nullable SqlParameter parameter) {
		return parameter instanceof SqlLengthParameter length ? length.length() : null;
	}
	
	/**
	 * Decomposes the precision value from the given parameter.<br>
	 *
	 * @param parameter The parameter to decompose the precision from
	 * @return The precision if the parameter is a precision parameter, otherwise {@code null}
	 */
	private static @Nullable Integer decomposePrecision(@Nullable SqlParameter parameter) {
		return parameter instanceof SqlPrecisionParameter precision ? precision.precision() : null;
	}
	
	/**
	 * Decomposes the scale value from the given parameter.<br>
	 *
	 * @param parameter The parameter to decompose the scale from
	 * @return The scale if the parameter is a precision parameter, otherwise {@code null}
	 */
	private static @Nullable Integer decomposeScale(@Nullable SqlParameter parameter) {
		return parameter instanceof SqlPrecisionParameter precision ? precision.scale() : null;
	}
	
	/**
	 * Decomposes the fractional digits value from the given parameter.<br>
	 *
	 * @param parameter The parameter to decompose the fractional digits from
	 * @return The fractional digits if the parameter is a fractional parameter, otherwise {@code null}
	 */
	private static @Nullable Integer decomposeFractional(@Nullable SqlParameter parameter) {
		return parameter instanceof SqlFractionalParameter fractional ? fractional.digits() : null;
	}
	
	/**
	 * Sets the given integer value on the prepared statement at the given index.<br>
	 * If the value is {@code null}, an sql null of type {@link Types#INTEGER} is set instead.<br>
	 *
	 * @param statement The prepared statement to set the value on
	 * @param index The parameter index to set the value at
	 * @param value The value to set or {@code null} to set sql null
	 * @throws SQLException If the value could not be set on the statement
	 */
	private static void setNullableInt(@NonNull PreparedStatement statement, int index, @Nullable Integer value) throws SQLException {
		if (value != null) {
			statement.setInt(index, value);
		} else {
			statement.setNull(index, Types.INTEGER);
		}
	}
	
	/**
	 * Sets the given string value on the prepared statement at the given index.<br>
	 * If the value is {@code null}, an sql null of type {@link Types#VARCHAR} is set instead.<br>
	 *
	 * @param statement The prepared statement to set the value on
	 * @param index The parameter index to set the value at
	 * @param value The value to set or {@code null} to set sql null
	 * @throws SQLException If the value could not be set on the statement
	 */
	private static void setNullableString(@NonNull PreparedStatement statement, int index, @Nullable String value) throws SQLException {
		if (value != null) {
			statement.setString(index, value);
		} else {
			statement.setNull(index, Types.VARCHAR);
		}
	}
	
	/**
	 * Checks whether the given result set contains a column with the given name.<br>
	 * This keeps snapshots that were written by an earlier version of the library, whose store table does not contain every column yet, readable.<br>
	 *
	 * @param rs The result set to check
	 * @param columnName The name of the column to look for
	 * @return An optional holding whether the result set contains the column, or an empty optional if the driver does not expose the result set metadata
	 * @throws SQLException If the result set metadata could not be read
	 */
	private static @NonNull Optional<Boolean> containsColumn(@NonNull ResultSet rs, @NonNull String columnName) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		if (meta == null) {
			return Optional.empty();
		}
		
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			if (columnName.equalsIgnoreCase(meta.getColumnLabel(i))) {
				return Optional.of(true);
			}
		}
		return Optional.of(false);
	}
	
	/**
	 * Reconstructs the parameter of a column from the current row of the given result set.<br>
	 * Reads the length, precision, scale and fractional columns and rebuilds the matching parameter.<br>
	 *
	 * @param rs The result set positioned at the row to read the parameter from
	 * @return The reconstructed parameter or {@code null} if the column has no length, precision or fractional information
	 * @throws SQLException If the parameter values could not be read from the result set
	 */
	private static @Nullable SqlParameter reconstructParameter(@NonNull ResultSet rs) throws SQLException {
		int length = rs.getInt("length");
		if (!rs.wasNull()) {
			return SqlParameter.length(length);
		}
		
		int precision = rs.getInt("precision");
		if (!rs.wasNull()) {
			int scale = rs.getInt("scale");
			return SqlParameter.precision(precision, scale);
		}
		
		int fractional = rs.getInt("fractional");
		if (!rs.wasNull()) {
			return SqlParameter.fractional(fractional);
		}
		return null;
	}
	//endregion
	
	/**
	 * Initializes the schema store by creating the tables used to store the columns and check constraints.<br>
	 * @throws SqlException If the schema store tables could not be initialized
	 */
	public void initialize() throws SqlException {
		try (Connection connection = this.dataSource.getConnection(); Statement statement = connection.createStatement()) {
			statement.execute(this.dialect.getCreateSchemaColumnsTableSql());
			statement.execute(this.dialect.getCreateSchemaCheckConstraintsTableSql());
		} catch (SQLException e) {
			throw new SqlMigrationExecutionException("Failed to initialize schema store tables", e);
		}
		this.upgradeSchemaColumnsTable();
	}
	
	/**
	 * Adds the columns missing from a schema columns table that was created by an earlier version of the library.<br>
	 * A table created by the current version already contains every column, in which case this does nothing.<br>
	 *
	 * @throws SqlException If the schema columns table could not be upgraded
	 */
	private void upgradeSchemaColumnsTable() throws SqlException {
		try (Connection connection = this.dataSource.getConnection()) {
			boolean present;
			try (PreparedStatement statement = connection.prepareStatement(this.dialect.getSelectSchemaColumnsSql())) {
				statement.setString(1, "");
				try (ResultSet rs = statement.executeQuery()) {
					present = containsColumn(rs, "type_identifier").orElse(true);
				}
			}
			
			if (present) {
				return;
			}
			
			SqlTable<Void> table = SqlTable.create(Void.class, SqlDialect.SCHEMA_COLUMNS_TABLE);
			SqlColumn<Void, String> column = table.column("type_identifier", SqlTypes.STRING.configure(SqlParameter.length(64)), _ -> null);
			String sql = this.dialect.migrationRenderer().renderAddColumn(table, column, column.type(), SqlColumnOptions.EMPTY).sql();
			
			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
			}
		} catch (SQLException e) {
			throw new SqlMigrationExecutionException("Failed to upgrade the schema columns store table", e);
		}
	}
	
	/**
	 * Saves a schema snapshot for the given version in a single transaction.<br>
	 * The columns and check constraints are committed together and rolled back on failure.<br>
	 *
	 * @param version The version the snapshot belongs to
	 * @param columnInfos The column infos to save
	 * @param checkConstraints The check constraints to save, keyed by table name
	 * @throws NullPointerException If the version, column infos or check constraints are null
	 * @throws SqlException If the schema snapshot could not be saved
	 */
	public void save(@NonNull Version version, @NonNull List<SqlSchemaColumnInfo> columnInfos, @NonNull Map<String, List<SqlCheckConstraintInfo>> checkConstraints) throws SqlException {
		Objects.requireNonNull(version, "Version must not be null");
		Objects.requireNonNull(columnInfos, "Sql column infos must not be null");
		Objects.requireNonNull(checkConstraints, "Sql check constraints must not be null");
		
		try (Connection connection = this.dataSource.getConnection()) {
			connection.setAutoCommit(false);
			try {
				this.save(connection, version, columnInfos, checkConstraints);
				connection.commit();
			} catch (SQLException e) {
				connection.rollback();
				throw e;
			}
		} catch (SQLException e) {
			throw new SqlMigrationExecutionException("Failed to save schema snapshot for version " + version, e);
		}
	}
	
	/**
	 * Saves a schema snapshot for the given version using the given connection.<br>
	 * Transaction handling is left to the caller.<br>
	 *
	 * @param connection The connection to use
	 * @param version The version the snapshot belongs to
	 * @param columnInfos The column infos to save
	 * @param checkConstraints The check constraints to save, keyed by table name
	 * @throws NullPointerException If the connection, version, column infos or check constraints are null
	 * @throws SqlException If the schema snapshot could not be saved
	 */
	public void save(@NonNull Connection connection, @NonNull Version version, @NonNull List<SqlSchemaColumnInfo> columnInfos, @NonNull Map<String, List<SqlCheckConstraintInfo>> checkConstraints) throws SqlException {
		Objects.requireNonNull(connection, "Connection must not be null");
		Objects.requireNonNull(version, "Version must not be null");
		Objects.requireNonNull(columnInfos, "Sql column infos must not be null");
		Objects.requireNonNull(checkConstraints, "Sql check constraints must not be null");
		
		String versionStr = version.toString();
		try (PreparedStatement statement = connection.prepareStatement(this.dialect.getInsertSchemaColumnSql())) {
			for (SqlSchemaColumnInfo info : columnInfos) {
				statement.setString(1, versionStr);
				statement.setString(2, info.tableName());
				statement.setString(3, info.columnName());
				statement.setInt(4, info.jdbcType());
				setNullableInt(statement, 5, decomposeLength(info.parameter()));
				setNullableInt(statement, 6, decomposePrecision(info.parameter()));
				setNullableInt(statement, 7, decomposeScale(info.parameter()));
				setNullableInt(statement, 8, decomposeFractional(info.parameter()));
				statement.setBoolean(9, info.nullable());
				statement.setBoolean(10, info.autoIncrement());
				statement.setBoolean(11, info.primaryKey());
				statement.setBoolean(12, info.unique());
				statement.setInt(13, info.ordinalPosition());
				setNullableString(statement, 14, info.typeIdentifier());
				statement.addBatch();
			}
			statement.executeBatch();
		} catch (SQLException e) {
			throw new SqlMigrationExecutionException("Failed to save column infos for version " + version, e);
		}
		
		try (PreparedStatement statement = connection.prepareStatement(this.dialect.getInsertSchemaCheckConstraintSql())) {
			for (Map.Entry<String, List<SqlCheckConstraintInfo>> entry : checkConstraints.entrySet()) {
				
				String tableName = entry.getKey();
				for (SqlCheckConstraintInfo constraint : entry.getValue()) {
					statement.setString(1, versionStr);
					statement.setString(2, tableName);
					statement.setString(3, constraint.constraintName());
					statement.setString(4, constraint.checkClause());
					statement.addBatch();
				}
			}
			
			statement.executeBatch();
		} catch (SQLException e) {
			throw new SqlMigrationExecutionException("Failed to save check constraints for version " + version, e);
		}
	}
	
	/**
	 * Loads the schema snapshot stored for the given version.<br>
	 *
	 * @param version The version to load the snapshot for
	 * @return The loaded schema snapshot or {@code null} if no snapshot exists for the version
	 * @throws NullPointerException If the version is null
	 * @throws SqlException If the schema snapshot could not be loaded
	 */
	public @Nullable SqlSchemaSnapshot load(@NonNull Version version) throws SqlException {
		Objects.requireNonNull(version, "Version must not be null");
		String versionStr = version.toString();
		
		List<SqlSchemaColumnInfo> columnInfos = Lists.newArrayList();
		try (Connection connection = this.dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(this.dialect.getSelectSchemaColumnsSql())) {
			statement.setString(1, versionStr);
			try (ResultSet rs = statement.executeQuery()) {
				boolean hasTypeIdentifier = containsColumn(rs, "type_identifier").orElse(false);
				while (rs.next()) {
					SqlParameter parameter = reconstructParameter(rs);
					
					columnInfos.add(new SqlSchemaColumnInfo(
						rs.getString("table_name"),
						rs.getString("column_name"),
						rs.getInt("jdbc_type"),
						parameter,
						rs.getBoolean("is_nullable"),
						rs.getBoolean("is_auto_increment"),
						rs.getBoolean("is_primary_key"),
						rs.getBoolean("is_unique"),
						rs.getInt("ordinal_position"),
						hasTypeIdentifier ? rs.getString("type_identifier") : null
					));
				}
			}
		} catch (SQLException e) {
			throw new SqlMigrationExecutionException("Failed to load column infos for version " + version, e);
		}
		
		if (columnInfos.isEmpty()) {
			return null;
		}
		
		Map<String, List<SqlCheckConstraintInfo>> checkConstraints = Maps.newLinkedHashMap();
		try (
			Connection connection = this.dataSource.getConnection();
			PreparedStatement statement = connection.prepareStatement(this.dialect.getSelectSchemaCheckConstraintsSql())
		) {
			statement.setString(1, versionStr);
			
			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					String tableName = rs.getString("table_name");
					checkConstraints.computeIfAbsent(tableName, k -> Lists.newArrayList()).add(new SqlCheckConstraintInfo(rs.getString("constraint_name"), rs.getString("check_clause")));
				}
			}
		} catch (SQLException e) {
			throw new SqlMigrationExecutionException("Failed to load check constraints for version " + version, e);
		}
		return new SqlSchemaSnapshot(List.copyOf(columnInfos), Collections.unmodifiableMap(checkConstraints));
	}
	
	/**
	 * Deletes the schema snapshot stored for the given version in a single transaction.<br>
	 * The columns and check constraints are removed together and rolled back on failure.<br>
	 *
	 * @param version The version to delete the snapshot for
	 * @throws NullPointerException If the version is null
	 * @throws SqlException If the schema snapshot could not be deleted
	 */
	public void delete(@NonNull Version version) throws SqlException {
		Objects.requireNonNull(version, "Version must not be null");
		
		try (Connection connection = this.dataSource.getConnection()) {
			connection.setAutoCommit(false);
			try {
				this.delete(connection, version);
				connection.commit();
			} catch (SQLException e) {
				connection.rollback();
				throw e;
			}
		} catch (SQLException e) {
			throw new SqlMigrationExecutionException("Failed to delete schema snapshot for version " + version, e);
		}
	}
	
	/**
	 * Deletes the schema snapshot stored for the given version using the given connection.<br>
	 * Transaction handling is left to the caller.<br>
	 *
	 * @param connection The connection to use
	 * @param version The version to delete the snapshot for
	 * @throws NullPointerException If the connection or version is null
	 * @throws SqlException If the schema snapshot could not be deleted
	 */
	public void delete(@NonNull Connection connection, @NonNull Version version) throws SqlException {
		Objects.requireNonNull(connection, "Connection must not be null");
		Objects.requireNonNull(version, "Version must not be null");
		String versionStr = version.toString();
		
		try (PreparedStatement statement = connection.prepareStatement(this.dialect.getDeleteSchemaColumnsSql())) {
			statement.setString(1, versionStr);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new SqlMigrationExecutionException("Failed to delete column infos for version " + version, e);
		}
		
		try (PreparedStatement statement = connection.prepareStatement(this.dialect.getDeleteSchemaCheckConstraintsSql())) {
			statement.setString(1, versionStr);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new SqlMigrationExecutionException("Failed to delete check constraints for version " + version, e);
		}
	}
}
