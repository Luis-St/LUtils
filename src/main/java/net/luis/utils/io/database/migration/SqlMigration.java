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
import org.jspecify.annotations.NonNull;

/**
 * Interface representing a SQL migration with manual {@code up()} and {@code down()} methods.<br>
 * <p>
 *     Migrations define schema changes using a DSL provided by {@link SqlMigrationBuilder}.<br>
 *     No raw SQL is allowed; all schema modifications go through the builder.
 * </p>
 *
 * @author Luis-St
 */
public interface SqlMigration {
	
	/**
	 * Returns the version of this migration.<br>
	 * <p>
	 *     Versions are compared numerically to determine migration ordering.<br>
	 *     Use a timestamp-based format ({@code YYYYMMDDHHmmss}, e.g., {@code "20260225120000"}) to avoid ordering conflicts in team environments.
	 * </p>
	 *
	 * @return The migration version
	 */
	@NonNull String version();
	
	/**
	 * Returns the description of this migration.<br>
	 * @return The migration description
	 */
	@NonNull String description();
	
	/**
	 * Applies this migration (forward).<br>
	 *
	 * @param builder The migration builder providing DSL operations
	 * @throws SqlException If the migration fails
	 */
	void up(@NonNull SqlMigrationBuilder builder) throws SqlException;
	
	/**
	 * Reverts this migration (rollback).<br>
	 *
	 * @param builder The migration builder providing DSL operations
	 * @throws SqlException If the rollback fails
	 */
	void down(@NonNull SqlMigrationBuilder builder) throws SqlException;
	
	/**
	 * Returns a checksum representing the content of this migration.<br>
	 * <p>
	 *     The checksum is used by the migration runner to detect changes in previously applied migrations.<br>
	 *     If the checksum of a registered migration differs from the stored checksum,<br>
	 *     the runner can flag the migration as tampered.
	 * </p>
	 * <p>
	 *     The default implementation computes the checksum from the migration's version and description.<br>
	 *     Implementations should override this to include the actual migration operations for more reliable drift detection.
	 * </p>
	 *
	 * @return The migration checksum
	 */
	default long checksum() {
		return (long) this.version().hashCode() * 31 + this.description().hashCode();
	}
}
