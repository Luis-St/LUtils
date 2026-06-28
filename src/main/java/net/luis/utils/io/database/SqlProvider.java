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

package net.luis.utils.io.database;

import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.query.SqlQueryProvider;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.table.SqlTableProvider;
import org.jspecify.annotations.NonNull;

/**
 * Central entry point for interacting with a sql database.<br>
 * Provides schema management operations as well as access to table-level and query-level providers for individual {@link SqlTable tables}.<br>
 *
 * @see SqlTableProvider
 * @see SqlQueryProvider
 *
 * @author Luis-St
 */
public interface SqlProvider {
	
	/**
	 * Creates a new schema with the given name.<br>
	 *
	 * @param name The name of the schema to create
	 * @throws NullPointerException If the name is null
	 * @throws SqlException If the schema could not be created
	 */
	void createSchema(@NonNull String name) throws SqlException;
	
	/**
	 * Creates a new schema with the given name if it does not already exist.<br>
	 *
	 * @param name The name of the schema to create
	 * @throws NullPointerException If the name is null
	 * @throws SqlException If the schema could not be created
	 */
	void createSchemaIfNotExists(@NonNull String name) throws SqlException;
	
	/**
	 * Checks whether a schema with the given name exists.<br>
	 *
	 * @param name The name of the schema to check
	 * @return True if a schema with the given name exists, otherwise false
	 * @throws NullPointerException If the name is null
	 * @throws SqlException If the existence of the schema could not be determined
	 */
	boolean existsSchema(@NonNull String name) throws SqlException;
	
	/**
	 * Drops the schema with the given name.<br>
	 *
	 * @param name The name of the schema to drop
	 * @param cascade Whether dependent objects should be dropped along with the schema
	 * @throws NullPointerException If the name is null
	 * @throws SqlException If the schema could not be dropped
	 */
	void dropSchema(@NonNull String name, boolean cascade) throws SqlException;
	
	/**
	 * Returns a table provider for the given table.<br>
	 * The table provider exposes schema and data manipulation operations scoped to that table.<br>
	 *
	 * @param table The table to obtain a provider for
	 * @param <T> The type the table maps its rows to
	 * @return A table provider scoped to the given table
	 * @throws NullPointerException If the table is null
	 * @throws SqlException If the table provider could not be created
	 */
	<T> @NonNull SqlTableProvider<T> table(@NonNull SqlTable<T> table) throws SqlException;
	
	/**
	 * Returns a query provider for building queries against the given table.<br>
	 *
	 * @param table The table to query against
	 * @param <T> The type the table maps its rows to
	 * @return A query provider scoped to the given table
	 * @throws NullPointerException If the table is null
	 * @throws SqlException If the query provider could not be created
	 */
	<T> @NonNull SqlQueryProvider<T> from(@NonNull SqlTable<T> table) throws SqlException;
}
