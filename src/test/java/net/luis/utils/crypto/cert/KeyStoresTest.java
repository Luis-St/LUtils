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

import net.luis.utils.crypto.Providers;
import net.luis.utils.crypto.Signatures;
import net.luis.utils.crypto.algorithm.SignatureAlgorithm;
import net.luis.utils.crypto.exception.*;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.*;

import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.*;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link KeyStores}.<br>
 *
 * @author Luis-St
 */
class KeyStoresTest {
	
	private static final Path DIRECTORY = Path.of("KeyStoresTest-files");
	private static final char[] PASSWORD = "password".toCharArray();
	private static final char[] WRONG_PASSWORD = "wrong".toCharArray();
	
	private static KeyPair caPair;
	private static KeyPair leafPair;
	private static X509Certificate ca;
	private static X509Certificate leaf;
	private static X509Certificate unrelated;
	
	@BeforeAll
	static void setUp() throws Exception {
		Providers.installBouncyCastle();
		Files.createDirectories(DIRECTORY);
		Instant notBefore = Instant.now().minus(Duration.ofMinutes(5));
		caPair = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
		leafPair = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
		ca = Certificates.builder().subject(DistinguishedName.ofCommonName("Root")).selfSigned(caPair).validity(notBefore, Duration.ofDays(365)).ca(0).build(SignatureAlgorithm.ED25519);
		leaf = Certificates.builder().subject(DistinguishedName.ofCommonName("Leaf")).subjectKey(leafPair.getPublic()).issuer(ca, caPair.getPrivate()).validity(notBefore, Duration.ofDays(30)).build(SignatureAlgorithm.ED25519);
		KeyPair unrelatedPair = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
		unrelated = Certificates.builder().subject(DistinguishedName.ofCommonName("Unrelated")).selfSigned(unrelatedPair).validity(notBefore, Duration.ofDays(30)).build(SignatureAlgorithm.ED25519);
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
	
	private static KeyStore leafStore() {
		return KeyStores.pkcs12(leafPair.getPrivate(), List.of(leaf), PASSWORD);
	}
	
	@Test
	void constructorIsPrivate() throws Exception {
		Constructor<?>[] constructors = KeyStores.class.getDeclaredConstructors();
		assertEquals(1, constructors.length);
		assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
		assertTrue(Modifier.isFinal(KeyStores.class.getModifiers()));
		
		Constructor<KeyStores> constructor = KeyStores.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		assertNotNull(constructor.newInstance());
	}
	
	@Test
	void pkcs12WithNullKey() {
		assertEquals("Key must not be null", assertThrows(NullPointerException.class, () -> KeyStores.pkcs12(null, List.of(leaf), PASSWORD)).getMessage());
	}
	
	@Test
	void pkcs12WithNullChain() {
		assertEquals("Chain must not be null", assertThrows(NullPointerException.class, () -> KeyStores.pkcs12(leafPair.getPrivate(), null, PASSWORD)).getMessage());
	}
	
	@Test
	void pkcs12WithNullPassword() {
		assertEquals("Password must not be null", assertThrows(NullPointerException.class, () -> KeyStores.pkcs12(leafPair.getPrivate(), List.of(leaf), null)).getMessage());
	}
	
	@Test
	void pkcs12WithNullAlias() {
		assertEquals("Alias must not be null", assertThrows(NullPointerException.class, () -> KeyStores.pkcs12(null, leafPair.getPrivate(), List.of(leaf), PASSWORD)).getMessage());
	}
	
	@Test
	void pkcs12WithChainHoldingNull() {
		List<X509Certificate> chain = Arrays.asList(leaf, null);
		assertThrows(NullPointerException.class, () -> KeyStores.pkcs12("server", leafPair.getPrivate(), chain, PASSWORD));
	}
	
	@Test
	void pkcs12WithEmptyChain() {
		assertEquals("Chain must not be empty", assertThrows(IllegalArgumentException.class, () -> KeyStores.pkcs12("server", leafPair.getPrivate(), List.of(), PASSWORD)).getMessage());
	}
	
	@Test
	void pkcs12WithUnlinkedChain() {
		CryptoException exception = assertThrows(CryptoException.class, () -> KeyStores.pkcs12("server", leafPair.getPrivate(), List.of(leaf, unrelated), PASSWORD));
		assertInstanceOf(KeyStoreException.class, exception.getCause());
	}
	
	@Test
	void pkcs12WithNonPkcs8Key() {
		CryptoException exception = assertThrows(CryptoException.class, () -> KeyStores.pkcs12("server", new RawPrivateKey(), List.of(leaf), PASSWORD));
		assertEquals("Cannot store a Raw key", exception.getMessage());
		assertInstanceOf(KeyStoreException.class, exception.getCause());
	}
	
	@Test
	void loadWithNullFile() {
		assertEquals("File must not be null", assertThrows(NullPointerException.class, () -> KeyStores.load(null, PASSWORD)).getMessage());
	}
	
	@Test
	void loadWithNullPassword() {
		assertEquals("Password must not be null", assertThrows(NullPointerException.class, () -> KeyStores.load(DIRECTORY.resolve("missing.p12"), null)).getMessage());
	}
	
	@Test
	void loadWithWrongPassword() {
		Path file = DIRECTORY.resolve("wrong-password.p12");
		KeyStores.save(leafStore(), file, PASSWORD);
		assertThrows(AuthenticationException.class, () -> KeyStores.load(file, WRONG_PASSWORD));
	}
	
	@Test
	void loadNonKeyStoreFile() throws Exception {
		Path file = DIRECTORY.resolve("garbage.p12");
		Files.write(file, new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 });
		assertThrows(MalformedDataException.class, () -> KeyStores.load(file, PASSWORD));
	}
	
