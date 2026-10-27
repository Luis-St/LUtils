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
import net.luis.utils.crypto.exception.CryptoException;
import net.luis.utils.crypto.exception.MalformedDataException;
import net.luis.utils.crypto.key.Secret;
import net.luis.utils.crypto.util.CryptoBytes;
import net.luis.utils.crypto.util.CryptoRandom;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CryptoOutputStream}.<br>
 *
 * @author Luis-St
 */
class CryptoOutputStreamTest {
	
	private static final byte[] DATA = "the quick brown fox".getBytes(StandardCharsets.UTF_8);
	private static final Map<CryptoSuite, KeyPair> KEY_PAIRS = new LinkedHashMap<>();
	
	@BeforeAll
	static void setUp() {
		Providers.installBouncyCastle();
		for (CryptoSuite suite : CryptoSuite.values()) {
			KEY_PAIRS.put(suite, Kems.generateKeyPair(suite.kem()));
		}
	}
	
	private static KeyPair keyPair(CryptoSuite suite) {
		return KEY_PAIRS.get(suite);
	}
	
	private static PublicKey recipient() {
		return keyPair(CryptoSuite.current()).getPublic();
	}
	
	private static int headerLength(CryptoSuite suite) {
		return CryptoMessages.HEADER_LENGTH + suite.kem().encapsulationLength() + suite.aead().nonceLength() - Integer.BYTES;
	}
	
	private static byte[] sealed(CryptoSuite suite, byte[] payload) throws IOException {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		try (CryptoOutputStream sealed = new CryptoOutputStream(suite, keyPair(suite).getPublic(), target)) {
			sealed.write(payload);
		}
		return target.toByteArray();
	}
	
	private static List<Frame> frames(CryptoSuite suite, byte[] artifact) {
		List<Frame> frames = new ArrayList<>();
		int offset = headerLength(suite);
		while (offset < artifact.length) {
			int length = ByteBuffer.wrap(artifact, offset + 1, Integer.BYTES).getInt();
			frames.add(new Frame(artifact[offset], length, offset));
			offset += 1 + Integer.BYTES + length;
		}
		return frames;
	}
	
	private static byte[] opened(CryptoSuite suite, byte[] artifact) throws IOException {
		try (CryptoInputStream sealed = new CryptoInputStream(keyPair(suite).getPrivate(), new ByteArrayInputStream(artifact))) {
			return sealed.readAllBytes();
		}
	}
	
	private static Object field(CryptoOutputStream stream, String name) throws Exception {
		Field field = CryptoOutputStream.class.getDeclaredField(name);
		field.setAccessible(true);
		return field.get(stream);
	}
	
	@Test
	void constructWithRecipient() throws Exception {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		try (CryptoOutputStream sealed = new CryptoOutputStream(recipient(), target)) {
			assertEquals(CryptoSuite.current(), sealed.suite());
			assertNotEquals(0, target.size());
		}
	}
	
	@Test
	void constructWithSuite() throws Exception {
		for (CryptoSuite suite : CryptoSuite.values()) {
			ByteArrayOutputStream target = new ByteArrayOutputStream();
			try (CryptoOutputStream sealed = new CryptoOutputStream(suite, keyPair(suite).getPublic(), target)) {
				assertSame(suite, sealed.suite());
			}
		}
	}
	
	@Test
	void constructWithRandom() throws Exception {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		try (CryptoOutputStream sealed = new CryptoOutputStream(CryptoRandom.instance(), CryptoSuite.current(), recipient(), target)) {
			assertSame(CryptoSuite.current(), sealed.suite());
			assertEquals(headerLength(CryptoSuite.current()), target.size());
		}
	}
	
	@Test
	void constructWithNullRandom() {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		assertThrows(NullPointerException.class, () -> new CryptoOutputStream(null, CryptoSuite.current(), recipient(), target));
	}
	
	@Test
	void constructWithNullSuite() {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		assertThrows(NullPointerException.class, () -> new CryptoOutputStream(CryptoRandom.instance(), null, recipient(), target));
	}
	
	@Test
	void constructWithNullRecipient() {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		assertThrows(NullPointerException.class, () -> new CryptoOutputStream(CryptoRandom.instance(), CryptoSuite.current(), null, target));
	}
	
	@Test
	void constructWithNullOutputStream() {
		assertThrows(NullPointerException.class, () -> new CryptoOutputStream(CryptoRandom.instance(), CryptoSuite.current(), recipient(), null));
	}
	
