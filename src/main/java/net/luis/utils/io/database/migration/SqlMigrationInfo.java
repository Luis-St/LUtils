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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

/**
 * Interface providing information about a SQL migration.<br>
 * <p>
 *     Exposes the version, description, current status, application timestamp,
 *     and checksum of a migration.<br>
 * </p>
 *
 * @author Luis-St
 */
public interface SqlMigrationInfo {

	/**
	 * Returns the version identifier of this migration.<br>
	 * @return The migration version
	 */
	@NonNull String version();

	/**
	 * Returns the human-readable description of this migration.<br>
	 * @return The migration description
	 */
	@NonNull String description();

	/**
	 * Returns the current status of this migration.<br>
	 * @return The migration status
	 */
	@NonNull SqlMigrationStatus status();

	/**
	 * Returns the instant at which this migration was applied.<br>
	 * @return The application timestamp, or {@code null} if not yet applied
	 */
	@Nullable Instant appliedAt();

	/**
	 * Returns the checksum of this migration.<br>
	 * Used to detect changes in previously applied migrations.<br>
	 * @return The migration checksum
	 */
	long checksum();
}
