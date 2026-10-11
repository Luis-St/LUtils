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

import net.luis.utils.crypto.Pems;
import net.luis.utils.crypto.algorithm.SignatureAlgorithm;
import net.luis.utils.crypto.exception.*;
import net.luis.utils.crypto.util.PemDocument;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Objects;

/**
 * PKCS#10 certificate signing requests.<br>
 * <p>
 *     A request carries a subject name, a public key and the subject alternative names it asks for, signed with the matching private key.<br>
 *     The signature proves possession of the key, nothing more.<br>
 *     Whether the requester may use the names it asks for is for the issuer to decide, which is why {@link #sign} never copies the requested names on its own.
 * </p>
 * <p>
 *     Example:
 * </p>
 * <pre>{@code
 * KeyPair pair = Signatures.generateKeyPair(SignatureAlgorithm.ECDSA_P256_SHA_256);
 * byte[] der = CertificateRequests.create(DistinguishedName.ofCommonName("api.example.com"), pair, SignatureAlgorithm.ECDSA_P256_SHA_256, List.of(new SubjectAltName.Dns("api.example.com")));
 *
 * // On the issuing side, after checking that the requester owns the names
 * CertificateRequest request = CertificateRequests.parse(der);
 * CertificateSpec spec = CertificateSpec.of(Instant.now(), Duration.ofDays(90)).withSubjectAltNames(request.subjectAltNames()).withExtendedKeyUsage(Set.of(ExtendedKeyUsage.SERVER_AUTH));
 * X509Certificate certificate = CertificateRequests.sign(request, ca, caKey, SignatureAlgorithm.ED25519, spec);
 * }</pre>
 *
 * @author Luis-St
 */
public final class CertificateRequests {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private CertificateRequests() {}
	
	/**
	 * Creates a DER encoded certificate signing request.<br>
	 *
	 * @param subject The subject name to ask a certificate for
	 * @param pair The key pair to ask a certificate for, whose private key signs the request
	 * @param algorithm The scheme to sign the request with, which must match the key pair
	 * @param subjectAltNames The subject alternative names to ask for, empty to ask for none
	 * @return The encoded request
	 * @throws NullPointerException If any parameter is null, or the names hold null
	 * @throws UnsupportedAlgorithmException If the scheme cannot sign a request
	 * @throws CryptoException If signing or encoding fails
	 */
	public static byte @NonNull [] create(@NonNull DistinguishedName subject, @NonNull KeyPair pair, @NonNull SignatureAlgorithm algorithm, @NonNull List<SubjectAltName> subjectAltNames) {
		Objects.requireNonNull(subject, "Subject must not be null");
		Objects.requireNonNull(pair, "Key pair must not be null");
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(subjectAltNames, "Subject alternative names must not be null");
		X509Support.requireCertificateScheme(algorithm);
		
		PKCS10CertificationRequestBuilder builder = new PKCS10CertificationRequestBuilder(subject.toX500Name(), X509Support.publicKeyInfo(pair.getPublic()));
		if (!subjectAltNames.isEmpty()) {
			try {
				Extension extension = new Extension(Extension.subjectAlternativeName, false, X509Support.generalNames(List.copyOf(subjectAltNames)).getEncoded());
				builder.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest, new Extensions(extension));
			} catch (IOException e) {
				throw new CryptoException("Cannot encode the subject alternative names", e);
			}
		}
		
