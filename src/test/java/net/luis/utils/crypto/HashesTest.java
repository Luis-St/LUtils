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

import net.luis.utils.crypto.algorithm.HashAlgorithm;
import net.luis.utils.crypto.util.CryptoRandom;
import net.luis.utils.resources.ResourceLocation;
import org.junit.jupiter.api.*;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Arrays;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Hashes}.<br>
 *
 * @author Luis-St
 */
class HashesTest {
	
	private static final String EMPTY_SHA_256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
	private static final byte[] DATA = "the quick brown fox".getBytes(StandardCharsets.UTF_8);
	
	private static final Path DIRECTORY = Path.of("HashesTest-files");
	private static final Path FILE = DIRECTORY.resolve("content.bin");
	private static final Path EMPTY_FILE = DIRECTORY.resolve("empty.bin");
	private static final Path LARGE_FILE = DIRECTORY.resolve("large.bin");
	private static final Path MUTABLE_FILE = DIRECTORY.resolve("mutable.bin");
	
	private static byte[] largeContent;
	
	@BeforeAll
	static void setUp() throws Exception {
		Files.createDirectories(DIRECTORY);
		Files.write(FILE, DATA);
		Files.write(EMPTY_FILE, new byte[0]);
		
		largeContent = CryptoRandom.bytes(100000);
		Files.write(LARGE_FILE, largeContent);
	}
	
	@AfterAll
	static void cleanUp() throws Exception {
		Files.deleteIfExists(FILE);
		Files.deleteIfExists(EMPTY_FILE);
		Files.deleteIfExists(LARGE_FILE);
		Files.deleteIfExists(MUTABLE_FILE);
		Files.deleteIfExists(DIRECTORY);
	}
	
	@Test
	void constructorIsPrivate() throws Exception {
		Constructor<?>[] constructors = Hashes.class.getDeclaredConstructors();
		assertEquals(1, constructors.length);
		assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
		assertTrue(Modifier.isFinal(Hashes.class.getModifiers()));
		
		Constructor<Hashes> constructor = Hashes.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		assertNotNull(constructor.newInstance());
	}
	
