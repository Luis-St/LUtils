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

import net.luis.utils.crypto.algorithm.SignatureAlgorithm;
import net.luis.utils.crypto.exception.CryptoException;
import net.luis.utils.crypto.exception.UnsupportedAlgorithmException;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * Builder class for issuing X.509 certificates.<br>
 * Provides a fluent API for setting the subject, the issuer and the extensions of a certificate.<br>
 * <p>
 *     The subject and authority key identifiers and the basic constraints are always added, nothing has to ask for them.<br>
 *     The serial number defaults to a random positive 128 bit number.<br>
 *     The subject, the subject key, the issuer and the validity have no default and must be set.
 * </p>
 * <p>
 *     Every native signature scheme of this library can sign a certificate, including ML-DSA, SLH-DSA and the composite schemes, as long as a registered provider serves it.<br>
 *     The hybrid schemes cannot, because their signature format is private to this library and no certificate parser understands it.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * KeyPair rootPair = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
 * X509Certificate root = Certificates.builder()
 *     .subject(DistinguishedName.ofCommonName("Example Root"))
 *     .selfSigned(rootPair)
 *     .validity(Instant.now(), Duration.ofDays(3650))
 *     .keyUsage(Set.of(KeyUsage.KEY_CERT_SIGN, KeyUsage.CRL_SIGN))
 *     .ca(0)
 *     .build(SignatureAlgorithm.ED25519);
 *
 * X509Certificate server = Certificates.builder()
 *     .subject(DistinguishedName.ofCommonName("api.example.com"))
 *     .subjectKey(serverPair.getPublic())
 *     .issuer(root, rootPair.getPrivate())
 *     .validity(Instant.now(), Duration.ofDays(90))
 *     .subjectAltName(new SubjectAltName.Dns("api.example.com"))
 *     .extendedKeyUsage(Set.of(ExtendedKeyUsage.SERVER_AUTH))
 *     .build(SignatureAlgorithm.ED25519);
 * }</pre>
 *
 * @see Certificates#builder()
 * @see CertificateSpec
 *
 * @author Luis-St
 */
public final class CertificateBuilder {
	
	/**
	 * The subject name, or null if not set yet.<br>
	 */
	private @Nullable DistinguishedName subject;
	/**
	 * The subject public key, or null if not set yet.<br>
	 */
	private @Nullable PublicKey subjectKey;
	/**
	 * The issuing certificate, or null if the certificate is self-signed or the issuer is not set yet.<br>
	 */
	private @Nullable X509Certificate issuerCertificate;
	/**
	 * The private key signing the certificate, or null if not set yet.<br>
	 */
	private @Nullable PrivateKey issuerKey;
	/**
	 * The serial number, or null to draw a random one.<br>
	 */
	private @Nullable BigInteger serial;
	/**
	 * The first instant the certificate is valid at, or null if not set yet.<br>
	 */
	private @Nullable Instant notBefore;
	/**
	 * How long the certificate is valid for, or null if not set yet.<br>
	 */
	private @Nullable Duration validity;
	/**
	 * The subject alternative names.<br>
	 */
	private List<SubjectAltName> subjectAltNames = List.of();
	/**
	 * The key usages.<br>
	 */
	private Set<KeyUsage> keyUsage = Set.of();
	/**
	 * The extended key usages.<br>
	 */
	private Set<ExtendedKeyUsage> extendedKeyUsage = Set.of();
	/**
	 * The path length of an authority, or null if the certificate is not an authority.<br>
	 */
	private @Nullable Integer pathLength;
	
	/**
	 * Constructs a new builder with nothing set.<br>
	 */
	CertificateBuilder() {}
	
	/**
	 * Sets the subject name.<br>
	 *
	 * @param subject The subject name
	 * @return This builder for method chaining
	 * @throws NullPointerException If the subject is null
	 */
	public @NonNull CertificateBuilder subject(@NonNull DistinguishedName subject) {
		this.subject = Objects.requireNonNull(subject, "Subject must not be null");
		return this;
	}
	
	/**
	 * Sets the public key the certificate is issued for.<br>
	 * Not needed for a self-signed certificate, {@link #selfSigned(KeyPair)} sets it.<br>
	 *
	 * @param subjectKey The subject public key
	 * @return This builder for method chaining
	 * @throws NullPointerException If the subject key is null
	 */
	public @NonNull CertificateBuilder subjectKey(@NonNull PublicKey subjectKey) {
		this.subjectKey = Objects.requireNonNull(subjectKey, "Subject key must not be null");
		return this;
	}
	
	/**
	 * Sets the authority issuing the certificate.<br>
	 * This replaces an earlier call to {@link #selfSigned(KeyPair)}.<br>
	 *
	 * @param certificate The certificate of the issuing authority
	 * @param key The private key of the issuing authority
	 * @return This builder for method chaining
	 * @throws NullPointerException If the certificate or the key is null
	 * @throws IllegalArgumentException If the certificate is not a certificate authority
	 */
	public @NonNull CertificateBuilder issuer(@NonNull X509Certificate certificate, @NonNull PrivateKey key) {
		Objects.requireNonNull(certificate, "Certificate must not be null");
		Objects.requireNonNull(key, "Key must not be null");
		if (certificate.getBasicConstraints() < 0) {
			throw new IllegalArgumentException("Issuer " + certificate.getSubjectX500Principal().getName() + " is not a certificate authority");
		}
		
		this.issuerCertificate = certificate;
		this.issuerKey = key;
		return this;
	}
	
