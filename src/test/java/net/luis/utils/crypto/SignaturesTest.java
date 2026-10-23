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
import net.luis.utils.resources.ResourceLocation;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Test class for {@link Signatures}.<br>
 *
 * @author Luis-St
 */
class SignaturesTest {
	
	private static final NativeSignatureAlgorithm FAKE = new NativeSignatureAlgorithm("fake", "NoSuchSig", "NoSuchKeyType", null, 1, false, false);
	private static final NativeSignatureAlgorithm FAKE_CURVE = new NativeSignatureAlgorithm("fake", "SHA256withECDSA", "EC", "not-a-curve", 1, false, false);
	private static final byte[] DATA = "the quick brown fox".getBytes(StandardCharsets.UTF_8);
	
	private static final Path DIRECTORY = Path.of("SignaturesTest-files");
	private static final Path FILE = DIRECTORY.resolve("content.bin");
	private static final Path EMPTY_FILE = DIRECTORY.resolve("empty.bin");
	private static final Path LARGE_FILE = DIRECTORY.resolve("large.bin");
	private static final Path CHANGING_FILE = DIRECTORY.resolve("changing.bin");
	
	private static KeyPair ed25519;
	private static KeyPair ecdsa;
	private static KeyPair hybrid;
	private static X509Certificate certificate;
	
	@BeforeAll
	static void setUp() throws Exception {
		Providers.installBouncyCastle();
		Files.createDirectories(DIRECTORY);
		Files.write(FILE, DATA);
		Files.write(EMPTY_FILE, new byte[0]);
		Files.write(LARGE_FILE, CryptoRandom.bytes(100000));
		
		ed25519 = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
		ecdsa = Signatures.generateKeyPair(SignatureAlgorithm.ECDSA_P256_SHA_256);
		hybrid = Signatures.generateKeyPair(SignatureAlgorithm.ED25519_ML_DSA_65);
		certificate = selfSignedCertificate(ed25519);
	}
	
	@AfterAll
	static void cleanUp() throws Exception {
		Files.deleteIfExists(FILE);
		Files.deleteIfExists(EMPTY_FILE);
		Files.deleteIfExists(LARGE_FILE);
		Files.deleteIfExists(CHANGING_FILE);
		Files.deleteIfExists(DIRECTORY);
	}
	
