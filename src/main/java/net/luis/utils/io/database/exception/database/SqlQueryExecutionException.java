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
import java.util.Optional;

/**
 * Thrown when executing a rendered statement fails at the driver level for a reason that is not captured by a more
 * specific leaf type.<br>
 * The rendered sql string is carried for diagnostics; bound parameter values are intentionally not captured.<br>
 *
 * @author Luis-St
 */
public class SqlQueryExecutionException extends SqlDatabaseException {
	
	/**
	 * The rendered sql string that failed to execute, or null if unavailable.<br>
	 */
	private final @Nullable String sql;
	
	/**
	 * Constructs a new query execution exception from the given message and cause.<br>
	 *
	 * @param message The detail message, may be null
	 * @param cause The sql exception cause
	 * @throws NullPointerException If the cause is null
	 */
	public SqlQueryExecutionException(@Nullable String message, @NonNull SQLException cause) {
		this(message, cause, null);
	}
	
	/**
	 * Constructs a new query execution exception from the given message, cause and rendered sql string.<br>
	 *
	 * @param message The detail message, may be null
	 * @param cause The sql exception cause
	 * @param sql The rendered sql string that failed to execute, may be null
	 * @throws NullPointerException If the cause is null
	 */
	public SqlQueryExecutionException(@Nullable String message, @NonNull SQLException cause, @Nullable String sql) {
		super(message, cause);
		this.sql = sql;
	}
	
	/**
	 * Returns the rendered sql string that failed to execute.<br>
	 * @return An optional holding the rendered sql string, or an empty optional if unavailable
	 */
	public @NonNull Optional<String> sql() {
		return Optional.ofNullable(this.sql);
	}
}
