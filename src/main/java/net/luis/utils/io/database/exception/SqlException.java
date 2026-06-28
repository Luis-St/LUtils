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

package net.luis.utils.io.database.exception;

import org.jspecify.annotations.Nullable;

/**
 * Checked root type for all failures raised by the database system.<br>
 * Every more specific failure extends this type, split into two origin-based paths:
 * <p>
 *     {@link SqlDatabaseException} for failures that reached the jdbc driver<br>
 *     and {@link SqlClientException} for library or caller logic errors that never did.<br>
 *     This base type itself carries only a message and cause; the typed diagnostics live on its subtypes.
 * </p>
 *
 * @author Luis-St
 */
public class SqlException extends Exception {
	
	/**
	 * Constructs a new sql exception with the given detail message.<br>
	 * @param message The detail message, may be null
	 */
	public SqlException(@Nullable String message) {
		super(message);
	}
	
	/**
	 * Constructs a new sql exception with the given detail message and cause.<br>
	 *
	 * @param message The detail message, may be null
	 * @param cause The cause, may be null
	 */
	public SqlException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new sql exception with the given cause.<br>
	 * @param cause The cause, may be null
	 */
	public SqlException(@Nullable Throwable cause) {
		super(cause);
	}
}
