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
import net.luis.utils.crypto.algorithm.*;
import net.luis.utils.crypto.exception.*;
import net.luis.utils.crypto.util.CryptoBytes;
import net.luis.utils.crypto.util.CryptoRandom;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.address.ipv6.Ipv6Address;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.*;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.operator.*;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.security.auth.x500.X500Principal;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.*;
import java.util.*;

/**
 * The shared plumbing between this package and BouncyCastle.<br>
 * <p>
 *     Every conversion between a type of this package and its ASN.1 counterpart lives here, so the public classes never expose a BouncyCastle type.<br>
 *     Every checked exception BouncyCastle throws is translated into the exception hierarchy of this library here as well.
 * </p>
 *
 * @author Luis-St
 */
final class X509Support {
	
	/**
	 * The length of a key identifier in bytes.<br>
	 * This is the length of a SHA-1 digest, which is what every other implementation produces and what tools expect to see.<br>
	 */
	private static final int KEY_IDENTIFIER_LENGTH = 20;
	/**
	 * The length of a random serial number in bytes.<br>
	 */
	private static final int SERIAL_LENGTH = 16;
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private X509Support() {}
	
	/**
	 * Draws a random positive 128 bit serial number.<br>
	 * @return The serial number
	 */
	static @NonNull BigInteger randomSerial() {
		BigInteger serial;
		do {
			serial = new BigInteger(1, CryptoRandom.bytes(SERIAL_LENGTH));
		} while (serial.signum() == 0);
		return serial;
	}
	
	/**
	 * Converts the given encoded name into a principal.<br>
	 *
	 * @param name The name to convert
	 * @return The converted principal
	 * @throws NullPointerException If the name is null
	 * @throws CryptoException If the name cannot be encoded
	 */
	static @NonNull X500Principal principal(@NonNull X500Name name) {
		Objects.requireNonNull(name, "Name must not be null");
		
		try {
			return new X500Principal(name.getEncoded(ASN1Encoding.DER));
		} catch (IOException e) {
			throw new CryptoException("Cannot encode distinguished name", e);
		}
	}
	
	/**
	 * Converts the given principal into its encoded name, keeping its exact encoding.<br>
	 *
	 * @param principal The principal to convert
	 * @return The converted name
	 * @throws NullPointerException If the principal is null
	 */
	static @NonNull X500Name name(@NonNull X500Principal principal) {
		Objects.requireNonNull(principal, "Principal must not be null");
		return X500Name.getInstance(principal.getEncoded());
	}
	
	/**
	 * Reads the given certificate into its ASN.1 form.<br>
	 *
	 * @param certificate The certificate to read
	 * @return The certificate holder
	 * @throws NullPointerException If the certificate is null
	 * @throws MalformedDataException If the certificate cannot be encoded or read
	 */
	static @NonNull X509CertificateHolder holder(@NonNull X509Certificate certificate) {
		Objects.requireNonNull(certificate, "Certificate must not be null");
		
		try {
			return new X509CertificateHolder(certificate.getEncoded());
		} catch (CertificateEncodingException | IOException e) {
			throw new MalformedDataException("Cannot read certificate", e);
		}
	}
	
	/**
	 * Converts the given certificate holder into a JDK certificate.<br>
	 *
	 * @param holder The holder to convert
	 * @return The converted certificate
	 * @throws NullPointerException If the holder is null
	 * @throws CryptoException If the certificate cannot be converted
	 */
	static @NonNull X509Certificate certificate(@NonNull X509CertificateHolder holder) {
		Objects.requireNonNull(holder, "Holder must not be null");
		
		try {
			return new JcaX509CertificateConverter().getCertificate(holder);
		} catch (CertificateException e) {
			throw new CryptoException("Cannot convert certificate", e);
		}
	}
	
	/**
	 * Returns the encoded subject public key info of the given certificate, exactly as the certificate carries it.<br>
	 *
	 * @param certificate The certificate to read
	 * @return The encoded subject public key info
	 * @throws NullPointerException If the certificate is null
	 * @throws MalformedDataException If the certificate cannot be read
	 */
	static byte @NonNull [] spki(@NonNull X509Certificate certificate) {
		Objects.requireNonNull(certificate, "Certificate must not be null");
		
		try {
			return holder(certificate).getSubjectPublicKeyInfo().getEncoded(ASN1Encoding.DER);
		} catch (IOException e) {
			throw new MalformedDataException("Cannot encode subject public key info", e);
		}
	}
	
