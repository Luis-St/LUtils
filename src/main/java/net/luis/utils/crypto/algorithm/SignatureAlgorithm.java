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
 * A digital signature scheme.<br>
 * <p>
 *     Two shapes exist: a scheme served directly by a JCA signature implementation, and a hybrid of one classical and one post-quantum scheme.<br>
 *     Unlike a message authentication code, a signature can be verified by parties that cannot produce one.
 * </p>
 * <p>
 *     A classical scheme can be combined with a post-quantum one in two ways, and the choice is about who has to read the result.<br>
 *     A {@link HybridSignatureAlgorithm} runs the two components side by side and joins them with this library's own length-prefixed encoding, which nothing outside this library can parse.<br>
 *     The composite constants are the IETF LAMPS schemes instead, where one registered algorithm name covers the pair and carries a defined wire encoding and an object identifier.<br>
 *     Pick a composite when the signature leaves this library, and a hybrid when it does not and the freedom to pair any two components matters more.
 * </p>
 * <p>
 *     RSA, DSA and PKCS#1 v1.5 are deliberately absent.<br>
 *     SLH-DSA is modeled in both of its parameter set families.<br>
 *     The small sets sign slowly and produce the shorter signature, the fast sets sign quickly and produce a signature several times the size.<br>
 *     Prefer a small set unless signing throughput is the binding constraint, since a fast signature runs to tens of kilobytes.
 * </p>
 *
 * @see NativeSignatureAlgorithm
 * @see HybridSignatureAlgorithm
 *
 * @author Luis-St
 */
public sealed interface SignatureAlgorithm permits NativeSignatureAlgorithm, HybridSignatureAlgorithm {
	
	/**
	 * Ed25519, the classical default.<br>
	 */
	NativeSignatureAlgorithm ED25519 = new NativeSignatureAlgorithm("Ed25519", "Ed25519", "Ed25519", null, 44, false, false);
	/**
	 * Ed448, at a higher security level than Ed25519.<br>
	 */
	NativeSignatureAlgorithm ED448 = new NativeSignatureAlgorithm("Ed448", "Ed448", "Ed448", null, 69, false, false);
	/**
	 * ECDSA over P-256 with SHA-256, for interoperability with existing systems.<br>
	 */
	NativeSignatureAlgorithm ECDSA_P256_SHA_256 = new NativeSignatureAlgorithm("ECDSA-P256-SHA256", "SHA256withECDSA", "EC", "secp256r1", 91, false, false);
	/**
	 * ECDSA over P-384 with SHA-384, for interoperability with existing systems.<br>
	 */
	NativeSignatureAlgorithm ECDSA_P384_SHA_384 = new NativeSignatureAlgorithm("ECDSA-P384-SHA384", "SHA384withECDSA", "EC", "secp384r1", 120, false, false);
	/**
	 * ECDSA over P-521 with SHA-512, for interoperability with existing systems.<br>
	 */
	NativeSignatureAlgorithm ECDSA_P521_SHA_512 = new NativeSignatureAlgorithm("ECDSA-P521-SHA512", "SHA512withECDSA", "EC", "secp521r1", 158, false, false);
	
	/**
	 * ML-DSA at the 44 parameter set, the smallest of the standardised lattice signatures.<br>
	 */
	NativeSignatureAlgorithm ML_DSA_44 = new NativeSignatureAlgorithm("ML-DSA-44", "ML-DSA-44", "ML-DSA-44", null, 1334, true, false);
	/**
	 * ML-DSA at the 65 parameter set, the recommended post-quantum default.<br>
	 */
	NativeSignatureAlgorithm ML_DSA_65 = new NativeSignatureAlgorithm("ML-DSA-65", "ML-DSA-65", "ML-DSA-65", null, 1974, true, false);
	/**
	 * ML-DSA at the 87 parameter set, for the highest security category.<br>
	 */
	NativeSignatureAlgorithm ML_DSA_87 = new NativeSignatureAlgorithm("ML-DSA-87", "ML-DSA-87", "ML-DSA-87", null, 2614, true, false);
	
