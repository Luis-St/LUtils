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

import net.luis.utils.crypto.algorithm.NativeSignatureAlgorithm;
import net.luis.utils.crypto.algorithm.SignatureAlgorithm;
import net.luis.utils.crypto.exception.AuthenticationException;
import net.luis.utils.crypto.exception.CryptoException;
import net.luis.utils.crypto.util.CryptoBytes;
import net.luis.utils.crypto.util.CryptoRandom;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.*;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Verifier}.<br>
 *
 * @author Luis-St
 */
class VerifierTest {
	
	private static final byte[] DATA = "the quick brown fox".getBytes(StandardCharsets.UTF_8);
	
	private static final Path DIRECTORY = Path.of("VerifierTest-files");
	private static final Path FILE = DIRECTORY.resolve("content.bin");
	private static final Path EMPTY_FILE = DIRECTORY.resolve("empty.bin");
	private static final Path LARGE_FILE = DIRECTORY.resolve("large.bin");
	
	private static KeyPair ed25519;
	private static byte[] largeContent;
	private static byte[] signature;
	private static byte[] emptySignature;
	
	@BeforeAll
	static void setUp() throws Exception {
		Providers.installBouncyCastle();
		Files.createDirectories(DIRECTORY);
		Files.write(FILE, DATA);
		Files.write(EMPTY_FILE, new byte[0]);
		
		largeContent = CryptoRandom.bytes(100000);
		Files.write(LARGE_FILE, largeContent);
		ed25519 = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
		signature = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), DATA);
		emptySignature = Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), new byte[0]);
	}
	
	@AfterAll
	static void cleanUp() throws Exception {
		Files.deleteIfExists(FILE);
		Files.deleteIfExists(EMPTY_FILE);
		Files.deleteIfExists(LARGE_FILE);
		Files.deleteIfExists(DIRECTORY);
	}
	
	private static Verifier verifier() {
		return Signatures.verifier(SignatureAlgorithm.ED25519, ed25519.getPublic());
	}
	
	private static Verifier uninitialised() throws Exception {
		return new Verifier(SignatureAlgorithm.ED25519, Signature.getInstance("Ed25519"));
	}
	
	private static byte[] signatureOf(byte[] content) {
		return Signatures.sign(SignatureAlgorithm.ED25519, ed25519.getPrivate(), content);
	}
	
	@Test
	void constructVerifier() throws Exception {
		Signature instance = Signature.getInstance("Ed25519");
		instance.initVerify(ed25519.getPublic());
		Verifier verifier = new Verifier(SignatureAlgorithm.ED25519, instance);
		
		assertNotNull(verifier);
		assertTrue(verifier.update(DATA).verify(signature));
	}
	
	@Test
	void constructWithNullAlgorithm() throws Exception {
		Signature instance = Signature.getInstance("Ed25519");
		assertEquals("Algorithm must not be null", assertThrows(NullPointerException.class, () -> new Verifier(null, instance)).getMessage());
	}
	
	@Test
	void constructWithNullSignature() {
		assertEquals("Signature must not be null", assertThrows(NullPointerException.class, () -> new Verifier(SignatureAlgorithm.ED25519, null)).getMessage());
	}
	
	@Test
	void constructWithBothNull() {
		assertEquals("Algorithm must not be null", assertThrows(NullPointerException.class, () -> new Verifier(null, null)).getMessage());
	}
	
	@Test
	void updateWithNullByteArray() {
		assertEquals("Data must not be null", assertThrows(NullPointerException.class, () -> verifier().update((byte[]) null)).getMessage());
	}
	
	@Test
	void updateSectionWithNullByteArray() {
		assertEquals("Data must not be null", assertThrows(NullPointerException.class, () -> verifier().update(null, 0, 0)).getMessage());
	}
	
	@Test
	void updateSectionWithNegativeOffset() {
		assertThrows(IndexOutOfBoundsException.class, () -> verifier().update(new byte[4], -1, 2));
	}
	
	@Test
	void updateSectionWithNegativeLength() {
		assertThrows(IndexOutOfBoundsException.class, () -> verifier().update(new byte[4], 0, -1));
	}
	
	@Test
	void updateSectionBeyondArrayEnd() {
		assertThrows(IndexOutOfBoundsException.class, () -> verifier().update(new byte[4], 2, 3));
		assertThrows(IndexOutOfBoundsException.class, () -> verifier().update(new byte[4], 5, 0));
	}
	
	@Test
	void updateWithNullBuffer() {
		assertEquals("Buffer must not be null", assertThrows(NullPointerException.class, () -> verifier().update((ByteBuffer) null)).getMessage());
	}
	
	@Test
	void updateWithNullString() {
		assertEquals("Data must not be null", assertThrows(NullPointerException.class, () -> verifier().update(null, StandardCharsets.UTF_8)).getMessage());
	}
	
	@Test
	void updateWithNullCharset() {
		assertEquals("Charset must not be null", assertThrows(NullPointerException.class, () -> verifier().update("x", null)).getMessage());
	}
	
	@Test
	void updateStringWithBothNull() {
		assertEquals("Data must not be null", assertThrows(NullPointerException.class, () -> verifier().update(null, null)).getMessage());
	}
	
	@Test
	void updateWithNullInputStream() {
		assertEquals("Input must not be null", assertThrows(NullPointerException.class, () -> verifier().update((InputStream) null)).getMessage());
	}
	
	@Test
	void updateWithNullFile() {
		assertEquals("File must not be null", assertThrows(NullPointerException.class, () -> verifier().update((Path) null)).getMessage());
	}
	
	@Test
	void updateWithMissingFile() {
		Path missing = DIRECTORY.resolve("missing.bin");
		UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> verifier().update(missing));
		
		assertTrue(exception.getMessage().contains(missing.toString()));
		assertTrue(exception.getMessage().contains("to verify"));
		assertInstanceOf(NoSuchFileException.class, exception.getCause());
	}
	
	@Test
	void updateWithDirectoryAsFile() {
		assertThrows(UncheckedIOException.class, () -> verifier().update(DIRECTORY));
	}
	
	@Test
	void updateWithFailingStream() {
		IOException failure = new IOException("broken");
		UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> verifier().update(new FailingStream(0, failure)));
		
		assertEquals("Failed to read the stream to verify", exception.getMessage());
		assertSame(failure, exception.getCause());
	}
	
	@Test
	void updateWithUninitialisedSignature() {
		for (Executable call : new Executable[] { () -> uninitialised().update(DATA), () -> uninitialised().update(DATA, 0, 4), () -> uninitialised().update(ByteBuffer.wrap(DATA)) }) {
			CryptoException exception = assertThrows(CryptoException.class, call);
			assertTrue(exception.getMessage().contains("Cannot update the verifier for"));
			assertTrue(exception.getMessage().contains("Ed25519"));
			assertInstanceOf(SignatureException.class, exception.getCause());
		}
	}
	
	@Test
	void verifyWithNullSignature() {
		assertEquals("Signature must not be null", assertThrows(NullPointerException.class, () -> verifier().update(DATA).verify(null)).getMessage());
	}
	
	@Test
	void requireWithNullSignature() {
		assertThrows(NullPointerException.class, () -> verifier().update(DATA).require(null));
	}
	
	@Test
	void verifyWithUninitialisedSignature() throws Exception {
		assertFalse(assertDoesNotThrow(() -> uninitialised().verify(signature)));
	}
	
	@Test
	void requireWithWrongSignature() {
		AuthenticationException exception = assertThrows(AuthenticationException.class, () -> verifier().update(DATA).require(new byte[64]));
		assertTrue(exception.getMessage().contains("Signature verification failed for"));
		assertTrue(exception.getMessage().contains("Ed25519"));
		assertInstanceOf(CryptoException.class, exception);
	}
	
	@Test
	void requireWithMalformedSignature() {
		AuthenticationException exception = assertThrows(AuthenticationException.class, () -> verifier().update(DATA).require(new byte[] { 1, 2, 3 }));
		assertInstanceOf(CryptoException.class, exception);
	}
	
	@Test
	void updateWithEmptyStream() {
		assertTrue(verifier().update(new ByteArrayInputStream(new byte[0])).verify(emptySignature));
	}
	
	@Test
	void updateWithSingleReadStream() {
		byte[] content = CryptoRandom.bytes(10);
		assertTrue(verifier().update(new ByteArrayInputStream(content)).verify(signatureOf(content)));
	}
	
	@Test
	void updateWithMultiReadStream() {
		byte[] content = CryptoRandom.bytes(20000);
		assertTrue(verifier().update(new ByteArrayInputStream(content)).verify(signatureOf(content)));
	}
	
	@Test
	void updateStreamAtBufferBoundaries() {
		for (int size : new int[] { 8191, 8192, 8193 }) {
			byte[] content = CryptoRandom.bytes(size);
			assertTrue(verifier().update(new ByteArrayInputStream(content)).verify(signatureOf(content)));
		}
	}
	
	@Test
	void updateWithEmptyByteArray() {
		assertTrue(verifier().update(new byte[0]).verify(emptySignature));
	}
	
	@Test
	void updateWithEmptySection() {
		assertTrue(verifier().update(new byte[4], 0, 0).verify(emptySignature));
		assertTrue(verifier().update(new byte[4], 4, 0).verify(emptySignature));
	}
	
	@Test
	void updateWithEmptyBuffer() {
		ByteBuffer buffer = ByteBuffer.wrap(new byte[4]);
		buffer.position(4);
		assertTrue(verifier().update(buffer).verify(emptySignature));
	}
	
	@Test
	void updateWithEmptyString() {
		assertTrue(verifier().update("", StandardCharsets.UTF_8).verify(emptySignature));
	}
	
	@Test
	void updateWithEmptyFile() {
		assertTrue(verifier().update(EMPTY_FILE).verify(emptySignature));
	}
	
	@Test
	void updateWithInitialisedSignature() {
		Verifier verifier = verifier();
		assertDoesNotThrow(() -> verifier.update(DATA));
		assertDoesNotThrow(() -> verifier.update(DATA, 0, 2));
		assertDoesNotThrow(() -> verifier.update(ByteBuffer.wrap(DATA)));
		assertDoesNotThrow(() -> verifier.update("x", StandardCharsets.UTF_8));
		assertDoesNotThrow(() -> verifier.update(new ByteArrayInputStream(DATA)));
		assertDoesNotThrow(() -> verifier.update(FILE));
		assertDoesNotThrow(() -> verifier.verify(signature));
	}
	
	@Test
	void verifyWithCorrectSignature() {
		assertTrue(verifier().update(DATA).verify(signature));
	}
	
	@Test
	void verifyWithWrongSignature() {
		byte[] flipped = signature.clone();
		flipped[0] ^= 1;
		assertFalse(verifier().update(DATA).verify(flipped));
	}
	
	@Test
	void verifyWithMalformedSignature() {
		assertFalse(assertDoesNotThrow(() -> verifier().update(DATA).verify(new byte[] { 1, 2, 3 })));
		assertFalse(assertDoesNotThrow(() -> verifier().update(DATA).verify(CryptoRandom.bytes(64))));
	}
	
	@Test
	void verifyWithEmptySignature() {
		assertFalse(assertDoesNotThrow(() -> verifier().update(DATA).verify(new byte[0])));
	}
	
	@Test
	void requireWithCorrectSignature() {
		assertDoesNotThrow(() -> verifier().update(DATA).require(signature));
	}
	
	@Test
	void updateByteArray() {
		assertTrue(verifier().update(DATA).verify(signature));
	}
	
	@Test
	void updateSection() {
		assertTrue(verifier().update(new byte[] { 1, 2, 3, 4, 5 }, 1, 3).verify(signatureOf(new byte[] { 2, 3, 4 })));
	}
	
	@Test
	void updateEntireArrayAsSection() {
		assertTrue(verifier().update(DATA, 0, DATA.length).verify(signature));
	}
	
	@Test
	void updateBuffer() {
		assertTrue(verifier().update(ByteBuffer.wrap(DATA)).verify(signature));
	}
	
	@Test
	void updateBufferConsumesRemaining() {
		byte[] content = CryptoRandom.bytes(10);
		ByteBuffer buffer = ByteBuffer.wrap(content);
		buffer.position(4);
		
		assertTrue(verifier().update(buffer).verify(signatureOf(Arrays.copyOfRange(content, 4, 10))));
		assertEquals(0, buffer.remaining());
	}
	
	@Test
	void updateString() {
		assertTrue(verifier().update("abc", StandardCharsets.UTF_8).verify(signatureOf("abc".getBytes(StandardCharsets.UTF_8))));
	}
	
	@Test
	void updateStringWithDifferentCharsets() {
		assertTrue(verifier().update("äöü", StandardCharsets.UTF_8).verify(signatureOf("äöü".getBytes(StandardCharsets.UTF_8))));
		assertTrue(verifier().update("äöü", StandardCharsets.UTF_16).verify(signatureOf("äöü".getBytes(StandardCharsets.UTF_16))));
		assertFalse(verifier().update("äöü", StandardCharsets.UTF_16).verify(signatureOf("äöü".getBytes(StandardCharsets.UTF_8))));
	}
	
	@Test
	void updateStream() {
		assertTrue(verifier().update(new ByteArrayInputStream(DATA)).verify(signature));
	}
	
	@Test
	void updateStreamDoesNotCloseIt() throws Exception {
		RecordingStream stream = new RecordingStream(DATA);
		assertTrue(verifier().update(stream).verify(signature));
		
		assertFalse(stream.closed);
		assertEquals(-1, stream.read());
	}
	
	@Test
	void updateFile() {
		assertTrue(verifier().update(FILE).verify(signature));
	}
	
	@Test
	void updateFileClosesTheStream() throws Exception {
		Path temporary = DIRECTORY.resolve("closed.bin");
		Files.write(temporary, DATA);
		
		assertTrue(verifier().update(temporary).verify(signature));
		assertTrue(verifier().update(temporary).verify(signature));
		assertTrue(Files.deleteIfExists(temporary));
	}
	
	@Test
	void updateReturnsThisForChaining() {
		Verifier verifier = verifier();
		
		assertSame(verifier, verifier.update(DATA));
		assertSame(verifier, verifier.update(DATA, 0, 2));
		assertSame(verifier, verifier.update(ByteBuffer.wrap(DATA)));
		assertSame(verifier, verifier.update("x", StandardCharsets.UTF_8));
		assertSame(verifier, verifier.update(new ByteArrayInputStream(DATA)));
		assertSame(verifier, verifier.update(FILE));
	}
	
	@Test
	void chainedUpdatesMatchConcatenation() {
		byte[] first = CryptoRandom.bytes(5);
		byte[] second = CryptoRandom.bytes(7);
		byte[] third = CryptoRandom.bytes(11);
		
		assertTrue(verifier().update(first).update(second).update(third).verify(signatureOf(CryptoBytes.concat(first, second, third))));
	}
	
	@Test
	void mixedUpdateTypesMatchManualEncoding() {
		byte[] header = "header".getBytes(StandardCharsets.UTF_8);
		byte[] body = CryptoRandom.bytes(10);
		byte[] manual = CryptoBytes.concat(header, Arrays.copyOf(body, 4), DATA);
		
		assertTrue(verifier().update(header).update(body, 0, 4).update(FILE).verify(signatureOf(manual)));
	}
	
	@Test
	void verifyResetsVerifier() {
		Verifier verifier = verifier().update(DATA);
		
		assertTrue(verifier.verify(signature));
		assertFalse(verifier.verify(signature));
		assertTrue(verifier().update(new byte[0]).verify(emptySignature));
	}
	
	@Test
	void verifierReuseAfterVerify() {
		byte[] first = CryptoRandom.bytes(8);
		byte[] second = CryptoRandom.bytes(8);
		Verifier verifier = verifier();
		
		assertTrue(verifier.update(first).verify(signatureOf(first)));
		assertTrue(verifier.update(second).verify(signatureOf(second)));
	}
	
	@Test
	void verifierResetsAfterFailedVerify() {
		Verifier verifier = verifier().update(DATA);
		
		assertFalse(verifier.verify(new byte[64]));
		assertFalse(verifier.verify(signature));
		assertTrue(verifier.update(DATA).verify(signature));
	}
	
	@Test
	void verifyRejectsTamperedMessage() {
		byte[] other = DATA.clone();
		other[0] ^= 1;
		assertFalse(verifier().update(other).verify(signature));
	}
	
	@Test
	void verifyRejectsWrongKey() {
		KeyPair other = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
		assertFalse(Signatures.verifier(SignatureAlgorithm.ED25519, other.getPublic()).update(DATA).verify(signature));
	}
	
	@Test
	void verifyRejectsSignatureFromDifferentAlgorithm() {
		KeyPair ecdsa = Signatures.generateKeyPair(SignatureAlgorithm.ECDSA_P256_SHA_256);
		assertFalse(assertDoesNotThrow(() -> Signatures.verifier(SignatureAlgorithm.ECDSA_P256_SHA_256, ecdsa.getPublic()).update(DATA).verify(signature)));
	}
	
	@Test
	void verifyAcceptsForEveryClassicalAlgorithm() {
		for (NativeSignatureAlgorithm algorithm : new NativeSignatureAlgorithm[] { SignatureAlgorithm.ED25519, SignatureAlgorithm.ED448, SignatureAlgorithm.ECDSA_P256_SHA_256, SignatureAlgorithm.ECDSA_P384_SHA_384, SignatureAlgorithm.ECDSA_P521_SHA_512 }) {
			KeyPair pair = Signatures.generateKeyPair(algorithm);
			byte[] own = Signatures.signer(algorithm, pair.getPrivate()).update(DATA).sign();
			byte[] other = DATA.clone();
			other[0] ^= 1;
			
			assertTrue(Signatures.verifier(algorithm, pair.getPublic()).update(DATA).verify(own), algorithm.name());
			assertFalse(Signatures.verifier(algorithm, pair.getPublic()).update(other).verify(own), algorithm.name());
		}
	}
	
	@Test
	void requireAndVerifyAgreeAcrossInputs() {
		byte[] flipped = signature.clone();
		flipped[0] ^= 1;
		
		for (byte[] candidate : new byte[][] { signature, flipped, new byte[0], { 1, 2, 3 } }) {
			if (verifier().update(DATA).verify(candidate)) {
				assertDoesNotThrow(() -> verifier().update(DATA).require(candidate));
			} else {
				assertThrows(AuthenticationException.class, () -> verifier().update(DATA).require(candidate));
			}
		}
	}
	
	@Test
	void verifyLargeFile() {
		assertTrue(verifier().update(LARGE_FILE).verify(signatureOf(largeContent)));
	}
	
	@Test
	void verifyDoesNotMutateInputs() {
		byte[] data = DATA.clone();
		byte[] candidate = signature.clone();
		byte[] bufferContent = CryptoRandom.bytes(10);
		byte[] bufferCopy = bufferContent.clone();
		
		verifier().update(data).update(ByteBuffer.wrap(bufferContent)).verify(candidate);
		assertArrayEquals(DATA, data);
		assertArrayEquals(signature, candidate);
		assertArrayEquals(bufferCopy, bufferContent);
	}
	
	@Test
	void updateAfterFailedStreamLeavesDirtyState() {
		Verifier verifier = verifier();
		assertThrows(UncheckedIOException.class, () -> verifier.update(new FailingStream(100, new IOException("broken"))));
		
		assertTrue(assertDoesNotThrow(() -> verifier.verify(signatureOf(new byte[100]))));
	}
	
	@Test
	void allInputPathsAgree() {
		assertTrue(verifier().update(DATA).verify(signature));
		assertTrue(verifier().update(new ByteArrayInputStream(DATA)).verify(signature));
		assertTrue(verifier().update(FILE).verify(signature));
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
