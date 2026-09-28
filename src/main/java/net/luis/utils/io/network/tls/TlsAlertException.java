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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Thrown when a TLS alert ends the connection.<br>
 * <p>
 *     A local alert was raised by this side, because a received record was malformed or did not authenticate.<br>
 *     It has not been sent yet, so the caller should forward it to the peer before closing the connection.<br>
 *     A remote alert was received from the peer and must not be answered.
 * </p>
 * <pre>{@code
 * try {
 *     int read = in.read(buffer);
 * } catch (TlsAlertException e) {
 *     if (e.isLocal()) {
 *         out.writeAlert(e.alert());
 *     }
 *     throw e;
 * }
 * }</pre>
 *
 * @see TlsAlert
 * @see TlsException
 *
 * @author Luis-St
 */
public class TlsAlertException extends TlsException {
	
	/**
	 * The alert that ends the connection.<br>
	 */
	private final TlsAlert alert;
	/**
	 * Whether the alert was raised locally instead of being received from the peer.<br>
	 */
	private final boolean local;
	
	/**
	 * Constructs a new tls alert exception.<br>
	 *
	 * @param alert The alert that ends the connection
	 * @param local Whether the alert was raised locally instead of being received from the peer
	 * @param message The detail message, may be null
	 * @throws NullPointerException If the alert is null
	 */
	public TlsAlertException(@NonNull TlsAlert alert, boolean local, @Nullable String message) {
		super(Objects.requireNonNull(alert, "Alert must not be null") + (message == null ? "" : ": " + message));
		this.alert = alert;
		this.local = local;
	}
	
	/**
	 * Creates an exception for a fatal alert raised by this side.<br>
	 *
	 * @param description The reason of the alert
	 * @param message The detail message, may be null
	 * @return The created exception
	 * @throws NullPointerException If the description is null
	 */
	public static @NonNull TlsAlertException local(@NonNull TlsAlertDescription description, @Nullable String message) {
		Objects.requireNonNull(description, "Description must not be null");
		return new TlsAlertException(TlsAlert.fatal(description), true, message);
	}
	
	/**
	 * Creates an exception for an alert received from the peer.<br>
	 *
	 * @param alert The received alert
	 * @return The created exception
	 * @throws NullPointerException If the alert is null
	 */
	public static @NonNull TlsAlertException remote(@NonNull TlsAlert alert) {
		Objects.requireNonNull(alert, "Alert must not be null");
		return new TlsAlertException(alert, false, "Alert received from peer");
	}
	
	/**
	 * Returns the alert that ends the connection.<br>
	 * @return The alert
	 */
	public @NonNull TlsAlert alert() {
		return this.alert;
	}
	
	/**
	 * Checks whether the alert was raised by this side and still has to be sent to the peer.<br>
	 * @return True if the alert was raised locally, false if it was received from the peer
	 */
	public boolean isLocal() {
		return this.local;
	}
}
