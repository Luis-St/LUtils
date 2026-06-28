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

import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.util.Version;
import org.jspecify.annotations.NonNull;

/**
 * Represents a single schema migration that can be applied to or rolled back from a database.<br>
 * <p>
 *     An implementation defines a unique {@link #version() version}, a human-readable
 *     {@link #description() description} and the schema changes to perform when the migration is
 *     applied ({@link #up(SqlMigrationBuilder, SqlMigrationSchema) up}) or reverted
 *     ({@link #down(SqlMigrationBuilder, SqlMigrationSchema) down}).
 * </p>
 * <p>
 *     The {@code up} and {@code down} methods receive a builder used to collect the statements to
 *     execute and a schema describing the current database structure.
 * </p>
 *
 * @author Luis-St
 */
public interface SqlMigration {
	
	/**
	 * Returns the version that uniquely identifies this migration.<br>
	 * @return The migration version
	 */
	@NonNull Version version();
	
	/**
	 * Returns the human-readable description of this migration.<br>
	 * @return The migration description
	 */
	@NonNull String description();
	
	/**
	 * Defines the schema changes that are applied when this migration is run.<br>
	 *
	 * @param builder The builder used to collect the statements to execute
	 * @param schema The schema describing the current database structure
	 * @throws SqlException If an error occurs while defining the migration
	 */
	void up(@NonNull SqlMigrationBuilder builder, @NonNull SqlMigrationSchema schema) throws SqlException;
	
	/**
	 * Defines the schema changes that are applied when this migration is rolled back.<br>
	 *
	 * @param builder The builder used to collect the statements to execute
	 * @param schema The schema describing the current database structure
	 * @throws SqlException If an error occurs while defining the rollback
	 */
	void down(@NonNull SqlMigrationBuilder builder, @NonNull SqlMigrationSchema schema) throws SqlException;
	
	/**
	 * Returns whether this migration may be executed without wrapping its statements in a single transaction.<br>
	 * By default migrations are executed atomically and this method returns {@code false}.<br>
	 *
	 * @return {@code true} if the migration allows non-atomic execution, {@code false} otherwise
	 */
	default boolean allowsNonAtomicExecution() {
		return false;
	}
}