	/**
	 * SLH-DSA over SHA-2 at the 128 bit small parameter set, a hash based alternative to ML-DSA.<br>
	 */
	NativeSignatureAlgorithm SLH_DSA_SHA2_128S = new NativeSignatureAlgorithm("SLH-DSA-SHA2-128S", "SLH-DSA-SHA2-128S", "SLH-DSA-SHA2-128S", null, 50, true, true);
	/**
	 * SLH-DSA over SHA-2 at the 192 bit small parameter set.<br>
	 */
	NativeSignatureAlgorithm SLH_DSA_SHA2_192S = new NativeSignatureAlgorithm("SLH-DSA-SHA2-192S", "SLH-DSA-SHA2-192S", "SLH-DSA-SHA2-192S", null, 66, true, true);
	/**
	 * SLH-DSA over SHA-2 at the 256 bit small parameter set.<br>
	 */
	NativeSignatureAlgorithm SLH_DSA_SHA2_256S = new NativeSignatureAlgorithm("SLH-DSA-SHA2-256S", "SLH-DSA-SHA2-256S", "SLH-DSA-SHA2-256S", null, 82, true, true);
	/**
	 * SLH-DSA over SHAKE at the 128 bit small parameter set.<br>
	 */
	NativeSignatureAlgorithm SLH_DSA_SHAKE_128S = new NativeSignatureAlgorithm("SLH-DSA-SHAKE-128S", "SLH-DSA-SHAKE-128S", "SLH-DSA-SHAKE-128S", null, 50, true, true);
	/**
	 * SLH-DSA over SHAKE at the 192 bit small parameter set.<br>
	 */
	NativeSignatureAlgorithm SLH_DSA_SHAKE_192S = new NativeSignatureAlgorithm("SLH-DSA-SHAKE-192S", "SLH-DSA-SHAKE-192S", "SLH-DSA-SHAKE-192S", null, 66, true, true);
	/**
	 * SLH-DSA over SHAKE at the 256 bit small parameter set.<br>
	 */
	NativeSignatureAlgorithm SLH_DSA_SHAKE_256S = new NativeSignatureAlgorithm("SLH-DSA-SHAKE-256S", "SLH-DSA-SHAKE-256S", "SLH-DSA-SHAKE-256S", null, 82, true, true);
	
	/**
	 * SLH-DSA over SHA-2 at the 128 bit fast parameter set, signing faster than the small set at several times the signature size.<br>
	 */
	NativeSignatureAlgorithm SLH_DSA_SHA2_128F = new NativeSignatureAlgorithm("SLH-DSA-SHA2-128F", "SLH-DSA-SHA2-128F", "SLH-DSA-SHA2-128F", null, 50, true, true);
	/**
	 * SLH-DSA over SHA-2 at the 192 bit fast parameter set.<br>
	 */
	NativeSignatureAlgorithm SLH_DSA_SHA2_192F = new NativeSignatureAlgorithm("SLH-DSA-SHA2-192F", "SLH-DSA-SHA2-192F", "SLH-DSA-SHA2-192F", null, 66, true, true);
	/**
	 * SLH-DSA over SHA-2 at the 256 bit fast parameter set.<br>
	 */
	NativeSignatureAlgorithm SLH_DSA_SHA2_256F = new NativeSignatureAlgorithm("SLH-DSA-SHA2-256F", "SLH-DSA-SHA2-256F", "SLH-DSA-SHA2-256F", null, 82, true, true);
	/**
	 * SLH-DSA over SHAKE at the 128 bit fast parameter set.<br>
	 */
	NativeSignatureAlgorithm SLH_DSA_SHAKE_128F = new NativeSignatureAlgorithm("SLH-DSA-SHAKE-128F", "SLH-DSA-SHAKE-128F", "SLH-DSA-SHAKE-128F", null, 50, true, true);
	/**
	 * SLH-DSA over SHAKE at the 192 bit fast parameter set.<br>
	 */
	NativeSignatureAlgorithm SLH_DSA_SHAKE_192F = new NativeSignatureAlgorithm("SLH-DSA-SHAKE-192F", "SLH-DSA-SHAKE-192F", "SLH-DSA-SHAKE-192F", null, 66, true, true);
	/**
	 * SLH-DSA over SHAKE at the 256 bit fast parameter set.<br>
	 */
	NativeSignatureAlgorithm SLH_DSA_SHAKE_256F = new NativeSignatureAlgorithm("SLH-DSA-SHAKE-256F", "SLH-DSA-SHAKE-256F", "SLH-DSA-SHAKE-256F", null, 82, true, true);
	
