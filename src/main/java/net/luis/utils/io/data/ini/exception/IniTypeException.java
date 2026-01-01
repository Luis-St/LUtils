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

package net.luis.utils.io.data.ini.exception;

import org.jspecify.annotations.Nullable;

/**
 * Thrown when an ini element is not of the expected type.<br>
 * The exception message will contain the details about the expected and actual types.<br>
 *
 * @author Luis-St
 */
public class IniTypeException extends RuntimeException {
	
	/**
	 * Constructs a new ini type exception with no details.<br>
	 */
	public IniTypeException() {}
	
	/**
	 * Constructs a new ini type exception with the specified message.<br>
	 * @param message The message of the exception
	 */
	public IniTypeException(@Nullable String message) {
		super(message);
	}
	
	/**
	 * Constructs a new ini type exception with the specified message and cause.<br>
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public IniTypeException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new ini type exception with the specified cause.<br>
	 * @param cause The cause of the exception
	 */
	public IniTypeException(@Nullable Throwable cause) {
		super(cause);
	}
}
