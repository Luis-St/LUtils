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

import net.luis.utils.crypto.*;
import net.luis.utils.crypto.algorithm.*;
import net.luis.utils.crypto.exception.*;
import net.luis.utils.io.network.address.IpAddresses;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.*;
import org.bouncycastle.operator.ContentSigner;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.security.auth.x500.X500Principal;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link X509Support}.<br>
 *
 * @author Luis-St
 */
class X509SupportTest {
	
	private static final NativeSignatureAlgorithm FAKE = new NativeSignatureAlgorithm("fake", "NoSuchSig", "NoSuchKeyType", null, 1, false, false);
	private static final X500Name SUBJECT = new X500Name("CN=LUtils Test,O=LUtils");
	private static final X500Name AUTHORITY = new X500Name("CN=LUtils Test CA");
	private static final Instant NOW = Instant.now().truncatedTo(ChronoUnit.SECONDS);
	private static final CertificateSpec SPEC = CertificateSpec.of(NOW, Duration.ofDays(1));
	private static final String SAN_OID = "2.5.29.17";
	
	private static KeyPair ed25519;
	private static KeyPair authority;
	private static KeyPair ecdsa;
	private static SubjectPublicKeyInfo ed25519Info;
	private static X509Certificate certificate;
	
	@BeforeAll
	static void setUp() {
		Providers.installBouncyCastle();
		ed25519 = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
		authority = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
		ecdsa = Signatures.generateKeyPair(SignatureAlgorithm.ECDSA_P256_SHA_256);
		ed25519Info = SubjectPublicKeyInfo.getInstance(ed25519.getPublic().getEncoded());
		certificate = selfSigned(SUBJECT, SPEC);
	}
	
	private static X509Certificate selfSigned(X500Name subject, CertificateSpec spec) {
		return X509Support.issue(subject, ed25519Info, subject, X509Support.keyIdentifier(ed25519Info), ed25519.getPrivate(), SignatureAlgorithm.ED25519, spec);
	}
	
	private static X509Certificate issueBy(X500Name subject, CertificateSpec spec) {
		return X509Support.issue(subject, ed25519Info, AUTHORITY, new byte[] { 9, 8, 7 }, authority.getPrivate(), SignatureAlgorithm.ED25519, spec);
	}
	
