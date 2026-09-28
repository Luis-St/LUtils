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
import org.jspecify.annotations.Nullable;

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.util.Objects;

/**
 * A signature scheme served directly by a JCA signature implementation.<br>
 * <p>
 *     The curve is only set for the elliptic curve schemes, where the key pair generator needs it to know which curve to generate on.<br>
 *     The other schemes carry their parameters in their name.
 * </p>
 *
 * @author Luis-St
 *
 * @param name The name of this algorithm as it appears in this library
 * @param jcaName The JCA name of the signature serving this algorithm
 * @param keyJcaName The JCA name used to generate and decode keys of this algorithm
 * @param curve The name of the curve to generate keys on, or null if the algorithm needs none
 * @param publicKeyLength The length of an encoded public key in bytes
 * @param isPostQuantum Whether this algorithm resists an attacker with a quantum computer
 * @param requiresBouncyCastle Whether this algorithm needs BouncyCastle to be installed
 */
public record NativeSignatureAlgorithm(
	@NonNull String name,
	@NonNull String jcaName,
	@NonNull String keyJcaName,
	@Nullable String curve,
	int publicKeyLength,
	boolean isPostQuantum,
	boolean requiresBouncyCastle
) implements SignatureAlgorithm {
	
	/**
	 * Constructs a new native signature algorithm.<br>
	 * @throws NullPointerException If the name, the jca name or the key jca name is null
	 */
	public NativeSignatureAlgorithm {
		Objects.requireNonNull(name, "Name must not be null");
		Objects.requireNonNull(jcaName, "Jca name must not be null");
		Objects.requireNonNull(keyJcaName, "Key jca name must not be null");
	}
	
	/**
	 * Returns the parameter spec a key pair generator has to be initialised with.<br>
	 * @return The parameter spec, or null if the generator needs none
	 */
	public @Nullable AlgorithmParameterSpec keySpec() {
		return this.curve == null ? null : new ECGenParameterSpec(this.curve);
	}
}
