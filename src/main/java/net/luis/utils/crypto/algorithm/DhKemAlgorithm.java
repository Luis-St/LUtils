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
 * The RFC 9180 DHKEM over X25519 or X448, served by the JDK key encapsulation API.<br>
 * <p>
 *     Encapsulation generates an ephemeral key pair, agrees against the recipient,<br>
 *     and derives the shared secret from the agreement output bound to both public keys.<br>
 *     All of that happens inside the provider, so this record carries only the name and the two lengths.
 * </p>
 * <p>
 *     One JCA name covers both curves and the provider reads which one to use off the key,<br>
 *     which is why {@link #jcaName()} is the same for every constant while {@link #keyJcaName()} is not.
 * </p>
 *
 * @author Luis-St
 *
 * @param name The name of this algorithm as it appears in this library, which is also the JCA name of its keys
 * @param publicKeyLength The length of a raw u-coordinate in bytes, which is also the wire length of a public key
 * @param sharedSecretLength The length of the derived shared secret in bytes
 */
public record DhKemAlgorithm(
	@NonNull String name,
	int publicKeyLength,
	int sharedSecretLength
) implements KemAlgorithm {
	
	/**
	 * The JCA name of the key encapsulation mechanism serving every constant of this shape.<br>
	 */
	private static final String JCA_NAME = "DHKEM";
	
	/**
	 * Constructs a new dh kem algorithm.<br>
	 * @throws NullPointerException If the name is null
	 */
	public DhKemAlgorithm {
		Objects.requireNonNull(name, "Name must not be null");
	}
	
	/**
	 * Returns the JCA name of the key encapsulation mechanism serving this algorithm.<br>
	 * @return The JCA name
	 */
	public @NonNull String jcaName() {
		return JCA_NAME;
	}
	
	/**
	 * Returns the JCA name used to generate and decode keys of this algorithm.<br>
	 * @return The JCA key name
	 */
	public @NonNull String keyJcaName() {
		return this.name;
	}
	
	@Override
	public int encapsulationLength() {
		return this.publicKeyLength;
	}
	
	@Override
	public boolean isPostQuantum() {
		return false;
	}
}