		try {
			return builder.build(X509Support.contentSigner(algorithm, pair.getPrivate())).getEncoded();
		} catch (IOException e) {
			throw new CryptoException("Cannot encode the certificate request", e);
		}
	}
	
	/**
	 * Parses a DER encoded certificate signing request.<br>
	 * The signature is not checked, see {@link CertificateRequest#verify()}.<br>
	 *
	 * @param der The encoded request
	 * @return The parsed request
	 * @throws NullPointerException If the encoded request is null
	 * @throws MalformedDataException If the data is not a certificate signing request, or its key cannot be read
	 */
	public static @NonNull CertificateRequest parse(byte @NonNull [] der) {
		Objects.requireNonNull(der, "Encoded request must not be null");
		
		PKCS10CertificationRequest request;
		try {
			request = new PKCS10CertificationRequest(der);
		} catch (IOException e) {
			throw new MalformedDataException("Malformed certificate request", e);
		}
		return new CertificateRequest(request);
	}
	
	/**
	 * Parses the certificate signing request held by the given PEM document.<br>
	 *
	 * @param document The document holding the request
	 * @return The parsed request
	 * @throws NullPointerException If the document is null
	 * @throws MalformedDataException If the document does not carry the request label, or holds a malformed request
	 */
	public static @NonNull CertificateRequest parse(@NonNull PemDocument document) {
		Objects.requireNonNull(document, "Document must not be null");
		
		if (!Pems.CERTIFICATE_REQUEST.equals(document.label())) {
			throw new MalformedDataException("Expected a '" + Pems.CERTIFICATE_REQUEST + "' PEM document, got '" + document.label() + "'");
		}
		return parse(document.content());
	}
	
	/**
	 * Issues a certificate for the given request.<br>
	 * <p>
	 *     The subject name and the public key come from the request, everything else comes from the spec.<br>
	 *     The subject alternative names the request asks for are deliberately not copied.<br>
	 *     Pass {@link CertificateRequest#subjectAltNames()} into the spec once the requester is known to own them.
	 * </p>
	 *
	 * @param request The request to issue a certificate for
	 * @param ca The certificate of the issuing authority
	 * @param caKey The private key of the issuing authority
	 * @param algorithm The scheme the authority signs with, which must match its key
	 * @param spec What the certificate says apart from the subject's name and key
	 * @return The issued certificate
	 * @throws NullPointerException If any parameter is null
	 * @throws IllegalArgumentException If the certificate of the authority is not a certificate authority
	 * @throws AuthenticationException If the signature of the request does not verify
	 * @throws UnsupportedAlgorithmException If the scheme cannot sign a certificate
	 * @throws CryptoException If issuing fails
	 */
	public static @NonNull X509Certificate sign(@NonNull CertificateRequest request, @NonNull X509Certificate ca, @NonNull PrivateKey caKey, @NonNull SignatureAlgorithm algorithm, @NonNull CertificateSpec spec) {
		Objects.requireNonNull(request, "Request must not be null");
		Objects.requireNonNull(ca, "Certificate authority must not be null");
		Objects.requireNonNull(caKey, "Certificate authority key must not be null");
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(spec, "Spec must not be null");
		X509Support.requireCertificateScheme(algorithm);
		if (ca.getBasicConstraints() < 0) {
			throw new IllegalArgumentException("Issuer " + ca.getSubjectX500Principal().getName() + " is not a certificate authority");
		}
		requireValidSignature(request);
		
		return issue(request, ca, caKey, algorithm, spec);
	}
	
	/**
	 * Ensures that the signature of the given request verifies.<br>
	 *
	 * @param request The request to check
	 * @throws NullPointerException If the request is null
	 * @throws AuthenticationException If the signature of the request does not verify
	 * @throws CryptoException If the signature cannot be checked
	 */
	static void requireValidSignature(@NonNull CertificateRequest request) {
		Objects.requireNonNull(request, "Request must not be null");
		
		if (!request.verify()) {
			throw new AuthenticationException("Signature verification failed for the certificate request");
		}
	}
	
	/**
	 * Issues a certificate for the given request without checking the request or the authority.<br>
	 * The caller has to ensure that the signature of the request verifies and that the issuer is a certificate authority.<br>
	 *
	 * @param request The request to issue a certificate for
	 * @param ca The certificate of the issuing authority
	 * @param caKey The private key of the issuing authority
	 * @param algorithm The scheme the authority signs with
	 * @param spec What the certificate says apart from the subject's name and key
	 * @return The issued certificate
	 * @throws NullPointerException If any parameter is null
	 * @throws UnsupportedAlgorithmException If the scheme cannot sign a certificate
	 * @throws CryptoException If issuing fails
	 */
	static @NonNull X509Certificate issue(@NonNull CertificateRequest request, @NonNull X509Certificate ca, @NonNull PrivateKey caKey, @NonNull SignatureAlgorithm algorithm, @NonNull CertificateSpec spec) {
		Objects.requireNonNull(request, "Request must not be null");
		Objects.requireNonNull(ca, "Certificate authority must not be null");
		Objects.requireNonNull(caKey, "Certificate authority key must not be null");
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(spec, "Spec must not be null");
		
		PKCS10CertificationRequest parsed = request.request();
		return X509Support.issue(
			parsed.getSubject(), parsed.getSubjectPublicKeyInfo(), X509Support.name(ca.getSubjectX500Principal()),
			X509Support.authorityKeyIdentifier(ca), caKey, algorithm, spec
		);
	}
}
