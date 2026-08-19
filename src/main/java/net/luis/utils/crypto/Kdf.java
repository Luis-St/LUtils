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

import net.luis.utils.crypto.algorithm.AeadAlgorithm;
import net.luis.utils.crypto.algorithm.KdfAlgorithm;
import net.luis.utils.crypto.exception.CryptoException;
import net.luis.utils.crypto.exception.UnsupportedAlgorithmException;
import net.luis.utils.crypto.key.Secret;
import net.luis.utils.crypto.util.CryptoBytes;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.crypto.KDF;
import javax.crypto.SecretKey;
import javax.crypto.spec.HKDFParameterSpec;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * RFC 5869 HKDF key derivation.<br>
 * <p>
 *     The derivation itself is the JDK {@link KDF} API, which ships HKDF over the three SHA-2 hashes as a first class primitive.<br>
 *     This class contributes the argument checking, the naming and the wiping, and owns no cryptographic code of its own.<br>
 *     The trade is that only what a provider serves can be modeled, so an HKDF over a SHA-3 hmac is no longer reachable by adding a constant.
 * </p>
 * <p>
 *     Every method returns a {@link Secret} rather than a raw array,<br>
 *     so intermediate key material is wiped structurally instead of by remembering a finally block.
 * </p>
 * <p>
 *     Example:
 * </p>
 * <pre>{@code
 * // One input secret, several independent keys, told apart by their info parameter
 * try (Secret encryption = Kdf.derive(KdfAlgorithm.HKDF_SHA_256, ikm, salt, "encryption".getBytes(StandardCharsets.UTF_8), 32)) {
 *     use(encryption.material());
 * }
 *
 * // The same derivation, handed straight to an aead algorithm as a key
 * SecretKey key = Kdf.deriveKey(KdfAlgorithm.HKDF_SHA_256, ikm, salt, info, AeadAlgorithm.AES_256_GCM);
 * }</pre>
 *
 * @see KdfAlgorithm
 *
 * @author Luis-St
 */
public final class Kdf {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private Kdf() {}
	
	/**
	 * Performs the extract step, condensing the input key material into a pseudo random key.<br>
	 * <p>
	 *     A null or empty salt is left out, which RFC 5869 defines to mean a block of zeros.<br>
	 *     The salt is not secret and may be transmitted in the clear.
	 * </p>
	 *
	 * @param algorithm The key derivation function to use
	 * @param salt The salt to extract with, may be null
	 * @param ikm The input key material
	 * @return The extracted pseudo random key
	 * @throws NullPointerException If the algorithm or the input key material is null
	 * @throws UnsupportedAlgorithmException If no registered provider serves the algorithm
	 * @throws CryptoException If the extraction fails
	 */
	public static @NonNull Secret extract(@NonNull KdfAlgorithm algorithm, byte @Nullable [] salt, byte @NonNull [] ikm) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(ikm, "Input key material must not be null");
		
		HKDFParameterSpec.Builder builder = HKDFParameterSpec.ofExtract().addIKM(ikm);
		if (salt != null && salt.length > 0) {
			builder.addSalt(salt);
		}
		
