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
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.migration.SqlMigrationInfo;
import net.luis.utils.io.database.migration.SqlMigrationStatus;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import net.luis.utils.util.Version;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

@SuppressWarnings({ "SqlSourceToSinkFlow", "DuplicatedCode" })
public class SqlMigrationTableStore implements SqlMigrationStore {
	
	private static final String TABLE_NAME = "_sql_migrations";
	
	private final DataSource dataSource;
	private final SqlDialect dialect;
	
	public SqlMigrationTableStore(@NonNull DataSource dataSource, @NonNull SqlDialect dialect) {
		this.dataSource = Objects.requireNonNull(dataSource, "Sql data source must not be null");
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
	private @NonNull String buildInitializeSql() throws SqlException {
		String table = this.dialect.quoteIdentifier(TABLE_NAME);
		String version = this.dialect.quoteIdentifier("version");
		String description = this.dialect.quoteIdentifier("description");
		String status = this.dialect.quoteIdentifier("status");
		String appliedAt = this.dialect.quoteIdentifier("applied_at");
		
		String varchar64 = this.dialect.getTypeName(SqlTypes.STRING.configure(SqlParameter.length(64)));
		String varchar256 = this.dialect.getTypeName(SqlTypes.STRING.configure(SqlParameter.length(256)));
		String varchar32 = this.dialect.getTypeName(SqlTypes.STRING.configure(SqlParameter.length(32)));
		String timestampType = this.dialect.getTypeName(SqlTypes.LOCAL_DATE_TIME.configure(SqlParameter.fractional(6)));
		
		return "CREATE TABLE IF NOT EXISTS " + table + " (" +
			version + " " + varchar64 + " NOT NULL PRIMARY KEY, " +
			description + " " + varchar256 + " NOT NULL, " +
			status + " " + varchar32 + " NOT NULL, " +
			appliedAt + " " + timestampType + " NULL" +
			")";
	}
	
	private @NonNull String buildLoadSql() {
		String table = this.dialect.quoteIdentifier(TABLE_NAME);
		String version = this.dialect.quoteIdentifier("version");
		String description = this.dialect.quoteIdentifier("description");
		String status = this.dialect.quoteIdentifier("status");
		String appliedAt = this.dialect.quoteIdentifier("applied_at");
		
		return "SELECT " + version + ", " + description + ", " + status + ", " + appliedAt + " FROM " + table;
	}
	
	private @NonNull String buildSaveSql() {
		String table = this.dialect.quoteIdentifier(TABLE_NAME);
		String version = this.dialect.quoteIdentifier("version");
		String description = this.dialect.quoteIdentifier("description");
		String status = this.dialect.quoteIdentifier("status");
		String appliedAt = this.dialect.quoteIdentifier("applied_at");
		
		return "INSERT INTO " + table + " (" + version + ", " + description + ", " + status + ", " + appliedAt + ") VALUES (?, ?, ?, ?)";
	}
	
	private @NonNull String buildUpdateSql() {
		String table = this.dialect.quoteIdentifier(TABLE_NAME);
		String version = this.dialect.quoteIdentifier("version");
		String status = this.dialect.quoteIdentifier("status");
		String appliedAt = this.dialect.quoteIdentifier("applied_at");
		
		return "UPDATE " + table + " SET " + status + " = ?, " + appliedAt + " = ? WHERE " + version + " = ?";
	}
	
	@Override
	public void initialize() throws SqlException {
		try (
			Connection connection = this.dataSource.getConnection();
			Statement statement = connection.createStatement()
		) {
			statement.execute(this.buildInitializeSql());
		} catch (SQLException e) {
			throw new SqlException("Failed to initialize migration table", e);
		}
	}
	
	@Override
	public @NonNull List<SqlMigrationInfo> loadAll() throws SqlException {
		try (
			Connection connection = this.dataSource.getConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(this.buildLoadSql())
		) {
			List<SqlMigrationInfo> results = Lists.newArrayList();
			
			while (resultSet.next()) {
				Version version = Version.parse(resultSet.getString("version"));
				String description = resultSet.getString("description");
				SqlMigrationStatus status = SqlMigrationStatus.valueOf(resultSet.getString("status"));
				Timestamp timestamp = resultSet.getTimestamp("applied_at");
				Instant appliedAt = timestamp != null ? timestamp.toInstant() : null;
				
				results.add(new SqlMigrationInfo(version, description, status, appliedAt));
			}
			results.sort(Comparator.comparing(SqlMigrationInfo::version));
			return List.copyOf(results);
		} catch (SQLException e) {
			throw new SqlException("Failed to load migration history", e);
		}
	}
	
	@Override
	public void save(@NonNull SqlMigrationInfo info) throws SqlException {
		Objects.requireNonNull(info, "Sql migration info must not be null");
		
		try (Connection connection = this.dataSource.getConnection()) {
			this.save(connection, info);
		} catch (SQLException e) {
			throw new SqlException("Failed to save migration info for version " + info.version(), e);
		}
	}
	
	@Override
	public void save(@NonNull Connection connection, @NonNull SqlMigrationInfo info) throws SqlException {
		Objects.requireNonNull(connection, "Connection must not be null");
		Objects.requireNonNull(info, "Sql migration info must not be null");
		
		try (PreparedStatement statement = connection.prepareStatement(this.buildSaveSql())) {
			statement.setString(1, info.version().toString());
			statement.setString(2, info.description());
			statement.setString(3, info.status().name());
			statement.setTimestamp(4, info.appliedAt() != null ? Timestamp.from(info.appliedAt()) : null);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new SqlException("Failed to save migration info for version " + info.version(), e);
		}
	}
	
	@Override
	public void update(@NonNull Version version, @NonNull SqlMigrationStatus status) throws SqlException {
		Objects.requireNonNull(version, "Sql migration version must not be null");
		Objects.requireNonNull(status, "Sql migration status must not be null");
		
		try (Connection connection = this.dataSource.getConnection()) {
			this.update(connection, version, status);
		} catch (SQLException e) {
			throw new SqlException("Failed to update migration status for version " + version, e);
		}
	}
	
	@Override
	public void update(@NonNull Connection connection, @NonNull Version version, @NonNull SqlMigrationStatus status) throws SqlException {
		Objects.requireNonNull(connection, "Connection must not be null");
		Objects.requireNonNull(version, "Sql migration version must not be null");
		Objects.requireNonNull(status, "Sql migration status must not be null");
		
		Instant now = status == SqlMigrationStatus.APPLIED ? Instant.now() : null;
		
		try (PreparedStatement statement = connection.prepareStatement(this.buildUpdateSql())) {
			statement.setString(1, status.name());
			statement.setTimestamp(2, now != null ? Timestamp.from(now) : null);
			statement.setString(3, version.toString());
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new SqlException("Failed to update migration status for version " + version, e);
		}
	}
}
