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
 * Test class for {@link Signer}.<br>
 *
 * @author Luis-St
 */
class SignerTest {
	
	private static final byte[] DATA = "the quick brown fox".getBytes(StandardCharsets.UTF_8);
	
	private static final Path DIRECTORY = Path.of("SignerTest-files");
	private static final Path FILE = DIRECTORY.resolve("content.bin");
	private static final Path EMPTY_FILE = DIRECTORY.resolve("empty.bin");
	private static final Path LARGE_FILE = DIRECTORY.resolve("large.bin");
	
	private static KeyPair ed25519;
	private static byte[] largeContent;
	
	@BeforeAll
	static void setUp() throws Exception {
		Providers.installBouncyCastle();
		Files.createDirectories(DIRECTORY);
		Files.write(FILE, DATA);
		Files.write(EMPTY_FILE, new byte[0]);
		
		largeContent = CryptoRandom.bytes(100000);
		Files.write(LARGE_FILE, largeContent);
		ed25519 = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
	}
	
	@AfterAll
	static void cleanUp() throws Exception {
		Files.deleteIfExists(FILE);
		Files.deleteIfExists(EMPTY_FILE);
		Files.deleteIfExists(LARGE_FILE);
		Files.deleteIfExists(DIRECTORY);
	}
	
	private static Signer signer() {
		return Signatures.signer(SignatureAlgorithm.ED25519, ed25519.getPrivate());
	}
	
	private static Signer uninitialised() throws Exception {
		return new Signer(SignatureAlgorithm.ED25519, Signature.getInstance("Ed25519"));
	}
	
	private static byte[] expected(byte[] content) {
		return signer().update(content).sign();
	}
	
	@Test
	void constructSigner() throws Exception {
		Signature signature = Signature.getInstance("Ed25519");
		signature.initSign(ed25519.getPrivate());
		Signer signer = new Signer(SignatureAlgorithm.ED25519, signature);
		
		assertNotNull(signer);
		assertArrayEquals(expected(DATA), signer.update(DATA).sign());
	}
	
	@Test
	void constructWithNullAlgorithm() throws Exception {
		Signature signature = Signature.getInstance("Ed25519");
		assertEquals("Algorithm must not be null", assertThrows(NullPointerException.class, () -> new Signer(null, signature)).getMessage());
	}
	
	@Test
	void constructWithNullSignature() {
		assertEquals("Signature must not be null", assertThrows(NullPointerException.class, () -> new Signer(SignatureAlgorithm.ED25519, null)).getMessage());
	}
	
	@Test
	void constructWithBothNull() {
		assertEquals("Algorithm must not be null", assertThrows(NullPointerException.class, () -> new Signer(null, null)).getMessage());
	}
	
	@Test
	void updateWithNullByteArray() {
		assertEquals("Data must not be null", assertThrows(NullPointerException.class, () -> signer().update((byte[]) null)).getMessage());
	}
	
	@Test
	void updateSectionWithNullByteArray() {
		assertEquals("Data must not be null", assertThrows(NullPointerException.class, () -> signer().update(null, 0, 0)).getMessage());
	}
	
	@Test
	void updateSectionWithNegativeOffset() {
		assertThrows(IndexOutOfBoundsException.class, () -> signer().update(new byte[4], -1, 2));
	}
	
	@Test
	void updateSectionWithNegativeLength() {
		assertThrows(IndexOutOfBoundsException.class, () -> signer().update(new byte[4], 0, -1));
	}
	
	@Test
	void updateSectionBeyondArrayEnd() {
		assertThrows(IndexOutOfBoundsException.class, () -> signer().update(new byte[4], 2, 3));
		assertThrows(IndexOutOfBoundsException.class, () -> signer().update(new byte[4], 5, 0));
	}
	
	@Test
	void updateWithNullBuffer() {
		assertEquals("Buffer must not be null", assertThrows(NullPointerException.class, () -> signer().update((ByteBuffer) null)).getMessage());
	}
	
	@Test
	void updateWithNullString() {
		assertEquals("Data must not be null", assertThrows(NullPointerException.class, () -> signer().update(null, StandardCharsets.UTF_8)).getMessage());
	}
	
	@Test
	void updateWithNullCharset() {
		assertEquals("Charset must not be null", assertThrows(NullPointerException.class, () -> signer().update("x", null)).getMessage());
	}
	
	@Test
	void updateStringWithBothNull() {
		assertEquals("Data must not be null", assertThrows(NullPointerException.class, () -> signer().update(null, null)).getMessage());
	}
	
	@Test
	void updateWithNullInputStream() {
		assertEquals("Input must not be null", assertThrows(NullPointerException.class, () -> signer().update((InputStream) null)).getMessage());
	}
	
