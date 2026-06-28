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

import net.luis.utils.util.Version;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.Objects;

/**
 * Holds the recorded information about a single migration as stored by the migration framework.<br>
 *
 * @author Luis-St
 *
 * @param version The version that uniquely identifies the migration
 * @param description The human-readable description of the migration
 * @param status The current status of the migration
 * @param appliedAt The instant at which the migration was applied or {@code null} if it has not been applied
 * @param checksum The checksum of the migration or {@code null} if no checksum is recorded
 */
public record SqlMigrationInfo(
	@NonNull Version version,
	@NonNull String description,
	@NonNull SqlMigrationStatus status,
	@Nullable Instant appliedAt,
	@Nullable String checksum
) {
	
	/**
	 * Constructs a new migration info with the given values.<br>
	 * @throws NullPointerException If the version, description or status is null
	 */
	public SqlMigrationInfo {
		Objects.requireNonNull(version, "Sql migration version must not be null");
		Objects.requireNonNull(description, "Sql migration description must not be null");
		Objects.requireNonNull(status, "Sql migration status must not be null");
	}
}