	@Test
	void loadTruncatedKeyStore() throws Exception {
		Path file = DIRECTORY.resolve("truncated.p12");
		KeyStores.save(leafStore(), file, PASSWORD);
		byte[] content = Files.readAllBytes(file);
		Files.write(file, Arrays.copyOf(content, content.length / 2));
		assertThrows(UncheckedIOException.class, () -> KeyStores.load(file, PASSWORD));
	}
	
	@Test
	void loadMissingFile() {
		assertThrows(IllegalArgumentException.class, () -> KeyStores.load(DIRECTORY.resolve("missing.p12"), PASSWORD));
	}
	
	@Test
	void saveWithNullStore() {
		assertEquals("Key store must not be null", assertThrows(NullPointerException.class, () -> KeyStores.save(null, DIRECTORY.resolve("null.p12"), PASSWORD)).getMessage());
	}
	
	@Test
	void saveWithNullFile() {
		KeyStore store = leafStore();
		assertEquals("File must not be null", assertThrows(NullPointerException.class, () -> KeyStores.save(store, null, PASSWORD)).getMessage());
	}
	
	@Test
	void saveWithNullPassword() {
		KeyStore store = leafStore();
		assertEquals("Password must not be null", assertThrows(NullPointerException.class, () -> KeyStores.save(store, DIRECTORY.resolve("null.p12"), null)).getMessage());
	}
	
	@Test
	void saveIntoMissingDirectory() {
		KeyStore store = leafStore();
		assertThrows(UncheckedIOException.class, () -> KeyStores.save(store, DIRECTORY.resolve("missing").resolve("store.p12"), PASSWORD));
	}
	
	@Test
	void saveUnloadedStore() throws Exception {
		KeyStore store = KeyStore.getInstance("PKCS12");
		Path file = DIRECTORY.resolve("unloaded.p12");
		assertThrows(CryptoException.class, () -> KeyStores.save(store, file, PASSWORD));
	}
	
	@Test
	void entryWithNullStore() {
		assertEquals("Key store must not be null", assertThrows(NullPointerException.class, () -> KeyStores.entry(null, KeyStores.DEFAULT_ALIAS, PASSWORD)).getMessage());
	}
	
	@Test
	void entryWithNullAlias() {
		KeyStore store = leafStore();
		assertEquals("Alias must not be null", assertThrows(NullPointerException.class, () -> KeyStores.entry(store, null, PASSWORD)).getMessage());
	}
	