		try {
			return Secret.adopt(kdf(algorithm).deriveData(builder.extractOnly()));
		} catch (GeneralSecurityException e) {
			throw new CryptoException("Extraction failed for " + algorithm.name(), e);
		}
	}
	
	/**
	 * Performs the expand step, stretching a pseudo random key to the requested length.<br>
	 * <p>
	 *     The info parameter binds the output to a context.<br>
	 *     Two derivations from the same pseudo random key with different info values are independent, which is what lets this library derive several keys from one shared secret.
	 * </p>
	 *
	 * @param algorithm The key derivation function to use
	 * @param prk The pseudo random key to expand
	 * @param info The context to bind the output to, may be null
	 * @param length The number of bytes to produce
	 * @return The expanded output key material
	 * @throws NullPointerException If the algorithm or the pseudo random key is null
	 * @throws IllegalArgumentException If the length is not in the range supported by the algorithm, or the pseudo random key is shorter than one output block
	 * @throws IllegalStateException If the pseudo random key has already been closed
	 * @throws UnsupportedAlgorithmException If no registered provider serves the algorithm
	 * @throws CryptoException If the expansion fails
	 */
	public static @NonNull Secret expand(@NonNull KdfAlgorithm algorithm, @NonNull Secret prk, byte @Nullable [] info, int length) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(prk, "Pseudo random key must not be null");
		
		int hashLength = algorithm.outputLength();
		if (length < 1 || length > 255 * hashLength) {
			throw new IllegalArgumentException("HKDF output length must be in [1, " + (255 * hashLength) + "], was " + length);
		}
		if (prk.material().length < hashLength) {
			throw new IllegalArgumentException("HKDF pseudo random key must be at least " + hashLength + " bytes for " + algorithm.name() + ", was " + prk.material().length);
		}
		
		byte[] safeInfo = info == null ? CryptoBytes.EMPTY : info;
		SecretKey key = prk.toKey(algorithm.jcaName());
		try {
			return Secret.adopt(kdf(algorithm).deriveData(HKDFParameterSpec.expandOnly(key, safeInfo, length)));
		} catch (GeneralSecurityException e) {
			throw new CryptoException("Expansion failed for " + algorithm.name(), e);
		}
	}
	
	/**
	 * Derives output key material in one step, extracting and then expanding.<br>
	 *
	 * @param algorithm The key derivation function to use
	 * @param ikm The input key material
	 * @param salt The salt to extract with, may be null
	 * @param info The context to bind the output to, may be null
	 * @param length The number of bytes to produce
	 * @return The derived output key material
	 * @throws NullPointerException If the algorithm or the input key material is null
	 * @throws IllegalArgumentException If the length is not in the range supported by the algorithm
	 * @throws UnsupportedAlgorithmException If no registered provider serves the algorithm
	 * @throws CryptoException If the derivation fails
	 */
	public static @NonNull Secret derive(@NonNull KdfAlgorithm algorithm, byte @NonNull [] ikm, byte @Nullable [] salt, byte @Nullable [] info, int length) {
		try (Secret prk = extract(algorithm, salt, ikm)) {
			return expand(algorithm, prk, info, length);
		}
	}
	
	/**
	 * Derives a key of the length the given aead algorithm requires.<br>
	 * The intermediate material is wiped before this returns.<br>
	 * Only the key survives.<br>
	 *
	 * @param algorithm The key derivation function to use
	 * @param ikm The input key material
	 * @param salt The salt to extract with, may be null
	 * @param info The context to bind the output to, may be null
	 * @param target The algorithm the derived key is for
	 * @return The derived key
	 * @throws NullPointerException If the algorithm, the input key material or the target is null
	 */
	public static @NonNull SecretKey deriveKey(@NonNull KdfAlgorithm algorithm, byte @NonNull [] ikm, byte @Nullable [] salt, byte @Nullable [] info, @NonNull AeadAlgorithm target) {
		Objects.requireNonNull(target, "Target must not be null");
		
		try (Secret material = derive(algorithm, ikm, salt, info, target.keyLength())) {
			return material.toKey(target.keyJcaName());
		}
	}
	
	/**
	 * Creates the JDK key derivation function serving the given algorithm.<br>
	 *
	 * @param algorithm The key derivation function to create
	 * @return The created key derivation function
	 * @throws NullPointerException If the algorithm is null
	 * @throws UnsupportedAlgorithmException If no registered provider serves the algorithm
	 */
	private static @NonNull KDF kdf(@NonNull KdfAlgorithm algorithm) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		
		try {
			return KDF.getInstance(algorithm.jcaName());
		} catch (NoSuchAlgorithmException e) {
			throw UnsupportedAlgorithmException.forAlgorithm(algorithm.jcaName(), e);
		}
	}
}
