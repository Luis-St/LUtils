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

import net.luis.utils.crypto.Hashes;
import net.luis.utils.crypto.Pems;
import net.luis.utils.crypto.algorithm.HashAlgorithm;
import net.luis.utils.crypto.exception.CryptoException;
import net.luis.utils.crypto.exception.MalformedDataException;
import net.luis.utils.crypto.key.KeyId;
import net.luis.utils.crypto.util.PemDocument;
import org.bouncycastle.cert.CertException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.io.ByteArrayInputStream;
import java.security.cert.*;
import java.time.Instant;
import java.util.*;

/**
 * X.509 certificates and certificate revocation lists.<br>
 * <p>
 *     Parsing goes through the certificate factory of the JDK, so a certificate from a public authority is read exactly as the rest of the JDK reads it, whatever scheme it is signed with.<br>
 *     Issuing goes through {@link #builder()}, a {@link CertificateAuthority} or {@link CertificateRequests#sign}.
 * </p>
 * <p>
 *     Example:
 * </p>
 * <pre>{@code
 * List<X509Certificate> chain = Certificates.parseChain(Files.readString(Path.of("fullchain.pem")));
 * X509Certificate leaf = chain.getFirst();
 *
 * byte[] fingerprint = Certificates.fingerprint(HashAlgorithm.SHA_256, leaf);
 * List<SubjectAltName> names = Certificates.subjectAltNames(leaf);
 *
 * if (!Certificates.isValidAt(leaf, Instant.now().plus(Duration.ofDays(14)))) {
 *     renew(leaf);
 * }
 * }</pre>
 *
 * @author Luis-St
 */
public final class Certificates {
	
	/**
	 * The certificate factory type of X.509 certificates and revocation lists.<br>
	 */
	private static final String X509 = "X.509";
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private Certificates() {}
	
	/**
	 * Creates a new builder for issuing a certificate.<br>
	 * @return The created builder
	 */
	public static @NonNull CertificateBuilder builder() {
		return new CertificateBuilder();
	}
	
	/**
	 * Parses a DER encoded certificate.<br>
	 *
	 * @param der The encoded certificate
	 * @return The parsed certificate
	 * @throws NullPointerException If the encoded certificate is null
	 * @throws MalformedDataException If the data is not an X.509 certificate
	 */
	public static @NonNull X509Certificate parse(byte @NonNull [] der) {
		Objects.requireNonNull(der, "Encoded certificate must not be null");
		
		try {
			if (factory().generateCertificate(new ByteArrayInputStream(der)) instanceof X509Certificate certificate) {
				return certificate;
			}
			throw new MalformedDataException("Not an X.509 certificate");
		} catch (CertificateException e) {
			throw new MalformedDataException("Malformed certificate", e);
		}
	}
	
	/**
	 * Parses the certificate held by the given PEM document.<br>
	 *
	 * @param document The document holding the certificate
	 * @return The parsed certificate
	 * @throws NullPointerException If the document is null
	 * @throws MalformedDataException If the document does not carry the certificate label, or holds a malformed certificate
	 */
	public static @NonNull X509Certificate parse(@NonNull PemDocument document) {
		Objects.requireNonNull(document, "Document must not be null");
		
		if (!Pems.CERTIFICATE.equals(document.label())) {
			throw new MalformedDataException("Expected a '" + Pems.CERTIFICATE + "' PEM document, got '" + document.label() + "'");
		}
		return parse(document.content());
	}
	
	/**
	 * Parses every certificate in the given PEM text, in order.<br>
	 * <p>
	 *     A chain is written leaf first, so the first certificate returned is the leaf.<br>
	 *     A document with any other label is rejected, so a private key stored in the same file is never silently skipped.
	 * </p>
	 *
	 * @param pem The text to parse
	 * @return Every certificate in the text, in order
	 * @throws NullPointerException If the text is null
	 * @throws MalformedDataException If a document is malformed or is not a certificate
	 */
	public static @NonNull @Unmodifiable List<X509Certificate> parseChain(@NonNull String pem) {
		Objects.requireNonNull(pem, "Pem must not be null");
		return Pems.decodeAll(pem).stream().map(Certificates::parse).toList();
	}
	
	/**
	 * Parses a DER encoded certificate, returning empty instead of throwing when the data is not one.<br>
	 * <p>
	 *     The certificate factory of the JDK reports a malformed certificate only by throwing, so that one exception is contained here.<br>
	 *     Every other failure still propagates.
	 * </p>
	 *
	 * @param der The encoded certificate
	 * @return The parsed certificate, or empty if the data is not an X.509 certificate
	 * @throws NullPointerException If the encoded certificate is null
	 */
	public static @NonNull Optional<X509Certificate> tryParse(byte @NonNull [] der) {
		Objects.requireNonNull(der, "Encoded certificate must not be null");
		
		Certificate certificate;
		try {
			certificate = factory().generateCertificate(new ByteArrayInputStream(der));
		} catch (CertificateException e) {
			return Optional.empty();
		}
		return certificate instanceof X509Certificate x509 ? Optional.of(x509) : Optional.empty();
	}
	
	/**
	 * Parses a DER encoded certificate revocation list.<br>
	 *
	 * @param der The encoded revocation list
	 * @return The parsed revocation list
	 * @throws NullPointerException If the encoded revocation list is null
	 * @throws MalformedDataException If the data is not an X.509 revocation list
	 */
	public static @NonNull X509CRL parseCrl(byte @NonNull [] der) {
		Objects.requireNonNull(der, "Encoded revocation list must not be null");
		
		try {
			if (factory().generateCRL(new ByteArrayInputStream(der)) instanceof X509CRL crl) {
				return crl;
			}
			throw new MalformedDataException("Not an X.509 revocation list");
		} catch (CRLException e) {
			throw new MalformedDataException("Malformed revocation list", e);
		}
	}
	