	/**
	 * Reads the given public key into its subject public key info.<br>
	 *
	 * @param key The key to read
	 * @return The subject public key info
	 * @throws NullPointerException If the key is null
	 * @throws CryptoException If the key has no X.509 encoding
	 */
	static @NonNull SubjectPublicKeyInfo publicKeyInfo(@NonNull PublicKey key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		if (!"X.509".equals(key.getFormat()) || key.getEncoded() == null) {
			throw new CryptoException("A " + key.getAlgorithm() + " key in the " + key.getFormat() + " format cannot be placed in a certificate");
		}
		return SubjectPublicKeyInfo.getInstance(key.getEncoded());
	}
	
	/**
	 * Computes the key identifier of the given public key.<br>
	 * <p>
	 *     This is the first method of RFC 7093, the leftmost 160 bits of the SHA-256 digest of the subject public key.<br>
	 *     It has the length of the SHA-1 identifier of RFC 5280 without reaching SHA-1.
	 * </p>
	 *
	 * @param key The key to identify
	 * @return The key identifier
	 * @throws NullPointerException If the key is null
	 */
	static byte @NonNull [] keyIdentifier(@NonNull SubjectPublicKeyInfo key) {
		Objects.requireNonNull(key, "Key must not be null");
		return CryptoBytes.slice(Hashes.hash(HashAlgorithm.SHA_256, key.getPublicKeyData().getBytes()), 0, KEY_IDENTIFIER_LENGTH);
	}
	
	/**
	 * Returns the key identifier a certificate issued by the given issuer has to name as its authority key identifier.<br>
	 * The issuer's own subject key identifier is used where it has one, so the two always match.<br>
	 *
	 * @param issuer The issuing certificate
	 * @return The authority key identifier
	 * @throws NullPointerException If the issuer is null
	 * @throws MalformedDataException If the issuer cannot be read
	 */
	static byte @NonNull [] authorityKeyIdentifier(@NonNull X509Certificate issuer) {
		Objects.requireNonNull(issuer, "Issuer must not be null");
		
		X509CertificateHolder holder = holder(issuer);
		SubjectKeyIdentifier identifier = SubjectKeyIdentifier.fromExtensions(holder.getExtensions());
		return identifier == null ? keyIdentifier(holder.getSubjectPublicKeyInfo()) : identifier.getKeyIdentifier();
	}
	