	@Test
	void updateWithNullFile() {
		assertEquals("File must not be null", assertThrows(NullPointerException.class, () -> signer().update((Path) null)).getMessage());
	}
	
	@Test
	void updateWithMissingFile() {
		Path missing = DIRECTORY.resolve("missing.bin");
		UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> signer().update(missing));
		
		assertTrue(exception.getMessage().contains(missing.toString()));
		assertTrue(exception.getMessage().contains("to sign"));
		assertInstanceOf(NoSuchFileException.class, exception.getCause());
	}
	
	@Test
	void updateWithDirectoryAsFile() {
		assertThrows(UncheckedIOException.class, () -> signer().update(DIRECTORY));
	}
	
	@Test
	void updateWithFailingStream() {
		IOException failure = new IOException("broken");
		UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> signer().update(new FailingStream(0, failure)));
		
		assertEquals("Failed to read the stream to sign", exception.getMessage());
		assertSame(failure, exception.getCause());
	}
	
	@Test
	void updateWithUninitialisedSignature() throws Exception {
		for (Executable call : new Executable[] { () -> uninitialised().update(DATA), () -> uninitialised().update(DATA, 0, 4), () -> uninitialised().update(ByteBuffer.wrap(DATA)) }) {
			CryptoException exception = assertThrows(CryptoException.class, call);
			assertTrue(exception.getMessage().contains("Cannot update the signer for"));
			assertTrue(exception.getMessage().contains("Ed25519"));
			assertInstanceOf(SignatureException.class, exception.getCause());
		}
	}
	
	@Test
	void signWithUninitialisedSignature() throws Exception {
		CryptoException exception = assertThrows(CryptoException.class, () -> uninitialised().sign());
		
		assertTrue(exception.getMessage().contains("Signing failed for"));
		assertTrue(exception.getMessage().contains("Ed25519"));
		assertInstanceOf(SignatureException.class, exception.getCause());
	}
	
	@Test
	void signWithVerifyInitialisedSignature() throws Exception {
		Signature signature = Signature.getInstance("Ed25519");
		signature.initVerify(ed25519.getPublic());
		
		CryptoException exception = assertThrows(CryptoException.class, () -> new Signer(SignatureAlgorithm.ED25519, signature).sign());
		assertInstanceOf(SignatureException.class, exception.getCause());
	}
	
	@Test
	void updateWithEmptyStream() {
		assertArrayEquals(expected(new byte[0]), signer().update(new ByteArrayInputStream(new byte[0])).sign());
	}
	
	@Test
	void updateWithSingleReadStream() {
		byte[] content = CryptoRandom.bytes(10);
		assertArrayEquals(expected(content), signer().update(new ByteArrayInputStream(content)).sign());
	}
	
	@Test
	void updateWithMultiReadStream() {
		byte[] content = CryptoRandom.bytes(20000);
		assertArrayEquals(expected(content), signer().update(new ByteArrayInputStream(content)).sign());
	}
	
	@Test
	void updateStreamAtBufferBoundaries() {
		for (int size : new int[] { 8191, 8192, 8193 }) {
			byte[] content = CryptoRandom.bytes(size);
			assertArrayEquals(expected(content), signer().update(new ByteArrayInputStream(content)).sign());
		}
	}
	
	@Test
	void updateWithEmptyByteArray() {
		assertArrayEquals(expected(new byte[0]), signer().update(new byte[0]).sign());
	}
	
	@Test
	void updateWithEmptySection() {
		assertArrayEquals(expected(new byte[0]), signer().update(new byte[4], 0, 0).sign());
		assertArrayEquals(expected(new byte[0]), signer().update(new byte[4], 4, 0).sign());
	}
	
	@Test
	void updateWithEmptyBuffer() {
		ByteBuffer buffer = ByteBuffer.wrap(new byte[4]);
		buffer.position(4);
		assertArrayEquals(expected(new byte[0]), signer().update(buffer).sign());
	}
	
	@Test
	void updateWithEmptyString() {
		assertArrayEquals(expected(new byte[0]), signer().update("", StandardCharsets.UTF_8).sign());
	}
	
	@Test
	void updateWithEmptyFile() {
		assertArrayEquals(expected(new byte[0]), signer().update(EMPTY_FILE).sign());
	}
	
	@Test
	void updateWithInitialisedSignature() {
		Signer signer = signer();
		assertDoesNotThrow(() -> signer.update(DATA));
		assertDoesNotThrow(() -> signer.update(DATA, 0, 2));
		assertDoesNotThrow(() -> signer.update(ByteBuffer.wrap(DATA)));
		assertDoesNotThrow(() -> signer.update("x", StandardCharsets.UTF_8));
		assertDoesNotThrow(() -> signer.update(new ByteArrayInputStream(DATA)));
		assertDoesNotThrow(() -> signer.update(FILE));
		assertDoesNotThrow(signer::sign);
	}
	
	@Test
	void signWithInitialisedSignature() {
		byte[] signature = signer().update(DATA).sign();
		assertEquals(64, signature.length);
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), DATA, signature));
	}
	
	@Test
	void updateByteArray() {
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), DATA, signer().update(DATA).sign()));
	}
	
	@Test
	void updateSection() {
		assertArrayEquals(expected(new byte[] { 2, 3, 4 }), signer().update(new byte[] { 1, 2, 3, 4, 5 }, 1, 3).sign());
	}
	
	@Test
	void updateEntireArrayAsSection() {
		assertArrayEquals(expected(DATA), signer().update(DATA, 0, DATA.length).sign());
	}
	
	@Test
	void updateBuffer() {
		assertArrayEquals(expected(DATA), signer().update(ByteBuffer.wrap(DATA)).sign());
	}
	
	@Test
	void updateBufferConsumesRemaining() {
		byte[] content = CryptoRandom.bytes(10);
		ByteBuffer buffer = ByteBuffer.wrap(content);
		buffer.position(4);
		
		assertArrayEquals(expected(Arrays.copyOfRange(content, 4, 10)), signer().update(buffer).sign());
		assertEquals(0, buffer.remaining());
	}
	
	@Test
	void updateString() {
		assertArrayEquals(expected("abc".getBytes(StandardCharsets.UTF_8)), signer().update("abc", StandardCharsets.UTF_8).sign());
	}
	
	@Test
	void updateStringWithDifferentCharsets() {
		byte[] utf8 = signer().update("äöü", StandardCharsets.UTF_8).sign();
		byte[] utf16 = signer().update("äöü", StandardCharsets.UTF_16).sign();
		
		assertFalse(Arrays.equals(utf8, utf16));
		assertArrayEquals(expected("äöü".getBytes(StandardCharsets.UTF_8)), utf8);
		assertArrayEquals(expected("äöü".getBytes(StandardCharsets.UTF_16)), utf16);
	}
	
	@Test
	void updateStream() {
		assertArrayEquals(expected(DATA), signer().update(new ByteArrayInputStream(DATA)).sign());
	}
	
	@Test
	void updateStreamDoesNotCloseIt() throws Exception {
		RecordingStream stream = new RecordingStream(DATA);
		byte[] signature = signer().update(stream).sign();
		
		assertArrayEquals(expected(DATA), signature);
		assertFalse(stream.closed);
		assertEquals(-1, stream.read());
	}
	
	@Test
	void updateFile() {
		assertArrayEquals(expected(DATA), signer().update(FILE).sign());
	}
	
	@Test
	void updateFileClosesTheStream() throws Exception {
		Path temporary = DIRECTORY.resolve("closed.bin");
		Files.write(temporary, DATA);
		
		assertArrayEquals(expected(DATA), signer().update(temporary).sign());
		assertArrayEquals(expected(DATA), signer().update(temporary).sign());
		assertTrue(Files.deleteIfExists(temporary));
	}
	
	@Test
	void updateReturnsThisForChaining() {
		Signer signer = signer();
		
		assertSame(signer, signer.update(DATA));
		assertSame(signer, signer.update(DATA, 0, 2));
		assertSame(signer, signer.update(ByteBuffer.wrap(DATA)));
		assertSame(signer, signer.update("x", StandardCharsets.UTF_8));
		assertSame(signer, signer.update(new ByteArrayInputStream(DATA)));
		assertSame(signer, signer.update(FILE));
	}
	
	@Test
	void chainedUpdatesMatchConcatenation() {
		byte[] first = CryptoRandom.bytes(5);
		byte[] second = CryptoRandom.bytes(7);
		byte[] third = CryptoRandom.bytes(11);
		
		assertArrayEquals(expected(CryptoBytes.concat(first, second, third)), signer().update(first).update(second).update(third).sign());
	}
	
	@Test
	void mixedUpdateTypesMatchManualEncoding() {
		byte[] header = "header".getBytes(StandardCharsets.UTF_8);
		byte[] body = CryptoRandom.bytes(10);
		byte[] manual = CryptoBytes.concat(header, Arrays.copyOf(body, 4), DATA);
		
		assertArrayEquals(expected(manual), signer().update(header).update(body, 0, 4).update(FILE).sign());
	}
	
	@Test
	void signResetsSigner() {
		Signer signer = signer().update(DATA);
		byte[] first = signer.sign();
		byte[] second = signer.sign();
		
		assertFalse(Arrays.equals(first, second));
		assertArrayEquals(expected(new byte[0]), second);
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), new byte[0], second));
		assertFalse(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), DATA, second));
	}
	
	@Test
	void signerReuseAfterSign() {
		byte[] first = CryptoRandom.bytes(8);
		byte[] second = CryptoRandom.bytes(8);
		Signer signer = signer();
		
		byte[] firstSignature = signer.update(first).sign();
		byte[] secondSignature = signer.update(second).sign();
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), second, secondSignature));
		assertFalse(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), first, secondSignature));
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), first, firstSignature));
	}
	
	@Test
	void signProducesVerifiableSignature() {
		for (NativeSignatureAlgorithm algorithm : new NativeSignatureAlgorithm[] { SignatureAlgorithm.ED25519, SignatureAlgorithm.ED448, SignatureAlgorithm.ECDSA_P256_SHA_256, SignatureAlgorithm.ECDSA_P384_SHA_384, SignatureAlgorithm.ECDSA_P521_SHA_512 }) {
			KeyPair pair = Signatures.generateKeyPair(algorithm);
			byte[] signature = Signatures.signer(algorithm, pair.getPrivate()).update(DATA).sign();
			byte[] other = DATA.clone();
			other[0] ^= 1;
			
			assertTrue(Signatures.verifier(algorithm, pair.getPublic()).update(DATA).verify(signature), algorithm.name());
			assertFalse(Signatures.verifier(algorithm, pair.getPublic()).update(other).verify(signature), algorithm.name());
		}
	}
	
	@Test
	void signIsDeterministicForEdDsa() {
		assertArrayEquals(signer().update(DATA).sign(), signer().update(DATA).sign());
		
		KeyPair pair = Signatures.generateKeyPair(SignatureAlgorithm.ECDSA_P256_SHA_256);
		byte[] first = Signatures.signer(SignatureAlgorithm.ECDSA_P256_SHA_256, pair.getPrivate()).update(DATA).sign();
		byte[] second = Signatures.signer(SignatureAlgorithm.ECDSA_P256_SHA_256, pair.getPrivate()).update(DATA).sign();
		assertFalse(Arrays.equals(first, second));
		assertTrue(Signatures.verifier(SignatureAlgorithm.ECDSA_P256_SHA_256, pair.getPublic()).update(DATA).verify(first));
		assertTrue(Signatures.verifier(SignatureAlgorithm.ECDSA_P256_SHA_256, pair.getPublic()).update(DATA).verify(second));
	}
	
	@Test
	void signatureLengthPerAlgorithm() {
		assertEquals(64, signer().update(DATA).sign().length);
		
		KeyPair ed448 = Signatures.generateKeyPair(SignatureAlgorithm.ED448);
		assertEquals(114, Signatures.signer(SignatureAlgorithm.ED448, ed448.getPrivate()).update(DATA).sign().length);
		
		KeyPair ecdsa = Signatures.generateKeyPair(SignatureAlgorithm.ECDSA_P256_SHA_256);
		int length = Signatures.signer(SignatureAlgorithm.ECDSA_P256_SHA_256, ecdsa.getPrivate()).update(DATA).sign().length;
		assertTrue(length >= 68 && length <= 72, "was " + length);
	}
	
	@Test
	void signLargeFile() {
		byte[] signature = signer().update(LARGE_FILE).sign();
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), largeContent, signature));
	}
	
	@Test
	void signDoesNotMutateInputs() {
		byte[] data = DATA.clone();
		byte[] bufferContent = CryptoRandom.bytes(10);
		byte[] bufferCopy = bufferContent.clone();
		String text = "abc";
		
		signer().update(data).update(ByteBuffer.wrap(bufferContent)).update(text, StandardCharsets.UTF_8).sign();
		assertArrayEquals(DATA, data);
		assertArrayEquals(bufferCopy, bufferContent);
		assertEquals("abc", text);
	}
	
	@Test
	void updateAfterFailedStreamLeavesDirtyState() {
		Signer signer = signer();
		assertThrows(UncheckedIOException.class, () -> signer.update(new FailingStream(100, new IOException("broken"))));
		
		byte[] signature = assertDoesNotThrow(signer::sign);
		assertArrayEquals(expected(new byte[100]), signature);
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519, ed25519.getPublic(), new byte[100], signature));
	}
	
	@Test
	void allInputPathsAgree() {
		byte[] fromArray = signer().update(DATA).sign();
		byte[] fromStream = signer().update(new ByteArrayInputStream(DATA)).sign();
		byte[] fromFile = signer().update(FILE).sign();
		
		assertArrayEquals(fromArray, fromStream);
		assertArrayEquals(fromArray, fromFile);
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
