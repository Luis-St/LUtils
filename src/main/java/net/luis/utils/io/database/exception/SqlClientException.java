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
 * Base type for all client originated sql failures.<br>
 * <p>
 *     A client exception represents a library or caller logic error that never reached the jdbc driver,<br>
 *     such as a misused query builder, an unsupported dialect feature or a result cardinality mismatch.
 * </p>
 * <p>
 *     Unlike {@link SqlDatabaseException}, <br>
 *     a client exception carries no jdbc diagnostics because no {@link java.sql.SQLException} was ever produced.
 * </p>
 *
 * @author Luis-St
 */
public class SqlClientException extends SqlException {
	
	/**
	 * Constructs a new client exception with the given detail message.<br>
	 * @param message The detail message, may be null
	 */
	public SqlClientException(@Nullable String message) {
		super(message);
	}
	
	/**
	 * Constructs a new client exception with the given detail message and cause.<br>
	 *
	 * @param message The detail message, may be null
	 * @param cause The cause, may be null
	 */
	public SqlClientException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new client exception with the given cause.<br>
	 * @param cause The cause, may be null
	 */
	public SqlClientException(@Nullable Throwable cause) {
		super(cause);
	}
}
