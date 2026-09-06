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
 * Thrown when a cryptographic operation fails.<br>
 * <p>
 *     This is the root of every exception in this package.<br>
 *     Almost everything the JCA reports as a checked exception is an unrecoverable configuration failure at the call site,<br>
 *     so it is wrapped into this unchecked type instead of being propagated.
 * </p>
 * <p>
 *     The one condition that is genuinely meaningful to a caller is an integrity failure,<br>
 *     which is reported as an {@link AuthenticationException} and can be caught deliberately.
 * </p>
 *
 * @see AuthenticationException
 * @see MalformedDataException
 * @see UnsupportedAlgorithmException
 *
 * @author Luis-St
 */
public class CryptoException extends RuntimeException {
	
	/**
	 * Constructs a new crypto exception with no details.<br>
	 */
	public CryptoException() {}
	
	/**
	 * Constructs a new crypto exception with the specified message.<br>
	 * @param message The message of the exception
	 */
	public CryptoException(@Nullable String message) {
		super(message);
	}
	
	/**
	 * Constructs a new crypto exception with the specified message and cause.<br>
	 *
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public CryptoException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new crypto exception with the specified cause.<br>
	 * @param cause The cause of the exception
	 */
	public CryptoException(@Nullable Throwable cause) {
		super(cause);
	}
}
