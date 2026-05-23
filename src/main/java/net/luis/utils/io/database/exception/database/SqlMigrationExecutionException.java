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

package net.luis.utils.io.database.exception.database;

import net.luis.utils.io.database.exception.SqlDatabaseException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.SQLException;
import java.util.OptionalInt;

/**
 * Thrown when executing a migration against the database fails at the driver level.<br>
 *
 * @author Luis-St
 */
public class SqlMigrationExecutionException extends SqlDatabaseException {
	
	/**
	 * Sentinel value used for {@link #version} when the migration version is unknown.<br>
	 */
	private static final int UNKNOWN_VERSION = -1;
	
	/**
	 * The version of the migration that failed to execute, or {@code -1} if unknown.<br>
	 */
	private final int version;
	
	/**
	 * Constructs a new migration execution exception from the given message and cause.<br>
	 *
	 * @param message The detail message, may be null
	 * @param cause The sql exception cause
	 * @throws NullPointerException If the cause is null
	 */
	public SqlMigrationExecutionException(@Nullable String message, @NonNull SQLException cause) {
		this(message, cause, UNKNOWN_VERSION);
	}
	
	/**
	 * Constructs a new migration execution exception from the given message, cause and migration version.<br>
	 *
	 * @param message The detail message, may be null
	 * @param cause The sql exception cause
	 * @param version The version of the migration that failed, or {@code -1} if unknown
	 * @throws NullPointerException If the cause is null
	 */
	public SqlMigrationExecutionException(@Nullable String message, @NonNull SQLException cause, int version) {
		super(message, cause);
		this.version = version;
	}
	
	/**
	 * Returns the version of the migration that failed to execute.<br>
	 * @return An optional int holding the migration version, or an empty optional int if unknown
	 */
	public @NonNull OptionalInt version() {
		return this.version == UNKNOWN_VERSION ? OptionalInt.empty() : OptionalInt.of(this.version);
	}
}
