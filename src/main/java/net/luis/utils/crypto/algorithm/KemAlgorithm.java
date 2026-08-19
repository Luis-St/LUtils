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
 * A key encapsulation mechanism.<br>
 * <p>
 *     Three shapes exist, and they behave differently enough that the difference is modeled in the type rather than in nullable carrier fields:<br>
 *     A native mechanism served by the JCA key encapsulation API, a Diffie-Hellman mechanism built from a key agreement, and a hybrid of one of each.
 * </p>
 * <p>
 *     A key encapsulation mechanism never takes a plaintext.<br>
 *     It produces a random shared secret plus an encapsulation that only the holder of the private key can turn back into that secret,<br>
 *     which is what replaces encrypting to a public key directly.
 * </p>
 *
 * @see NativeKemAlgorithm
 * @see DhKemAlgorithm
 * @see HybridKemAlgorithm
 *
 * @author Luis-St
 */
public sealed interface KemAlgorithm permits NativeKemAlgorithm, DhKemAlgorithm, HybridKemAlgorithm {
	
	/**
	 * ML-KEM at the 512 parameter set, the smallest of the standardized lattice mechanisms.<br>
	 */
	NativeKemAlgorithm ML_KEM_512 = new NativeKemAlgorithm("ML-KEM-512", "ML-KEM", "ML-KEM-512", 768, 822, 32);
	/**
	 * ML-KEM at the 768 parameter set, the recommended post-quantum default.<br>
	 */
	NativeKemAlgorithm ML_KEM_768 = new NativeKemAlgorithm("ML-KEM-768", "ML-KEM", "ML-KEM-768", 1088, 1206, 32);
	/**
	 * ML-KEM at the 1024 parameter set, for the highest security category.<br>
	 */
	NativeKemAlgorithm ML_KEM_1024 = new NativeKemAlgorithm("ML-KEM-1024", "ML-KEM", "ML-KEM-1024", 1568, 1590, 32);
	
	/**
	 * X25519 used as a Diffie-Hellman key encapsulation mechanism, in the RFC 9180 DHKEM construction.<br>
	 */
	DhKemAlgorithm X25519 = new DhKemAlgorithm("X25519", 32, 32);
	/**
	 * X448 used as a Diffie-Hellman key encapsulation mechanism, in the RFC 9180 DHKEM construction.<br>
	 * Its shared secret is 64 bytes rather than the 56 of its public key, because RFC 9180 pairs this curve with HKDF-SHA-512.<br>
	 */
	DhKemAlgorithm X448 = new DhKemAlgorithm("X448", 56, 64);
	
	/**
	 * X25519 combined with ML-KEM-768, the recommended hybrid.<br>
	 */
	HybridKemAlgorithm X25519_ML_KEM_768 = new HybridKemAlgorithm(X25519, ML_KEM_768);
	/**
	 * X448 combined with ML-KEM-1024, for the highest security category.<br>
	 */
	HybridKemAlgorithm X448_ML_KEM_1024 = new HybridKemAlgorithm(X448, ML_KEM_1024);
	
	/**
	 * Every mechanism this library knows, in a stable order.<br>
	 */
	List<KemAlgorithm> VALUES = List.of(ML_KEM_512, ML_KEM_768, ML_KEM_1024, X25519, X448, X25519_ML_KEM_768, X448_ML_KEM_1024);
	
	/**
	 * Looks up a mechanism by its name.<br>
	 *
	 * @param name The name to look up
	 * @return The mechanism with the given name, or empty if there is none
	 * @throws NullPointerException If the name is null
	 */
	static @NonNull Optional<KemAlgorithm> byName(@NonNull String name) {
		Objects.requireNonNull(name, "Name must not be null");
		return VALUES.stream().filter(algorithm -> algorithm.name().equals(name)).findFirst();
	}
	
	/**
	 * Returns the name of this mechanism as it appears in this library.<br>
	 * @return The name
	 */
	@NonNull String name();
	
	/**
	 * Returns the length of an encapsulation in bytes.<br>
	 * This is a fixed length, and an encapsulation of any other length is rejected before use.<br>
	 *
	 * @return The encapsulation length
	 */
	int encapsulationLength();
	
	/**
	 * Returns the length of a public key in bytes.<br>
	 * <p>
	 *     This is the encoded length for the native mechanisms and the raw u-coordinate length for the Diffie-Hellman ones,<br>
	 *     matching what each actually puts on the wire.<br>
	 *     It is informational, nothing in this library validates a key against it.
	 * </p>
	 *
	 * @return The public key length
	 */
	int publicKeyLength();
	
	/**
	 * Returns the length of the shared secret in bytes.<br>
	 * @return The shared secret length
	 */
	int sharedSecretLength();
	
	/**
	 * Returns whether this mechanism resists an attacker with a quantum computer.<br>
	 * A hybrid counts as post-quantum, since its post-quantum half has to be broken as well.<br>
	 *
	 * @return True if the mechanism is post-quantum
	 */
	boolean isPostQuantum();
}
