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

package net.luis.utils.io.database.exception.constraint;

import net.luis.utils.io.database.exception.SqlDatabaseException;
import org.jspecify.annotations.Nullable;

/**
 * Base exception for SQL constraint violations.<br>
 *
 * @author Luis-St
 */
public class SqlConstraintViolationException extends SqlDatabaseException {
	
	/**
	 * Constructs a new SQL constraint violation exception with no details.<br>
	 */
	public SqlConstraintViolationException() {}
	
	/**
	 * Constructs a new SQL constraint violation exception with the specified message.<br>
	 * @param message The message of the exception
	 */
	public SqlConstraintViolationException(@Nullable String message) {
		super(message);
	}
	
	/**
	 * Constructs a new SQL constraint violation exception with the specified message and cause.<br>
	 *
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public SqlConstraintViolationException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new SQL constraint violation exception with the specified cause.<br>
	 * @param cause The cause of the exception
	 */
	public SqlConstraintViolationException(@Nullable Throwable cause) {
		super(cause);
	}
}
