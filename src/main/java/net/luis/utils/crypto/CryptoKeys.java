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

import net.luis.utils.crypto.algorithm.*;
import net.luis.utils.crypto.exception.MalformedDataException;
import net.luis.utils.crypto.key.*;
import org.jspecify.annotations.NonNull;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

/**
 * Encodes and decodes key material, including the composite keys of the hybrid algorithms.<br>
 * <p>
 *     Composite keys are written as {@code length || classical || length || post-quantum} and are decoded against the algorithm they belong to, because no JCA {@link KeyFactory} knows the composite name.<br>
 *     Without this, a hybrid key could be written and never read back, which would make key storage impossible for exactly the suite this library recommends.
 * </p>
 * <p>
 *     Example:
 * </p>
 * <pre>{@code
 * KeyPair pair = Kems.generateKeyPair(KemAlgorithm.X25519_ML_KEM_768);
 *
 * // A composite key is decoded against the algorithm it belongs to, no key factory knows its name
 * byte[] encoded = pair.getPublic().getEncoded();
 * PublicKey restored = CryptoKeys.publicKey(KemAlgorithm.X25519_ML_KEM_768, encoded);
 *
 * KeyId id = CryptoKeys.id(restored);
 * byte[] fingerprint = CryptoKeys.fingerprint(HashAlgorithm.SHA_256, restored);
 * }</pre>
 *
 * @author Luis-St
 */
public final class CryptoKeys {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private CryptoKeys() {}
	
	/**
	 * Decodes a public key of the given key encapsulation mechanism.<br>
	 *
	 * @param algorithm The mechanism the key belongs to
	 * @param encoded The encoded key
	 * @return The decoded key
	 * @throws NullPointerException If the algorithm or the encoded key is null
	 * @throws MalformedDataException If the key cannot be decoded
	 */
	public static @NonNull PublicKey publicKey(@NonNull KemAlgorithm algorithm, byte @NonNull [] encoded) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(encoded, "Encoded key must not be null");
		