	/**
	 * Requires that the given scheme can sign a certificate.<br>
	 * <p>
	 *     A hybrid of this library has no encoding outside this library, so no certificate can carry it and it is rejected.<br>
	 *     The composite schemes carry a registered identifier and are accepted.
	 * </p>
	 *
	 * @param algorithm The scheme to check
	 * @return The scheme as a native scheme
	 * @throws NullPointerException If the algorithm is null
	 * @throws UnsupportedAlgorithmException If the scheme is a hybrid
	 */
	static @NonNull NativeSignatureAlgorithm requireCertificateScheme(@NonNull SignatureAlgorithm algorithm) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		
		return switch (algorithm) {
			case NativeSignatureAlgorithm n -> n;
			case HybridSignatureAlgorithm hybrid -> throw new UnsupportedAlgorithmException(
				"Hybrid signature scheme " + hybrid.name() + " has no certificate encoding, use a composite scheme instead"
			);
		};
	}
	
	/**
	 * Creates a content signer for the given scheme and key.<br>
	 *
	 * @param algorithm The scheme to sign with
	 * @param key The private key to sign with
	 * @return The created content signer
	 * @throws NullPointerException If the algorithm or the key is null
	 * @throws UnsupportedAlgorithmException If the scheme is a hybrid, or if no identifier or provider is known for it
	 * @throws CryptoException If the key is not usable for the scheme
	 */
	static @NonNull ContentSigner contentSigner(@NonNull SignatureAlgorithm algorithm, @NonNull PrivateKey key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		NativeSignatureAlgorithm n = requireCertificateScheme(algorithm);
		JcaContentSignerBuilder builder;
		try {
			builder = new JcaContentSignerBuilder(n.jcaName());
		} catch (IllegalArgumentException e) {
			throw new UnsupportedAlgorithmException("Signature scheme " + n.name() + " has no known certificate signature identifier", e);
		}
		
		try {
			return builder.setSecureRandom(CryptoRandom.instance()).build(key);
		} catch (OperatorCreationException e) {
			if (e.getCause() instanceof NoSuchAlgorithmException) {
				throw UnsupportedAlgorithmException.forAlgorithm(n.jcaName(), e);
			}
			throw new CryptoException("Cannot sign with " + n.name() + " using the given key", e);
		}
	}
	
	/**
	 * Creates a verifier provider for the given public key.<br>
	 *
	 * @param key The key to verify with
	 * @return The created verifier provider
	 * @throws NullPointerException If the key is null
	 * @throws CryptoException If the key is not usable
	 */
	static @NonNull ContentVerifierProvider verifierProvider(@NonNull PublicKey key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			return new JcaContentVerifierProviderBuilder().build(key);
		} catch (OperatorCreationException e) {
			throw new CryptoException("Cannot verify with a " + key.getAlgorithm() + " key", e);
		}
	}
	
	/**
	 * Issues a certificate.<br>
	 * <p>
	 *     The subject and authority key identifiers are always added.<br>
	 *     Basic constraints are always added as well and marked critical, so an end entity certificate can never be mistaken for an authority.
	 * </p>
	 *
	 * @param subject The subject name
	 * @param subjectKey The subject public key
	 * @param issuer The issuer name, equal to the subject for a self-signed certificate
	 * @param authorityKeyId The key identifier of the issuer key
	 * @param signingKey The private key of the issuer
	 * @param algorithm The scheme to sign with
	 * @param spec What the certificate says apart from the subject's name and key
	 * @return The issued certificate
	 * @throws NullPointerException If any parameter is null
	 * @throws UnsupportedAlgorithmException If the scheme cannot sign a certificate
	 * @throws CryptoException If issuing fails
	 */
	static @NonNull X509Certificate issue(
		@NonNull X500Name subject, @NonNull SubjectPublicKeyInfo subjectKey, @NonNull X500Name issuer, byte @NonNull [] authorityKeyId,
		@NonNull PrivateKey signingKey, @NonNull SignatureAlgorithm algorithm, @NonNull CertificateSpec spec
	) {
		Objects.requireNonNull(subject, "Subject must not be null");
		Objects.requireNonNull(subjectKey, "Subject key must not be null");
		Objects.requireNonNull(issuer, "Issuer must not be null");
		Objects.requireNonNull(authorityKeyId, "Authority key id must not be null");
		Objects.requireNonNull(signingKey, "Signing key must not be null");
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(spec, "Spec must not be null");
		
		BigInteger serial = spec.serial() == null ? randomSerial() : spec.serial();
		X509v3CertificateBuilder builder = new X509v3CertificateBuilder(issuer, serial, Date.from(spec.notBefore()), Date.from(spec.notAfter()), subject, subjectKey);
		try {
			builder.addExtension(Extension.subjectKeyIdentifier, false, new SubjectKeyIdentifier(keyIdentifier(subjectKey)));
			builder.addExtension(Extension.authorityKeyIdentifier, false, new AuthorityKeyIdentifier(authorityKeyId));
			builder.addExtension(Extension.basicConstraints, true, spec.isCa() ? new BasicConstraints(spec.pathLength()) : new BasicConstraints(false));
			if (!spec.keyUsage().isEmpty()) {
				builder.addExtension(Extension.keyUsage, true, keyUsage(spec.keyUsage()));
			}
			if (!spec.extendedKeyUsage().isEmpty()) {
				builder.addExtension(Extension.extendedKeyUsage, false, extendedKeyUsage(spec.extendedKeyUsage()));
			}
			if (!spec.subjectAltNames().isEmpty()) {
				// RFC 5280 requires the extension to be critical when it is the only identity, because the subject is empty
				builder.addExtension(Extension.subjectAlternativeName, subject.getRDNs().length == 0, generalNames(spec.subjectAltNames()));
			}
		} catch (CertIOException e) {
			throw new CryptoException("Cannot encode certificate extensions", e);
		}
		return certificate(builder.build(contentSigner(algorithm, signingKey)));
	}
	
	/**
	 * Converts the given key usages into their extension value.<br>
	 *
	 * @param usages The usages to convert
	 * @return The extension value
	 * @throws NullPointerException If the usages are null
	 */
	static org.bouncycastle.asn1.x509.@NonNull KeyUsage keyUsage(@NonNull Set<KeyUsage> usages) {
		Objects.requireNonNull(usages, "Usages must not be null");
		
		int mask = 0;
		for (KeyUsage usage : usages) {
			mask |= 1 << (usage.bit() < 8 ? 7 - usage.bit() : 15 - (usage.bit() - 8));
		}
		return new org.bouncycastle.asn1.x509.KeyUsage(mask);
	}
	
	/**
	 * Converts the given extended key usages into their extension value.<br>
	 *
	 * @param usages The usages to convert
	 * @return The extension value
	 * @throws NullPointerException If the usages are null
	 */
	static org.bouncycastle.asn1.x509.@NonNull ExtendedKeyUsage extendedKeyUsage(@NonNull Set<ExtendedKeyUsage> usages) {
		Objects.requireNonNull(usages, "Usages must not be null");
		
		KeyPurposeId[] purposes = usages.stream().sorted().map(usage -> KeyPurposeId.getInstance(new ASN1ObjectIdentifier(usage.oid()))).toArray(KeyPurposeId[]::new);
		return new org.bouncycastle.asn1.x509.ExtendedKeyUsage(purposes);
	}
	
	/**
	 * Converts the given subject alternative names into their extension value.<br>
	 *
	 * @param names The names to convert
	 * @return The extension value
	 * @throws NullPointerException If the names are null
	 */
	static @NonNull GeneralNames generalNames(@NonNull List<SubjectAltName> names) {
		Objects.requireNonNull(names, "Names must not be null");
		return new GeneralNames(names.stream().map(X509Support::generalName).toArray(GeneralName[]::new));
	}
	
	/**
	 * Converts the given subject alternative name into its general name.<br>
	 *
	 * @param name The name to convert
	 * @return The general name
	 * @throws NullPointerException If the name is null
	 */
	static @NonNull GeneralName generalName(@NonNull SubjectAltName name) {
		Objects.requireNonNull(name, "Name must not be null");
		
		return switch (name) {
			case SubjectAltName.Dns dns -> new GeneralName(GeneralName.dNSName, dns.name());
			case SubjectAltName.Ip ip -> new GeneralName(GeneralName.iPAddress, new DEROctetString(ip.address().toBytes()));
			case SubjectAltName.Uri uri -> new GeneralName(GeneralName.uniformResourceIdentifier, uri.uri().toASCIIString());
			case SubjectAltName.Email email -> new GeneralName(GeneralName.rfc822Name, email.address());
		};
	}
	
	/**
	 * Reads the subject alternative names from the given extensions.<br>
	 * Names of a kind {@link SubjectAltName} does not model are skipped.<br>
	 *
	 * @param extensions The extensions to read, null if there are none
	 * @return The subject alternative names, in the order they are written
	 * @throws MalformedDataException If a name is malformed
	 */
	static @NonNull @Unmodifiable List<SubjectAltName> subjectAltNames(@Nullable Extensions extensions) {
		GeneralNames names = extensions == null ? null : GeneralNames.fromExtensions(extensions, Extension.subjectAlternativeName);
		if (names == null) {
			return List.of();
		}
		return Arrays.stream(names.getNames()).map(X509Support::subjectAltName).flatMap(Optional::stream).toList();
	}
	
	/**
	 * Converts the given general name into a subject alternative name.<br>
	 *
	 * @param name The name to convert
	 * @return The subject alternative name, or empty if the name is of a kind that is not modeled
	 * @throws NullPointerException If the name is null
	 * @throws MalformedDataException If the name is malformed
	 */
	private static @NonNull Optional<SubjectAltName> subjectAltName(@NonNull GeneralName name) {
		Objects.requireNonNull(name, "Name must not be null");
		
		try {
			return switch (name.getTagNo()) {
				case GeneralName.dNSName -> Optional.of(new SubjectAltName.Dns(ASN1IA5String.getInstance(name.getName()).getString()));
				case GeneralName.rfc822Name -> Optional.of(new SubjectAltName.Email(ASN1IA5String.getInstance(name.getName()).getString()));
				case GeneralName.uniformResourceIdentifier -> Optional.of(new SubjectAltName.Uri(new URI(ASN1IA5String.getInstance(name.getName()).getString())));
				case GeneralName.iPAddress -> {
					byte[] octets = ASN1OctetString.getInstance(name.getName()).getOctets();
					yield switch (octets.length) {
						case 4 -> Optional.of(new SubjectAltName.Ip(Ipv4Address.fromBytes(octets)));
						case 16 -> Optional.of(new SubjectAltName.Ip(Ipv6Address.fromBytes(octets)));
						default -> throw new MalformedDataException("An address name must be 4 or 16 bytes long, was " + octets.length);
					};
				}
				default -> Optional.empty();
			};
		} catch (URISyntaxException | IllegalArgumentException e) {
			throw new MalformedDataException("Malformed subject alternative name " + name, e);
		}
	}
}