	@Test
	void constructWithNullRecipientOnShortOverload() {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		assertThrows(NullPointerException.class, () -> new CryptoOutputStream(null, target));
		assertThrows(NullPointerException.class, () -> new CryptoOutputStream(CryptoSuite.current(), null, target));
	}
	
	@Test
	void constructWithNullOutputStreamOnShortOverload() {
		assertThrows(NullPointerException.class, () -> new CryptoOutputStream(recipient(), null));
		assertThrows(NullPointerException.class, () -> new CryptoOutputStream(CryptoSuite.current(), recipient(), null));
	}
	
	@Test
	void constructWithNullSuiteOnShortOverload() {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		assertThrows(NullPointerException.class, () -> new CryptoOutputStream(null, recipient(), target));
	}
	
	@Test
	void constructWithFailingOutputStream() {
		assertThrows(IOException.class, () -> new CryptoOutputStream(recipient(), new FailingOutputStream(0)));
	}
	
	@Test
	void constructWithForeignKey() {
		PublicKey foreign = Signatures.generateKeyPair(SignatureAlgorithm.ED25519).getPublic();
		
		assertThrows(ClassCastException.class, () -> new CryptoOutputStream(foreign, new ByteArrayOutputStream()));
		assertThrows(CryptoException.class, () -> new CryptoOutputStream(CryptoSuite.CLASSICAL_V1, foreign, new ByteArrayOutputStream()));
	}
	
	@Test
	void writeWithNullData() throws Exception {
		try (CryptoOutputStream sealed = new CryptoOutputStream(recipient(), new ByteArrayOutputStream())) {
			assertThrows(NullPointerException.class, () -> sealed.write(null, 0, 1));
		}
	}
	
	@Test
	void writeWithNegativeOffset() throws Exception {
		try (CryptoOutputStream sealed = new CryptoOutputStream(recipient(), new ByteArrayOutputStream())) {
			assertThrows(IndexOutOfBoundsException.class, () -> sealed.write(DATA, -1, 1));
		}
	}
	
	@Test
	void writeWithNegativeLength() throws Exception {
		try (CryptoOutputStream sealed = new CryptoOutputStream(recipient(), new ByteArrayOutputStream())) {
			assertThrows(IndexOutOfBoundsException.class, () -> sealed.write(DATA, 0, -1));
		}
	}
	
	@Test
	void writeBeyondArrayEnd() throws Exception {
		try (CryptoOutputStream sealed = new CryptoOutputStream(recipient(), new ByteArrayOutputStream())) {
			assertThrows(IndexOutOfBoundsException.class, () -> sealed.write(DATA, 1, DATA.length));
		}
	}
	
	@Test
	void writeAfterClose() throws Exception {
		CryptoOutputStream sealed = new CryptoOutputStream(recipient(), new ByteArrayOutputStream());
		sealed.close();
		
		IOException exception = assertThrows(IOException.class, () -> sealed.write(DATA));
		assertEquals("Stream is closed", exception.getMessage());
	}
	
	@Test
	void writeAfterFinish() throws Exception {
		try (CryptoOutputStream sealed = new CryptoOutputStream(recipient(), new ByteArrayOutputStream())) {
			sealed.finish();
			IOException exception = assertThrows(IOException.class, () -> sealed.write(DATA));
			assertTrue(exception.getMessage().contains("finished"));
		}
	}
	
	@Test
	void writeSingleByteAfterFinish() throws Exception {
		try (CryptoOutputStream sealed = new CryptoOutputStream(recipient(), new ByteArrayOutputStream())) {
			sealed.finish();
			assertThrows(IOException.class, () -> sealed.write(42));
		}
	}
	
	@Test
	void writeEmptyArrayAfterFinish() throws Exception {
		try (CryptoOutputStream sealed = new CryptoOutputStream(recipient(), new ByteArrayOutputStream())) {
			sealed.finish();
			assertThrows(IOException.class, () -> sealed.write(new byte[0], 0, 0));
		}
	}
	
	@Test
	void writeWithBadRangeAfterClose() throws Exception {
		CryptoOutputStream sealed = new CryptoOutputStream(recipient(), new ByteArrayOutputStream());
		sealed.close();
		
		assertThrows(IndexOutOfBoundsException.class, () -> sealed.write(new byte[4], 0, 8));
	}
	
