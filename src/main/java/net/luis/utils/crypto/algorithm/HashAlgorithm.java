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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The cryptographic hash functions available in this library.<br>
 * <p>
 *     {@link #SHA_1} and {@link #MD5} are broken and are listed last,<br>
 *     so that everything reached before them is safe to pick.<br>
 *     They exist to read data that already exists, never to write anything new.
 * </p>
 *
 * @author Luis-St
 */
public enum HashAlgorithm {
	
	/**
	 * SHA-256, the general purpose default with a 32 byte digest.<br>
	 */
	SHA_256("SHA-256", 32, 64),
	/**
	 * SHA-384, a truncated SHA-512 with a 48 byte digest.<br>
	 */
	SHA_384("SHA-384", 48, 128),
	/**
	 * SHA-512, with a 64 byte digest and a faster software implementation than SHA-256 on 64-bit hardware.<br>
	 */
	SHA_512("SHA-512", 64, 128),
	/**
	 * SHA-512/256, a truncated SHA-512 with a 32 byte digest and no length extension weakness.<br>
	 */
	SHA_512_256("SHA-512/256", 32, 128),
	/**
	 * SHA3-256, the Keccak based alternative to SHA-256.<br>
	 */
	SHA3_256("SHA3-256", 32, 136),
	/**
	 * SHA3-384, the Keccak based alternative to SHA-384.<br>
	 */
	SHA3_384("SHA3-384", 48, 104),
	/**
	 * SHA3-512, the Keccak based alternative to SHA-512.<br>
	 */
	SHA3_512("SHA3-512", 64, 72),
	/**
	 * SHA-1, with a 20 byte digest.<br>
	 * <p>
	 *     Broken and unfit for any security purpose.<br>
	 *     Chosen-prefix collisions are practical on ordinary hardware, so a matching digest is no evidence that two inputs are the same.<br>
	 *     Never use it for signatures, certificates, key fingerprints, or anything else that rests on collision resistance.
	 * </p>
	 */
	SHA_1("SHA-1", 20, 64),
	/**
	 * MD5, with a 16 byte digest.<br>
	 * <p>
	 *     Broken and unfit for any security purpose.<br>
	 *     Collisions are produced in seconds and its preimage resistance is weakened as well, which leaves it weaker than {@link #SHA_1} on every count.
	 * </p>
	 */
	MD5("MD5", 16, 64);
	
	/**
	 * The JCA name of this algorithm.<br>
	 */
	private final String jcaName;
	/**
	 * The length of a digest of this algorithm in bytes.<br>
	 */
	private final int digestLength;
	/**
	 * The internal block length of this algorithm in bytes.<br>
	 */
	private final int blockLength;
	
	/**
	 * Constructs a new hash algorithm constant.<br>
	 *
	 * @param jcaName The JCA name of the algorithm
	 * @param digestLength The length of a digest in bytes
	 * @param blockLength The internal block length in bytes
	 */
	HashAlgorithm(@NonNull String jcaName, int digestLength, int blockLength) {
		this.jcaName = jcaName;
		this.digestLength = digestLength;
		this.blockLength = blockLength;
	}
	
	/**
	 * Returns the JCA name of this algorithm.<br>
	 * @return The JCA name
	 */
	public @NonNull String jcaName() {
		return this.jcaName;
	}
	
	/**
	 * Returns the length of a digest of this algorithm in bytes.<br>
	 * @return The digest length
	 */
	public int digestLength() {
		return this.digestLength;
	}
	
	/**
	 * Returns the internal block length of this algorithm in bytes.<br>
	 * For the SHA-3 family this is the rate rather than a classical block size.<br>
	 *
	 * @return The block length
	 */
	public int blockLength() {
		return this.blockLength;
	}
	
	/**
	 * Creates a new message digest for this algorithm.<br>
	 * The returned instance is stateful and belongs to the caller.<br>
	 *
	 * @return The created message digest
	 * @throws UnsupportedAlgorithmException If no registered provider serves this algorithm
	 */
	public @NonNull MessageDigest digest() {
		try {
			return MessageDigest.getInstance(this.jcaName);
		} catch (NoSuchAlgorithmException e) {
			throw UnsupportedAlgorithmException.forAlgorithm(this.jcaName, e);
		}
	}
}
