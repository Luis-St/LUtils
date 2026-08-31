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
import net.luis.utils.crypto.exception.*;
import net.luis.utils.crypto.util.CryptoBytes;
import net.luis.utils.crypto.util.CryptoRandom;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Optional;

/**
 * Authenticated encryption with associated data.<br>
 * <p>
 *     The default overloads generate a nonce and prepend it to the ciphertext, so a repeated nonce is never a caller's problem.<br>
 *     The detached-nonce overloads exist for the wire formats that carry the nonce in their own header, and there the caller takes that responsibility back.
 * </p>
 * <p>
 *     None of these modes are key-committing.<br>
 *     A ciphertext can be constructed that authenticates under two different keys, which matters wherever a reader tries more than one key against the same ciphertext.<br>
 *     {@link Sealed} therefore derives an explicit key commitment and binds it into the header.<br>
 *     Do the same in any construction that trial-decrypts.
 * </p>
 * <p>
 *     Example:
 * </p>
 * <pre>{@code
 * SecretKey key = Aead.generateKey(AeadAlgorithm.AES_256_GCM);
 *
 * byte[] ciphertext = Aead.encrypt(AeadAlgorithm.AES_256_GCM, key, plaintext);
 * byte[] recovered = Aead.decrypt(AeadAlgorithm.AES_256_GCM, key, ciphertext);
 *
 * // Associated data is authenticated but not encrypted, and has to be presented again to decrypt
 * byte[] header = "message-42".getBytes(StandardCharsets.UTF_8);
 * byte[] bound = Aead.encrypt(AeadAlgorithm.AES_256_GCM, key, plaintext, header);
 * byte[] opened = Aead.decrypt(AeadAlgorithm.AES_256_GCM, key, bound, header);
 * }</pre>
 *
 * @see AeadAlgorithm
 *
 * @author Luis-St
 */
public final class Aead {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private Aead() {}
	
