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
import net.luis.utils.crypto.util.CryptoBytes;
import net.luis.utils.crypto.util.CryptoRandom;
import net.luis.utils.resources.ResourceLocation;
import net.luis.utils.util.UUIDs;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Hasher}.<br>
 *
 * @author Luis-St
 */
class HasherTest {
	
	private static final String EMPTY_SHA_256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
	private static final byte[] EMPTY_DIGEST = HexFormat.of().parseHex(EMPTY_SHA_256);
	private static final byte[] DATA = "the quick brown fox".getBytes(StandardCharsets.UTF_8);
	private static final UUID UUID_VALUE = UUID.fromString("11111111-2222-3333-4444-555555555555");
	
	private static final Path DIRECTORY = Path.of("HasherTest-files");
	private static final Path FILE = DIRECTORY.resolve("content.bin");
	private static final Path EMPTY_FILE = DIRECTORY.resolve("empty.bin");
	private static final Path LARGE_FILE = DIRECTORY.resolve("large.bin");
	
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
		Files.deleteIfExists(DIRECTORY);
	}
	
	private static Hasher hasher() {
		return Hasher.of(HashAlgorithm.SHA_256);
	}
	
	@Test
	void constructHasher() throws Exception {
		Hasher hasher = new Hasher(MessageDigest.getInstance("SHA-256"));
		assertArrayEquals(EMPTY_DIGEST, hasher.digest());
	}
	
	@Test
	void constructWithNullDigest() {
		assertThrows(NullPointerException.class, () -> new Hasher(null));
	}
	
	@Test
	void ofAlgorithm() {
		Hasher hasher = Hasher.of(HashAlgorithm.SHA_256);
		assertNotNull(hasher);
		assertEquals(32, hasher.digest().length);
	}
	
	@Test
	void ofWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> Hasher.of(null));
	}
	
	@Test
	void ofReturnsIndependentHashers() {
		Hasher first = hasher();
		Hasher second = hasher();
		
		assertNotSame(first, second);
		first.update(DATA);
		assertArrayEquals(EMPTY_DIGEST, second.digest());
	}
	
	@Test
	void updateWithNullByteArray() {
		assertThrows(NullPointerException.class, () -> hasher().update((byte[]) null));
	}
	
	@Test
	void updateSectionWithNullByteArray() {
		assertThrows(NullPointerException.class, () -> hasher().update(null, 0, 0));
	}
	
	@Test
	void updateSectionWithNegativeOffset() {
		assertThrows(IndexOutOfBoundsException.class, () -> hasher().update(new byte[4], -1, 2));
	}
	
	@Test
	void updateSectionWithNegativeLength() {
		assertThrows(IndexOutOfBoundsException.class, () -> hasher().update(new byte[4], 0, -1));
	}
	
	@Test
	void updateSectionBeyondArrayEnd() {
		assertThrows(IndexOutOfBoundsException.class, () -> hasher().update(new byte[4], 2, 3));
		assertThrows(IndexOutOfBoundsException.class, () -> hasher().update(new byte[4], 5, 0));
	}
	
	@Test
	void updateWithNullBuffer() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> hasher().update((ByteBuffer) null));
		assertEquals("Buffer must not be null", exception.getMessage());
	}
	
	@Test
	void updateWithNullString() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> hasher().update(null, StandardCharsets.UTF_8));
		assertEquals("Data must not be null", exception.getMessage());
	}
	
	@Test
	void updateWithNullCharset() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> hasher().update("x", null));
		assertEquals("Charset must not be null", exception.getMessage());
	}
	
	@Test
	void updateStringWithBothNull() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> hasher().update(null, null));
		assertEquals("Data must not be null", exception.getMessage());
	}
	
	@Test
	void updateWithNullUuid() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> hasher().update((UUID) null));
		assertEquals("UUID must not be null", exception.getMessage());
	}
	
	@Test
	void updateWithNullInputStream() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> hasher().update((InputStream) null));
		assertEquals("Input must not be null", exception.getMessage());
	}
	
	@Test
	void updateWithNullFile() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> hasher().update((Path) null));
		assertEquals("File must not be null", exception.getMessage());
	}
	
	@Test
	void updateWithMissingFile() {
		Path missing = DIRECTORY.resolve("does-not-exist.bin");
		UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> hasher().update(missing));
		
		assertTrue(exception.getMessage().contains(missing.toString()));
		assertInstanceOf(NoSuchFileException.class, exception.getCause());
	}
	
	@Test
	void updateWithDirectoryAsFile() {
		assertThrows(UncheckedIOException.class, () -> hasher().update(DIRECTORY));
	}
	
	@Test
	void updateWithFailingStream() {
		IOException failure = new IOException("broken");
		UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> hasher().update(new FailingStream(0, failure)));
		
		assertEquals("Failed to read the stream to hash", exception.getMessage());
		assertSame(failure, exception.getCause());
	}
	
	@Test
	void updateWithStreamFailingPartway() {
		Hasher hasher = hasher();
		assertThrows(UncheckedIOException.class, () -> hasher.update(new FailingStream(100, new IOException("broken"))));
		
		byte[] digest = assertDoesNotThrow(hasher::digest);
		assertEquals(32, digest.length);
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, new byte[100]), digest);
	}
	
	@Test
	void updateWithEmptyStream() {
		assertArrayEquals(EMPTY_DIGEST, hasher().update(new ByteArrayInputStream(new byte[0])).digest());
	}
	
	@Test
	void updateWithSingleReadStream() {
		byte[] content = CryptoRandom.bytes(10);
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, content), hasher().update(new ByteArrayInputStream(content)).digest());
	}
	
	@Test
	void updateWithMultiReadStream() {
		byte[] content = CryptoRandom.bytes(20000);
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, content), hasher().update(new ByteArrayInputStream(content)).digest());
	}
	
	@Test
	void updateStreamAtBufferBoundaries() {
		for (int size : new int[] { 8191, 8192, 8193 }) {
			byte[] content = CryptoRandom.bytes(size);
			assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, content), hasher().update(new ByteArrayInputStream(content)).digest());
		}
	}
	
	@Test
	void updateWithEmptyByteArray() {
		assertArrayEquals(EMPTY_DIGEST, hasher().update(new byte[0]).digest());
	}
	
	@Test
	void updateWithEmptySection() {
		assertArrayEquals(EMPTY_DIGEST, hasher().update(new byte[4], 0, 0).digest());
		assertArrayEquals(EMPTY_DIGEST, hasher().update(new byte[4], 4, 0).digest());
	}
	
	@Test
	void updateWithEmptyBuffer() {
		ByteBuffer buffer = ByteBuffer.wrap(new byte[4]);
		buffer.position(4);
		assertArrayEquals(EMPTY_DIGEST, hasher().update(buffer).digest());
	}
	
	@Test
	void updateWithEmptyString() {
		assertArrayEquals(EMPTY_DIGEST, hasher().update("", StandardCharsets.UTF_8).digest());
	}
	
	@Test
	void updateWithEmptyFile() {
		assertArrayEquals(EMPTY_DIGEST, hasher().update(EMPTY_FILE).digest());
	}
	
	@Test
	void updateSingleByte() {
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, new byte[] { 0x41 }), hasher().update((byte) 0x41).digest());
	}
	
	@Test
	void updateByteArray() {
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, DATA), hasher().update(DATA).digest());
	}
	
	@Test
	void updateSection() {
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, new byte[] { 2, 3, 4 }), hasher().update(new byte[] { 1, 2, 3, 4, 5 }, 1, 3).digest());
	}
	
	@Test
	void updateEntireArrayAsSection() {
		assertArrayEquals(hasher().update(DATA).digest(), hasher().update(DATA, 0, DATA.length).digest());
	}
	
	@Test
	void updateBuffer() {
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, DATA), hasher().update(ByteBuffer.wrap(DATA)).digest());
	}
	
	@Test
	void updateBufferConsumesRemaining() {
		byte[] content = CryptoRandom.bytes(10);
		ByteBuffer buffer = ByteBuffer.wrap(content);
		buffer.position(4);
		
		byte[] digest = hasher().update(buffer).digest();
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, Arrays.copyOfRange(content, 4, 10)), digest);
		assertEquals(0, buffer.remaining());
	}
	
	@Test
	void updateString() {
		assertArrayEquals(hasher().update("abc".getBytes(StandardCharsets.UTF_8)).digest(), hasher().update("abc", StandardCharsets.UTF_8).digest());
	}
	
	@Test
	void updateStringWithDifferentCharsets() {
		byte[] utf8 = hasher().update("äöü", StandardCharsets.UTF_8).digest();
		byte[] utf16 = hasher().update("äöü", StandardCharsets.UTF_16).digest();
		
		assertFalse(Arrays.equals(utf8, utf16));
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, "äöü".getBytes(StandardCharsets.UTF_8)), utf8);
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, "äöü".getBytes(StandardCharsets.UTF_16)), utf16);
	}
	
	@Test
	void updateLong() {
		assertArrayEquals(hasher().update(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 }).digest(), hasher().update(0x0102030405060708L).digest());
		assertArrayEquals(hasher().update(new byte[8]).digest(), hasher().update(0L).digest());
	}
	
	@Test
	void updateLongBoundaryValues() {
		for (long value : new long[] { Long.MAX_VALUE, Long.MIN_VALUE, -1L }) {
			assertArrayEquals(hasher().update(CryptoBytes.of(value)).digest(), hasher().update(value).digest());
		}
	}
	
	@Test
	void updateUuid() {
		assertArrayEquals(hasher().update(UUIDs.toBytes(UUID_VALUE)).digest(), hasher().update(UUID_VALUE).digest());
		assertEquals(16, UUIDs.toBytes(UUID_VALUE).length);
	}
	
	@Test
	void updateStream() {
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, DATA), hasher().update(new ByteArrayInputStream(DATA)).digest());
	}
	
	@Test
	void updateStreamDoesNotCloseIt() throws Exception {
		RecordingStream stream = new RecordingStream(DATA);
		byte[] digest = hasher().update(stream).digest();
		
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, DATA), digest);
		assertFalse(stream.closed);
		assertEquals(-1, stream.read());
	}
	
	@Test
	void updateFile() throws Exception {
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, Files.readAllBytes(FILE)), hasher().update(FILE).digest());
	}
	
	@Test
	void updateFileClosesTheStream() throws Exception {
		Path temporary = DIRECTORY.resolve("closed.bin");
		Files.write(temporary, DATA);
		
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, DATA), hasher().update(temporary).digest());
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, DATA), hasher().update(temporary).digest());
		assertTrue(Files.deleteIfExists(temporary));
	}
	
	@Test
	void digestHexIsLowercase() {
		String hex = hasher().update(DATA).digestHex();
		assertEquals(64, hex.length());
		assertTrue(hex.matches("[0-9a-f]{64}"));
		assertEquals(HexFormat.of().formatHex(hasher().update(DATA).digest()), hex);
	}
	
	@Test
	void digestOfEmptyHasher() {
		assertArrayEquals(EMPTY_DIGEST, hasher().digest());
	}
	
	@Test
	void digestForEveryAlgorithm() {
		for (HashAlgorithm algorithm : HashAlgorithm.values()) {
			assertEquals(algorithm.digestLength(), Hasher.of(algorithm).update(DATA).digest().length);
		}
	}
	
	@Test
	void updateReturnsThisForChaining() {
		Hasher hasher = hasher();
		
		assertSame(hasher, hasher.update((byte) 1));
		assertSame(hasher, hasher.update(DATA));
		assertSame(hasher, hasher.update(DATA, 0, 2));
		assertSame(hasher, hasher.update(ByteBuffer.wrap(DATA)));
		assertSame(hasher, hasher.update("x", StandardCharsets.UTF_8));
		assertSame(hasher, hasher.update(1L));
		assertSame(hasher, hasher.update(UUID_VALUE));
		assertSame(hasher, hasher.update(new ByteArrayInputStream(DATA)));
		assertSame(hasher, hasher.update(FILE));
	}
	
	@Test
	void chainedUpdatesMatchConcatenation() {
		byte[] first = CryptoRandom.bytes(5);
		byte[] second = CryptoRandom.bytes(7);
		byte[] third = CryptoRandom.bytes(11);
		
		byte[] chained = hasher().update(first).update(second).update(third).digest();
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, CryptoBytes.concat(first, second, third)), chained);
	}
	
	@Test
	void mixedUpdateTypesMatchManualEncoding() {
		byte[] chained = hasher().update(UUID_VALUE).update("payload", StandardCharsets.UTF_8).update(42L).update(DATA).digest();
		byte[] manual = CryptoBytes.concat(UUIDs.toBytes(UUID_VALUE), "payload".getBytes(StandardCharsets.UTF_8), CryptoBytes.of(42L), DATA);
		
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, manual), chained);
	}
	
	@Test
	void digestResetsHasher() {
		Hasher hasher = hasher().update(DATA);
		byte[] first = hasher.digest();
		byte[] second = hasher.digest();
		
		assertFalse(Arrays.equals(first, second));
		assertArrayEquals(EMPTY_DIGEST, second);
	}
	
	@Test
	void digestHexResetsHasher() {
		Hasher hasher = hasher().update(DATA);
		String first = hasher.digestHex();
		
		assertNotEquals(EMPTY_SHA_256, first);
		assertEquals(EMPTY_SHA_256, hasher.digestHex());
	}
	
	@Test
	void hasherReuseAfterDigest() {
		byte[] first = CryptoRandom.bytes(8);
		byte[] second = CryptoRandom.bytes(8);
		Hasher hasher = hasher();
		
		byte[] firstDigest = hasher.update(first).digest();
		byte[] secondDigest = hasher.update(second).digest();
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, second), secondDigest);
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, first), firstDigest);
	}
	
	@Test
	void resetDiscardsPendingUpdates() {
		byte[] first = CryptoRandom.bytes(8);
		byte[] second = CryptoRandom.bytes(8);
		Hasher hasher = hasher().update(first);
		
		hasher.reset();
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, second), hasher.update(second).digest());
	}
	
	@Test
	void resetOnFreshHasher() {
		Hasher hasher = hasher();
		assertDoesNotThrow(hasher::reset);
		assertArrayEquals(EMPTY_DIGEST, hasher.digest());
	}
	
	@Test
	void resetAfterDigest() {
		Hasher hasher = hasher().update(DATA);
		hasher.digest();
		
		assertDoesNotThrow(hasher::reset);
		assertArrayEquals(EMPTY_DIGEST, hasher.digest());
	}
	
	@Test
	void chainingConsistencyAcrossAlgorithms() {
		byte[] first = CryptoRandom.bytes(5);
		byte[] second = CryptoRandom.bytes(7);
		byte[] third = CryptoRandom.bytes(11);
		byte[] joined = CryptoBytes.concat(first, second, third);
		
		for (HashAlgorithm algorithm : HashAlgorithm.values()) {
			assertArrayEquals(Hashes.hash(algorithm, joined), Hasher.of(algorithm).update(first).update(second).update(third).digest());
		}
	}
	
	@Test
	void updateDoesNotMutateInputs() {
		byte[] data = DATA.clone();
		byte[] bufferContent = CryptoRandom.bytes(10);
		byte[] bufferCopy = bufferContent.clone();
		ByteBuffer buffer = ByteBuffer.wrap(bufferContent);
		String text = "abc";
		
		hasher().update(data).update(buffer).update(text, StandardCharsets.UTF_8).update(UUID_VALUE).digest();
		assertArrayEquals(DATA, data);
		assertArrayEquals(bufferCopy, bufferContent);
		assertEquals("abc", text);
		assertEquals(UUID.fromString("11111111-2222-3333-4444-555555555555"), UUID_VALUE);
	}
	
	@Test
	void allFileAndStreamPathsAgree() {
		byte[] fromArray = hasher().update(DATA).digest();
		byte[] fromStream = hasher().update(new ByteArrayInputStream(DATA)).digest();
		byte[] fromFile = hasher().update(FILE).digest();
		
		assertArrayEquals(fromArray, fromStream);
		assertArrayEquals(fromArray, fromFile);
	}
	
	@Test
	void knownAnswerThroughChain() {
		String hex = hasher().update("a", StandardCharsets.UTF_8).update("b", StandardCharsets.UTF_8).update("c", StandardCharsets.UTF_8).digestHex();
		assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", hex);
	}
	
	@Test
	void largeFileCrossesBufferBoundary() {
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, largeContent), hasher().update(LARGE_FILE).digest());
	}
	
	@Test
	void updateWithNullResource() {
		assertThrows(NullPointerException.class, () -> hasher().update((ResourceLocation) null));
	}
	
	@Test
	void updateWithMissingExternalResource() {
		ResourceLocation resource = ResourceLocation.external(DIRECTORY.resolve("missing.bin").toString());
		UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> hasher().update(resource));
		assertTrue(exception.getMessage().contains("missing.bin"));
	}
	
	@Test
	void updateWithMissingInternalResource() {
		ResourceLocation resource = ResourceLocation.internal("does/not/exist.bin");
		assertThrows(NullPointerException.class, () -> hasher().update(resource));
	}
	
	@Test
	void updateWithDirectoryAsResource() {
		ResourceLocation resource = ResourceLocation.external(DIRECTORY.toString());
		assertThrows(UncheckedIOException.class, () -> hasher().update(resource));
	}
	
	@Test
	void updateWithExternalResource() {
		Hasher hasher = hasher();
		ResourceLocation resource = ResourceLocation.external(FILE.toString());
		
		assertSame(hasher, hasher.update(resource));
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, DATA), hasher.digest());
	}
	
	@Test
	void updateWithInternalResource() throws Exception {
		ResourceLocation resource = ResourceLocation.internal("ResourceLocation/ResourceLocation.json");
		byte[] expected = Hashes.hash(HashAlgorithm.SHA_256, resource.getBytes());
		assertArrayEquals(expected, hasher().update(resource).digest());
	}
	
	@Test
	void updateWithEmptyResource() {
		ResourceLocation resource = ResourceLocation.external(EMPTY_FILE.toString());
		assertArrayEquals(EMPTY_DIGEST, hasher().update(resource).digest());
	}
	
	@Test
	void updateResourceReturnsSameHasher() {
		Hasher hasher = hasher();
		assertSame(hasher, hasher.update(ResourceLocation.external(FILE.toString())));
		assertSame(hasher, hasher.update(ResourceLocation.external(EMPTY_FILE.toString())));
	}
	
	@Test
	void updateResourceMatchesUpdateFile() {
		byte[] fromResource = hasher().update(ResourceLocation.external(FILE.toString())).digest();
		byte[] fromFile = hasher().update(FILE).digest();
		assertArrayEquals(fromFile, fromResource);
	}
	
	@Test
	void updateResourceMatchesUpdateBytes() {
		byte[] fromResource = hasher().update(ResourceLocation.external(FILE.toString())).digest();
		byte[] fromBytes = hasher().update(DATA).digest();
		assertArrayEquals(fromBytes, fromResource);
	}
	
	@Test
	void updateResourceCombinedWithOtherUpdates() {
		byte[] prefix = "prefix".getBytes(StandardCharsets.UTF_8);
		byte[] suffix = "suffix".getBytes(StandardCharsets.UTF_8);
		byte[] combined = hasher().update("prefix", StandardCharsets.UTF_8).update(ResourceLocation.external(FILE.toString())).update(suffix).digest();
		
		assertArrayEquals(hasher().update(CryptoBytes.concat(prefix, DATA, suffix)).digest(), combined);
	}
	
	@Test
	void updateResourceMultipleTimes() {
		ResourceLocation resource = ResourceLocation.external(FILE.toString());
		byte[] twice = hasher().update(resource).update(resource).digest();
		assertArrayEquals(hasher().update(CryptoBytes.concat(DATA, DATA)).digest(), twice);
	}
	
	@Test
	void updateLargeResourceCrossesBufferBoundaries() {
		ResourceLocation resource = ResourceLocation.external(LARGE_FILE.toString());
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, largeContent), hasher().update(resource).digest());
	}
	
	@Test
	void updateResourceAfterDigestReuse() {
		Hasher hasher = hasher();
		ResourceLocation resource = ResourceLocation.external(FILE.toString());
		
		byte[] first = hasher.update(resource).digest();
		byte[] second = hasher.update(resource).digest();
		assertArrayEquals(first, second);
	}
	
	@Test
	void updateResourceForEveryAlgorithm() throws Exception {
		ResourceLocation resource = ResourceLocation.internal("ResourceLocation/ResourceLocation.json");
		byte[] content = resource.getBytes();
		for (HashAlgorithm algorithm : HashAlgorithm.values()) {
			assertArrayEquals(Hashes.hash(algorithm, content), Hasher.of(algorithm).update(resource).digest());
		}
	}
	
	private static final class FailingStream extends InputStream {
		
		private final IOException failure;
		private int remaining;
		
		private FailingStream(int remaining, IOException failure) {
			this.remaining = remaining;
			this.failure = failure;
		}
		
		@Override
		public int read() throws IOException {
			byte[] single = new byte[1];
			return this.read(single, 0, 1) == -1 ? -1 : single[0] & 0xFF;
		}
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException {
			if (this.remaining <= 0) {
				throw this.failure;
			}
			
			int read = Math.min(this.remaining, length);
			Arrays.fill(buffer, offset, offset + read, (byte) 0);
			this.remaining -= read;
			return read;
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
