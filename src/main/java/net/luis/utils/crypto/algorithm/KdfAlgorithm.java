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

package net.luis.utils.crypto.algorithm;

import org.jspecify.annotations.NonNull;

/**
 * The key derivation functions available in this library.<br>
 * <p>
 *     Each constant is an RFC 5869 HKDF over the named HMAC, served by the JDK key derivation API.<br>
 *     The mac and the hash are carried alongside the JCA name because they describe what the construction is built from, not because anything resolves through them.
 * </p>
 *
 * @author Luis-St
 */
public enum KdfAlgorithm {
	
	/**
	 * HKDF over HMAC-SHA-256, producing 32 bytes per expansion block.<br>
	 */
	HKDF_SHA_256("HKDF-SHA256", MacAlgorithm.HMAC_SHA_256, HashAlgorithm.SHA_256),
	/**
	 * HKDF over HMAC-SHA-384, producing 48 bytes per expansion block.<br>
	 */
	HKDF_SHA_384("HKDF-SHA384", MacAlgorithm.HMAC_SHA_384, HashAlgorithm.SHA_384),
	/**
	 * HKDF over HMAC-SHA-512, producing 64 bytes per expansion block.<br>
	 */
	HKDF_SHA_512("HKDF-SHA512", MacAlgorithm.HMAC_SHA_512, HashAlgorithm.SHA_512);
	
	/**
	 * The JCA name of the key derivation function serving this constant.<br>
	 */
	private final String jcaName;
	/**
	 * The mac this key derivation function is built on.<br>
	 */
	private final MacAlgorithm mac;
	/**
	 * The hash behind the mac of this key derivation function.<br>
	 */
	private final HashAlgorithm hash;
	
	/**
	 * Constructs a new kdf algorithm constant.<br>
	 *
	 * @param jcaName The JCA name of the serving key derivation function
	 * @param mac The mac to build on
	 * @param hash The hash behind the mac
	 */
	KdfAlgorithm(@NonNull String jcaName, @NonNull MacAlgorithm mac, @NonNull HashAlgorithm hash) {
		this.jcaName = jcaName;
		this.mac = mac;
		this.hash = hash;
	}
	
	/**
	 * Returns the JCA name of the key derivation function serving this constant.<br>
	 * @return The JCA name
	 */
	public @NonNull String jcaName() {
		return this.jcaName;
	}
	
	/**
	 * Returns the mac this key derivation function is built on.<br>
	 * @return The mac algorithm
	 */
	public @NonNull MacAlgorithm mac() {
		return this.mac;
	}
	
	/**
	 * Returns the hash behind the mac of this key derivation function.<br>
	 * @return The hash algorithm
	 */
	public @NonNull HashAlgorithm hash() {
		return this.hash;
	}
	
	/**
	 * Returns the length of one expansion block in bytes.<br>
	 * The largest output this function can produce is 255 times this value.<br>
	 *
	 * @return The output length of one block
	 */
	public int outputLength() {
		return this.hash.digestLength();
	}
}