	@Test
	void entryWithNullPassword() {
		KeyStore store = leafStore();
		assertEquals("Password must not be null", assertThrows(NullPointerException.class, () -> KeyStores.entry(store, KeyStores.DEFAULT_ALIAS, null)).getMessage());
	}
	
	@Test
	void entryWithWrongPassword() {
		KeyStore store = leafStore();
		AuthenticationException exception = assertThrows(AuthenticationException.class, () -> KeyStores.entry(store, KeyStores.DEFAULT_ALIAS, WRONG_PASSWORD));
		assertEquals("Cannot read entry key, the password is wrong", exception.getMessage());
	}
	
	@Test
	void entryOfUnloadedStore() throws Exception {
		KeyStore store = KeyStore.getInstance("PKCS12");
		CryptoException exception = assertThrows(CryptoException.class, () -> KeyStores.entry(store, KeyStores.DEFAULT_ALIAS, PASSWORD));
		assertEquals("Cannot read entry key", exception.getMessage());
	}
	
	@Test
	void pkcs12StoresUnderDefaultAlias() throws Exception {
		KeyStore store = leafStore();
		assertEquals("PKCS12", store.getType());
		assertEquals(List.of(KeyStores.DEFAULT_ALIAS), Collections.list(store.aliases()));
		assertTrue(store.isKeyEntry(KeyStores.DEFAULT_ALIAS));
	}
	
	@Test
	void pkcs12StoresUnderGivenAlias() throws Exception {
		KeyStore store = KeyStores.pkcs12("server", leafPair.getPrivate(), List.of(leaf), PASSWORD);
		assertEquals(List.of("server"), Collections.list(store.aliases()));
		assertArrayEquals(leafPair.getPrivate().getEncoded(), store.getKey("server", PASSWORD).getEncoded());
		assertArrayEquals(new X509Certificate[] { leaf }, store.getCertificateChain("server"));
	}
	
	@Test
	void saveCreatesOwnerOnlyFile() throws Exception {
		Path file = DIRECTORY.resolve("new.p12");
		Files.deleteIfExists(file);
		KeyStores.save(leafStore(), file, PASSWORD);
		assertTrue(Files.exists(file));
		assertEquals("rw-------", PosixFilePermissions.toString(Files.getPosixFilePermissions(file)));
	}
	
	@Test
	void saveOverwritesExistingFile() throws Exception {
		Path file = DIRECTORY.resolve("existing.p12");
		Files.write(file, new byte[] { 1, 2, 3 });
		Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("rw-r--r--"));
		
