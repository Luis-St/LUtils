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

import net.luis.utils.crypto.exception.AuthenticationException;
import net.luis.utils.crypto.exception.MalformedDataException;
import net.luis.utils.crypto.key.Secret;
import net.luis.utils.crypto.util.CryptoBytes;
import net.luis.utils.crypto.util.CryptoRandom;
import net.luis.utils.util.UUIDs;
import org.jspecify.annotations.NonNull;

import javax.crypto.SecretKey;
import java.io.*;
import java.nio.ByteBuffer;
import java.security.*;
import java.util.Objects;
import java.util.UUID;

/**
 * Public-key encryption of a payload too large to hold in memory.<br>
 * <p>
 *     The payload is framed into chunks with an individually authenticated tag per chunk,<br>
 *     so nothing is written out before it has been authenticated.<br>
 *     The nonce of a chunk is {@code noncePrefix(8) || counter(4)}.
 * </p>
 * <p>
 *     Each frame carries an explicit kind byte which is bound into the associated data.<br>
 *     A truncated stream is therefore detected: the last chunk actually read is marked as "more" and the input then ends,<br>
 *     which ends the loop with a failure rather than with a plausible looking result.
 * </p>
 * <p>
 *     The header is the same as the one {@link Sealed} writes, followed by the nonce prefix and the chunks.
 * </p>
 * <p>
 *     Example:
 * </p>
 * <pre>{@code
 * // Neither stream is closed, and nothing is held in memory beyond one chunk
 * try (InputStream in = Files.newInputStream(source); OutputStream out = Files.newOutputStream(target)) {
 *     SealedStream.seal(CryptoSuite.current(), recipient.getPublic(), in, out);
 * }
 *
 * try (InputStream in = Files.newInputStream(target); OutputStream out = Files.newOutputStream(restored)) {
 *     SealedStream.unseal(recipient.getPrivate(), in, out);
 * }
 * }</pre>
 *
 * @see Sealed
 *
 * @author Luis-St
 */
public final class SealedStream {
	
	/**
	 * The number of plaintext bytes per chunk.<br>
	 */
	private static final int CHUNK_SIZE = 64 * 1024;
	/**
	 * The kind byte marking a chunk that is followed by more chunks.<br>
	 */
	private static final byte CHUNK_MORE = 0x00;
	/**
	 * The kind byte marking the last chunk of a stream.<br>
	 */
	private static final byte CHUNK_FINAL = 0x01;
	/**
	 * The length of the random nonce prefix in bytes.<br>
	 */
	private static final int NONCE_PREFIX_LENGTH = 8;
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private SealedStream() {}
	
	/**
	 * Encrypts everything the given input yields to the given output.<br>
	 * Neither stream is closed.<br>
	 * The output is flushed.<br>
	 *
	 * @param suite The suite to encrypt with
	 * @param recipient The public key to encrypt to
	 * @param in The stream to read the payload from
	 * @param out The stream to write the sealed payload to
	 * @throws NullPointerException If the suite, the recipient, the input or the output is null
	 * @throws UncheckedIOException If reading or writing fails
	 */
	public static void seal(@NonNull CryptoSuite suite, @NonNull PublicKey recipient, @NonNull InputStream in, @NonNull OutputStream out) {
		seal(CryptoRandom.instance(), suite, recipient, in, out);
	}
	
	/**
	 * Encrypts everything the given input yields, drawing the nonce prefix from the given source.<br>
	 * This overload exists so the wire format can be tested against known answers with a fixed source.<br>
	 *
	 * @param random The source to draw the nonce prefix from
	 * @param suite The suite to encrypt with
	 * @param recipient The public key to encrypt to
	 * @param in The stream to read the payload from
	 * @param out The stream to write the sealed payload to
	 * @throws NullPointerException If the random source, the suite, the recipient, the input or the output is null
	 * @throws UncheckedIOException If reading or writing fails
	 */
	public static void seal(@NonNull SecureRandom random, @NonNull CryptoSuite suite, @NonNull PublicKey recipient, @NonNull InputStream in, @NonNull OutputStream out) {
		Objects.requireNonNull(random, "Random must not be null");
		Objects.requireNonNull(suite, "Suite must not be null");
		Objects.requireNonNull(recipient, "Recipient must not be null");
		Objects.requireNonNull(in, "Input must not be null");
		Objects.requireNonNull(out, "Output must not be null");
		
		try {
			writeSealed(random, suite, recipient, in, out);
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to write the sealed stream", e);
		}
	}
	
