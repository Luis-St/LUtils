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

package net.luis.utils.io.properties.exception;

import org.jetbrains.annotations.Nullable;

/**
 * Thrown when the part of a property key does not meet the set requirements.<br>
 * The exception message should contain details about the error.<br>
 * The message may also contain information about the requirements of the key part.<br>
 *
 * @author Luis-St
 */
public class IllegalPropertyKeyPartException extends IllegalPropertyKeyException {
	
	/**
	 * Constructs a new illegal property key part exception with no details.<br>
	 */
	public IllegalPropertyKeyPartException() {}
	
	/**
	 * Constructs a new illegal property key part exception with the specified message.<br>
	 * @param message The message of the exception
	 */
	public IllegalPropertyKeyPartException(@Nullable String message) {
		super(message);
	}
	
	/**
	 * Constructs a new illegal property key part exception with the specified message and cause.<br>
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public IllegalPropertyKeyPartException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new illegal property key part exception with the specified cause.<br>
	 * @param cause The cause of the exception
	 */
	public IllegalPropertyKeyPartException(@Nullable Throwable cause) {
		super(cause);
	}
}
