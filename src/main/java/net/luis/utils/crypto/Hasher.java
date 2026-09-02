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
import net.luis.utils.crypto.util.CryptoBytes;
import net.luis.utils.util.UUIDs;
import org.jspecify.annotations.NonNull;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.*;

/**
 * An incremental hash computation.<br>
 * <p>
 *     Every update method returns this hasher, so a computation over several values reads as a chain.<br>
 *     The digest is only produced when {@link #digest()} is called, which also resets the hasher for the next computation.
 * </p>
 * <p>
 *     Instances are stateful and not thread-safe.
 * </p>
 * <p>
 *     Example:
 * </p>
 * <pre>{@code
 * // Every update returns this hasher, so a digest over several values reads as a chain
 * byte[] digest = Hashes.hasher(HashAlgorithm.SHA_256)
 *     .update(messageId)
 *     .update("payload", StandardCharsets.UTF_8)
 *     .update(Path.of("archive.zip"))
 *     .digest();
 *
 * // The digest call reset the hasher, so the next computation starts clean
 * String hex = Hashes.hasher(HashAlgorithm.SHA_256).update(data).digestHex();
 * }</pre>
 *
 * @see Hashes
 *
 * @author Luis-St
 */
public final class Hasher {
	
	/**
	 * The buffer size used when hashing a stream.<br>
	 */
	private static final int BUFFER_SIZE = 8192;
	
	/**
	 * The message digest backing this hasher.<br>
	 */
	private final MessageDigest digest;
	
	/**
	 * Constructs a new hasher over the given message digest.<br>
	 *
	 * @param digest The message digest to use
	 * @throws NullPointerException If the digest is null
	 */
	Hasher(@NonNull MessageDigest digest) {
		this.digest = Objects.requireNonNull(digest, "Digest must not be null");
	}
	
	/**
	 * Creates a new hasher for the given algorithm.<br>
	 *
	 * @param algorithm The algorithm to hash with
	 * @return The created hasher
	 * @throws NullPointerException If the algorithm is null
	 * @throws UnsupportedAlgorithmException If no registered provider serves the algorithm
	 */
	public static @NonNull Hasher of(@NonNull HashAlgorithm algorithm) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		return new Hasher(algorithm.digest());
	}
	
	/**
	 * Updates this hasher with a single byte.<br>
	 *
	 * @param value The byte to add
	 * @return This hasher
	 */
	public @NonNull Hasher update(byte value) {
		this.digest.update(value);
		return this;
	}
	
	/**
	 * Updates this hasher with the given bytes.<br>
	 *
	 * @param data The bytes to add
	 * @return This hasher
	 * @throws NullPointerException If the data is null
	 */
	public @NonNull Hasher update(byte @NonNull [] data) {
		Objects.requireNonNull(data, "Data must not be null");
		this.digest.update(data);
		return this;
	}
	
	/**
	 * Updates this hasher with a section of the given bytes.<br>
	 *
	 * @param data The bytes to add from
	 * @param offset The index of the first byte to add
	 * @param length The number of bytes to add
	 * @return This hasher
	 * @throws NullPointerException If the data is null
	 * @throws IndexOutOfBoundsException If the section is not fully inside the array
	 */
	public @NonNull Hasher update(byte @NonNull [] data, int offset, int length) {
		Objects.requireNonNull(data, "Data must not be null");
		Objects.checkFromIndexSize(offset, length, data.length);
		
		this.digest.update(data, offset, length);
		return this;
	}
	
	/**
	 * Updates this hasher with the remaining bytes of the given buffer.<br>
	 *
	 * @param buffer The buffer to add
	 * @return This hasher
	 * @throws NullPointerException If the buffer is null
	 */
	public @NonNull Hasher update(@NonNull ByteBuffer buffer) {
		Objects.requireNonNull(buffer, "Buffer must not be null");
		
		this.digest.update(buffer);
		return this;
	}
	
	/**
	 * Updates this hasher with the given string, encoded with the given charset.<br>
	 *
	 * @param data The string to add
	 * @param charset The charset to encode the string with
	 * @return This hasher
	 * @throws NullPointerException If the data or the charset is null
	 */
	public @NonNull Hasher update(@NonNull String data, @NonNull Charset charset) {
		Objects.requireNonNull(data, "Data must not be null");
		Objects.requireNonNull(charset, "Charset must not be null");
		
		this.digest.update(data.getBytes(charset));
		return this;
	}
	
	/**
	 * Updates this hasher with the eight big-endian bytes of the given value.<br>
	 *
	 * @param value The value to add
	 * @return This hasher
	 */
	public @NonNull Hasher update(long value) {
		this.digest.update(CryptoBytes.of(value));
		return this;
	}
	
	/**
	 * Updates this hasher with the sixteen big-endian bytes of the given uuid.<br>
	 *
	 * @param uuid The uuid to add
	 * @return This hasher
	 * @throws NullPointerException If the uuid is null
	 */
	public @NonNull Hasher update(@NonNull UUID uuid) {
		Objects.requireNonNull(uuid, "UUID must not be null");
		
		this.digest.update(UUIDs.toBytes(uuid));
		return this;
	}
	
	/**
	 * Updates this hasher with everything the given stream yields.<br>
	 * The stream is read to its end but not closed.<br>
	 *
	 * @param input The stream to read
	 * @return This hasher
	 * @throws NullPointerException If the input is null
	 * @throws UncheckedIOException If reading the stream fails
	 */
	public @NonNull Hasher update(@NonNull InputStream input) {
		Objects.requireNonNull(input, "Input must not be null");
		
		byte[] buffer = new byte[BUFFER_SIZE];
		try {
			int read;
			while ((read = input.read(buffer)) != -1) {
				this.digest.update(buffer, 0, read);
			}
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to read the stream to hash", e);
		}
		return this;
	}
	
	/**
	 * Updates this hasher with the contents of the given file.<br>
	 *
	 * @param file The file to read
	 * @return This hasher
	 * @throws NullPointerException If the file is null
	 * @throws UncheckedIOException If reading the file fails
	 */
	public @NonNull Hasher update(@NonNull Path file) {
		Objects.requireNonNull(file, "File must not be null");
		
		try (InputStream input = Files.newInputStream(file)) {
			return this.update(input);
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to read the file to hash: " + file, e);
		}
	}
	
	/**
	 * Completes the computation and returns the digest.<br>
	 * The hasher is reset afterwards and can be used for a new computation.<br>
	 *
	 * @return The computed digest
	 */
	public byte @NonNull [] digest() {
		return this.digest.digest();
	}
	
	/**
	 * Completes the computation and returns the digest as a lowercase hex string.<br>
	 * The hasher is reset afterwards and can be used for a new computation.<br>
	 *
	 * @return The computed digest as hex
	 */
	public @NonNull String digestHex() {
		return HexFormat.of().formatHex(this.digest());
	}
	
	/**
	 * Discards everything added so far and starts a new computation.<br>
	 */
	public void reset() {
		this.digest.reset();
	}
}