	/**
	 * Decrypts a sealed stream to the given output.<br>
	 * Neither stream is closed.<br>
	 * The output is flushed.<br>
	 *
	 * @param recipient The private key to decrypt with
	 * @param in The stream to read the sealed payload from
	 * @param out The stream to write the recovered payload to
	 * @throws NullPointerException If the recipient, the input or the output is null
	 * @throws MalformedDataException If the stream is not readable, truncated or framed wrongly
	 * @throws AuthenticationException If a chunk does not authenticate under this key
	 * @throws UncheckedIOException If reading or writing fails
	 */
	public static void unseal(@NonNull PrivateKey recipient, @NonNull InputStream in, @NonNull OutputStream out) {
		Objects.requireNonNull(recipient, "Recipient must not be null");
		Objects.requireNonNull(in, "Input must not be null");
		Objects.requireNonNull(out, "Output must not be null");
		
		try {
			readSealed(recipient, in, out);
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to read the sealed stream", e);
		}
	}
	
	/**
	 * Seals a stream, without the null checks and without wrapping the I/O failure.<br>
	 * Split out so the public entry point stays readable while the whole body is guarded in one place.<br>
	 *
	 * @param random The source to draw the nonce prefix from
	 * @param suite The suite to encrypt with
	 * @param recipient The public key to encrypt to
	 * @param in The stream to read the payload from
	 * @param out The stream to write the sealed payload to
	 * @throws IOException If reading or writing fails
	 */
	private static void writeSealed(@NonNull SecureRandom random, @NonNull CryptoSuite suite, @NonNull PublicKey recipient, @NonNull InputStream in, @NonNull OutputStream out) throws IOException {
		try (Kems.Encapsulation encapsulated = Kems.encapsulate(suite.kem(), recipient)) {
			UUID messageId = UUIDs.v7();
			try (Secret material = Sealed.deriveMaterial(suite, encapsulated.sharedSecret(), messageId, encapsulated.encapsulation())) {
				SecretKey key = Aead.key(suite.aead(), CryptoBytes.slice(material.material(), 0, suite.aead().keyLength()));
				byte[] commitment = CryptoBytes.slice(material.material(), suite.aead().keyLength(), Sealed.COMMITMENT_LENGTH);
				byte[] prologue = CryptoBytes.concat(Sealed.header(suite, messageId, commitment), encapsulated.encapsulation());
				byte[] noncePrefix = CryptoRandom.bytes(random, NONCE_PREFIX_LENGTH);
				
				out.write(prologue);
				out.write(noncePrefix);
				
				byte[] current = new byte[CHUNK_SIZE];
				byte[] next = new byte[CHUNK_SIZE];
				try {
					int read = in.readNBytes(current, 0, CHUNK_SIZE);
					int counter = 0;
					while (true) {
						int following = in.readNBytes(next, 0, CHUNK_SIZE);
						byte kind = following == 0 ? CHUNK_FINAL : CHUNK_MORE;
						byte[] plaintext = CryptoBytes.slice(current, 0, read);
						
						byte[] ciphertext;
						try {
							ciphertext = Aead.encrypt(suite.aead(), key, nonce(noncePrefix, counter), plaintext, aad(prologue, counter, kind));
						} finally {
							CryptoBytes.wipe(plaintext);
						}
						
						out.write(kind);
						out.write(CryptoBytes.of(ciphertext.length));
						out.write(ciphertext);
						if (kind == CHUNK_FINAL) {
							break;
						}
						
						byte[] swap = current;
						current = next;
						next = swap;
						read = following;
						counter = increment(counter);
					}
				} finally {
					CryptoBytes.wipe(current);
					CryptoBytes.wipe(next);
				}
				out.flush();
			}
		}
	}
	