	private static X509Certificate selfSignedCertificate(KeyPair pair) throws Exception {
		AlgorithmIdentifier signatureId = new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.3.101.112"));
		V1TBSCertificateGenerator generator = new V1TBSCertificateGenerator();
		generator.setSerialNumber(new ASN1Integer(1));
		generator.setIssuer(new X500Name("CN=LUtils Test"));
		generator.setSubject(new X500Name("CN=LUtils Test"));
		generator.setStartDate(new Time(new Date(System.currentTimeMillis() - 86_400_000L)));
		generator.setEndDate(new Time(new Date(System.currentTimeMillis() + 86_400_000L)));
		generator.setSignature(signatureId);
		generator.setSubjectPublicKeyInfo(SubjectPublicKeyInfo.getInstance(pair.getPublic().getEncoded()));
		
		TBSCertificate tbs = generator.generateTBSCertificate();
		byte[] signature = Signatures.sign(SignatureAlgorithm.ED25519, pair.getPrivate(), tbs.getEncoded(ASN1Encoding.DER));
		ASN1EncodableVector vector = new ASN1EncodableVector();
		vector.add(tbs);
		vector.add(signatureId);
		vector.add(new DERBitString(signature));
		
		byte[] encoded = new DERSequence(vector).getEncoded(ASN1Encoding.DER);
		return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(encoded));
	}
	
	private static ThrowableSupplier<InputStream, IOException> counting(AtomicInteger counter) {
		return () -> {
			counter.incrementAndGet();
			return new ByteArrayInputStream(DATA);
		};
	}
	
	@Test
	void constructorIsPrivate() throws Exception {
		Constructor<?>[] constructors = Signatures.class.getDeclaredConstructors();
		assertEquals(1, constructors.length);
		assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
		assertTrue(Modifier.isFinal(Signatures.class.getModifiers()));
		
		Constructor<Signatures> constructor = Signatures.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		assertNotNull(constructor.newInstance());
	}
	
	@Test
	void generateKeyPairWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> Signatures.generateKeyPair(null));
	}
	
	@Test
	void generateKeyPairWithUnservedAlgorithm() {
		UnsupportedAlgorithmException exception = assertThrows(UnsupportedAlgorithmException.class, () -> Signatures.generateKeyPair(FAKE));
		assertTrue(exception.getMessage().contains("NoSuchKeyType"));
		assertInstanceOf(NoSuchAlgorithmException.class, exception.getCause());
	}
	
	@Test
	void generateKeyPairWithUnknownCurve() {
		UnsupportedAlgorithmException exception = assertThrows(UnsupportedAlgorithmException.class, () -> Signatures.generateKeyPair(FAKE_CURVE));
		assertInstanceOf(InvalidAlgorithmParameterException.class, exception.getCause());
	}
	
	@Test
	void signWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> Signatures.sign(null, ed25519.getPrivate(), DATA));
	}
	
	@Test
	void signWithNullKey() {
		assertEquals("Key must not be null", assertThrows(NullPointerException.class, () -> Signatures.sign(SignatureAlgorithm.ED25519, null, DATA)).getMessage());
	}
	
	@Test
	void signWithNullData() {
		assertEquals("Data must not be null", assertThrows(NullPointerException.class, () -> Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), (byte[]) null)).getMessage());
	}
	
	@Test
	void signWithNullDataAndNullAlgorithm() {
		assertEquals("Data must not be null", assertThrows(NullPointerException.class, () -> Signatures.sign(null, ed25519.getPrivate(), (byte[]) null)).getMessage());
	}
	
	@Test
	void signWithNullSource() {
		assertEquals("Source must not be null", assertThrows(NullPointerException.class, () -> Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), (ThrowableSupplier<InputStream, IOException>) null)).getMessage());
	}
	
	@Test
	void signWithNullFile() {
		assertEquals("File must not be null", assertThrows(NullPointerException.class, () -> Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), (Path) null)).getMessage());
	}
	
	@Test
	void signWithNullInputStream() {
		assertEquals("Input must not be null", assertThrows(NullPointerException.class, () -> Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), (InputStream) null)).getMessage());
	}
	
	@Test
	void signWithMissingFile() {
		Path missing = DIRECTORY.resolve("missing.bin");
		UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), missing));
		
		assertEquals("Failed to read the source to sign", exception.getMessage());
		assertInstanceOf(NoSuchFileException.class, exception.getCause());
	}
	
	@Test
	void signWithFailingSource() {
		ThrowableSupplier<InputStream, IOException> source = () -> {
			throw new IOException("broken");
		};
		assertThrows(UncheckedIOException.class, () -> Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), source));
	}
	
	@Test
	void signWithSourceFailingPartway() {
		ThrowableSupplier<InputStream, IOException> source = () -> new FailingStream(100);
		assertThrows(UncheckedIOException.class, () -> Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), source));
	}
	
	@Test
	void signHybridWithNonHybridKey() {
		assertThrows(ClassCastException.class, () -> Signatures.sign(SignatureAlgorithm.ED25519_ML_DSA_65, ed25519.getPrivate(), DATA));
	}
	
	@Test
	void verifyWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> Signatures.verify(null, ed25519.getPublic(), DATA, new byte[64]));
	}
	
	@Test
	void verifyWithNullKey() {
		assertEquals("Key must not be null", assertThrows(NullPointerException.class, () -> Signatures.verify(SignatureAlgorithm.ED25519, (PublicKey) null, DATA, new byte[64])).getMessage());
	}
	
	@Test
	void verifyWithNullData() {
		assertEquals("Data must not be null", assertThrows(NullPointerException.class, () -> Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), (byte[]) null, new byte[64])).getMessage());
	}
	
	@Test
	void verifyWithNullSignature() {
		assertEquals("Signature must not be null", assertThrows(NullPointerException.class, () -> Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), DATA, null)).getMessage());
	}
	
	@Test
	void verifyWithNullSource() {
		assertEquals("Source must not be null", assertThrows(NullPointerException.class, () -> Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), (ThrowableSupplier<InputStream, IOException>) null, new byte[64])).getMessage());
	}
	
	@Test
	void verifyWithNullFile() {
		assertEquals("File must not be null", assertThrows(NullPointerException.class, () -> Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), (Path) null, new byte[64])).getMessage());
	}
	
	@Test
	void verifyWithNullInputStream() {
		assertEquals("Input must not be null", assertThrows(NullPointerException.class, () -> Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), (InputStream) null, new byte[64])).getMessage());
	}
	
	@Test
	void verifyWithNullCertificate() {
		assertEquals("Certificate must not be null", assertThrows(NullPointerException.class, () -> Signatures.verify(SignatureAlgorithm.ED25519, (X509Certificate) null, DATA, new byte[64])).getMessage());
	}
	
	@Test
	void verifyWithMissingFile() {
		Path missing = DIRECTORY.resolve("missing.bin");
		UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), missing, new byte[64]));
		assertEquals("Failed to read the source to verify", exception.getMessage());
	}
	
	@Test
	void verifyHybridWithNonHybridKey() {
		byte[] signature = Signatures.sign(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPrivate(), DATA);
		assertThrows(ClassCastException.class, () -> Signatures.verify(SignatureAlgorithm.ED25519_ML_DSA_65, ed25519.getPublic(), DATA, signature));
	}
	
	@Test
	void verifyHybridWithMalformedSignature() {
		byte[] plain = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), DATA);
		byte[] negative = { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 1, 2, 3, 4 };
		
		for (byte[] malformed : new byte[][] { new byte[0], { 1, 2, 3 }, negative, plain }) {
			assertThrows(MalformedDataException.class, () -> Signatures.verify(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPublic(), DATA, malformed));
		}
	}
	
	@Test
	void requireWithWrongSignature() {
		AuthenticationException exception = assertThrows(AuthenticationException.class, () -> Signatures.require(SignatureAlgorithm.ED25519, ed25519.getPublic(), DATA, new byte[64]));
		assertTrue(exception.getMessage().contains(SignatureAlgorithm.ED25519.name()));
		assertInstanceOf(CryptoException.class, exception);
	}
	
	@Test
	void requireWithNullData() {
		assertThrows(NullPointerException.class, () -> Signatures.require(SignatureAlgorithm.ED25519, ed25519.getPublic(), null, new byte[64]));
	}
	
	@Test
	void signerWithNullAlgorithm() {
		assertEquals("Algorithm must not be null", assertThrows(NullPointerException.class, () -> Signatures.signer(null, ed25519.getPrivate())).getMessage());
	}
	
	@Test
	void signerWithNullKey() {
		assertEquals("Key must not be null", assertThrows(NullPointerException.class, () -> Signatures.signer(SignatureAlgorithm.ED25519, null)).getMessage());
	}
	
	@Test
	void signerWithBothNull() {
		assertEquals("Algorithm must not be null", assertThrows(NullPointerException.class, () -> Signatures.signer(null, null)).getMessage());
	}
	
	@Test
	void signerWithUnservedAlgorithm() {
		UnsupportedAlgorithmException exception = assertThrows(UnsupportedAlgorithmException.class, () -> Signatures.signer(FAKE, ed25519.getPrivate()));
		assertTrue(exception.getMessage().contains("NoSuchSig"));
	}
	
	@Test
	void signerWithWrongKeyType() {
		CryptoException exception = assertThrows(CryptoException.class, () -> Signatures.signer(SignatureAlgorithm.ED25519, ecdsa.getPrivate()));
		assertEquals("Invalid private key for Ed25519", exception.getMessage());
		assertInstanceOf(InvalidKeyException.class, exception.getCause());
	}
	
	@Test
	void signerWithPublicKeyAsPrivate() {
		CryptoException exception = assertThrows(CryptoException.class, () -> Signatures.signer(SignatureAlgorithm.ED25519, new UnusableKey()));
		assertInstanceOf(InvalidKeyException.class, exception.getCause());
	}
	
	@Test
	void verifierWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> Signatures.verifier(null, ed25519.getPublic()));
	}
	
	@Test
	void verifierWithNullKey() {
		assertThrows(NullPointerException.class, () -> Signatures.verifier(SignatureAlgorithm.ED25519, null));
	}
	
	@Test
	void verifierWithUnservedAlgorithm() {
		assertThrows(UnsupportedAlgorithmException.class, () -> Signatures.verifier(FAKE, ed25519.getPublic()));
	}
	
	@Test
	void verifierWithWrongKeyType() {
		CryptoException exception = assertThrows(CryptoException.class, () -> Signatures.verifier(SignatureAlgorithm.ED25519, ecdsa.getPublic()));
		assertEquals("Invalid public key for Ed25519", exception.getMessage());
	}
	
	@Test
	void generateKeyPairForNativeAlgorithmWithoutCurve() {
		for (NativeSignatureAlgorithm algorithm : new NativeSignatureAlgorithm[] { SignatureAlgorithm.ED25519, SignatureAlgorithm.ED448 }) {
			assertNull(algorithm.keySpec());
			KeyPair pair = Signatures.generateKeyPair(algorithm);
			assertEquals(algorithm.publicKeyLength(), pair.getPublic().getEncoded().length);
		}
	}
	
	@Test
	void generateKeyPairForNativeAlgorithmWithCurve() {
		for (NativeSignatureAlgorithm algorithm : new NativeSignatureAlgorithm[] { SignatureAlgorithm.ECDSA_P256_SHA_256, SignatureAlgorithm.ECDSA_P384_SHA_384, SignatureAlgorithm.ECDSA_P521_SHA_512 }) {
			assertNotNull(algorithm.keySpec());
			KeyPair pair = Signatures.generateKeyPair(algorithm);
			assertEquals("EC", pair.getPublic().getAlgorithm());
			assertEquals(algorithm.publicKeyLength(), pair.getPublic().getEncoded().length, algorithm.name());
		}
	}
	
	@Test
	void generateKeyPairForHybridAlgorithm() {
		for (SignatureAlgorithm algorithm : new SignatureAlgorithm[] { SignatureAlgorithm.ED25519_ML_DSA_65, SignatureAlgorithm.ED25519_SLH_DSA_SHA2_128S }) {
			assumeTrue(Providers.supports(algorithm));
			HybridSignatureAlgorithm hybridAlgorithm = assertInstanceOf(HybridSignatureAlgorithm.class, algorithm);
			KeyPair pair = Signatures.generateKeyPair(algorithm);
			
			HybridPublicKey publicKey = assertInstanceOf(HybridPublicKey.class, pair.getPublic());
			assertInstanceOf(HybridPrivateKey.class, pair.getPrivate());
			assertEquals(hybridAlgorithm.classical().publicKeyLength(), publicKey.classical().getEncoded().length);
		}
	}
	
	@Test
	void generateKeyPairForCompositeAlgorithm() {
		for (SignatureAlgorithm algorithm : SignatureAlgorithm.VALUES) {
			if (!algorithm.name().startsWith("MLDSA")) {
				continue;
			}
			assumeTrue(Providers.supports(algorithm));
			NativeSignatureAlgorithm composite = assertInstanceOf(NativeSignatureAlgorithm.class, algorithm);
			KeyPair pair = Signatures.generateKeyPair(composite);
			
			assertFalse(pair.getPublic() instanceof HybridPublicKey);
			assertEquals(composite.name(), pair.getPublic().getAlgorithm());
			assertEquals(composite.publicKeyLength(), pair.getPublic().getEncoded().length, composite.name());
		}
	}
	
	@Test
	void signAndVerifyWithNativeAlgorithm() {
		byte[] signature = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), DATA);
		byte[] other = DATA.clone();
		other[0] ^= 1;
		
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), DATA, signature));
		assertFalse(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), other, signature));
	}
	
	@Test
	void signAndVerifyWithCompositeAlgorithm() {
		NativeSignatureAlgorithm algorithm = SignatureAlgorithm.COMPOSITE_ML_DSA_65_ED25519;
		assumeTrue(Providers.supports(algorithm));
		KeyPair pair = Signatures.generateKeyPair(algorithm);
		byte[] signature = Signatures.sign(algorithm, pair.getPrivate(), DATA);
		byte[] other = DATA.clone();
		other[0] ^= 1;
		
		assertTrue(Signatures.verify(algorithm, pair.getPublic(), DATA, signature));
		assertFalse(Signatures.verify(algorithm, pair.getPublic(), other, signature));
		assertThrows(MalformedDataException.class, () -> Signatures.verify(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPublic(), DATA, signature));
	}
	
	@Test
	void signAndVerifyWithHybridAlgorithm() {
		byte[] signature = Signatures.sign(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPrivate(), DATA);
		HybridSignature parts = HybridSignature.parse(signature);
		byte[] other = DATA.clone();
		other[0] ^= 1;
		
		assertEquals(8 + parts.classical().length + parts.postQuantum().length, signature.length);
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPublic(), DATA, signature));
		assertFalse(Signatures.verify(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPublic(), other, signature));
	}
	
	@Test
	void verifyHybridWithBrokenClassicalHalf() {
		HybridSignature parts = HybridSignature.parse(Signatures.sign(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPrivate(), DATA));
		byte[] classical = parts.classical().clone();
		classical[0] ^= 1;
		
		assertFalse(Signatures.verify(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPublic(), DATA, HybridSignature.encode(classical, parts.postQuantum())));
	}
	
	@Test
	void verifyHybridWithBrokenPostQuantumHalf() {
		HybridSignature parts = HybridSignature.parse(Signatures.sign(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPrivate(), DATA));
		byte[] postQuantum = parts.postQuantum().clone();
		postQuantum[0] ^= 1;
		
		assertFalse(Signatures.verify(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPublic(), DATA, HybridSignature.encode(parts.classical(), postQuantum)));
	}
	
	@Test
	void verifyHybridWithBothHalvesValid() {
		byte[] signature = Signatures.sign(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPrivate(), DATA);
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPublic(), DATA, signature));
	}
	
	@Test
	void requireWithCorrectSignature() {
		byte[] signature = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), DATA);
		assertDoesNotThrow(() -> Signatures.require(SignatureAlgorithm.ED25519, ed25519.getPublic(), DATA, signature));
	}
	
	@Test
	void signerReturnsUsableSigner() {
		Signer signer = Signatures.signer(SignatureAlgorithm.ED25519, ed25519.getPrivate());
		assertNotNull(signer);
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), DATA, signer.update(DATA).sign()));
	}
	
	@Test
	void verifierReturnsUsableVerifier() {
		byte[] signature = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), DATA);
		Verifier verifier = Signatures.verifier(SignatureAlgorithm.ED25519, ed25519.getPublic());
		
		assertNotNull(verifier);
		assertTrue(verifier.update(DATA).verify(signature));
	}
	
	@Test
	void signWithEmptyData() {
		byte[] signature = assertDoesNotThrow(() -> Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), new byte[0]));
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), new byte[0], signature));
		assertFalse(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), DATA, signature));
	}
	
	@Test
	void signFromEmptyFile() {
		assertArrayEquals(Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), new byte[0]), Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), EMPTY_FILE));
	}
	
	@Test
	void signFromEmptyStream() {
		byte[] signature = assertDoesNotThrow(() -> Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), new ByteArrayInputStream(new byte[0])));
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), new byte[0], signature));
	}
	
	@Test
	void signatureLengthForNativeAlgorithms() {
		assertEquals(64, Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), DATA).length);
		
		KeyPair ed448 = Signatures.generateKeyPair(SignatureAlgorithm.ED448);
		assertEquals(114, Signatures.sign(SignatureAlgorithm.ED448, ed448.getPrivate(), DATA).length);
		
		int length = Signatures.sign(SignatureAlgorithm.ECDSA_P256_SHA_256, ecdsa.getPrivate(), DATA).length;
		assertTrue(length >= 68 && length <= 72, "was " + length);
	}
	
	@Test
	void verifyWithTamperedSignature() {
		byte[] signature = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), DATA);
		byte[] flipped = signature.clone();
		flipped[0] ^= 1;
		
		assertFalse(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), DATA, flipped));
		assertFalse(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), DATA, Arrays.copyOf(signature, 32)));
		assertFalse(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), DATA, new byte[0]));
	}
	
	@Test
	void verifyWithWrongKey() {
		byte[] signature = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), DATA);
		KeyPair other = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
		assertFalse(Signatures.verify(SignatureAlgorithm.ED25519, other.getPublic(), DATA, signature));
	}
	
	@Test
	void verifyWithCertificate() {
		byte[] signature = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), DATA);
		
		assertEquals(ed25519.getPublic(), certificate.getPublicKey());
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519, certificate, DATA, signature));
		assertEquals(Signatures.verify(SignatureAlgorithm.ED25519, certificate.getPublicKey(), DATA, signature), Signatures.verify(SignatureAlgorithm.ED25519, certificate, DATA, signature));
		assertFalse(Signatures.verify(SignatureAlgorithm.ED25519, certificate, DATA, new byte[64]));
	}
	
	@Test
	void signFromFileMatchesSignFromBytes() {
		byte[] fromBytes = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), DATA);
		byte[] fromFile = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), FILE);
		
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), FILE, fromBytes));
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), DATA, fromFile));
	}
	
	@Test
	void signFromStreamMatchesSignFromBytes() {
		byte[] fromStream = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), new ByteArrayInputStream(DATA));
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), DATA, fromStream));
	}
	
	@Test
	void generateKeyPairProducesDistinctPairs() {
		assertFalse(Arrays.equals(Signatures.generateKeyPair(SignatureAlgorithm.ED25519).getPublic().getEncoded(), Signatures.generateKeyPair(SignatureAlgorithm.ED25519).getPublic().getEncoded()));
	}
	
	@Test
	void roundTripForEveryNativeAlgorithm() {
		for (SignatureAlgorithm algorithm : SignatureAlgorithm.VALUES) {
			if (!(algorithm instanceof NativeSignatureAlgorithm native0) || !Providers.supports(algorithm)) {
				continue;
			}
			KeyPair pair = Signatures.generateKeyPair(native0);
			byte[] signature = Signatures.sign(native0, pair.getPrivate(), DATA);
			byte[] other = DATA.clone();
			other[0] ^= 1;
			
			assertTrue(Signatures.verify(native0, pair.getPublic(), DATA, signature), native0.name());
			assertFalse(Signatures.verify(native0, pair.getPublic(), other, signature), native0.name());
		}
	}
	
	@Test
	void roundTripForEveryHybridAlgorithm() {
		for (SignatureAlgorithm algorithm : SignatureAlgorithm.VALUES) {
			if (!(algorithm instanceof HybridSignatureAlgorithm) || !Providers.supports(algorithm)) {
				continue;
			}
			KeyPair pair = Signatures.generateKeyPair(algorithm);
			byte[] signature = Signatures.sign(algorithm, pair.getPrivate(), DATA);
			HybridSignature parts = HybridSignature.parse(signature);
			
			assertEquals(8 + parts.classical().length + parts.postQuantum().length, signature.length, algorithm.name());
			assertTrue(Signatures.verify(algorithm, pair.getPublic(), DATA, signature), algorithm.name());
		}
	}
	
	@Test
	void roundTripForEveryCompositeAlgorithm() {
		for (SignatureAlgorithm algorithm : SignatureAlgorithm.VALUES) {
			if (!algorithm.name().startsWith("MLDSA") || !Providers.supports(algorithm)) {
				continue;
			}
			NativeSignatureAlgorithm composite = assertInstanceOf(NativeSignatureAlgorithm.class, algorithm);
			KeyPair pair = Signatures.generateKeyPair(composite);
			byte[] signature = Signatures.sign(composite, pair.getPrivate(), DATA);
			byte[] other = DATA.clone();
			other[0] ^= 1;
			
			assertTrue(Signatures.verify(composite, pair.getPublic(), DATA, signature), composite.name());
			assertFalse(Signatures.verify(composite, pair.getPublic(), other, signature), composite.name());
		}
	}
	
	@Test
	void compositeSignsFromAStream() {
		NativeSignatureAlgorithm algorithm = SignatureAlgorithm.COMPOSITE_ML_DSA_65_ED25519;
		assumeTrue(Providers.supports(algorithm));
		KeyPair pair = Signatures.generateKeyPair(algorithm);
		byte[] signature = Signatures.sign(algorithm, pair.getPrivate(), new ByteArrayInputStream(DATA));
		
		assertTrue(Signatures.verify(algorithm, pair.getPublic(), DATA, signature));
		assertTrue(Signatures.verify(algorithm, pair.getPublic(), new ByteArrayInputStream(DATA), signature));
	}
	
	@Test
	void compositeWorksWithTheIncrementalSigner() {
		NativeSignatureAlgorithm algorithm = SignatureAlgorithm.COMPOSITE_ML_DSA_65_ED25519;
		assumeTrue(Providers.supports(algorithm));
		KeyPair pair = Signatures.generateKeyPair(algorithm);
		byte[] signature = Signatures.signer(algorithm, pair.getPrivate()).update(DATA).sign();
		
		assertTrue(Signatures.verifier(algorithm, pair.getPublic()).update(DATA).verify(signature));
		assertTrue(Signatures.verify(algorithm, pair.getPublic(), DATA, signature));
	}
	
	@Test
	void compositeSourceIsOpenedExactlyOnce() {
		NativeSignatureAlgorithm composite = SignatureAlgorithm.COMPOSITE_ML_DSA_65_ED25519;
		assumeTrue(Providers.supports(composite));
		
		AtomicInteger compositeCount = new AtomicInteger();
		Signatures.sign(composite, Signatures.generateKeyPair(composite).getPrivate(), counting(compositeCount));
		assertEquals(1, compositeCount.get());
		
		AtomicInteger hybridCount = new AtomicInteger();
		Signatures.sign(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPrivate(), counting(hybridCount));
		assertEquals(2, hybridCount.get());
	}
	
	@Test
	void hybridSourceIsOpenedOncePerComponent() {
		AtomicInteger nativeCount = new AtomicInteger();
		byte[] nativeSignature = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), counting(nativeCount));
		assertEquals(1, nativeCount.get());
		
		AtomicInteger hybridCount = new AtomicInteger();
		byte[] hybridSignature = Signatures.sign(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPrivate(), counting(hybridCount));
		assertEquals(2, hybridCount.get());
		
		AtomicInteger verifyCount = new AtomicInteger();
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), counting(verifyCount), nativeSignature));
		assertEquals(1, verifyCount.get());
		
		AtomicInteger hybridVerifyCount = new AtomicInteger();
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPublic(), counting(hybridVerifyCount), hybridSignature));
		assertEquals(2, hybridVerifyCount.get());
	}
	
	@Test
	void hybridSignatureFromSingleUseSourceIsRejectedAtCompileTime() throws Exception {
		Method sign = Signatures.class.getMethod("sign", NativeSignatureAlgorithm.class, PrivateKey.class, InputStream.class);
		Method verify = Signatures.class.getMethod("verify", NativeSignatureAlgorithm.class, PublicKey.class, InputStream.class, byte[].class);
		
		assertEquals(NativeSignatureAlgorithm.class, sign.getParameterTypes()[0]);
		assertEquals(NativeSignatureAlgorithm.class, verify.getParameterTypes()[0]);
		assertThrows(NoSuchMethodException.class, () -> Signatures.class.getMethod("sign", SignatureAlgorithm.class, PrivateKey.class, InputStream.class));
	}
	
	@Test
	void compositeAndHybridProduceIncompatibleSignatures() {
		NativeSignatureAlgorithm composite = SignatureAlgorithm.COMPOSITE_ML_DSA_65_ED25519;
		assumeTrue(Providers.supports(composite));
		KeyPair compositePair = Signatures.generateKeyPair(composite);
		byte[] compositeSignature = Signatures.sign(composite, compositePair.getPrivate(), DATA);
		byte[] hybridSignature = Signatures.sign(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPrivate(), DATA);
		
		assertInstanceOf(HybridPublicKey.class, hybrid.getPublic());
		assertFalse(compositePair.getPublic() instanceof HybridPublicKey);
		assertThrows(MalformedDataException.class, () -> Signatures.verify(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPublic(), DATA, compositeSignature));
		assertFalse(Signatures.verify(composite, compositePair.getPublic(), DATA, hybridSignature));
	}
	
	@Test
	void hybridSignatureBindsComponentsToTheirSchemes() {
		HybridSignature parts = HybridSignature.parse(Signatures.sign(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPrivate(), DATA));
		byte[] swapped = HybridSignature.encode(parts.postQuantum(), parts.classical());
		assertFalse(Signatures.verify(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPublic(), DATA, swapped));
	}
	
	@Test
	void hybridSignatureRejectsSingleHalfOnly() {
		HybridSignature parts = HybridSignature.parse(Signatures.sign(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPrivate(), DATA));
		assertFalse(Signatures.verify(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPublic(), DATA, HybridSignature.encode(parts.classical(), new byte[0])));
	}
	
	@Test
	void signAndVerifyAcrossAllInputForms() {
		byte[] fromBytes = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), DATA);
		byte[] fromSource = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), () -> new ByteArrayInputStream(DATA));
		byte[] fromFile = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), FILE);
		byte[] fromStream = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), new ByteArrayInputStream(DATA));
		
		for (byte[] signature : new byte[][] { fromBytes, fromSource, fromFile, fromStream }) {
			assertTrue(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), DATA, signature));
			assertTrue(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), FILE, signature));
			assertTrue(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), new ByteArrayInputStream(DATA), signature));
		}
	}
	
	@Test
	void signLargeFile() {
		byte[] signature = Signatures.sign(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPrivate(), LARGE_FILE);
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPublic(), LARGE_FILE, signature));
	}
	
	@Test
	void requireAndVerifyAgreeAcrossInputs() {
		byte[] nativeSignature = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), DATA);
		byte[] tampered = nativeSignature.clone();
		tampered[0] ^= 1;
		
		for (byte[] candidate : new byte[][] { nativeSignature, tampered, Arrays.copyOf(nativeSignature, 32) }) {
			if (Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), DATA, candidate)) {
				assertDoesNotThrow(() -> Signatures.require(SignatureAlgorithm.ED25519, ed25519.getPublic(), DATA, candidate));
			} else {
				assertThrows(AuthenticationException.class, () -> Signatures.require(SignatureAlgorithm.ED25519, ed25519.getPublic(), DATA, candidate));
			}
		}
		
		byte[] hybridSignature = Signatures.sign(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPrivate(), DATA);
		assertDoesNotThrow(() -> Signatures.require(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPublic(), DATA, hybridSignature));
	}
	
	@Test
	void signerMatchesOneShotSign() {
		byte[] incremental = Signatures.signer(SignatureAlgorithm.ED25519, ed25519.getPrivate()).update(DATA).sign();
		
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), DATA, incremental));
		assertArrayEquals(Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), DATA), incremental);
	}
	
	@Test
	void verifierMatchesOneShotVerify() {
		byte[] signature = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), DATA);
		byte[] tampered = signature.clone();
		tampered[0] ^= 1;
		
		for (byte[] candidate : new byte[][] { signature, tampered, new byte[0] }) {
			assertEquals(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), DATA, candidate), Signatures.verifier(SignatureAlgorithm.ED25519, ed25519.getPublic()).update(DATA).verify(candidate));
		}
	}
	
	@Test
	void signDoesNotMutateInputs() {
		byte[] data = DATA.clone();
		byte[] signature = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), data);
		byte[] signatureCopy = signature.clone();
		
		Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), data, signature);
		Signatures.sign(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPrivate(), data);
		assertArrayEquals(DATA, data);
		assertArrayEquals(signatureCopy, signature);
	}
	
	@Test
	void keyPairFromGenerateIsUsableWithCryptoKeys() {
		PublicKey restoredPublic = CryptoKeys.publicKey(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPublic().getEncoded());
		PrivateKey restoredPrivate = CryptoKeys.privateKey(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPrivate().getEncoded());
		byte[] signature = Signatures.sign(SignatureAlgorithm.ED25519_ML_DSA_65, restoredPrivate, DATA);
		
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519_ML_DSA_65, restoredPublic, DATA, signature));
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPublic(), DATA, signature));
	}
	
	@Test
	void signResourceWithNullAlgorithm() {
		ResourceLocation resource = ResourceLocation.external(FILE.toString());
		assertThrows(NullPointerException.class, () -> Signatures.sign(null, ed25519.getPrivate(), resource));
	}
	
	@Test
	void signResourceWithNullKey() {
		ResourceLocation resource = ResourceLocation.external(FILE.toString());
		assertThrows(NullPointerException.class, () -> Signatures.sign(SignatureAlgorithm.ED25519, null, resource));
	}
	
	@Test
	void signWithNullResource() {
		assertThrows(NullPointerException.class, () -> Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), (ResourceLocation) null));
	}
	
	@Test
	void signResourceWithAllNull() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Signatures.sign(null, null, (ResourceLocation) null));
		assertEquals("Resource must not be null", exception.getMessage());
	}
	
	@Test
	void signMissingExternalResource() {
		ResourceLocation resource = ResourceLocation.external(DIRECTORY.resolve("missing.bin").toString());
		assertThrows(UncheckedIOException.class, () -> Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), resource));
	}
	
	@Test
	void signMissingInternalResource() {
		ResourceLocation resource = ResourceLocation.internal("does/not/exist.bin");
		assertThrows(NullPointerException.class, () -> Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), resource));
	}
	
	@Test
	void signResourceWithHybridSchemeAndNativeKey() {
		ResourceLocation resource = ResourceLocation.external(FILE.toString());
		assertThrows(ClassCastException.class, () -> Signatures.sign(SignatureAlgorithm.ED25519_ML_DSA_65, ed25519.getPrivate(), resource));
	}
	
	@Test
	void verifyResourceWithNullAlgorithm() {
		ResourceLocation resource = ResourceLocation.external(FILE.toString());
		assertThrows(NullPointerException.class, () -> Signatures.verify(null, ed25519.getPublic(), resource, new byte[64]));
	}
	
	@Test
	void verifyResourceWithNullKey() {
		ResourceLocation resource = ResourceLocation.external(FILE.toString());
		assertThrows(NullPointerException.class, () -> Signatures.verify(SignatureAlgorithm.ED25519, null, resource, new byte[64]));
	}
	
	@Test
	void verifyWithNullResource() {
		assertThrows(NullPointerException.class, () -> Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), (ResourceLocation) null, new byte[64]));
	}
	
	@Test
	void verifyResourceWithNullSignature() {
		ResourceLocation resource = ResourceLocation.external(FILE.toString());
		assertThrows(NullPointerException.class, () -> Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), resource, null));
	}
	
	@Test
	void verifyMissingExternalResource() {
		ResourceLocation resource = ResourceLocation.external(DIRECTORY.resolve("missing.bin").toString());
		assertThrows(UncheckedIOException.class, () -> Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), resource, new byte[64]));
	}
	
	@Test
	void verifyResourceWithMalformedHybridSignature() {
		ResourceLocation resource = ResourceLocation.external(FILE.toString());
		assertThrows(MalformedDataException.class, () -> Signatures.verify(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPublic(), resource, new byte[] { 1, 2, 3 }));
	}
	
	@Test
	void verifyResourceWithHybridSchemeAndNativeKey() {
		ResourceLocation resource = ResourceLocation.external(FILE.toString());
		assertThrows(ClassCastException.class, () -> Signatures.verify(SignatureAlgorithm.ED25519_ML_DSA_65, ed25519.getPublic(), resource, new byte[64]));
	}
	
	@Test
	void signExternalResourceWithNativeScheme() {
		byte[] signature = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), ResourceLocation.external(FILE.toString()));
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), DATA, signature));
	}
	
	@Test
	void signInternalResourceWithNativeScheme() throws Exception {
		ResourceLocation resource = ResourceLocation.internal("ResourceLocation/ResourceLocation.json");
		byte[] signature = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), resource);
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), resource.getBytes(), signature));
	}
	
	@Test
	void signResourceWithHybridScheme() {
		ResourceLocation resource = ResourceLocation.external(FILE.toString());
		byte[] signature = assertDoesNotThrow(() -> Signatures.sign(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPrivate(), resource));
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPublic(), resource, signature));
	}
	
	@Test
	void verifyResourceReturnsTrueForMatchingSignature() {
		ResourceLocation resource = ResourceLocation.external(FILE.toString());
		byte[] signature = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), DATA);
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), resource, signature));
	}
	
	@Test
	void verifyResourceReturnsFalseForForeignSignature() {
		ResourceLocation resource = ResourceLocation.external(FILE.toString());
		byte[] signature = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), new byte[0]);
		assertFalse(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), resource, signature));
	}
	
	@Test
	void verifyResourceReturnsFalseForWrongKey() {
		ResourceLocation resource = ResourceLocation.external(FILE.toString());
		byte[] signature = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), DATA);
		KeyPair other = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
		
		assertFalse(Signatures.verify(SignatureAlgorithm.ED25519, other.getPublic(), resource, signature));
	}
	
	@Test
	void signResourceMatchesSignBytes() {
		byte[] fromResource = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), ResourceLocation.external(FILE.toString()));
		assertArrayEquals(Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), DATA), fromResource);
	}
	
	@Test
	void signResourceMatchesSignFile() {
		byte[] fromResource = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), ResourceLocation.external(FILE.toString()));
		assertArrayEquals(Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), FILE), fromResource);
	}
	
	@Test
	void signEmptyResource() {
		byte[] signature = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), ResourceLocation.external(EMPTY_FILE.toString()));
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), new byte[0], signature));
	}
	
	@Test
	void verifyEmptyResource() {
		ResourceLocation resource = ResourceLocation.external(EMPTY_FILE.toString());
		byte[] empty = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), new byte[0]);
		byte[] overData = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), DATA);
		
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), resource, empty));
		assertFalse(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), resource, overData));
	}
	
	@Test
	void verifyResourceWithEmptySignature() {
		ResourceLocation resource = ResourceLocation.external(FILE.toString());
		assertFalse(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), resource, new byte[0]));
	}
	
	@Test
	void signAndVerifyResourceRoundTrip() {
		ResourceLocation resource = ResourceLocation.external(FILE.toString());
		for (SignatureAlgorithm algorithm : new SignatureAlgorithm[] { SignatureAlgorithm.ED25519, SignatureAlgorithm.ECDSA_P256_SHA_256, SignatureAlgorithm.ED25519_ML_DSA_65 }) {
			assumeTrue(Providers.supports(algorithm));
			KeyPair pair = Signatures.generateKeyPair(algorithm);
			byte[] signature = Signatures.sign(algorithm, pair.getPrivate(), resource);
			assertTrue(Signatures.verify(algorithm, pair.getPublic(), resource, signature));
		}
	}
	
	@Test
	void verifyResourceDetectsContentChange() throws Exception {
		Files.write(CHANGING_FILE, DATA);
		ResourceLocation resource = ResourceLocation.external(CHANGING_FILE.toString());
		byte[] signature = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), resource);
		
		byte[] changed = Arrays.copyOf(DATA, DATA.length);
		changed[0] ^= 0x01;
		Files.write(CHANGING_FILE, changed);
		
		assertFalse(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), resource, signature));
	}
	
	@Test
	void signResourceReopensPerComponentForHybrid() {
		ResourceLocation resource = ResourceLocation.external(FILE.toString());
		byte[] signature = Signatures.sign(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPrivate(), resource);
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPublic(), resource, signature));
		
		AtomicInteger verifyCount = new AtomicInteger();
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPublic(), counting(verifyCount), signature));
		assertEquals(2, verifyCount.get());
	}
	
	@Test
	void signLargeResource() throws Exception {
		ResourceLocation resource = ResourceLocation.external(LARGE_FILE.toString());
		byte[] signature = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), resource);
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), Files.readAllBytes(LARGE_FILE), signature));
	}
	
	@Test
	void verifyResourceAgainstHybridSignatureWithSwappedComponents() {
		ResourceLocation resource = ResourceLocation.external(FILE.toString());
		byte[] foreign = Signatures.sign(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPrivate(), new byte[] { 9, 9, 9 });
		
		assertFalse(Signatures.verify(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid.getPublic(), resource, foreign));
	}
	
	@Test
	void signResourceThroughSignerMatchesStaticOverload() {
		ResourceLocation resource = ResourceLocation.external(FILE.toString());
		byte[] fromStatic = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), resource);
		byte[] fromSigner = Signatures.signer(SignatureAlgorithm.ED25519, ed25519.getPrivate()).update(resource).sign();
		
		assertArrayEquals(fromStatic, fromSigner);
	}
	
	private static final class FailingStream extends InputStream {
		
		private int remaining;
		
		private FailingStream(int remaining) {
			this.remaining = remaining;
		}
		
		@Override
		public int read() throws IOException {
			byte[] single = new byte[1];
			return this.read(single, 0, 1) == -1 ? -1 : single[0] & 0xFF;
		}
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException {
			if (this.remaining <= 0) {
				throw new IOException("broken");
			}
			
			int read = Math.min(this.remaining, length);
			Arrays.fill(buffer, offset, offset + read, (byte) 0);
			this.remaining -= read;
			return read;
		}
	}
	
	private record UnusableKey() implements PrivateKey {
		
		@Override
		public String getAlgorithm() {
			return "Ed25519";
		}
		
		@Override
		public String getFormat() {
			return "RAW";
		}
		
		@Override
		public byte[] getEncoded() {
			return null;
		}
	}
}
