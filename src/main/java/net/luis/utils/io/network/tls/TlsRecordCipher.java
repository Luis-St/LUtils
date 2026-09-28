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
 * Protects and unprotects the records of one direction of a TLS connection.<br>
 * <p>
 *     A cipher does not own the record sequence number.<br>
 *     The stream keeps it and passes it in, and resets it whenever a new cipher is installed.<br>
 *     Each direction of a connection uses its own cipher, which is why the record layer is split into two independent streams.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * TlsRecordCipher cipher = TlsCipherSuite.TLS_AES_256_GCM_SHA384.createCipher(key, iv);
 *
 * byte[] body = cipher.encrypt(0, TlsContentType.APPLICATION_DATA, plaintext);
 * TlsRecord record = cipher.decrypt(0, cipher.outerContentType(TlsContentType.APPLICATION_DATA), body);
 * }</pre>
 *
 * @see TlsAeadRecordCipher
 * @see TlsCipherSuite#createCipher(byte[], byte[])
 *
 * @author Luis-St
 */
public interface TlsRecordCipher {
	
	/**
	 * The initial cipher of every connection, which passes fragments through unchanged.<br>
	 */
	TlsRecordCipher NULL = new TlsRecordCipher() {
		
		@Override
		public byte @NonNull [] encrypt(long sequence, @NonNull TlsContentType type, byte @NonNull [] fragment) {
			Objects.requireNonNull(type, "Type must not be null");
			return Objects.requireNonNull(fragment, "Fragment must not be null");
		}
		
		@Override
		public @NonNull TlsRecord decrypt(long sequence, @NonNull TlsContentType type, byte @NonNull [] body) {
			Objects.requireNonNull(type, "Type must not be null");
			Objects.requireNonNull(body, "Body must not be null");
			
			return new TlsRecord(type, body);
		}
		
		@Override
		public String toString() {
			return "NULL";
		}
	};
	
	/**
	 * Turns a plaintext fragment into the body of a record.<br>
	 *
	 * @param sequence The record sequence number of this record
	 * @param type The real content type of the fragment
	 * @param fragment The plaintext fragment of at most {@link TlsRecord#MAX_PLAINTEXT_LENGTH} bytes
	 * @return The record body to write after the header
	 * @throws NullPointerException If the type or the fragment is null
	 */
	byte @NonNull [] encrypt(long sequence, @NonNull TlsContentType type, byte @NonNull [] fragment);
	
	/**
	 * Turns the body of a record back into its plaintext fragment.<br>
	 *
	 * @param sequence The record sequence number of this record
	 * @param type The content type from the record header
	 * @param body The record body as received
	 * @return The record with its real content type
	 * @throws NullPointerException If the type or the body is null
	 * @throws TlsAlertException If the record is malformed or does not authenticate
	 */
	@NonNull TlsRecord decrypt(long sequence, @NonNull TlsContentType type, byte @NonNull [] body) throws TlsAlertException;
	
	/**
	 * Returns the version this cipher protects records for.<br>
	 * @return An optional containing the version, or an empty optional for the {@link #NULL} cipher
	 */
	default @NonNull Optional<TlsVersion> version() {
		return Optional.empty();
	}
	
	/**
	 * Returns the content type written into the header of a record with the given real content type.<br>
	 * TLS 1.3 hides the real content type inside the ciphertext and always writes {@code application_data} into the header.<br>
	 *
	 * @param type The real content type
	 * @return The content type for the record header
	 * @throws NullPointerException If the type is null
	 */
	default @NonNull TlsContentType outerContentType(@NonNull TlsContentType type) {
		Objects.requireNonNull(type, "Type must not be null");
		return type;
	}
	
	/**
	 * Returns the maximum length a record body protected by this cipher may have on the wire.<br>
	 * @return The maximum body length in bytes
	 */
	default int maxBodyLength() {
		return TlsRecord.MAX_PLAINTEXT_LENGTH;
	}
}
