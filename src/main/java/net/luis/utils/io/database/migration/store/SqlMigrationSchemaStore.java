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
import net.luis.utils.io.database.type.parameter.*;
import net.luis.utils.util.Version;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class SqlMigrationSchemaStore {
	
	private final DataSource dataSource;
	private final SqlDialect dialect;
	
	public SqlMigrationSchemaStore(@NonNull DataSource dataSource, @NonNull SqlDialect dialect) {
		this.dataSource = Objects.requireNonNull(dataSource, "Sql data source must not be null");
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
	//region Parameter decomposition
	private static @Nullable Integer decomposeLength(@Nullable SqlParameter parameter) {
		return parameter instanceof SqlLengthParameter length ? length.length() : null;
	}
	
	private static @Nullable Integer decomposePrecision(@Nullable SqlParameter parameter) {
		return parameter instanceof SqlPrecisionParameter precision ? precision.precision() : null;
	}
	
	private static @Nullable Integer decomposeScale(@Nullable SqlParameter parameter) {
		return parameter instanceof SqlPrecisionParameter precision ? precision.scale() : null;
	}
	
	private static @Nullable Integer decomposeFractional(@Nullable SqlParameter parameter) {
		return parameter instanceof SqlFractionalParameter fractional ? fractional.digits() : null;
	}
	
	private static void setNullableInt(@NonNull PreparedStatement statement, int index, @Nullable Integer value) throws SQLException {
		if (value != null) {
			statement.setInt(index, value);
		} else {
			statement.setNull(index, Types.INTEGER);
		}
	}
	
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
	
	public void initialize() throws SqlException {
		try (Connection connection = this.dataSource.getConnection(); Statement statement = connection.createStatement()) {
			statement.execute(this.dialect.getCreateSchemaColumnsTableSql());
			statement.execute(this.dialect.getCreateSchemaCheckConstraintsTableSql());
		} catch (SQLException e) {
			throw new SqlMigrationExecutionException("Failed to initialize schema store tables", e);
		}
	}
	
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
	
	public @Nullable SqlSchemaSnapshot load(@NonNull Version version) throws SqlException {
		Objects.requireNonNull(version, "Version must not be null");
		String versionStr = version.toString();
		
		List<SqlSchemaColumnInfo> columnInfos = Lists.newArrayList();
		try (Connection connection = this.dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(this.dialect.getSelectSchemaColumnsSql())) {
			statement.setString(1, versionStr);
			try (ResultSet rs = statement.executeQuery()) {
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
						rs.getInt("ordinal_position")
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