	@Test
	void closeWithFailingTarget() throws Exception {
		FailingOutputStream target = new FailingOutputStream(Integer.MAX_VALUE);
		target.failOnClose = true;
		CryptoOutputStream sealed = new CryptoOutputStream(recipient(), target);
		
		assertThrows(IOException.class, sealed::close);
		assertDoesNotThrow(sealed::close);
	}
	
	@Test
	void finishWithFailingTarget() throws Exception {
		FailingOutputStream target = new FailingOutputStream(2);
		CryptoOutputStream sealed = new CryptoOutputStream(recipient(), target);
		
		assertThrows(IOException.class, sealed::finish);
		assertThrows(IOException.class, sealed::close);
		assertTrue(target.closed);
	}
	
	@Test
	void writeChunkCounterOverflow() throws Exception {
		try (CryptoOutputStream sealed = new CryptoOutputStream(recipient(), new ByteArrayOutputStream())) {
			Field counter = CryptoOutputStream.class.getDeclaredField("counter");
			counter.setAccessible(true);
			counter.setInt(sealed, Integer.MAX_VALUE);
			
			assertThrows(MalformedDataException.class, () -> sealed.write(new byte[CryptoMessages.CHUNK_SIZE]));
		}
	}
	
	@Test
	void writeShorterThanChunkWritesNoChunk() throws Exception {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		try (CryptoOutputStream sealed = new CryptoOutputStream(recipient(), target)) {
			sealed.write(DATA);
			assertEquals(headerLength(CryptoSuite.current()), target.size());
		}
		assertEquals(1, frames(CryptoSuite.current(), target.toByteArray()).size());
	}
	
	@Test
	void writeExactlyOneChunk() throws Exception {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		try (CryptoOutputStream sealed = new CryptoOutputStream(recipient(), target)) {
			sealed.write(new byte[CryptoMessages.CHUNK_SIZE]);
			List<Frame> written = frames(CryptoSuite.current(), target.toByteArray());
			
			assertEquals(1, written.size());
			assertEquals(CryptoMessages.CHUNK_MORE, written.getFirst().kind());
		}
	}
	
	@Test
	void writeAcrossSeveralChunks() throws Exception {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		try (CryptoOutputStream sealed = new CryptoOutputStream(recipient(), target)) {
			sealed.write(new byte[CryptoMessages.CHUNK_SIZE * 2 + 1]);
			assertEquals(2, frames(CryptoSuite.current(), target.toByteArray()).size());
		}
		
		List<Frame> written = frames(CryptoSuite.current(), target.toByteArray());
		assertEquals(3, written.size());
		assertEquals(CryptoMessages.CHUNK_FINAL, written.getLast().kind());
	}
	
	@Test
	void writeZeroLength() throws Exception {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		try (CryptoOutputStream sealed = new CryptoOutputStream(recipient(), target)) {
			sealed.write(new byte[0], 0, 0);
			assertEquals(headerLength(CryptoSuite.current()), target.size());
			assertDoesNotThrow(() -> sealed.write(DATA));
		}
	}
	
	@Test
	void writeSingleByte() throws Exception {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		try (CryptoOutputStream sealed = new CryptoOutputStream(recipient(), target)) {
			sealed.write(0xFF);
		}
		assertArrayEquals(new byte[] { (byte) 0xFF }, opened(CryptoSuite.current(), target.toByteArray()));
	}
	
	@Test
	void flushWithBufferedData() throws Exception {
		CountingOutputStream target = new CountingOutputStream();
		try (CryptoOutputStream sealed = new CryptoOutputStream(recipient(), target)) {
			sealed.write(DATA);
			sealed.flush();
			
			assertEquals(1, frames(CryptoSuite.current(), target.toByteArray()).size());
			assertEquals(1, target.flushes);
		}
	}
	
	@Test
	void flushWithEmptyBuffer() throws Exception {
		CountingOutputStream target = new CountingOutputStream();
		try (CryptoOutputStream sealed = new CryptoOutputStream(recipient(), target)) {
			sealed.flush();
			
			assertEquals(0, frames(CryptoSuite.current(), target.toByteArray()).size());
			assertEquals(1, target.flushes);
		}
	}
	