		return switch (algorithm) {
			case NativeKemAlgorithm n -> decodePublic(n.keyJcaName(), encoded);
			case DhKemAlgorithm dh -> decodePublic(dh.keyJcaName(), encoded);
			case HybridKemAlgorithm hybrid -> {
				Split split = Split.of(encoded);
				yield new HybridPublicKey(publicKey(hybrid.classical(), split.first()), publicKey(hybrid.postQuantum(), split.second()));
			}
		};
	}
	
	/**
	 * Decodes a private key of the given key encapsulation mechanism.<br>
	 *
	 * @param algorithm The mechanism the key belongs to
	 * @param encoded The encoded key
	 * @return The decoded key
	 * @throws NullPointerException If the algorithm or the encoded key is null
	 * @throws MalformedDataException If the key cannot be decoded
	 */
	public static @NonNull PrivateKey privateKey(@NonNull KemAlgorithm algorithm, byte @NonNull [] encoded) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(encoded, "Encoded key must not be null");
		
		return switch (algorithm) {
			case NativeKemAlgorithm n -> decodePrivate(n.keyJcaName(), encoded);
			case DhKemAlgorithm dh -> decodePrivate(dh.keyJcaName(), encoded);
			case HybridKemAlgorithm hybrid -> {
				Split split = Split.of(encoded);
				yield new HybridPrivateKey(privateKey(hybrid.classical(), split.first()), privateKey(hybrid.postQuantum(), split.second()));
			}
		};
	}
	
	/**
	 * Decodes a public key of the given signature scheme.<br>
	 *
	 * @param algorithm The scheme the key belongs to
	 * @param encoded The encoded key
	 * @return The decoded key
	 * @throws NullPointerException If the algorithm or the encoded key is null
	 * @throws MalformedDataException If the key cannot be decoded
	 */
	public static @NonNull PublicKey publicKey(@NonNull SignatureAlgorithm algorithm, byte @NonNull [] encoded) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(encoded, "Encoded key must not be null");
		
		return switch (algorithm) {
			case NativeSignatureAlgorithm n -> decodePublic(n.keyJcaName(), encoded);
			case HybridSignatureAlgorithm hybrid -> {
				Split split = Split.of(encoded);
				yield new HybridPublicKey(publicKey(hybrid.classical(), split.first()), publicKey(hybrid.postQuantum(), split.second()));
			}
		};
	}
	
	/**
	 * Decodes a private key of the given signature scheme.<br>
	 *
	 * @param algorithm The scheme the key belongs to
	 * @param encoded The encoded key
	 * @return The decoded key
	 * @throws NullPointerException If the algorithm or the encoded key is null
	 * @throws MalformedDataException If the key cannot be decoded
	 */
	public static @NonNull PrivateKey privateKey(@NonNull SignatureAlgorithm algorithm, byte @NonNull [] encoded) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(encoded, "Encoded key must not be null");
		
		return switch (algorithm) {
			case NativeSignatureAlgorithm n -> decodePrivate(n.keyJcaName(), encoded);
			case HybridSignatureAlgorithm hybrid -> {
				Split split = Split.of(encoded);
				yield new HybridPrivateKey(privateKey(hybrid.classical(), split.first()), privateKey(hybrid.postQuantum(), split.second()));
			}
		};
	}
	
	/**
	 * Returns the key id of the given public key.<br>
	 *
	 * @param key The key to identify
	 * @return The key id
	 * @throws NullPointerException If the key is null
	 */
	public static @NonNull KeyId id(@NonNull PublicKey key) {
		return KeyId.of(key);
	}
	
	/**
	 * Computes a fingerprint of the given public key.<br>
	 * Unlike a {@link KeyId} this is the full digest, so it is suitable for out-of-band comparison.<br>
	 *
	 * @param algorithm The hash to fingerprint with
	 * @param key The key to fingerprint
	 * @return The computed fingerprint
	 * @throws NullPointerException If the algorithm or the key is null
	 */
	public static byte @NonNull [] fingerprint(@NonNull HashAlgorithm algorithm, @NonNull PublicKey key) {
		Objects.requireNonNull(key, "Key must not be null");
		return Hashes.hash(algorithm, key.getEncoded());
	}
	
	/**
	 * Decodes an X.509 encoded public key through the key factory of the given name.<br>
	 *
	 * @param keyJcaName The JCA key name to decode with
	 * @param encoded The encoded key
	 * @return The decoded key
	 * @throws NullPointerException If the key JCA name or the encoded key is null
	 * @throws MalformedDataException If the key cannot be decoded
	 */
	static @NonNull PublicKey decodePublic(@NonNull String keyJcaName, byte @NonNull [] encoded) {
		Objects.requireNonNull(keyJcaName, "Key JCA name must not be null");
		Objects.requireNonNull(encoded, "Encoded key must not be null");
		
		try {
			return KeyFactory.getInstance(keyJcaName).generatePublic(new X509EncodedKeySpec(encoded));
		} catch (GeneralSecurityException e) {
			throw new MalformedDataException("Cannot decode " + keyJcaName + " public key", e);
		}
	}
	
	/**
	 * Decodes a PKCS#8 encoded private key through the key factory of the given name.<br>
	 *
	 * @param keyJcaName The JCA key name to decode with
	 * @param encoded The encoded key
	 * @return The decoded key
	 * @throws NullPointerException If the key JCA name or the encoded key is null
	 * @throws MalformedDataException If the key cannot be decoded
	 */
	static @NonNull PrivateKey decodePrivate(@NonNull String keyJcaName, byte @NonNull [] encoded) {
		Objects.requireNonNull(keyJcaName, "Key JCA name must not be null");
		Objects.requireNonNull(encoded, "Encoded key must not be null");
		
		try {
			return KeyFactory.getInstance(keyJcaName).generatePrivate(new PKCS8EncodedKeySpec(encoded));
		} catch (GeneralSecurityException e) {
			throw new MalformedDataException("Cannot decode " + keyJcaName + " private key", e);
		}
	}
	
	/**
	 * The two components of a composite key encoding.<br>
	 *
	 * @author Luis-St
	 *
	 * @param first The classical component
	 * @param second The post-quantum component
	 */
	private record Split(
		byte @NonNull [] first,
		byte @NonNull [] second
	) {
		
		/**
		 * Constructs a new split with the given components.<br>
		 * @throws NullPointerException If any of the components is null
		 */
		private Split {
			Objects.requireNonNull(first, "First component must not be null");
			Objects.requireNonNull(second, "Second component must not be null");
		}
		
		/**
		 * Splits a composite key encoding into its two components.<br>
		 * <p>
		 *     Each length is checked against what is actually left in the buffer before anything is allocated, so a hostile length prefix cannot turn a short input into a huge allocation.
		 * </p>
		 *
		 * @param encoded The composite encoding to split
		 * @return The two components
		 * @throws NullPointerException If the encoded key is null
		 * @throws MalformedDataException If the encoding is malformed or truncated
		 */
		private static @NonNull Split of(byte @NonNull [] encoded) {
			Objects.requireNonNull(encoded, "Encoded composite key must not be null");
			ByteBuffer buffer = ByteBuffer.wrap(encoded);
			
			try {
				byte[] first = new byte[bounded(buffer)];
				buffer.get(first);
				byte[] second = new byte[bounded(buffer)];
				buffer.get(second);
				return new Split(first, second);
			} catch (RuntimeException e) {
				throw new MalformedDataException("Malformed composite key encoding", e);
			}
		}
		
		/**
		 * Reads a length prefix and checks it against the bytes remaining in the buffer.<br>
		 *
		 * @param buffer The buffer to read from
		 * @return The read length
		 * @throws NullPointerException If the buffer is null
		 * @throws BufferUnderflowException If the buffer holds no length prefix
		 * @throws MalformedDataException If the length is negative or longer than the remaining bytes
		 */
		private static int bounded(@NonNull ByteBuffer buffer) {
			Objects.requireNonNull(buffer, "Buffer must not be null");
			
			int length = buffer.getInt();
			if (length < 0 || length > buffer.remaining()) {
				throw new MalformedDataException("Composite key component length " + length + " does not fit into the remaining " + buffer.remaining() + " bytes");
			}
			return length;
		}
	}
}
