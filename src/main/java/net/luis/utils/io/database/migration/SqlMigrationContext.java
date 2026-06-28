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

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.migration.store.SqlMigrationSchemaStore;
import net.luis.utils.io.database.migration.store.SqlMigrationStore;
import net.luis.utils.io.database.query.SqlQueryProvider;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.util.Version;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.util.List;

/**
 * Carries the contextual data that is passed through a migration run.<br>
 * <p>
 *     The context provides access to the underlying {@link DataSource data source} and
 *     {@link SqlDialect dialect} and exposes operations used to execute the rendered statements of a
 *     migration while recording its result.
 * </p>
 *
 * @author Luis-St
 */
interface SqlMigrationContext {
	
	/**
	 * Returns the data source the migration is executed against.<br>
	 * @return The data source
	 */
	@NonNull DataSource dataSource();
	
	/**
	 * Returns the dialect used to render the migration statements.<br>
	 * @return The dialect
	 */
	@NonNull SqlDialect dialect();
	
	/**
	 * Creates a query provider for the given table.<br>
	 *
	 * @param table The table to query
	 * @param <E> The entity type of the table
	 * @return The query provider for the table
	 * @throws SqlException If an error occurs while creating the query provider
	 */
	<E> @NonNull SqlQueryProvider<E> from(@NonNull SqlTable<E> table) throws SqlException;
	
	/**
	 * Executes the given statements and saves the migration information afterwards.<br>
	 *
	 * @param statements The rendered statements to execute
	 * @param store The store used to record the migration information
	 * @param info The migration information to save
	 * @param schemaStore The store used to record the schema snapshot
	 * @throws SqlException If an error occurs while executing or saving
	 */
	void executeAndSave(@NonNull List<SqlRendered> statements, @NonNull SqlMigrationStore store, @NonNull SqlMigrationInfo info, @NonNull SqlMigrationSchemaStore schemaStore) throws SqlException;
	
	/**
	 * Executes the given statements and updates the status of the recorded migration afterwards.<br>
	 *
	 * @param statements The rendered statements to execute
	 * @param store The store used to record the migration information
	 * @param version The version of the migration to update
	 * @param status The new status to set for the migration
	 * @param schemaStore The store used to record the schema snapshot
	 * @throws SqlException If an error occurs while executing or updating
	 */
	void executeAndUpdate(@NonNull List<SqlRendered> statements, @NonNull SqlMigrationStore store, @NonNull Version version, @NonNull SqlMigrationStatus status, @NonNull SqlMigrationSchemaStore schemaStore) throws SqlException;
}
