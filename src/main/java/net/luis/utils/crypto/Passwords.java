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
import net.luis.utils.crypto.exception.CryptoException;
import net.luis.utils.crypto.exception.MalformedDataException;
import net.luis.utils.crypto.key.Secret;
import net.luis.utils.crypto.util.CryptoBytes;
import net.luis.utils.crypto.util.CryptoRandom;
import org.bouncycastle.jcajce.spec.Argon2KeySpec;
import org.bouncycastle.jcajce.spec.ScryptKeySpec;
import org.jspecify.annotations.NonNull;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;
import java.util.*;

/**
 * Password hashing in the PHC string format.<br>
 * <p>
 *     The function and its cost travel with the hash, so both can be raised later without invalidating existing records.<br>
 *     Call {@link #needsRehash(String)} after a successful login to migrate silently.
 * </p>
 * <p>
 *     New records are produced with {@link PasswordAlgorithm#ARGON2ID}.<br>
 *     Records produced with any of the other functions still verify, and {@link #needsRehash(String)} reports them as due for replacement.
 * </p>
 * <p>
 *     Passwords are taken as character arrays and never as strings, so the caller can wipe them.<br>
 *     This class wipes everything it derives, but the password itself stays the caller's responsibility.
 * </p>
 * <p>
 *     Example:
 * </p>
 * <pre>{@code
 * char[] password = readPassword();
 * String stored = Passwords.hash(password);
 *
 * if (Passwords.verify(password, stored)) {
 *     // Migrate silently once the default function or cost moves on
 *     if (Passwords.needsRehash(stored)) {
 *         stored = Passwords.hash(password);
 *     }
 * }
 * Arrays.fill(password, '\0');
 * }</pre>
 *
 * @see PasswordAlgorithm
 *
 * @author Luis-St
 */
public final class Passwords {
	
	/**
	 * The configuration new records are produced with.<br>
	 */
	private static final PasswordAlgorithm DEFAULT = PasswordAlgorithm.ARGON2ID;
	/**
	 * The salt length in bytes.<br>
	 */
	private static final int SALT_LENGTH = 16;
	/**
	 * The hash length in bytes for new records.<br>
	 */
	private static final int HASH_LENGTH = 32;
	/**
	 * The smallest hash length that is accepted from an encoded record.<br>
	 */
	private static final int MIN_HASH_LENGTH = 16;
	/**
	 * The largest hash length that is accepted from an encoded record.<br>
	 */
	private static final int MAX_HASH_LENGTH = 64;
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private Passwords() {}
	
	/**
	 * Hashes the given password with the current default function and cost.<br>
	 *
	 * @param password The password to hash
	 * @return The encoded hash, in the PHC string format
	 * @throws NullPointerException If the password is null
	 * @throws CryptoException If the derivation fails
	 */
	public static @NonNull String hash(char @NonNull [] password) {
		return hash(password, DEFAULT);
	}
	
	/**
	 * Hashes the given password with the given function and cost.<br>
	 * <p>
	 *     The returned string carries the hash itself, so the derived bytes exist as a string regardless of any wiping.<br>
	 *     That is inherent to the format.<br>
	 *     The wiping still bounds how long the raw array lives.
	 * </p>
	 *
	 * @param password The password to hash
	 * @param algorithm The function and cost to derive with
	 * @return The encoded hash, in the PHC string format
	 * @throws NullPointerException If the password or the algorithm is null
	 * @throws CryptoException If the derivation fails
	 */
	public static @NonNull String hash(char @NonNull [] password, @NonNull PasswordAlgorithm algorithm) {
		Objects.requireNonNull(password, "Password must not be null");
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		
		byte[] salt = CryptoRandom.bytes(SALT_LENGTH);
		try (Secret hash = derive(algorithm, password, salt, HASH_LENGTH)) {
			return "$" + algorithm.identifier()
				+ "$" + algorithm.encodeParameters()
				+ "$" + Base64.getUrlEncoder().withoutPadding().encodeToString(salt)
				+ "$" + Base64.getUrlEncoder().withoutPadding().encodeToString(hash.material());
		}
	}
	
	/**
	 * Checks a password against an encoded hash.<br>
	 * <p>
	 *     The record is verified with the function and cost it was produced with, not with the current default,<br>
	 *     which is what lets the default move without invalidating anything already stored.<br>
	 *     The comparison is constant-time, so it does not leak how much of the hash matched.
	 * </p>
	 *
	 * @param password The password to check
	 * @param encoded The encoded hash to check against
	 * @return True if the password matches the encoded hash
	 * @throws NullPointerException If the password or the encoded hash is null
	 * @throws MalformedDataException If the encoded hash cannot be parsed
	 * @throws CryptoException If the derivation fails
	 */
	public static boolean verify(char @NonNull [] password, @NonNull String encoded) {
		Objects.requireNonNull(password, "Password must not be null");
		
		Encoded parsed = Encoded.parse(encoded);
		try (Secret candidate = derive(parsed.algorithm(), password, parsed.salt(), parsed.hash().length)) {
			return CryptoBytes.equalsConstantTime(candidate.material(), parsed.hash());
		}
	}
	
