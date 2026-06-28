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

import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.migration.SqlMigrationInfo;
import net.luis.utils.io.database.migration.SqlMigrationStatus;
import net.luis.utils.util.Version;
import org.jspecify.annotations.NonNull;

import java.sql.Connection;
import java.util.List;

/**
 * Storage abstraction for migration bookkeeping.<br>
 * Defines how applied-migration records are persisted, loaded and updated in the database.<br>
 *
 * @author Luis-St
 */

public interface SqlMigrationStore {
	
	/**
	 * Initializes the underlying storage, creating any required tables if they do not yet exist.<br>
	 * @throws SqlException If the storage could not be initialized
	 */
	void initialize() throws SqlException;
	
	/**
	 * Loads all stored migration records.<br>
	 * @return The list of all stored migration infos
	 * @throws SqlException If the migration records could not be loaded
	 */
	@NonNull List<SqlMigrationInfo> loadAll() throws SqlException;
	
	/**
	 * Saves the given migration info to the storage.<br>
	 *
	 * @param info The migration info to save
	 * @throws NullPointerException If the migration info is null
	 * @throws SqlException If the migration info could not be saved
	 */
	void save(@NonNull SqlMigrationInfo info) throws SqlException;
	
	/**
	 * Saves the given migration info to the storage using the given connection.<br>
	 *
	 * @param connection The connection to use
	 * @param info The migration info to save
	 * @throws NullPointerException If the connection or migration info is null
	 * @throws SqlException If the migration info could not be saved
	 */
	default void save(@NonNull Connection connection, @NonNull SqlMigrationInfo info) throws SqlException {
		this.save(info);
	}
	
	/**
	 * Updates the status of the stored migration record with the given version.<br>
	 *
	 * @param version The version of the migration record to update
	 * @param status The new status to set
	 * @throws NullPointerException If the version or status is null
	 * @throws SqlException If the migration status could not be updated
	 */
	void update(@NonNull Version version, @NonNull SqlMigrationStatus status) throws SqlException;
	
	/**
	 * Updates the status of the stored migration record with the given version using the given connection.<br>
	 *
	 * @param connection The connection to use
	 * @param version The version of the migration record to update
	 * @param status The new status to set
	 * @throws NullPointerException If the connection, version or status is null
	 * @throws SqlException If the migration status could not be updated
	 */
	default void update(@NonNull Connection connection, @NonNull Version version, @NonNull SqlMigrationStatus status) throws SqlException {
		this.update(version, status);
	}
}
