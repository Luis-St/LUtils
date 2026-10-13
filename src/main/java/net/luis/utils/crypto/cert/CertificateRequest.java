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
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.security.auth.x500.X500Principal;
import java.io.IOException;
import java.security.*;
import java.util.List;
import java.util.Objects;

/**
 * A parsed PKCS#10 certificate signing request.<br>
 * <p>
 *     Parsing a request does not check its signature.<br>
 *     Call {@link #verify()} first, or hand it to {@link CertificateRequests#sign}, which refuses a request whose signature does not verify.
 * </p>
 * <p>
 *     The subject stays a {@link X500Principal}, because a request from a foreign tool may carry attributes a {@link DistinguishedName} does not model.<br>
 *     A signed certificate copies it byte for byte.
 * </p>
 *
 * @see CertificateRequests
 *
 * @author Luis-St
 */
public final class CertificateRequest {
	
	/**
	 * The parsed request.<br>
	 */
	private final JcaPKCS10CertificationRequest request;
	/**
	 * The public key the request asks a certificate for.<br>
	 */
	private final PublicKey publicKey;
	/**
	 * The subject alternative names the request asks for.<br>
	 */
	private final List<SubjectAltName> subjectAltNames;
	
	/**
	 * Constructs a new certificate request from the given parsed request.<br>
	 *
	 * @param request The parsed request
	 * @throws NullPointerException If the request is null
	 * @throws MalformedDataException If the public key or the requested extensions cannot be read
	 */
	CertificateRequest(@NonNull PKCS10CertificationRequest request) {
		Objects.requireNonNull(request, "Request must not be null");
		
		this.request = new JcaPKCS10CertificationRequest(request);
		try {
			this.publicKey = this.request.getPublicKey();
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new MalformedDataException("Cannot read the public key of the certificate request", e);
		}
		this.subjectAltNames = X509Support.subjectAltNames(requestedExtensions(request));
	}
	
	/**
	 * Returns the extensions the given request asks for.<br>
	 *
	 * @param request The request to read
	 * @return The requested extensions, or null if the request asks for none
	 * @throws NullPointerException If the request is null
	 * @throws MalformedDataException If the request asks for extensions more than once
	 */
	private static @Nullable Extensions requestedExtensions(@NonNull PKCS10CertificationRequest request) {
		Objects.requireNonNull(request, "Request must not be null");
		
		Attribute[] attributes = request.getAttributes(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest);
		if (attributes.length == 0) {
			return null;
		}
		if (attributes.length > 1 || attributes[0].getAttrValues().size() != 1) {
			throw new MalformedDataException("A certificate request must ask for extensions at most once");
		}
		return Extensions.getInstance(attributes[0].getAttrValues().getObjectAt(0));
	}
	
	/**
	 * Returns the subject name the request asks a certificate for.<br>
	 * @return The subject name
	 */
	public @NonNull X500Principal subject() {
		return X509Support.principal(this.request.getSubject());
	}
	
	/**
	 * Returns the public key the request asks a certificate for.<br>
	 * @return The public key
	 */
	public @NonNull PublicKey publicKey() {
		return this.publicKey;
	}
	
	/**
	 * Returns the subject alternative names the request asks for.<br>
	 * Names of a kind {@link SubjectAltName} does not model are skipped.<br>
	 *
	 * @return The requested names, empty if the request asks for none
	 */
	public @NonNull @Unmodifiable List<SubjectAltName> subjectAltNames() {
		return this.subjectAltNames;
	}
	
	/**
	 * Checks the signature of the request against its own public key.<br>
	 * A valid signature proves that whoever made the request holds the private key.<br>
	 *
	 * @return True if the signature is valid
	 * @throws CryptoException If the signature scheme of the request is not served by a registered provider
	 */
	public boolean verify() {
		try {
			return this.request.isSignatureValid(X509Support.verifierProvider(this.publicKey));
		} catch (PKCSException e) {
			throw new CryptoException("Cannot verify the signature of the certificate request", e);
		}
	}
	
	/**
	 * Returns the DER encoding of the request.<br>
	 *
	 * @return The encoded request
	 * @throws CryptoException If the request cannot be encoded
	 */
	public byte @NonNull [] encoded() {
		try {
			return this.request.getEncoded();
		} catch (IOException e) {
			throw new CryptoException("Cannot encode the certificate request", e);
		}
	}
	
	/**
	 * Returns the parsed request, for this package to sign.<br>
	 * @return The parsed request
	 */
	@NonNull PKCS10CertificationRequest request() {
		return this.request;
	}
	
	@Override
	public String toString() {
		return "CertificateRequest[subject=" + this.subject().getName() + ", key=" + this.publicKey.getAlgorithm() + "]";
	}
}
