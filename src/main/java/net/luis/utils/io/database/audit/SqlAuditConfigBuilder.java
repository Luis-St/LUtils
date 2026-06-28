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

import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.jspecify.annotations.NonNull;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Builder for {@link SqlAuditConfig}.<br>
 * All values are initialized with their defaults and can be overridden individually before building.<br>
 *
 * @author Luis-St
 */
public class SqlAuditConfigBuilder {
	
	/**
	 * The name of the version column.
	 */
	private String versionColumn = "version";
	/**
	 * The name of the created-at column.
	 */
	private String createdAtColumn = "created_at";
	/**
	 * The name of the created-by column.
	 */
	private String createdByColumn = "created_by";
	/**
	 * The name of the updated-at column.
	 */
	private String updatedAtColumn = "updated_at";
	/**
	 * The name of the updated-by column.
	 */
	private String updatedByColumn = "updated_by";
	/**
	 * The sql type used for the version column.
	 */
	private SqlType<Long> versionType = SqlTypes.LONG;
	/**
	 * The sql type used for the timestamp columns.
	 */
	private SqlType<LocalDateTime> timestampType = SqlTypes.LOCAL_DATE_TIME.configure(SqlParameter.fractional(6));
	/**
	 * The sql type used for the user columns.
	 */
	private SqlType<String> userType = SqlTypes.STRING.configure(SqlParameter.length(255));
	/**
	 * The source from which the audit values originate.
	 */
	private SqlAuditValueSource valueSource = SqlAuditValueSource.ORM_CLOCK;
	/**
	 * The clock used to generate timestamps.
	 */
	private Clock clock = Clock.systemUTC();
	
	/**
	 * Constructs a new audit config builder with the default values.<br>
	 */
	SqlAuditConfigBuilder() {}
	
	/**
	 * Sets the name of the version column.<br>
	 *
	 * @param name The version column name
	 * @return This builder
	 * @throws NullPointerException If the name is null
	 */
	public @NonNull SqlAuditConfigBuilder versionColumn(@NonNull String name) {
		this.versionColumn = Objects.requireNonNull(name, "Sql version column name must not be null");
		return this;
	}
	
	/**
	 * Sets the name of the created-at column.<br>
	 *
	 * @param name The created-at column name
	 * @return This builder
	 * @throws NullPointerException If the name is null
	 */
	public @NonNull SqlAuditConfigBuilder createdAtColumn(@NonNull String name) {
		this.createdAtColumn = Objects.requireNonNull(name, "Sql created-at column name must not be null");
		return this;
	}
	
	/**
	 * Sets the name of the created-by column.<br>
	 *
	 * @param name The created-by column name
	 * @return This builder
	 * @throws NullPointerException If the name is null
	 */
	public @NonNull SqlAuditConfigBuilder createdByColumn(@NonNull String name) {
		this.createdByColumn = Objects.requireNonNull(name, "Sql created-by column name must not be null");
		return this;
	}
	
	/**
	 * Sets the name of the updated-at column.<br>
	 *
	 * @param name The updated-at column name
	 * @return This builder
	 * @throws NullPointerException If the name is null
	 */
	public @NonNull SqlAuditConfigBuilder updatedAtColumn(@NonNull String name) {
		this.updatedAtColumn = Objects.requireNonNull(name, "Sql updated-at column name must not be null");
		return this;
	}
	
	/**
	 * Sets the name of the updated-by column.<br>
	 *
	 * @param name The updated-by column name
	 * @return This builder
	 * @throws NullPointerException If the name is null
	 */
	public @NonNull SqlAuditConfigBuilder updatedByColumn(@NonNull String name) {
		this.updatedByColumn = Objects.requireNonNull(name, "Sql updated-by column name must not be null");
		return this;
	}
	
	/**
	 * Sets the sql type used for the version column.<br>
	 *
	 * @param type The version type
	 * @return This builder
	 * @throws NullPointerException If the type is null
	 */
	public @NonNull SqlAuditConfigBuilder versionType(@NonNull SqlType<Long> type) {
		this.versionType = Objects.requireNonNull(type, "Sql version type must not be null");
		return this;
	}
	
	/**
	 * Sets the sql type used for the timestamp columns.<br>
	 *
	 * @param type The timestamp type
	 * @return This builder
	 * @throws NullPointerException If the type is null
	 */
	public @NonNull SqlAuditConfigBuilder timestampType(@NonNull SqlType<LocalDateTime> type) {
		this.timestampType = Objects.requireNonNull(type, "Sql timestamp type must not be null");
		return this;
	}
	
	/**
	 * Sets the sql type used for the user columns.<br>
	 *
	 * @param type The user type
	 * @return This builder
	 * @throws NullPointerException If the type is null
	 */
	public @NonNull SqlAuditConfigBuilder userType(@NonNull SqlType<String> type) {
		this.userType = Objects.requireNonNull(type, "Sql user type must not be null");
		return this;
	}
	
	/**
	 * Sets the source from which the audit values originate.<br>
	 *
	 * @param valueSource The audit value source
	 * @return This builder
	 * @throws NullPointerException If the value source is null
	 */
	public @NonNull SqlAuditConfigBuilder valueSource(@NonNull SqlAuditValueSource valueSource) {
		this.valueSource = Objects.requireNonNull(valueSource, "Sql audit value source must not be null");
		return this;
	}
	
	/**
	 * Sets the clock used to generate timestamps.<br>
	 *
	 * @param clock The clock
	 * @return This builder
	 * @throws NullPointerException If the clock is null
	 */
	public @NonNull SqlAuditConfigBuilder clock(@NonNull Clock clock) {
		this.clock = Objects.requireNonNull(clock, "Clock must not be null");
		return this;
	}
	
	/**
	 * Builds a new audit config from the configured values.<br>
	 *
	 * @return The newly built audit config
	 * @throws IllegalArgumentException If any column name is blank or the column names are not distinct
	 */
	public @NonNull SqlAuditConfig build() {
		return new SqlAuditConfig(
			this.versionColumn,
			this.createdAtColumn,
			this.createdByColumn,
			this.updatedAtColumn,
			this.updatedByColumn,
			this.versionType,
			this.timestampType,
			this.userType,
			this.valueSource,
			this.clock
		);
	}
}
