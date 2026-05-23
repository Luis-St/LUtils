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

package net.luis.utils.io.database.exception.client.dialect;

import org.jspecify.annotations.Nullable;

/**
 * Thrown when a dialect cannot render a requested sql construct.<br>
 *
 * @author Luis-St
 */
public class SqlDialectUnsupportedRenderingException extends SqlDialectException {
	
	/**
	 * Constructs a new unsupported rendering exception with the given detail message.<br>
	 * @param message The detail message, may be null
	 */
	public SqlDialectUnsupportedRenderingException(@Nullable String message) {
		super(message);
	}
	
	/**
	 * Constructs a new unsupported rendering exception with the given detail message and cause.<br>
	 *
	 * @param message The detail message, may be null
	 * @param cause The cause, may be null
	 */
	public SqlDialectUnsupportedRenderingException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new unsupported rendering exception with the given cause.<br>
	 * @param cause The cause, may be null
	 */
	public SqlDialectUnsupportedRenderingException(@Nullable Throwable cause) {
		super(cause);
	}
}
