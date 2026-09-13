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

import java.util.Optional;

/**
 * The reason a TLS alert was sent.<br>
 * The constants cover the descriptions registered for TLS 1.2 and TLS 1.3.<br>
 *
 * @see TlsAlert
 *
 * @author Luis-St
 */
public enum TlsAlertDescription {
	
	/**
	 * The sender will not send any more data on this connection.<br>
	 */
	CLOSE_NOTIFY(0),
	/**
	 * An inappropriate message, such as a wrong handshake message or premature application data, was received.<br>
	 */
	UNEXPECTED_MESSAGE(10),
	/**
	 * A record was received that did not authenticate.<br>
	 */
	BAD_RECORD_MAC(20),
	/**
	 * A record was received whose length exceeds the limit of the negotiated version.<br>
	 */
	RECORD_OVERFLOW(22),
	/**
	 * The sender was unable to negotiate an acceptable set of security parameters.<br>
	 */
	HANDSHAKE_FAILURE(40),
	/**
	 * A certificate was corrupt or contained signatures that did not verify.<br>
	 */
	BAD_CERTIFICATE(42),
	/**
	 * A certificate was of an unsupported type.<br>
	 */
	UNSUPPORTED_CERTIFICATE(43),
	/**
	 * A certificate was revoked by its signer.<br>
	 */
	CERTIFICATE_REVOKED(44),
	/**
	 * A certificate has expired or is not currently valid.<br>
	 */
	CERTIFICATE_EXPIRED(45),
	/**
	 * Some other unspecified issue arose in processing a certificate.<br>
	 */
	CERTIFICATE_UNKNOWN(46),
	/**
	 * A field in the handshake was incorrect or inconsistent with other fields.<br>
	 */
	ILLEGAL_PARAMETER(47),
	/**
	 * The certificate authority of a certificate chain could not be located or matched with a trust anchor.<br>
	 */
	UNKNOWN_CA(48),
	/**
	 * A valid certificate or pre-shared key was received, but access control decided not to proceed.<br>
	 */
	ACCESS_DENIED(49),
	/**
	 * A message could not be decoded because a field was out of range or the length was incorrect.<br>
	 */
	DECODE_ERROR(50),
	/**
	 * A handshake cryptographic operation failed.<br>
	 */
	DECRYPT_ERROR(51),
	/**
	 * The protocol version the peer attempted to negotiate is recognized but not supported.<br>
	 */
	PROTOCOL_VERSION(70),
	/**
	 * The negotiation failed because the peer requires parameters more secure than the sender supports.<br>
	 */
	INSUFFICIENT_SECURITY(71),
	/**
	 * An internal error unrelated to the peer or to the correctness of the protocol prevents continuing.<br>
	 */
	INTERNAL_ERROR(80),
	/**
	 * A fallback retry was detected although the sender supports a higher protocol version.<br>
	 */
	INAPPROPRIATE_FALLBACK(86),
	/**
	 * The user cancels the handshake for a reason unrelated to a protocol failure.<br>
	 */
	USER_CANCELED(90),
	/**
	 * A handshake message was received without an extension that is mandatory for it.<br>
	 */
	MISSING_EXTENSION(109),
	/**
	 * A handshake message was received with an extension that is not allowed in it.<br>
	 */
	UNSUPPORTED_EXTENSION(110),
	/**
	 * The server has no identity matching the requested server name.<br>
	 */
	UNRECOGNIZED_NAME(112),
	/**
	 * An invalid or unacceptable OCSP response was provided by the server.<br>
	 */
	BAD_CERTIFICATE_STATUS_RESPONSE(113),
	/**
	 * The server does not recognize the offered pre-shared key identity.<br>
	 */
	UNKNOWN_PSK_IDENTITY(115),
	/**
	 * The server requires a client certificate, but the client did not provide one.<br>
	 */
	CERTIFICATE_REQUIRED(116),
	/**
	 * None of the application protocols offered by the client is supported by the server.<br>
	 */
	NO_APPLICATION_PROTOCOL(120);
	
	/**
	 * The single byte description code as it appears on the wire.<br>
	 */
	private final int code;
	
	/**
	 * Constructs a new alert description constant.<br>
	 * @param code The single byte description code
	 */
	TlsAlertDescription(int code) {
		this.code = code;
	}
	
	/**
	 * Returns the alert description with the given single byte code.<br>
	 *
	 * @param code The single byte description code
	 * @return An optional containing the matching description, or an empty optional if the code is unknown
	 */
	public static @NonNull Optional<TlsAlertDescription> byCode(int code) {
		for (TlsAlertDescription description : values()) {
			if (description.code == code) {
				return Optional.of(description);
			}
		}
		return Optional.empty();
	}
	
	/**
	 * Returns the single byte description code as it appears on the wire.<br>
	 * @return The description code
	 */
	public int code() {
		return this.code;
	}
}
