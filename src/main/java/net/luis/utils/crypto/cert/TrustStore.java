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

package net.luis.utils.crypto.cert;

import net.luis.utils.crypto.exception.CryptoException;
import net.luis.utils.crypto.exception.MalformedDataException;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * An immutable set of trust anchors, the certificates a chain has to end in to be trusted.<br>
 * <p>
 *     An anchor is trusted by being in the store, nothing about it is checked.<br>
 *     Its validity period, key usage and signature do not matter, which is why only certificates that were obtained through a trusted channel belong here.
 * </p>
 * <p>
 *     Example:
 * </p>
 * <pre>{@code
 * // The public authorities of the JDK, plus the root of a private infrastructure
 * TrustStore trust = TrustStore.system().union(TrustStore.fromPem(Path.of("internal-root.pem")));
 *
 * SSLContext context = SSLContext.getInstance("TLS");
 * context.init(null, new TrustManager[] { trust.trustManager() }, null);
 * }</pre>
 *
 * @author Luis-St
 */
public final class TrustStore {
	
	/**
	 * The algorithm of the trust manager factory and of certificate path validation.<br>
	 */
	static final String PKIX = "PKIX";
	
	/**
	 * The anchors of this store, in the order they were added.<br>
	 */
	private final List<X509Certificate> certificates;
	
	/**
	 * Constructs a new trust store holding the given anchors.<br>
	 *
	 * @param certificates The anchors, duplicates removed
	 * @throws NullPointerException If the certificates are null or hold null
	 */
	private TrustStore(@NonNull Collection<X509Certificate> certificates) {
		Objects.requireNonNull(certificates, "Certificates must not be null");
		this.certificates = List.copyOf(new LinkedHashSet<>(certificates));
	}
	
	/**
	 * Creates a trust store holding the given anchors.<br>
	 *
	 * @param certificates The anchors
	 * @return The created trust store
	 * @throws NullPointerException If the certificates are null or hold null
	 * @throws IllegalArgumentException If no certificate is given
	 */
	public static @NonNull TrustStore of(X509Certificate @NonNull ... certificates) {
		Objects.requireNonNull(certificates, "Certificates must not be null");
		return of(List.of(certificates));
	}
	
	/**
	 * Creates a trust store holding the given anchors.<br>
	 *
	 * @param certificates The anchors
	 * @return The created trust store
	 * @throws NullPointerException If the certificates are null or hold null
	 * @throws IllegalArgumentException If no certificate is given
	 */
	public static @NonNull TrustStore of(@NonNull Collection<X509Certificate> certificates) {
		Objects.requireNonNull(certificates, "Certificates must not be null");
		if (certificates.isEmpty()) {
			throw new IllegalArgumentException("A trust store needs at least one anchor");
		}
		
		return new TrustStore(certificates);
	}
	
	/**
	 * Reads a trust store from a PEM file holding one or more certificates.<br>
	 *
	 * @param file The file to read
	 * @return The read trust store
	 * @throws NullPointerException If the file is null
	 * @throws MalformedDataException If the file holds no certificate, a malformed one, or a document that is not a certificate
	 * @throws UncheckedIOException If reading fails
	 */
	public static @NonNull TrustStore fromPem(@NonNull Path file) {
		Objects.requireNonNull(file, "File must not be null");
		
		List<X509Certificate> certificates;
		try {
			certificates = Certificates.parseChain(Files.readString(file, StandardCharsets.US_ASCII));
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to read the pem file: " + file, e);
		}
		
		if (certificates.isEmpty()) {
			throw new MalformedDataException("No certificate in " + file);
		}
		return new TrustStore(certificates);
	}
	
	/**
	 * Returns the trust store the JDK uses by default.<br>
	 * <p>
	 *     This is the {@code cacerts} file of the running JDK, or the store the {@code javax.net.ssl.trustStore} system property points to.<br>
	 *     The anchors are read once per call, so a changed file is picked up by calling this again.
	 * </p>
	 *
	 * @return The system trust store
	 * @throws CryptoException If the default trust store cannot be loaded
	 */
	public static @NonNull TrustStore system() {
		try {
			TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			factory.init((KeyStore) null);
			return new TrustStore(Arrays.stream(factory.getTrustManagers())
				.filter(X509TrustManager.class::isInstance)
				.map(X509TrustManager.class::cast)
				.flatMap(manager -> Arrays.stream(manager.getAcceptedIssuers()))
				.toList());
		} catch (NoSuchAlgorithmException | KeyStoreException e) {
			throw new CryptoException("Cannot load the system trust store", e);
		}
	}
	
	/**
	 * Returns the anchors of this store, in the order they were added.<br>
	 * @return The anchors
	 */
	public @NonNull @Unmodifiable List<X509Certificate> certificates() {
		return this.certificates;
	}
	
	/**
	 * Returns whether this store holds no anchor.<br>
	 * Only the system store can be empty, and only on a runtime without any trusted authority.<br>
	 *
	 * @return True if this store is empty
	 */
	public boolean isEmpty() {
		return this.certificates.isEmpty();
	}
	
	/**
	 * Returns whether the given certificate is an anchor of this store.<br>
	 *
	 * @param certificate The certificate to check
	 * @return True if the certificate is an anchor
	 * @throws NullPointerException If the certificate is null
	 */
	public boolean contains(@NonNull X509Certificate certificate) {
		Objects.requireNonNull(certificate, "Certificate must not be null");
		return this.certificates.contains(certificate);
	}
	
	/**
	 * Returns a store holding the anchors of this store and of the given one.<br>
	 *
	 * @param other The store to combine with
	 * @return The combined store
	 * @throws NullPointerException If the other store is null
	 */
	public @NonNull TrustStore union(@NonNull TrustStore other) {
		Objects.requireNonNull(other, "Other trust store must not be null");
		
		List<X509Certificate> combined = new ArrayList<>(this.certificates);
		combined.addAll(other.certificates);
		return new TrustStore(combined);
	}
	
	/**
	 * Returns the anchors of this store as trust anchors for certificate path validation.<br>
	 * @return The trust anchors
	 */
	@NonNull Set<TrustAnchor> anchors() {
		Set<TrustAnchor> anchors = new LinkedHashSet<>();
		for (X509Certificate certificate : this.certificates) {
			anchors.add(new TrustAnchor(certificate, null));
		}
		return anchors;
	}
	
	/**
	 * Creates a trust manager that accepts exactly the chains ending in an anchor of this store.<br>
	 * <p>
	 *     The trust manager is the PKIX one of the JDK, so it checks validity periods, key usages and signatures along the chain.<br>
	 *     Revocation is checked only as far as the JDK is configured to check it.
	 * </p>
	 *
	 * @return The created trust manager
	 * @throws CryptoException If the trust manager cannot be created
	 */
	public @NonNull X509TrustManager trustManager() {
		try {
			KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
			store.load(null, null);
			for (int i = 0; i < this.certificates.size(); i++) {
				store.setCertificateEntry("anchor-" + i, this.certificates.get(i));
			}
			
			TrustManagerFactory factory = TrustManagerFactory.getInstance(PKIX);
			factory.init(store);
			return Arrays.stream(factory.getTrustManagers())
				.filter(X509TrustManager.class::isInstance)
				.map(X509TrustManager.class::cast)
				.findFirst()
				.orElseThrow(() -> new CryptoException("The PKIX trust manager factory produced no X.509 trust manager"));
		} catch (GeneralSecurityException | IOException e) {
			throw new CryptoException("Cannot create a trust manager", e);
		}
	}
	
	@Override
	public String toString() {
		return "TrustStore[anchors=" + this.certificates.size() + "]";
	}
}