	/**
	 * Returns whether the stored record used a weaker function or cost than the current default.<br>
	 * Call this after a successful verification to re-hash and store silently.<br>
	 *
	 * @param encoded The encoded hash to inspect
	 * @return True if the record should be replaced by a freshly hashed one
	 * @throws NullPointerException If the encoded hash is null
	 * @throws MalformedDataException If the encoded hash cannot be parsed
	 */
	public static boolean needsRehash(@NonNull String encoded) {
		return Encoded.parse(encoded).algorithm().isWeakerThan(DEFAULT);
	}
	
	/**
	 * Derives a hash from the given password and salt.<br>
	 * <p>
	 *     Every function is reached through a secret key factory, so the three paths differ only in the key spec they build.<br>
	 *     The two BouncyCastle ones are asked of the provider instance directly rather than of whatever is registered,<br>
	 *     so hashing works without {@link Providers#installBouncyCastle()} having been called.
	 * </p>
	 *
	 * @param algorithm The function and cost to derive with
	 * @param password The password to derive from
	 * @param salt The salt to derive with
	 * @param length The number of bytes to produce
	 * @return The derived hash
	 * @throws NullPointerException If the algorithm, the password or the salt is null
	 * @throws CryptoException If the derivation fails
	 */
	private static @NonNull Secret derive(@NonNull PasswordAlgorithm algorithm, char @NonNull [] password, byte @NonNull [] salt, int length) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(password, "Password must not be null");
		Objects.requireNonNull(salt, "Salt must not be null");
		
		int bits = length * Byte.SIZE;
		KeySpec spec = switch (algorithm) {
			case Argon2PasswordAlgorithm argon2 -> new Argon2KeySpec(password, salt, argon2.iterations(), argon2.memory(), argon2.parallelism(), bits);
			case ScryptPasswordAlgorithm scrypt -> new ScryptKeySpec(password, salt, scrypt.cost(), scrypt.blockSize(), scrypt.parallelism(), bits);
			case Pbkdf2PasswordAlgorithm pbkdf2 -> new PBEKeySpec(password, salt, pbkdf2.iterations(), bits);
		};
		
		try {
			SecretKeyFactory factory = algorithm.requiresBouncyCastle() ? SecretKeyFactory.getInstance(algorithm.jcaName(), Providers.bouncyCastle()) : SecretKeyFactory.getInstance(algorithm.jcaName());
			return Secret.adopt(factory.generateSecret(spec).getEncoded());
		} catch (GeneralSecurityException e) {
			throw new CryptoException("Password derivation failed for " + algorithm.identifier(), e);
		} finally {
			if (spec instanceof PBEKeySpec pbe) {
				pbe.clearPassword();
			}
		}
	}
	
	/**
	 * A parsed PHC string.<br>
	 *
	 * @author Luis-St
	 *
	 * @param algorithm The function and cost the record was produced with
	 * @param salt The salt the record was produced with
	 * @param hash The stored hash
	 */
	record Encoded(
		@NonNull PasswordAlgorithm algorithm,
		byte @NonNull [] salt,
		byte @NonNull [] hash
	) {
		
		/**
		 * Constructs a new parsed record.<br>
		 * @throws NullPointerException If the algorithm, the salt or the hash is null
		 */
		Encoded {
			Objects.requireNonNull(algorithm, "Algorithm must not be null");
			Objects.requireNonNull(salt, "Salt must not be null");
			Objects.requireNonNull(hash, "Hash must not be null");
		}
		
		/**
		 * Parses a PHC string.<br>
		 * <p>
		 *     The number of parameter sections differs between the functions, because the format gives the Argon2 version a section of its own,<br>
		 *     so everything between the identifier and the trailing salt and hash is handed to the function to read.
		 * </p>
		 * <p>
		 *     Every cost parameter is bounded by the constructor it flows into, so an untrusted record cannot make a later verification run arbitrarily long or allocate arbitrarily much.
		 * </p>
		 *
		 * @param encoded The PHC string to parse
		 * @return The parsed record
		 * @throws NullPointerException If the encoded hash is null
		 * @throws MalformedDataException If the string is not a well-formed record of a known function
		 */
		static @NonNull Encoded parse(@NonNull String encoded) {
			Objects.requireNonNull(encoded, "Encoded hash must not be null");
			
			String[] sections = encoded.split("\\$", -1);
			if (sections.length < 5 || !sections[0].isEmpty()) {
				throw new MalformedDataException("Malformed password hash, expected at least four dollar separated sections");
			}
			String identifier = sections[1];
			PasswordAlgorithm algorithm = PasswordAlgorithm.byIdentifier(identifier).orElseThrow(() -> new MalformedDataException("Unknown password algorithm '" + identifier + "'"));
			
			String[] parameters = Arrays.copyOfRange(sections, 2, sections.length - 2);
			try {
				byte[] salt = Base64.getUrlDecoder().decode(sections[sections.length - 2]);
				byte[] hash = Base64.getUrlDecoder().decode(sections[sections.length - 1]);
				
				if (hash.length < MIN_HASH_LENGTH || hash.length > MAX_HASH_LENGTH) {
					throw new MalformedDataException("Hash length must be in [" + MIN_HASH_LENGTH + ", " + MAX_HASH_LENGTH + "], was " + hash.length);
				}
				return new Encoded(algorithm.parseParameters(parameters), salt, hash);
			} catch (MalformedDataException e) {
				throw e;
			} catch (RuntimeException e) {
				throw new MalformedDataException("Malformed password hash for algorithm '" + identifier + "'", e);
			}
		}
	}
}
