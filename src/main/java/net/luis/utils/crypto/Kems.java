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
import net.luis.utils.crypto.exception.*;
import net.luis.utils.crypto.key.*;
import net.luis.utils.crypto.util.CryptoBytes;
import org.jspecify.annotations.NonNull;

import javax.crypto.KEM;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Objects;

/**
 * Key encapsulation, the replacement for encrypting directly to a public key.<br>
 * <p>
 *     A mechanism produces a random shared secret plus an encapsulation that only the holder of the private key can turn back into that secret.<br>
 *     The secret is then used to key an authenticated cipher, which is what {@link Sealed} does.
 * </p>
 * <p>
 *     Every mechanism here goes through the JDK key encapsulation API, the Diffie-Hellman ones included.<br>
 *     Those are the RFC 9180 DHKEM as the provider implements it, so their encapsulation and their shared secret are what any other RFC 9180 implementation would produce.<br>
 *     The hybrid combiner is still this library's own and interoperates with nothing.
 * </p>
 * <p>
 *     Example:
 * </p>
 * <pre>{@code
 * KeyPair pair = Kems.generateKeyPair(KemAlgorithm.X25519_ML_KEM_768);
 *
 * // Sender, produces a fresh shared secret plus the encapsulation carrying it
 * byte[] wire;
 * try (Kems.Encapsulation sent = Kems.encapsulate(KemAlgorithm.X25519_ML_KEM_768, pair.getPublic())) {
 *     wire = sent.encapsulation();
 *     use(sent.sharedSecret().material());
 * }
 *
 * // Recipient, recovers the same secret from the encapsulation alone
 * try (Secret received = Kems.decapsulate(KemAlgorithm.X25519_ML_KEM_768, pair.getPrivate(), wire)) {
 *     use(received.material());
 * }
 * }</pre>
 *
 * @see KemAlgorithm
 * @see Sealed
 *
 * @author Luis-St
 */
public final class Kems {
	
	/**
	 * The key derivation function used to derive and combine shared secrets.<br>
	 */
	private static final KdfAlgorithm COMBINER = KdfAlgorithm.HKDF_SHA_256;
	/**
	 * The domain separation label of the hybrid combiner.<br>
	 */
	private static final byte[] HYBRID_LABEL = "lutils-hybrid-kem-v1".getBytes(StandardCharsets.UTF_8);
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private Kems() {}
	
