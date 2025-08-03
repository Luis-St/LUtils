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

import org.jetbrains.annotations.Nullable;

/**
 * Thrown when an error occurs during structured concurrency operations.<br>
 * This exception is used to indicate issues related to task management, execution, or completion within a structured concurrency context.<br>
 *
 * @author Luis-St
 */
public class StructuredConcurrencyException extends RuntimeException {
	
	/**
	 * Constructs a new structured concurrency exception with no details.<br>
	 */
	public StructuredConcurrencyException() {}
	
	/**
	 * Constructs a new structured concurrency exception with the specified message.<br>
	 * @param message The message of the exception
	 */
	public StructuredConcurrencyException(@Nullable String message) {
		super(message);
	}
	
	/**
	 * Constructs a new structured concurrency exception with the specified message and cause.<br>
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public StructuredConcurrencyException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new structured concurrency exception with the specified cause.<br>
	 * @param cause The cause of the exception
	 */
	public StructuredConcurrencyException(@Nullable Throwable cause) {
		super(cause);
	}
}
