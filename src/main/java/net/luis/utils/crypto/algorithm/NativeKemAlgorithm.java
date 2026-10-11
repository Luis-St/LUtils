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

import java.util.Objects;

/**
 * A key encapsulation mechanism served natively by the JCA key encapsulation API.<br>
 * <p>
 *     The JCA serves every parameter set of a family through one mechanism name, while the key pair generator and key factory use the sized name.<br>
 *     Both are carried here, because they genuinely differ.
 * </p>
 *
 * @author Luis-St
 *
 * @param name The name of this algorithm as it appears in this library
 * @param jcaName The JCA name of the mechanism serving this algorithm
 * @param keyJcaName The JCA name used to generate and decode keys of this algorithm
 * @param encapsulationLength The length of an encapsulation in bytes
 * @param publicKeyLength The length of an encoded public key in bytes
 * @param sharedSecretLength The length of the shared secret in bytes
 */
public record NativeKemAlgorithm(
	@NonNull String name,
	@NonNull String jcaName,
	@NonNull String keyJcaName,
	int encapsulationLength,
	int publicKeyLength,
	int sharedSecretLength
) implements KemAlgorithm {
	
	/**
	 * Constructs a new native kem algorithm.<br>
	 * @throws NullPointerException If the name, the jca name or the key jca name is null
	 */
	public NativeKemAlgorithm {
		Objects.requireNonNull(name, "Name must not be null");
		Objects.requireNonNull(jcaName, "Jca name must not be null");
		Objects.requireNonNull(keyJcaName, "Key jca name must not be null");
	}
	
	@Override
	public boolean isPostQuantum() {
		return true;
	}
}
