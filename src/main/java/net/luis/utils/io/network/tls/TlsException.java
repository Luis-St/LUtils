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

package net.luis.utils.io.network.tls;

import org.jspecify.annotations.Nullable;

import java.io.IOException;

/**
 * Thrown when the TLS record layer fails.<br>
 * <p>
 *     This is the root of every checked exception in this package.<br>
 *     A failure of the wrapped transport is reported as this exception with the original exception as its cause.
 * </p>
 * <p>
 *     A failure that ends the connection with an alert is reported as a {@link TlsAlertException},<br>
 *     which tells the caller whether the alert still has to be sent to the peer.
 * </p>
 *
 * @see TlsAlertException
 *
 * @author Luis-St
 */
public class TlsException extends IOException {
	
	/**
	 * Constructs a new tls exception with no details.<br>
	 */
	public TlsException() {}
	
	/**
	 * Constructs a new tls exception with the specified message.<br>
	 * @param message The message of the exception
	 */
	public TlsException(@Nullable String message) {
		super(message);
	}
	
	/**
	 * Constructs a new tls exception with the specified message and cause.<br>
	 *
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public TlsException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new tls exception with the specified cause.<br>
	 * @param cause The cause of the exception
	 */
	public TlsException(@Nullable Throwable cause) {
		super(cause);
	}
}
