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
import net.luis.utils.crypto.exception.UnsupportedAlgorithmException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jspecify.annotations.NonNull;

import javax.crypto.KEM;
import java.security.*;
import java.util.*;

/**
 * The single place that knows what the runtime can actually do.<br>
 * <p>
 *     Availability is answered with a boolean rather than by catching an exception, so a legitimate "no" never travels as a failure.<br>
 *     Installing a provider mutates JVM-global state and is therefore always an explicit call, never a static initializer side effect.
 * </p>
 * <p>
 *     Example:
 * </p>
 * <pre>{@code
 * // Once at startup, before anything that needs BouncyCastle runs
 * Providers.installBouncyCastle();
 * Providers.require(CryptoSuite.current());
 * Providers.logProviders(CryptoSuite.current());
 *
 * // Asking rather than catching, so a legitimate no never travels as a failure
 * if (Providers.supports(SignatureAlgorithm.ML_DSA_65)) {
 *     signWith(SignatureAlgorithm.ML_DSA_65);
 * }
 * }</pre>
 *
 * @author Luis-St
 */
public final class Providers {
	
	/**
	 * The logger of this class.<br>
	 */
	private static final Logger LOGGER = LogManager.getLogger(Providers.class);
	/**
	 * The name BouncyCastle registers itself under.<br>
	 */
	private static final String BOUNCY_CASTLE_NAME = "BC";
	/**
	 * The module name of the BouncyCastle provider.<br>
	 */
	private static final String BOUNCY_CASTLE_MODULE = "org.bouncycastle.provider";
	/**
	 * The class resource of the BouncyCastle provider, used for the non-modular detection fallback.<br>
	 */
	private static final String BOUNCY_CASTLE_RESOURCE = "org/bouncycastle/jce/provider/BouncyCastleProvider.class";
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private Providers() {}
	
	/**
	 * Installs BouncyCastle as a JCA provider if it is available.<br>
	 * <p>
	 *     This mutates JVM-global state and is therefore never called implicitly.<br>
	 *     Call it once at application start, before any algorithm that requires BouncyCastle is used.
	 * </p>
	 * <p>
	 *     Putting this library on the module path while leaving BouncyCastle on the class path is not supported: the provider class cannot be resolved from a named module in that arrangement.<br>
	 *     Either both go on the module path, or both on the class path.
	 * </p>
	 *
	 * @return True if BouncyCastle is available after this call
	 */
	public static boolean installBouncyCastle() {
		if (isBouncyCastleAvailable()) {
			return true;
		}
		if (!isBouncyCastleOnPath()) {
			return false;
		}
		
		Security.addProvider(BouncyCastle.create());
		return true;
	}
	
	/**
	 * Returns whether BouncyCastle is installed as a JCA provider.<br>
	 *
	 * @return True if BouncyCastle is installed
	 * @see #installBouncyCastle()
	 */
	public static boolean isBouncyCastleAvailable() {
		return Security.getProvider(BOUNCY_CASTLE_NAME) != null;
	}
	
	/**
	 * Returns whether the BouncyCastle provider can be loaded.<br>
	 * <p>
	 *     The module lookup answers the question for a modular deployment, where the provider is an optional resolved module.<br>
	 *     The resource lookup covers the non-modular deployment, where the module layer knows nothing about a jar on the class path.<br>
	 *     Neither loads or initialises the class, and neither answers the question by catching a failure.
	 * </p>
	 *
	 * @return True if the provider class is reachable
	 */
	public static boolean isBouncyCastleOnPath() {
		if (ModuleLayer.boot().findModule(BOUNCY_CASTLE_MODULE).isPresent()) {
			return true;
		}
		return Providers.class.getClassLoader().getResource(BOUNCY_CASTLE_RESOURCE) != null;
	}
	