	@Test
	void flushAfterFinish() throws Exception {
		CountingOutputStream target = new CountingOutputStream();
		try (CryptoOutputStream sealed = new CryptoOutputStream(recipient(), target)) {
			sealed.finish();
			int size = target.size();
			int flushes = target.flushes;
			
			assertDoesNotThrow(sealed::flush);
			assertEquals(size, target.size());
			assertEquals(flushes, target.flushes);
		}
	}
	
	@Test
	void flushRepeatedly() throws Exception {
		CountingOutputStream target = new CountingOutputStream();
		try (CryptoOutputStream sealed = new CryptoOutputStream(recipient(), target)) {
			sealed.write(DATA);
			sealed.flush();
			sealed.flush();
			sealed.flush();
			
			assertEquals(1, frames(CryptoSuite.current(), target.toByteArray()).size());
			assertEquals(3, target.flushes);
		}
	}
	
	@Test
	void finishWritesFinalChunk() throws Exception {
		CountingOutputStream target = new CountingOutputStream();
		try (CryptoOutputStream sealed = new CryptoOutputStream(recipient(), target)) {
			sealed.write(DATA);
			sealed.finish();
			
			assertEquals(CryptoMessages.CHUNK_FINAL, frames(CryptoSuite.current(), target.toByteArray()).getLast().kind());
			assertFalse(target.closed);
		}
	}
	
	@Test
	void finishIsIdempotent() throws Exception {
		CountingOutputStream target = new CountingOutputStream();
		try (CryptoOutputStream sealed = new CryptoOutputStream(recipient(), target)) {
			sealed.finish();
			int size = target.size();
			sealed.finish();
			
			assertEquals(size, target.size());
		}
	}
	
	@Test
	void closeFinishesAndClosesTarget() throws Exception {
		CountingOutputStream target = new CountingOutputStream();
		CryptoOutputStream sealed = new CryptoOutputStream(recipient(), target);
		sealed.write(DATA);
		sealed.close();
		
		assertEquals(CryptoMessages.CHUNK_FINAL, frames(CryptoSuite.current(), target.toByteArray()).getLast().kind());
		assertTrue(target.closed);
	}
	
	@Test
	void closeAfterFinishStillClosesTarget() throws Exception {
		CountingOutputStream target = new CountingOutputStream();
		CryptoOutputStream sealed = new CryptoOutputStream(recipient(), target);
		sealed.finish();
		int frames = frames(CryptoSuite.current(), target.toByteArray()).size();
		sealed.close();
		
		assertEquals(frames, frames(CryptoSuite.current(), target.toByteArray()).size());
		assertTrue(target.closed);
	}
	
	@Test
	void closeIsIdempotent() throws Exception {
		CountingOutputStream target = new CountingOutputStream();
		CryptoOutputStream sealed = new CryptoOutputStream(recipient(), target);
		sealed.close();
		int size = target.size();
		sealed.close();
		
		assertEquals(size, target.size());
		assertEquals(1, target.closes);
	}
	
	@Test
	void flushAfterClose() throws Exception {
		CountingOutputStream target = new CountingOutputStream();
		CryptoOutputStream sealed = new CryptoOutputStream(recipient(), target);
		sealed.close();
		int size = target.size();
		
		assertDoesNotThrow(sealed::flush);
		assertEquals(size, target.size());
	}
	
	@Test
	void suiteReturnsConfiguredSuite() throws Exception {
		for (CryptoSuite suite : CryptoSuite.values()) {
			try (CryptoOutputStream sealed = new CryptoOutputStream(suite, keyPair(suite).getPublic(), new ByteArrayOutputStream())) {
				assertSame(suite, sealed.suite());
			}
		}
	}
	
	@Test
	void headerIsWrittenOnConstruction() throws Exception {
		for (CryptoSuite suite : CryptoSuite.values()) {
			ByteArrayOutputStream target = new ByteArrayOutputStream();
			try (CryptoOutputStream sealed = new CryptoOutputStream(suite, keyPair(suite).getPublic(), target)) {
				assertEquals(headerLength(suite), target.size());
				assertNotNull(sealed.suite());
			}
		}
	}
	
	@Test
	void headerCarriesStreamMagic() throws Exception {
		byte[] artifact = sealed(CryptoSuite.current(), DATA);
		byte[] head = Arrays.copyOf(artifact, CryptoMessages.HEADER_LENGTH);
		
		assertArrayEquals("LUCS".getBytes(StandardCharsets.US_ASCII), Arrays.copyOf(artifact, 4));
		assertEquals(CryptoMessages.VERSION, artifact[4]);
		assertEquals(CryptoSuite.current(), CryptoMessages.Head.parse(head, CryptoMessages.STREAM_MAGIC).suite());
	}
	
