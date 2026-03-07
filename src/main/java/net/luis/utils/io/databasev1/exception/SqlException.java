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

package net.luis.utils.io.databasev1.exception;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Base checked exception for all SQL database operations.<br>
 * <p>
 *     All SQL exceptions extend this class, making them checked exceptions
 *     that must be explicitly handled or declared in method signatures.<br>
 * </p>
 *
 * @author Luis-St
 */
public class SqlException extends Exception {
	
	/**
	 * Constructs a new SQL exception with no details.<br>
	 */
	public SqlException() {}
	
	/**
	 * Constructs a new SQL exception with the specified message.<br>
	 * @param message The message of the exception
	 */
	public SqlException(@Nullable String message) {
		super(message);
	}
	
	/**
	 * Constructs a new SQL exception with the specified message and cause.<br>
	 *
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public SqlException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new SQL exception with the specified cause.<br>
	 * @param cause The cause of the exception
	 */
	public SqlException(@Nullable Throwable cause) {
		super(cause);
	}
	
	/**
	 * Returns the SQL state code.<br>
	 * @return The SQL state or {@code null} if not available
	 */
	public @Nullable String getSqlState() {
		return null;
	}
	
	/**
	 * Returns the vendor-specific error code.<br>
	 * @return The vendor error code
	 */
	public int getVendorCode() {
		return 0;
	}
	
	/**
	 * Returns the SQL statement that caused the exception, if available.<br>
	 * @return An optional containing the SQL statement
	 */
	public @NonNull Optional<String> getSql() {
		return Optional.empty();
	}
	
	/**
	 * Returns the parameters that were bound to the SQL statement.<br>
	 * @return The list of parameters
	 */
	public @NonNull List<Object> getParameters() {
		return List.of();
	}
}
