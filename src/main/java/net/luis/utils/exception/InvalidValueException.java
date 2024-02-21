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

package net.luis.utils.exception;

import org.jetbrains.annotations.Nullable;

/**
 * Thrown to indicate that the value of a system property is invalid.<br>
 * <p>
 *     The exception message should contain the name of the system property and the invalid value.<br>
 *     The message may also contain a description of the expected value.<br>
 * </p>
 *
 * @author Luis-St
 */
public class InvalidValueException extends RuntimeException {
	
	/**
	 * Constructs a new {@link InvalidValueException} with no details.<br>
	 */
	public InvalidValueException() {}
	
	/**
	 * Constructs a new {@link InvalidValueException} with the specified message.<br>
	 * @param message The message of the exception
	 */
	public InvalidValueException(@Nullable String message) {
		super(message);
	}
	
	/**
	 * Constructs a new {@link InvalidValueException} with the specified message and cause.<br>
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public InvalidValueException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new {@link InvalidValueException} with the specified cause.<br>
	 * @param cause The cause of the exception
	 */
	public InvalidValueException(@Nullable Throwable cause) {
		super(cause);
	}
	
}