	/**
	 * Generates a new random key for the given algorithm.<br>
	 *
	 * @param algorithm The algorithm the key is for
	 * @return The generated key
	 * @throws NullPointerException If the algorithm is null
	 */
	public static @NonNull SecretKey generateKey(@NonNull AeadAlgorithm algorithm) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		return new SecretKeySpec(CryptoRandom.bytes(algorithm.keyLength()), algorithm.keyJcaName());
	}
	
	/**
	 * Wraps the given raw bytes as a key for the given algorithm.<br>
	 *
	 * @param algorithm The algorithm the key is for
	 * @param raw The raw key bytes
	 * @return The wrapped key
	 * @throws NullPointerException If the algorithm or the raw bytes are null
	 * @throws IllegalArgumentException If the raw key does not have the length the algorithm requires
	 */
	public static @NonNull SecretKey key(@NonNull AeadAlgorithm algorithm, byte @NonNull [] raw) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(raw, "Raw key must not be null");
		if (raw.length != algorithm.keyLength()) {
			throw new IllegalArgumentException(algorithm + " requires a " + algorithm.keyLength() + " byte key, got " + raw.length);
		}
		
		return new SecretKeySpec(raw, algorithm.keyJcaName());
	}
	
	/**
	 * Encrypts the given plaintext, generating a nonce and prepending it to the ciphertext.<br>
	 *
	 * @param algorithm The algorithm to encrypt with
	 * @param key The key to encrypt with
	 * @param plaintext The plaintext to encrypt
	 * @return The nonce followed by the ciphertext and its tag
	 * @throws NullPointerException If the algorithm, the key or the plaintext is null
	 * @throws CryptoException If the encryption fails
	 */
	public static byte @NonNull [] encrypt(@NonNull AeadAlgorithm algorithm, @NonNull SecretKey key, byte @NonNull [] plaintext) {
		return encrypt(algorithm, key, plaintext, null);
	}
	
	/**
	 * Encrypts the given plaintext, generating a nonce and prepending it to the ciphertext.<br>
	 * <p>
	 *     The associated data is authenticated but not encrypted.<br>
	 *     It must be presented again, unchanged, to decrypt.
	 * </p>
	 *
	 * @param algorithm The algorithm to encrypt with
	 * @param key The key to encrypt with
	 * @param plaintext The plaintext to encrypt
	 * @param associatedData The data to authenticate alongside the plaintext, may be null
	 * @return The nonce followed by the ciphertext and its tag
	 * @throws NullPointerException If the algorithm, the key or the plaintext is null
	 * @throws CryptoException If the encryption fails
	 */
	public static byte @NonNull [] encrypt(@NonNull AeadAlgorithm algorithm, @NonNull SecretKey key, byte @NonNull [] plaintext, byte @Nullable [] associatedData) {
		return encrypt(CryptoRandom.instance(), algorithm, key, plaintext, associatedData);
	}
	
	/**
	 * Encrypts the given plaintext, drawing the nonce from the given source and prepending it.<br>
	 * This overload exists so the wire formats can be tested against known answers with a fixed source.<br>
	 *
	 * @param random The source to draw the nonce from
	 * @param algorithm The algorithm to encrypt with
	 * @param key The key to encrypt with
	 * @param plaintext The plaintext to encrypt
	 * @param associatedData The data to authenticate alongside the plaintext, may be null
	 * @return The nonce followed by the ciphertext and its tag
	 * @throws NullPointerException If the random source, the algorithm, the key or the plaintext is null
	 * @throws CryptoException If the encryption fails
	 */
	public static byte @NonNull [] encrypt(@NonNull SecureRandom random, @NonNull AeadAlgorithm algorithm, @NonNull SecretKey key, byte @NonNull [] plaintext, byte @Nullable [] associatedData) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		
		byte[] nonce = CryptoRandom.bytes(random, algorithm.nonceLength());
		return CryptoBytes.concat(nonce, encrypt(algorithm, key, nonce, plaintext, associatedData));
	}
	
	/**
	 * Encrypts the given plaintext under an explicit nonce, which is not prepended.<br>
	 * <p>
	 *     The caller is responsible for never repeating a nonce under the same key.<br>
	 *     For the modes that are not misuse resistant, a repeat loses the authentication key outright.
	 * </p>
	 *
	 * @param algorithm The algorithm to encrypt with
	 * @param key The key to encrypt with
	 * @param nonce The nonce to encrypt with
	 * @param plaintext The plaintext to encrypt
	 * @param associatedData The data to authenticate alongside the plaintext, may be null
	 * @return The ciphertext and its tag
	 * @throws NullPointerException If the algorithm, the key, the nonce or the plaintext is null
	 * @throws CryptoException If the encryption fails
	 */
	public static byte @NonNull [] encrypt(@NonNull AeadAlgorithm algorithm, @NonNull SecretKey key, byte @NonNull [] nonce, byte @NonNull [] plaintext, byte @Nullable [] associatedData) {
		Objects.requireNonNull(plaintext, "Plaintext must not be null");
		
		try {
			return cipher(algorithm, Cipher.ENCRYPT_MODE, key, nonce, associatedData).doFinal(plaintext);
		} catch (GeneralSecurityException e) {
			throw new CryptoException("Encryption failed for " + algorithm, e);
		}
	}
	
	/**
	 * Decrypts a ciphertext which carries its nonce in front of it.<br>
	 *
	 * @param algorithm The algorithm to decrypt with
	 * @param key The key to decrypt with
	 * @param ciphertext The nonce followed by the ciphertext and its tag
	 * @return The recovered plaintext
	 * @throws NullPointerException If the algorithm, the key or the ciphertext is null
	 * @throws MalformedDataException If the ciphertext is too short to hold a nonce and a tag
	 * @throws AuthenticationException If the ciphertext does not authenticate
	 * @throws CryptoException If the decryption fails for any other reason
	 */
	public static byte @NonNull [] decrypt(@NonNull AeadAlgorithm algorithm, @NonNull SecretKey key, byte @NonNull [] ciphertext) {
		return decrypt(algorithm, key, ciphertext, null);
	}
	
	/**
	 * Decrypts a ciphertext which carries its nonce in front of it.<br>
	 *
	 * @param algorithm The algorithm to decrypt with
	 * @param key The key to decrypt with
	 * @param ciphertext The nonce followed by the ciphertext and its tag
	 * @param associatedData The data authenticated alongside the plaintext, may be null
	 * @return The recovered plaintext
	 * @throws NullPointerException If the algorithm, the key or the ciphertext is null
	 * @throws MalformedDataException If the ciphertext is too short to hold a nonce and a tag
	 * @throws AuthenticationException If the ciphertext does not authenticate
	 * @throws CryptoException If the decryption fails for any other reason
	 */
	public static byte @NonNull [] decrypt(@NonNull AeadAlgorithm algorithm, @NonNull SecretKey key, byte @NonNull [] ciphertext, byte @Nullable [] associatedData) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(ciphertext, "Ciphertext must not be null");
		
		int nonceLength = algorithm.nonceLength();
		if (ciphertext.length < nonceLength + algorithm.tagLength()) {
			throw new MalformedDataException("Ciphertext too short for " + algorithm + ": " + ciphertext.length);
		}
		
		byte[] nonce = CryptoBytes.slice(ciphertext, 0, nonceLength);
		byte[] body = CryptoBytes.slice(ciphertext, nonceLength, ciphertext.length - nonceLength);
		return decrypt(algorithm, key, nonce, body, associatedData);
	}
	
	/**
	 * Decrypts a ciphertext under an explicit nonce.<br>
	 *
	 * @param algorithm The algorithm to decrypt with
	 * @param key The key to decrypt with
	 * @param nonce The nonce the ciphertext was produced with
	 * @param ciphertext The ciphertext and its tag
	 * @param associatedData The data authenticated alongside the plaintext, may be null
	 * @return The recovered plaintext
	 * @throws NullPointerException If the algorithm, the key, the nonce or the ciphertext is null
	 * @throws AuthenticationException If the ciphertext does not authenticate
	 * @throws CryptoException If the decryption fails for any other reason
	 */
	public static byte @NonNull [] decrypt(@NonNull AeadAlgorithm algorithm, @NonNull SecretKey key, byte @NonNull [] nonce, byte @NonNull [] ciphertext, byte @Nullable [] associatedData) {
		Objects.requireNonNull(ciphertext, "Ciphertext must not be null");
		
		try {
			return cipher(algorithm, Cipher.DECRYPT_MODE, key, nonce, associatedData).doFinal(ciphertext);
		} catch (BadPaddingException e) {
			throw new AuthenticationException("Authentication tag mismatch, the ciphertext was modified or the key is wrong", e);
		} catch (GeneralSecurityException e) {
			throw new CryptoException("Decryption failed for " + algorithm, e);
		}
	}
	
	/**
	 * Attempts a decryption without treating a failure as exceptional.<br>
	 * <p>
	 *     Used wherever a reader legitimately does not know in advance whether a key fits, such as the recipient slots of a multi-recipient artifact.<br>
	 *     Only an authentication failure returns empty.<br>
	 *     A broken configuration still throws.
	 * </p>
	 *
	 * @param algorithm The algorithm to decrypt with
	 * @param key The key to decrypt with
	 * @param nonce The nonce the ciphertext was produced with
	 * @param ciphertext The ciphertext and its tag
	 * @param associatedData The data authenticated alongside the plaintext, may be null
	 * @return The recovered plaintext, or empty if the ciphertext does not authenticate
	 * @throws NullPointerException If the algorithm, the key, the nonce or the ciphertext is null
	 * @throws CryptoException If the decryption fails for a reason other than authentication
	 */
	public static @NonNull Optional<byte[]> tryDecrypt(@NonNull AeadAlgorithm algorithm, @NonNull SecretKey key, byte @NonNull [] nonce, byte @NonNull [] ciphertext, byte @Nullable [] associatedData) {
		Objects.requireNonNull(ciphertext, "Ciphertext must not be null");
		
		try {
			return Optional.of(cipher(algorithm, Cipher.DECRYPT_MODE, key, nonce, associatedData).doFinal(ciphertext));
		} catch (BadPaddingException e) {
			return Optional.empty();
		} catch (GeneralSecurityException e) {
			throw new CryptoException("Decryption failed for " + algorithm, e);
		}
	}
	
	/**
	 * Creates and initialises a cipher for the given algorithm, mode, key and nonce.<br>
	 *
	 * @param algorithm The algorithm to use
	 * @param mode The cipher mode, either encrypt or decrypt
	 * @param key The key to use
	 * @param nonce The nonce to use
	 * @param associatedData The data to authenticate alongside the plaintext, may be null
	 * @return The initialised cipher
	 * @throws NullPointerException If the algorithm, the key or the nonce is null
	 * @throws GeneralSecurityException If the cipher cannot be created or initialised
	 */
	static @NonNull Cipher cipher(@NonNull AeadAlgorithm algorithm, int mode, @NonNull SecretKey key, byte @NonNull [] nonce, byte @Nullable [] associatedData) throws GeneralSecurityException {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(nonce, "Nonce must not be null");
		
		Cipher cipher = Cipher.getInstance(algorithm.jcaName());
		cipher.init(mode, key, algorithm.parameterSpec(nonce));
		if (associatedData != null && associatedData.length > 0) {
			cipher.updateAAD(associatedData);
		}
		return cipher;
	}
}
