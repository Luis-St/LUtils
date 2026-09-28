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
 * Thrown when authenticated data fails its integrity check.<br>
 * This covers an AEAD tag mismatch, a bad signature, a bad MAC and a key commitment mismatch.<br>
 * <p>
 *     This is the only exception in this package that callers routinely need to catch.<br>
 *     It always means the same thing: the data was modified in transit, or the key does not belong to it.
 * </p>
 * <p>
 *     The message never distinguishes which of the two it was,<br>
 *     because that distinction is not available to the verifier and guessing at it would leak information to an attacker.
 * </p>
 *
 * @author Luis-St
 */
public class AuthenticationException extends CryptoException {
	
	/**
	 * Constructs a new authentication exception with no details.<br>
	 */
	public AuthenticationException() {}
	
	/**
	 * Constructs a new authentication exception with the specified message.<br>
	 * @param message The message of the exception
	 */
	public AuthenticationException(@Nullable String message) {
		super(message);
	}
	
	/**
	 * Constructs a new authentication exception with the specified message and cause.<br>
	 *
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public AuthenticationException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new authentication exception with the specified cause.<br>
	 * @param cause The cause of the exception
	 */
	public AuthenticationException(@Nullable Throwable cause) {
		super(cause);
	}
}
