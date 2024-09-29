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

import java.io.IOException;

/**
 * Thrown when the key of a property does not meet the set requirements.<br>
 * The exception message should contain details about the error.<br>
 * The message may also contain information about the requirements of the key.<br>
 *
 * @author Luis-St
 */
public class IllegalPropertyKeyException extends IOException {
	
	/**
	 * Constructs a new illegal property key exception with no details.<br>
	 */
	public IllegalPropertyKeyException() {}
	
	/**
	 * Constructs a new illegal property key exception with the specified message.<br>
	 * @param message The message of the exception
	 */
	public IllegalPropertyKeyException(@Nullable String message) {
		super(message);
	}
	
	/**
	 * Constructs a new illegal property key exception with the specified message and cause.<br>
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public IllegalPropertyKeyException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new illegal property key exception with the specified cause.<br>
	 * @param cause The cause of the exception
	 */
	public IllegalPropertyKeyException(@Nullable Throwable cause) {
		super(cause);
	}
}
