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

import net.luis.utils.crypto.algorithm.SignatureAlgorithm;
import net.luis.utils.crypto.exception.AuthenticationException;
import net.luis.utils.crypto.exception.MalformedDataException;
import net.luis.utils.crypto.util.CryptoBytes;
import net.luis.utils.crypto.util.CryptoRandom;
import net.luis.utils.util.UUIDs;
import org.junit.jupiter.api.*;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Test class for {@link SealedStream}.<br>
 *
 * @author Luis-St
 */
class SealedStreamTest {
	
	private static final CryptoSuite SUITE = CryptoSuite.HYBRID_V1;
	private static final int CHUNK_SIZE = 64 * 1024;
	private static final int NONCE_PREFIX_LENGTH = 8;
	private static final byte[] PAYLOAD = "the quick brown fox".getBytes(StandardCharsets.UTF_8);
	
	private static final Path DIRECTORY = Path.of("SealedStreamTest-files");
	
	private static KeyPair recipient;
	private static KeyPair otherRecipient;
	private static KeyPair ed25519;
	private static int prologueLength;
	
	@BeforeAll
	static void setUp() throws Exception {
		Providers.installBouncyCastle();
		Files.createDirectories(DIRECTORY);
		recipient = Kems.generateKeyPair(SUITE.kem());
		otherRecipient = Kems.generateKeyPair(SUITE.kem());
		ed25519 = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
		prologueLength = Sealed.HEADER_LENGTH + SUITE.kem().encapsulationLength() + NONCE_PREFIX_LENGTH;
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
	
	private static byte[] seal(byte[] payload) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		SealedStream.seal(SUITE, recipient.getPublic(), new ByteArrayInputStream(payload), out);
		return out.toByteArray();
	}
	
