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
 *
 * @author Luis-St
 *
 */

public class SqlAuditConfigBuilder {
	
	private String versionColumn = "version";
	private String createdAtColumn = "created_at";
	private String createdByColumn = "created_by";
	private String updatedAtColumn = "updated_at";
	private String updatedByColumn = "updated_by";
	private SqlType<Long> versionType = SqlTypes.LONG;
	private SqlType<LocalDateTime> timestampType = SqlTypes.LOCAL_DATE_TIME.configure(SqlParameter.fractional(6));
	private SqlType<String> userType = SqlTypes.STRING.configure(SqlParameter.length(255));
	private SqlAuditValueSource valueSource = SqlAuditValueSource.ORM_CLOCK;
	private Clock clock = Clock.systemUTC();
	
	SqlAuditConfigBuilder() {}
	
	public @NonNull SqlAuditConfigBuilder versionColumn(@NonNull String name) {
		this.versionColumn = Objects.requireNonNull(name, "Sql version column name must not be null");
		return this;
	}
	
	public @NonNull SqlAuditConfigBuilder createdAtColumn(@NonNull String name) {
		this.createdAtColumn = Objects.requireNonNull(name, "Sql created-at column name must not be null");
		return this;
	}
	
	public @NonNull SqlAuditConfigBuilder createdByColumn(@NonNull String name) {
		this.createdByColumn = Objects.requireNonNull(name, "Sql created-by column name must not be null");
		return this;
	}
	
	public @NonNull SqlAuditConfigBuilder updatedAtColumn(@NonNull String name) {
		this.updatedAtColumn = Objects.requireNonNull(name, "Sql updated-at column name must not be null");
		return this;
	}
	
	public @NonNull SqlAuditConfigBuilder updatedByColumn(@NonNull String name) {
		this.updatedByColumn = Objects.requireNonNull(name, "Sql updated-by column name must not be null");
		return this;
	}
	
	public @NonNull SqlAuditConfigBuilder versionType(@NonNull SqlType<Long> type) {
		this.versionType = Objects.requireNonNull(type, "Sql version type must not be null");
		return this;
	}
	
	public @NonNull SqlAuditConfigBuilder timestampType(@NonNull SqlType<LocalDateTime> type) {
		this.timestampType = Objects.requireNonNull(type, "Sql timestamp type must not be null");
		return this;
	}
	
	public @NonNull SqlAuditConfigBuilder userType(@NonNull SqlType<String> type) {
		this.userType = Objects.requireNonNull(type, "Sql user type must not be null");
		return this;
	}
	
	public @NonNull SqlAuditConfigBuilder valueSource(@NonNull SqlAuditValueSource valueSource) {
		this.valueSource = Objects.requireNonNull(valueSource, "Sql audit value source must not be null");
		return this;
	}
	
	public @NonNull SqlAuditConfigBuilder clock(@NonNull Clock clock) {
		this.clock = Objects.requireNonNull(clock, "Clock must not be null");
		return this;
	}
	
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
