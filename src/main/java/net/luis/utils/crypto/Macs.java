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

package net.luis.utils.crypto;

import net.luis.utils.crypto.algorithm.MacAlgorithm;
import net.luis.utils.crypto.exception.AuthenticationException;
import net.luis.utils.crypto.exception.CryptoException;
import net.luis.utils.crypto.util.CryptoRandom;
import org.jspecify.annotations.NonNull;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.util.Objects;

/**
 * Message authentication codes.<br>
 * <p>
 *     A mac proves that data was produced by someone holding the key.<br>
 *     It is symmetric: everyone who can verify a tag can also create one, so it authenticates data but never a party to a third party.<br>
 *     Use {@link Signatures} when the verifier must not be able to forge.
 * </p>
 * <p>
 *     Example:
 * </p>
 * <pre>{@code
 * SecretKey key = Macs.generateKey(MacAlgorithm.HMAC_SHA_256);
 * byte[] tag = Macs.mac(MacAlgorithm.HMAC_SHA_256, key, data);
 *
 * if (Macs.verify(MacAlgorithm.HMAC_SHA_256, key, data, tag)) {
 *     accept(data);
 * }
 *
 * // Same check, but a mismatch throws instead of returning false
 * Macs.require(MacAlgorithm.HMAC_SHA_256, key, data, tag);
 * }</pre>
 *
 * @see MacAlgorithm
 *
 * @author Luis-St
 */
public final class Macs {
	
	/**
	 * The buffer size used when authenticating a stream.<br>
	 */
	private static final int BUFFER_SIZE = 8192;
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private Macs() {}
	
	/**
	 * Generates a new random key of the recommended length for the given algorithm.<br>
	 *
	 * @param algorithm The algorithm the key is for
	 * @return The generated key
	 * @throws NullPointerException If the algorithm is null
	 */
	public static @NonNull SecretKey generateKey(@NonNull MacAlgorithm algorithm) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		return new SecretKeySpec(CryptoRandom.bytes(algorithm.recommendedKeyLength()), algorithm.jcaName());
	}
	
	/**
	 * Wraps the given raw bytes as a key for the given algorithm.<br>
	 * <p>
	 *     HMAC accepts a key of any length, so no length is enforced here beyond rejecting an empty array.<br>
	 *     A key shorter than the algorithm's recommended length reduces its strength accordingly.
	 * </p>
	 *
	 * @param algorithm The algorithm the key is for
	 * @param raw The raw key bytes
	 * @return The wrapped key
	 * @throws NullPointerException If the algorithm or the raw bytes are null
	 * @throws CryptoException If the raw key is empty
	 */
	public static @NonNull SecretKey key(@NonNull MacAlgorithm algorithm, byte @NonNull [] raw) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(raw, "Raw key must not be null");
		if (raw.length == 0) {
			throw new CryptoException("Cannot create a " + algorithm + " key from an empty array");
		}
		
		return new SecretKeySpec(raw, algorithm.jcaName());
	}
	
	/**
	 * Computes the tag of the given data.<br>
	 *
	 * @param algorithm The algorithm to authenticate with
	 * @param key The key to authenticate with
	 * @param data The data to authenticate
	 * @return The computed tag
	 * @throws NullPointerException If the algorithm, the key or the data is null
	 * @throws CryptoException If the key is not usable for the algorithm
	 */
	public static byte @NonNull [] mac(@NonNull MacAlgorithm algorithm, @NonNull SecretKey key, byte @NonNull [] data) {
		Objects.requireNonNull(data, "Data must not be null");
		return init(algorithm, key).doFinal(data);
	}
	
	/**
	 * Computes the tag of the given data with a raw key.<br>
	 *
	 * @param algorithm The algorithm to authenticate with
	 * @param key The raw key to authenticate with
	 * @param data The data to authenticate
	 * @return The computed tag
	 * @throws NullPointerException If the algorithm, the key or the data is null
	 * @throws CryptoException If the key is empty or not usable for the algorithm
	 */
	public static byte @NonNull [] mac(@NonNull MacAlgorithm algorithm, byte @NonNull [] key, byte @NonNull [] data) {
		return mac(algorithm, key(algorithm, key), data);
	}
	
	/**
	 * Computes the tag over everything the given stream yields.<br>
	 * The stream is read to its end but not closed.<br>
	 *
	 * @param algorithm The algorithm to authenticate with
	 * @param key The key to authenticate with
	 * @param input The stream to read
	 * @return The computed tag
	 * @throws NullPointerException If the algorithm, the key or the input is null
	 * @throws CryptoException If the key is not usable for the algorithm
	 * @throws UncheckedIOException If reading the stream fails
	 */
	public static byte @NonNull [] mac(@NonNull MacAlgorithm algorithm, @NonNull SecretKey key, @NonNull InputStream input) {
		Objects.requireNonNull(input, "Input must not be null");
		
		Mac mac = init(algorithm, key);
		byte[] buffer = new byte[BUFFER_SIZE];
		try {
			int read;
			while ((read = input.read(buffer)) != -1) {
				mac.update(buffer, 0, read);
			}
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to read the stream to authenticate", e);
		}
		return mac.doFinal();
	}
	
	/**
	 * Checks whether the given data authenticates to the given tag.<br>
	 * The comparison is constant-time, so this is safe to use on a tag that arrived from outside.<br>
	 *
	 * @param algorithm The algorithm to authenticate with
	 * @param key The key to authenticate with
	 * @param data The data to authenticate
	 * @param expectedTag The tag to compare against
	 * @return True if the data authenticates to the expected tag
	 * @throws NullPointerException If the algorithm, the key or the data is null
	 * @throws CryptoException If the key is not usable for the algorithm
	 */
	public static boolean verify(@NonNull MacAlgorithm algorithm, @NonNull SecretKey key, byte @NonNull [] data, byte @NonNull [] expectedTag) {
		return MessageDigest.isEqual(mac(algorithm, key, data), expectedTag);
	}
	
	/**
	 * Requires that the given data authenticates to the given tag.<br>
	 *
	 * @param algorithm The algorithm to authenticate with
	 * @param key The key to authenticate with
	 * @param data The data to authenticate
	 * @param expectedTag The tag to compare against
	 * @throws NullPointerException If the algorithm, the key or the data is null
	 * @throws CryptoException If the key is not usable for the algorithm
	 * @throws AuthenticationException If the data does not authenticate to the expected tag
	 */
	public static void require(@NonNull MacAlgorithm algorithm, @NonNull SecretKey key, byte @NonNull [] data, byte @NonNull [] expectedTag) {
		if (!verify(algorithm, key, data, expectedTag)) {
			throw new AuthenticationException("MAC verification failed for " + algorithm);
		}
	}
	
	/**
	 * Creates and initializes a mac for the given algorithm and key.<br>
	 *
	 * @param algorithm The algorithm to authenticate with
	 * @param key The key to authenticate with
	 * @return The initialized mac
	 * @throws NullPointerException If the algorithm or the key is null
	 * @throws CryptoException If the key is not usable for the algorithm
	 */
	static @NonNull Mac init(@NonNull MacAlgorithm algorithm, @NonNull SecretKey key) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			Mac mac = algorithm.mac();
			mac.init(key);
			return mac;
		} catch (InvalidKeyException e) {
			throw new CryptoException("Invalid key for " + algorithm, e);
		}
	}
}
