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

import net.luis.utils.crypto.algorithm.NativeSignatureAlgorithm;
import net.luis.utils.crypto.exception.CryptoException;
import net.luis.utils.resources.ResourceLocation;
import org.jspecify.annotations.NonNull;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Objects;

/**
 * An incremental signing operation.<br>
 * <p>
 *     The message is fed in through the update methods and the signature produced by {@link #sign()},<br>
 *     which also resets the signer for the next message.
 * </p>
 * <p>
 *     Instances are stateful and not thread-safe.
 * </p>
 * <p>
 *     Example:
 * </p>
 * <pre>{@code
 * // The message is fed in piece by piece, so nothing has to be concatenated first
 * Signer signer = Signatures.signer(SignatureAlgorithm.ED25519, pair.getPrivate());
 * byte[] signature = signer.update(header)
 *     .update(body, 0, length)
 *     .update(Path.of("payload.bin"))
 *     .sign();
 * }</pre>
 *
 * @see Signatures
 * @see Verifier
 *
 * @author Luis-St
 */
public final class Signer {
	
	/**
	 * The buffer size used when signing a stream.<br>
	 */
	private static final int BUFFER_SIZE = 8192;
	
	/**
	 * The scheme this signer signs with, held for error messages.<br>
	 */
	private final NativeSignatureAlgorithm algorithm;
	/**
	 * The signature backing this signer.<br>
	 */
	private final Signature signature;
	
	/**
	 * Constructs a new signer over the given signature.<br>
	 *
	 * @param algorithm The scheme being signed with
	 * @param signature The initialized signature to use
	 * @throws NullPointerException If the algorithm or the signature is null
	 */
	Signer(@NonNull NativeSignatureAlgorithm algorithm, @NonNull Signature signature) {
		this.algorithm = Objects.requireNonNull(algorithm, "Algorithm must not be null");
		this.signature = Objects.requireNonNull(signature, "Signature must not be null");
	}
	
	/**
	 * Updates this signer with the given bytes.<br>
	 *
	 * @param data The bytes to add
	 * @return This signer
	 * @throws NullPointerException If the data is null
	 * @throws CryptoException If the update fails
	 */
	public @NonNull Signer update(byte @NonNull [] data) {
		Objects.requireNonNull(data, "Data must not be null");
		
		try {
			this.signature.update(data);
			return this;
		} catch (SignatureException e) {
			throw new CryptoException("Cannot update the signer for " + this.algorithm.name(), e);
		}
	}
	
	/**
	 * Updates this signer with a section of the given bytes.<br>
	 *
	 * @param data The bytes to add from
	 * @param offset The index of the first byte to add
	 * @param length The number of bytes to add
	 * @return This signer
	 * @throws NullPointerException If the data is null
	 * @throws IndexOutOfBoundsException If the section is not fully inside the array
	 * @throws CryptoException If the update fails
	 */
	public @NonNull Signer update(byte @NonNull [] data, int offset, int length) {
		Objects.requireNonNull(data, "Data must not be null");
		Objects.checkFromIndexSize(offset, length, data.length);
		
		try {
			this.signature.update(data, offset, length);
			return this;
		} catch (SignatureException e) {
			throw new CryptoException("Cannot update the signer for " + this.algorithm.name(), e);
		}
	}
	
	/**
	 * Updates this signer with the remaining bytes of the given buffer.<br>
	 *
	 * @param buffer The buffer to add
	 * @return This signer
	 * @throws NullPointerException If the buffer is null
	 * @throws CryptoException If the update fails
	 */
	public @NonNull Signer update(@NonNull ByteBuffer buffer) {
		Objects.requireNonNull(buffer, "Buffer must not be null");
		
		try {
			this.signature.update(buffer);
			return this;
		} catch (SignatureException e) {
			throw new CryptoException("Cannot update the signer for " + this.algorithm.name(), e);
		}
	}
	
	/**
	 * Updates this signer with the given string, encoded with the given charset.<br>
	 *
	 * @param data The string to add
	 * @param charset The charset to encode the string with
	 * @return This signer
	 * @throws NullPointerException If the data or the charset is null
	 * @throws CryptoException If the update fails
	 */
	public @NonNull Signer update(@NonNull String data, @NonNull Charset charset) {
		Objects.requireNonNull(data, "Data must not be null");
		Objects.requireNonNull(charset, "Charset must not be null");
		
		return this.update(data.getBytes(charset));
	}
	
	/**
	 * Updates this signer with everything the given stream yields.<br>
	 * The stream is read to its end but not closed.<br>
	 *
	 * @param input The stream to read
	 * @return This signer
	 * @throws NullPointerException If the input is null
	 * @throws UncheckedIOException If reading the stream fails
	 * @throws CryptoException If the update fails
	 */
	public @NonNull Signer update(@NonNull InputStream input) {
		Objects.requireNonNull(input, "Input must not be null");
		
		byte[] buffer = new byte[BUFFER_SIZE];
		try {
			int read;
			while ((read = input.read(buffer)) != -1) {
				this.update(buffer, 0, read);
			}
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to read the stream to sign", e);
		}
		return this;
	}
	
	/**
	 * Updates this signer with the contents of the given file.<br>
	 *
	 * @param file The file to read
	 * @return This signer
	 * @throws NullPointerException If the file is null
	 * @throws UncheckedIOException If reading the file fails
	 * @throws CryptoException If the update fails
	 */
	public @NonNull Signer update(@NonNull Path file) {
		Objects.requireNonNull(file, "File must not be null");
		
		try (InputStream input = Files.newInputStream(file)) {
			return this.update(input);
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to read the file to sign: " + file, e);
		}
	}
	
	/**
	 * Updates this signer with the contents of the given resource.<br>
	 * The resource may live on the classpath or on the filesystem.<br>
	 *
	 * @param resource The resource to read
	 * @return This signer
	 * @throws NullPointerException If the resource is null
	 * @throws UncheckedIOException If reading the resource fails
	 * @throws CryptoException If the update fails
	 */
	public @NonNull Signer update(@NonNull ResourceLocation resource) {
		Objects.requireNonNull(resource, "Resource must not be null");
		
		try (InputStream input = resource.getStream()) {
			return this.update(input);
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to read the resource to sign: " + resource, e);
		}
	}
	
	/**
	 * Completes the operation and returns the signature.<br>
	 * The signer is reset afterwards and can be used for a new message.<br>
	 *
	 * @return The produced signature
	 * @throws CryptoException If the signing fails
	 */
	public byte @NonNull [] sign() {
		try {
			return this.signature.sign();
		} catch (SignatureException e) {
			throw new CryptoException("Signing failed for " + this.algorithm.name(), e);
		}
	}
}
