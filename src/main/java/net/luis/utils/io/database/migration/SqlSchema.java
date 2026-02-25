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
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * Interface representing a SQL schema for migration purposes.<br>
 *
 * @author Luis-St
 */
public interface SqlSchema {
	
	/**
	 * Introspects the current database schema.<br>
	 *
	 * @param db The database to introspect
	 * @return The current schema
	 * @throws SqlException If a database access error occurs
	 */
	static @NonNull SqlSchema fromDatabase(@NonNull SqlDatabase db) throws SqlException {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns all tables in this schema.<br>
	 * @return The list of tables
	 */
	@NonNull List<SqlTable<?>> tables();
	
	/**
	 * Returns a specific table by name.<br>
	 *
	 * @param name The table name
	 * @return An optional containing the table, or empty if not found
	 */
	@NonNull Optional<SqlTable<?>> table(@NonNull String name);
}
