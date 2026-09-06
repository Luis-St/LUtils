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
import net.luis.utils.crypto.exception.MalformedDataException;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * A named, versioned bundle of primitives with a 16-bit id.<br>
 * <p>
 *     The id goes into every artifact this library writes, so the write path can move to a new default without invalidating anything already written.<br>
 *     A reader looks the suite up by id and uses the primitives the artifact was actually written with.
 * </p>
 * <p>
 *     To migrate, change what {@link #current()} returns.<br>
 *     Call sites do not name a suite at all unless they have a reason to.
 * </p>
 *
 * @author Luis-St
 *
 * @param id The 16-bit identifier written into every artifact
 * @param name The human-readable name of this suite
 * @param aead The authenticated cipher of this suite
 * @param kem The key encapsulation mechanism of this suite
 * @param signature The signature scheme of this suite
 * @param kdf The key derivation function of this suite
 * @param hash The hash function of this suite
 * @param deprecated Whether this suite should no longer be used for new artifacts
 */
public record CryptoSuite(
	short id,
	@NonNull String name,
	@NonNull AeadAlgorithm aead,
	@NonNull KemAlgorithm kem,
	@NonNull SignatureAlgorithm signature,
	@NonNull KdfAlgorithm kdf,
	@NonNull HashAlgorithm hash,
	boolean deprecated
) {
	
	/**
	 * Every registered suite, by id, in registration order.<br>
	 * Declared before the constants below, because they populate it as they are initialized.<br>
	 */
	private static final Map<Short, CryptoSuite> REGISTRY = new LinkedHashMap<>();
	
	/**
	 * Classical-only.<br>
	 * Kept solely so pre-migration artifacts stay readable.<br>
	 */
	public static final CryptoSuite CLASSICAL_V1 = register(
		new CryptoSuite((short) 1, "classical-v1", AeadAlgorithm.AES_256_GCM, KemAlgorithm.X25519, SignatureAlgorithm.ED25519, KdfAlgorithm.HKDF_SHA_256, HashAlgorithm.SHA_256, true)
	);
	
	/**
	 * Deploy this today: secure if either the classical or the post-quantum half holds.<br>
	 */
	public static final CryptoSuite HYBRID_V1 = register(
		new CryptoSuite((short) 2, "hybrid-v1", AeadAlgorithm.AES_256_GCM, KemAlgorithm.X25519_ML_KEM_768, SignatureAlgorithm.ED25519_ML_DSA_65, KdfAlgorithm.HKDF_SHA_256, HashAlgorithm.SHA_256, false)
	);
	
	/**
	 * The endpoint of the migration.<br>
	 * Switch the default here once peers are ready.<br>
	 */
	public static final CryptoSuite POST_QUANTUM_V1 = register(
		new CryptoSuite((short) 3, "post-quantum-v1", AeadAlgorithm.AES_256_GCM, KemAlgorithm.ML_KEM_768, SignatureAlgorithm.ML_DSA_65, KdfAlgorithm.HKDF_SHA_256, HashAlgorithm.SHA_256, false)
	);
	
	/**
	 * Constructs a new crypto suite.<br>
	 * @throws NullPointerException If the name or any of the algorithms is null
	 */
	public CryptoSuite {
		Objects.requireNonNull(name, "Name must not be null");
		Objects.requireNonNull(aead, "Aead algorithm must not be null");
		Objects.requireNonNull(kem, "Kem algorithm must not be null");
		Objects.requireNonNull(signature, "Signature algorithm must not be null");
		Objects.requireNonNull(kdf, "Kdf algorithm must not be null");
		Objects.requireNonNull(hash, "Hash algorithm must not be null");
	}
	
	/**
	 * Registers the given suite so it can be looked up by its id.<br>
	 *
	 * @param suite The suite to register
	 * @return The registered suite
	 * @throws NullPointerException If the suite is null
	 * @throws IllegalStateException If another suite is already registered under the same id
	 */
	private static @NonNull CryptoSuite register(@NonNull CryptoSuite suite) {
		Objects.requireNonNull(suite, "Suite must not be null");
		
		CryptoSuite previous = REGISTRY.put(suite.id(), suite);
		if (previous != null) {
			throw new IllegalStateException("Duplicate crypto suite id " + suite.id());
		}
		return suite;
	}
	
	/**
	 * Returns the suite used for everything newly written.<br>
	 * Change this one line to migrate, not the call sites.<br>
	 *
	 * @return The current default suite
	 */
	public static @NonNull CryptoSuite current() {
		return HYBRID_V1;
	}
	
	/**
	 * Looks up a suite by the id carried in an artifact.<br>
	 *
	 * @param id The id to look up
	 * @return The suite with the given id
	 * @throws MalformedDataException If no suite is registered under the given id
	 */
	public static @NonNull CryptoSuite byId(short id) {
		CryptoSuite suite = REGISTRY.get(id);
		if (suite == null) {
			throw new MalformedDataException("Unknown crypto suite id " + id + " - the artifact was written by a newer version of this library");
		}
		return suite;
	}
	
	/**
	 * Looks up a suite by its name.<br>
	 *
	 * @param name The name to look up
	 * @return The suite with the given name, or empty if there is none
	 * @throws NullPointerException If the name is null
	 */
	public static @NonNull Optional<CryptoSuite> byName(@NonNull String name) {
		Objects.requireNonNull(name, "Name must not be null");
		return REGISTRY.values().stream().filter(suite -> suite.name().equals(name)).findFirst();
	}
	
	/**
	 * Returns every registered suite, in registration order.<br>
	 * @return Every registered suite
	 */
	public static @NonNull @Unmodifiable Collection<CryptoSuite> values() {
		return Collections.unmodifiableCollection(REGISTRY.values());
	}
	
	/**
	 * Returns whether this suite should no longer be used for new artifacts.<br>
	 * Existing artifacts written with it stay readable.<br>
	 *
	 * @return True if this suite is deprecated
	 */
	public boolean isDeprecated() {
		return this.deprecated;
	}
	
	/**
	 * Returns whether every algorithm of this suite is served by a registered provider.<br>
	 * @return True if the whole suite is usable on this runtime
	 */
	public boolean isSupported() {
		return Providers.supports(this.aead) && Providers.supports(this.kem) && Providers.supports(this.signature) && Providers.supports(this.kdf) && Providers.supports(this.hash);
	}
	
	@Override
	public @NonNull String toString() {
		return this.name + "(" + this.id + ")";
	}
}