	private static X509Certificate buildWithExtension(@Nullable SubjectKeyIdentifier identifier) throws Exception {
		X509v3CertificateBuilder builder = new X509v3CertificateBuilder(SUBJECT, BigInteger.ONE, Date.from(NOW), Date.from(NOW.plusSeconds(3600)), SUBJECT, ed25519Info);
		builder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));
		if (identifier != null) {
			builder.addExtension(Extension.subjectKeyIdentifier, false, identifier);
		}
		return X509Support.certificate(builder.build(X509Support.contentSigner(SignatureAlgorithm.ED25519, ed25519.getPrivate())));
	}
	
	private static Extensions sanExtensions(GeneralName... names) throws Exception {
		ExtensionsGenerator generator = new ExtensionsGenerator();
		generator.addExtension(Extension.subjectAlternativeName, false, new GeneralNames(names));
		return generator.generate();
	}
	
	@Test
	void constructorIsPrivate() {
		Constructor<?>[] constructors = X509Support.class.getDeclaredConstructors();
		assertEquals(1, constructors.length);
		assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
	}
	
	@Test
	void principalWithNullName() {
		assertEquals("Name must not be null", assertThrows(NullPointerException.class, () -> X509Support.principal(null)).getMessage());
	}
	
	@Test
	void nameWithNullPrincipal() {
		assertEquals("Principal must not be null", assertThrows(NullPointerException.class, () -> X509Support.name(null)).getMessage());
	}
	
	@Test
	void holderWithNullCertificate() {
		assertEquals("Certificate must not be null", assertThrows(NullPointerException.class, () -> X509Support.holder(null)).getMessage());
	}
	
	@Test
	void certificateWithNullHolder() {
		assertEquals("Holder must not be null", assertThrows(NullPointerException.class, () -> X509Support.certificate(null)).getMessage());
	}
	
	@Test
	void spkiWithNullCertificate() {
		assertEquals("Certificate must not be null", assertThrows(NullPointerException.class, () -> X509Support.spki(null)).getMessage());
	}
	
	@Test
	void publicKeyInfoWithNullKey() {
		assertEquals("Key must not be null", assertThrows(NullPointerException.class, () -> X509Support.publicKeyInfo(null)).getMessage());
	}
	
	@Test
	void keyIdentifierWithNullKey() {
		assertEquals("Key must not be null", assertThrows(NullPointerException.class, () -> X509Support.keyIdentifier(null)).getMessage());
	}
	
	@Test
	void authorityKeyIdentifierWithNullIssuer() {
		assertEquals("Issuer must not be null", assertThrows(NullPointerException.class, () -> X509Support.authorityKeyIdentifier(null)).getMessage());
	}
	
	@Test
	void requireCertificateSchemeWithNullAlgorithm() {
		assertEquals("Algorithm must not be null", assertThrows(NullPointerException.class, () -> X509Support.requireCertificateScheme(null)).getMessage());
	}
	
	@Test
	void contentSignerWithNullKey() {
		assertEquals("Key must not be null", assertThrows(NullPointerException.class, () -> X509Support.contentSigner(SignatureAlgorithm.ED25519, null)).getMessage());
	}
	
	@Test
	void contentSignerWithNullAlgorithm() {
		assertEquals("Algorithm must not be null", assertThrows(NullPointerException.class, () -> X509Support.contentSigner(null, ed25519.getPrivate())).getMessage());
	}
	
	@Test
	void verifierProviderWithNullKey() {
		assertEquals("Key must not be null", assertThrows(NullPointerException.class, () -> X509Support.verifierProvider(null)).getMessage());
	}
	
	@Test
	void keyUsageWithNullUsages() {
		assertEquals("Usages must not be null", assertThrows(NullPointerException.class, () -> X509Support.keyUsage(null)).getMessage());
	}
	
	@Test
	void extendedKeyUsageWithNullUsages() {
		assertEquals("Usages must not be null", assertThrows(NullPointerException.class, () -> X509Support.extendedKeyUsage(null)).getMessage());
	}
	
	@Test
	void generalNamesWithNullNames() {
		assertEquals("Names must not be null", assertThrows(NullPointerException.class, () -> X509Support.generalNames(null)).getMessage());
	}
	
	@Test
	void generalNameWithNullName() {
		assertEquals("Name must not be null", assertThrows(NullPointerException.class, () -> X509Support.generalName(null)).getMessage());
	}
	
	@Test
	void issueWithNullSubject() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> X509Support.issue(null, ed25519Info, SUBJECT, new byte[20], ed25519.getPrivate(), SignatureAlgorithm.ED25519, SPEC));
		assertEquals("Subject must not be null", exception.getMessage());
	}
	
	@Test
	void issueWithNullSubjectKey() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> X509Support.issue(SUBJECT, null, SUBJECT, new byte[20], ed25519.getPrivate(), SignatureAlgorithm.ED25519, SPEC));
		assertEquals("Subject key must not be null", exception.getMessage());
	}
	
	@Test
	void issueWithNullIssuer() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> X509Support.issue(SUBJECT, ed25519Info, null, new byte[20], ed25519.getPrivate(), SignatureAlgorithm.ED25519, SPEC));
		assertEquals("Issuer must not be null", exception.getMessage());
	}
	
	@Test
	void issueWithNullAuthorityKeyId() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> X509Support.issue(SUBJECT, ed25519Info, SUBJECT, null, ed25519.getPrivate(), SignatureAlgorithm.ED25519, SPEC));
		assertEquals("Authority key id must not be null", exception.getMessage());
	}
	
	@Test
	void issueWithNullSigningKey() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> X509Support.issue(SUBJECT, ed25519Info, SUBJECT, new byte[20], null, SignatureAlgorithm.ED25519, SPEC));
		assertEquals("Signing key must not be null", exception.getMessage());
	}
	
	@Test
	void issueWithNullAlgorithm() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> X509Support.issue(SUBJECT, ed25519Info, SUBJECT, new byte[20], ed25519.getPrivate(), null, SPEC));
		assertEquals("Algorithm must not be null", exception.getMessage());
	}
	
	@Test
	void issueWithNullSpec() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> X509Support.issue(SUBJECT, ed25519Info, SUBJECT, new byte[20], ed25519.getPrivate(), SignatureAlgorithm.ED25519, null));
		assertEquals("Spec must not be null", exception.getMessage());
	}
	
	@Test
	void publicKeyInfoWithNonX509Format() {
		CryptoException exception = assertThrows(CryptoException.class, () -> X509Support.publicKeyInfo(new TestPublicKey("RAW", new byte[] { 1 })));
		assertEquals(CryptoException.class, exception.getClass());
		assertTrue(exception.getMessage().contains("Test"));
		assertTrue(exception.getMessage().contains("RAW"));
	}
	
	@Test
	void publicKeyInfoWithNullEncoding() {
		CryptoException exception = assertThrows(CryptoException.class, () -> X509Support.publicKeyInfo(new TestPublicKey("X.509", null)));
		assertEquals(CryptoException.class, exception.getClass());
		assertTrue(exception.getMessage().contains("X.509"));
	}
	
	@Test
	void requireCertificateSchemeWithHybrid() {
		UnsupportedAlgorithmException exception = assertThrows(UnsupportedAlgorithmException.class, () -> X509Support.requireCertificateScheme(SignatureAlgorithm.ED25519_ML_DSA_65));
		assertTrue(exception.getMessage().contains(SignatureAlgorithm.ED25519_ML_DSA_65.name()));
	}
	
	@Test
	void contentSignerWithHybrid() {
		assertThrows(UnsupportedAlgorithmException.class, () -> X509Support.contentSigner(SignatureAlgorithm.ED25519_ML_DSA_65, ed25519.getPrivate()));
	}
	
	@Test
	void contentSignerWithUnknownSignatureName() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> X509Support.contentSigner(FAKE, ed25519.getPrivate()));
		assertTrue(exception.getMessage().contains("NoSuchSig"));
		assertEquals(IllegalArgumentException.class, exception.getClass());
	}
	
	@Test
	void contentSignerWithMismatchedKey() {
		CryptoException exception = assertThrows(CryptoException.class, () -> X509Support.contentSigner(SignatureAlgorithm.ED25519, ecdsa.getPrivate()));
		assertEquals(CryptoException.class, exception.getClass());
		assertTrue(exception.getMessage().contains("Ed25519"));
	}
	
	@Test
	void issueWithHybridAlgorithm() {
		assertThrows(UnsupportedAlgorithmException.class, () -> X509Support.issue(SUBJECT, ed25519Info, SUBJECT, new byte[20], ed25519.getPrivate(), SignatureAlgorithm.ED25519_ML_DSA_65, SPEC));
	}
	
	@Test
	void subjectAltNamesWithWrongAddressLength() throws Exception {
		Extensions extensions = sanExtensions(new GeneralName(GeneralName.iPAddress, new DEROctetString(new byte[5])));
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> X509Support.subjectAltNames(extensions));
		assertTrue(exception.getMessage().contains("5"));
	}
	
	@Test
	void subjectAltNamesWithMalformedUri() throws Exception {
		Extensions extensions = sanExtensions(new GeneralName(GeneralName.uniformResourceIdentifier, "http://exa mple.com"));
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> X509Support.subjectAltNames(extensions));
		assertInstanceOf(URISyntaxException.class, exception.getCause());
	}
	
	@Test
	void subjectAltNamesWithRelativeUri() throws Exception {
		Extensions extensions = sanExtensions(new GeneralName(GeneralName.uniformResourceIdentifier, "relative/path"));
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> X509Support.subjectAltNames(extensions));
		assertInstanceOf(IllegalArgumentException.class, exception.getCause());
	}
	
	@Test
	void subjectAltNamesWithBlankDns() throws Exception {
		Extensions extensions = sanExtensions(new GeneralName(GeneralName.dNSName, " "));
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> X509Support.subjectAltNames(extensions));
		assertInstanceOf(IllegalArgumentException.class, exception.getCause());
	}
	
	@Test
	void subjectAltNamesWithEmailWithoutAtSign() throws Exception {
		Extensions extensions = sanExtensions(new GeneralName(GeneralName.rfc822Name, "nobody"));
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> X509Support.subjectAltNames(extensions));
		assertInstanceOf(IllegalArgumentException.class, exception.getCause());
	}
	
	@Test
	void publicKeyInfoWithX509Key() throws Exception {
		SubjectPublicKeyInfo info = X509Support.publicKeyInfo(ed25519.getPublic());
		assertArrayEquals(ed25519.getPublic().getEncoded(), info.getEncoded(ASN1Encoding.DER));
		assertEquals(ed25519Info, info);
	}
	
	@Test
	void authorityKeyIdentifierUsesSubjectKeyIdentifier() throws Exception {
		X509Certificate issuer = buildWithExtension(new SubjectKeyIdentifier(new byte[] { 1, 2, 3 }));
		byte[] identifier = X509Support.authorityKeyIdentifier(issuer);
		assertArrayEquals(new byte[] { 1, 2, 3 }, identifier);
		assertFalse(Arrays.equals(X509Support.keyIdentifier(ed25519Info), identifier));
	}
	
	@Test
	void authorityKeyIdentifierWithoutExtensions() throws Exception {
		X509v1CertificateBuilder builder = new X509v1CertificateBuilder(SUBJECT, BigInteger.TWO, Date.from(NOW), Date.from(NOW.plusSeconds(3600)), SUBJECT, ed25519Info);
		X509Certificate issuer = X509Support.certificate(builder.build(X509Support.contentSigner(SignatureAlgorithm.ED25519, ed25519.getPrivate())));
		assertEquals(1, issuer.getVersion());
		assertArrayEquals(X509Support.keyIdentifier(ed25519Info), X509Support.authorityKeyIdentifier(issuer));
	}
	
	@Test
	void authorityKeyIdentifierWithoutSubjectKeyIdentifier() throws Exception {
		X509Certificate issuer = buildWithExtension(null);
		assertNotNull(X509Support.holder(issuer).getExtensions());
		assertArrayEquals(X509Support.keyIdentifier(ed25519Info), X509Support.authorityKeyIdentifier(issuer));
	}
	
	@Test
	void requireCertificateSchemeWithNative() {
		assertSame(SignatureAlgorithm.ED25519, X509Support.requireCertificateScheme(SignatureAlgorithm.ED25519));
		assertSame(SignatureAlgorithm.COMPOSITE_ML_DSA_65_ED25519, X509Support.requireCertificateScheme(SignatureAlgorithm.COMPOSITE_ML_DSA_65_ED25519));
	}
	
	@Test
	void issueWithRandomSerial() {
		X509Certificate first = selfSigned(SUBJECT, SPEC);
		X509Certificate second = selfSigned(SUBJECT, SPEC);
		assertEquals(1, first.getSerialNumber().signum());
		assertTrue(first.getSerialNumber().bitLength() <= 128);
		assertNotEquals(first.getSerialNumber(), second.getSerialNumber());
	}
	
	@Test
	void issueWithGivenSerial() {
		X509Certificate issued = selfSigned(SUBJECT, SPEC.withSerial(BigInteger.valueOf(4711)));
		assertEquals(BigInteger.valueOf(4711), issued.getSerialNumber());
	}
	
	@Test
	void issueEndEntity() {
		X509Certificate issued = selfSigned(SUBJECT, SPEC);
		assertEquals(-1, issued.getBasicConstraints());
		assertTrue(issued.getCriticalExtensionOIDs().contains(Extension.basicConstraints.getId()));
	}
	
	@Test
	void issueAuthority() {
		X509Certificate issued = selfSigned(SUBJECT, SPEC.withCa(2));
		assertEquals(2, issued.getBasicConstraints());
		assertTrue(issued.getCriticalExtensionOIDs().contains(Extension.basicConstraints.getId()));
	}
	
	@Test
	void issueWithoutKeyUsage() {
		X509Certificate issued = selfSigned(SUBJECT, SPEC);
		assertNull(issued.getKeyUsage());
		assertFalse(issued.getCriticalExtensionOIDs().contains(Extension.keyUsage.getId()));
	}
	
	@Test
	void issueWithKeyUsage() {
		X509Certificate issued = selfSigned(SUBJECT, SPEC.withKeyUsage(EnumSet.of(KeyUsage.DIGITAL_SIGNATURE, KeyUsage.KEY_CERT_SIGN)));
		boolean[] usage = issued.getKeyUsage();
		assertTrue(usage[0]);
		assertFalse(usage[1]);
		assertTrue(usage[5]);
		assertTrue(issued.getCriticalExtensionOIDs().contains(Extension.keyUsage.getId()));
	}
	
	@Test
	void issueWithoutExtendedKeyUsage() throws Exception {
		X509Certificate issued = selfSigned(SUBJECT, SPEC);
		assertNull(issued.getExtendedKeyUsage());
	}
	
	@Test
	void issueWithExtendedKeyUsage() throws Exception {
		X509Certificate issued = selfSigned(SUBJECT, SPEC.withExtendedKeyUsage(EnumSet.of(ExtendedKeyUsage.SERVER_AUTH, ExtendedKeyUsage.CLIENT_AUTH)));
		assertEquals(List.of("1.3.6.1.5.5.7.3.1", "1.3.6.1.5.5.7.3.2"), issued.getExtendedKeyUsage());
		assertTrue(issued.getNonCriticalExtensionOIDs().contains(Extension.extendedKeyUsage.getId()));
	}
	
	@Test
	void issueWithoutSubjectAltNames() {
		X509Certificate issued = selfSigned(SUBJECT, SPEC);
		assertNull(issued.getExtensionValue(SAN_OID));
		assertTrue(X509Support.subjectAltNames(X509Support.holder(issued).getExtensions()).isEmpty());
	}
	
	@Test
	void issueWithSubjectAltNamesAndSubject() {
		X509Certificate issued = selfSigned(SUBJECT, SPEC.withSubjectAltNames(new SubjectAltName.Dns("example.com")));
		assertTrue(issued.getNonCriticalExtensionOIDs().contains(SAN_OID));
		assertFalse(issued.getCriticalExtensionOIDs().contains(SAN_OID));
	}
	
	@Test
	void issueWithSubjectAltNamesAndEmptySubject() {
		X509Certificate issued = issueBy(new X500Name(new RDN[0]), SPEC.withSubjectAltNames(new SubjectAltName.Dns("example.com")));
		assertTrue(issued.getCriticalExtensionOIDs().contains(SAN_OID));
		assertFalse(issued.getNonCriticalExtensionOIDs().contains(SAN_OID));
		assertEquals("", issued.getSubjectX500Principal().getName());
	}
	
	@Test
	void keyUsageWithEmptySet() {
		assertEquals(new org.bouncycastle.asn1.x509.KeyUsage(0), X509Support.keyUsage(Set.of()));
	}
	
	@Test
	void keyUsageWithLowBit() {
		org.bouncycastle.asn1.x509.KeyUsage usage = X509Support.keyUsage(EnumSet.of(KeyUsage.DIGITAL_SIGNATURE));
		assertEquals(new org.bouncycastle.asn1.x509.KeyUsage(org.bouncycastle.asn1.x509.KeyUsage.digitalSignature), usage);
		assertTrue(usage.hasUsages(org.bouncycastle.asn1.x509.KeyUsage.digitalSignature));
		assertFalse(usage.hasUsages(org.bouncycastle.asn1.x509.KeyUsage.decipherOnly));
	}
	
	@Test
	void keyUsageWithHighBit() {
		org.bouncycastle.asn1.x509.KeyUsage usage = X509Support.keyUsage(EnumSet.of(KeyUsage.DECIPHER_ONLY));
		assertEquals(new org.bouncycastle.asn1.x509.KeyUsage(org.bouncycastle.asn1.x509.KeyUsage.decipherOnly), usage);
		assertTrue(usage.hasUsages(org.bouncycastle.asn1.x509.KeyUsage.decipherOnly));
		assertFalse(usage.hasUsages(org.bouncycastle.asn1.x509.KeyUsage.digitalSignature));
	}
	
	@Test
	void extendedKeyUsageWithEmptySet() {
		assertEquals(0, X509Support.extendedKeyUsage(Set.of()).getUsages().length);
	}
	
	@Test
	void generalNameWithDns() {
		GeneralName name = X509Support.generalName(new SubjectAltName.Dns("*.example.com"));
		assertEquals(GeneralName.dNSName, name.getTagNo());
		assertEquals("*.example.com", ASN1IA5String.getInstance(name.getName()).getString());
	}
	
	@Test
	void generalNameWithIpv4() {
		GeneralName name = X509Support.generalName(new SubjectAltName.Ip(IpAddresses.parse("192.0.2.1")));
		assertEquals(GeneralName.iPAddress, name.getTagNo());
		assertArrayEquals(new byte[] { (byte) 192, 0, 2, 1 }, ASN1OctetString.getInstance(name.getName()).getOctets());
	}
	
	@Test
	void generalNameWithIpv6() {
		GeneralName name = X509Support.generalName(new SubjectAltName.Ip(IpAddresses.parse("2001:db8::1")));
		byte[] octets = ASN1OctetString.getInstance(name.getName()).getOctets();
		assertEquals(GeneralName.iPAddress, name.getTagNo());
		assertEquals(16, octets.length);
		assertEquals(0x20, octets[0]);
		assertEquals(1, octets[15]);
	}
	
	@Test
	void generalNameWithUri() {
		GeneralName name = X509Support.generalName(new SubjectAltName.Uri(URI.create("spiffe://example.org/service")));
		assertEquals(GeneralName.uniformResourceIdentifier, name.getTagNo());
		assertEquals("spiffe://example.org/service", ASN1IA5String.getInstance(name.getName()).getString());
	}
	
	@Test
	void generalNameWithEmail() {
		GeneralName name = X509Support.generalName(new SubjectAltName.Email("admin@example.com"));
		assertEquals(GeneralName.rfc822Name, name.getTagNo());
		assertEquals("admin@example.com", ASN1IA5String.getInstance(name.getName()).getString());
	}
	
	@Test
	void generalNamesWithEmptyList() {
		assertEquals(0, X509Support.generalNames(List.of()).getNames().length);
	}
	
	@Test
	void subjectAltNamesWithNullExtensions() {
		assertEquals(List.of(), X509Support.subjectAltNames(null));
	}
	
	@Test
	void subjectAltNamesWithoutExtension() throws Exception {
		ExtensionsGenerator generator = new ExtensionsGenerator();
		generator.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));
		assertEquals(List.of(), X509Support.subjectAltNames(generator.generate()));
	}
	
	@Test
	void subjectAltNamesSkipsUnmodeledKind() throws Exception {
		Extensions extensions = sanExtensions(new GeneralName(new X500Name("CN=Directory")), new GeneralName(GeneralName.dNSName, "example.com"));
		assertEquals(List.of(new SubjectAltName.Dns("example.com")), X509Support.subjectAltNames(extensions));
	}
	
	@Test
	void randomSerialIsPositive() {
		BigInteger first = X509Support.randomSerial();
		BigInteger second = X509Support.randomSerial();
		assertEquals(1, first.signum());
		assertTrue(first.bitLength() <= 128);
		assertEquals(1, second.signum());
		assertNotEquals(first, second);
	}
	
	@Test
	void principalConvertsName() throws Exception {
		X500Principal principal = X509Support.principal(SUBJECT);
		assertArrayEquals(SUBJECT.getEncoded(ASN1Encoding.DER), principal.getEncoded());
		assertEquals(new X500Principal(SUBJECT.getEncoded(ASN1Encoding.DER)), principal);
		assertEquals("O=LUtils,CN=LUtils Test", principal.getName());
	}
	
	@Test
	void nameKeepsEncoding() throws Exception {
		X500Principal principal = new X500Principal("CN=Name Test,O=LUtils,C=DE");
		X500Name name = X509Support.name(principal);
		assertArrayEquals(principal.getEncoded(), name.getEncoded(ASN1Encoding.DER));
		assertEquals(3, name.getRDNs().length);
	}
	
	@Test
	void holderReadsCertificate() throws Exception {
		X509CertificateHolder holder = X509Support.holder(certificate);
		assertEquals(certificate.getSerialNumber(), holder.getSerialNumber());
		assertEquals(SUBJECT, holder.getSubject());
		assertArrayEquals(certificate.getEncoded(), holder.getEncoded());
	}
	
	@Test
	void certificateConvertsHolder() {
		X509Certificate converted = X509Support.certificate(X509Support.holder(certificate));
		assertEquals(certificate, converted);
		assertEquals(certificate.getSubjectX500Principal(), converted.getSubjectX500Principal());
	}
	
	@Test
	void spkiMatchesPublicKey() {
		assertArrayEquals(ed25519.getPublic().getEncoded(), X509Support.spki(certificate));
		assertArrayEquals(certificate.getPublicKey().getEncoded(), X509Support.spki(certificate));
	}
	
	@Test
	void keyIdentifierIsTruncatedSha256() {
		byte[] digest = Hashes.hash(HashAlgorithm.SHA_256, ed25519Info.getPublicKeyData().getBytes());
		byte[] identifier = X509Support.keyIdentifier(ed25519Info);
		assertEquals(20, identifier.length);
		assertArrayEquals(Arrays.copyOf(digest, 20), identifier);
	}
	
	@Test
	void contentSignerWithMatchingKey() {
		ContentSigner signer = X509Support.contentSigner(SignatureAlgorithm.ED25519, ed25519.getPrivate());
		assertEquals("1.3.101.112", signer.getAlgorithmIdentifier().getAlgorithm().getId());
		assertNotNull(signer.getOutputStream());
	}
	
	@Test
	void verifierProviderVerifiesIssuedCertificate() throws Exception {
		X509CertificateHolder holder = X509Support.holder(certificate);
		assertTrue(holder.isSignatureValid(X509Support.verifierProvider(ed25519.getPublic())));
		assertFalse(holder.isSignatureValid(X509Support.verifierProvider(authority.getPublic())));
	}
	
	@Test
	void subjectAltNamesReadsEveryKind() throws Exception {
		Extensions extensions = sanExtensions(
			new GeneralName(GeneralName.dNSName, "example.com"),
			new GeneralName(GeneralName.iPAddress, new DEROctetString(new byte[] { 10, 0, 0, 1 })),
			new GeneralName(GeneralName.uniformResourceIdentifier, "https://example.com/path"),
			new GeneralName(GeneralName.rfc822Name, "admin@example.com")
		);
		List<SubjectAltName> expected = List.of(
			new SubjectAltName.Dns("example.com"),
			new SubjectAltName.Ip(IpAddresses.parse("10.0.0.1")),
			new SubjectAltName.Uri(URI.create("https://example.com/path")),
			new SubjectAltName.Email("admin@example.com")
		);
		assertEquals(expected, X509Support.subjectAltNames(extensions));
	}
	
	@Test
	void issueSelfSigned() throws Exception {
		X509Certificate issued = selfSigned(SUBJECT, SPEC);
		X509CertificateHolder holder = X509Support.holder(issued);
		byte[] identifier = X509Support.keyIdentifier(ed25519Info);
		assertEquals(issued.getSubjectX500Principal(), issued.getIssuerX500Principal());
		assertEquals(Date.from(NOW), issued.getNotBefore());
		assertEquals(Date.from(NOW.plus(Duration.ofDays(1))), issued.getNotAfter());
		assertArrayEquals(identifier, SubjectKeyIdentifier.fromExtensions(holder.getExtensions()).getKeyIdentifier());
		assertArrayEquals(identifier, org.bouncycastle.asn1.x509.AuthorityKeyIdentifier.fromExtensions(holder.getExtensions()).getKeyIdentifierObject().getOctets());
		assertDoesNotThrow(() -> issued.verify(ed25519.getPublic()));
	}
	
	@Test
	void issueByAuthority() throws Exception {
		SubjectPublicKeyInfo authorityInfo = SubjectPublicKeyInfo.getInstance(authority.getPublic().getEncoded());
		X509Certificate ca = X509Support.issue(AUTHORITY, authorityInfo, AUTHORITY, X509Support.keyIdentifier(authorityInfo), authority.getPrivate(), SignatureAlgorithm.ED25519, SPEC.withCa(0));
		X509Certificate leaf = X509Support.issue(SUBJECT, ed25519Info, AUTHORITY, X509Support.authorityKeyIdentifier(ca), authority.getPrivate(), SignatureAlgorithm.ED25519, SPEC);
		
		byte[] caIdentifier = SubjectKeyIdentifier.fromExtensions(X509Support.holder(ca).getExtensions()).getKeyIdentifier();
		byte[] leafAuthority = org.bouncycastle.asn1.x509.AuthorityKeyIdentifier.fromExtensions(X509Support.holder(leaf).getExtensions()).getKeyIdentifierObject().getOctets();
		assertArrayEquals(caIdentifier, leafAuthority);
		assertEquals(ca.getSubjectX500Principal(), leaf.getIssuerX500Principal());
		assertDoesNotThrow(() -> leaf.verify(authority.getPublic()));
		assertThrows(Exception.class, () -> leaf.verify(ed25519.getPublic()));
	}
	
	@Test
	void keyUsageWithEverything() {
		org.bouncycastle.asn1.x509.KeyUsage usage = X509Support.keyUsage(EnumSet.allOf(KeyUsage.class));
		int all = org.bouncycastle.asn1.x509.KeyUsage.digitalSignature | org.bouncycastle.asn1.x509.KeyUsage.nonRepudiation | org.bouncycastle.asn1.x509.KeyUsage.keyEncipherment
			| org.bouncycastle.asn1.x509.KeyUsage.dataEncipherment | org.bouncycastle.asn1.x509.KeyUsage.keyAgreement | org.bouncycastle.asn1.x509.KeyUsage.keyCertSign
			| org.bouncycastle.asn1.x509.KeyUsage.cRLSign | org.bouncycastle.asn1.x509.KeyUsage.encipherOnly | org.bouncycastle.asn1.x509.KeyUsage.decipherOnly;
		assertEquals(new org.bouncycastle.asn1.x509.KeyUsage(all), usage);
		assertTrue(usage.hasUsages(all));
	}
	
	@Test
	void extendedKeyUsageIsSorted() {
		Set<ExtendedKeyUsage> usages = new LinkedHashSet<>(List.of(ExtendedKeyUsage.IPSEC_IKE, ExtendedKeyUsage.CODE_SIGNING, ExtendedKeyUsage.SERVER_AUTH));
		KeyPurposeId[] purposes = X509Support.extendedKeyUsage(usages).getUsages();
		assertEquals(3, purposes.length);
		assertEquals(KeyPurposeId.id_kp_serverAuth, purposes[0]);
		assertEquals(KeyPurposeId.id_kp_codeSigning, purposes[1]);
		assertEquals(KeyPurposeId.id_kp_ipsecIKE, purposes[2]);
	}
	
	@Test
	void generalNamesKeepsOrder() {
		List<SubjectAltName> names = List.of(new SubjectAltName.Email("a@example.com"), new SubjectAltName.Dns("example.com"), new SubjectAltName.Uri(URI.create("urn:test")));
		GeneralName[] generalNames = X509Support.generalNames(names).getNames();
		assertEquals(3, generalNames.length);
		assertEquals(GeneralName.rfc822Name, generalNames[0].getTagNo());
		assertEquals(GeneralName.dNSName, generalNames[1].getTagNo());
		assertEquals(GeneralName.uniformResourceIdentifier, generalNames[2].getTagNo());
	}
	
	@Test
	void subjectAltNamesRoundTrip() {
		List<SubjectAltName> names = List.of(
			new SubjectAltName.Dns("*.example.com"),
			new SubjectAltName.Ip(IpAddresses.parse("2001:db8::1")),
			new SubjectAltName.Ip(IpAddresses.parse("192.0.2.1")),
			new SubjectAltName.Uri(URI.create("spiffe://example.org/service")),
			new SubjectAltName.Email("admin@example.com")
		);
		List<SubjectAltName> read = X509Support.subjectAltNames(X509Support.holder(selfSigned(SUBJECT, SPEC.withSubjectAltNames(names))).getExtensions());
		assertEquals(names, read);
		assertThrows(UnsupportedOperationException.class, () -> read.add(new SubjectAltName.Dns("other.example.com")));
	}
	
	private record TestPublicKey(String format, byte @Nullable [] encoded) implements PublicKey {
		
		@Override
		public String getAlgorithm() {
			return "Test";
		}
		
		@Override
		public String getFormat() {
			return this.format;
		}
		
		@Override
		public byte @Nullable [] getEncoded() {
			return this.encoded;
		}
	}
}
