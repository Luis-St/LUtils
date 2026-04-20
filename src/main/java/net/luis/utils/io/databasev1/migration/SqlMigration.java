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

/*package net.luis.utils.io.databasev1.migration;

import net.luis.utils.io.databasev1.exception.SqlException;
import net.luis.utils.util.Version;
import org.jspecify.annotations.NonNull;

*//**
 * Interface representing a SQL migration with manual {@code up()} and {@code down()} methods.<br>
 * <p>
 *     Migrations define schema changes using a DSL provided by {@link SqlMigrationBuilder}.<br>
 *     No raw SQL is allowed; all schema modifications go through the builder.
 * </p>
 * <p>
 *     Migrations are ordered by their {@link #version()} using the natural ordering of {@link Version}.<br>
 *     The checksum is computed by {@link SqlMigrationRunner} at registration time by dry-running {@link #up(SqlMigrationBuilder)}.
 * </p>
 *
 * @author Luis-St
 *//*
public interface SqlMigration {
	
	*//**
	 * Returns the version of this migration.<br>
	 * Migrations are applied in ascending version order.<br>
	 *
	 * @return The migration version
	 *//*
	@NonNull Version version();
	
	*//**
	 * Returns the description of this migration.<br>
	 * @return The migration description
	 *//*
	@NonNull String description();
	
	*//**
	 * Applies this migration (forward).<br>
	 *
	 * @param builder The migration builder providing DSL operations
	 * @throws SqlException If the migration fails
	 *//*
	void up(@NonNull SqlMigrationBuilder builder) throws SqlException;
	
	*//**
	 * Reverts this migration (rollback).<br>
	 *
	 * @param builder The migration builder providing DSL operations
	 * @throws SqlException If the rollback fails
	 *//*
	void down(@NonNull SqlMigrationBuilder builder) throws SqlException;
}*/
