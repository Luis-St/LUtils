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

import net.luis.utils.crypto.Signatures;
import net.luis.utils.crypto.algorithm.SignatureAlgorithm;
import net.luis.utils.crypto.exception.*;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v2CRLBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.jspecify.annotations.NonNull;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * A small certificate authority for a private public key infrastructure.<br>
 * <p>
 *     The authority holds its root certificate, the root's private key and a serial counter, and issues end entity certificates from them.<br>
 *     It also keeps the serial numbers it has revoked and publishes them as a signed certificate revocation list.
 * </p>
 * <p>
 *     Nothing is persisted.<br>
 *     To carry an authority across restarts, store the root and its key, and restore it with {@link #of} starting at the serial {@link #nextSerial()} returned before.<br>
 *     Restarting the counter would issue a serial number twice, which RFC 5280 forbids.<br>
 *     The revoked serial numbers have to be stored and revoked again for the same reason.
 * </p>
 * <p>
 *     Instances are thread-safe.
 * </p>
 * <p>
 *     Example:
 * </p>
 * <pre>{@code
 * CertificateAuthority ca = CertificateAuthority.create(DistinguishedName.ofCommonName("Example Root"), SignatureAlgorithm.ED25519, Duration.ofDays(3650));
 *
 * X509Certificate server = ca.issueServer(DistinguishedName.ofCommonName("api.example.com"), serverKey, Duration.ofDays(90), new SubjectAltName.Dns("api.example.com"));
 * X509Certificate client = ca.issueClient(DistinguishedName.ofCommonName("worker-1"), clientKey, Duration.ofDays(30));
 *
 * ca.revoke(client.getSerialNumber(), RevocationReason.KEY_COMPROMISE);
 * X509CRL crl = ca.crl();
 * }</pre>
 *
 * @author Luis-St
 */
public final class CertificateAuthority {
	
	/**
	 * How long a revocation list is valid for when no validity is given.<br>
	 */
	private static final Duration DEFAULT_CRL_VALIDITY = Duration.ofDays(7);
	/**
	 * How far the not before instant of an issued certificate lies in the past, so a peer with a slightly late clock accepts it at once.<br>
	 */
	private static final Duration CLOCK_SKEW = Duration.ofMinutes(5);
	/**
	 * The message signed when restoring an authority, to check that the key belongs to the root certificate.<br>
	 */
	private static final byte[] KEY_PROBE = "lutils-certificate-authority-key-probe".getBytes(StandardCharsets.UTF_8);
	
	/**
	 * The root certificate of this authority.<br>
	 */
	private final X509Certificate certificate;
	/**
	 * The private key of the root certificate.<br>
	 */
	private final PrivateKey key;
	/**
	 * The scheme this authority signs with.<br>
	 */
	private final SignatureAlgorithm algorithm;
	/**
	 * The revoked serial numbers and their revocation, in the order they were revoked.<br>
	 */
	private final Map<BigInteger, Revocation> revoked = new LinkedHashMap<>();
	/**
	 * The serial number the next certificate is issued with.<br>
	 */
	private BigInteger nextSerial;
	/**
	 * The number the next revocation list is published with.<br>
	 */
	private BigInteger nextCrlNumber = BigInteger.ONE;
	
	/**
	 * Constructs a new certificate authority.<br>
	 *
	 * @param certificate The root certificate
	 * @param key The private key of the root certificate
	 * @param algorithm The scheme the authority signs with
	 * @param nextSerial The serial number the next certificate is issued with
	 * @throws NullPointerException If any parameter is null
	 */
	private CertificateAuthority(@NonNull X509Certificate certificate, @NonNull PrivateKey key, @NonNull SignatureAlgorithm algorithm, @NonNull BigInteger nextSerial) {
		this.certificate = Objects.requireNonNull(certificate, "Certificate must not be null");
		this.key = Objects.requireNonNull(key, "Key must not be null");
		this.algorithm = Objects.requireNonNull(algorithm, "Algorithm must not be null");
		this.nextSerial = Objects.requireNonNull(nextSerial, "Next serial must not be null");
	}
	
	/**
	 * Creates a new authority with a freshly generated key and a self-signed root certificate.<br>
	 * The root may only issue end entity certificates, its path length is zero.<br>
	 *
	 * @param subject The subject name of the root
	 * @param algorithm The scheme to generate the root key for and to sign with
	 * @param validity How long the root certificate is valid for
	 * @return The created authority
	 * @throws NullPointerException If any parameter is null
	 * @throws IllegalArgumentException If the validity is not positive
	 * @throws UnsupportedAlgorithmException If the scheme cannot sign a certificate
	 * @throws CryptoException If issuing the root fails
	 */
	public static @NonNull CertificateAuthority create(@NonNull DistinguishedName subject, @NonNull SignatureAlgorithm algorithm, @NonNull Duration validity) {
		Objects.requireNonNull(subject, "Subject must not be null");
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(validity, "Validity must not be null");
		X509Support.requireCertificateScheme(algorithm);
		
		KeyPair pair = Signatures.generateKeyPair(algorithm);
		X509Certificate root = Certificates.builder()
			.subject(subject)
			.selfSigned(pair)
			.validity(Instant.now().minus(CLOCK_SKEW), validity)
			.keyUsage(Set.of(KeyUsage.KEY_CERT_SIGN, KeyUsage.CRL_SIGN, KeyUsage.DIGITAL_SIGNATURE))
			.ca(0)
			.build(algorithm);
		return new CertificateAuthority(root, pair.getPrivate(), algorithm, BigInteger.ONE);
	}
	
	/**
	 * Restores an authority from an existing root certificate and key.<br>
	 *
	 * @param certificate The root certificate
	 * @param key The private key of the root certificate
	 * @param algorithm The scheme the authority signs with, which must match the key
	 * @param nextSerial The serial number the next certificate is issued with, see {@link #nextSerial()}
	 * @return The restored authority
	 * @throws NullPointerException If any parameter is null
	 * @throws IllegalArgumentException If the certificate is not a certificate authority, if the serial is not positive, or if the key does not belong to the certificate
	 * @throws UnsupportedAlgorithmException If the scheme cannot sign a certificate
	 * @throws CryptoException If the key or the key of the certificate is not usable for the scheme
	 */
	public static @NonNull CertificateAuthority of(@NonNull X509Certificate certificate, @NonNull PrivateKey key, @NonNull SignatureAlgorithm algorithm, @NonNull BigInteger nextSerial) {
		Objects.requireNonNull(certificate, "Certificate must not be null");
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(nextSerial, "Next serial must not be null");
		X509Support.requireCertificateScheme(algorithm);
		if (certificate.getBasicConstraints() < 0) {
			throw new IllegalArgumentException("Certificate " + certificate.getSubjectX500Principal().getName() + " is not a certificate authority");
		}
		if (nextSerial.signum() <= 0) {
			throw new IllegalArgumentException("Next serial must be positive, was " + nextSerial);
		}
		if (!Signatures.verify(algorithm, certificate.getPublicKey(), KEY_PROBE, Signatures.sign(algorithm, key, KEY_PROBE))) {
			throw new IllegalArgumentException("Key does not belong to certificate " + certificate.getSubjectX500Principal().getName());
		}
		
		return new CertificateAuthority(certificate, key, algorithm, nextSerial);
	}
	
	/**
	 * Returns the root certificate of this authority.<br>
	 * This is the certificate peers have to trust.<br>
	 *
	 * @return The root certificate
	 */
	public @NonNull X509Certificate certificate() {
		return this.certificate;
	}
	
	/**
	 * Returns the scheme this authority signs with.<br>
	 * @return The signature scheme
	 */
	public @NonNull SignatureAlgorithm algorithm() {
		return this.algorithm;
	}
	
	/**
	 * Returns the serial number the next certificate will be issued with.<br>
	 * Store it to restore this authority later through {@link #of}.<br>
	 *
	 * @return The next serial number
	 */
	public synchronized @NonNull BigInteger nextSerial() {
		return this.nextSerial;
	}
	
	/**
	 * Issues a TLS server certificate.<br>
	 * <p>
	 *     The certificate carries the digital signature key usage and the server authentication extended key usage.<br>
	 *     At least one subject alternative name is required, because hostname verification ignores the common name.
	 * </p>
	 *
	 * @param subject The subject name
	 * @param key The public key of the server
	 * @param validity How long the certificate is valid for
	 * @param names The names the server is reachable under
	 * @return The issued certificate
	 * @throws NullPointerException If any parameter is null, or the names hold null
	 * @throws IllegalArgumentException If no name is given, or if the validity is not positive
	 * @throws CryptoException If issuing fails
	 */
	public @NonNull X509Certificate issueServer(@NonNull DistinguishedName subject, @NonNull PublicKey key, @NonNull Duration validity, SubjectAltName @NonNull ... names) {
		Objects.requireNonNull(validity, "Validity must not be null");
		Objects.requireNonNull(names, "Names must not be null");
		if (names.length == 0) {
			throw new IllegalArgumentException("A server certificate needs at least one subject alternative name");
		}
		
		CertificateSpec spec = CertificateSpec.of(Instant.now().minus(CLOCK_SKEW), validity)
			.withSubjectAltNames(names)
			.withKeyUsage(Set.of(KeyUsage.DIGITAL_SIGNATURE))
			.withExtendedKeyUsage(Set.of(ExtendedKeyUsage.SERVER_AUTH));
		return this.issue(subject, key, spec);
	}
	
	/**
	 * Issues a TLS client certificate.<br>
	 * The certificate carries the digital signature key usage and the client authentication extended key usage.<br>
	 *
	 * @param subject The subject name, which identifies the client to the server
	 * @param key The public key of the client
	 * @param validity How long the certificate is valid for
	 * @param names The subject alternative names, may be empty
	 * @return The issued certificate
	 * @throws NullPointerException If any parameter is null, or the names hold null
	 * @throws IllegalArgumentException If the validity is not positive
	 * @throws CryptoException If issuing fails
	 */
	public @NonNull X509Certificate issueClient(@NonNull DistinguishedName subject, @NonNull PublicKey key, @NonNull Duration validity, SubjectAltName @NonNull ... names) {
		Objects.requireNonNull(validity, "Validity must not be null");
		Objects.requireNonNull(names, "Names must not be null");
		
		CertificateSpec spec = CertificateSpec.of(Instant.now().minus(CLOCK_SKEW), validity)
			.withSubjectAltNames(names)
			.withKeyUsage(Set.of(KeyUsage.DIGITAL_SIGNATURE))
			.withExtendedKeyUsage(Set.of(ExtendedKeyUsage.CLIENT_AUTH));
		return this.issue(subject, key, spec);
	}
	
	/**
	 * Issues a certificate as described by the given spec.<br>
	 * The serial number of the spec is ignored, the certificate is always issued with the next serial of this authority.<br>
	 *
	 * @param subject The subject name
	 * @param key The public key of the subject
	 * @param spec What the certificate says apart from the subject's name and key
	 * @return The issued certificate
	 * @throws NullPointerException If any parameter is null
	 * @throws CryptoException If issuing fails
	 */
	public @NonNull X509Certificate issue(@NonNull DistinguishedName subject, @NonNull PublicKey key, @NonNull CertificateSpec spec) {
		Objects.requireNonNull(subject, "Subject must not be null");
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(spec, "Spec must not be null");
		
		return X509Support.issue(
			subject.toX500Name(), X509Support.publicKeyInfo(key), X509Support.name(this.certificate.getSubjectX500Principal()),
			X509Support.authorityKeyIdentifier(this.certificate), this.key, this.algorithm, spec.withSerial(this.takeSerial())
		);
	}
	
	/**
	 * Issues a certificate for the given certificate signing request.<br>
	 * As for {@link CertificateRequests#sign}, the names the request asks for are not copied.<br>
	 *
	 * @param request The request to issue a certificate for
	 * @param spec What the certificate says apart from the subject's name and key
	 * @return The issued certificate
	 * @throws NullPointerException If the request or the spec is null
	 * @throws AuthenticationException If the signature of the request does not verify
	 * @throws CryptoException If issuing fails
	 */
	public @NonNull X509Certificate sign(@NonNull CertificateRequest request, @NonNull CertificateSpec spec) {
		Objects.requireNonNull(request, "Request must not be null");
		Objects.requireNonNull(spec, "Spec must not be null");
		
		CertificateRequests.requireValidSignature(request);
		
		return CertificateRequests.issue(request, this.certificate, this.key, this.algorithm, spec.withSerial(this.takeSerial()));
	}
	
	/**
	 * Revokes the certificate with the given serial number without giving a reason.<br>
	 *
	 * @param serial The serial number to revoke
	 * @return True if the serial was not revoked before
	 * @throws NullPointerException If the serial is null
	 * @throws IllegalArgumentException If this authority has not issued the serial yet
	 */
	public boolean revoke(@NonNull BigInteger serial) {
		return this.revoke(serial, RevocationReason.UNSPECIFIED);
	}
	
	/**
	 * Revokes the certificate with the given serial number.<br>
	 * A serial that is already revoked keeps its original revocation.<br>
	 *
	 * @param serial The serial number to revoke
	 * @param reason The reason for the revocation
	 * @return True if the serial was not revoked before
	 * @throws NullPointerException If the serial or the reason is null
	 * @throws IllegalArgumentException If this authority has not issued the serial yet
	 */
	public synchronized boolean revoke(@NonNull BigInteger serial, @NonNull RevocationReason reason) {
		Objects.requireNonNull(serial, "Serial must not be null");
		Objects.requireNonNull(reason, "Reason must not be null");
		if (serial.signum() <= 0 || serial.compareTo(this.nextSerial) >= 0) {
			throw new IllegalArgumentException("Serial " + serial + " has not been issued by this authority");
		}
		
		return this.revoked.putIfAbsent(serial, new Revocation(Instant.now(), reason)) == null;
	}
	
	/**
	 * Returns whether the certificate with the given serial number is revoked.<br>
	 *
	 * @param serial The serial number to check
	 * @return True if the serial is revoked
	 * @throws NullPointerException If the serial is null
	 */
	public synchronized boolean isRevoked(@NonNull BigInteger serial) {
		Objects.requireNonNull(serial, "Serial must not be null");
		return this.revoked.containsKey(serial);
	}
	
	/**
	 * Publishes a revocation list valid for seven days.<br>
	 *
	 * @return The signed revocation list
	 * @throws CryptoException If signing fails
	 */
	public @NonNull X509CRL crl() {
		return this.crl(DEFAULT_CRL_VALIDITY);
	}
	
	/**
	 * Publishes a revocation list valid for the given duration.<br>
	 * Every list carries a new, increasing CRL number, so a relying party can tell which of two lists is newer.<br>
	 *
	 * @param validity How long until the next list is due
	 * @return The signed revocation list
	 * @throws NullPointerException If the validity is null
	 * @throws IllegalArgumentException If the validity is not positive
	 * @throws CryptoException If signing fails
	 */
	public synchronized @NonNull X509CRL crl(@NonNull Duration validity) {
		Objects.requireNonNull(validity, "Validity must not be null");
		if (validity.isNegative() || validity.isZero()) {
			throw new IllegalArgumentException("Validity must be positive, was " + validity);
		}
		
		Instant now = Instant.now();
		X509v2CRLBuilder builder = new X509v2CRLBuilder(X509Support.name(this.certificate.getSubjectX500Principal()), Date.from(now));
		builder.setNextUpdate(Date.from(now.plus(validity)));
		this.revoked.forEach((serial, revocation) -> {
			if (revocation.reason() == RevocationReason.UNSPECIFIED) {
				builder.addCRLEntry(serial, Date.from(revocation.at()), null);
			} else {
				builder.addCRLEntry(serial, Date.from(revocation.at()), revocation.reason().code());
			}
		});
		
		try {
			builder.addExtension(Extension.authorityKeyIdentifier, false, new AuthorityKeyIdentifier(X509Support.authorityKeyIdentifier(this.certificate)));
			builder.addExtension(Extension.cRLNumber, false, new CRLNumber(this.nextCrlNumber));
		} catch (CertIOException e) {
			throw new CryptoException("Cannot encode revocation list extensions", e);
		}
		
		try {
			X509CRL crl = new JcaX509CRLConverter().getCRL(builder.build(X509Support.contentSigner(this.algorithm, this.key)));
			this.nextCrlNumber = this.nextCrlNumber.add(BigInteger.ONE);
			return crl;
		} catch (CRLException e) {
			throw new CryptoException("Cannot convert revocation list", e);
		}
	}
	
	/**
	 * Takes the next serial number and advances the counter.<br>
	 * @return The taken serial number
	 */
	private synchronized @NonNull BigInteger takeSerial() {
		BigInteger serial = this.nextSerial;
		this.nextSerial = serial.add(BigInteger.ONE);
		return serial;
	}
	
	/**
	 * A revocation of one serial number.<br>
	 *
	 * @author Luis-St
	 *
	 * @param at The instant the certificate was revoked at
	 * @param reason The reason for the revocation
	 */
	private record Revocation(@NonNull Instant at, @NonNull RevocationReason reason) {
		
		/**
		 * Constructs a new revocation.<br>
		 * @throws NullPointerException If the instant or the reason is null
		 */
		private Revocation {
			Objects.requireNonNull(at, "Instant must not be null");
			Objects.requireNonNull(reason, "Reason must not be null");
		}
	}
}