	/**
	 * Makes the certificate self-signed with the given key pair.<br>
	 * The public key becomes the subject key and the private key signs the certificate.<br>
	 * This replaces an earlier call to {@link #issuer(X509Certificate, PrivateKey)}.<br>
	 *
	 * @param pair The key pair of the subject
	 * @return This builder for method chaining
	 * @throws NullPointerException If the key pair is null
	 */
	public @NonNull CertificateBuilder selfSigned(@NonNull KeyPair pair) {
		Objects.requireNonNull(pair, "Key pair must not be null");
		
		this.subjectKey = pair.getPublic();
		this.issuerCertificate = null;
		this.issuerKey = pair.getPrivate();
		return this;
	}
	
	/**
	 * Sets the serial number instead of drawing a random one.<br>
	 *
	 * @param serial The serial number
	 * @return This builder for method chaining
	 * @throws NullPointerException If the serial is null
	 */
	public @NonNull CertificateBuilder serial(@NonNull BigInteger serial) {
		this.serial = Objects.requireNonNull(serial, "Serial must not be null");
		return this;
	}
	
	/**
	 * Sets the validity period of the certificate.<br>
	 *
	 * @param notBefore The first instant the certificate is valid at
	 * @param validity How long the certificate is valid for
	 * @return This builder for method chaining
	 * @throws NullPointerException If the not before instant or the validity is null
	 */
	public @NonNull CertificateBuilder validity(@NonNull Instant notBefore, @NonNull Duration validity) {
		this.notBefore = Objects.requireNonNull(notBefore, "Not before must not be null");
		this.validity = Objects.requireNonNull(validity, "Validity must not be null");
		return this;
	}
	
	/**
	 * Sets the subject alternative names, replacing any set before.<br>
	 *
	 * @param names The subject alternative names
	 * @return This builder for method chaining
	 * @throws NullPointerException If the names are null or hold null
	 */
	public @NonNull CertificateBuilder subjectAltName(SubjectAltName @NonNull ... names) {
		Objects.requireNonNull(names, "Names must not be null");
		this.subjectAltNames = List.of(names);
		return this;
	}
	
	/**
	 * Sets the key usages, replacing any set before.<br>
	 *
	 * @param usages The key usages
	 * @return This builder for method chaining
	 * @throws NullPointerException If the usages are null or hold null
	 */
	public @NonNull CertificateBuilder keyUsage(@NonNull Set<KeyUsage> usages) {
		Objects.requireNonNull(usages, "Usages must not be null");
		
		this.keyUsage = Set.copyOf(usages);
		return this;
	}
	
	/**
	 * Sets the extended key usages, replacing any set before.<br>
	 *
	 * @param usages The extended key usages
	 * @return This builder for method chaining
	 * @throws NullPointerException If the usages are null or hold null
	 */
	public @NonNull CertificateBuilder extendedKeyUsage(@NonNull Set<ExtendedKeyUsage> usages) {
		Objects.requireNonNull(usages, "Usages must not be null");
		
		this.extendedKeyUsage = Set.copyOf(usages);
		return this;
	}
	
	/**
	 * Makes the certificate a certificate authority.<br>
	 *
	 * @param pathLength The maximum number of intermediate authorities below this one, zero if it may only issue end entity certificates
	 * @return This builder for method chaining
	 * @throws IllegalArgumentException If the path length is negative
	 */
	public @NonNull CertificateBuilder ca(int pathLength) {
		if (pathLength < 0) {
			throw new IllegalArgumentException("Path length must not be negative, was " + pathLength);
		}
		
		this.pathLength = pathLength;
		return this;
	}
	
	/**
	 * Returns the spec collected by this builder.<br>
	 *
	 * @return The spec
	 * @throws IllegalStateException If the validity is not set
	 * @throws IllegalArgumentException If the serial or the validity is out of range
	 */
	public @NonNull CertificateSpec spec() {
		if (this.notBefore == null || this.validity == null) {
			throw new IllegalStateException("Validity must be set");
		}
		return new CertificateSpec(this.serial, this.notBefore, this.validity, this.subjectAltNames, this.keyUsage, this.extendedKeyUsage, this.pathLength);
	}
	
	/**
	 * Builds and signs the certificate.<br>
	 *
	 * @param algorithm The scheme the issuer signs with, which must match the issuer's key
	 * @return The issued certificate
	 * @throws NullPointerException If the algorithm is null
	 * @throws IllegalStateException If the subject, the subject key, the issuer or the validity is not set
	 * @throws IllegalArgumentException If the serial or the validity is out of range
	 * @throws UnsupportedAlgorithmException If the scheme cannot sign a certificate
	 * @throws CryptoException If signing fails
	 */
	public @NonNull X509Certificate build(@NonNull SignatureAlgorithm algorithm) {
		X509Support.requireCertificateScheme(algorithm);
		if (this.subject == null) {
			throw new IllegalStateException("Subject must be set");
		}
		if (this.subjectKey == null) {
			throw new IllegalStateException("Subject key must be set");
		}
		if (this.issuerKey == null) {
			throw new IllegalStateException("Issuer must be set, either through issuer or through selfSigned");
		}
		
		CertificateSpec spec = this.spec();
		SubjectPublicKeyInfo subjectKeyInfo = X509Support.publicKeyInfo(this.subjectKey);
		if (this.issuerCertificate == null) {
			return X509Support.issue(this.subject.toX500Name(), subjectKeyInfo, this.subject.toX500Name(), X509Support.keyIdentifier(subjectKeyInfo), this.issuerKey, algorithm, spec);
		}
		return X509Support.issue(
			this.subject.toX500Name(), subjectKeyInfo, X509Support.name(this.issuerCertificate.getSubjectX500Principal()), X509Support.authorityKeyIdentifier(this.issuerCertificate), this.issuerKey, algorithm, spec
		);
	}
}
