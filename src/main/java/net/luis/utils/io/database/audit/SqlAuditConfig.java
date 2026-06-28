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

import com.google.common.collect.Sets;
import net.luis.utils.io.database.type.SqlType;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Configuration that describes how auditing is applied to a table.<br>
 * It defines the names and sql types of the audit columns, the source of the audit values and the clock used to generate timestamps.<br>
 *
 * @author Luis-St
 *
 * @param versionColumn The name of the version column
 * @param createdAtColumn The name of the created-at column
 * @param createdByColumn The name of the created-by column
 * @param updatedAtColumn The name of the updated-at column
 * @param updatedByColumn The name of the updated-by column
 * @param versionType The sql type used for the version column
 * @param timestampType The sql type used for the timestamp columns
 * @param userType The sql type used for the user columns
 * @param valueSource The source from which the audit values originate
 * @param clock The clock used to generate timestamps
 */
public record SqlAuditConfig(
	@NonNull String versionColumn,
	@NonNull String createdAtColumn,
	@NonNull String createdByColumn,
	@NonNull String updatedAtColumn,
	@NonNull String updatedByColumn,
	@NonNull SqlType<Long> versionType,
	@NonNull SqlType<LocalDateTime> timestampType,
	@NonNull SqlType<String> userType,
	@NonNull SqlAuditValueSource valueSource,
	@NonNull Clock clock
) {
	
	/**
	 * The default audit configuration using the standard column names, types and an utc clock.
	 */
	public static final SqlAuditConfig DEFAULT = builder().build();
	
	/**
	 * Constructs a new audit config with the given column names, types, value source and clock.<br>
	 *
	 * @throws NullPointerException If any of the parameters is null
	 * @throws IllegalArgumentException If any column name is blank or the column names are not distinct
	 */
	public SqlAuditConfig {
		Objects.requireNonNull(versionColumn, "Sql version column name must not be null");
		Objects.requireNonNull(createdAtColumn, "Sql created-at column name must not be null");
		Objects.requireNonNull(createdByColumn, "Sql created-by column name must not be null");
		Objects.requireNonNull(updatedAtColumn, "Sql updated-at column name must not be null");
		Objects.requireNonNull(updatedByColumn, "Sql updated-by column name must not be null");
		Objects.requireNonNull(versionType, "Sql version type must not be null");
		Objects.requireNonNull(timestampType, "Sql timestamp type must not be null");
		Objects.requireNonNull(userType, "Sql user type must not be null");
		Objects.requireNonNull(valueSource, "Sql audit value source must not be null");
		Objects.requireNonNull(clock, "Clock must not be null");
		
		List<String> names = List.of(versionColumn, createdAtColumn, createdByColumn, updatedAtColumn, updatedByColumn);
		for (String name : names) {
			if (name.isBlank()) {
				throw new IllegalArgumentException("Sql audit column names must not be blank");
			}
		}
		if (Sets.newLinkedHashSet(names).size() != names.size()) {
			throw new IllegalArgumentException("Sql audit column names must be distinct, but got: " + names);
		}
	}
	
	/**
	 * Creates a new builder for an audit config initialized with the default values.<br>
	 * @return A new audit config builder
	 */
	public static @NonNull SqlAuditConfigBuilder builder() {
		return new SqlAuditConfigBuilder();
	}
	
	/**
	 * Returns the audit columns described by this configuration in their canonical order.<br>
	 * @return An unmodifiable list of the audit columns
	 */
	public @NonNull @Unmodifiable List<SqlAuditColumn> auditColumns() {
		return List.of(
			new SqlAuditColumn(this.versionColumn, this.versionType, SqlAuditRole.VERSION, false),
			new SqlAuditColumn(this.createdAtColumn, this.timestampType, SqlAuditRole.CREATED_AT, false),
			new SqlAuditColumn(this.createdByColumn, this.userType, SqlAuditRole.CREATED_BY, true),
			new SqlAuditColumn(this.updatedAtColumn, this.timestampType, SqlAuditRole.UPDATED_AT, true),
			new SqlAuditColumn(this.updatedByColumn, this.userType, SqlAuditRole.UPDATED_BY, true)
		);
	}
	
	/**
	 * Returns the names of the audit columns in their canonical order.<br>
	 * @return An unmodifiable list of the audit column names
	 */
	public @NonNull @Unmodifiable List<String> columnNames() {
		return List.of(this.versionColumn, this.createdAtColumn, this.createdByColumn, this.updatedAtColumn, this.updatedByColumn);
	}
}