	/**
	 * Unseals a stream, without the null checks and without wrapping the I/O failure.<br>
	 * Split out so the public entry point stays readable while the whole body is guarded in one place.<br>
	 *
	 * @param recipient The private key to decrypt with
	 * @param in The stream to read the sealed payload from
	 * @param out The stream to write the recovered payload to
	 * @throws MalformedDataException If the stream is not readable, truncated or framed wrongly
	 * @throws AuthenticationException If a chunk does not authenticate under this key
	 * @throws IOException If reading or writing fails
	 */
	private static void readSealed(@NonNull PrivateKey recipient, @NonNull InputStream in, @NonNull OutputStream out) throws IOException {
		byte[] head = in.readNBytes(Sealed.HEADER_LENGTH);
		Sealed.Head parsed = Sealed.Head.parse(head);
		CryptoSuite suite = parsed.suite();
		
		byte[] encapsulation = in.readNBytes(suite.kem().encapsulationLength());
		byte[] noncePrefix = in.readNBytes(NONCE_PREFIX_LENGTH);
		if (encapsulation.length != suite.kem().encapsulationLength() || noncePrefix.length != NONCE_PREFIX_LENGTH) {
			throw new MalformedDataException("Truncated sealed stream header");
		}
		byte[] prologue = CryptoBytes.concat(head, encapsulation);
		
		try (Secret shared = Kems.decapsulate(suite.kem(), recipient, encapsulation); Secret material = Sealed.deriveMaterial(suite, shared, parsed.messageId(), encapsulation)) {
			SecretKey key = Aead.key(suite.aead(), CryptoBytes.slice(material.material(), 0, suite.aead().keyLength()));
			byte[] commitment = CryptoBytes.slice(material.material(), suite.aead().keyLength(), Sealed.COMMITMENT_LENGTH);
			if (!CryptoBytes.equalsConstantTime(commitment, parsed.commitment())) {
				throw new AuthenticationException("Key commitment mismatch - the header does not belong to this key");
			}
			
			int counter = 0;
			while (true) {
				byte[] frame = in.readNBytes(1 + Integer.BYTES);
				if (frame.length == 0) {
					throw new MalformedDataException("Sealed stream ended without a final chunk (truncated)");
				}
				if (frame.length < 1 + Integer.BYTES) {
					throw new MalformedDataException("Truncated chunk header");
				}
				
				byte kind = frame[0];
				if (kind != CHUNK_MORE && kind != CHUNK_FINAL) {
					throw new MalformedDataException("Unknown chunk kind 0x" + String.format("%02X", kind));
				}
				int length = ByteBuffer.wrap(frame, 1, Integer.BYTES).getInt();
				if (length < suite.aead().tagLength() || length > CHUNK_SIZE + suite.aead().tagLength()) {
					throw new MalformedDataException("Implausible chunk length " + length);
				}
				
				byte[] ciphertext = in.readNBytes(length);
				if (ciphertext.length != length) {
					throw new MalformedDataException("Truncated chunk body");
				}
				
				byte[] plaintext = Aead.decrypt(suite.aead(), key, nonce(noncePrefix, counter), ciphertext, aad(prologue, counter, kind));
				try {
					out.write(plaintext);
				} finally {
					CryptoBytes.wipe(plaintext);
				}
				
				if (kind == CHUNK_FINAL) {
					break;
				}
				counter = increment(counter);
			}
			out.flush();
		}
	}
	
	/**
	 * Advances the chunk counter, refusing to wrap.<br>
	 * <p>
	 *     The counter is the varying part of the nonce.<br>
	 *     Wrapping it would repeat a nonce under the same key, which for the modes that are not misuse resistant loses the authentication key outright,<br>
	 *     so the stream is rejected instead.
	 * </p>
	 *
	 * @param counter The current counter
	 * @return The next counter
	 * @throws MalformedDataException If the counter would wrap
	 */
	private static int increment(int counter) {
		if (counter == Integer.MAX_VALUE) {
			throw new MalformedDataException("Sealed stream exceeds the maximum number of chunks");
		}
		return counter + 1;
	}
	
	/**
	 * Builds the nonce of the given chunk.<br>
	 *
	 * @param prefix The random nonce prefix of the stream
	 * @param counter The counter of the chunk
	 * @return The built nonce
	 * @throws NullPointerException If the nonce prefix is null
	 */
	private static byte @NonNull [] nonce(byte @NonNull [] prefix, int counter) {
		Objects.requireNonNull(prefix, "Nonce prefix must not be null");
		return CryptoBytes.concat(prefix, CryptoBytes.of(counter));
	}
	
	/**
	 * Builds the associated data of the given chunk.<br>
	 * Binding the counter and the kind is what makes reordering, truncation and a flipped kind byte detectable.<br>
	 *
	 * @param prologue The header and encapsulation of the stream
	 * @param counter The counter of the chunk
	 * @param kind The kind byte of the chunk
	 * @return The built associated data
	 * @throws NullPointerException If the prologue is null
	 */
	private static byte @NonNull [] aad(byte @NonNull [] prologue, int counter, byte kind) {
		Objects.requireNonNull(prologue, "Prologue must not be null");
		return CryptoBytes.concat(prologue, CryptoBytes.of(counter), new byte[] { kind });
	}
}
