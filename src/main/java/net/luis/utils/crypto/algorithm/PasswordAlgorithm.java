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

package net.luis.utils.crypto.algorithm;

import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * A password hashing function together with the cost it is used at.<br>
 * <p>
 *     Three shapes exist, and each one takes a different set of cost parameters,<br>
 *     so the difference is modeled in the type rather than in nullable carrier fields.<br>
 *     An instance is therefore not just an identity, it is a complete configuration that a record can be produced with or read back into.
 * </p>
 * <p>
 *     Every cost parameter is bounded by the record that carries it, which is what stops a stored record from deciding how much memory and time a later verification spends.<br>
 *     Parsing an untrusted record goes through the same constructors as building one by hand, so the bounds cannot be bypassed.
 * </p>
 *
 * @see Argon2PasswordAlgorithm
 * @see ScryptPasswordAlgorithm
 * @see Pbkdf2PasswordAlgorithm
 *
 * @author Luis-St
 */
public sealed interface PasswordAlgorithm permits Argon2PasswordAlgorithm, ScryptPasswordAlgorithm, Pbkdf2PasswordAlgorithm {
	
	/**
	 * Argon2id at 64 mebibytes, three passes and four lanes.<br>
	 * <p>
	 *     This is the second recommended option of RFC 9106, meant for the case where the first one does not fit in memory.<br>
	 *     It costs roughly a quarter of a second per derivation on ordinary hardware.
	 * </p>
	 */
	Argon2PasswordAlgorithm ARGON2ID = new Argon2PasswordAlgorithm(65_536, 3, 4);
	/**
	 * Scrypt at a cost of two to the sixteenth, a block size of eight and no parallelism.<br>
	 * This allocates 64 mebibytes per derivation, matching what the Argon2id default costs.<br>
	 */
	ScryptPasswordAlgorithm SCRYPT = new ScryptPasswordAlgorithm(1 << 16, 8, 1);
	/**
	 * PBKDF2 over HMAC-SHA-512 at 210000 iterations, which is the OWASP figure for that construction.<br>
	 */
	Pbkdf2PasswordAlgorithm PBKDF2_HMAC_SHA_512 = new Pbkdf2PasswordAlgorithm(210_000);
	
	/**
	 * Every function this library knows at its default cost, strongest first.<br>
	 */
	List<PasswordAlgorithm> VALUES = List.of(ARGON2ID, SCRYPT, PBKDF2_HMAC_SHA_512);
	
	/**
	 * Looks up a function by the identifier from an encoded hash.<br>
	 * The returned instance carries the default cost, not the cost of any particular record.<br>
	 *
	 * @param identifier The identifier to look up
	 * @return The function with the given identifier, or empty if there is none
	 * @throws NullPointerException If the identifier is null
	 */
	static @NonNull Optional<PasswordAlgorithm> byIdentifier(@NonNull String identifier) {
		Objects.requireNonNull(identifier, "Identifier must not be null");
		return VALUES.stream().filter(algorithm -> algorithm.identifier().equals(identifier)).findFirst();
	}
	
	/**
	 * Reads a single named integer parameter such as the m of an Argon2id record.<br>
	 * The name is checked rather than skipped, so a record that names its parameters in the wrong order is rejected instead of being read as something else.<br>
	 *
	 * @param parameter The parameter to read, in its name equals value form
	 * @param name The name the parameter has to carry
	 * @return The read value
	 * @throws NullPointerException If the parameter or the name is null
	 * @throws IllegalArgumentException If the parameter does not carry the given name, or its value is not an integer
	 */
	static int readInt(@NonNull String parameter, @NonNull String name) {
		Objects.requireNonNull(parameter, "Parameter must not be null");
		Objects.requireNonNull(name, "Name must not be null");
		
		String prefix = name + "=";
		if (!parameter.startsWith(prefix)) {
			throw new IllegalArgumentException("Expected parameter '" + name + "', got '" + parameter + "'");
		}
		
		try {
			return Integer.parseInt(parameter.substring(prefix.length()));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Parameter '" + name + "' is not an integer in '" + parameter + "'", e);
		}
	}
	
	/**
	 * Returns the identifier this function carries in an encoded hash.<br>
	 * @return The identifier
	 */
	@NonNull String identifier();
	
	/**
	 * Returns the JCA name of the secret key factory serving this function.<br>
	 * @return The JCA name
	 */
	@NonNull String jcaName();
	
	/**
	 * Returns whether this function needs BouncyCastle to be installed.<br>
	 * Only PBKDF2 is served by the JDK on its own.<br>
	 *
	 * @return True if BouncyCastle is required
	 */
	boolean requiresBouncyCastle();
	
	/**
	 * Returns the cost parameters of this function as they appear in an encoded hash.<br>
	 * <p>
	 *     The result may itself contain a dollar sign, because the PHC format gives the Argon2 version a section of its own.<br>
	 *     It is the inverse of {@link #parseParameters(String[])}.
	 * </p>
	 *
	 * @return The encoded cost parameters
	 */
	@NonNull String encodeParameters();
	
	/**
	 * Returns whether a record produced with this configuration should be replaced by one produced with the given configuration.<br>
	 * <p>
	 *     A configuration of a different function is compared by how strong the two functions are rather than by their parameters,<br>
	 *     because there is no meaningful ordering between a memory cost and an iteration count.
	 * </p>
	 *
	 * @param current The configuration new records are produced with
	 * @return True if this configuration is the weaker one
	 * @throws NullPointerException If the current configuration is null
	 */
	boolean isWeakerThan(@NonNull PasswordAlgorithm current);
	
	/**
	 * Reads the cost parameters of an encoded hash into a configuration of this function.<br>
	 * <p>
	 *     This is called on the constant found by identifier and returns a new instance carrying the record's own cost.<br>
	 *     Every value passes through the bounds of the constructor, so a record cannot ask for more work than the bounds allow.
	 * </p>
	 *
	 * @param parameters The dollar separated parameter sections of the record
	 * @return The configuration the record was produced with
	 * @throws NullPointerException If the parameters are null
	 * @throws IllegalArgumentException If the sections are not the ones this function writes, or a value is outside the accepted range
	 */
	@NonNull PasswordAlgorithm parseParameters(@NonNull String @NonNull [] parameters);
}