	/**
	 * Parses the certificate revocation list held by the given PEM document.<br>
	 *
	 * @param document The document holding the revocation list
	 * @return The parsed revocation list
	 * @throws NullPointerException If the document is null
	 * @throws MalformedDataException If the document does not carry the revocation list label, or holds a malformed revocation list
	 */
	public static @NonNull X509CRL parseCrl(@NonNull PemDocument document) {
		Objects.requireNonNull(document, "Document must not be null");
		
		if (!Pems.X509_CRL.equals(document.label())) {
			throw new MalformedDataException("Expected a '" + Pems.X509_CRL + "' PEM document, got '" + document.label() + "'");
		}
		return parseCrl(document.content());
	}
	
	/**
	 * Computes a fingerprint of the given certificate over its complete DER encoding.<br>
	 * This is the value browsers and {@code openssl x509 -fingerprint} show.<br>
	 *
	 * @param algorithm The hash to fingerprint with
	 * @param certificate The certificate to fingerprint
	 * @return The computed fingerprint
	 * @throws NullPointerException If the algorithm or the certificate is null
	 * @throws CryptoException If the certificate cannot be encoded
	 */
	public static byte @NonNull [] fingerprint(@NonNull HashAlgorithm algorithm, @NonNull X509Certificate certificate) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(certificate, "Certificate must not be null");
		
		try {
			return Hashes.hash(algorithm, certificate.getEncoded());
		} catch (CertificateEncodingException e) {
			throw new CryptoException("Cannot encode certificate", e);
		}
	}
	
	/**
	 * Returns the key id of the public key of the given certificate.<br>
	 * Two certificates for the same key share the id, so it finds the certificates of a key rather than one certificate.<br>
	 *
	 * @param certificate The certificate to identify
	 * @return The key id
	 * @throws NullPointerException If the certificate is null
	 * @throws CryptoException If the public key does not expose an encoded form
	 */
	public static @NonNull KeyId id(@NonNull X509Certificate certificate) {
		Objects.requireNonNull(certificate, "Certificate must not be null");
		return KeyId.of(certificate.getPublicKey());
	}
	
	/**
	 * Returns whether the given instant lies within the validity period of the given certificate.<br>
	 * Both bounds are inclusive, as RFC 5280 defines them.<br>
	 *
	 * @param certificate The certificate to check
	 * @param at The instant to check
	 * @return True if the certificate is valid at the instant
	 * @throws NullPointerException If the certificate or the instant is null
	 */
	public static boolean isValidAt(@NonNull X509Certificate certificate, @NonNull Instant at) {
		Objects.requireNonNull(certificate, "Certificate must not be null");
		Objects.requireNonNull(at, "Instant must not be null");
		
		return !at.isBefore(certificate.getNotBefore().toInstant()) && !at.isAfter(certificate.getNotAfter().toInstant());
	}
	
	/**
	 * Returns whether the given certificate is self-signed.<br>
	 * <p>
	 *     A certificate is self-signed when its subject and issuer are the same name and its own public key verifies its signature.<br>
	 *     The name alone is not enough, since anybody can issue a certificate whose issuer name equals its subject name.
	 * </p>
	 *
	 * @param certificate The certificate to check
	 * @return True if the certificate is self-signed
	 * @throws NullPointerException If the certificate is null
	 * @throws CryptoException If the signature scheme of the certificate is not served by a registered provider
	 */
	public static boolean isSelfSigned(@NonNull X509Certificate certificate) {
		Objects.requireNonNull(certificate, "Certificate must not be null");
		
		if (!certificate.getSubjectX500Principal().equals(certificate.getIssuerX500Principal())) {
			return false;
		}
		
		X509CertificateHolder holder = X509Support.holder(certificate);
		try {
			return holder.isSignatureValid(X509Support.verifierProvider(certificate.getPublicKey()));
		} catch (CertException e) {
			throw new CryptoException("Cannot verify the signature of certificate " + certificate.getSubjectX500Principal().getName(), e);
		}
	}
	
	/**
	 * Returns the subject alternative names of the given certificate, in the order they are written.<br>
	 * Names of a kind {@link SubjectAltName} does not model, such as directory names, are skipped.<br>
	 *
	 * @param certificate The certificate to read
	 * @return The subject alternative names, empty if the certificate has none
	 * @throws NullPointerException If the certificate is null
	 * @throws MalformedDataException If the certificate or one of its names is malformed
	 */
	public static @NonNull @Unmodifiable List<SubjectAltName> subjectAltNames(@NonNull X509Certificate certificate) {
		Objects.requireNonNull(certificate, "Certificate must not be null");
		return X509Support.subjectAltNames(X509Support.holder(certificate).getExtensions());
	}
	
	/**
	 * Returns the X.509 certificate factory of the JDK.<br>
	 *
	 * @return The certificate factory
	 * @throws CryptoException If the runtime provides no X.509 certificate factory
	 */
	private static @NonNull CertificateFactory factory() {
		try {
			return CertificateFactory.getInstance(X509);
		} catch (CertificateException e) {
			throw new CryptoException("No X.509 certificate factory is available", e);
		}
	}
}