		KeyStores.save(leafStore(), file, PASSWORD);
		assertEquals("rw-------", PosixFilePermissions.toString(Files.getPosixFilePermissions(file)));
		assertTrue(KeyStores.load(file, PASSWORD).isKeyEntry(KeyStores.DEFAULT_ALIAS));
	}
	
	@Test
	void entryOfAbsentAlias() {
		assertEquals(Optional.empty(), KeyStores.entry(leafStore(), "absent", PASSWORD));
	}
	
	@Test
	void entryOfCertificateAlias() throws Exception {
		KeyStore store = leafStore();
		store.setCertificateEntry("ca", ca);
		assertEquals(Optional.empty(), KeyStores.entry(store, "ca", PASSWORD));
		assertTrue(store.isCertificateEntry("ca"));
	}
	
	@Test
	void entryOfPrivateKeyAlias() {
		Optional<KeyStore.PrivateKeyEntry> entry = KeyStores.entry(leafStore(), KeyStores.DEFAULT_ALIAS, PASSWORD);
		assertTrue(entry.isPresent());
		assertArrayEquals(leafPair.getPrivate().getEncoded(), entry.get().getPrivateKey().getEncoded());
		assertEquals(leaf, entry.get().getCertificate());
	}
	
	@Test
	void defaultAliasIsKey() {
		assertEquals("key", KeyStores.DEFAULT_ALIAS);
	}
	
	@Test
	void pkcs12DoesNotCheckKeyMatchesLeaf() {
		KeyStore store = KeyStores.pkcs12(caPair.getPrivate(), List.of(leaf), PASSWORD);
		Optional<KeyStore.PrivateKeyEntry> entry = KeyStores.entry(store, KeyStores.DEFAULT_ALIAS, PASSWORD);
		assertTrue(entry.isPresent());
		assertArrayEquals(caPair.getPrivate().getEncoded(), entry.get().getPrivateKey().getEncoded());
		assertEquals(leaf, entry.get().getCertificate());
	}
	
	@Test
	void pkcs12CopiesChain() throws Exception {
		List<X509Certificate> chain = new ArrayList<>(List.of(leaf));
		KeyStore store = KeyStores.pkcs12(leafPair.getPrivate(), chain, PASSWORD);
		chain.add(ca);
		assertEquals(1, store.getCertificateChain(KeyStores.DEFAULT_ALIAS).length);
	}
	
	@Test
	void saveAndLoadRoundTrip() {
		Path file = DIRECTORY.resolve("round-trip.p12");
		KeyStores.save(leafStore(), file, PASSWORD);
		
		KeyStore loaded = KeyStores.load(file, PASSWORD);
		assertEquals("PKCS12", loaded.getType());
		Optional<KeyStore.PrivateKeyEntry> entry = KeyStores.entry(loaded, KeyStores.DEFAULT_ALIAS, PASSWORD);
		assertTrue(entry.isPresent());
		assertArrayEquals(leafPair.getPrivate().getEncoded(), entry.get().getPrivateKey().getEncoded());
	}
	
	@Test
	void pkcs12WithTwoCertificateChain() throws Exception {
		KeyStore store = KeyStores.pkcs12("server", leafPair.getPrivate(), List.of(leaf, ca), PASSWORD);
		assertArrayEquals(new X509Certificate[] { leaf, ca }, store.getCertificateChain("server"));
		
		Optional<KeyStore.PrivateKeyEntry> entry = KeyStores.entry(store, "server", PASSWORD);
		assertTrue(entry.isPresent());
		assertArrayEquals(new X509Certificate[] { leaf, ca }, entry.get().getCertificateChain());
	}
	
	@Test
	void loadJksFile() throws Exception {
		KeyStore jks = KeyStore.getInstance("JKS");
		jks.load(null, null);
		jks.setCertificateEntry("ca", ca);
		Path file = DIRECTORY.resolve("store.jks");
		try (OutputStream output = Files.newOutputStream(file)) {
			jks.store(output, PASSWORD);
		}
		
		KeyStore loaded = KeyStores.load(file, PASSWORD);
		assertEquals("JKS", loaded.getType());
		assertEquals(ca, loaded.getCertificate("ca"));
		assertEquals(Optional.empty(), KeyStores.entry(loaded, "ca", PASSWORD));
	}
	
	@Test
	void saveLoadEntryWithEcdsaKey() {
		KeyPair pair = Signatures.generateKeyPair(SignatureAlgorithm.ECDSA_P256_SHA_256);
		X509Certificate certificate = Certificates.builder().subject(DistinguishedName.ofCommonName("Ecdsa")).selfSigned(pair).validity(Instant.now(), Duration.ofDays(1)).build(SignatureAlgorithm.ECDSA_P256_SHA_256);
		Path file = DIRECTORY.resolve("ecdsa.p12");
		KeyStores.save(KeyStores.pkcs12("ecdsa", pair.getPrivate(), List.of(certificate), PASSWORD), file, PASSWORD);
		
		Optional<KeyStore.PrivateKeyEntry> entry = KeyStores.entry(KeyStores.load(file, PASSWORD), "ecdsa", PASSWORD);
		assertTrue(entry.isPresent());
		assertArrayEquals(pair.getPrivate().getEncoded(), entry.get().getPrivateKey().getEncoded());
		assertEquals(certificate, entry.get().getCertificate());
	}
	
	private record RawPrivateKey() implements PrivateKey {
		
		@Override
		public @NonNull String getAlgorithm() {
			return "Raw";
		}
		
		@Override
		public @NonNull String getFormat() {
			return "RAW";
		}
		
		@Override
		public byte @NonNull [] getEncoded() {
			return new byte[] { 1, 2, 3 };
		}
	}
}