	@Test
	void headerIsNotAMessageMagic() throws Exception {
		byte[] head = Arrays.copyOf(sealed(CryptoSuite.current(), DATA), CryptoMessages.HEADER_LENGTH);
		assertThrows(MalformedDataException.class, () -> CryptoMessages.Head.parse(head));
	}
	
	@Test
	void noncePrefixLengthFollowsSuite() throws Exception {
		ByteArrayOutputStream narrow = new ByteArrayOutputStream();
		try (CryptoOutputStream sealed = new CryptoOutputStream(CryptoSuite.current(), recipient(), narrow)) {
			assertEquals(8, narrow.size() - CryptoMessages.HEADER_LENGTH - sealed.suite().kem().encapsulationLength());
		}
		
		CryptoSuite wide = new CryptoSuite((short) 999, "wide-nonce", AeadAlgorithm.XCHACHA20_POLY1305, KemAlgorithm.X25519, SignatureAlgorithm.ED25519, KdfAlgorithm.HKDF_SHA_256, HashAlgorithm.SHA_256, false);
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		try (CryptoOutputStream sealed = new CryptoOutputStream(wide, keyPair(CryptoSuite.CLASSICAL_V1).getPublic(), target)) {
			assertEquals(20, target.size() - CryptoMessages.HEADER_LENGTH - sealed.suite().kem().encapsulationLength());
		}
	}
	
	@Test
	void noncePrefixDiffersPerStream() throws Exception {
		byte[] first = sealed(CryptoSuite.current(), DATA);
		byte[] second = sealed(CryptoSuite.current(), DATA);
		int start = CryptoMessages.HEADER_LENGTH + CryptoSuite.current().kem().encapsulationLength();
		
		assertFalse(Arrays.equals(Arrays.copyOfRange(first, start, start + 8), Arrays.copyOfRange(second, start, start + 8)));
	}
	
	@Test
	void noncePrefixComesFromGivenRandom() throws Exception {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		try (CryptoOutputStream sealed = new CryptoOutputStream(new FixedRandom(), CryptoSuite.current(), recipient(), target)) {
			assertNotNull(sealed.suite());
		}
		
		byte[] artifact = target.toByteArray();
		int start = CryptoMessages.HEADER_LENGTH + CryptoSuite.current().kem().encapsulationLength();
		assertArrayEquals(new byte[] { 7, 7, 7, 7, 7, 7, 7, 7 }, Arrays.copyOfRange(artifact, start, start + 8));
	}
	
	@Test
	void chunkFrameLayout() throws Exception {
		byte[] artifact = sealed(CryptoSuite.current(), DATA);
		List<Frame> written = frames(CryptoSuite.current(), artifact);
		
		assertEquals(1, written.size());
		assertEquals(CryptoMessages.CHUNK_FINAL, written.getFirst().kind());
		assertEquals(DATA.length + CryptoSuite.current().aead().tagLength(), written.getFirst().length());
		assertEquals(artifact.length, written.getFirst().offset() + 1 + Integer.BYTES + written.getFirst().length());
	}
	
	@Test
	void emptyStreamWritesOnlyAFinalChunk() throws Exception {
		byte[] artifact = sealed(CryptoSuite.current(), new byte[0]);
		List<Frame> written = frames(CryptoSuite.current(), artifact);
		
		assertEquals(1, written.size());
		assertEquals(CryptoMessages.CHUNK_FINAL, written.getFirst().kind());
		assertEquals(CryptoSuite.current().aead().tagLength(), written.getFirst().length());
	}
	
	@Test
	void writeWithOffsetAndLength() throws Exception {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		try (CryptoOutputStream sealed = new CryptoOutputStream(recipient(), target)) {
			sealed.write(DATA, 4, 5);
		}
		assertArrayEquals(Arrays.copyOfRange(DATA, 4, 9), opened(CryptoSuite.current(), target.toByteArray()));
	}
	
