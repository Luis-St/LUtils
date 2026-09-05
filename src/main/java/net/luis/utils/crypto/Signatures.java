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
import net.luis.utils.crypto.key.HybridPrivateKey;
import net.luis.utils.crypto.key.HybridPublicKey;
import net.luis.utils.crypto.util.CryptoRandom;
import net.luis.utils.function.throwable.ThrowableSupplier;
import org.jspecify.annotations.NonNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Digital signatures.<br>
 * <p>
 *     Every entry point routes through one dispatch method, so the split into a hybrid's two components lives in exactly one place and no overload can forget it.
 * </p>
 * <p>
 *     A message that is not already in memory is signed from a re-openable source rather than from a stream,<br>
 *     because a hybrid has to read the message once per component.<br>
 *     A stream can only be read once, so the bare stream overloads are typed to {@link NativeSignatureAlgorithm}<br>
 *     and the compiler rejects the combination that would otherwise sign an empty message with the second component.
 * </p>
 * <p>
 *     The composite schemes are not affected by this.<br>
 *     One registered algorithm covers both of their components, so the provider reads the message once and they are native schemes as far as this class is concerned.<br>
 *     That makes them the only way to sign a stream, or to sign incrementally through {@link #signer}, with a scheme that has both a classical and a post-quantum half.
 * </p>
 * <p>
 *     Example:
 * </p>
 * <pre>{@code
 * KeyPair pair = Signatures.generateKeyPair(SignatureAlgorithm.ED25519_ML_DSA_65);
 * byte[] signature = Signatures.sign(SignatureAlgorithm.ED25519_ML_DSA_65, pair.getPrivate(), data);
 *
 * if (Signatures.verify(SignatureAlgorithm.ED25519_ML_DSA_65, pair.getPublic(), data, signature)) {
 *     accept(data);
 * }
 *
 * // Same check, but a mismatch throws instead of returning false
 * Signatures.require(SignatureAlgorithm.ED25519_ML_DSA_65, pair.getPublic(), data, signature);
 *
 * // A hybrid reads the message once per component, so it is signed from a re-openable source
 * byte[] ofFile = Signatures.sign(SignatureAlgorithm.ED25519_ML_DSA_65, pair.getPrivate(), Path.of("release.jar"));
 * }</pre>
 *
 * @see SignatureAlgorithm
 * @see Signer
 * @see Verifier
 *
 * @author Luis-St
 */
public final class Signatures {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private Signatures() {}
	
	/**
	 * Generates a new key pair for the given scheme.<br>
	 *
	 * @param algorithm The scheme the key pair is for
	 * @return The generated key pair
	 * @throws NullPointerException If the algorithm is null
	 * @throws UnsupportedAlgorithmException If no registered provider serves the algorithm
	 */
	public static @NonNull KeyPair generateKeyPair(@NonNull SignatureAlgorithm algorithm) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		
		return switch (algorithm) {
			case NativeSignatureAlgorithm n -> {
				try {
					KeyPairGenerator generator = KeyPairGenerator.getInstance(n.keyJcaName());
					if (n.keySpec() != null) {
						generator.initialize(n.keySpec(), CryptoRandom.instance());
					}
					yield generator.generateKeyPair();
				} catch (GeneralSecurityException e) {
					throw UnsupportedAlgorithmException.forAlgorithm(n.keyJcaName(), e);
				}
			}
			case HybridSignatureAlgorithm hybrid -> {
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
	 * Signs the given bytes.<br>
	 *
	 * @param algorithm The scheme to sign with
	 * @param key The private key to sign with
	 * @param data The message to sign
	 * @return The produced signature
	 * @throws NullPointerException If the algorithm, the key or the data is null
	 * @throws ClassCastException If a hybrid scheme is given a key that is not a hybrid private key
	 * @throws CryptoException If the signing fails
	 */
	public static byte @NonNull [] sign(@NonNull SignatureAlgorithm algorithm, @NonNull PrivateKey key, byte @NonNull [] data) {
		Objects.requireNonNull(data, "Data must not be null");
		return dispatchSign(algorithm, key, signer -> signer.update(data));
	}
	
	/**
	 * Signs everything the given source yields.<br>
	 * <p>
	 *     The source is opened once per component, so a hybrid scheme reads the message twice.<br>
	 *     This is why a re-openable source is required rather than a stream: a stream would be exhausted after the first component and the second would sign an empty message.
	 * </p>
	 *
	 * @param algorithm The scheme to sign with
	 * @param key The private key to sign with
	 * @param source The source of the message, openable once per component
	 * @return The produced signature
	 * @throws NullPointerException If the algorithm, the key or the source is null
	 * @throws ClassCastException If a hybrid scheme is given a key that is not a hybrid private key
	 * @throws UncheckedIOException If opening or reading the source fails
	 * @throws CryptoException If the signing fails
	 */
	public static byte @NonNull [] sign(@NonNull SignatureAlgorithm algorithm, @NonNull PrivateKey key, @NonNull ThrowableSupplier<InputStream, IOException> source) {
		Objects.requireNonNull(source, "Source must not be null");
		
		return dispatchSign(algorithm, key, signer -> {
			try (InputStream input = source.get()) {
				signer.update(input);
			} catch (IOException e) {
				throw new UncheckedIOException("Failed to read the source to sign", e);
			}
		});
	}
	
	/**
	 * Signs the contents of the given file.<br>
	 * The file is re-opened once per component, which is what makes it usable with a hybrid scheme.<br>
	 *
	 * @param algorithm The scheme to sign with
	 * @param key The private key to sign with
	 * @param file The file to sign
	 * @return The produced signature
	 * @throws NullPointerException If the algorithm, the key or the file is null
	 * @throws ClassCastException If a hybrid scheme is given a key that is not a hybrid private key
	 * @throws UncheckedIOException If opening or reading the file fails
	 * @throws CryptoException If the signing fails
	 */
	public static byte @NonNull [] sign(@NonNull SignatureAlgorithm algorithm, @NonNull PrivateKey key, @NonNull Path file) {
		Objects.requireNonNull(file, "File must not be null");
		return sign(algorithm, key, () -> Files.newInputStream(file));
	}
	
	/**
	 * Signs everything the given stream yields.<br>
	 * <p>
	 *     Restricted to the schemes that read the message exactly once.<br>
	 *     A hybrid needs the message twice and cannot be served from a stream, so it is rejected by the type rather than at runtime.<br>
	 *     Use the source or file overload for those.
	 * </p>
	 *
	 * @param algorithm The scheme to sign with
	 * @param key The private key to sign with
	 * @param input The stream to read the message from
	 * @return The produced signature
	 * @throws NullPointerException If the algorithm, the key or the input is null
	 * @throws UncheckedIOException If reading the stream fails
	 * @throws CryptoException If the signing fails
	 */
	public static byte @NonNull [] sign(@NonNull NativeSignatureAlgorithm algorithm, @NonNull PrivateKey key, @NonNull InputStream input) {
		Objects.requireNonNull(input, "Input must not be null");
		return signer(algorithm, key).update(input).sign();
	}
	
	/**
	 * Checks the given signature against the given bytes.<br>
	 *
	 * @param algorithm The scheme to verify with
	 * @param key The public key to verify with
	 * @param data The message the signature should cover
	 * @param signature The signature to check
	 * @return True if the signature matches the message and the key
	 * @throws NullPointerException If the algorithm, the key, the data or the signature is null
	 * @throws ClassCastException If a hybrid scheme is given a key that is not a hybrid public key
	 * @throws MalformedDataException If a hybrid signature is malformed
	 */
	public static boolean verify(@NonNull SignatureAlgorithm algorithm, @NonNull PublicKey key, byte @NonNull [] data, byte @NonNull [] signature) {
		Objects.requireNonNull(data, "Data must not be null");
		return dispatchVerify(algorithm, key, verifier -> verifier.update(data), signature);
	}
	
	/**
	 * Checks the given signature against everything the given source yields.<br>
	 * The source is opened once per component, as it is for signing.<br>
	 *
	 * @param algorithm The scheme to verify with
	 * @param key The public key to verify with
	 * @param source The source of the message, openable once per component
	 * @param signature The signature to check
	 * @return True if the signature matches the message and the key
	 * @throws NullPointerException If the algorithm, the key, the source or the signature is null
	 * @throws ClassCastException If a hybrid scheme is given a key that is not a hybrid public key
	 * @throws MalformedDataException If a hybrid signature is malformed
	 * @throws UncheckedIOException If opening or reading the source fails
	 */
	public static boolean verify(@NonNull SignatureAlgorithm algorithm, @NonNull PublicKey key, @NonNull ThrowableSupplier<InputStream, IOException> source, byte @NonNull [] signature) {
		Objects.requireNonNull(source, "Source must not be null");
		
		return dispatchVerify(algorithm, key, verifier -> {
			try (InputStream input = source.get()) {
				verifier.update(input);
			} catch (IOException e) {
				throw new UncheckedIOException("Failed to read the source to verify", e);
			}
		}, signature);
	}
	
	/**
	 * Checks the given signature against the contents of the given file.<br>
	 *
	 * @param algorithm The scheme to verify with
	 * @param key The public key to verify with
	 * @param file The file the signature should cover
	 * @param signature The signature to check
	 * @return True if the signature matches the file and the key
	 * @throws NullPointerException If the algorithm, the key, the file or the signature is null
	 * @throws ClassCastException If a hybrid scheme is given a key that is not a hybrid public key
	 * @throws MalformedDataException If a hybrid signature is malformed
	 * @throws UncheckedIOException If opening or reading the file fails
	 */
	public static boolean verify(@NonNull SignatureAlgorithm algorithm, @NonNull PublicKey key, @NonNull Path file, byte @NonNull [] signature) {
		Objects.requireNonNull(file, "File must not be null");
		return verify(algorithm, key, () -> Files.newInputStream(file), signature);
	}
	
	/**
	 * Checks the given signature against everything the given stream yields.<br>
	 * Restricted to the schemes that read the message exactly once, for the reason given on the signing overload.<br>
	 *
	 * @param algorithm The scheme to verify with
	 * @param key The public key to verify with
	 * @param input The stream to read the message from
	 * @param signature The signature to check
	 * @return True if the signature matches the message and the key
	 * @throws NullPointerException If the algorithm, the key, the input or the signature is null
	 * @throws UncheckedIOException If reading the stream fails
	 */
	public static boolean verify(@NonNull NativeSignatureAlgorithm algorithm, @NonNull PublicKey key, @NonNull InputStream input, byte @NonNull [] signature) {
		Objects.requireNonNull(input, "Input must not be null");
		return verifier(algorithm, key).update(input).verify(signature);
	}
	
	/**
	 * Checks the given signature against the given bytes, using the certificate's public key.<br>
	 *
	 * @param algorithm The scheme to verify with
	 * @param certificate The certificate holding the public key to verify with
	 * @param data The message the signature should cover
	 * @param signature The signature to check
	 * @return True if the signature matches the message and the key
	 * @throws NullPointerException If the algorithm, the certificate, the data or the signature is null
	 */
	public static boolean verify(@NonNull SignatureAlgorithm algorithm, @NonNull X509Certificate certificate, byte @NonNull [] data, byte @NonNull [] signature) {
		Objects.requireNonNull(certificate, "Certificate must not be null");
		return verify(algorithm, certificate.getPublicKey(), data, signature);
	}
	
	/**
	 * Requires that the given signature matches the given bytes.<br>
	 *
	 * @param algorithm The scheme to verify with
	 * @param key The public key to verify with
	 * @param data The message the signature should cover
	 * @param signature The signature to check
	 * @throws NullPointerException If the algorithm, the key, the data or the signature is null
	 * @throws AuthenticationException If the signature does not match
	 */
	public static void require(@NonNull SignatureAlgorithm algorithm, @NonNull PublicKey key, byte @NonNull [] data, byte @NonNull [] signature) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		
		if (!verify(algorithm, key, data, signature)) {
			throw new AuthenticationException("Signature verification failed for " + algorithm.name());
		}
	}
	
	/**
	 * Creates a new incremental signer for the given scheme.<br>
	 * <p>
	 *     Only the schemes that read the message once can be served incrementally, which is why this takes a native scheme.<br>
	 *     A hybrid has to go through the one-shot methods.
	 * </p>
	 *
	 * @param algorithm The scheme to sign with
	 * @param key The private key to sign with
	 * @return The created signer
	 * @throws NullPointerException If the algorithm, the key or the content callback is null
	 * @throws UnsupportedAlgorithmException If no registered provider serves the algorithm
	 * @throws CryptoException If the key is not usable for the algorithm
	 */
	public static @NonNull Signer signer(@NonNull NativeSignatureAlgorithm algorithm, @NonNull PrivateKey key) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			Signature signature = Signature.getInstance(algorithm.jcaName());
			signature.initSign(key);
			return new Signer(algorithm, signature);
		} catch (NoSuchAlgorithmException e) {
			throw UnsupportedAlgorithmException.forAlgorithm(algorithm.jcaName(), e);
		} catch (InvalidKeyException e) {
			throw new CryptoException("Invalid private key for " + algorithm.name(), e);
		}
	}
	
	/**
	 * Creates a new incremental verifier for the given scheme.<br>
	 * Only the schemes that read the message once can be served incrementally, as for {@link #signer}.<br>
	 *
	 * @param algorithm The scheme to verify with
	 * @param key The public key to verify with
	 * @return The created verifier
	 * @throws NullPointerException If the algorithm or the key is null
	 * @throws UnsupportedAlgorithmException If no registered provider serves the algorithm
	 * @throws CryptoException If the key is not usable for the algorithm
	 */
	public static @NonNull Verifier verifier(@NonNull NativeSignatureAlgorithm algorithm, @NonNull PublicKey key) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			Signature signature = Signature.getInstance(algorithm.jcaName());
			signature.initVerify(key);
			return new Verifier(algorithm, signature);
		} catch (NoSuchAlgorithmException e) {
			throw UnsupportedAlgorithmException.forAlgorithm(algorithm.jcaName(), e);
		} catch (InvalidKeyException e) {
			throw new CryptoException("Invalid public key for " + algorithm.name(), e);
		}
	}
	
	/**
	 * The single point where the hybrid split happens while signing.<br>
	 * <p>
	 *     Every signing overload routes through here, so no overload can forget the hybrid case.<br>
	 *     The content is fed in once per component, which is why it is a callback rather than a buffer.
	 * </p>
	 *
	 * @param algorithm The scheme to sign with
	 * @param key The private key to sign with
	 * @param content The callback feeding the message into a signer
	 * @return The produced signature
	 * @throws NullPointerException If the algorithm or the key is null
	 * @throws ClassCastException If a hybrid scheme is given a key that is not a hybrid private key
	 */
	private static byte @NonNull [] dispatchSign(@NonNull SignatureAlgorithm algorithm, @NonNull PrivateKey key, @NonNull Consumer<Signer> content) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(content, "Content callback must not be null");
		
		return switch (algorithm) {
			case NativeSignatureAlgorithm n -> {
				Signer signer = signer(n, key);
				content.accept(signer);
				yield signer.sign();
			}
			case HybridSignatureAlgorithm hybrid -> {
				HybridPrivateKey hybridKey = (HybridPrivateKey) key;
				byte[] classical = dispatchSign(hybrid.classical(), hybridKey.classical(), content);
				byte[] postQuantum = dispatchSign(hybrid.postQuantum(), hybridKey.postQuantum(), content);
				yield HybridSignature.encode(classical, postQuantum);
			}
		};
	}
	
	/**
	 * The single point where the hybrid split happens while verifying.<br>
	 *
	 * @param algorithm The scheme to verify with
	 * @param key The public key to verify with
	 * @param content The callback feeding the message into a verifier
	 * @param signature The signature to check
	 * @return True if the signature matches the message and the key
	 * @throws NullPointerException If the algorithm, the key, the content callback or the signature is null
	 * @throws ClassCastException If a hybrid scheme is given a key that is not a hybrid public key
	 * @throws MalformedDataException If a hybrid signature is malformed
	 */
	private static boolean dispatchVerify(@NonNull SignatureAlgorithm algorithm, @NonNull PublicKey key, @NonNull Consumer<Verifier> content, byte @NonNull [] signature) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(content, "Content callback must not be null");
		Objects.requireNonNull(signature, "Signature must not be null");
		
		return switch (algorithm) {
			case NativeSignatureAlgorithm n -> {
				Verifier verifier = verifier(n, key);
				content.accept(verifier);
				yield verifier.verify(signature);
			}
			case HybridSignatureAlgorithm hybrid -> {
				HybridPublicKey hybridKey = (HybridPublicKey) key;
				HybridSignature parts = HybridSignature.parse(signature);
				yield dispatchVerify(hybrid.classical(), hybridKey.classical(), content, parts.classical()) && dispatchVerify(hybrid.postQuantum(), hybridKey.postQuantum(), content, parts.postQuantum());
			}
		};
	}
}
