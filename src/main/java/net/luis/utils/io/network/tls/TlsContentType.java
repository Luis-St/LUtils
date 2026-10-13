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
 * The content types of the TLS record layer.<br>
 *
 * @author Luis-St
 */
public enum TlsContentType {
	
	/**
	 * The single byte record that switches TLS 1.2 to the negotiated keys.<br>
	 * TLS 1.3 only sends it for middlebox compatibility, where it carries no meaning.<br>
	 */
	CHANGE_CIPHER_SPEC(20),
	/**
	 * A record carrying an alert.<br>
	 */
	ALERT(21),
	/**
	 * A record carrying handshake messages.<br>
	 */
	HANDSHAKE(22),
	/**
	 * A record carrying application data.<br>
	 */
	APPLICATION_DATA(23);
	
	/**
	 * The single byte type code as it appears on the wire.<br>
	 */
	private final int code;
	
	/**
	 * Constructs a new content type constant.<br>
	 * @param code The single byte type code
	 */
	TlsContentType(int code) {
		this.code = code;
	}
	
	/**
	 * Returns the content type with the given single byte code.<br>
	 *
	 * @param code The single byte type code
	 * @return An optional containing the matching content type, or an empty optional if the code is unknown
	 */
	public static @NonNull Optional<TlsContentType> byCode(int code) {
		for (TlsContentType type : values()) {
			if (type.code == code) {
				return Optional.of(type);
			}
		}
		return Optional.empty();
	}
	
	/**
	 * Returns the single byte type code as it appears on the wire.<br>
	 * @return The type code
	 */
	public int code() {
		return this.code;
	}
}