	@Test
	void roundTripEmptyPayload() throws Exception {
		byte[] artifact = sealed(CryptoSuite.current(), new byte[0]);
		try (CryptoInputStream sealed = new CryptoInputStream(keyPair(CryptoSuite.current()).getPrivate(), new ByteArrayInputStream(artifact))) {
			assertEquals(-1, sealed.read());
		}
	}
	
	@Test
	void roundTripSmallPayload() throws Exception {
		byte[] payload = { 42 };
		assertArrayEquals(payload, opened(CryptoSuite.current(), sealed(CryptoSuite.current(), payload)));
	}
	
	@Test
	void roundTripExactChunkSize() throws Exception {
		byte[] payload = CryptoRandom.bytes(CryptoMessages.CHUNK_SIZE);
		assertArrayEquals(payload, opened(CryptoSuite.current(), sealed(CryptoSuite.current(), payload)));
	}
	
	@Test
	void roundTripJustOverChunkSize() throws Exception {
		byte[] payload = CryptoRandom.bytes(CryptoMessages.CHUNK_SIZE + 1);
		assertArrayEquals(payload, opened(CryptoSuite.current(), sealed(CryptoSuite.current(), payload)));
	}
	
	@Test
	void roundTripManyChunks() throws Exception {
		byte[] payload = CryptoRandom.bytes(CryptoMessages.CHUNK_SIZE * 3 + 17);
		assertArrayEquals(payload, opened(CryptoSuite.current(), sealed(CryptoSuite.current(), payload)));
	}
	
	@Test
	void roundTripForEverySuite() throws Exception {
		for (CryptoSuite suite : CryptoSuite.values()) {
			assertArrayEquals(DATA, opened(suite, sealed(suite, DATA)));
		}
	}
	
	@Test
	void roundTripWithByteAtATimeWrites() throws Exception {
		byte[] payload = CryptoRandom.bytes(CryptoMessages.CHUNK_SIZE + 5);
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		try (CryptoOutputStream sealed = new CryptoOutputStream(recipient(), target)) {
			for (byte value : payload) {
				sealed.write(value);
			}
		}
		assertArrayEquals(payload, opened(CryptoSuite.current(), target.toByteArray()));
	}
	
	@Test
	void roundTripWithMidPayloadFlushes() throws Exception {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		try (CryptoOutputStream sealed = new CryptoOutputStream(recipient(), target)) {
			sealed.write(DATA);
			sealed.flush();
			sealed.write(DATA);
			sealed.flush();
		}
		
		byte[] artifact = target.toByteArray();
		assertEquals(3, frames(CryptoSuite.current(), artifact).size());
		assertArrayEquals(CryptoBytes.concat(DATA, DATA), opened(CryptoSuite.current(), artifact));
	}
	
	@Test
	void finishedStreamIsEmbeddable() throws Exception {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		CryptoOutputStream sealed = new CryptoOutputStream(recipient(), target);
		sealed.write(DATA);
		sealed.finish();
		target.write(DATA);
		
		ByteArrayInputStream source = new ByteArrayInputStream(target.toByteArray());
		try (CryptoInputStream reader = new CryptoInputStream(keyPair(CryptoSuite.current()).getPrivate(), source)) {
			assertArrayEquals(DATA, reader.readAllBytes());
		}
		assertArrayEquals(DATA, source.readAllBytes());
	}
	
	@Test
	void twoSectionsOnOneTarget() throws Exception {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		CryptoOutputStream first = new CryptoOutputStream(recipient(), target);
		first.write(DATA);
		first.finish();
		CryptoOutputStream second = new CryptoOutputStream(recipient(), target);
		second.write(new byte[] { 1, 2, 3 });
		second.finish();
		
		ByteArrayInputStream source = new ByteArrayInputStream(target.toByteArray());
		assertArrayEquals(DATA, new CryptoInputStream(keyPair(CryptoSuite.current()).getPrivate(), source).readAllBytes());
		assertArrayEquals(new byte[] { 1, 2, 3 }, new CryptoInputStream(keyPair(CryptoSuite.current()).getPrivate(), source).readAllBytes());
	}
	
	@Test
	void roundTripThroughGzip() throws Exception {
		byte[] payload = "compress me".repeat(500).getBytes(StandardCharsets.UTF_8);
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		try (OutputStream out = new GZIPOutputStream(new CryptoOutputStream(recipient(), target))) {
			out.write(payload);
		}
		
		try (CryptoInputStream sealed = new CryptoInputStream(keyPair(CryptoSuite.current()).getPrivate(), new ByteArrayInputStream(target.toByteArray()));
			 GZIPInputStream in = new GZIPInputStream(sealed)) {
			assertArrayEquals(payload, in.readAllBytes());
		}
	}
	
