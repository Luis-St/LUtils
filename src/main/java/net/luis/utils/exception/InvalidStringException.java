/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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

package net.luis.utils.exception;

import org.jspecify.annotations.Nullable;

/**
 * Thrown to indicate that a string is invalid.<br>
 * This exception will be thrown if a string is not valid for a specific operation.<br>
 * For example:<br><br>
 * <ul>
 *     <li>If a string is empty and the operation requires a non-empty string.</li>
 *     <li>If a string is not in the correct format.</li>
 * </ul>
 * The exception message should contain a description of the invalid string.<br>
 * The message may also contain a description of the expected string.<br>
 *
 * @author Luis-St
 */
public class InvalidStringException extends RuntimeException {
	
	/**
	 * Constructs a new invalid string exception with no details.<br>
	 */
	public InvalidStringException() {}
	
	/**
	 * Constructs a new invalid string exception with the specified message.<br>
	 * @param message The message of the exception
	 */
	public InvalidStringException(@Nullable String message) {
		super(message);
	}
	
	/**
	 * Constructs a new invalid string exception with the specified message and cause.<br>
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public InvalidStringException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new invalid string exception with the specified cause.<br>
	 * @param cause The cause of the exception
	 */
	public InvalidStringException(@Nullable Throwable cause) {
		super(cause);
	}
}
