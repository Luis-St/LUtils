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
import net.luis.utils.crypto.algorithm.SignatureAlgorithm;
import net.luis.utils.crypto.exception.*;
import net.luis.utils.crypto.key.Secret;
import net.luis.utils.crypto.util.CryptoBytes;
import net.luis.utils.crypto.util.CryptoRandom;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CryptoInputStream}.<br>
 *
 * @author Luis-St
 */
class CryptoInputStreamTest {
	
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
	
	private static PrivateKey recipient() {
		return keyPair(CryptoSuite.current()).getPrivate();
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
	
	private static byte[] sealed(byte[] payload) throws IOException {
		return sealed(CryptoSuite.current(), payload);
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
	
	private static CryptoInputStream reader(byte[] artifact) throws IOException {
		return new CryptoInputStream(recipient(), new ByteArrayInputStream(artifact));
	}
	
	private static Object field(CryptoInputStream stream, String name) throws Exception {
		Field field = CryptoInputStream.class.getDeclaredField(name);
		field.setAccessible(true);
		return field.get(stream);
	}
	
	private static void counter(Class<?> type, Object stream, int value) throws Exception {
		Field counter = type.getDeclaredField("counter");
		counter.setAccessible(true);
		counter.setInt(stream, value);
	}
	
	private static void writeLength(byte[] artifact, int frameOffset, int length) {
		ByteBuffer.wrap(artifact, frameOffset + 1, Integer.BYTES).putInt(length);
	}
	
	@Test
	void constructOverSealedStream() throws Exception {
		try (CryptoInputStream sealed = reader(sealed(DATA))) {
			assertEquals(CryptoSuite.current(), sealed.suite());
		}
	}
	
	@Test
	void constructWithNullRecipient() {
		assertThrows(NullPointerException.class, () -> new CryptoInputStream(null, new ByteArrayInputStream(new byte[0])));
	}
	
	@Test
	void constructWithNullInputStream() {
		assertThrows(NullPointerException.class, () -> new CryptoInputStream(recipient(), null));
	}
	
	@Test
	void constructWithEmptySource() {
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> reader(new byte[0]));
		assertTrue(exception.getMessage().contains("too short"));
	}
	
	@Test
	void constructWithShortHeader() throws Exception {
		byte[] artifact = Arrays.copyOf(sealed(DATA), CryptoMessages.HEADER_LENGTH - 1);
		assertThrows(MalformedDataException.class, () -> reader(artifact));
	}
	
	@Test
	void constructWithMessageMagic() {
		byte[] message = CryptoMessages.seal(keyPair(CryptoSuite.current()).getPublic(), DATA);
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> reader(message));
		assertTrue(exception.getMessage().contains("magic"));
	}
	