	/**
	 * Ed25519 combined with ML-DSA-65, the recommended hybrid.<br>
	 */
	HybridSignatureAlgorithm ED25519_ML_DSA_65 = new HybridSignatureAlgorithm(ED25519, ML_DSA_65);
	/**
	 * ECDSA over P-256 combined with ML-DSA-44, for systems that need an elliptic curve half.<br>
	 */
	HybridSignatureAlgorithm ECDSA_P256_ML_DSA_44 = new HybridSignatureAlgorithm(ECDSA_P256_SHA_256, ML_DSA_44);
	/**
	 * ECDSA over P-384 combined with ML-DSA-65, at a higher security level.<br>
	 */
	HybridSignatureAlgorithm ECDSA_P384_ML_DSA_65 = new HybridSignatureAlgorithm(ECDSA_P384_SHA_384, ML_DSA_65);
	/**
	 * Ed448 combined with ML-DSA-87, the highest security level this library models.<br>
	 */
	HybridSignatureAlgorithm ED448_ML_DSA_87 = new HybridSignatureAlgorithm(ED448, ML_DSA_87);
	/**
	 * ECDSA over P-521 combined with ML-DSA-87, for systems that need an elliptic curve half at the highest security level.<br>
	 */
	HybridSignatureAlgorithm ECDSA_P521_ML_DSA_87 = new HybridSignatureAlgorithm(ECDSA_P521_SHA_512, ML_DSA_87);
	/**
	 * Ed25519 combined with SLH-DSA over SHA-2 at the 128 bit small parameter set.<br>
	 * <p>
	 *     The post-quantum half is hash-based rather than lattice-based, so this is the pairing that survives a break of the lattice assumption the ML-DSA hybrids rest on.<br>
	 *     It signs slowly and produces a signature just under eight kilobytes, which is why it is a deliberate choice rather than the default.
	 * </p>
	 */
	HybridSignatureAlgorithm ED25519_SLH_DSA_SHA2_128S = new HybridSignatureAlgorithm(ED25519, SLH_DSA_SHA2_128S);
	
