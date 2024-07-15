/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

import net.luis.utils.crypto.key.*;
import net.luis.utils.math.Mth;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static java.math.BigInteger.*;

/**
 * RSA encryption and decryption utility class.<br>
 * The class provides a basic implementation of the RSA algorithm.<br>
 * The class provides methods for generating key pairs, encrypting and decrypting data.<br>
 *
 * @author Luis-St
 */
public class RSA {
	
	/**
	 * The default public key used for key pair generation with a value of 65.537.<br>
	 * This is the fourth Fermat prime and is commonly used as a public key in RSA encryption.<br>
	 */
	private static final BigInteger DEFAULT_PUBLIC_KEY = BigInteger.valueOf(65537);
	
	//region Key pair generation
	
	/**
	 * Generates a fast key pair with a bit size of 1024 and the default public key of 65.537.<br>
	 * The generation of a fast key takes roughly 1 second, depending on the system.<br>
	 * @return The generated key pair
	 * @see #createKeyPair(int, BigInteger)
	 */
	public static @NotNull KeyPair createFastKeyPair() {
		return createKeyPair(1024);
	}
	
	/**
	 * Generates a fast key pair with a bit size of 1024 and the specified public key.<br>
	 * The generation of a fast key takes roughly 1 second, depending on the system.<br>
	 * @param e The public key to use
	 * @return The generated key pair
	 * @throws NullPointerException If the public key is null
	 * @throws ArithmeticException If the public key is not a prime number
	 * @see #createKeyPair(int, BigInteger)
	 */
	public static @NotNull KeyPair createFastKeyPair(@NotNull BigInteger e) {
		return createKeyPair(1024, e);
	}
	
	/**
	 * Generates a secure key pair with a bit size of 4096 and the default public key of 65.537.<br>
	 * The generation of a secure key takes roughly 1 minute, depending on the system.<br>
	 * @return The generated key pair
	 * @see #createKeyPair(int, BigInteger)
	 */
	public static @NotNull KeyPair createSecureKeyPair() {
		return createKeyPair(4096);
	}
	
	/**
	 * Generates a secure key pair with a bit size of 4096 and the specified public key.<br>
	 * The generation of a secure key takes roughly 1 minute, depending on the system.<br>
	 * @param e The public key to use
	 * @return The generated key pair
	 * @throws NullPointerException If the public key is null
	 * @throws ArithmeticException If the public key is not a prime number
	 * @see #createKeyPair(int, BigInteger)
	 */
	public static @NotNull KeyPair createSecureKeyPair(@NotNull BigInteger e) {
		return createKeyPair(4096, e);
	}
	
	/**
	 * Generates a key pair with the specified bit size and the default public key of 65.537.<br>
	 * The bit size must be a power of two and greater than 2.<br>
	 * @param bitSize The bit size of the key pair
	 * @return The generated key pair
	 * @throws IllegalArgumentException If the bit size is not a power of two or less or equal to 2
	 * @see #createKeyPair(int, BigInteger)
	 */
	public static @NotNull KeyPair createKeyPair(int bitSize) {
		return createKeyPair(bitSize, DEFAULT_PUBLIC_KEY);
	}
	
	/**
	 * Generates a key pair with the specified bit size and the specified public key.<br>
	 * The bit size must be a power of two and greater than 2.<br>
	 * The generation time of the key pair depends on the bit size and the system:<br>
	 * <ul>
	 *     <li>512 Bits: (roughly) 0.1 seconds</li>
	 *     <li>1024 Bits: (roughly) 0.25 seconds</li>
	 *     <li>2048 Bits: (roughly) 0.5 seconds</li>
	 *     <li>4096 Bits: (roughly) 1 seconds</li>
	 *     <li>8192 Bits: (roughly) 20 seconds</li>
	 *     <li>16384 Bits: (roughly) 10 minutes</li>
	 * </ul>
	 * <p>
	 *     <strong>Note</strong>:<br>
	 *     In some cases, a private key can not be found for the given public key in first try.<br>
	 *     In such cases, the key pair generation will be repeated until a private key is found.<br>
	 * </p>
	 * <p>
	 *     The recommended bit sizes are 1024, 2048, 4096.<br>
	 *     If performance matters, a bit size of 1024 or 2048 are recommended.<br>
	 * </p>
	 * The bit size
	 * <ul>
	 *     <li>
	 *         <strong>512</strong> is recommended for local use.<br>
	 *         Like:
	 *         <ul>
	 *             <li>communication between two programs</li>
	 *             <li>storage of data on the same system</li>
	 *         </ul>
	 *     </li>
	 *     <li>
	 *         <strong>1024</strong> is recommended for general use.<br>
	 *         Like:
	 *         <ul>
	 *             <li>communication between two systems in the same network</li>
	 *             <li>storage of data</li>
	 *         </ul>
	 *     </li>
	 *     <li>
	 *         <strong>2048</strong> is recommended for security use.<br>
	 *         Like:
	 *         <ul>
	 *             <li>communication between two systems in different networks</li>
	 *             <li>storage of sensitive data</li>
	 *         </ul>
	 *     </li>
	 *     <li>
	 *         <strong>4096</strong> is recommended for high security use.<br>
	 *     </li>
	 * </ul>
	 * <p>
	 *     The maximum bit size which is recommended is 8192 bits.<br>
	 *     The generation of a key pair with a bit size larger than 16.384 bits is not recommended.<br>
	 * </p>
	 * @param bitSize The bit size of the key pair
	 * @param e The public key to use
	 * @throws NullPointerException If the public key is null
	 * @throws IllegalArgumentException If the bit size is not a power of two or less or equal to 2
	 * @throws ArithmeticException If the public key is not a prime number
	 * @return The generated key pair
	 */
	public static @NotNull KeyPair createKeyPair(int bitSize, @NotNull BigInteger e) {
		Objects.requireNonNull(e, "The public key must not be null");
		if (!Mth.isPowerOfTwo(bitSize)) {
			throw new IllegalArgumentException("The bit size must be a power of two");
		}
		if (2 >= bitSize) {
			throw new IllegalArgumentException("The bit size must be greater than 2");
		}
		if (!e.isProbablePrime(100)) {
			throw new ArithmeticException("The public key must be a prime number");
		}
		return createKeyPairInSequence(bitSize, e);
	}
	