	/**
	 * Returns whether the given hash algorithm is served by a registered provider.<br>
	 *
	 * @param algorithm The algorithm to check
	 * @return True if the algorithm is available
	 * @throws NullPointerException If the algorithm is null
	 */
	public static boolean supports(@NonNull HashAlgorithm algorithm) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		return isAvailable("MessageDigest", algorithm.jcaName());
	}
	
	/**
	 * Returns whether the given mac algorithm is served by a registered provider.<br>
	 *
	 * @param algorithm The algorithm to check
	 * @return True if the algorithm is available
	 * @throws NullPointerException If the algorithm is null
	 */
	public static boolean supports(@NonNull MacAlgorithm algorithm) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		return isAvailable("Mac", algorithm.jcaName());
	}
	
	/**
	 * Returns whether the given aead algorithm is served by a registered provider.<br>
	 * <p>
	 *     A cipher transformation is not always discoverable through the service registry:<br>
	 *     A provider may register the base cipher and resolve the mode internally, which is what BouncyCastle does for GCM-SIV.<br>
	 *     For those algorithms the question reduces to whether their provider is installed, which is checked instead of reporting a false negative.
	 * </p>
	 *
	 * @param algorithm The algorithm to check
	 * @return True if the algorithm is available
	 * @throws NullPointerException If the algorithm is null
	 */
	public static boolean supports(@NonNull AeadAlgorithm algorithm) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		if (isAvailable("Cipher", algorithm.jcaName())) {
			return true;
		}
		return algorithm.requiresBouncyCastle() && isBouncyCastleAvailable();
	}
	
	/**
	 * Returns whether the given key encapsulation mechanism is served by a registered provider.<br>
	 * A hybrid mechanism is available only if both of its components are.<br>
	 *
	 * @param algorithm The algorithm to check
	 * @return True if the algorithm is available
	 * @throws NullPointerException If the algorithm is null
	 */
	public static boolean supports(@NonNull KemAlgorithm algorithm) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		
		return switch (algorithm) {
			case NativeKemAlgorithm n -> isAvailable("KEM", n.jcaName()) && isAvailable("KeyPairGenerator", n.keyJcaName());
			case DhKemAlgorithm dh -> isAvailable("KEM", dh.jcaName()) && isAvailable("KeyPairGenerator", dh.keyJcaName());
			case HybridKemAlgorithm hybrid -> supports(hybrid.classical()) && supports(hybrid.postQuantum());
		};
	}
	
	/**
	 * Returns whether the given signature scheme is served by a registered provider.<br>
	 * A hybrid scheme is available only if both of its components are.<br>
	 *
	 * @param algorithm The algorithm to check
	 * @return True if the algorithm is available
	 * @throws NullPointerException If the algorithm is null
	 */
	public static boolean supports(@NonNull SignatureAlgorithm algorithm) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		
		return switch (algorithm) {
			case NativeSignatureAlgorithm n -> isAvailable("Signature", n.jcaName()) && isAvailable("KeyPairGenerator", n.keyJcaName());
			case HybridSignatureAlgorithm hybrid -> supports(hybrid.classical()) && supports(hybrid.postQuantum());
		};
	}
	
	/**
	 * Returns whether the given key derivation function is served by a registered provider.<br>
	 *
	 * @param algorithm The algorithm to check
	 * @return True if the algorithm is available
	 * @throws NullPointerException If the algorithm is null
	 */
	public static boolean supports(@NonNull KdfAlgorithm algorithm) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		return isAvailable("KDF", algorithm.jcaName());
	}
	
	/**
	 * Returns whether the given password hashing function is served.<br>
	 * <p>
	 *     The two BouncyCastle functions are served by the provider instance this library holds rather than by whatever is registered,<br>
	 *     so they are available whether {@link #installBouncyCastle()} has been called.
	 * </p>
	 *
	 * @param algorithm The algorithm to check
	 * @return True if the algorithm is available
	 * @throws NullPointerException If the algorithm is null
	 */
	public static boolean supports(@NonNull PasswordAlgorithm algorithm) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		
		if (algorithm.requiresBouncyCastle()) {
			return bouncyCastle().getService("SecretKeyFactory", algorithm.jcaName()) != null;
		}
		return isAvailable("SecretKeyFactory", algorithm.jcaName());
	}
	
	/**
	 * Returns the BouncyCastle provider instance this library derives with.<br>
	 * <p>
	 *     Handing the instance to a factory directly is not the same as installing it.<br>
	 *     Nothing global is mutated, and an algorithm reached this way works even in a process that never called {@link #installBouncyCastle()}.
	 * </p>
	 *
	 * @return The provider instance
	 */
	static @NonNull Provider bouncyCastle() {
		return BouncyCastle.INSTANCE;
	}
	
	/**
	 * Returns whether the given service and algorithm are served by a registered provider.<br>
	 *
	 * @param service The JCA service type, such as Cipher or Signature
	 * @param algorithm The JCA algorithm name
	 * @return True if the algorithm is available
	 * @throws NullPointerException If the service or the algorithm is null
	 */
	public static boolean isAvailable(@NonNull String service, @NonNull String algorithm) {
		Objects.requireNonNull(service, "Service must not be null");
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		
		Provider[] providers = Security.getProviders(service + "." + algorithm);
		return providers != null && providers.length > 0;
	}
	
	/**
	 * Returns which provider will actually serve the given algorithm.<br>
	 * Intended for a one-line startup log, so provider surprises are visible before they matter.<br>
	 *
	 * @param service The JCA service type, such as Cipher or Signature
	 * @param algorithm The JCA algorithm name
	 * @return The provider which would serve the algorithm, or empty if none would
	 * @throws NullPointerException If the service or the algorithm is null
	 */
	public static @NonNull Optional<Provider> preferred(@NonNull String service, @NonNull String algorithm) {
		Objects.requireNonNull(service, "Service must not be null");
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		
		Provider[] providers = Security.getProviders(service + "." + algorithm);
		return providers == null || providers.length == 0 ? Optional.empty() : Optional.of(providers[0]);
	}
	
	/**
	 * Verifies that every algorithm of the given suite is servable.<br>
	 * <p>
	 *     Call this once at startup so a misconfigured runtime fails loudly, rather than at the first encrypted request in production.
	 * </p>
	 *
	 * @param suite The suite to check
	 * @throws NullPointerException If the suite is null
	 * @throws UnsupportedAlgorithmException If any algorithm of the suite is missing
	 */
	public static void require(@NonNull CryptoSuite suite) {
		Objects.requireNonNull(suite, "Suite must not be null");
		
		List<String> missing = new ArrayList<>();
		if (!supports(suite.aead())) {
			missing.add("Cipher    " + suite.aead().jcaName());
		}
		if (!supports(suite.kem())) {
			missing.add("KEM       " + suite.kem().name());
		}
		if (!supports(suite.signature())) {
			missing.add("Signature " + suite.signature().name());
		}
		if (!supports(suite.kdf())) {
			missing.add("KDF       " + suite.kdf().name());
		}
		if (!supports(suite.hash())) {
			missing.add("Hash      " + suite.hash().jcaName());
		}
		if (missing.isEmpty()) {
			return;
		}
		
		throw UnsupportedAlgorithmException.forAlgorithm(
			suite.name(), new IllegalStateException("Missing algorithms (JDK " + Runtime.version().feature() + ", BouncyCastle " + (isBouncyCastleAvailable() ? "present" : "absent") + "):\n  " + String.join("\n  ", missing))
		);
	}
	
	/**
	 * Logs which provider serves each algorithm of the given suite.<br>
	 * <p>
	 *     Provider surprises are far cheaper to notice in a startup line than in a support ticket.<br>
	 *     No key material, nonce, salt or derived secret is ever logged, at any level.
	 * </p>
	 *
	 * @param suite The suite to report on
	 * @throws NullPointerException If the suite is null
	 */
	public static void logProviders(@NonNull CryptoSuite suite) {
		Objects.requireNonNull(suite, "Suite must not be null");
		LOGGER.info("Crypto suite {} served by: AEAD={}, KEM={}, Signature={}, KDF={}, Hash={}",
			suite.name(),
			serving("Cipher", suite.aead().jcaName()),
			servingKem(suite.kem()),
			suite.signature() instanceof NativeSignatureAlgorithm n ? serving("Signature", n.jcaName()) : suite.signature().name(),
			serving("KDF", suite.kdf().jcaName()),
			serving("MessageDigest", suite.hash().jcaName())
		);
	}
	
	/**
	 * Returns the name of the provider serving the given mechanism, for logging.<br>
	 * <p>
	 *     Both the native and the Diffie-Hellman mechanisms are served through the key encapsulation API and carry a JCA name, so both report a provider.<br>
	 *     A hybrid has no JCA name of its own because it is two mechanisms, so it reports its own name instead.
	 * </p>
	 *
	 * @param algorithm The mechanism to report on
	 * @return The serving provider's name, or the mechanism's own name if it has no single provider
	 * @throws NullPointerException If the algorithm is null
	 */
	private static @NonNull String servingKem(@NonNull KemAlgorithm algorithm) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		
		return switch (algorithm) {
			case NativeKemAlgorithm n -> serving("KEM", n.jcaName());
			case DhKemAlgorithm dh -> serving("KEM", dh.jcaName());
			case HybridKemAlgorithm hybrid -> hybrid.name();
		};
	}
	
	/**
	 * Returns the name of the provider serving the given algorithm, for logging.<br>
	 *
	 * @param service The JCA service type
	 * @param algorithm The JCA algorithm name
	 * @return The provider name, or a placeholder if none serves it
	 * @throws NullPointerException If the service or the algorithm is null
	 */
	private static @NonNull String serving(@NonNull String service, @NonNull String algorithm) {
		Objects.requireNonNull(service, "Service must not be null");
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		
		return preferred(service, algorithm).map(Provider::getName).orElse("none");
	}
	
	/**
	 * Creates a key encapsulation mechanism for the given JCA name.<br>
	 *
	 * @param algorithm The JCA algorithm name
	 * @return The created key encapsulation mechanism
	 * @throws NullPointerException If the algorithm is null
	 * @throws UnsupportedAlgorithmException If no registered provider serves the algorithm
	 */
	static @NonNull KEM kem(@NonNull String algorithm) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		
		try {
			return KEM.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw UnsupportedAlgorithmException.forAlgorithm(algorithm, e);
		}
	}
	
	/**
	 * Holder for the direct reference to the BouncyCastle provider.<br>
	 * <p>
	 *     The reference lives in its own class on purpose.<br>
	 *     Verifying a class that mentions {@link BouncyCastleProvider} forces that class to be loaded, so keeping the reference in {@link Providers} itself would make the whole class unusable whenever BouncyCastle is absent, including the methods that exist precisely to report that it is absent.<br>
	 *     Nested here, it is only loaded once the instance is actually reached.
	 * </p>
	 * <p>
	 *     One instance is shared between installing the provider and handing it to a factory directly,<br>
	 *     so a process that does both ends up with the same provider in either path.
	 * </p>
	 *
	 * @author Luis-St
	 */
	private static final class BouncyCastle {
		
		/**
		 * The single BouncyCastle provider instance this library uses.<br>
		 */
		private static final Provider INSTANCE = new BouncyCastleProvider();
		
		/**
		 * Private constructor to prevent instantiation.<br>
		 * This is a static helper class.<br>
		 */
		private BouncyCastle() {}
		
		/**
		 * Returns the shared BouncyCastle provider instance.<br>
		 * @return The provider
		 */
		private static @NonNull Provider create() {
			return INSTANCE;
		}
	}
}