	/**
	 * ML-DSA-44 composed with ECDSA over P-256, in the IETF LAMPS encoding.<br>
	 */
	NativeSignatureAlgorithm COMPOSITE_ML_DSA_44_ECDSA_P256 = new NativeSignatureAlgorithm("MLDSA44-ECDSA-P256-SHA256", "MLDSA44-ECDSA-P256-SHA256", "MLDSA44-ECDSA-P256-SHA256", null, 1398, true, true);
	/**
	 * ML-DSA-65 composed with Ed25519, in the IETF LAMPS encoding.<br>
	 * This is the composite counterpart of {@link #ED25519_ML_DSA_65} and the one to reach for first.<br>
	 */
	NativeSignatureAlgorithm COMPOSITE_ML_DSA_65_ED25519 = new NativeSignatureAlgorithm("MLDSA65-Ed25519-SHA512", "MLDSA65-Ed25519-SHA512", "MLDSA65-Ed25519-SHA512", null, 2005, true, true);
	/**
	 * ML-DSA-65 composed with ECDSA over P-384, in the IETF LAMPS encoding.<br>
	 */
	NativeSignatureAlgorithm COMPOSITE_ML_DSA_65_ECDSA_P384 = new NativeSignatureAlgorithm("MLDSA65-ECDSA-P384-SHA512", "MLDSA65-ECDSA-P384-SHA512", "MLDSA65-ECDSA-P384-SHA512", null, 2070, true, true);
	/**
	 * ML-DSA-87 composed with Ed448, in the IETF LAMPS encoding, at the highest security category.<br>
	 */
	NativeSignatureAlgorithm COMPOSITE_ML_DSA_87_ED448 = new NativeSignatureAlgorithm("MLDSA87-Ed448-SHAKE256", "MLDSA87-Ed448-SHAKE256", "MLDSA87-Ed448-SHAKE256", null, 2670, true, true);
	/**
	 * ML-DSA-87 composed with ECDSA over P-521, in the IETF LAMPS encoding, at the highest security category.<br>
	 */
	NativeSignatureAlgorithm COMPOSITE_ML_DSA_87_ECDSA_P521 = new NativeSignatureAlgorithm("MLDSA87-ECDSA-P521-SHA512", "MLDSA87-ECDSA-P521-SHA512", "MLDSA87-ECDSA-P521-SHA512", null, 2746, true, true);
	
	/**
	 * Every scheme this library knows, in a stable order.<br>
	 */
	List<SignatureAlgorithm> VALUES = List.of(
		ED25519, ED448, ECDSA_P256_SHA_256, ECDSA_P384_SHA_384, ECDSA_P521_SHA_512,
		ML_DSA_44, ML_DSA_65, ML_DSA_87,
		SLH_DSA_SHA2_128S, SLH_DSA_SHA2_192S, SLH_DSA_SHA2_256S, SLH_DSA_SHAKE_128S, SLH_DSA_SHAKE_192S, SLH_DSA_SHAKE_256S,
		SLH_DSA_SHA2_128F, SLH_DSA_SHA2_192F, SLH_DSA_SHA2_256F, SLH_DSA_SHAKE_128F, SLH_DSA_SHAKE_192F, SLH_DSA_SHAKE_256F,
		ED25519_ML_DSA_65, ECDSA_P256_ML_DSA_44, ECDSA_P384_ML_DSA_65, ED448_ML_DSA_87, ECDSA_P521_ML_DSA_87, ED25519_SLH_DSA_SHA2_128S,
		COMPOSITE_ML_DSA_44_ECDSA_P256, COMPOSITE_ML_DSA_65_ED25519, COMPOSITE_ML_DSA_65_ECDSA_P384, COMPOSITE_ML_DSA_87_ED448, COMPOSITE_ML_DSA_87_ECDSA_P521);
	
	/**
	 * Looks up a scheme by its name.<br>
	 *
	 * @param name The name to look up
	 * @return The scheme with the given name, or empty if there is none
	 * @throws NullPointerException If the name is null
	 */
	static @NonNull Optional<SignatureAlgorithm> byName(@NonNull String name) {
		Objects.requireNonNull(name, "Name must not be null");
		return VALUES.stream().filter(algorithm -> algorithm.name().equals(name)).findFirst();
	}
	
	/**
	 * Returns the name of this scheme as it appears in this library.<br>
	 * @return The name
	 */
	@NonNull String name();
	
	/**
	 * Returns the length of an encoded public key in bytes.<br>
	 * This is informational.<br>
	 * Nothing in this library validates a key against it.<br>
	 *
	 * @return The public key length
	 */
	int publicKeyLength();
	
	/**
	 * Returns whether this scheme resists an attacker with a quantum computer.<br>
	 * A hybrid counts as post-quantum, since its post-quantum half has to be broken as well.<br>
	 *
	 * @return True if the scheme is post-quantum
	 */
	boolean isPostQuantum();
	
	/**
	 * Returns whether this scheme needs BouncyCastle to be installed.<br>
	 * @return True if BouncyCastle is required
	 */
	boolean requiresBouncyCastle();
}
