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

import java.util.Objects;
import java.util.Optional;

/**
 * The body of a TLS alert record.<br>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * out.writeAlert(TlsAlert.fatal(TlsAlertDescription.HANDSHAKE_FAILURE));
 *
 * TlsAlert alert = TlsAlert.decode(record.data()).orElseThrow();
 * }</pre>
 *
 * @see TlsAlertLevel
 * @see TlsAlertDescription
 * @see TlsOutputStream#writeAlert(TlsAlert)
 *
 * @author Luis-St
 *
 * @param level The severity of the alert
 * @param description The reason the alert was sent
 */
public record TlsAlert(@NonNull TlsAlertLevel level, @NonNull TlsAlertDescription description) {
	
	/**
	 * The length of an encoded alert in bytes.<br>
	 */
	private static final int ENCODED_LENGTH = 2;
	/**
	 * The alert announcing that the sender will not send any more data.<br>
	 */
	public static final TlsAlert CLOSE_NOTIFY = new TlsAlert(TlsAlertLevel.WARNING, TlsAlertDescription.CLOSE_NOTIFY);
	
	/**
	 * Constructs a new alert.<br>
	 *
	 * @param level The severity of the alert
	 * @param description The reason the alert was sent
	 * @throws NullPointerException If the level or the description is null
	 */
	public TlsAlert {
		Objects.requireNonNull(level, "Level must not be null");
		Objects.requireNonNull(description, "Description must not be null");
	}
	
	/**
	 * Creates a fatal alert with the given description.<br>
	 *
	 * @param description The reason the alert is sent
	 * @return The created alert
	 * @throws NullPointerException If the description is null
	 */
	public static @NonNull TlsAlert fatal(@NonNull TlsAlertDescription description) {
		Objects.requireNonNull(description, "Description must not be null");
		return new TlsAlert(TlsAlertLevel.FATAL, description);
	}
	
	/**
	 * Creates a warning alert with the given description.<br>
	 *
	 * @param description The reason the alert is sent
	 * @return The created alert
	 * @throws NullPointerException If the description is null
	 */
	public static @NonNull TlsAlert warning(@NonNull TlsAlertDescription description) {
		Objects.requireNonNull(description, "Description must not be null");
		return new TlsAlert(TlsAlertLevel.WARNING, description);
	}
	
	/**
	 * Decodes the body of an alert record.<br>
	 *
	 * @param body The two byte alert record body
	 * @return An optional containing the decoded alert, or an empty optional if the body has the wrong length or holds unknown codes
	 * @throws NullPointerException If the body is null
	 */
	public static @NonNull Optional<TlsAlert> decode(byte @NonNull [] body) {
		Objects.requireNonNull(body, "Body must not be null");
		
		if (body.length != ENCODED_LENGTH) {
			return Optional.empty();
		}
		return TlsAlertLevel.byCode(body[0] & 0xFF).flatMap(level -> TlsAlertDescription.byCode(body[1] & 0xFF).map(description -> new TlsAlert(level, description)));
	}
	
	/**
	 * Encodes this alert into the body of an alert record.<br>
	 * @return The two byte alert record body
	 */
	public byte @NonNull [] encode() {
		return new byte[] { (byte) this.level.code(), (byte) this.description.code() };
	}
	
	/**
	 * Checks whether this alert ends the connection in the direction it is sent.<br>
	 * @return True if this alert is fatal or a {@code close_notify}
	 */
	public boolean isTerminal() {
		return this.level == TlsAlertLevel.FATAL || this.description == TlsAlertDescription.CLOSE_NOTIFY;
	}
}
