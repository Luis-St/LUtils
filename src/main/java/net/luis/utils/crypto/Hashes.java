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

import net.luis.utils.crypto.algorithm.HashAlgorithm;
import net.luis.utils.crypto.exception.UnsupportedAlgorithmException;
import org.jspecify.annotations.NonNull;

import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Objects;

/**
 * One-shot hashing.<br>
 * <p>
 *     Use {@link Hasher} instead when a digest is computed over several values that are not already in one array.
 * </p>
 * <p>
 *     Example:
 * </p>
 * <pre>{@code
 * byte[] digest = Hashes.hash(HashAlgorithm.SHA_256, data);
 * String hex = Hashes.hashHex(HashAlgorithm.SHA_256, data);
 * byte[] ofFile = Hashes.hash(HashAlgorithm.SHA_256, Path.of("archive.zip"));
 *
 * // Constant-time, so it does not leak how much of the digest matched
 * if (Hashes.matches(HashAlgorithm.SHA_256, data, expected)) {
 *     accept(data);
 * }
 * }</pre>
 *
 * @see Hasher
 * @see HashAlgorithm
 *
 * @author Luis-St
 */
public final class Hashes {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private Hashes() {}
	
	/**
	 * Hashes the given bytes.<br>
	 *
	 * @param algorithm The algorithm to hash with
	 * @param data The bytes to hash
	 * @return The computed digest
	 * @throws NullPointerException If the algorithm or the data is null
	 * @throws UnsupportedAlgorithmException If no registered provider serves the algorithm
	 */
	public static byte @NonNull [] hash(@NonNull HashAlgorithm algorithm, byte @NonNull [] data) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(data, "Data must not be null");
		
		return algorithm.digest().digest(data);
	}
	
	/**
	 * Hashes the given string, encoded with the given charset.<br>
	 *
	 * @param algorithm The algorithm to hash with
	 * @param data The string to hash
	 * @param charset The charset to encode the string with
	 * @return The computed digest
	 * @throws NullPointerException If the algorithm, the data or the charset is null
	 * @throws net.luis.utils.crypto.exception.UnsupportedAlgorithmException If no registered provider serves the algorithm
	 */
	public static byte @NonNull [] hash(@NonNull HashAlgorithm algorithm, @NonNull String data, @NonNull Charset charset) {
		Objects.requireNonNull(data, "Data must not be null");
		Objects.requireNonNull(charset, "Charset must not be null");
		
		return hash(algorithm, data.getBytes(charset));
	}
	
	/**
	 * Hashes everything the given stream yields.<br>
	 * The stream is read to its end but not closed.<br>
	 *
	 * @param algorithm The algorithm to hash with
	 * @param input The stream to read
	 * @return The computed digest
	 * @throws NullPointerException If the algorithm or the input is null
	 * @throws UncheckedIOException If reading the stream fails
	 */
	public static byte @NonNull [] hash(@NonNull HashAlgorithm algorithm, @NonNull InputStream input) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(input, "Input stream must not be null");
		
		return Hasher.of(algorithm).update(input).digest();
	}
	
	/**
	 * Hashes the contents of the given file.<br>
	 *
	 * @param algorithm The algorithm to hash with
	 * @param file The file to read
	 * @return The computed digest
	 * @throws NullPointerException If the algorithm or the file is null
	 * @throws UncheckedIOException If reading the file fails
	 */
	public static byte @NonNull [] hash(@NonNull HashAlgorithm algorithm, @NonNull Path file) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(file, "File must not be null");
		
		return Hasher.of(algorithm).update(file).digest();
	}
	
	/**
	 * Hashes the given bytes and returns the digest as a lowercase hex string.<br>
	 *
	 * @param algorithm The algorithm to hash with
	 * @param data The bytes to hash
	 * @return The computed digest as hex
	 * @throws NullPointerException If the algorithm or the data is null
	 * @throws UnsupportedAlgorithmException If no registered provider serves the algorithm
	 */
	public static @NonNull String hashHex(@NonNull HashAlgorithm algorithm, byte @NonNull [] data) {
		return HexFormat.of().formatHex(hash(algorithm, data));
	}
	
	/**
	 * Checks whether the given bytes hash to the given digest.<br>
	 * The comparison is constant-time, so this is safe to use on a digest that arrived from outside.<br>
	 *
	 * @param algorithm The algorithm to hash with
	 * @param data The bytes to hash
	 * @param expected The digest to compare against
	 * @return True if the data hashes to the expected digest
	 * @throws NullPointerException If the algorithm, the data or the expected digest is null
	 * @throws UnsupportedAlgorithmException If no registered provider serves the algorithm
	 */
	public static boolean matches(@NonNull HashAlgorithm algorithm, byte @NonNull [] data, byte @NonNull [] expected) {
		Objects.requireNonNull(expected, "Expected digest must not be null");
		return MessageDigest.isEqual(hash(algorithm, data), expected);
	}
	
	/**
	 * Creates a new incremental hasher for the given algorithm.<br>
	 *
	 * @param algorithm The algorithm to hash with
	 * @return The created hasher
	 * @throws NullPointerException If the algorithm is null
	 * @throws UnsupportedAlgorithmException If no registered provider serves the algorithm
	 */
	public static @NonNull Hasher hasher(@NonNull HashAlgorithm algorithm) {
		return Hasher.of(algorithm);
	}
}
