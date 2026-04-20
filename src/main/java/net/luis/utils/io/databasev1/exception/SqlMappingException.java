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

/*package net.luis.utils.io.databasev1.exception;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

*//**
 * Exception thrown when a SQL result set cannot be mapped to the expected record type.<br>
 * <p>
 *     Carries the expected record type and the actual column names from the result set
 *     to aid in diagnosing mapping mismatches.<br>
 * </p>
 *
 * @author Luis-St
 *//*
public class SqlMappingException extends SqlException {
	
	*//**
	 * Constructs a new SQL mapping exception with no details.<br>
	 *//*
	public SqlMappingException() {}
	
	*//**
	 * Constructs a new SQL mapping exception with the specified message.<br>
	 * @param message The message of the exception
	 *//*
	public SqlMappingException(@Nullable String message) {
		super(message);
	}
	
	*//**
	 * Constructs a new SQL mapping exception with the specified message and cause.<br>
	 *
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 *//*
	public SqlMappingException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	*//**
	 * Constructs a new SQL mapping exception with the specified cause.<br>
	 * @param cause The cause of the exception
	 *//*
	public SqlMappingException(@Nullable Throwable cause) {
		super(cause);
	}
	
	*//**
	 * Returns the expected record type that the mapping targeted.<br>
	 * @return The expected record type
	 *//*
	public @NonNull Class<?> getExpectedType() {
		throw new UnsupportedOperationException();
	}
	
	*//**
	 * Returns the actual column names from the result set.<br>
	 * @return The list of actual column names
	 *//*
	public @NonNull List<String> getActualColumns() {
		throw new UnsupportedOperationException();
	}
}*/
