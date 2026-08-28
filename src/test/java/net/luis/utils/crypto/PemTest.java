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

import net.luis.utils.crypto.algorithm.KemAlgorithm;
import net.luis.utils.crypto.algorithm.SignatureAlgorithm;
import net.luis.utils.crypto.exception.MalformedDataException;
import net.luis.utils.crypto.util.CryptoRandom;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.junit.jupiter.api.*;

import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Test class for {@link Pem}.<br>
 *
 * @author Luis-St
 */
class PemTest {
	
	private static final Path DIRECTORY = Path.of("PemTest-files");
	
	private static KeyPair ed25519;
	private static X509Certificate certificate;
	private static boolean posix;
	
	@BeforeAll
	static void setUp() throws Exception {
		Providers.installBouncyCastle();
		Files.createDirectories(DIRECTORY);
		ed25519 = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
		certificate = selfSignedCertificate(ed25519);
		posix = Files.getFileStore(DIRECTORY.toAbsolutePath()).supportsFileAttributeView(PosixFileAttributeView.class);
	}
	
	@AfterAll
	static void cleanUp() throws Exception {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(DIRECTORY)) {
			for (Path file : stream) {
				Files.deleteIfExists(file);
			}
		}
		Files.deleteIfExists(DIRECTORY);
	}
	
	private static X509Certificate selfSignedCertificate(KeyPair pair) throws Exception {
		AlgorithmIdentifier signatureId = new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.3.101.112"));
		V1TBSCertificateGenerator generator = new V1TBSCertificateGenerator();
		generator.setSerialNumber(new ASN1Integer(System.nanoTime() & 0x7FFFFFFFL));
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
	
	@Test
	void constructorIsPrivate() throws Exception {
		Constructor<?>[] constructors = Pem.class.getDeclaredConstructors();
		assertEquals(1, constructors.length);
		assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
		assertTrue(Modifier.isFinal(Pem.class.getModifiers()));
		
		Constructor<Pem> constructor = Pem.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		assertNotNull(constructor.newInstance());
	}
	
	@Test
	void constructDocument() {
		Pem.Document document = new Pem.Document(Pem.PUBLIC_KEY, new byte[] { 1, 2, 3 });
		assertEquals("PUBLIC KEY", document.label());
		assertArrayEquals(new byte[] { 1, 2, 3 }, document.der());
	}
	
	@Test
	void constructDocumentWithNullLabel() {
		assertEquals("Label must not be null", assertThrows(NullPointerException.class, () -> new Pem.Document(null, new byte[0])).getMessage());
	}
	
	@Test
	void constructDocumentWithNullDer() {
		assertEquals("Der must not be null", assertThrows(NullPointerException.class, () -> new Pem.Document("TEST", null)).getMessage());
	}
	
	@Test
	void constructDocumentWithBothNull() {
		assertEquals("Label must not be null", assertThrows(NullPointerException.class, () -> new Pem.Document(null, null)).getMessage());
	}
	
	@Test
	void encodeWithNullLabel() {
		assertEquals("Label must not be null", assertThrows(NullPointerException.class, () -> Pem.encode(null, new byte[0])).getMessage());
	}
	
	@Test
	void encodeWithNullDer() {
		assertEquals("Der must not be null", assertThrows(NullPointerException.class, () -> Pem.encode("TEST", null)).getMessage());
	}
	
	@Test
	void encodeWithBothNull() {
		assertEquals("Label must not be null", assertThrows(NullPointerException.class, () -> Pem.encode(null, null)).getMessage());
	}
	
	@Test
	void encodeWithNullKey() {
		assertEquals("Key must not be null", assertThrows(NullPointerException.class, () -> Pem.encode((Key) null)).getMessage());
	}
	
	@Test
	void encodeWithNullCertificate() {
		assertEquals("Certificate must not be null", assertThrows(NullPointerException.class, () -> Pem.encode((X509Certificate) null)).getMessage());
	}
	
	@Test
	void encodeKeyWithoutEncodedForm() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Pem.encode(new UnencodableKey()));
		assertEquals("Der must not be null", exception.getMessage());
	}
	
	@Test
	void decodeWithNullPem() {
		assertEquals("Pem must not be null", assertThrows(NullPointerException.class, () -> Pem.decode(null)).getMessage());
	}
	
	@Test
	void decodeWithNullExpectedLabel() {
		assertEquals("Expected label must not be null", assertThrows(NullPointerException.class, () -> Pem.decode(Pem.encode(ed25519.getPublic()), null)).getMessage());
	}
	
	@Test
	void decodeAllWithNullPem() {
		assertEquals("Pem must not be null", assertThrows(NullPointerException.class, () -> Pem.decodeAll(null)).getMessage());
	}
	
	@Test
	void decodeWithoutAnyDocument() {
		for (String input : new String[] { "", "not a pem", "-----BEGIN", "   \n\t  " }) {
			MalformedDataException exception = assertThrows(MalformedDataException.class, () -> Pem.decode(input));
			assertEquals("Not a PEM document", exception.getMessage());
		}
	}
	
	@Test
	void decodeWithWrongLabel() {
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> Pem.decode(Pem.encode(ed25519.getPublic()), Pem.PRIVATE_KEY));
		
		assertTrue(exception.getMessage().contains("PRIVATE KEY"));
		assertTrue(exception.getMessage().contains("PUBLIC KEY"));
	}
	
	@Test
	void decodeWithMismatchedBeginAndEndLabels() {
		String pem = "-----BEGIN PUBLIC KEY-----\nAAAA\n-----END PRIVATE KEY-----\n";
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> Pem.decode(pem));
		assertTrue(exception.getMessage().contains("Malformed PEM document at index"));
	}
	
	@Test
	void decodeWithMissingEndMarker() {
		assertThrows(MalformedDataException.class, () -> Pem.decode("-----BEGIN PUBLIC KEY-----\nAAAA\n"));
	}
	
	@Test
	void decodeWithMissingLabelTerminator() {
		assertThrows(MalformedDataException.class, () -> Pem.decode("-----BEGIN PUBLIC KEY"));
	}
	
	@Test
	void decodeWithInvalidBase64Body() {
		String pem = "-----BEGIN TEST-----\nAAAAA\n-----END TEST-----\n";
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> Pem.decode(pem));
		assertInstanceOf(IllegalArgumentException.class, exception.getCause());
	}
	
	@Test
	void writeWithNullFile() {
		assertEquals("File must not be null", assertThrows(NullPointerException.class, () -> Pem.write(null, ed25519.getPublic())).getMessage());
	}
	
	@Test
	void writeWithNullKey() {
		assertEquals("Key must not be null", assertThrows(NullPointerException.class, () -> Pem.write(DIRECTORY.resolve("x.pem"), null)).getMessage());
	}
	
	@Test
	void writeToUnwritablePath() {
		Path target = DIRECTORY.resolve("no-such-directory").resolve("key.pem");
		UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> Pem.write(target, ed25519.getPublic()));
		assertTrue(exception.getMessage().contains(target.toString()));
	}
	
	@Test
	void readWithNullFile() {
		assertEquals("File must not be null", assertThrows(NullPointerException.class, () -> Pem.read(null)).getMessage());
	}
	
	@Test
	void readMissingFile() {
		Path missing = DIRECTORY.resolve("missing.pem");
		UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> Pem.read(missing));
		
		assertTrue(exception.getMessage().contains(missing.toString()));
		assertInstanceOf(NoSuchFileException.class, exception.getCause());
	}
	
	@Test
	void readDirectoryAsFile() {
		assertThrows(UncheckedIOException.class, () -> Pem.read(DIRECTORY));
	}
	
	@Test
	void readMalformedFile() throws Exception {
		Path file = DIRECTORY.resolve("malformed.pem");
		Files.writeString(file, "not a pem");
		assertThrows(MalformedDataException.class, () -> Pem.read(file));
	}
	
	@Test
	void encodeWithEmptyDer() {
		assertEquals("-----BEGIN TEST-----\n-----END TEST-----\n", Pem.encode("TEST", new byte[0]));
	}
	
	@Test
	void encodeWithBodyShorterThanOneLine() {
		String[] lines = Pem.encode("TEST", new byte[3]).split("\n", -1);
		assertEquals(4, lines[1].length());
		assertEquals(3, lines.length - 1);
		assertEquals("-----END TEST-----", lines[2]);
	}
	
	@Test
	void encodeWithBodyExactlyOneLine() {
		String[] lines = Pem.encode("TEST", new byte[48]).split("\n", -1);
		assertEquals(64, lines[1].length());
		assertEquals("-----END TEST-----", lines[2]);
	}
	
	@Test
	void encodeWithBodyJustOverOneLine() {
		String[] lines = Pem.encode("TEST", new byte[49]).split("\n", -1);
		assertEquals(64, lines[1].length());
		assertEquals(4, lines[2].length());
		assertEquals("-----END TEST-----", lines[3]);
	}
	
	@Test
	void encodeWithMultiLineBody() {
		String[] lines = Pem.encode("TEST", CryptoRandom.bytes(1000)).split("\n", -1);
		for (int i = 1; i < lines.length - 3; i++) {
			assertEquals(64, lines[i].length(), "line " + i);
		}
		int last = lines.length - 3;
		assertTrue(lines[last].length() >= 1 && lines[last].length() <= 64);
	}
	
	@Test
	void encodePrivateKey() {
		String pem = Pem.encode(ed25519.getPrivate());
		Pem.Document document = Pem.decode(pem);
		
		assertEquals(Pem.PRIVATE_KEY, document.label());
		assertArrayEquals(ed25519.getPrivate().getEncoded(), document.der());
	}
	
	@Test
	void encodePublicKey() {
		Pem.Document document = Pem.decode(Pem.encode(ed25519.getPublic()));
		
		assertEquals(Pem.PUBLIC_KEY, document.label());
		assertArrayEquals(ed25519.getPublic().getEncoded(), document.der());
	}
	
	@Test
	void encodeSecretKeyUsesPublicLabel() {
		Pem.Document document = Pem.decode(Pem.encode(new SecretKeySpec(new byte[32], "AES")));
		
		assertEquals(Pem.PUBLIC_KEY, document.label());
		assertArrayEquals(new byte[32], document.der());
	}
	
	@Test
	void encodeCertificate() throws Exception {
		Pem.Document document = Pem.decode(Pem.encode(certificate));
		
		assertEquals(Pem.CERTIFICATE, document.label());
		assertArrayEquals(certificate.getEncoded(), document.der());
	}
	
	@Test
	void decodeAllWithNoDocuments() {
		List<Pem.Document> documents = assertDoesNotThrow(() -> Pem.decodeAll("just some text"));
		assertTrue(documents.isEmpty());
	}
	
	@Test
	void decodeAllWithSingleDocument() {
		List<Pem.Document> documents = Pem.decodeAll(Pem.encode(ed25519.getPublic()));
		
		assertEquals(1, documents.size());
		assertEquals(Pem.PUBLIC_KEY, documents.getFirst().label());
		assertArrayEquals(ed25519.getPublic().getEncoded(), documents.getFirst().der());
	}
	
	@Test
	void decodeAllWithMultipleDocuments() throws Exception {
		String pem = Pem.encode(certificate) + Pem.encode(ed25519.getPublic()) + Pem.encode(ed25519.getPrivate());
		List<Pem.Document> documents = Pem.decodeAll(pem);
		
		assertEquals(3, documents.size());
		assertEquals(Pem.CERTIFICATE, documents.get(0).label());
		assertEquals(Pem.PUBLIC_KEY, documents.get(1).label());
		assertEquals(Pem.PRIVATE_KEY, documents.get(2).label());
		assertArrayEquals(certificate.getEncoded(), documents.get(0).der());
	}
	
	@Test
	void decodeAllIgnoresTextBetweenDocuments() {
		String pem = "before\n" + Pem.encode(ed25519.getPublic()) + "between\n" + Pem.encode(ed25519.getPrivate()) + "after\n";
		List<Pem.Document> documents = Pem.decodeAll(pem);
		
		assertEquals(2, documents.size());
		assertArrayEquals(ed25519.getPublic().getEncoded(), documents.get(0).der());
		assertArrayEquals(ed25519.getPrivate().getEncoded(), documents.get(1).der());
	}
	
	@Test
	void decodeWithCorrectLabel() {
		Pem.Document document = assertDoesNotThrow(() -> Pem.decode(Pem.encode(ed25519.getPublic()), Pem.PUBLIC_KEY));
		assertEquals(Pem.PUBLIC_KEY, document.label());
	}
	
	@Test
	void decodeReturnsFirstDocument() {
		Pem.Document document = Pem.decode(Pem.encode(certificate) + Pem.encode(ed25519.getPublic()));
		assertEquals(Pem.CERTIFICATE, document.label());
	}
	
	@Test
	void writeToNewFileOnPosix() throws Exception {
		assumeTrue(posix);
		Path file = DIRECTORY.resolve("new-posix.pem");
		Files.deleteIfExists(file);
		
		Pem.write(file, ed25519.getPublic());
		assertTrue(Files.exists(file));
		assertEquals(Set.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE), Files.getPosixFilePermissions(file));
		assertArrayEquals(ed25519.getPublic().getEncoded(), Pem.read(file).der());
	}
	
	@Test
	void writeToExistingFileOnPosix() throws Exception {
		assumeTrue(posix);
		Path file = DIRECTORY.resolve("existing-posix.pem");
		Files.deleteIfExists(file);
		Files.createFile(file);
		Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("rw-rw-rw-"));
		
		Pem.write(file, ed25519.getPublic());
		assertEquals(Set.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE), Files.getPosixFilePermissions(file));
	}
	
	@Test
	void encodeLineTerminator() {
		String pem = Pem.encode(ed25519.getPublic());
		assertFalse(pem.contains("\r"));
		assertTrue(pem.endsWith("-----\n"));
	}
	
	@Test
	void encodeMarkerFormat() {
		String[] lines = Pem.encode("TEST", new byte[10]).split("\n", -1);
		assertEquals("-----BEGIN TEST-----", lines[0]);
		assertEquals("-----END TEST-----", lines[lines.length - 2]);
	}
	
	@Test
	void encodeWithEmptyLabel() {
		String pem = Pem.encode("", new byte[3]);
		
		assertTrue(pem.startsWith("-----BEGIN -----"));
		assertTrue(pem.contains("-----END -----"));
		assertEquals("", Pem.decodeAll(pem).getFirst().label());
	}
	
	@Test
	void encodeUsesStandardBase64() {
		String[] lines = Pem.encode("TEST", new byte[10]).split("\n", -1);
		assertTrue(lines[1].endsWith("="));
	}
	
	@Test
	void labelConstants() {
		assertEquals("PUBLIC KEY", Pem.PUBLIC_KEY);
		assertEquals("PRIVATE KEY", Pem.PRIVATE_KEY);
		assertEquals("CERTIFICATE", Pem.CERTIFICATE);
	}
	
	@Test
	void decodeAllReturnsUnmodifiableList() {
		List<Pem.Document> documents = Pem.decodeAll(Pem.encode(ed25519.getPublic()));
		
		assertThrows(UnsupportedOperationException.class, () -> documents.add(documents.getFirst()));
		assertThrows(UnsupportedOperationException.class, () -> documents.remove(0));
	}
	
	@Test
	void decodeToleratesWhitespaceInBody() {
		byte[] der = ed25519.getPublic().getEncoded();
		String body = Base64.getEncoder().encodeToString(der);
		String pem = "-----BEGIN TEST-----\n" + body.substring(0, 10) + " \t\n\n" + body.substring(10) + "\n-----END TEST-----\n";
		
		assertArrayEquals(der, Pem.decode(pem).der());
	}
	
	@Test
	void decodeToleratesCarriageReturns() {
		String pem = Pem.encode(ed25519.getPublic()).replace("\n", "\r\n");
		Pem.Document document = Pem.decode(pem);
		
		assertArrayEquals(ed25519.getPublic().getEncoded(), document.der());
		assertEquals(Pem.PUBLIC_KEY, document.label());
	}
	
	@Test
	void readFromFile() {
		Path file = DIRECTORY.resolve("read.pem");
		Pem.write(file, ed25519.getPublic());
		Pem.Document document = Pem.read(file);
		
		assertEquals(Pem.PUBLIC_KEY, document.label());
		assertArrayEquals(ed25519.getPublic().getEncoded(), document.der());
	}
	
	@Test
	void writeCreatesReadableDocument() throws Exception {
		Path file = DIRECTORY.resolve("readable.pem");
		Pem.write(file, ed25519.getPublic());
		assertEquals(Pem.encode(ed25519.getPublic()), Files.readString(file, StandardCharsets.US_ASCII));
	}
	
	@Test
	void encodeDecodeRoundTripForKeys() {
		List<Key> keys = new ArrayList<>();
		for (SignatureAlgorithm algorithm : new SignatureAlgorithm[] { SignatureAlgorithm.ED25519, SignatureAlgorithm.ED448, SignatureAlgorithm.ECDSA_P256_SHA_256 }) {
			KeyPair pair = Signatures.generateKeyPair(algorithm);
			keys.add(pair.getPublic());
			keys.add(pair.getPrivate());
		}
		KeyPair hybrid = Kems.generateKeyPair(KemAlgorithm.X25519_ML_KEM_768);
		keys.add(hybrid.getPublic());
		keys.add(hybrid.getPrivate());
		
		for (Key key : keys) {
			Pem.Document document = Pem.decode(Pem.encode(key));
			assertArrayEquals(key.getEncoded(), document.der());
			assertEquals(key instanceof PrivateKey ? Pem.PRIVATE_KEY : Pem.PUBLIC_KEY, document.label());
		}
	}
	
	@Test
	void encodeDecodeRoundTripForCertificate() throws Exception {
		assertArrayEquals(certificate.getEncoded(), Pem.decode(Pem.encode(certificate), Pem.CERTIFICATE).der());
	}
	
	@Test
	void roundTripThroughCryptoKeys() {
		KeyPair hybrid = Kems.generateKeyPair(KemAlgorithm.X25519_ML_KEM_768);
		Pem.Document document = Pem.decode(Pem.encode(hybrid.getPublic()), Pem.PUBLIC_KEY);
		PublicKey restored = CryptoKeys.publicKey(KemAlgorithm.X25519_ML_KEM_768, document.der());
		
		assertEquals(hybrid.getPublic(), restored);
		try (Kems.Encapsulation sent = Kems.encapsulate(KemAlgorithm.X25519_ML_KEM_768, restored)) {
			assertEquals(KemAlgorithm.X25519_ML_KEM_768.encapsulationLength(), sent.encapsulation().length);
		}
	}
	
	@Test
	void decodeRejectsPrivateKeyWhereAPublicOneIsExpected() {
		assertThrows(MalformedDataException.class, () -> Pem.decode(Pem.encode(ed25519.getPrivate()), Pem.PUBLIC_KEY));
	}
	
	@Test
	void decodeAllReadsACertificateChain() throws Exception {
		X509Certificate second = selfSignedCertificate(Signatures.generateKeyPair(SignatureAlgorithm.ED25519));
		X509Certificate third = selfSignedCertificate(Signatures.generateKeyPair(SignatureAlgorithm.ED25519));
		String chain = Pem.encode(certificate) + "\n" + Pem.encode(second) + "\n" + Pem.encode(third);
		List<Pem.Document> documents = Pem.decodeAll(chain);
		
		assertEquals(3, documents.size());
		assertArrayEquals(certificate.getEncoded(), documents.get(0).der());
		assertArrayEquals(second.getEncoded(), documents.get(1).der());
		assertArrayEquals(third.getEncoded(), documents.get(2).der());
	}
	
	@Test
	void encodeDecodeRoundTripAtLineBoundaries() {
		for (int size : new int[] { 0, 1, 47, 48, 49, 95, 96, 97, 1000 }) {
			byte[] der = CryptoRandom.bytes(size);
			assertArrayEquals(der, Pem.decode(Pem.encode("TEST", der)).der(), "size " + size);
		}
	}
	
	@Test
	void decodeIgnoresTrailingContentAfterLastDocument() {
		List<Pem.Document> documents = assertDoesNotThrow(() -> Pem.decodeAll(Pem.encode(ed25519.getPublic()) + "trailing prose with no markers\n"));
		assertEquals(1, documents.size());
	}
	
	@Test
	void decodeWithNestedMarkersInBody() {
		String pem = "-----BEGIN TEST-----\nAAAA\n-----BEGIN INNER-----\n-----END TEST-----\n";
		Pem.Document document = assertDoesNotThrow(() -> Pem.decode(pem));
		
		assertEquals("TEST", document.label());
		assertArrayEquals(Base64.getMimeDecoder().decode("AAAA-----BEGININNER-----"), document.der());
		assertEquals(1, Pem.decodeAll(pem).size());
	}
	
	@Test
	void writeThenReadPreservesKey() {
		Path publicFile = DIRECTORY.resolve("public.pem");
		Path privateFile = DIRECTORY.resolve("private.pem");
		Pem.write(publicFile, ed25519.getPublic());
		Pem.write(privateFile, ed25519.getPrivate());
		
		assertEquals(Pem.PUBLIC_KEY, Pem.read(publicFile).label());
		assertArrayEquals(ed25519.getPublic().getEncoded(), Pem.read(publicFile).der());
		assertEquals(Pem.PRIVATE_KEY, Pem.read(privateFile).label());
		assertArrayEquals(ed25519.getPrivate().getEncoded(), Pem.read(privateFile).der());
	}
	
	@Test
	void writeOverwritesExistingContent() throws Exception {
		Path file = DIRECTORY.resolve("overwrite.pem");
		KeyPair second = Signatures.generateKeyPair(SignatureAlgorithm.ED448);
		
		Pem.write(file, ed25519.getPublic());
		Pem.write(file, second.getPublic());
		assertArrayEquals(second.getPublic().getEncoded(), Pem.read(file).der());
		assertEquals(Pem.encode(second.getPublic()).length(), Files.readString(file, StandardCharsets.US_ASCII).length());
	}
	
	@Test
	void writtenFileIsAsciiOnly() throws Exception {
		Path file = DIRECTORY.resolve("ascii.pem");
		Pem.write(file, ed25519.getPublic());
		
		for (byte value : Files.readAllBytes(file)) {
			assertTrue((value & 0xFF) < 0x80);
		}
	}
	
	@Test
	void documentEqualsIsIdentityBasedForArrayComponent() {
		byte[] der = { 1, 2, 3 };
		Pem.Document first = new Pem.Document("TEST", der);
		
		assertNotEquals(new Pem.Document("TEST", der.clone()), first);
		assertEquals(new Pem.Document("TEST", der), first);
		assertEquals(first, first);
	}
	
	@Test
	void documentDoesNotAliasDecodedBytes() {
		String pem = Pem.encode(ed25519.getPublic());
		Pem.Document first = Pem.decode(pem);
		
		Arrays.fill(first.der(), (byte) 0);
		assertArrayEquals(ed25519.getPublic().getEncoded(), Pem.decode(pem).der());
	}
	
	@Test
	void encodeDoesNotMutateInput() {
		byte[] der = CryptoRandom.bytes(100);
		byte[] copy = der.clone();
		Pem.encode("TEST", der);
		assertArrayEquals(copy, der);
	}
	
	@Test
	void largeCertificateChainRoundTrip() {
		StringBuilder builder = new StringBuilder();
		List<byte[]> bodies = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			byte[] der = CryptoRandom.bytes(2000);
			bodies.add(der);
			builder.append(Pem.encode(Pem.CERTIFICATE, der));
		}
		
		List<Pem.Document> documents = Pem.decodeAll(builder.toString());
		assertEquals(20, documents.size());
		for (int i = 0; i < 20; i++) {
			assertArrayEquals(bodies.get(i), documents.get(i).der(), "document " + i);
		}
	}
	
	@Test
	void decodeAllRethrowArmIsUnreachable() {
		String[] malformed = {
			"-----BEGIN PUBLIC KEY-----\nAAAA\n-----END PRIVATE KEY-----\n",
			"-----BEGIN PUBLIC KEY-----\nAAAA\n",
			"-----BEGIN PUBLIC KEY",
			"-----BEGIN TEST-----\nAAAAA\n-----END TEST-----\n"
		};
		for (String input : malformed) {
			MalformedDataException exception = assertThrows(MalformedDataException.class, () -> Pem.decodeAll(input));
			assertTrue(exception.getMessage().contains("Malformed PEM document at index"), input);
			assertNotNull(exception.getCause(), input);
		}
	}
	
	private record UnencodableKey() implements PublicKey {
		
		@Override
		public String getAlgorithm() {
			return "HSM";
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