	@Test
	void roundTripThroughDataStreams() throws Exception {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		try (CryptoOutputStream sealed = new CryptoOutputStream(recipient(), target); DataOutputStream out = new DataOutputStream(new BufferedOutputStream(sealed))) {
			out.writeInt(2);
			out.writeLong(1L);
			out.writeUTF("first");
			out.writeLong(2L);
			out.writeUTF("second");
		}
		
		try (CryptoInputStream sealed = new CryptoInputStream(keyPair(CryptoSuite.current()).getPrivate(), new ByteArrayInputStream(target.toByteArray()));
			 DataInputStream in = new DataInputStream(new BufferedInputStream(sealed))) {
			assertEquals(2, in.readInt());
			assertEquals(1L, in.readLong());
			assertEquals("first", in.readUTF());
			assertEquals(2L, in.readLong());
			assertEquals("second", in.readUTF());
		}
	}
	
	@Test
	void keyIsWipedOnClose() throws Exception {
		CryptoOutputStream sealed = new CryptoOutputStream(recipient(), new ByteArrayOutputStream());
		Secret key = (Secret) field(sealed, "key");
		assertNotEquals(0, key.material().length);
		
		sealed.close();
		assertThrows(IllegalStateException.class, key::material);
	}
	
	@Test
	void bufferIsWipedOnClose() throws Exception {
		CryptoOutputStream sealed = new CryptoOutputStream(recipient(), new ByteArrayOutputStream());
		sealed.write(DATA);
		byte[] buffer = (byte[]) field(sealed, "buffer");
		assertEquals(DATA[0], buffer[0]);
		
		sealed.close();
		assertArrayEquals(new byte[CryptoMessages.CHUNK_SIZE], buffer);
	}
	
	@Test
	void unfinishedStreamProducesUnreadableArtifact() throws Exception {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		CryptoOutputStream sealed = new CryptoOutputStream(recipient(), target);
		sealed.write(DATA);
		sealed.flush();
		
		byte[] artifact = target.toByteArray();
		assertThrows(MalformedDataException.class, () -> opened(CryptoSuite.current(), artifact));
	}
	
	@Test
	void twoStreamsToOneRecipientDiffer() throws Exception {
		byte[] first = sealed(CryptoSuite.current(), DATA);
		byte[] second = sealed(CryptoSuite.current(), DATA);
		
		assertEquals(first.length, second.length);
		assertFalse(Arrays.equals(first, second));
	}
	
	@Test
	void largePayloadStreamsWithBoundedMemory() throws Exception {
		byte[] payload = CryptoRandom.bytes(CryptoMessages.CHUNK_SIZE * 40 + 3);
		byte[] artifact = sealed(CryptoSuite.current(), payload);
		
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, payload), Hashes.hash(HashAlgorithm.SHA_256, opened(CryptoSuite.current(), artifact)));
	}
	
	private record Frame(byte kind, int length, int offset) {}
	
	private static final class FixedRandom extends SecureRandom {
		
		@Override
		public void nextBytes(byte[] bytes) {
			Arrays.fill(bytes, (byte) 7);
		}
	}
	
	private static final class CountingOutputStream extends ByteArrayOutputStream {
		
		private int flushes;
		private int closes;
		private boolean closed;
		
		@Override
		public void flush() {
			this.flushes++;
		}
		
		@Override
		public void close() {
			this.closes++;
			this.closed = true;
		}
	}
	
	private static final class FailingOutputStream extends OutputStream {
		
		private final int allowedWrites;
		private int writes;
		private boolean failOnClose;
		private boolean closed;
		
		private FailingOutputStream(int allowedWrites) {
			this.allowedWrites = allowedWrites;
		}
		
		@Override
		public void write(int b) throws IOException {
			this.write(new byte[] { (byte) b }, 0, 1);
		}
		
		@Override
		public void write(byte[] data, int offset, int length) throws IOException {
			if (this.writes++ >= this.allowedWrites) {
				throw new IOException("Write failed");
			}
		}
		
		@Override
		public void close() throws IOException {
			this.closed = true;
			if (this.failOnClose) {
				throw new IOException("Close failed");
			}
		}
	}
}
