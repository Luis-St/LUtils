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

import net.luis.utils.crypto.util.CryptoBytes;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * A single TLS record after its protection has been removed.<br>
 * <p>
 *     For TLS 1.3 the content type is the inner one taken from the end of the decrypted payload,<br>
 *     not the {@code application_data} type that appeared in the record header.
 * </p>
 *
 * @see TlsInputStream#readRecord()
 *
 * @author Luis-St
 *
 * @param type The content type of the record
 * @param data The plaintext fragment of the record, which is not copied
 */
public record TlsRecord(@NonNull TlsContentType type, byte @NonNull [] data) {
	
	/**
	 * The length of a record header in bytes.<br>
	 */
	public static final int HEADER_LENGTH = 5;
	/**
	 * The maximum length of a plaintext fragment in bytes, which is {@code 2^14}.<br>
	 */
	public static final int MAX_PLAINTEXT_LENGTH = 1 << 14;
	
	/**
	 * Constructs a new record.<br>
	 *
	 * @param type The content type of the record
	 * @param data The plaintext fragment of the record
	 * @throws NullPointerException If the type or the data is null
	 */
	public TlsRecord {
		Objects.requireNonNull(type, "Type must not be null");
		Objects.requireNonNull(data, "Data must not be null");
	}
	
	/**
	 * Builds the five byte header of a record.<br>
	 * <p>
	 *     The version field is always {@code 0x0303}.<br>
	 *     TLS 1.2 writes its own version there, and TLS 1.3 freezes the field at that value for middlebox compatibility.
	 * </p>
	 *
	 * @param type The content type written into the header
	 * @param length The length of the record body
	 * @return The built header
	 * @throws NullPointerException If the type is null
	 */
	static byte @NonNull [] header(@NonNull TlsContentType type, int length) {
		Objects.requireNonNull(type, "Type must not be null");
		return CryptoBytes.concat(new byte[] { (byte) type.code() }, CryptoBytes.of((short) TlsVersion.TLS_1_2.code()), CryptoBytes.of((short) length));
	}
	
	/**
	 * Returns the length of the plaintext fragment.<br>
	 * @return The fragment length in bytes
	 */
	public int length() {
		return this.data.length;
	}
	
	//region Object overrides
	@Override
	public String toString() {
		return this.type + "[" + this.data.length + "]";
	}
	//endregion
}