	@Test
	void constructWithUnknownVersion() throws Exception {
		byte[] artifact = sealed(DATA);
		artifact[4] = 2;
		
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> reader(artifact));
		assertTrue(exception.getMessage().contains("2"));
	}
	
	@Test
	void constructWithUnknownSuiteId() throws Exception {
		byte[] artifact = sealed(DATA);
		artifact[5] = 0x7F;
		artifact[6] = (byte) 0xFF;
		
		assertThrows(MalformedDataException.class, () -> reader(artifact));
	}
	
	@Test
	void constructWithTruncatedEncapsulation() throws Exception {
		byte[] artifact = Arrays.copyOf(sealed(DATA), CryptoMessages.HEADER_LENGTH + 4);
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> reader(artifact));
		assertEquals("Truncated sealed stream header", exception.getMessage());
	}
	
	@Test
	void constructWithTruncatedNoncePrefix() throws Exception {
		byte[] artifact = Arrays.copyOf(sealed(DATA), headerLength(CryptoSuite.current()) - 1);
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> reader(artifact));
		assertEquals("Truncated sealed stream header", exception.getMessage());
	}
	
	@Test
	void constructWithWrongRecipientKey() throws Exception {
		byte[] artifact = sealed(DATA);
		KeyPair other = Kems.generateKeyPair(CryptoSuite.current().kem());
		
		AuthenticationException exception = assertThrows(AuthenticationException.class, () -> new CryptoInputStream(other.getPrivate(), new ByteArrayInputStream(artifact)));
		assertTrue(exception.getMessage().contains("commitment"));
	}
	
	@Test
	void constructWithForeignKeyType() throws Exception {
		byte[] artifact = sealed(CryptoSuite.CLASSICAL_V1, DATA);
		PrivateKey foreign = Signatures.generateKeyPair(SignatureAlgorithm.ED25519).getPrivate();
		
		assertThrows(CryptoException.class, () -> new CryptoInputStream(foreign, new ByteArrayInputStream(artifact)));
	}
	
	@Test
	void constructWithFailingSource() {
		assertThrows(IOException.class, () -> new CryptoInputStream(recipient(), new FailingInputStream(new byte[0], 0)));
	}
	
	@Test
	void readWithNullTarget() throws Exception {
		try (CryptoInputStream sealed = reader(sealed(DATA))) {
			assertThrows(NullPointerException.class, () -> sealed.read(null, 0, 1));
		}
	}
	
	@Test
	void readWithNegativeOffset() throws Exception {
		try (CryptoInputStream sealed = reader(sealed(DATA))) {
			assertThrows(IndexOutOfBoundsException.class, () -> sealed.read(new byte[8], -1, 1));
		}
	}
	
	@Test
	void readWithNegativeLength() throws Exception {
		try (CryptoInputStream sealed = reader(sealed(DATA))) {
			assertThrows(IndexOutOfBoundsException.class, () -> sealed.read(new byte[8], 0, -1));
		}
	}
	
	@Test
	void readBeyondArrayEnd() throws Exception {
		try (CryptoInputStream sealed = reader(sealed(DATA))) {
			assertThrows(IndexOutOfBoundsException.class, () -> sealed.read(new byte[8], 1, 8));
		}
	}
	
	@Test
	void readAfterClose() throws Exception {
		CryptoInputStream sealed = reader(sealed(DATA));
		sealed.close();
		
		IOException exception = assertThrows(IOException.class, () -> sealed.read(new byte[8], 0, 8));
		assertEquals("Stream is closed", exception.getMessage());
	}
	
	@Test
	void readZeroLengthAfterClose() throws Exception {
		CryptoInputStream sealed = reader(sealed(DATA));
		sealed.close();
		
		assertThrows(IOException.class, () -> sealed.read(new byte[8], 0, 0));
	}
	
	@Test
	void readSingleByteAfterClose() throws Exception {
		CryptoInputStream sealed = reader(sealed(DATA));
		sealed.close();
		
		assertThrows(IOException.class, sealed::read);
	}
	
	@Test
	void readWithBadRangeAfterClose() throws Exception {
		CryptoInputStream sealed = reader(sealed(DATA));
		sealed.close();
		
		assertThrows(IndexOutOfBoundsException.class, () -> sealed.read(new byte[4], 0, 8));
	}
	
	@Test
	void readTruncatedAfterHeader() throws Exception {
		byte[] artifact = Arrays.copyOf(sealed(DATA), headerLength(CryptoSuite.current()));
		try (CryptoInputStream sealed = reader(artifact)) {
			MalformedDataException exception = assertThrows(MalformedDataException.class, sealed::read);
			assertTrue(exception.getMessage().contains("truncated"));
		}
	}
	
	@Test
	void readTruncatedChunkHeader() throws Exception {
		byte[] artifact = Arrays.copyOf(sealed(DATA), headerLength(CryptoSuite.current()) + 3);
		try (CryptoInputStream sealed = reader(artifact)) {
			MalformedDataException exception = assertThrows(MalformedDataException.class, sealed::read);
			assertEquals("Truncated chunk header", exception.getMessage());
		}
	}
	
	@Test
	void readWithUnknownChunkKind() throws Exception {
		byte[] artifact = sealed(DATA);
		artifact[frames(CryptoSuite.current(), artifact).getFirst().offset()] = 0x7F;
		
		try (CryptoInputStream sealed = reader(artifact)) {
			MalformedDataException exception = assertThrows(MalformedDataException.class, sealed::read);
			assertTrue(exception.getMessage().contains("7F"));
		}
	}
	
	@Test
	void readWithChunkLengthBelowTagLength() throws Exception {
		byte[] artifact = sealed(DATA);
		writeLength(artifact, frames(CryptoSuite.current(), artifact).getFirst().offset(), CryptoSuite.current().aead().tagLength() - 1);
		
		try (CryptoInputStream sealed = reader(artifact)) {
			MalformedDataException exception = assertThrows(MalformedDataException.class, sealed::read);
			assertTrue(exception.getMessage().contains("Implausible chunk length"));
		}
	}
	
	@Test
	void readWithChunkLengthAboveMaximum() throws Exception {
		byte[] artifact = sealed(DATA);
		writeLength(artifact, frames(CryptoSuite.current(), artifact).getFirst().offset(), CryptoMessages.CHUNK_SIZE + CryptoSuite.current().aead().tagLength() + 1);
		
		try (CryptoInputStream sealed = reader(artifact)) {
			assertThrows(MalformedDataException.class, sealed::read);
		}
	}
	
	@Test
	void readWithNegativeChunkLength() throws Exception {
		byte[] artifact = sealed(DATA);
		writeLength(artifact, frames(CryptoSuite.current(), artifact).getFirst().offset(), -1);
		
		try (CryptoInputStream sealed = reader(artifact)) {
			assertThrows(MalformedDataException.class, sealed::read);
		}
	}
	
	@Test
	void readWithTruncatedChunkBody() throws Exception {
		byte[] artifact = Arrays.copyOf(sealed(DATA), headerLength(CryptoSuite.current()) + 5 + 2);
		try (CryptoInputStream sealed = reader(artifact)) {
			MalformedDataException exception = assertThrows(MalformedDataException.class, sealed::read);
			assertEquals("Truncated chunk body", exception.getMessage());
		}
	}
	
	@Test
	void readWithTamperedCiphertext() throws Exception {
		byte[] artifact = sealed(DATA);
		artifact[frames(CryptoSuite.current(), artifact).getFirst().offset() + 5] ^= 0x01;
		
		try (CryptoInputStream sealed = reader(artifact)) {
			assertThrows(AuthenticationException.class, sealed::read);
		}
	}
	
	@Test
	void readWithFlippedKindByte() throws Exception {
		byte[] artifact = sealed(CryptoRandom.bytes(CryptoMessages.CHUNK_SIZE + 10));
		artifact[frames(CryptoSuite.current(), artifact).getFirst().offset()] = CryptoMessages.CHUNK_FINAL;
		
		try (CryptoInputStream sealed = reader(artifact)) {
			assertThrows(AuthenticationException.class, sealed::readAllBytes);
		}
	}
	
	@Test
	void readWithReorderedChunks() throws Exception {
		byte[] artifact = sealed(CryptoRandom.bytes(CryptoMessages.CHUNK_SIZE * 2));
		List<Frame> written = frames(CryptoSuite.current(), artifact);
		Frame first = written.get(0);
		Frame second = written.get(1);
		assertEquals(first.length(), second.length());
		
		byte[] swapped = artifact.clone();
		int size = 1 + Integer.BYTES + first.length();
		System.arraycopy(artifact, second.offset(), swapped, first.offset(), size);
		System.arraycopy(artifact, first.offset(), swapped, second.offset(), size);
		try (CryptoInputStream sealed = reader(swapped)) {
			assertThrows(AuthenticationException.class, sealed::readAllBytes);
		}
	}
	
	@Test
	void readWithDroppedFinalChunk() throws Exception {
		byte[] artifact = sealed(CryptoRandom.bytes(CryptoMessages.CHUNK_SIZE + 10));
		byte[] truncated = Arrays.copyOf(artifact, frames(CryptoSuite.current(), artifact).getLast().offset());
		
		try (CryptoInputStream sealed = reader(truncated)) {
			assertEquals(CryptoMessages.CHUNK_SIZE, sealed.read(new byte[CryptoMessages.CHUNK_SIZE], 0, CryptoMessages.CHUNK_SIZE));
			assertThrows(MalformedDataException.class, sealed::read);
		}
	}
	
	@Test
	void readWithTamperedHeaderBinding() throws Exception {
		byte[] artifact = sealed(DATA);
		artifact[CryptoMessages.HEADER_LENGTH] ^= 0x01;
		
		assertThrows(CryptoException.class, () -> {
			try (CryptoInputStream sealed = reader(artifact)) {
				sealed.readAllBytes();
			}
		});
	}
	
	@Test
	void readChunkCounterOverflow() throws Exception {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		CryptoOutputStream writer = new CryptoOutputStream(keyPair(CryptoSuite.current()).getPublic(), target);
		counter(CryptoOutputStream.class, writer, Integer.MAX_VALUE);
		assertThrows(MalformedDataException.class, () -> writer.write(new byte[CryptoMessages.CHUNK_SIZE]));
		
		try (CryptoInputStream sealed = reader(target.toByteArray())) {
			counter(CryptoInputStream.class, sealed, Integer.MAX_VALUE);
			MalformedDataException exception = assertThrows(MalformedDataException.class, sealed::readAllBytes);
			assertTrue(exception.getMessage().contains("maximum number of chunks"));
		}
	}
	
	@Test
	void closeWithFailingSource() throws Exception {
		byte[] artifact = sealed(DATA);
		FailingInputStream source = new FailingInputStream(artifact, artifact.length);
		source.failOnClose = true;
		CryptoInputStream sealed = new CryptoInputStream(recipient(), source);
		
		assertThrows(IOException.class, sealed::close);
		assertThrows(IOException.class, () -> sealed.read(new byte[1], 0, 1));
	}
	
	@Test
	void readWithFailingSourceMidPayload() throws Exception {
		byte[] artifact = sealed(CryptoRandom.bytes(CryptoMessages.CHUNK_SIZE + 10));
		int firstFrameEnd = frames(CryptoSuite.current(), artifact).getLast().offset();
		try (CryptoInputStream sealed = new CryptoInputStream(recipient(), new FailingInputStream(artifact, firstFrameEnd))) {
			assertEquals(CryptoMessages.CHUNK_SIZE, sealed.read(new byte[CryptoMessages.CHUNK_SIZE], 0, CryptoMessages.CHUNK_SIZE));
			
			IOException exception = assertThrows(IOException.class, sealed::read);
			assertEquals("Read failed", exception.getMessage());
		}
	}
	
	@Test
	void readReturnsMinusOneAtEndOfPayload() throws Exception {
		try (CryptoInputStream sealed = reader(sealed(DATA))) {
			assertArrayEquals(DATA, sealed.readNBytes(DATA.length));
			assertEquals(-1, sealed.read());
			assertEquals(-1, sealed.read());
		}
	}
	
	@Test
	void readEmptyPayload() throws Exception {
		try (CryptoInputStream sealed = reader(sealed(new byte[0]))) {
			assertEquals(-1, sealed.read());
		}
	}
	
	@Test
	void readZeroLength() throws Exception {
		try (CryptoInputStream sealed = reader(sealed(DATA))) {
			assertEquals(0, sealed.read(new byte[8], 0, 0));
			assertEquals(0, sealed.available());
		}
	}
	
	@Test
	void readSingleByte() throws Exception {
		try (CryptoInputStream sealed = reader(sealed(new byte[] { (byte) 0xFF, 1 }))) {
			assertEquals(255, sealed.read());
			assertEquals(1, sealed.read());
		}
	}
	
	@Test
	void readAcrossChunkBoundaryReturnsShort() throws Exception {
		byte[] payload = CryptoRandom.bytes(CryptoMessages.CHUNK_SIZE + 100);
		try (CryptoInputStream sealed = reader(sealed(payload))) {
			byte[] target = new byte[payload.length];
			assertEquals(CryptoMessages.CHUNK_SIZE, sealed.read(target, 0, target.length));
			assertEquals(100, sealed.read(target, CryptoMessages.CHUNK_SIZE, 100));
			assertArrayEquals(payload, target);
		}
	}
	
	@Test
	void readExactChunkSizePayload() throws Exception {
		byte[] payload = CryptoRandom.bytes(CryptoMessages.CHUNK_SIZE);
		try (CryptoInputStream sealed = reader(sealed(payload))) {
			assertArrayEquals(payload, sealed.readNBytes(payload.length));
			assertEquals(-1, sealed.read());
		}
	}
	
	@Test
	void readIntoOffsetRegion() throws Exception {
		try (CryptoInputStream sealed = reader(sealed(DATA))) {
			byte[] target = new byte[DATA.length + 8];
			assertEquals(DATA.length, sealed.read(target, 4, DATA.length));
			
			assertArrayEquals(new byte[4], Arrays.copyOf(target, 4));
			assertArrayEquals(DATA, Arrays.copyOfRange(target, 4, 4 + DATA.length));
			assertArrayEquals(new byte[4], Arrays.copyOfRange(target, 4 + DATA.length, target.length));
		}
	}
	
	@Test
	void availableIsZeroBeforeFirstRead() throws Exception {
		try (CryptoInputStream sealed = reader(sealed(DATA))) {
			assertEquals(0, sealed.available());
		}
	}
	
	@Test
	void availableAfterPartialRead() throws Exception {
		try (CryptoInputStream sealed = reader(sealed(DATA))) {
			assertEquals(4, sealed.read(new byte[4], 0, 4));
			assertEquals(DATA.length - 4, sealed.available());
		}
	}
	
	@Test
	void availableIsZeroAtChunkBoundary() throws Exception {
		byte[] payload = CryptoRandom.bytes(CryptoMessages.CHUNK_SIZE + 10);
		try (CryptoInputStream sealed = reader(sealed(payload))) {
			assertEquals(CryptoMessages.CHUNK_SIZE, sealed.readNBytes(new byte[CryptoMessages.CHUNK_SIZE], 0, CryptoMessages.CHUNK_SIZE));
			assertEquals(0, sealed.available());
			assertNotEquals(-1, sealed.read());
		}
	}
	
	@Test
	void closeIsIdempotent() throws Exception {
		CloseRecordingInputStream source = new CloseRecordingInputStream(sealed(DATA));
		CryptoInputStream sealed = new CryptoInputStream(recipient(), source);
		sealed.close();
		sealed.close();
		
		assertEquals(1, source.closes);
	}
	
	@Test
	void closeClosesSource() throws Exception {
		CloseRecordingInputStream source = new CloseRecordingInputStream(sealed(DATA));
		new CryptoInputStream(recipient(), source).close();
		
		assertTrue(source.closed);
	}
	
	@Test
	void suiteReturnsParsedSuite() throws Exception {
		for (CryptoSuite suite : CryptoSuite.values()) {
			byte[] artifact = sealed(suite, DATA);
			try (CryptoInputStream sealed = new CryptoInputStream(keyPair(suite).getPrivate(), new ByteArrayInputStream(artifact))) {
				assertEquals(suite, sealed.suite());
			}
		}
	}
	
	@Test
	void readWholePayloadWithReadAllBytes() throws Exception {
		try (CryptoInputStream sealed = reader(sealed(DATA))) {
			assertArrayEquals(DATA, sealed.readAllBytes());
		}
	}
	
	@Test
	void readByteAtATime() throws Exception {
		byte[] payload = CryptoRandom.bytes(CryptoMessages.CHUNK_SIZE + 5);
		try (CryptoInputStream sealed = reader(sealed(payload))) {
			for (byte expected : payload) {
				assertEquals(expected & 0xFF, sealed.read());
			}
			assertEquals(-1, sealed.read());
		}
	}
	
	@Test
	void readWithSmallBuffer() throws Exception {
		ByteArrayOutputStream collected = new ByteArrayOutputStream();
		try (CryptoInputStream sealed = reader(sealed(DATA))) {
			byte[] buffer = new byte[7];
			int read;
			while ((read = sealed.read(buffer, 0, buffer.length)) != -1) {
				collected.write(buffer, 0, read);
			}
		}
		assertArrayEquals(DATA, collected.toByteArray());
	}
	
	@Test
	void readIgnoresTrailingBytesAfterFinalChunk() throws Exception {
		byte[] artifact = CryptoBytes.concat(sealed(DATA), DATA);
		ByteArrayInputStream source = new ByteArrayInputStream(artifact);
		try (CryptoInputStream sealed = new CryptoInputStream(recipient(), source)) {
			assertArrayEquals(DATA, sealed.readAllBytes());
			assertEquals(-1, sealed.read());
		}
		assertArrayEquals(DATA, source.readAllBytes());
	}
	
	@Test
	void keyIsWipedOnClose() throws Exception {
		CryptoInputStream sealed = reader(sealed(DATA));
		Secret key = (Secret) field(sealed, "key");
		assertNotEquals(0, key.material().length);
		
		sealed.close();
		assertThrows(IllegalStateException.class, key::material);
	}
	
	@Test
	void bufferIsWipedOnClose() throws Exception {
		CryptoInputStream sealed = reader(sealed(DATA));
		assertEquals(4, sealed.read(new byte[4], 0, 4));
		byte[] buffer = (byte[]) field(sealed, "buffer");
		assertEquals(DATA.length, buffer.length);
		
		sealed.close();
		assertArrayEquals(new byte[DATA.length], buffer);
	}
	
	@Test
	void roundTripForEverySuite() throws Exception {
		for (CryptoSuite suite : CryptoSuite.values()) {
			byte[] artifact = sealed(suite, DATA);
			try (CryptoInputStream sealed = new CryptoInputStream(keyPair(suite).getPrivate(), new ByteArrayInputStream(artifact))) {
				assertArrayEquals(DATA, sealed.readAllBytes());
			}
		}
	}
	
	@Test
	void roundTripAtChunkBoundaries() throws Exception {
		for (int size : new int[] { 0, 1, CryptoMessages.CHUNK_SIZE - 1, CryptoMessages.CHUNK_SIZE, CryptoMessages.CHUNK_SIZE + 1, CryptoMessages.CHUNK_SIZE * 2 }) {
			byte[] payload = CryptoRandom.bytes(size);
			try (CryptoInputStream sealed = reader(sealed(payload))) {
				assertArrayEquals(payload, sealed.readAllBytes());
			}
		}
	}
	
	@Test
	void roundTripWithMidPayloadFlushes() throws Exception {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		try (CryptoOutputStream sealed = new CryptoOutputStream(keyPair(CryptoSuite.current()).getPublic(), target)) {
			sealed.write(DATA);
			sealed.flush();
			sealed.write(DATA);
			sealed.flush();
		}
		
		try (CryptoInputStream sealed = reader(target.toByteArray())) {
			assertArrayEquals(CryptoBytes.concat(DATA, DATA), sealed.readAllBytes());
		}
	}
	
	@Test
	void roundTripThroughDataStreams() throws Exception {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		try (CryptoOutputStream sealed = new CryptoOutputStream(keyPair(CryptoSuite.current()).getPublic(), target); DataOutputStream out = new DataOutputStream(new BufferedOutputStream(sealed))) {
			out.writeInt(2);
			out.writeLong(11L);
			out.writeUTF("first");
			out.writeLong(22L);
			out.writeUTF("second");
		}
		
		try (CryptoInputStream sealed = reader(target.toByteArray()); DataInputStream in = new DataInputStream(new BufferedInputStream(sealed))) {
			assertEquals(2, in.readInt());
			assertEquals(11L, in.readLong());
			assertEquals("first", in.readUTF());
			assertEquals(22L, in.readLong());
			assertEquals("second", in.readUTF());
		}
	}
	
	@Test
	void roundTripThroughGzip() throws Exception {
		byte[] payload = "compress me".repeat(500).getBytes(StandardCharsets.UTF_8);
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		try (OutputStream out = new GZIPOutputStream(new CryptoOutputStream(keyPair(CryptoSuite.current()).getPublic(), target))) {
			out.write(payload);
		}
		
		try (CryptoInputStream sealed = reader(target.toByteArray()); GZIPInputStream in = new GZIPInputStream(sealed)) {
			assertArrayEquals(payload, in.readAllBytes());
		}
	}
	
	@Test
	void roundTripLinesThroughBufferedReader() throws Exception {
		String text = "INFO started\nERROR failed once\nINFO working\nERROR failed twice\n";
		byte[] artifact = sealed(text.getBytes(StandardCharsets.UTF_8));
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(reader(artifact), StandardCharsets.UTF_8))) {
			assertEquals(List.of("ERROR failed once", "ERROR failed twice"), reader.lines().filter(line -> line.startsWith("ERROR")).toList());
		}
	}
	
	@Test
	void readEmbeddedSectionLeavesSourceOpen() throws Exception {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		CryptoOutputStream sealed = new CryptoOutputStream(keyPair(CryptoSuite.current()).getPublic(), target);
		sealed.write(DATA);
		sealed.finish();
		target.write(new byte[] { 1, 2, 3 });
		
		ByteArrayInputStream source = new ByteArrayInputStream(target.toByteArray());
		try (CryptoInputStream reader = new CryptoInputStream(recipient(), source)) {
			assertArrayEquals(DATA, reader.readAllBytes());
		}
		assertArrayEquals(new byte[] { 1, 2, 3 }, source.readAllBytes());
	}
	
	@Test
	void readTwoSectionsFromOneSource() throws Exception {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		CryptoOutputStream first = new CryptoOutputStream(keyPair(CryptoSuite.current()).getPublic(), target);
		first.write(DATA);
		first.finish();
		CryptoOutputStream second = new CryptoOutputStream(keyPair(CryptoSuite.current()).getPublic(), target);
		second.write(new byte[] { 7, 8, 9 });
		second.finish();
		
		ByteArrayInputStream source = new ByteArrayInputStream(target.toByteArray());
		assertArrayEquals(DATA, new CryptoInputStream(recipient(), source).readAllBytes());
		assertArrayEquals(new byte[] { 7, 8, 9 }, new CryptoInputStream(recipient(), source).readAllBytes());
	}
	
	@Test
	void wrongKeyFailsBeforeAnyPlaintext() throws Exception {
		byte[] artifact = sealed(DATA);
		KeyPair other = Kems.generateKeyPair(CryptoSuite.current().kem());
		CloseRecordingInputStream source = new CloseRecordingInputStream(artifact);
		
		assertThrows(AuthenticationException.class, () -> new CryptoInputStream(other.getPrivate(), source));
		assertEquals(headerLength(CryptoSuite.current()), source.read);
	}
	
	@Test
	void tamperedLastChunkFailsAfterEarlierBytes() throws Exception {
		byte[] artifact = sealed(CryptoRandom.bytes(CryptoMessages.CHUNK_SIZE + 10));
		artifact[frames(CryptoSuite.current(), artifact).getLast().offset() + 5] ^= 0x01;
		
		try (CryptoInputStream sealed = reader(artifact)) {
			assertEquals(CryptoMessages.CHUNK_SIZE, sealed.read(new byte[CryptoMessages.CHUNK_SIZE], 0, CryptoMessages.CHUNK_SIZE));
			assertThrows(AuthenticationException.class, sealed::read);
		}
	}
	
	@Test
	void largePayloadStreamsWithBoundedMemory() throws Exception {
		byte[] payload = CryptoRandom.bytes(CryptoMessages.CHUNK_SIZE * 40 + 3);
		try (CryptoInputStream sealed = reader(sealed(payload))) {
			assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, payload), Hashes.hash(HashAlgorithm.SHA_256, sealed.readAllBytes()));
		}
	}
	
	@Test
	void readAfterEndOfPayloadStaysMinusOne() throws Exception {
		try (CryptoInputStream sealed = reader(sealed(DATA))) {
			assertArrayEquals(DATA, sealed.readAllBytes());
			assertEquals(-1, sealed.read());
			assertEquals(-1, sealed.read());
			assertEquals(-1, sealed.read());
		}
	}
	
	private record Frame(byte kind, int length, int offset) {}
	
	private static final class FailingInputStream extends InputStream {
		
		private final ByteArrayInputStream delegate;
		private final int allowedBytes;
		private int read;
		private boolean failOnClose;
		
		private FailingInputStream(byte[] content, int allowedBytes) {
			this.delegate = new ByteArrayInputStream(content);
			this.allowedBytes = allowedBytes;
		}
		
		@Override
		public int read() throws IOException {
			byte[] single = new byte[1];
			return this.read(single, 0, 1) == -1 ? -1 : single[0] & 0xFF;
		}
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException {
			if (length == 0) {
				return 0;
			}
			if (this.read >= this.allowedBytes) {
				throw new IOException("Read failed");
			}
			
			int take = this.delegate.read(buffer, offset, Math.min(length, this.allowedBytes - this.read));
			if (take > 0) {
				this.read += take;
			}
			return take;
		}
		
		@Override
		public void close() throws IOException {
			if (this.failOnClose) {
				throw new IOException("Close failed");
			}
		}
	}
	
	private static final class CloseRecordingInputStream extends InputStream {
		
		private final ByteArrayInputStream delegate;
		private int read;
		private int closes;
		private boolean closed;
		
		private CloseRecordingInputStream(byte[] content) {
			this.delegate = new ByteArrayInputStream(content);
		}
		
		@Override
		public int read() {
			int value = this.delegate.read();
			if (value != -1) {
				this.read++;
			}
			return value;
		}
		
		@Override
		public int read(byte[] buffer, int offset, int length) {
			int take = this.delegate.read(buffer, offset, length);
			if (take > 0) {
				this.read += take;
			}
			return take;
		}
		
		@Override
		public void close() {
			this.closes++;
			this.closed = true;
		}
	}
}
