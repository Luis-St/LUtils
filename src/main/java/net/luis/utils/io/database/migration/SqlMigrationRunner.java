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

import net.luis.utils.io.database.SqlDatabase;
import net.luis.utils.io.database.SqlRendered;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.util.Version;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Interface for running SQL migrations against a database.<br>
 * <p>
 *     Provides methods to apply, rollback, inspect status, and dry-run migrations.<br>
 *     Migrations must be registered before they can be executed.<br>
 * </p>
 *
 * @author Luis-St
 */
public interface SqlMigrationRunner {
	
	/**
	 * Creates a new migration runner for the specified database.<br>
	 *
	 * @param database The database to run migrations against
	 * @return A new migration runner instance
	 */
	static @NonNull SqlMigrationRunner of(@NonNull SqlDatabase database) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Applies all pending migrations in version order.<br>
	 * @throws SqlException If a migration fails
	 */
	void migrate() throws SqlException;
	
	/**
	 * Applies all pending migrations up to and including the specified version.<br>
	 *
	 * @param targetVersion The target migration version
	 * @throws SqlException If a migration fails
	 */
	void migrateTo(@NonNull Version targetVersion) throws SqlException;
	
	/**
	 * Rolls back the most recently applied migration.<br>
	 * @throws SqlException If the rollback fails
	 */
	void rollback() throws SqlException;
	
	/**
	 * Rolls back the specified number of most recently applied migrations.<br>
	 *
	 * @param count The number of migrations to roll back
	 * @throws SqlException If a rollback fails
	 */
	void rollback(int count) throws SqlException;
	
	/**
	 * Rolls back all migrations down to and including the specified version.<br>
	 *
	 * @param targetVersion The target migration version to roll back to
	 * @throws SqlException If a rollback fails
	 */
	void rollbackTo(@NonNull Version targetVersion) throws SqlException;
	
	/**
	 * Returns the status of all registered migrations.<br>
	 *
	 * @return The list of migration info entries
	 * @throws SqlException If the status cannot be retrieved
	 */
	@NonNull List<SqlMigrationInfo> status() throws SqlException;
	
	/**
	 * Performs a dry run of all pending migrations without applying them.<br>
	 * Returns the SQL statements that would be executed.<br>
	 *
	 * @return The list of rendered SQL statements
	 * @throws SqlException If the dry run fails
	 */
	@NonNull List<SqlRendered> dryRun() throws SqlException;
	
	/**
	 * Performs a dry run of rolling back the most recently applied migration.<br>
	 * Returns the SQL statements that would be executed.<br>
	 *
	 * @return The list of rendered SQL statements for rollback
	 * @throws SqlException If the dry run fails
	 */
	@NonNull List<SqlRendered> dryRunRollback() throws SqlException;
	
	/**
	 * Registers a single migration with this runner.<br>
	 * The checksum is computed at registration time by dry-running {@link SqlMigration#up(SqlMigrationBuilder)}
	 * against a capturing builder and hashing the resulting SQL statements.<br>
	 *
	 * @param migration The migration to register
	 */
	void register(@NonNull SqlMigration migration);
	
	/**
	 * Registers a list of migrations with this runner.<br>
	 * The checksum for each migration is computed at registration time.<br>
	 *
	 * @param migrations The migrations to register
	 */
	void register(@NonNull List<SqlMigration> migrations);
}