	/**
	 * Generates a new key pair for the given mechanism.<br>
	 *
	 * @param algorithm The mechanism the key pair is for
	 * @return The generated key pair
	 * @throws NullPointerException If the algorithm is null
	 * @throws UnsupportedAlgorithmException If no registered provider serves the algorithm
	 */
	public static @NonNull KeyPair generateKeyPair(@NonNull KemAlgorithm algorithm) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		
		return switch (algorithm) {
			case NativeKemAlgorithm n -> generate(n.keyJcaName());
			case DhKemAlgorithm dh -> generate(dh.keyJcaName());
			case HybridKemAlgorithm hybrid -> {
				KeyPair classical = generateKeyPair(hybrid.classical());
				KeyPair postQuantum = generateKeyPair(hybrid.postQuantum());
				
				yield new KeyPair(
					new HybridPublicKey(classical.getPublic(), postQuantum.getPublic()),
					new HybridPrivateKey(classical.getPrivate(), postQuantum.getPrivate())
				);
			}
		};
	}
	
	/**
	 * Encapsulates a fresh shared secret to the given recipient.<br>
	 *
	 * @param algorithm The mechanism to encapsulate with
	 * @param recipient The public key to encapsulate to
	 * @return The encapsulation and the shared secret it carries
	 * @throws NullPointerException If the algorithm or the recipient is null
	 * @throws ClassCastException If a hybrid mechanism is given a key that is not a hybrid public key
	 * @throws CryptoException If the encapsulation fails
	 */
	public static @NonNull Encapsulation encapsulate(@NonNull KemAlgorithm algorithm, @NonNull PublicKey recipient) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(recipient, "Recipient must not be null");
		
		return switch (algorithm) {
			case NativeKemAlgorithm n -> encapsulateNative(n.name(), n.jcaName(), recipient);
			case DhKemAlgorithm dh -> encapsulateNative(dh.name(), dh.jcaName(), recipient);
			case HybridKemAlgorithm hybrid -> encapsulateHybrid(hybrid, (HybridPublicKey) recipient);
		};
	}
	
	/**
	 * Recovers the shared secret from the given encapsulation.<br>
	 * <p>
	 *     A wrong key does not necessarily fail here.<br>
	 *     The lattice mechanisms reject implicitly, which means they return a different secret rather than an error,<br>
	 *     so the caller has to detect the mismatch further down, which is what the key commitment in {@link Sealed} is for.
	 * </p>
	 *
	 * @param algorithm The mechanism to decapsulate with
	 * @param recipient The private key to decapsulate with
	 * @param encapsulation The encapsulation to open
	 * @return The recovered shared secret
	 * @throws NullPointerException If the algorithm, the recipient or the encapsulation is null
	 * @throws MalformedDataException If the encapsulation does not have the length the mechanism requires
	 * @throws ClassCastException If a hybrid mechanism is given a key that is not a hybrid private key
	 * @throws CryptoException If the decapsulation fails
	 */
	public static @NonNull Secret decapsulate(@NonNull KemAlgorithm algorithm, @NonNull PrivateKey recipient, byte @NonNull [] encapsulation) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(recipient, "Recipient must not be null");
		Objects.requireNonNull(encapsulation, "Encapsulation must not be null");
		if (encapsulation.length != algorithm.encapsulationLength()) {
			throw new MalformedDataException("Expected " + algorithm.encapsulationLength() + " byte encapsulation for " + algorithm.name() + ", got " + encapsulation.length);
		}
		
		return switch (algorithm) {
			case NativeKemAlgorithm n -> decapsulateNative(n.name(), n.jcaName(), recipient, encapsulation);
			case DhKemAlgorithm dh -> decapsulateNative(dh.name(), dh.jcaName(), recipient, encapsulation);
			case HybridKemAlgorithm hybrid -> decapsulateHybrid(hybrid, (HybridPrivateKey) recipient, encapsulation);
		};
	}
	
	/**
	 * Encapsulates through the native JCA key encapsulation API.<br>
	 * <p>
	 *     The name and the JCA name are passed separately rather than the mechanism itself,<br>
	 *     because both the lattice mechanisms and the Diffie-Hellman ones are served here and they do not share a type that carries a JCA name.
	 * </p>
	 *
	 * @param name The name of the mechanism, for the error message
	 * @param jcaName The JCA name of the mechanism to encapsulate with
	 * @param recipient The public key to encapsulate to
	 * @return The encapsulation and the shared secret it carries
	 * @throws NullPointerException If the name, the jca name or the recipient key is null
	 * @throws CryptoException If the encapsulation fails
	 */
	private static @NonNull Encapsulation encapsulateNative(@NonNull String name, @NonNull String jcaName, @NonNull PublicKey recipient) {
		Objects.requireNonNull(name, "Name must not be null");
		Objects.requireNonNull(jcaName, "Jca name must not be null");
		Objects.requireNonNull(recipient, "Recipient key must not be null");
		
		try {
			KEM.Encapsulated encapsulated = Providers.kem(jcaName).newEncapsulator(recipient).encapsulate();
			return new Encapsulation(encapsulated.encapsulation(), Secret.adopt(encapsulated.key().getEncoded()));
		} catch (GeneralSecurityException e) {
			throw new CryptoException("Encapsulation failed for " + name, e);
		}
	}
	
	/**
	 * Decapsulates through the native JCA key encapsulation API.<br>
	 * The name and the JCA name are passed separately for the reason given on {@link #encapsulateNative}.<br>
	 *
	 * @param name The name of the mechanism, for the error message
	 * @param jcaName The JCA name of the mechanism to decapsulate with
	 * @param recipient The private key to decapsulate with
	 * @param encapsulation The encapsulation to open
	 * @return The recovered shared secret
	 * @throws NullPointerException If the name, the jca name, the recipient key or the encapsulation is null
	 * @throws AuthenticationException If the encapsulation is rejected by the mechanism
	 */
	private static @NonNull Secret decapsulateNative(@NonNull String name, @NonNull String jcaName, @NonNull PrivateKey recipient, byte @NonNull [] encapsulation) {
		Objects.requireNonNull(name, "Name must not be null");
		Objects.requireNonNull(jcaName, "Jca name must not be null");
		Objects.requireNonNull(recipient, "Recipient key must not be null");
		Objects.requireNonNull(encapsulation, "Encapsulation must not be null");
		
		try {
			return Secret.adopt(Providers.kem(jcaName).newDecapsulator(recipient).decapsulate(encapsulation).getEncoded());
		} catch (GeneralSecurityException e) {
			throw new AuthenticationException("Decapsulation failed for " + name, e);
		}
	}
	
	/**
	 * Encapsulates with both components and combines their secrets.<br>
	 *
	 * @param algorithm The mechanism to encapsulate with
	 * @param recipient The composite public key to encapsulate to
	 * @return The concatenated encapsulation and the combined shared secret
	 * @throws NullPointerException If the algorithm or the recipient key is null
	 * @throws CryptoException If either component fails
	 */
	private static @NonNull Encapsulation encapsulateHybrid(@NonNull HybridKemAlgorithm algorithm, @NonNull HybridPublicKey recipient) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(recipient, "Recipient key must not be null");
		
		try (Encapsulation classical = encapsulate(algorithm.classical(), recipient.classical()); Encapsulation postQuantum = encapsulate(algorithm.postQuantum(), recipient.postQuantum())) {
			byte[] encapsulation = CryptoBytes.concat(classical.encapsulation(), postQuantum.encapsulation());
			return new Encapsulation(encapsulation, combine(algorithm, classical.sharedSecret(), postQuantum.sharedSecret(), encapsulation));
		}
	}
	
	/**
	 * Decapsulates both components and combines their secrets.<br>
	 *
	 * @param algorithm The mechanism to decapsulate with
	 * @param recipient The composite private key to decapsulate with
	 * @param encapsulation The concatenated encapsulation to open
	 * @return The combined shared secret
	 * @throws NullPointerException If the algorithm, the recipient key or the encapsulation is null
	 * @throws CryptoException If either component fails
	 */
	private static @NonNull Secret decapsulateHybrid(@NonNull HybridKemAlgorithm algorithm, @NonNull HybridPrivateKey recipient, byte @NonNull [] encapsulation) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(recipient, "Recipient key must not be null");
		Objects.requireNonNull(encapsulation, "Encapsulation must not be null");
		
		int split = algorithm.classical().encapsulationLength();
		byte[] classicalPart = CryptoBytes.slice(encapsulation, 0, split);
		byte[] postQuantumPart = CryptoBytes.slice(encapsulation, split, encapsulation.length - split);
		
		try (Secret classical = decapsulate(algorithm.classical(), recipient.classical(), classicalPart); Secret postQuantum = decapsulate(algorithm.postQuantum(), recipient.postQuantum(), postQuantumPart)) {
			return combine(algorithm, classical, postQuantum, encapsulation);
		}
	}
	
	/**
	 * Combines the two component secrets into one.<br>
	 * <p>
	 *     The post-quantum secret comes first and the full encapsulation is bound into the context, so neither component can be substituted without changing the result.<br>
	 *     The ordering follows the spirit of the IETF hybrid designs, but the encoding is this library's own and is not interoperable with them.
	 * </p>
	 *
	 * @param algorithm The mechanism being combined for
	 * @param classical The classical component's secret
	 * @param postQuantum The post-quantum component's secret
	 * @param encapsulation The concatenated encapsulation to bind
	 * @return The combined shared secret
	 * @throws NullPointerException If the algorithm, either component secret or the encapsulation is null
	 */
	private static @NonNull Secret combine(@NonNull HybridKemAlgorithm algorithm, @NonNull Secret classical, @NonNull Secret postQuantum, byte @NonNull [] encapsulation) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(classical, "Classical secret must not be null");
		Objects.requireNonNull(postQuantum, "Post-quantum secret must not be null");
		Objects.requireNonNull(encapsulation, "Encapsulation must not be null");
		
		byte[] ikm = CryptoBytes.concat(postQuantum.material(), classical.material());
		byte[] context = CryptoBytes.concat(HYBRID_LABEL, algorithm.name().getBytes(StandardCharsets.UTF_8), encapsulation);
		try {
			return Kdf.derive(COMBINER, ikm, null, context, algorithm.sharedSecretLength());
		} finally {
			CryptoBytes.wipe(ikm);
		}
	}
	
	/**
	 * Generates a key pair through the JCA key pair generator of the given name.<br>
	 *
	 * @param keyJcaName The JCA key name to generate for
	 * @return The generated key pair
	 * @throws NullPointerException If the key jca name is null
	 * @throws UnsupportedAlgorithmException If no registered provider serves the algorithm
	 */
	private static @NonNull KeyPair generate(@NonNull String keyJcaName) {
		Objects.requireNonNull(keyJcaName, "Key jca name must not be null");
		
		try {
			return KeyPairGenerator.getInstance(keyJcaName).generateKeyPair();
		} catch (GeneralSecurityException e) {
			throw UnsupportedAlgorithmException.forAlgorithm(keyJcaName, e);
		}
	}
	
	/**
	 * An encapsulation and the shared secret it carries.<br>
	 * Close it when done, which wipes the secret.<br>
	 * The encapsulation is public and is not wiped.<br>
	 *
	 * @author Luis-St
	 *
	 * @param encapsulation The encapsulation to send to the recipient
	 * @param sharedSecret The shared secret the recipient will recover
	 */
	public record Encapsulation(
		byte @NonNull [] encapsulation,
		@NonNull Secret sharedSecret
	) implements AutoCloseable {
		
		/**
		 * Constructs a new encapsulation.<br>
		 * @throws NullPointerException If the encapsulation or the shared secret is null
		 */
		public Encapsulation {
			Objects.requireNonNull(encapsulation, "Encapsulation must not be null");
			Objects.requireNonNull(sharedSecret, "Shared secret must not be null");
		}
		
		/**
		 * Wipes the shared secret.<br>
		 */
		@Override
		public void close() {
			this.sharedSecret.close();
		}
	}
}
