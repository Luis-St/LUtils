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

package net.luis.utils.crypto.key;

import net.luis.utils.crypto.Hashes;
import net.luis.utils.crypto.algorithm.HashAlgorithm;
import net.luis.utils.crypto.exception.CryptoException;
import net.luis.utils.crypto.util.CryptoBytes;
import net.luis.utils.util.UUIDs;
import org.jspecify.annotations.NonNull;

import java.security.PublicKey;
import java.util.Objects;
import java.util.UUID;

/**
 * A stable, non-secret identifier for a public key.<br>
 * <p>
 *     The identifier is the first 16 bytes of the SHA-256 digest of the key's encoded form, wrapped in a version 8 UUID.<br>
 *     Version 8 is the application-defined version, which is exactly what this is.
 * </p>
 * <p>
 *     {@link UUIDs#v3(UUID, byte[])} and {@link UUIDs#v5(UUID, byte[])} are deliberately not used:<br>
 *     They are specified over MD5 and SHA-1, and this library does not make either reachable.
 * </p>
 * <p>
 *     A key id is a lookup hint, never an authorization decision.<br>
 *     It is truncated and unauthenticated.<br>
 *     Two keys with the same id must still be distinguished by comparing the keys themselves.
 * </p>
 *
 * @author Luis-St
 *
 * @param value The uuid holding the truncated digest
 */
public record KeyId(@NonNull UUID value) {
	
	/**
	 * Constructs a new key id.<br>
	 * @throws NullPointerException If the value is null
	 */
	public KeyId {
		Objects.requireNonNull(value, "Value must not be null");
	}
	
	/**
	 * Computes the key id of the given public key.<br>
	 *
	 * @param key The key to identify
	 * @return The computed key id
	 * @throws NullPointerException If the key is null
	 * @throws CryptoException If the key does not expose an encoded form
	 */
	public static @NonNull KeyId of(@NonNull PublicKey key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		byte[] encoded = key.getEncoded();
		if (encoded == null) {
			throw new CryptoException("Cannot compute a key id for a " + key.getAlgorithm() + " key without an encoded form");
		}
		return of(encoded);
	}
	
	/**
	 * Computes the key id of the given encoded public key.<br>
	 *
	 * @param encodedKey The encoded key to identify
	 * @return The computed key id
	 * @throws NullPointerException If the encoded key is null
	 */
	public static @NonNull KeyId of(byte @NonNull [] encodedKey) {
		Objects.requireNonNull(encodedKey, "Encoded key must not be null");
		
		byte[] digest = Hashes.hash(HashAlgorithm.SHA_256, encodedKey);
		return new KeyId(UUIDs.v8(CryptoBytes.slice(digest, 0, 16)));
	}
	
	/**
	 * Reads a key id from its 16 byte representation.<br>
	 *
	 * @param data The 16 bytes to read
	 * @return The read key id
	 * @throws NullPointerException If the data is null
	 * @throws IllegalArgumentException If the data is not exactly 16 bytes long
	 * @see #toBytes()
	 */
	public static @NonNull KeyId fromBytes(byte @NonNull [] data) {
		return new KeyId(UUIDs.fromBytes(data));
	}
	
	/**
	 * Converts this key id into its 16 byte representation.<br>
	 *
	 * @return The 16 bytes of this key id
	 * @see #fromBytes(byte[])
	 */
	public byte @NonNull [] toBytes() {
		return UUIDs.toBytes(this.value);
	}
	
	@Override
	public String toString() {
		return this.value.toString();
	}
}
