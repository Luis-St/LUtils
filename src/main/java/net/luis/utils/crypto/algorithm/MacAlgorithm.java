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

import net.luis.utils.crypto.exception.UnsupportedAlgorithmException;
import org.jspecify.annotations.NonNull;

import javax.crypto.Mac;
import java.security.NoSuchAlgorithmException;

/**
 * The message authentication codes available in this library.<br>
 * <p>
 *     Only HMAC constructions are modeled.<br>
 *     KMAC is absent because it needs a parameter spec for its output length and customization string,<br>
 *     and a constant that carries a tag length the implementation never passes on would be metadata that misleads.
 * </p>
 *
 * @author Luis-St
 */
public enum MacAlgorithm {
	
	/**
	 * HMAC over SHA-256, with a 32 byte tag.<br>
	 */
	HMAC_SHA_256("HmacSHA256", 32, 32),
	/**
	 * HMAC over SHA-384, with a 48 byte tag.<br>
	 */
	HMAC_SHA_384("HmacSHA384", 48, 48),
	/**
	 * HMAC over SHA-512, with a 64 byte tag.<br>
	 */
	HMAC_SHA_512("HmacSHA512", 64, 64),
	/**
	 * HMAC over SHA3-256, with a 32 byte tag.<br>
	 */
	HMAC_SHA3_256("HmacSHA3-256", 32, 32),
	/**
	 * HMAC over SHA3-384, with a 48 byte tag.<br>
	 */
	HMAC_SHA3_384("HmacSHA3-384", 48, 48),
	/**
	 * HMAC over SHA3-512, with a 64 byte tag.<br>
	 */
	HMAC_SHA3_512("HmacSHA3-512", 64, 64);
	
	/**
	 * The JCA name of this algorithm.<br>
	 */
	private final String jcaName;
	/**
	 * The length of a tag of this algorithm in bytes.<br>
	 */
	private final int tagLength;
	/**
	 * The recommended key length for this algorithm in bytes.<br>
	 */
	private final int recommendedKeyLength;
	
	/**
	 * Constructs a new mac algorithm constant.<br>
	 *
	 * @param jcaName The JCA name of the algorithm
	 * @param tagLength The length of a tag in bytes
	 * @param recommendedKeyLength The recommended key length in bytes
	 */
	MacAlgorithm(@NonNull String jcaName, int tagLength, int recommendedKeyLength) {
		this.jcaName = jcaName;
		this.tagLength = tagLength;
		this.recommendedKeyLength = recommendedKeyLength;
	}
	
	/**
	 * Returns the JCA name of this algorithm.<br>
	 * @return The JCA name
	 */
	public @NonNull String jcaName() {
		return this.jcaName;
	}
	
	/**
	 * Returns the length of a tag of this algorithm in bytes.<br>
	 * @return The tag length
	 */
	public int tagLength() {
		return this.tagLength;
	}
	
	/**
	 * Returns the recommended key length for this algorithm in bytes.<br>
	 * HMAC accepts keys of any length.<br>
	 * This is the length at which it reaches its full strength.<br>
	 *
	 * @return The recommended key length
	 */
	public int recommendedKeyLength() {
		return this.recommendedKeyLength;
	}
	
	/**
	 * Creates a new mac for this algorithm.<br>
	 * The returned instance is stateful, uninitialised and belongs to the caller.<br>
	 *
	 * @return The created mac
	 * @throws UnsupportedAlgorithmException If no registered provider serves this algorithm
	 */
	public @NonNull Mac mac() {
		try {
			return Mac.getInstance(this.jcaName);
		} catch (NoSuchAlgorithmException e) {
			throw UnsupportedAlgorithmException.forAlgorithm(this.jcaName, e);
		}
	}
}