	@Test
	void hashBytesWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> Hashes.hash(null, new byte[0]));
	}
	
	@Test
	void hashBytesWithNullData() {
		assertThrows(NullPointerException.class, () -> Hashes.hash(HashAlgorithm.SHA_256, (byte[]) null));
	}
	
	@Test
	void hashBytesWithBothNull() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Hashes.hash(null, (byte[]) null));
		assertEquals("Algorithm must not be null", exception.getMessage());
	}
	
	@Test
	void hashStringWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> Hashes.hash(null, "x", StandardCharsets.UTF_8));
	}
	
	@Test
	void hashStringWithNullData() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Hashes.hash(HashAlgorithm.SHA_256, null, StandardCharsets.UTF_8));
		assertEquals("Data must not be null", exception.getMessage());
	}
	
	@Test
	void hashStringWithNullCharset() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Hashes.hash(HashAlgorithm.SHA_256, "x", null));
		assertEquals("Charset must not be null", exception.getMessage());
	}
	
	@Test
	void hashStringWithNullAlgorithmAndNullData() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Hashes.hash(null, null, StandardCharsets.UTF_8));
		assertEquals("Data must not be null", exception.getMessage());
	}
	
	@Test
	void hashStreamWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> Hashes.hash(null, new ByteArrayInputStream(DATA)));
	}
	
	@Test
	void hashStreamWithNullInput() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Hashes.hash(HashAlgorithm.SHA_256, (InputStream) null));
		assertEquals("Input stream must not be null", exception.getMessage());
	}
	
	@Test
	void hashFileWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> Hashes.hash(null, FILE));
	}
	
	@Test
	void hashFileWithNullFile() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Hashes.hash(HashAlgorithm.SHA_256, (Path) null));
		assertEquals("File must not be null", exception.getMessage());
	}
	
	@Test
	void hashHexWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> Hashes.hashHex(null, new byte[0]));
	}
	
	@Test
	void hashHexWithNullData() {
		assertThrows(NullPointerException.class, () -> Hashes.hashHex(HashAlgorithm.SHA_256, null));
	}
	
	@Test
	void matchesWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> Hashes.matches(null, new byte[0], new byte[0]));
	}
	
	@Test
	void matchesWithNullData() {
		assertThrows(NullPointerException.class, () -> Hashes.matches(HashAlgorithm.SHA_256, null, new byte[0]));
	}
	
	@Test
	void hasherWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> Hashes.hasher(null));
	}
	
	@Test
	void hashMissingFile() {
		Path missing = DIRECTORY.resolve("does-not-exist.bin");
		UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> Hashes.hash(HashAlgorithm.SHA_256, missing));
		
		assertTrue(exception.getMessage().contains(missing.toString()));
		assertInstanceOf(IOException.class, exception.getCause());
		assertInstanceOf(NoSuchFileException.class, exception.getCause());
	}
	
	@Test
	void hashDirectoryAsFile() {
		assertThrows(UncheckedIOException.class, () -> Hashes.hash(HashAlgorithm.SHA_256, DIRECTORY));
	}
	
	@Test
	void hashFailingStream() {
		IOException failure = new IOException("broken");
		UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> Hashes.hash(HashAlgorithm.SHA_256, new FailingStream(failure)));
		assertSame(failure, exception.getCause());
	}
	
	@Test
	void matchesWithNullExpected() {
		assertThrows(NullPointerException.class, () -> Hashes.matches(HashAlgorithm.SHA_256, new byte[0], null));
		assertThrows(NullPointerException.class, () -> Hashes.matches(HashAlgorithm.SHA_256, DATA, null));
	}
	
	@Test
	void matchesWithCorrectDigest() {
		assertTrue(Hashes.matches(HashAlgorithm.SHA_256, DATA, Hashes.hash(HashAlgorithm.SHA_256, DATA)));
		assertTrue(Hashes.matches(HashAlgorithm.SHA_256, new byte[0], Hashes.hash(HashAlgorithm.SHA_256, new byte[0])));
	}
	
	@Test
	void matchesWithWrongDigest() {
		byte[] digest = Hashes.hash(HashAlgorithm.SHA_256, DATA);
		byte[] flipped = digest.clone();
		flipped[0] ^= 1;
		
		assertFalse(Hashes.matches(HashAlgorithm.SHA_256, DATA, flipped));
		assertFalse(Hashes.matches(HashAlgorithm.SHA_256, DATA, Arrays.copyOf(digest, 16)));
		assertFalse(Hashes.matches(HashAlgorithm.SHA_256, DATA, new byte[0]));
	}
	
	@Test
	void hashBytes() {
		byte[] digest = Hashes.hash(HashAlgorithm.SHA_256, DATA);
		assertEquals(32, digest.length);
		assertArrayEquals(digest, Hashes.hash(HashAlgorithm.SHA_256, DATA));
		assertFalse(Arrays.equals(digest, Hashes.hash(HashAlgorithm.SHA_256, "other".getBytes(StandardCharsets.UTF_8))));
	}
	
	@Test
	void hashEmptyBytes() {
		byte[] digest = assertDoesNotThrow(() -> Hashes.hash(HashAlgorithm.SHA_256, new byte[0]));
		assertEquals(EMPTY_SHA_256, HexFormat.of().formatHex(digest));
	}
	
	@Test
	void hashBytesForEveryAlgorithm() {
		for (HashAlgorithm algorithm : HashAlgorithm.values()) {
			assertEquals(algorithm.digestLength(), Hashes.hash(algorithm, DATA).length);
		}
	}
	
	@Test
	void hashString() {
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, "abc".getBytes(StandardCharsets.UTF_8)), Hashes.hash(HashAlgorithm.SHA_256, "abc", StandardCharsets.UTF_8));
	}
	
	@Test
	void hashEmptyString() {
		assertEquals(EMPTY_SHA_256, HexFormat.of().formatHex(Hashes.hash(HashAlgorithm.SHA_256, "", StandardCharsets.UTF_8)));
	}
	
	@Test
	void hashStringWithDifferentCharsets() {
		byte[] utf8 = Hashes.hash(HashAlgorithm.SHA_256, "äöü", StandardCharsets.UTF_8);
		byte[] utf16 = Hashes.hash(HashAlgorithm.SHA_256, "äöü", StandardCharsets.UTF_16);
		
		assertFalse(Arrays.equals(utf8, utf16));
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, "äöü".getBytes(StandardCharsets.UTF_8)), utf8);
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, "äöü".getBytes(StandardCharsets.UTF_16)), utf16);
	}
	
	@Test
	void hashStream() {
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, DATA), Hashes.hash(HashAlgorithm.SHA_256, new ByteArrayInputStream(DATA)));
	}
	
	@Test
	void hashEmptyStream() {
		assertEquals(EMPTY_SHA_256, HexFormat.of().formatHex(Hashes.hash(HashAlgorithm.SHA_256, new ByteArrayInputStream(new byte[0]))));
	}
	
	@Test
	void hashStreamDoesNotCloseIt() throws Exception {
		RecordingStream stream = new RecordingStream(DATA);
		byte[] digest = Hashes.hash(HashAlgorithm.SHA_256, stream);
		
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, DATA), digest);
		assertFalse(stream.closed);
		assertEquals(-1, stream.read());
	}
	
	@Test
	void hashFile() throws Exception {
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, Files.readAllBytes(FILE)), Hashes.hash(HashAlgorithm.SHA_256, FILE));
	}
	
	@Test
	void hashEmptyFile() {
		assertEquals(EMPTY_SHA_256, HexFormat.of().formatHex(Hashes.hash(HashAlgorithm.SHA_256, EMPTY_FILE)));
	}
	
	@Test
	void hashHexIsLowercase() {
		String hex = Hashes.hashHex(HashAlgorithm.SHA_256, DATA);
		assertEquals(64, hex.length());
		assertTrue(hex.matches("[0-9a-f]{64}"));
		assertEquals(HexFormat.of().formatHex(Hashes.hash(HashAlgorithm.SHA_256, DATA)), hex);
	}
	
	@Test
	void hashHexForEveryAlgorithm() {
		for (HashAlgorithm algorithm : HashAlgorithm.values()) {
			assertEquals(2 * algorithm.digestLength(), Hashes.hashHex(algorithm, DATA).length());
		}
	}
	
	@Test
	void hasherReturnsFreshInstances() {
		Hasher first = Hashes.hasher(HashAlgorithm.SHA_256);
		assertNotNull(first);
		assertNotSame(first, Hashes.hasher(HashAlgorithm.SHA_256));
	}
	
	@Test
	void hashKnownAnswerVectors() {
		byte[] abc = "abc".getBytes(StandardCharsets.UTF_8);
		assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", Hashes.hashHex(HashAlgorithm.SHA_256, abc));
		assertEquals("a9993e364706816aba3e25717850c26c9cd0d89d", Hashes.hashHex(HashAlgorithm.SHA_1, abc));
		assertEquals("900150983cd24fb0d6963f7d28e17f72", Hashes.hashHex(HashAlgorithm.MD5, abc));
		assertEquals("3a985da74fe225b2045c172d6bd390bd855f086e3e9d525b46bfe24511431532", Hashes.hashHex(HashAlgorithm.SHA3_256, abc));
	}
	
	@Test
	void allOverloadsAgreeOnTheSameContent() throws Exception {
		byte[] fromArray = Hashes.hash(HashAlgorithm.SHA_256, DATA);
		byte[] fromString = Hashes.hash(HashAlgorithm.SHA_256, new String(DATA, StandardCharsets.UTF_8), StandardCharsets.UTF_8);
		byte[] fromStream = Hashes.hash(HashAlgorithm.SHA_256, new ByteArrayInputStream(DATA));
		byte[] fromFile = Hashes.hash(HashAlgorithm.SHA_256, FILE);
		
		assertArrayEquals(fromArray, fromString);
		assertArrayEquals(fromArray, fromStream);
		assertArrayEquals(fromArray, fromFile);
	}
	
	@Test
	void hashLargeStreamCrossesBufferBoundary() {
		for (int size : new int[] { 8191, 8192, 8193, 100000 }) {
			byte[] content = CryptoRandom.bytes(size);
			assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, content), Hashes.hash(HashAlgorithm.SHA_256, new ByteArrayInputStream(content)));
		}
	}
	
	@Test
	void hashLargeFile() {
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, largeContent), Hashes.hash(HashAlgorithm.SHA_256, LARGE_FILE));
	}
	
	@Test
	void hashDoesNotMutateInput() {
		byte[] data = DATA.clone();
		String text = "abc";
		
		Hashes.hash(HashAlgorithm.SHA_256, data);
		Hashes.hashHex(HashAlgorithm.SHA_256, data);
		Hashes.matches(HashAlgorithm.SHA_256, data, new byte[32]);
		Hashes.hash(HashAlgorithm.SHA_256, text, StandardCharsets.UTF_8);
		
		assertArrayEquals(DATA, data);
		assertEquals("abc", text);
	}
	
	@Test
	void hashReturnsFreshArrays() {
		byte[] first = Hashes.hash(HashAlgorithm.SHA_256, DATA);
		byte[] second = Hashes.hash(HashAlgorithm.SHA_256, DATA);
		
		assertNotSame(first, second);
		first[0] ^= 1;
		assertArrayEquals(second, Hashes.hash(HashAlgorithm.SHA_256, DATA));
	}
	
	@Test
	void matchesIsConsistentWithHash() {
		byte[] other = "other".getBytes(StandardCharsets.UTF_8);
		for (HashAlgorithm algorithm : HashAlgorithm.values()) {
			for (byte[] data : new byte[][] { new byte[0], new byte[] { 7 }, CryptoRandom.bytes(1000) }) {
				assertTrue(Hashes.matches(algorithm, data, Hashes.hash(algorithm, data)));
				assertFalse(Hashes.matches(algorithm, data, Hashes.hash(algorithm, other)));
			}
		}
	}
	
	@Test
	void matchesAcrossAlgorithmsIsFalse() {
		assertFalse(Hashes.matches(HashAlgorithm.SHA_256, DATA, Hashes.hash(HashAlgorithm.SHA3_256, DATA)));
		assertFalse(Hashes.matches(HashAlgorithm.SHA_256, DATA, Hashes.hash(HashAlgorithm.SHA_512_256, DATA)));
		assertEquals(HashAlgorithm.SHA_256.digestLength(), HashAlgorithm.SHA_512_256.digestLength());
	}
	
	@Test
	void hasherProducesSameDigestAsOneShot() {
		byte[] expected = Hashes.hash(HashAlgorithm.SHA_256, DATA);
		assertArrayEquals(expected, Hashes.hasher(HashAlgorithm.SHA_256).update(DATA).digest());
		
		byte[] split = Hashes.hasher(HashAlgorithm.SHA_256).update(DATA, 0, 4).update(DATA, 4, DATA.length - 4).digest();
		assertArrayEquals(expected, split);
	}
	
	@Test
	void hashResourceWithNullAlgorithm() {
		ResourceLocation resource = ResourceLocation.external(FILE.toString());
		assertThrows(NullPointerException.class, () -> Hashes.hash(null, resource));
	}
	
	@Test
	void hashResourceWithNullResource() {
		assertThrows(NullPointerException.class, () -> Hashes.hash(HashAlgorithm.SHA_256, (ResourceLocation) null));
	}
	
	@Test
	void hashResourceWithBothNull() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Hashes.hash(null, (ResourceLocation) null));
		assertEquals("Algorithm must not be null", exception.getMessage());
	}
	
	@Test
	void hashMissingExternalResource() {
		ResourceLocation resource = ResourceLocation.external(DIRECTORY.resolve("missing.bin").toString());
		assertThrows(UncheckedIOException.class, () -> Hashes.hash(HashAlgorithm.SHA_256, resource));
	}
	
	@Test
	void hashMissingInternalResource() {
		ResourceLocation resource = ResourceLocation.internal("does/not/exist.bin");
		assertThrows(NullPointerException.class, () -> Hashes.hash(HashAlgorithm.SHA_256, resource));
	}
	
	@Test
	void hashExternalResource() {
		byte[] digest = Hashes.hash(HashAlgorithm.SHA_256, ResourceLocation.external(FILE.toString()));
		assertEquals(32, digest.length);
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, DATA), digest);
	}
	
	@Test
	void hashInternalResource() throws Exception {
		ResourceLocation resource = ResourceLocation.internal("ResourceLocation/ResourceLocation.json");
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, resource.getBytes()), Hashes.hash(HashAlgorithm.SHA_256, resource));
	}
	
	@Test
	void hashEmptyResource() {
		byte[] digest = Hashes.hash(HashAlgorithm.SHA_256, ResourceLocation.external(EMPTY_FILE.toString()));
		assertEquals(EMPTY_SHA_256, HexFormat.of().formatHex(digest));
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, new byte[0]), digest);
	}
	
	@Test
	void hashResourceMatchesHashFile() {
		byte[] fromResource = Hashes.hash(HashAlgorithm.SHA_512, ResourceLocation.external(FILE.toString()));
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_512, FILE), fromResource);
	}
	
	@Test
	void hashResourceIsDeterministic() {
		ResourceLocation resource = ResourceLocation.external(FILE.toString());
		byte[] first = Hashes.hash(HashAlgorithm.SHA_256, resource);
		byte[] second = Hashes.hash(HashAlgorithm.SHA_256, resource);
		
		assertArrayEquals(first, second);
		assertNotSame(first, second);
	}
	
	@Test
	void hashResourceForEveryAlgorithm() {
		ResourceLocation resource = ResourceLocation.external(FILE.toString());
		for (HashAlgorithm algorithm : HashAlgorithm.values()) {
			byte[] digest = Hashes.hash(algorithm, resource);
			assertEquals(algorithm.digestLength(), digest.length);
			assertArrayEquals(Hashes.hash(algorithm, DATA), digest);
		}
	}
	
	@Test
	void hashResourceDetectsContentChange() throws Exception {
		Files.write(MUTABLE_FILE, DATA);
		ResourceLocation resource = ResourceLocation.external(MUTABLE_FILE.toString());
		byte[] before = Hashes.hash(HashAlgorithm.SHA_256, resource);
		
		byte[] changed = Arrays.copyOf(DATA, DATA.length);
		changed[0] ^= 0x01;
		Files.write(MUTABLE_FILE, changed);
		
		assertFalse(Arrays.equals(before, Hashes.hash(HashAlgorithm.SHA_256, resource)));
	}
	
	@Test
	void hashLargeResource() {
		ResourceLocation resource = ResourceLocation.external(LARGE_FILE.toString());
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, largeContent), Hashes.hash(HashAlgorithm.SHA_256, resource));
	}
	
	@Test
	void hashResourceMatchesHashHexOfBytes() throws Exception {
		ResourceLocation resource = ResourceLocation.internal("ResourceLocation/ResourceLocation.json");
		for (HashAlgorithm algorithm : HashAlgorithm.values()) {
			String hex = HexFormat.of().formatHex(Hashes.hash(algorithm, resource));
			assertEquals(Hashes.hashHex(algorithm, resource.getBytes()), hex);
		}
	}
	
	private static final class FailingStream extends InputStream {
		
		private final IOException failure;
		
		private FailingStream(IOException failure) {
			this.failure = failure;
		}
		
		@Override
		public int read() throws IOException {
			throw this.failure;
		}
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException {
			throw this.failure;
		}
	}
	
	private static final class RecordingStream extends InputStream {
		
		private final ByteArrayInputStream delegate;
		private boolean closed;
		
		private RecordingStream(byte[] content) {
			this.delegate = new ByteArrayInputStream(content);
		}
		
		@Override
		public int read() {
			return this.delegate.read();
		}
		
		@Override
		public int read(byte[] buffer, int offset, int length) {
			return this.delegate.read(buffer, offset, length);
		}
		
		@Override
		public void close() {
			this.closed = true;
		}
	}
}
