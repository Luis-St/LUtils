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

package net.luis.utils.io.database.audit;

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Holds the audit metadata of a single row.<br>
 * Each value is optional, since a row may not have all audit information populated.<br>
 *
 * @author Luis-St
 *
 * @param version The optimistic-locking version of the row
 * @param createdAt The timestamp at which the row was created
 * @param createdBy The user that created the row
 * @param updatedAt The timestamp at which the row was last updated
 * @param updatedBy The user that last updated the row
 */
public record SqlAuditMetadata(
	@NonNull OptionalLong version,
	@NonNull Optional<LocalDateTime> createdAt,
	@NonNull Optional<String> createdBy,
	@NonNull Optional<LocalDateTime> updatedAt,
	@NonNull Optional<String> updatedBy
) {
	
	/**
	 * Constructs a new audit metadata with the given version, timestamps and users.<br>
	 *
	 * @throws NullPointerException If any of the parameters is null
	 */
	public SqlAuditMetadata {
		Objects.requireNonNull(version, "Sql audit version must not be null");
		Objects.requireNonNull(createdAt, "Sql audit created-at must not be null");
		Objects.requireNonNull(createdBy, "Sql audit created-by must not be null");
		Objects.requireNonNull(updatedAt, "Sql audit updated-at must not be null");
		Objects.requireNonNull(updatedBy, "Sql audit updated-by must not be null");
	}
	
	/**
	 * Creates a new audit metadata from the given raw values.<br>
	 * A negative version is treated as absent, and {@code null} timestamps and users are treated as empty.<br>
	 *
	 * @param version The version or a negative value if absent
	 * @param createdAt The created-at timestamp or null if absent
	 * @param createdBy The created-by user or null if absent
	 * @param updatedAt The updated-at timestamp or null if absent
	 * @param updatedBy The updated-by user or null if absent
	 * @return The newly created audit metadata
	 */
	public static @NonNull SqlAuditMetadata of(long version, @Nullable LocalDateTime createdAt, @Nullable String createdBy, @Nullable LocalDateTime updatedAt, @Nullable String updatedBy) {
		return new SqlAuditMetadata(
			version < 0 ? OptionalLong.empty() : OptionalLong.of(version),
			Optional.ofNullable(createdAt),
			Optional.ofNullable(createdBy),
			Optional.ofNullable(updatedAt),
			Optional.ofNullable(updatedBy)
		);
	}
	
	/**
	 * Reads the audit metadata from the given result set starting at the given column index.<br>
	 * The values are read in the canonical audit column order using the types defined by the given config.<br>
	 *
	 * @param dialect The sql dialect used to read the values
	 * @param resultSet The result set to read the values from
	 * @param startIndex The index of the first audit column in the result set
	 * @param config The audit config defining the column types
	 * @return The audit metadata read from the result set
	 * @throws NullPointerException If the dialect, result set or config is null
	 * @throws SqlException If reading the values from the result set fails
	 */
	public static @NonNull SqlAuditMetadata readFrom(@NonNull SqlDialect dialect, @NonNull ResultSet resultSet, int startIndex, @NonNull SqlAuditConfig config) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(resultSet, "Result set must not be null");
		Objects.requireNonNull(config, "Sql audit config must not be null");
		
		Long version = SqlType.getValue(config.versionType(), dialect, resultSet, startIndex);
		LocalDateTime createdAt = SqlType.getValue(config.timestampType(), dialect, resultSet, startIndex + 1);
		String createdBy = SqlType.getValue(config.userType(), dialect, resultSet, startIndex + 2);
		LocalDateTime updatedAt = SqlType.getValue(config.timestampType(), dialect, resultSet, startIndex + 3);
		String updatedBy = SqlType.getValue(config.userType(), dialect, resultSet, startIndex + 4);
		return of(version == null ? -1L : version, createdAt, createdBy, updatedAt, updatedBy);
	}
	
	/**
	 * Creates a copy of this audit metadata with an incremented version and the given updated-at and updated-by values.<br>
	 * The created-at and created-by values are retained from this metadata.<br>
	 *
	 * @param updatedAt The new updated-at timestamp or null if absent
	 * @param updatedBy The new updated-by user or null if absent
	 * @return The bumped audit metadata
	 */
	public @NonNull SqlAuditMetadata bumped(@Nullable LocalDateTime updatedAt, @Nullable String updatedBy) {
		long next = this.version.orElse(0L) + 1L;
		return new SqlAuditMetadata(OptionalLong.of(next), this.createdAt, this.createdBy, Optional.ofNullable(updatedAt), Optional.ofNullable(updatedBy));
	}
}
