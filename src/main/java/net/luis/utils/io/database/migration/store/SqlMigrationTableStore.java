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
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.migration.SqlMigrationInfo;
import net.luis.utils.io.database.migration.SqlMigrationStatus;
import net.luis.utils.util.Version;
import org.intellij.lang.annotations.Language;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class SqlMigrationTableStore implements SqlMigrationStore {
	
	private static final String TABLE_NAME = "_sql_migrations";
	@Language("SQL")
	private static final String SQL_INITIALIZE = """
		CREATE TABLE IF NOT EXISTS {SqlTable} (
			version VARCHAR(64) NOT NULL PRIMARY KEY,
			description VARCHAR(256) NOT NULL,
			status VARCHAR(32) NOT NULL,
			applied_at TIMESTAMP NULL,
			checksum BIGINT NOT NULL
		);
		""";
	@Language("SQL")
	private static final String SQL_LOAD = """
		SELECT version, description, status, applied_at, checksum
		FROM {SqlTable}
		ORDER BY version;
		""";
	@Language("SQL")
	private static final String SQL_SAVE = """
		INSERT INTO {SqlTable} (version, description, status, applied_at, checksum)
		VALUES (?, ?, ?, ?, ?);
		""";
	@Language("SQL")
	private static final String SQL_UPDATE = """
		UPDATE {SqlTable}
		SET status = ?, applied_at = ?
		WHERE version = ?;
		""";
	
	private final DataSource dataSource;
	
	public SqlMigrationTableStore(@NonNull DataSource dataSource) {
		this.dataSource = Objects.requireNonNull(dataSource, "Sql data source must not be null");
	}
	
	@Override
	public void initialize() throws SqlException {
		try (
			Connection connection = this.dataSource.getConnection();
			Statement statement = connection.createStatement()
		) {
			statement.execute(SQL_INITIALIZE.replace("{SqlTable}", TABLE_NAME));
		} catch (SQLException e) {
			throw new SqlException("Failed to initialize migration table", e);
		}
	}
	
	@Override
	public @NonNull List<SqlMigrationInfo> loadAll() throws SqlException {
		try (
			Connection connection = this.dataSource.getConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(SQL_LOAD.replace("{SqlTable}", TABLE_NAME))
		) {
			List<SqlMigrationInfo> results = Lists.newArrayList();
			
			while (resultSet.next()) {
				Version version = Version.parse(resultSet.getString("version"));
				String description = resultSet.getString("description");
				SqlMigrationStatus status = SqlMigrationStatus.valueOf(resultSet.getString("status"));
				Timestamp timestamp = resultSet.getTimestamp("applied_at");
				Instant appliedAt = timestamp != null ? timestamp.toInstant() : null;
				long checksum = resultSet.getLong("checksum");
				
				results.add(new SqlMigrationInfo(version, description, status, appliedAt, checksum));
			}
			return List.copyOf(results);
		} catch (SQLException e) {
			throw new SqlException("Failed to load migration history", e);
		}
	}
	
	@Override
	public void save(@NonNull SqlMigrationInfo info) throws SqlException {
		Objects.requireNonNull(info, "Sql migration info must not be null");
		
		try (
			Connection connection = this.dataSource.getConnection();
			PreparedStatement statement = connection.prepareStatement(SQL_SAVE.replace("{SqlTable}", TABLE_NAME))
		) {
			statement.setString(1, info.version().toString());
			statement.setString(2, info.description());
			statement.setString(3, info.status().name());
			statement.setTimestamp(4, info.appliedAt() != null ? Timestamp.from(info.appliedAt()) : null);
			statement.setLong(5, info.checksum());
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new SqlException("Failed to save migration info for version " + info.version(), e);
		}
	}
	
	@Override
	public void update(@NonNull Version version, @NonNull SqlMigrationStatus status) throws SqlException {
		Objects.requireNonNull(version, "Sql migration version must not be null");
		Objects.requireNonNull(status, "Sql migration status must not be null");
		
		Instant now = status == SqlMigrationStatus.APPLIED ? Instant.now() : null;
		
		try (Connection connection = this.dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(SQL_UPDATE.replace("{SqlTable}", TABLE_NAME))) {
			statement.setString(1, status.name());
			statement.setTimestamp(2, now != null ? Timestamp.from(now) : null);
			statement.setString(3, version.toString());
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new SqlException("Failed to update migration status for version " + version, e);
		}
	}
}