	/**
	 * Generates a key pair in sequence, used for bit sizes less than 4096.<br>
	 * This means that the prime numbers are generated one after the other.<br>
	 * @param bitSize The bit size of the key pair
	 * @param e The public key to use
	 * @return The generated key pair
	 */
	private static @NotNull KeyPair createKeyPairInSequence(int bitSize, @NotNull BigInteger e) {
		SecureRandom rng = new SecureRandom();
		BigInteger p, q, phi;
		do {
			p = BigInteger.probablePrime(bitSize / 2, rng);
			q = BigInteger.probablePrime(bitSize / 2, rng);
			phi = p.subtract(ONE).multiply(q.subtract(ONE));
		} while (!e.gcd(phi).equals(ONE));
		
		BigInteger n = p.multiply(q);
		return new KeyPair(new PublicKey(e, n), new PrivateKey(e.modInverse(phi), n));
	}
	//endregion
	
	//region Encryption
	
	/**
	 * Encrypts the specified string using the given public key.<br>
	 * @param data The string to encrypt
	 * @param key The public key to use
	 * @return The encrypted data
	 * @throws NullPointerException If the data or key is null
	 */
	public static byte @NotNull [] encrypt(@NotNull String data, @NotNull PublicKey key) {
		Objects.requireNonNull(data, "Data must not be null");
		Objects.requireNonNull(key, "Key must not be null");
		return encrypt(data.isEmpty() ? new byte[] { 0 } : data.getBytes(), key);
	}
	
	/**
	 * Encrypts the specified byte array using the given public key.<br>
	 * @param data The byte array to encrypt
 	 * @param key The public key to use
	 * @return The encrypted data
	 * @throws NullPointerException If the data or key is null
	 */
	public static byte @NotNull [] encrypt(byte @NotNull [] data, @NotNull PublicKey key) {
		Objects.requireNonNull(data, "Data must not be null");
		Objects.requireNonNull(key, "Key must not be null");
		return encrypt(data.length == 0 ? ZERO : new BigInteger(data), key);
	}
	
	/**
	 * Encrypts the specified data using the given public key.<br>
	 * @param data The data to encrypt
	 * @param key The public key to use
	 * @return The encrypted data
	 * @throws NullPointerException If the data or key is null
	 */
	public static byte @NotNull [] encrypt(@NotNull BigInteger data, @NotNull PublicKey key) {
		Objects.requireNonNull(data, "Data must not be null");
		Objects.requireNonNull(key, "Key must not be null");
		return data.modPow(key.getKey(), key.getModulus()).toByteArray();
	}
	//endregion
	
	//region Decryption
	
	/**
	 * Decrypts the specified byte array using the given private key.<br>
	 * @param data The byte array to decrypt
	 * @param key The private key to use
	 * @return The decrypted data
	 * @throws NullPointerException If the data or key is null
	 */
	public static byte @NotNull [] decrypt(byte @NotNull [] data, @NotNull PrivateKey key) {
		Objects.requireNonNull(data, "Data must not be null");
		Objects.requireNonNull(key, "Key must not be null");
		return decrypt(new BigInteger(data), key);
	}
	
	/**
	 * Decrypts the specified data using the given private key.<br>
	 * @param data The data to decrypt
	 * @param key The private key to use
	 * @return The decrypted data
	 * @throws NullPointerException If the data or key is null
	 */
	public static byte @NotNull [] decrypt(@NotNull BigInteger data, @NotNull PrivateKey key) {
		Objects.requireNonNull(data, "Data must not be null");
		Objects.requireNonNull(key, "Key must not be null");
		return data.modPow(key.getKey(), key.getModulus()).toByteArray();
	}
	//endregion
}
