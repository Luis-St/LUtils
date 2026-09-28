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

import net.luis.utils.io.network.connection.ssl.TlsProtocol;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Optional;

/**
 * The TLS protocol versions the record layer of this package can protect records for.<br>
 * <p>
 *     Only TLS 1.2 and TLS 1.3 are modeled.<br>
 *     The older versions are deprecated by RFC 8996 and only offer the block cipher suites, which this record layer does not implement.
 * </p>
 *
 * @see TlsProtocol
 *
 * @author Luis-St
 */
public enum TlsVersion {
	
	/**
	 * TLS 1.2, whose AEAD cipher suites authenticate the sequence number, content type, version and length of each record.<br>
	 */
	TLS_1_2(0x0303, TlsProtocol.TLS_V1_2),
	/**
	 * TLS 1.3, which hides the real content type inside the ciphertext and authenticates the record header.<br>
	 */
	TLS_1_3(0x0304, TlsProtocol.TLS_V1_3);
	
	/**
	 * The two byte version code as it appears on the wire.<br>
	 */
	private final int code;
	/**
	 * The matching protocol constant of the JSSE based connections.<br>
	 */
	private final TlsProtocol protocol;
	
	/**
	 * Constructs a new version constant.<br>
	 *
	 * @param code The two byte version code
	 * @param protocol The matching protocol constant
	 */
	TlsVersion(int code, @NonNull TlsProtocol protocol) {
		this.code = code;
		this.protocol = Objects.requireNonNull(protocol, "Protocol must not be null");
	}
	
	/**
	 * Returns the version with the given two byte code.<br>
	 *
	 * @param code The two byte version code
	 * @return An optional containing the matching version, or an empty optional if the code is unknown
	 */
	public static @NonNull Optional<TlsVersion> byCode(int code) {
		for (TlsVersion version : values()) {
			if (version.code == code) {
				return Optional.of(version);
			}
		}
		return Optional.empty();
	}
	
	/**
	 * Returns the two byte version code as it appears on the wire.<br>
	 * @return The version code, for example {@code 0x0304} for TLS 1.3
	 */
	public int code() {
		return this.code;
	}
	
	/**
	 * Returns the matching protocol constant of the JSSE based connections.<br>
	 * @return The protocol constant
	 */
	public @NonNull TlsProtocol protocol() {
		return this.protocol;
	}
	
	/**
	 * Returns the maximum length a protected record body may have on the wire.<br>
	 * This is the maximum plaintext length plus the expansion the version allows for the cipher.<br>
	 *
	 * @return The maximum body length in bytes
	 */
	public int maxCiphertextLength() {
		return switch (this) {
			case TLS_1_2 -> TlsRecord.MAX_PLAINTEXT_LENGTH + 2048;
			case TLS_1_3 -> TlsRecord.MAX_PLAINTEXT_LENGTH + 256;
		};
	}
	
	//region Object overrides
	@Override
	public String toString() {
		return this.protocol.protocolName();
	}
	//endregion
}
