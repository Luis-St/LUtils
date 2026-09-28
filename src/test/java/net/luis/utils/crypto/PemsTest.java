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
import net.luis.utils.crypto.util.PemDocument;
import net.luis.utils.resources.ResourceLocation;
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
 * Test class for {@link Pems}.<br>
 *
 * @author Luis-St
 */
class PemsTest {
	
	private static final Path DIRECTORY = Path.of("PemsTest-files");
	private static final Path MALFORMED_FILE = DIRECTORY.resolve("malformed.pem");
	private static final byte[] FIXTURE_CONTENT = HexFormat.of().parseHex("000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f");
	
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
		
		String document = Pems.encode(ed25519.getPublic());
		Files.writeString(MALFORMED_FILE, document.substring(0, document.indexOf("-----END")), StandardCharsets.US_ASCII);
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
		Constructor<?>[] constructors = Pems.class.getDeclaredConstructors();
		assertEquals(1, constructors.length);
		assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
		assertTrue(Modifier.isFinal(Pems.class.getModifiers()));
		
		Constructor<Pems> constructor = Pems.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		assertNotNull(constructor.newInstance());
	}
	
	@Test
	void encodeWithNullLabel() {
		assertEquals("Label must not be null", assertThrows(NullPointerException.class, () -> Pems.encode(null, new byte[0])).getMessage());
	}
	
	@Test
	void encodeWithNullDer() {
		assertEquals("Content must not be null", assertThrows(NullPointerException.class, () -> Pems.encode("TEST", null)).getMessage());
	}
	
	@Test
	void encodeWithBothNull() {
		assertEquals("Label must not be null", assertThrows(NullPointerException.class, () -> Pems.encode(null, null)).getMessage());
	}
	
	@Test
	void encodeWithNullKey() {
		assertEquals("Key must not be null", assertThrows(NullPointerException.class, () -> Pems.encode((Key) null)).getMessage());
	}
	
	@Test
	void encodeWithNullCertificate() {
		assertEquals("Certificate must not be null", assertThrows(NullPointerException.class, () -> Pems.encode((X509Certificate) null)).getMessage());
	}
	
	@Test
	void encodeKeyWithoutEncodedForm() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Pems.encode(new UnencodableKey()));
		assertEquals("Content must not be null", exception.getMessage());
	}
	
	@Test
	void decodeWithNullPem() {
		assertEquals("Pems must not be null", assertThrows(NullPointerException.class, () -> Pems.decode(null)).getMessage());
	}
	
	@Test
	void decodeWithNullExpectedLabel() {
		assertEquals("Expected label must not be null", assertThrows(NullPointerException.class, () -> Pems.decode(Pems.encode(ed25519.getPublic()), null)).getMessage());
	}
	
	@Test
	void decodeAllWithNullPem() {
		assertEquals("Pems must not be null", assertThrows(NullPointerException.class, () -> Pems.decodeAll(null)).getMessage());
	}
	
	@Test
	void decodeWithoutAnyDocument() {
		for (String input : new String[] { "", "not a pem", "-----BEGIN", "   \n\t  " }) {
			MalformedDataException exception = assertThrows(MalformedDataException.class, () -> Pems.decode(input));
			assertEquals("Not a PEM document", exception.getMessage());
		}
	}
	
	@Test
	void decodeWithWrongLabel() {
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> Pems.decode(Pems.encode(ed25519.getPublic()), Pems.PRIVATE_KEY));
		
		assertTrue(exception.getMessage().contains("PRIVATE KEY"));
		assertTrue(exception.getMessage().contains("PUBLIC KEY"));
	}
	
	@Test
	void decodeWithMismatchedBeginAndEndLabels() {
		String pem = "-----BEGIN PUBLIC KEY-----\nAAAA\n-----END PRIVATE KEY-----\n";
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> Pems.decode(pem));
		assertTrue(exception.getMessage().contains("Malformed PEM document at index"));
	}
	
	@Test
	void decodeWithMissingEndMarker() {
		assertThrows(MalformedDataException.class, () -> Pems.decode("-----BEGIN PUBLIC KEY-----\nAAAA\n"));
	}
	
	@Test
	void decodeWithMissingLabelTerminator() {
		assertThrows(MalformedDataException.class, () -> Pems.decode("-----BEGIN PUBLIC KEY"));
	}
	
	@Test
	void decodeWithInvalidBase64Body() {
		String pem = "-----BEGIN TEST-----\nAAAAA\n-----END TEST-----\n";
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> Pems.decode(pem));
		assertInstanceOf(IllegalArgumentException.class, exception.getCause());
	}
	
	@Test
	void writeWithNullFile() {
		assertEquals("File must not be null", assertThrows(NullPointerException.class, () -> Pems.write((Path) null, ed25519.getPublic())).getMessage());
	}
	
	@Test
	void writeWithNullKey() {
		assertEquals("Key must not be null", assertThrows(NullPointerException.class, () -> Pems.write(DIRECTORY.resolve("x.pem"), null)).getMessage());
	}
	
	@Test
	void writeToUnwritablePath() {
		Path target = DIRECTORY.resolve("no-such-directory").resolve("key.pem");
		UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> Pems.write(target, ed25519.getPublic()));
		assertTrue(exception.getMessage().contains(target.toString()));
	}
	
	@Test
	void readWithNullFile() {
		assertEquals("File must not be null", assertThrows(NullPointerException.class, () -> Pems.read((Path) null)).getMessage());
	}
	
	@Test
	void readMissingFile() {
		Path missing = DIRECTORY.resolve("missing.pem");
		UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> Pems.read(missing));
		
		assertTrue(exception.getMessage().contains(missing.toString()));
		assertInstanceOf(NoSuchFileException.class, exception.getCause());
	}
	
	@Test
	void readDirectoryAsFile() {
		assertThrows(UncheckedIOException.class, () -> Pems.read(DIRECTORY));
	}
	
	@Test
	void readMalformedFile() throws Exception {
		Path file = DIRECTORY.resolve("malformed.pem");
		Files.writeString(file, "not a pem");
		assertThrows(MalformedDataException.class, () -> Pems.read(file));
	}
	
	@Test
	void encodeWithEmptyDer() {
		assertEquals("-----BEGIN TEST-----\n-----END TEST-----\n", Pems.encode("TEST", new byte[0]));
	}
	
	@Test
	void encodeWithBodyShorterThanOneLine() {
		String[] lines = Pems.encode("TEST", new byte[3]).split("\n", -1);
		assertEquals(4, lines[1].length());
		assertEquals(3, lines.length - 1);
		assertEquals("-----END TEST-----", lines[2]);
	}
	
	@Test
	void encodeWithBodyExactlyOneLine() {
		String[] lines = Pems.encode("TEST", new byte[48]).split("\n", -1);
		assertEquals(64, lines[1].length());
		assertEquals("-----END TEST-----", lines[2]);
	}
	
	@Test
	void encodeWithBodyJustOverOneLine() {
		String[] lines = Pems.encode("TEST", new byte[49]).split("\n", -1);
		assertEquals(64, lines[1].length());
		assertEquals(4, lines[2].length());
		assertEquals("-----END TEST-----", lines[3]);
	}
	
	@Test
	void encodeWithMultiLineBody() {
		String[] lines = Pems.encode("TEST", CryptoRandom.bytes(1000)).split("\n", -1);
		for (int i = 1; i < lines.length - 3; i++) {
			assertEquals(64, lines[i].length(), "line " + i);
		}
		int last = lines.length - 3;
		assertTrue(lines[last].length() >= 1 && lines[last].length() <= 64);
	}
	
	@Test
	void encodePrivateKey() {
		String pem = Pems.encode(ed25519.getPrivate());
		PemDocument document = Pems.decode(pem);
		
		assertEquals(Pems.PRIVATE_KEY, document.label());
		assertArrayEquals(ed25519.getPrivate().getEncoded(), document.content());
	}
	
	@Test
	void encodePublicKey() {
		PemDocument document = Pems.decode(Pems.encode(ed25519.getPublic()));
		
		assertEquals(Pems.PUBLIC_KEY, document.label());
		assertArrayEquals(ed25519.getPublic().getEncoded(), document.content());
	}
	
	@Test
	void encodeSecretKeyUsesPublicLabel() {
		PemDocument document = Pems.decode(Pems.encode(new SecretKeySpec(new byte[32], "AES")));
		
		assertEquals(Pems.PUBLIC_KEY, document.label());
		assertArrayEquals(new byte[32], document.content());
	}
	
	@Test
	void encodeCertificate() throws Exception {
		PemDocument document = Pems.decode(Pems.encode(certificate));
		
		assertEquals(Pems.CERTIFICATE, document.label());
		assertArrayEquals(certificate.getEncoded(), document.content());
	}
	
	@Test
	void decodeAllWithNoDocuments() {
		List<PemDocument> documents = assertDoesNotThrow(() -> Pems.decodeAll("just some text"));
		assertTrue(documents.isEmpty());
	}
	
	@Test
	void decodeAllWithSingleDocument() {
		List<PemDocument> documents = Pems.decodeAll(Pems.encode(ed25519.getPublic()));
		
		assertEquals(1, documents.size());
		assertEquals(Pems.PUBLIC_KEY, documents.getFirst().label());
		assertArrayEquals(ed25519.getPublic().getEncoded(), documents.getFirst().content());
	}
	
	@Test
	void decodeAllWithMultipleDocuments() throws Exception {
		String pem = Pems.encode(certificate) + Pems.encode(ed25519.getPublic()) + Pems.encode(ed25519.getPrivate());
		List<PemDocument> documents = Pems.decodeAll(pem);
		
		assertEquals(3, documents.size());
		assertEquals(Pems.CERTIFICATE, documents.get(0).label());
		assertEquals(Pems.PUBLIC_KEY, documents.get(1).label());
		assertEquals(Pems.PRIVATE_KEY, documents.get(2).label());
		assertArrayEquals(certificate.getEncoded(), documents.get(0).content());
	}
	
	@Test
	void decodeAllIgnoresTextBetweenDocuments() {
		String pem = "before\n" + Pems.encode(ed25519.getPublic()) + "between\n" + Pems.encode(ed25519.getPrivate()) + "after\n";
		List<PemDocument> documents = Pems.decodeAll(pem);
		
		assertEquals(2, documents.size());
		assertArrayEquals(ed25519.getPublic().getEncoded(), documents.get(0).content());
		assertArrayEquals(ed25519.getPrivate().getEncoded(), documents.get(1).content());
	}
	
	@Test
	void decodeWithCorrectLabel() {
		PemDocument document = assertDoesNotThrow(() -> Pems.decode(Pems.encode(ed25519.getPublic()), Pems.PUBLIC_KEY));
		assertEquals(Pems.PUBLIC_KEY, document.label());
	}
	
	@Test
	void decodeReturnsFirstDocument() {
		PemDocument document = Pems.decode(Pems.encode(certificate) + Pems.encode(ed25519.getPublic()));
		assertEquals(Pems.CERTIFICATE, document.label());
	}
	
	@Test
	void writeToNewFileOnPosix() throws Exception {
		assumeTrue(posix);
		Path file = DIRECTORY.resolve("new-posix.pem");
		Files.deleteIfExists(file);
		
		Pems.write(file, ed25519.getPublic());
		assertTrue(Files.exists(file));
		assertEquals(Set.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE), Files.getPosixFilePermissions(file));
		assertArrayEquals(ed25519.getPublic().getEncoded(), Pems.read(file).content());
	}
	
	@Test
	void writeToExistingFileOnPosix() throws Exception {
		assumeTrue(posix);
		Path file = DIRECTORY.resolve("existing-posix.pem");
		Files.deleteIfExists(file);
		Files.createFile(file);
		Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("rw-rw-rw-"));
		
		Pems.write(file, ed25519.getPublic());
		assertEquals(Set.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE), Files.getPosixFilePermissions(file));
	}
	
	@Test
	void encodeLineTerminator() {
		String pem = Pems.encode(ed25519.getPublic());
		assertFalse(pem.contains("\r"));
		assertTrue(pem.endsWith("-----\n"));
	}
	
	@Test
	void encodeMarkerFormat() {
		String[] lines = Pems.encode("TEST", new byte[10]).split("\n", -1);
		assertEquals("-----BEGIN TEST-----", lines[0]);
		assertEquals("-----END TEST-----", lines[lines.length - 2]);
	}
	
	@Test
	void encodeWithEmptyLabel() {
		String pem = Pems.encode("", new byte[3]);
		
		assertTrue(pem.startsWith("-----BEGIN -----"));
		assertTrue(pem.contains("-----END -----"));
		assertEquals("", Pems.decodeAll(pem).getFirst().label());
	}
	
	@Test
	void encodeUsesStandardBase64() {
		String[] lines = Pems.encode("TEST", new byte[10]).split("\n", -1);
		assertTrue(lines[1].endsWith("="));
	}
	
	@Test
	void labelConstants() {
		assertEquals("PUBLIC KEY", Pems.PUBLIC_KEY);
		assertEquals("PRIVATE KEY", Pems.PRIVATE_KEY);
		assertEquals("CERTIFICATE", Pems.CERTIFICATE);
	}
	
	@Test
	void decodeAllReturnsUnmodifiableList() {
		List<PemDocument> documents = Pems.decodeAll(Pems.encode(ed25519.getPublic()));
		
		assertThrows(UnsupportedOperationException.class, () -> documents.add(documents.getFirst()));
		assertThrows(UnsupportedOperationException.class, () -> documents.remove(0));
	}
	
	@Test
	void decodeToleratesWhitespaceInBody() {
		byte[] der = ed25519.getPublic().getEncoded();
		String body = Base64.getEncoder().encodeToString(der);
		String pem = "-----BEGIN TEST-----\n" + body.substring(0, 10) + " \t\n\n" + body.substring(10) + "\n-----END TEST-----\n";
		
		assertArrayEquals(der, Pems.decode(pem).content());
	}
	
	@Test
	void decodeToleratesCarriageReturns() {
		String pem = Pems.encode(ed25519.getPublic()).replace("\n", "\r\n");
		PemDocument document = Pems.decode(pem);
		
		assertArrayEquals(ed25519.getPublic().getEncoded(), document.content());
		assertEquals(Pems.PUBLIC_KEY, document.label());
	}
	
	@Test
	void readFromFile() {
		Path file = DIRECTORY.resolve("read.pem");
		Pems.write(file, ed25519.getPublic());
		PemDocument document = Pems.read(file);
		
		assertEquals(Pems.PUBLIC_KEY, document.label());
		assertArrayEquals(ed25519.getPublic().getEncoded(), document.content());
	}
	
	@Test
	void writeCreatesReadableDocument() throws Exception {
		Path file = DIRECTORY.resolve("readable.pem");
		Pems.write(file, ed25519.getPublic());
		assertEquals(Pems.encode(ed25519.getPublic()), Files.readString(file, StandardCharsets.US_ASCII));
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
			PemDocument document = Pems.decode(Pems.encode(key));
			assertArrayEquals(key.getEncoded(), document.content());
			assertEquals(key instanceof PrivateKey ? Pems.PRIVATE_KEY : Pems.PUBLIC_KEY, document.label());
		}
	}
	
	@Test
	void encodeDecodeRoundTripForCertificate() throws Exception {
		assertArrayEquals(certificate.getEncoded(), Pems.decode(Pems.encode(certificate), Pems.CERTIFICATE).content());
	}
	
	@Test
	void roundTripThroughCryptoKeys() {
		KeyPair hybrid = Kems.generateKeyPair(KemAlgorithm.X25519_ML_KEM_768);
		PemDocument document = Pems.decode(Pems.encode(hybrid.getPublic()), Pems.PUBLIC_KEY);
		PublicKey restored = CryptoKeys.publicKey(KemAlgorithm.X25519_ML_KEM_768, document.content());
		
		assertEquals(hybrid.getPublic(), restored);
		try (Kems.Encapsulation sent = Kems.encapsulate(KemAlgorithm.X25519_ML_KEM_768, restored)) {
			assertEquals(KemAlgorithm.X25519_ML_KEM_768.encapsulationLength(), sent.encapsulation().length);
		}
	}
	
	@Test
	void decodeRejectsPrivateKeyWhereAPublicOneIsExpected() {
		assertThrows(MalformedDataException.class, () -> Pems.decode(Pems.encode(ed25519.getPrivate()), Pems.PUBLIC_KEY));
	}
	
	@Test
	void decodeAllReadsACertificateChain() throws Exception {
		X509Certificate second = selfSignedCertificate(Signatures.generateKeyPair(SignatureAlgorithm.ED25519));
		X509Certificate third = selfSignedCertificate(Signatures.generateKeyPair(SignatureAlgorithm.ED25519));
		String chain = Pems.encode(certificate) + "\n" + Pems.encode(second) + "\n" + Pems.encode(third);
		List<PemDocument> documents = Pems.decodeAll(chain);
		
		assertEquals(3, documents.size());
		assertArrayEquals(certificate.getEncoded(), documents.get(0).content());
		assertArrayEquals(second.getEncoded(), documents.get(1).content());
		assertArrayEquals(third.getEncoded(), documents.get(2).content());
	}
	
	@Test
	void encodeDecodeRoundTripAtLineBoundaries() {
		for (int size : new int[] { 0, 1, 47, 48, 49, 95, 96, 97, 1000 }) {
			byte[] der = CryptoRandom.bytes(size);
			assertArrayEquals(der, Pems.decode(Pems.encode("TEST", der)).content(), "size " + size);
		}
	}
	
	@Test
	void decodeIgnoresTrailingContentAfterLastDocument() {
		List<PemDocument> documents = assertDoesNotThrow(() -> Pems.decodeAll(Pems.encode(ed25519.getPublic()) + "trailing prose with no markers\n"));
		assertEquals(1, documents.size());
	}
	
	@Test
	void decodeWithNestedMarkersInBody() {
		String pem = "-----BEGIN TEST-----\nAAAA\n-----BEGIN INNER-----\n-----END TEST-----\n";
		PemDocument document = assertDoesNotThrow(() -> Pems.decode(pem));
		
		assertEquals("TEST", document.label());
		assertArrayEquals(Base64.getMimeDecoder().decode("AAAA-----BEGININNER-----"), document.content());
		assertEquals(1, Pems.decodeAll(pem).size());
	}
	
	@Test
	void writeThenReadPreservesKey() {
		Path publicFile = DIRECTORY.resolve("public.pem");
		Path privateFile = DIRECTORY.resolve("private.pem");
		Pems.write(publicFile, ed25519.getPublic());
		Pems.write(privateFile, ed25519.getPrivate());
		
		assertEquals(Pems.PUBLIC_KEY, Pems.read(publicFile).label());
		assertArrayEquals(ed25519.getPublic().getEncoded(), Pems.read(publicFile).content());
		assertEquals(Pems.PRIVATE_KEY, Pems.read(privateFile).label());
		assertArrayEquals(ed25519.getPrivate().getEncoded(), Pems.read(privateFile).content());
	}
	
	@Test
	void writeOverwritesExistingContent() throws Exception {
		Path file = DIRECTORY.resolve("overwrite.pem");
		KeyPair second = Signatures.generateKeyPair(SignatureAlgorithm.ED448);
		
		Pems.write(file, ed25519.getPublic());
		Pems.write(file, second.getPublic());
		assertArrayEquals(second.getPublic().getEncoded(), Pems.read(file).content());
		assertEquals(Pems.encode(second.getPublic()).length(), Files.readString(file, StandardCharsets.US_ASCII).length());
	}
	
	@Test
	void writtenFileIsAsciiOnly() throws Exception {
		Path file = DIRECTORY.resolve("ascii.pem");
		Pems.write(file, ed25519.getPublic());
		
		for (byte value : Files.readAllBytes(file)) {
			assertTrue((value & 0xFF) < 0x80);
		}
	}
	
	@Test
	void documentDoesNotAliasDecodedBytes() {
		String pem = Pems.encode(ed25519.getPublic());
		PemDocument first = Pems.decode(pem);
		
		Arrays.fill(first.content(), (byte) 0);
		assertArrayEquals(ed25519.getPublic().getEncoded(), Pems.decode(pem).content());
	}
	
	@Test
	void encodeDoesNotMutateInput() {
		byte[] der = CryptoRandom.bytes(100);
		byte[] copy = der.clone();
		Pems.encode("TEST", der);
		assertArrayEquals(copy, der);
	}
	
	@Test
	void largeCertificateChainRoundTrip() {
		StringBuilder builder = new StringBuilder();
		List<byte[]> bodies = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			byte[] der = CryptoRandom.bytes(2000);
			bodies.add(der);
			builder.append(Pems.encode(Pems.CERTIFICATE, der));
		}
		
		List<PemDocument> documents = Pems.decodeAll(builder.toString());
		assertEquals(20, documents.size());
		for (int i = 0; i < 20; i++) {
			assertArrayEquals(bodies.get(i), documents.get(i).content(), "document " + i);
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
			MalformedDataException exception = assertThrows(MalformedDataException.class, () -> Pems.decodeAll(input));
			assertTrue(exception.getMessage().contains("Malformed PEM document at index"), input);
			assertNotNull(exception.getCause(), input);
		}
	}
	
	@Test
	void writeResourceWithNullResource() {
		assertThrows(NullPointerException.class, () -> Pems.write((ResourceLocation) null, ed25519.getPublic()));
	}
	
	@Test
	void writeResourceWithNullKey() {
		ResourceLocation resource = ResourceLocation.external(DIRECTORY.resolve("null-key.pem").toString());
		assertThrows(NullPointerException.class, () -> Pems.write(resource, null));
	}
	
	@Test
	void writeResourceWithBothNull() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Pems.write((ResourceLocation) null, null));
		assertEquals("Resource must not be null", exception.getMessage());
	}
	
	@Test
	void writeInternalResource() {
		ResourceLocation resource = ResourceLocation.internal("Pems/document.pem");
		assertThrows(UnsupportedOperationException.class, () -> Pems.write(resource, ed25519.getPublic()));
	}
	
	@Test
	void readResourceWithNullResource() {
		assertThrows(NullPointerException.class, () -> Pems.read((ResourceLocation) null));
	}
	
	@Test
	void readMissingExternalResource() {
		ResourceLocation resource = ResourceLocation.external(DIRECTORY.resolve("missing.pem").toString());
		UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> Pems.read(resource));
		assertTrue(exception.getMessage().contains("missing.pem"));
	}
	
	@Test
	void readMissingInternalResource() {
		ResourceLocation resource = ResourceLocation.internal("Pems/does-not-exist.pem");
		assertThrows(NullPointerException.class, () -> Pems.read(resource));
	}
	
	@Test
	void readResourceWithoutAnyDocument() throws Exception {
		Path file = DIRECTORY.resolve("plain.txt");
		Files.writeString(file, "no document here", StandardCharsets.US_ASCII);
		assertThrows(MalformedDataException.class, () -> Pems.read(ResourceLocation.external(file.toString())));
	}
	
	@Test
	void readMalformedResource() {
		ResourceLocation resource = ResourceLocation.external(MALFORMED_FILE.toString());
		assertThrows(MalformedDataException.class, () -> Pems.read(resource));
	}
	
	@Test
	void readDirectoryAsResource() {
		ResourceLocation resource = ResourceLocation.external(DIRECTORY.toString());
		assertThrows(UncheckedIOException.class, () -> Pems.read(resource));
	}
	
	@Test
	void writeToExternalResource() throws Exception {
		Path file = DIRECTORY.resolve("written-resource.pem");
		Pems.write(ResourceLocation.external(file.toString()), ed25519.getPublic());
		
		assertTrue(Files.exists(file));
		assertEquals(Pems.encode(ed25519.getPublic()), Files.readString(file, StandardCharsets.US_ASCII));
	}
	
	@Test
	void readFromExternalResource() throws Exception {
		Path file = DIRECTORY.resolve("read-resource.pem");
		Files.writeString(file, Pems.encode(ed25519.getPublic()), StandardCharsets.US_ASCII);
		PemDocument document = Pems.read(ResourceLocation.external(file.toString()));
		
		assertEquals(Pems.PUBLIC_KEY, document.label());
		assertArrayEquals(ed25519.getPublic().getEncoded(), document.content());
	}
	
	@Test
	void readFromInternalResource() {
		PemDocument document = Pems.read(ResourceLocation.internal("Pems/document.pem"));
		
		assertEquals(Pems.PUBLIC_KEY, document.label());
		assertArrayEquals(FIXTURE_CONTENT, document.content());
	}
	
	@Test
	void writeResourceThenReadResource() {
		ResourceLocation resource = ResourceLocation.external(DIRECTORY.resolve("round-trip-resource.pem").toString());
		Pems.write(resource, ed25519.getPublic());
		
		assertArrayEquals(ed25519.getPublic().getEncoded(), Pems.read(resource).content());
	}
	
	@Test
	void writeResourceOverwritesExistingContent() {
		ResourceLocation resource = ResourceLocation.external(DIRECTORY.resolve("overwritten-resource.pem").toString());
		Pems.write(resource, ed25519.getPublic());
		Pems.write(resource, ed25519.getPrivate());
		PemDocument document = Pems.read(resource);
		
		assertEquals(Pems.PRIVATE_KEY, document.label());
		assertArrayEquals(ed25519.getPrivate().getEncoded(), document.content());
	}
	
	@Test
	void writeResourceCreatesOwnerOnlyFileOnPosix() throws Exception {
		assumeTrue(posix);
		Path file = DIRECTORY.resolve("owner-only-resource.pem");
		Pems.write(ResourceLocation.external(file.toString()), ed25519.getPrivate());
		
		assertEquals(PosixFilePermissions.fromString("rw-------"), Files.getPosixFilePermissions(file));
	}
	
	@Test
	void readResourceReturnsFirstDocument() throws Exception {
		Path file = DIRECTORY.resolve("two-documents-resource.pem");
		Files.writeString(file, Pems.encode(ed25519.getPublic()) + Pems.encode(ed25519.getPrivate()), StandardCharsets.US_ASCII);
		PemDocument document = Pems.read(ResourceLocation.external(file.toString()));
		
		assertEquals(Pems.PUBLIC_KEY, document.label());
		assertArrayEquals(ed25519.getPublic().getEncoded(), document.content());
	}
	
	@Test
	void readResourceMatchesReadFile() throws Exception {
		Path file = DIRECTORY.resolve("same-file-resource.pem");
		Files.writeString(file, Pems.encode(ed25519.getPublic()), StandardCharsets.US_ASCII);
		PemDocument fromResource = Pems.read(ResourceLocation.external(file.toString()));
		PemDocument fromFile = Pems.read(file);
		
		assertEquals(fromFile.label(), fromResource.label());
		assertArrayEquals(fromFile.content(), fromResource.content());
	}
	
	@Test
	void writeResourceThenReadCertificateChain() throws Exception {
		Path file = DIRECTORY.resolve("certificate-resource.pem");
		Files.writeString(file, Pems.encode(Pems.CERTIFICATE, certificate.getEncoded()), StandardCharsets.US_ASCII);
		PemDocument document = Pems.read(ResourceLocation.external(file.toString()));
		
		assertEquals(Pems.CERTIFICATE, document.label());
		CertificateFactory factory = CertificateFactory.getInstance("X.509");
		assertEquals(certificate, factory.generateCertificate(new ByteArrayInputStream(document.content())));
	}
	
	@Test
	void readResourceRoundTripsThroughCryptoKeys() throws Exception {
		KeyPair hybrid = Kems.generateKeyPair(KemAlgorithm.X25519_ML_KEM_768);
		Path file = DIRECTORY.resolve("hybrid-resource.pem");
		Files.writeString(file, Pems.encode(hybrid.getPublic()), StandardCharsets.US_ASCII);
		PemDocument document = Pems.read(ResourceLocation.external(file.toString()));
		
		assertEquals(hybrid.getPublic(), CryptoKeys.publicKey(KemAlgorithm.X25519_ML_KEM_768, document.content()));
	}
	
	@Test
	void decodeReturnsPemDocument() {
		String pem = Pems.encode(ed25519.getPublic());
		
		assertInstanceOf(PemDocument.class, Pems.decode(pem));
		assertInstanceOf(PemDocument.class, Pems.decode(pem, Pems.PUBLIC_KEY));
		List<PemDocument> documents = Pems.decodeAll(pem);
		assertEquals(1, documents.size());
		assertInstanceOf(PemDocument.class, documents.getFirst());
	}
	
	@Test
	void readFileReturnsPemDocument() throws Exception {
		Path file = DIRECTORY.resolve("typed-read.pem");
		Files.writeString(file, Pems.encode(ed25519.getPublic()), StandardCharsets.US_ASCII);
		assertInstanceOf(PemDocument.class, Pems.read(file));
	}
	
	@Test
	void encodeParameterRenameKeepsOutput() {
		String pem = Pems.encode(Pems.PUBLIC_KEY, FIXTURE_CONTENT);
		List<String> lines = pem.lines().toList();
		
		assertEquals("-----BEGIN PUBLIC KEY-----", lines.getFirst());
		assertEquals("-----END PUBLIC KEY-----", lines.getLast());
		assertTrue(lines.subList(1, lines.size() - 1).stream().allMatch(line -> line.length() <= 64));
		assertArrayEquals(FIXTURE_CONTENT, Pems.decode(pem).content());
	}
	
	@Test
	void readResourceToleratesTrailingContent() throws Exception {
		Path file = DIRECTORY.resolve("trailing-resource.pem");
		Files.writeString(file, Pems.encode(ed25519.getPublic()) + "trailing text\n", StandardCharsets.US_ASCII);
		PemDocument document = Pems.read(ResourceLocation.external(file.toString()));
		
		assertEquals(Pems.PUBLIC_KEY, document.label());
		assertArrayEquals(ed25519.getPublic().getEncoded(), document.content());
	}
	
	@Test
	void writeResourceRejectsKeyWithoutEncodedForm() {
		ResourceLocation resource = ResourceLocation.external(DIRECTORY.resolve("unencodable.pem").toString());
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Pems.write(resource, new UnencodableKey()));
		assertEquals("Content must not be null", exception.getMessage());
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
