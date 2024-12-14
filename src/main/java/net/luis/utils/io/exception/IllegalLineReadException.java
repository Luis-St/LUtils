/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

package net.luis.utils.io.exception;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * Thrown when an io reader reads a line from a file in an illegal format.<br>
 * The exception message should contain details about the error.<br>
 * The message may also contain information about the expected format of the line.<br>
 *
 * @author Luis-St
 */
@SuppressWarnings("CheckedExceptionClass")
public class IllegalLineReadException extends IOException {
	
	/**
	 * Constructs a new illegal line read exception with no details.<br>
	 */
	public IllegalLineReadException() {}
	
	/**
	 * Constructs a new illegal line read exception with the specified message.<br>
	 * @param message The message of the exception
	 */
	public IllegalLineReadException(@Nullable String message) {
		super(message);
	}
	
	/**
	 * Constructs a new illegal line read exception with the specified message and cause.<br>
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public IllegalLineReadException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new illegal line read exception with the specified cause.<br>
	 * @param cause The cause of the exception
	 */
	public IllegalLineReadException(@Nullable Throwable cause) {
		super(cause);
	}
}
