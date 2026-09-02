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
import net.luis.utils.io.database.exception.database.SqlMigrationExecutionException;
import net.luis.utils.io.database.migration.SqlMigrationInfo;
import net.luis.utils.io.database.migration.SqlMigrationStatus;
import net.luis.utils.io.database.migration.operation.SqlColumnOptions;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import net.luis.utils.util.Version;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.*;

/**
 * Implementation of {@link SqlMigrationStore} that records applied migrations in a database table.<br>
 * The migration records are stored in a dedicated table created during initialization.<br>
 *
 * @author Luis-St
 */

@SuppressWarnings({ "DuplicatedCode", "SqlSourceToSinkFlow" })
public class SqlMigrationTableStore implements SqlMigrationStore {
	
	/**
	 * The name of the table used to store the migration records.
	 */
	private static final String TABLE_NAME = "_sql_migrations";
	
	/**
	 * The data source used to obtain database connections.
	 */
	private final DataSource dataSource;
	/**
	 * The dialect used to render and quote the sql statements.
	 */
	private final SqlDialect dialect;
	
	/**
	 * Constructs a new sql migration table store with the given data source and dialect.<br>
	 *
	 * @param dataSource The data source used to obtain database connections
	 * @param dialect The dialect used to render and quote the sql statements
	 * @throws NullPointerException If the data source or dialect is null
	 */
	public SqlMigrationTableStore(@NonNull DataSource dataSource, @NonNull SqlDialect dialect) {
		this.dataSource = Objects.requireNonNull(dataSource, "Sql data source must not be null");
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
	/**
	 * Builds the sql statement used to create the migration table.<br>
	 *
	 * @return The rendered create table sql statement
	 * @throws SqlException If the create table statement could not be rendered
	 */
	private @NonNull String buildInitializeSql() throws SqlException {
		SqlTable<Void> table = SqlTable.create(Void.class, TABLE_NAME);
		table.column("version", SqlTypes.STRING.configure(SqlParameter.length(64)), v -> null, col -> col.primaryKey().notNull());
		table.column("description", SqlTypes.STRING.configure(SqlParameter.length(256)), v -> null, col -> col.notNull());
		table.column("status", SqlTypes.STRING.configure(SqlParameter.length(32)), v -> null, col -> col.notNull());
		table.column("applied_at", SqlTypes.LONG, v -> null);
		table.column("checksum", SqlTypes.STRING.configure(SqlParameter.length(64)), v -> null);
		table.column("statements", SqlTypes.TEXT, v -> null);
		return this.dialect.tableRenderer().renderCreateTable(table, true).sql();
	}
	
	/**
	 * Builds the sql statement used to load all migration records from the migration table.<br>
	 * @return The rendered select sql statement
	 */
	private @NonNull String buildLoadSql() {
		String table = this.dialect.quoteIdentifier(TABLE_NAME);
		String version = this.dialect.quoteIdentifier("version");
		String description = this.dialect.quoteIdentifier("description");
		String status = this.dialect.quoteIdentifier("status");
		String appliedAt = this.dialect.quoteIdentifier("applied_at");
		String checksum = this.dialect.quoteIdentifier("checksum");
		String statements = this.dialect.quoteIdentifier("statements");
		
		return "SELECT " + version + ", " + description + ", " + status + ", " + appliedAt + ", " + checksum + ", " + statements + " FROM " + table;
	}
	
	/**
	 * Builds the sql statement used to insert a migration record into the migration table.<br>
	 * @return The rendered insert sql statement
	 */
	private @NonNull String buildSaveSql() {
		String table = this.dialect.quoteIdentifier(TABLE_NAME);
		String version = this.dialect.quoteIdentifier("version");
		String description = this.dialect.quoteIdentifier("description");
		String status = this.dialect.quoteIdentifier("status");
		String appliedAt = this.dialect.quoteIdentifier("applied_at");
		String checksum = this.dialect.quoteIdentifier("checksum");
		String statements = this.dialect.quoteIdentifier("statements");
		
		return "INSERT INTO " + table + " (" + version + ", " + description + ", " + status + ", " + appliedAt + ", " + checksum + ", " + statements + ") VALUES (?, ?, ?, ?, ?, ?)";
	}
	
	/**
	 * Builds the sql statement used to overwrite an existing migration record.<br>
	 * This is used to re-record a version that is already present, for example when a rolled back migration is applied again.<br>
	 *
	 * @return The rendered update sql statement
	 */
	private @NonNull String buildOverwriteSql() {
		String table = this.dialect.quoteIdentifier(TABLE_NAME);
		String version = this.dialect.quoteIdentifier("version");
		String description = this.dialect.quoteIdentifier("description");
		String status = this.dialect.quoteIdentifier("status");
		String appliedAt = this.dialect.quoteIdentifier("applied_at");
		String checksum = this.dialect.quoteIdentifier("checksum");
		String statements = this.dialect.quoteIdentifier("statements");
		
		return "UPDATE " + table + " SET " + description + " = ?, " + status + " = ?, " + appliedAt + " = ?, " + checksum + " = ?, " + statements + " = ? WHERE " + version + " = ?";
	}
	
	/**
	 * Builds the sql statement used to update the status and applied time of a migration record.<br>
	 * @return The rendered update sql statement
	 */
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
			throw new SqlMigrationExecutionException("Failed to initialize migration table", e);
		}
		this.upgradeMigrationTable();
	}
	
	/**
	 * Adds the columns missing from a migration table that was created by an earlier version of the library.<br>
	 * A table created by the current version already contains every column, in which case this does nothing.<br>
	 *
	 * @throws SqlException If the migration table could not be upgraded
	 */
	private void upgradeMigrationTable() throws SqlException {
		try (Connection connection = this.dataSource.getConnection()) {
			boolean present;
			try (
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery("SELECT * FROM " + this.dialect.quoteIdentifier(TABLE_NAME) + " WHERE 1 = 0")
			) {
				present = containsColumn(rs, "statements").orElse(true);
			}
			
			if (present) {
				return;
			}
			
			SqlTable<Void> table = SqlTable.create(Void.class, TABLE_NAME);
			SqlColumn<Void, String> column = table.column("statements", SqlTypes.TEXT, _ -> null);
			String sql = this.dialect.migrationRenderer().renderAddColumn(table, column, column.type(), SqlColumnOptions.EMPTY).sql();
			
			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
			}
		} catch (SQLException e) {
			throw new SqlMigrationExecutionException("Failed to upgrade the migration table", e);
		}
	}
	
	/**
	 * Checks whether the given result set contains a column with the given name.<br>
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
				long appliedAtMillis = resultSet.getLong("applied_at");
				Instant appliedAt = resultSet.wasNull() ? null : Instant.ofEpochMilli(appliedAtMillis);
				String checksum = resultSet.getString("checksum");
				String statements = resultSet.getString("statements");
				
				results.add(new SqlMigrationInfo(version, description, status, appliedAt, checksum, statements));
			}
			results.sort(Comparator.comparing(SqlMigrationInfo::version));
			return List.copyOf(results);
		} catch (SQLException e) {
			throw new SqlMigrationExecutionException("Failed to load migration history", e);
		}
	}
	
	@Override
	public void save(@NonNull SqlMigrationInfo info) throws SqlException {
		Objects.requireNonNull(info, "Sql migration info must not be null");
		
		try (Connection connection = this.dataSource.getConnection()) {
			this.save(connection, info);
		} catch (SQLException e) {
			throw new SqlMigrationExecutionException("Failed to save migration info for version " + info.version(), e);
		}
	}
	
	@Override
	public void save(@NonNull Connection connection, @NonNull SqlMigrationInfo info) throws SqlException {
		Objects.requireNonNull(connection, "Connection must not be null");
		Objects.requireNonNull(info, "Sql migration info must not be null");
		
		try (PreparedStatement overwrite = connection.prepareStatement(this.buildOverwriteSql())) {
			overwrite.setString(1, info.description());
			overwrite.setString(2, info.status().name());
			overwrite.setObject(3, info.appliedAt() != null ? info.appliedAt().toEpochMilli() : null);
			overwrite.setString(4, info.checksum());
			overwrite.setString(5, info.statements());
			overwrite.setString(6, info.version().toString());
			
			if (overwrite.executeUpdate() > 0) {
				return;
			}
		} catch (SQLException e) {
			throw new SqlMigrationExecutionException("Failed to save migration info for version " + info.version(), e);
		}
		
		try (PreparedStatement statement = connection.prepareStatement(this.buildSaveSql())) {
			statement.setString(1, info.version().toString());
			statement.setString(2, info.description());
			statement.setString(3, info.status().name());
			statement.setObject(4, info.appliedAt() != null ? info.appliedAt().toEpochMilli() : null);
			statement.setString(5, info.checksum());
			statement.setString(6, info.statements());
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new SqlMigrationExecutionException("Failed to save migration info for version " + info.version(), e);
		}
	}
	
	@Override
	public void update(@NonNull Version version, @NonNull SqlMigrationStatus status) throws SqlException {
		Objects.requireNonNull(version, "Sql migration version must not be null");
		Objects.requireNonNull(status, "Sql migration status must not be null");
		
		try (Connection connection = this.dataSource.getConnection()) {
			this.update(connection, version, status);
		} catch (SQLException e) {
			throw new SqlMigrationExecutionException("Failed to update migration status for version " + version, e);
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
			statement.setObject(2, now != null ? now.toEpochMilli() : null);
			statement.setString(3, version.toString());
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new SqlMigrationExecutionException("Failed to update migration status for version " + version, e);
		}
	}
}