	private static byte[] unseal(byte[] artifact) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		SealedStream.unseal(recipient.getPrivate(), new ByteArrayInputStream(artifact), out);
		return out.toByteArray();
	}
	
	private static byte[] flip(byte[] source, int index) {
		byte[] copy = source.clone();
		copy[index] ^= 1;
		return copy;
	}
	
	private static List<int[]> frames(byte[] artifact) {
		List<int[]> frames = new ArrayList<>();
		int offset = prologueLength;
		while (offset < artifact.length) {
			int kind = artifact[offset];
			int length = ByteBuffer.wrap(artifact, offset + 1, 4).getInt();
			frames.add(new int[] { offset, kind, length });
			offset += 5 + length;
		}
		return frames;
	}
	
	private static SecureRandom seeded() throws Exception {
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		random.setSeed(new byte[] { 1, 2, 3, 4 });
		return random;
	}
	
	@Test
	void constructorIsPrivate() throws Exception {
		Constructor<?>[] constructors = SealedStream.class.getDeclaredConstructors();
		assertEquals(1, constructors.length);
		assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
		assertTrue(Modifier.isFinal(SealedStream.class.getModifiers()));
		
		Constructor<SealedStream> constructor = SealedStream.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		assertNotNull(constructor.newInstance());
	}
	
	@Test
	void sealWithNullRandom() {
		assertEquals("Random must not be null", assertThrows(NullPointerException.class, () -> SealedStream.seal(null, SUITE, recipient.getPublic(), new ByteArrayInputStream(PAYLOAD), new ByteArrayOutputStream())).getMessage());
	}
	
	@Test
	void sealWithNullSuite() {
		assertEquals("Suite must not be null", assertThrows(NullPointerException.class, () -> SealedStream.seal(null, recipient.getPublic(), new ByteArrayInputStream(PAYLOAD), new ByteArrayOutputStream())).getMessage());
	}
	
	@Test
	void sealWithNullRecipient() {
		assertEquals("Recipient must not be null", assertThrows(NullPointerException.class, () -> SealedStream.seal(SUITE, null, new ByteArrayInputStream(PAYLOAD), new ByteArrayOutputStream())).getMessage());
	}
	
	@Test
	void sealWithNullInput() {
		assertEquals("Input must not be null", assertThrows(NullPointerException.class, () -> SealedStream.seal(SUITE, recipient.getPublic(), null, new ByteArrayOutputStream())).getMessage());
	}
	
	@Test
	void sealWithNullOutput() {
		assertEquals("Output must not be null", assertThrows(NullPointerException.class, () -> SealedStream.seal(SUITE, recipient.getPublic(), new ByteArrayInputStream(PAYLOAD), null)).getMessage());
	}
	
	@Test
	void sealWithAllNull() {
		assertEquals("Random must not be null", assertThrows(NullPointerException.class, () -> SealedStream.seal(null, null, null, null, null)).getMessage());
	}
	
	@Test
	void sealWithMismatchedRecipientKey() {
		assertThrows(ClassCastException.class, () -> SealedStream.seal(SUITE, ed25519.getPublic(), new ByteArrayInputStream(PAYLOAD), new ByteArrayOutputStream()));
	}
	
	@Test
	void sealWithFailingInput() {
		IOException failure = new IOException("broken");
		UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> SealedStream.seal(SUITE, recipient.getPublic(), new FailingStream(0, failure), new ByteArrayOutputStream()));
		
		assertEquals("Failed to write the sealed stream", exception.getMessage());
		assertSame(failure, exception.getCause());
	}
	
	@Test
	void sealWithFailingOutput() {
		UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> SealedStream.seal(SUITE, recipient.getPublic(), new ByteArrayInputStream(PAYLOAD), new FailingOutput(0)));
		assertEquals("Failed to write the sealed stream", exception.getMessage());
	}
	
	@Test
	void sealWithOutputFailingPartway() {
		FailingOutput out = new FailingOutput(100);
		assertThrows(UncheckedIOException.class, () -> SealedStream.seal(SUITE, recipient.getPublic(), new ByteArrayInputStream(PAYLOAD), out));
		assertEquals(100, out.written);
	}
	
	@Test
	void unsealWithNullRecipient() {
		assertEquals("Recipient must not be null", assertThrows(NullPointerException.class, () -> SealedStream.unseal(null, new ByteArrayInputStream(seal(PAYLOAD)), new ByteArrayOutputStream())).getMessage());
	}
	
	@Test
	void unsealWithNullInput() {
		assertEquals("Input must not be null", assertThrows(NullPointerException.class, () -> SealedStream.unseal(recipient.getPrivate(), null, new ByteArrayOutputStream())).getMessage());
	}
	
	@Test
	void unsealWithNullOutput() {
		assertEquals("Output must not be null", assertThrows(NullPointerException.class, () -> SealedStream.unseal(recipient.getPrivate(), new ByteArrayInputStream(seal(PAYLOAD)), null)).getMessage());
	}
	
	@Test
	void unsealWithFailingInput() {
		IOException failure = new IOException("broken");
		UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> SealedStream.unseal(recipient.getPrivate(), new FailingStream(0, failure), new ByteArrayOutputStream()));
		
		assertEquals("Failed to read the sealed stream", exception.getMessage());
		assertSame(failure, exception.getCause());
	}
	
	@Test
	void unsealWithFailingOutput() {
		byte[] artifact = seal(PAYLOAD);
		UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> SealedStream.unseal(recipient.getPrivate(), new ByteArrayInputStream(artifact), new FailingOutput(0)));
		assertEquals("Failed to read the sealed stream", exception.getMessage());
	}
	
	@Test
	void unsealWithEmptyInput() {
		assertThrows(MalformedDataException.class, () -> unseal(new byte[0]));
	}
	
	@Test
	void unsealWithTruncatedHeader() {
		for (int length : new int[] { 1, Sealed.HEADER_LENGTH - 1 }) {
			assertThrows(MalformedDataException.class, () -> unseal(new byte[length]));
		}
	}
	
	@Test
	void unsealWithBadMagic() {
		byte[] artifact = seal(PAYLOAD);
		for (int i = 0; i < 4; i++) {
			int index = i;
			MalformedDataException exception = assertThrows(MalformedDataException.class, () -> unseal(flip(artifact, index)), "index " + index);
			assertEquals("Not a sealed blob (bad magic)", exception.getMessage());
		}
	}
	
	@Test
	void unsealWithUnsupportedVersion() {
		byte[] artifact = seal(PAYLOAD);
		for (byte version : new byte[] { 0, 2, (byte) 0xFF }) {
			byte[] corrupted = artifact.clone();
			corrupted[4] = version;
			assertTrue(assertThrows(MalformedDataException.class, () -> unseal(corrupted)).getMessage().contains("Unsupported sealed format version"));
		}
	}
	
	@Test
	void unsealWithUnknownSuiteId() {
		byte[] artifact = seal(PAYLOAD);
		System.arraycopy(CryptoBytes.of((short) 999), 0, artifact, 5, 2);
		assertTrue(assertThrows(MalformedDataException.class, () -> unseal(artifact)).getMessage().contains("999"));
	}
	
	@Test
	void unsealWithTruncatedEncapsulation() {
		byte[] artifact = seal(PAYLOAD);
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> unseal(Arrays.copyOf(artifact, Sealed.HEADER_LENGTH + 10)));
		assertEquals("Truncated sealed stream header", exception.getMessage());
	}
	
	@Test
	void unsealWithTruncatedNoncePrefix() {
		byte[] artifact = seal(PAYLOAD);
		int afterEncapsulation = Sealed.HEADER_LENGTH + SUITE.kem().encapsulationLength();
		
		for (int extra : new int[] { 0, 7 }) {
			MalformedDataException exception = assertThrows(MalformedDataException.class, () -> unseal(Arrays.copyOf(artifact, afterEncapsulation + extra)));
			assertEquals("Truncated sealed stream header", exception.getMessage());
		}
	}
	
	@Test
	void unsealWithWrongKey() {
		byte[] artifact = seal(PAYLOAD);
		AuthenticationException exception = assertThrows(AuthenticationException.class, () -> SealedStream.unseal(otherRecipient.getPrivate(), new ByteArrayInputStream(artifact), new ByteArrayOutputStream()));
		assertEquals("Key commitment mismatch - the header does not belong to this key", exception.getMessage());
	}
	
	@Test
	void unsealWithTamperedCommitment() {
		byte[] artifact = seal(PAYLOAD);
		for (int index : new int[] { 23, 54 }) {
			AuthenticationException exception = assertThrows(AuthenticationException.class, () -> unseal(flip(artifact, index)));
			assertTrue(exception.getMessage().contains("Key commitment mismatch"));
		}
	}
	
	@Test
	void unsealWithNoChunks() {
		byte[] artifact = seal(PAYLOAD);
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> unseal(Arrays.copyOf(artifact, prologueLength)));
		assertEquals("Sealed stream ended without a final chunk (truncated)", exception.getMessage());
	}
	
	@Test
	void unsealWithPartialFrameHeader() {
		byte[] artifact = seal(PAYLOAD);
		for (int extra : new int[] { 1, 2, 3, 4 }) {
			MalformedDataException exception = assertThrows(MalformedDataException.class, () -> unseal(Arrays.copyOf(artifact, prologueLength + extra)));
			assertEquals("Truncated chunk header", exception.getMessage());
		}
	}
	
	@Test
	void unsealWithUnknownChunkKind() {
		byte[] artifact = seal(PAYLOAD);
		for (byte kind : new byte[] { 0x02, 0x7F, (byte) 0xFF }) {
			byte[] corrupted = artifact.clone();
			corrupted[prologueLength] = kind;
			
			MalformedDataException exception = assertThrows(MalformedDataException.class, () -> unseal(corrupted));
			assertTrue(exception.getMessage().contains(String.format("0x%02X", kind)), exception.getMessage());
		}
	}
	
	@Test
	void unsealWithImplausibleChunkLength() {
		byte[] artifact = seal(PAYLOAD);
		for (int length : new int[] { 0, SUITE.aead().tagLength() - 1, -1, CHUNK_SIZE + SUITE.aead().tagLength() + 1, Integer.MAX_VALUE }) {
			byte[] corrupted = artifact.clone();
			System.arraycopy(CryptoBytes.of(length), 0, corrupted, prologueLength + 1, 4);
			
			MalformedDataException exception = assertThrows(MalformedDataException.class, () -> unseal(corrupted));
			assertTrue(exception.getMessage().contains("Implausible chunk length " + length), exception.getMessage());
		}
	}
	
	@Test
	void unsealWithTruncatedChunkBody() {
		byte[] artifact = seal(PAYLOAD);
		for (int missing : new int[] { 1, 10 }) {
			MalformedDataException exception = assertThrows(MalformedDataException.class, () -> unseal(Arrays.copyOf(artifact, artifact.length - missing)));
			assertEquals("Truncated chunk body", exception.getMessage());
		}
	}
	
	@Test
	void unsealWithTruncatedMultiChunkStream() {
		byte[] artifact = seal(CryptoRandom.bytes(200 * 1024));
		List<int[]> frames = frames(artifact);
		assertEquals(4, frames.size());
		
		int cut = frames.get(2)[0];
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> SealedStream.unseal(recipient.getPrivate(), new ByteArrayInputStream(Arrays.copyOf(artifact, cut)), out));
		
		assertEquals("Sealed stream ended without a final chunk (truncated)", exception.getMessage());
		assertEquals(2 * CHUNK_SIZE, out.size());
	}
	
	@Test
	void unsealWithFlippedFinalKind() {
		byte[] artifact = seal(PAYLOAD);
		byte[] corrupted = artifact.clone();
		corrupted[prologueLength] = 0x00;
		
		assertThrows(AuthenticationException.class, () -> unseal(corrupted));
	}
	
	@Test
	void unsealWithFlippedMoreKind() {
		byte[] artifact = seal(CryptoRandom.bytes(CHUNK_SIZE + 1));
		assertEquals(2, frames(artifact).size());
		byte[] corrupted = artifact.clone();
		corrupted[prologueLength] = 0x01;
		
		assertThrows(AuthenticationException.class, () -> unseal(corrupted));
	}
	
	@Test
	void unsealWithReorderedChunks() {
		byte[] artifact = seal(CryptoRandom.bytes(3 * CHUNK_SIZE));
		List<int[]> frames = frames(artifact);
		assertEquals(3, frames.size());
		
		int size = 5 + frames.get(0)[2];
		byte[] reordered = artifact.clone();
		byte[] first = CryptoBytes.slice(artifact, frames.get(0)[0], size);
		System.arraycopy(artifact, frames.get(1)[0], reordered, frames.get(0)[0], size);
		System.arraycopy(first, 0, reordered, frames.get(1)[0], size);
		
		assertThrows(AuthenticationException.class, () -> unseal(reordered));
	}
	
	@Test
	void unsealWithDuplicatedChunk() {
		byte[] artifact = seal(CryptoRandom.bytes(CHUNK_SIZE + 1));
		List<int[]> frames = frames(artifact);
		byte[] first = CryptoBytes.slice(artifact, frames.get(0)[0], 5 + frames.get(0)[2]);
		byte[] duplicated = CryptoBytes.concat(CryptoBytes.slice(artifact, 0, frames.get(1)[0]), first, CryptoBytes.slice(artifact, frames.get(1)[0], artifact.length - frames.get(1)[0]));
		
		assertThrows(AuthenticationException.class, () -> unseal(duplicated));
	}
	
	@Test
	void unsealWithTamperedChunkBody() {
		byte[] artifact = seal(PAYLOAD);
		for (int index : new int[] { prologueLength + 5, artifact.length - 1 }) {
			assertThrows(AuthenticationException.class, () -> unseal(flip(artifact, index)), "index " + index);
		}
	}
	
	@Test
	void unsealWithTamperedPrologue() {
		byte[] artifact = seal(PAYLOAD);
		assertThrows(AuthenticationException.class, () -> unseal(flip(artifact, Sealed.HEADER_LENGTH)));
	}
	
	@Test
	void unsealWithAppendedChunkAfterFinal() {
		byte[] artifact = seal(PAYLOAD);
		List<int[]> frames = frames(artifact);
		byte[] appended = CryptoBytes.concat(artifact, CryptoBytes.slice(artifact, frames.getFirst()[0], 5 + frames.getFirst()[2]));
		
		assertArrayEquals(PAYLOAD, assertDoesNotThrow(() -> unseal(appended)));
	}
	
	@Test
	void unsealWithSealedSingleArtifact() {
		byte[] single = Sealed.seal(SUITE, recipient.getPublic(), PAYLOAD, null);
		assertThrows(MalformedDataException.class, () -> unseal(single));
	}
	
	@Test
	void sealAndUnsealRoundTrip() {
		byte[] payload = CryptoRandom.bytes(100);
		assertArrayEquals(payload, unseal(seal(payload)));
	}
	
	@Test
	void sealEmptyPayload() {
		byte[] artifact = seal(new byte[0]);
		List<int[]> frames = frames(artifact);
		
		assertEquals(1, frames.size());
		assertEquals(1, frames.getFirst()[1]);
		assertEquals(SUITE.aead().tagLength(), frames.getFirst()[2]);
		assertEquals(0, unseal(artifact).length);
	}
	
	@Test
	void sealPayloadSmallerThanOneChunk() {
		for (int size : new int[] { 1, CHUNK_SIZE - 1 }) {
			byte[] payload = CryptoRandom.bytes(size);
			byte[] artifact = seal(payload);
			
			assertEquals(1, frames(artifact).size(), "size " + size);
			assertEquals(1, frames(artifact).getFirst()[1]);
			assertArrayEquals(payload, unseal(artifact));
		}
	}
	
	@Test
	void sealPayloadExactlyOneChunk() {
		byte[] payload = CryptoRandom.bytes(CHUNK_SIZE);
		byte[] artifact = seal(payload);
		
		assertEquals(1, frames(artifact).size());
		assertEquals(1, frames(artifact).getFirst()[1]);
		assertArrayEquals(payload, unseal(artifact));
	}
	
	@Test
	void sealPayloadJustOverOneChunk() {
		byte[] payload = CryptoRandom.bytes(CHUNK_SIZE + 1);
		byte[] artifact = seal(payload);
		List<int[]> frames = frames(artifact);
		
		assertEquals(2, frames.size());
		assertEquals(0, frames.get(0)[1]);
		assertEquals(CHUNK_SIZE + SUITE.aead().tagLength(), frames.get(0)[2]);
		assertEquals(1, frames.get(1)[1]);
		assertEquals(1 + SUITE.aead().tagLength(), frames.get(1)[2]);
		assertArrayEquals(payload, unseal(artifact));
	}
	
	@Test
	void sealPayloadExactlyTwoChunks() {
		byte[] payload = CryptoRandom.bytes(2 * CHUNK_SIZE);
		byte[] artifact = seal(payload);
		List<int[]> frames = frames(artifact);
		
		assertEquals(2, frames.size());
		assertEquals(1, frames.get(1)[1]);
		assertEquals(CHUNK_SIZE + SUITE.aead().tagLength(), frames.get(1)[2]);
		assertArrayEquals(payload, unseal(artifact));
	}
	
	@Test
	void sealMultiChunkPayload() {
		byte[] payload = CryptoRandom.bytes(5 * CHUNK_SIZE + 123);
		byte[] artifact = seal(payload);
		List<int[]> frames = frames(artifact);
		
		assertEquals(6, frames.size());
		assertEquals(1, frames.getLast()[1]);
		assertArrayEquals(payload, unseal(artifact));
	}
	
	@Test
	void unsealWithMatchingCommitment() {
		assertDoesNotThrow(() -> unseal(seal(PAYLOAD)));
	}
	
	@Test
	void unsealWithMinimumValidChunkLength() {
		byte[] artifact = seal(new byte[0]);
		assertEquals(SUITE.aead().tagLength(), frames(artifact).getFirst()[2]);
		assertDoesNotThrow(() -> unseal(artifact));
	}
	
	@Test
	void unsealWithMaximumValidChunkLength() {
		byte[] artifact = seal(CryptoRandom.bytes(CHUNK_SIZE));
		assertEquals(CHUNK_SIZE + SUITE.aead().tagLength(), frames(artifact).getFirst()[2]);
		assertDoesNotThrow(() -> unseal(artifact));
	}
	
	@Test
	void incrementBelowMaximum() {
		byte[] artifact = seal(CryptoRandom.bytes(2 * CHUNK_SIZE + 1));
		List<int[]> frames = frames(artifact);
		
		assertEquals(3, frames.size());
		assertArrayEquals(CryptoRandom.bytes(0), new byte[0]);
		assertDoesNotThrow(() -> unseal(artifact));
	}
	
	@Test
	void sealedStreamLayout() {
		byte[] artifact = seal(PAYLOAD);
		
		assertArrayEquals("LUC1".getBytes(StandardCharsets.US_ASCII), Arrays.copyOf(artifact, 4));
		assertEquals(1, artifact[4]);
		assertEquals(SUITE.id(), ByteBuffer.wrap(artifact, 5, 2).getShort());
		assertEquals(7, UUIDs.fromBytes(CryptoBytes.slice(artifact, 7, 16)).version());
		assertEquals(32, CryptoBytes.slice(artifact, 23, 32).length);
		assertEquals(1, artifact[prologueLength]);
	}
	
	@Test
	void chunkFrameLayout() {
		byte[] payload = CryptoRandom.bytes(CHUNK_SIZE + 100);
		byte[] artifact = seal(payload);
		List<int[]> frames = frames(artifact);
		
		assertEquals(CHUNK_SIZE + SUITE.aead().tagLength(), frames.get(0)[2]);
		assertEquals(100 + SUITE.aead().tagLength(), frames.get(1)[2]);
		assertEquals(artifact.length, frames.get(1)[0] + 5 + frames.get(1)[2]);
	}
	
	@Test
	void chunkLengthIsBigEndian() {
		byte[] artifact = seal(new byte[0]);
		assertArrayEquals(new byte[] { 0, 0, 0, 16 }, CryptoBytes.slice(artifact, prologueLength + 1, 4));
		assertEquals(16, SUITE.aead().tagLength());
	}
	
	@Test
	void streamLengthIsPredictable() {
		for (int size : new int[] { 0, 100, CHUNK_SIZE, CHUNK_SIZE + 1 }) {
			byte[] artifact = seal(CryptoRandom.bytes(size));
			int chunks = Math.max(1, (size + CHUNK_SIZE - 1) / CHUNK_SIZE);
			int expected = prologueLength + chunks * (5 + SUITE.aead().tagLength()) + size;
			assertEquals(expected, artifact.length, "size " + size);
		}
	}
	
	@Test
	void sealSharesTheHeaderWithSealed() {
		byte[] artifact = seal(PAYLOAD);
		
		assertSame(SUITE, Sealed.suiteOf(artifact));
		assertEquals(UUIDs.fromBytes(CryptoBytes.slice(artifact, 7, 16)), Sealed.messageIdOf(artifact));
	}
	
	@Test
	void sealDoesNotCloseEitherStream() throws Exception {
		RecordingStream in = new RecordingStream(PAYLOAD);
		RecordingOutput out = new RecordingOutput();
		SealedStream.seal(SUITE, recipient.getPublic(), in, out);
		
		assertFalse(in.closed);
		assertFalse(out.closed);
		assertEquals(-1, in.read());
	}
	
	@Test
	void unsealDoesNotCloseEitherStream() {
		RecordingStream in = new RecordingStream(seal(PAYLOAD));
		RecordingOutput out = new RecordingOutput();
		SealedStream.unseal(recipient.getPrivate(), in, out);
		
		assertFalse(in.closed);
		assertFalse(out.closed);
		assertArrayEquals(PAYLOAD, out.delegate.toByteArray());
	}
	
	@Test
	void sealFlushesTheOutput() {
		RecordingOutput out = new RecordingOutput();
		SealedStream.seal(SUITE, recipient.getPublic(), new ByteArrayInputStream(PAYLOAD), out);
		assertTrue(out.flushes >= 1);
	}
	
	@Test
	void unsealFlushesTheOutput() {
		RecordingOutput out = new RecordingOutput();
		SealedStream.unseal(recipient.getPrivate(), new ByteArrayInputStream(seal(PAYLOAD)), out);
		assertTrue(out.flushes >= 1);
	}
	
	@Test
	void sealProducesDifferentStreamsPerCall() {
		byte[] first = seal(PAYLOAD);
		byte[] second = seal(PAYLOAD);
		int prefixOffset = Sealed.HEADER_LENGTH + SUITE.kem().encapsulationLength();
		
		assertFalse(Arrays.equals(first, second));
		assertNotEquals(Sealed.messageIdOf(first), Sealed.messageIdOf(second));
		assertFalse(Arrays.equals(CryptoBytes.slice(first, prefixOffset, 8), CryptoBytes.slice(second, prefixOffset, 8)));
	}
	
	@Test
	void messageIdIsTimeOrdered() {
		UUID messageId = Sealed.messageIdOf(seal(PAYLOAD));
		
		assertEquals(7, messageId.version());
		assertTrue(Math.abs(UUIDs.unixMillis(messageId) - System.currentTimeMillis()) < 10_000L);
	}
	
	@Test
	void roundTripForEverySuite() {
		for (CryptoSuite suite : CryptoSuite.values()) {
			assumeTrue(suite.isSupported());
			KeyPair pair = Kems.generateKeyPair(suite.kem());
			ByteArrayOutputStream sealedOut = new ByteArrayOutputStream();
			SealedStream.seal(suite, pair.getPublic(), new ByteArrayInputStream(PAYLOAD), sealedOut);
			
			ByteArrayOutputStream openedOut = new ByteArrayOutputStream();
			SealedStream.unseal(pair.getPrivate(), new ByteArrayInputStream(sealedOut.toByteArray()), openedOut);
			assertArrayEquals(PAYLOAD, openedOut.toByteArray(), suite.name());
		}
	}
	
	@Test
	void roundTripAtEveryChunkBoundary() {
		for (int size : new int[] { 0, 1, CHUNK_SIZE - 1, CHUNK_SIZE, CHUNK_SIZE + 1, 2 * CHUNK_SIZE - 1, 2 * CHUNK_SIZE, 2 * CHUNK_SIZE + 1 }) {
			byte[] payload = CryptoRandom.bytes(size);
			byte[] artifact = seal(payload);
			
			assertArrayEquals(payload, unseal(artifact), "size " + size);
			assertEquals(Math.max(1, (size + CHUNK_SIZE - 1) / CHUNK_SIZE), frames(artifact).size(), "size " + size);
		}
	}
	
	@Test
	void roundTripLargePayload() {
		byte[] payload = CryptoRandom.bytes(1 << 20);
		byte[] artifact = seal(payload);
		
		assertEquals(16, frames(artifact).size());
		assertArrayEquals(payload, unseal(artifact));
	}
	
	@Test
	void nothingIsWrittenBeforeItIsAuthenticated() {
		byte[] artifact = seal(CryptoRandom.bytes(3 * CHUNK_SIZE));
		List<int[]> frames = frames(artifact);
		byte[] corrupted = flip(artifact, frames.get(1)[0] + 5);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		assertThrows(AuthenticationException.class, () -> SealedStream.unseal(recipient.getPrivate(), new ByteArrayInputStream(corrupted), out));
		assertEquals(CHUNK_SIZE, out.size());
	}
	
	@Test
	void truncationIsDetectedAtEveryChunkBoundary() {
		byte[] artifact = seal(CryptoRandom.bytes(4 * CHUNK_SIZE));
		List<int[]> frames = frames(artifact);
		assertEquals(4, frames.size());
		
		for (int[] frame : frames) {
			assertThrows(MalformedDataException.class, () -> unseal(Arrays.copyOf(artifact, frame[0])), "boundary " + frame[0]);
			assertThrows(MalformedDataException.class, () -> unseal(Arrays.copyOf(artifact, frame[0] + 3)), "partial " + frame[0]);
		}
	}
	
	@Test
	void chunkNoncesAreDistinct() {
		byte[] artifact = seal(CryptoRandom.bytes(5 * CHUNK_SIZE));
		byte[] prefix = CryptoBytes.slice(artifact, Sealed.HEADER_LENGTH + SUITE.kem().encapsulationLength(), 8);
		
		Set<String> nonces = new HashSet<>();
		for (int counter = 0; counter < 5; counter++) {
			byte[] nonce = CryptoBytes.concat(prefix, CryptoBytes.of(counter));
			assertArrayEquals(prefix, CryptoBytes.slice(nonce, 0, 8));
			nonces.add(HexFormat.of().formatHex(nonce));
		}
		assertEquals(5, nonces.size());
	}
	
	@Test
	void noncePrefixIsRandomPerStream() {
		int prefixOffset = Sealed.HEADER_LENGTH + SUITE.kem().encapsulationLength();
		Set<String> prefixes = new HashSet<>();
		for (int i = 0; i < 20; i++) {
			prefixes.add(HexFormat.of().formatHex(CryptoBytes.slice(seal(PAYLOAD), prefixOffset, 8)));
		}
		assertEquals(20, prefixes.size());
	}
	
	@Test
	void sealWithFixedRandomProducesTheSameNoncePrefix() throws Exception {
		ByteArrayOutputStream first = new ByteArrayOutputStream();
		ByteArrayOutputStream second = new ByteArrayOutputStream();
		SealedStream.seal(seeded(), SUITE, recipient.getPublic(), new ByteArrayInputStream(PAYLOAD), first);
		SealedStream.seal(seeded(), SUITE, recipient.getPublic(), new ByteArrayInputStream(PAYLOAD), second);
		int prefixOffset = Sealed.HEADER_LENGTH + SUITE.kem().encapsulationLength();
		
		assertFalse(Arrays.equals(first.toByteArray(), second.toByteArray()));
		assertArrayEquals(CryptoBytes.slice(first.toByteArray(), prefixOffset, 8), CryptoBytes.slice(second.toByteArray(), prefixOffset, 8));
	}
	
	@Test
	void streamAndSealedAreNotInterchangeable() {
		byte[] stream = seal(PAYLOAD);
		byte[] single = Sealed.seal(SUITE, recipient.getPublic(), PAYLOAD, null);
		
		assertThrows(AuthenticationException.class, () -> Sealed.unseal(recipient.getPrivate(), stream));
		assertThrows(MalformedDataException.class, () -> unseal(single));
	}
	
	@Test
	void unsealAfterSuiteMigration() {
		assumeTrue(CryptoSuite.CLASSICAL_V1.isSupported());
		KeyPair classical = Kems.generateKeyPair(CryptoSuite.CLASSICAL_V1.kem());
		ByteArrayOutputStream sealedOut = new ByteArrayOutputStream();
		SealedStream.seal(CryptoSuite.CLASSICAL_V1, classical.getPublic(), new ByteArrayInputStream(PAYLOAD), sealedOut);
		
		assertSame(CryptoSuite.CLASSICAL_V1, Sealed.suiteOf(sealedOut.toByteArray()));
		assertNotSame(CryptoSuite.CLASSICAL_V1, CryptoSuite.current());
		ByteArrayOutputStream openedOut = new ByteArrayOutputStream();
		SealedStream.unseal(classical.getPrivate(), new ByteArrayInputStream(sealedOut.toByteArray()), openedOut);
		assertArrayEquals(PAYLOAD, openedOut.toByteArray());
	}
	
	@Test
	void sealFromSlowInputStream() {
		byte[] payload = CryptoRandom.bytes(CHUNK_SIZE + 500);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		SealedStream.seal(SUITE, recipient.getPublic(), new SlowStream(payload, 13), out);
		byte[] artifact = out.toByteArray();
		
		assertEquals(frames(seal(payload)).size(), frames(artifact).size());
		assertEquals(seal(payload).length, artifact.length);
		assertArrayEquals(payload, unseal(artifact));
	}
	
	@Test
	void unsealFromSlowInputStream() {
		byte[] payload = CryptoRandom.bytes(2 * CHUNK_SIZE + 7);
		byte[] artifact = seal(payload);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		assertDoesNotThrow(() -> SealedStream.unseal(recipient.getPrivate(), new SlowStream(artifact, 13), out));
		assertArrayEquals(payload, out.toByteArray());
	}
	
	@Test
	void sealToAndFromFiles() throws Exception {
		Path source = DIRECTORY.resolve("source.bin");
		Path target = DIRECTORY.resolve("target.sealed");
		Path restored = DIRECTORY.resolve("restored.bin");
		byte[] payload = CryptoRandom.bytes(300 * 1024);
		Files.write(source, payload);
		
		try (InputStream in = Files.newInputStream(source); OutputStream out = Files.newOutputStream(target)) {
			SealedStream.seal(SUITE, recipient.getPublic(), in, out);
		}
		try (InputStream in = Files.newInputStream(target); OutputStream out = Files.newOutputStream(restored)) {
			SealedStream.unseal(recipient.getPrivate(), in, out);
		}
		assertArrayEquals(payload, Files.readAllBytes(restored));
	}
	
	@Test
	void sealDoesNotMutateInput() {
		byte[] payload = CryptoRandom.bytes(1000);
		byte[] copy = payload.clone();
		
		seal(payload);
		assertArrayEquals(copy, payload);
	}
	
	@Test
	void repeatedUnsealIsDeterministic() {
		byte[] artifact = seal(PAYLOAD);
		for (int i = 0; i < 3; i++) {
			assertArrayEquals(PAYLOAD, unseal(artifact));
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
	
	private static final class FailingOutput extends OutputStream {
		
		private final int limit;
		private int written;
		
		private FailingOutput(int limit) {
			this.limit = limit;
		}
		
		@Override
		public void write(int value) throws IOException {
			this.write(new byte[] { (byte) value }, 0, 1);
		}
		
		@Override
		public void write(byte[] buffer, int offset, int length) throws IOException {
			int accepted = Math.min(length, this.limit - this.written);
			this.written += accepted;
			if (accepted < length) {
				throw new IOException("broken");
			}
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
	
	private static final class RecordingOutput extends OutputStream {
		
		private final ByteArrayOutputStream delegate = new ByteArrayOutputStream();
		private boolean closed;
		private int flushes;
		
		@Override
		public void write(int value) {
			this.delegate.write(value);
		}
		
		@Override
		public void write(byte[] buffer, int offset, int length) {
			this.delegate.write(buffer, offset, length);
		}
		
		@Override
		public void flush() {
			this.flushes++;
		}
		
		@Override
		public void close() {
			this.closed = true;
		}
	}
	
	private static final class SlowStream extends InputStream {
		
		private final byte[] content;
		private final int maximum;
		private int position;
		
		private SlowStream(byte[] content, int maximum) {
			this.content = content;
			this.maximum = maximum;
		}
		
		@Override
		public int read() {
			return this.position >= this.content.length ? -1 : this.content[this.position++] & 0xFF;
		}
		
		@Override
		public int read(byte[] buffer, int offset, int length) {
			if (this.position >= this.content.length) {
				return -1;
			}
			
			int read = Math.min(Math.min(this.maximum, length), this.content.length - this.position);
			System.arraycopy(this.content, this.position, buffer, offset, read);
			this.position += read;
			return read;
		}
	}
}
