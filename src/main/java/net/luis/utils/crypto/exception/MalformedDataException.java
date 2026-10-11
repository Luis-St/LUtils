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

package net.luis.utils.crypto.exception;

import org.jspecify.annotations.Nullable;

/**
 * Thrown when a serialized artifact cannot be parsed.<br>
 * This covers a bad magic value, an unknown suite id, a truncated input and a malformed encoding.<br>
 * <p>
 *     This is a structural failure, reported before any cryptographic check runs.<br>
 *     It says nothing about whether the data would have authenticated, only that it could not be read.
 * </p>
 *
 * @author Luis-St
 */
public class MalformedDataException extends CryptoException {
	
	/**
	 * Constructs a new malformed data exception with no details.<br>
	 */
	public MalformedDataException() {}
	
	/**
	 * Constructs a new malformed data exception with the specified message.<br>
	 * @param message The message of the exception
	 */
	public MalformedDataException(@Nullable String message) {
		super(message);
	}
	
	/**
	 * Constructs a new malformed data exception with the specified message and cause.<br>
	 *
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public MalformedDataException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new malformed data exception with the specified cause.<br>
	 * @param cause The cause of the exception
	 */
	public MalformedDataException(@Nullable Throwable cause) {
		super(cause);
	}
}
