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

import net.luis.utils.crypto.algorithm.MacAlgorithm;
import net.luis.utils.crypto.exception.AuthenticationException;
import net.luis.utils.crypto.exception.CryptoException;
import net.luis.utils.crypto.util.CryptoRandom;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.util.Arrays;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Macs}.<br>
 *
 * @author Luis-St
 */
class MacsTest {
	
	private static final MacAlgorithm ALGORITHM = MacAlgorithm.HMAC_SHA_256;
	private static final byte[] DATA = "the quick brown fox".getBytes(StandardCharsets.UTF_8);
	private static final byte[] RAW_KEY = new byte[32];
	
	private static SecretKey key;
	private static SecretKey otherKey;
	
	@BeforeAll
	static void setUp() {
		Arrays.fill(RAW_KEY, (byte) 0x2A);
		key = Macs.key(ALGORITHM, RAW_KEY);
		otherKey = Macs.generateKey(ALGORITHM);
	}
	
	@Test
	void constructorIsPrivate() throws Exception {
		Constructor<?>[] constructors = Macs.class.getDeclaredConstructors();
		assertEquals(1, constructors.length);
		assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
		assertTrue(Modifier.isFinal(Macs.class.getModifiers()));
		
		Constructor<Macs> constructor = Macs.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		assertNotNull(constructor.newInstance());
	}
	
	@Test
	void generateKeyWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> Macs.generateKey(null));
	}
	
	@Test
	void keyWithNullAlgorithm() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Macs.key(null, RAW_KEY));
		assertEquals("Algorithm must not be null", exception.getMessage());
	}
	
	@Test
	void keyWithNullRaw() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Macs.key(ALGORITHM, null));
		assertEquals("Raw key must not be null", exception.getMessage());
	}
	
	@Test
	void keyWithBothNull() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Macs.key(null, null));
		assertEquals("Algorithm must not be null", exception.getMessage());
	}
	
	@Test
	void keyWithEmptyRaw() {
		CryptoException exception = assertThrows(CryptoException.class, () -> Macs.key(ALGORITHM, new byte[0]));
		assertTrue(exception.getMessage().contains(ALGORITHM.toString()));
		assertTrue(exception.getMessage().contains("empty array"));
	}
	
	@Test
	void macWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> Macs.mac(null, key, DATA));
	}
	
	@Test
	void macWithNullKey() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Macs.mac(ALGORITHM, (SecretKey) null, DATA));
		assertEquals("Key must not be null", exception.getMessage());
	}
	
	@Test
	void macWithNullData() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Macs.mac(ALGORITHM, key, (byte[]) null));
		assertEquals("Data must not be null", exception.getMessage());
	}
	
	@Test
	void macWithNullDataAndNullAlgorithm() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Macs.mac(null, key, (byte[]) null));
		assertEquals("Data must not be null", exception.getMessage());
	}
	
	@Test
	void macWithRawKeyWithEmptyKey() {
		assertThrows(CryptoException.class, () -> Macs.mac(ALGORITHM, new byte[0], DATA));
	}
	
	@Test
	void macWithRawKeyWithNullKey() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Macs.mac(ALGORITHM, (byte[]) null, DATA));
		assertEquals("Raw key must not be null", exception.getMessage());
	}
	
	@Test
	void macStreamWithNullInput() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Macs.mac(ALGORITHM, key, (InputStream) null));
		assertEquals("Input must not be null", exception.getMessage());
	}
	
	@Test
	void macStreamWithNullKey() {
		assertThrows(NullPointerException.class, () -> Macs.mac(ALGORITHM, null, new ByteArrayInputStream(DATA)));
	}
	
	@Test
	void macFailingStream() {
		IOException failure = new IOException("broken");
		UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> Macs.mac(ALGORITHM, key, new FailingStream(failure)));
		
		assertEquals("Failed to read the stream to authenticate", exception.getMessage());
		assertSame(failure, exception.getCause());
	}
	
	@Test
	void verifyWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> Macs.verify(null, key, DATA, new byte[32]));
	}
	
	@Test
	void verifyWithNullKey() {
		assertThrows(NullPointerException.class, () -> Macs.verify(ALGORITHM, null, DATA, new byte[32]));
	}
	
	@Test
	void verifyWithNullData() {
		assertThrows(NullPointerException.class, () -> Macs.verify(ALGORITHM, key, null, new byte[32]));
	}
	
	@Test
	void requireWithNullData() {
		assertThrows(NullPointerException.class, () -> Macs.require(ALGORITHM, key, null, new byte[32]));
	}
	
	@Test
	void requireWithWrongTag() {
		AuthenticationException exception = assertThrows(AuthenticationException.class, () -> Macs.require(ALGORITHM, key, DATA, new byte[32]));
		assertTrue(exception.getMessage().contains(ALGORITHM.toString()));
		assertInstanceOf(CryptoException.class, exception);
	}
	
	@Test
	void initWithNullAlgorithm() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Macs.init(null, key));
		assertEquals("Algorithm must not be null", exception.getMessage());
	}
	
	@Test
	void initWithNullKey() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Macs.init(ALGORITHM, null));
		assertEquals("Key must not be null", exception.getMessage());
	}
	
	@Test
	void initWithBothNull() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Macs.init(null, null));
		assertEquals("Algorithm must not be null", exception.getMessage());
	}
	
	@Test
	void initWithUnusableKey() {
		CryptoException exception = assertThrows(CryptoException.class, () -> Macs.init(ALGORITHM, new UnencodableKey(ALGORITHM.jcaName())));
		
		assertTrue(exception.getMessage().contains("Invalid key for"));
		assertTrue(exception.getMessage().contains(ALGORITHM.toString()));
		assertInstanceOf(InvalidKeyException.class, exception.getCause());
	}
	
	@Test
	void keyWithNonEmptyRaw() {
		byte[] raw = CryptoRandom.bytes(32);
		SecretKey wrapped = Macs.key(ALGORITHM, raw);
		
		assertNotNull(wrapped);
		assertEquals(ALGORITHM.jcaName(), wrapped.getAlgorithm());
		assertArrayEquals(raw, wrapped.getEncoded());
	}
	
	@Test
	void keyWithSingleByteRaw() {
		SecretKey single = assertDoesNotThrow(() -> Macs.key(ALGORITHM, new byte[] { 7 }));
		assertEquals(ALGORITHM.tagLength(), Macs.mac(ALGORITHM, single, DATA).length);
	}
	
	@Test
	void macWithEmptyStream() {
		assertArrayEquals(Macs.mac(ALGORITHM, key, new byte[0]), Macs.mac(ALGORITHM, key, new ByteArrayInputStream(new byte[0])));
	}
	
	@Test
	void macWithSingleReadStream() {
		byte[] content = CryptoRandom.bytes(10);
		assertArrayEquals(Macs.mac(ALGORITHM, key, content), Macs.mac(ALGORITHM, key, new ByteArrayInputStream(content)));
	}
	
	@Test
	void macWithMultiReadStream() {
		byte[] content = CryptoRandom.bytes(20000);
		assertArrayEquals(Macs.mac(ALGORITHM, key, content), Macs.mac(ALGORITHM, key, new ByteArrayInputStream(content)));
	}
	
	@Test
	void macStreamAtBufferBoundaries() {
		for (int size : new int[] { 8191, 8192, 8193 }) {
			byte[] content = CryptoRandom.bytes(size);
			assertArrayEquals(Macs.mac(ALGORITHM, key, content), Macs.mac(ALGORITHM, key, new ByteArrayInputStream(content)));
		}
	}
	
	@Test
	void verifyWithCorrectTag() {
		assertTrue(Macs.verify(ALGORITHM, key, DATA, Macs.mac(ALGORITHM, key, DATA)));
		assertTrue(Macs.verify(ALGORITHM, key, new byte[0], Macs.mac(ALGORITHM, key, new byte[0])));
	}
	
	@Test
	void verifyWithWrongTag() {
		byte[] tag = Macs.mac(ALGORITHM, key, DATA);
		byte[] flipped = tag.clone();
		flipped[0] ^= 1;
		
		assertFalse(Macs.verify(ALGORITHM, key, DATA, flipped));
		assertFalse(Macs.verify(ALGORITHM, key, DATA, Arrays.copyOf(tag, 16)));
		assertFalse(Macs.verify(ALGORITHM, key, DATA, new byte[0]));
	}
	
	@Test
	void verifyWithNullExpectedTag() {
		assertFalse(Macs.verify(ALGORITHM, key, DATA, null));
	}
	
	@Test
	void requireWithCorrectTag() {
		byte[] tag = Macs.mac(ALGORITHM, key, DATA);
		assertDoesNotThrow(() -> Macs.require(ALGORITHM, key, DATA, tag));
	}
	
	@Test
	void requireWithNullExpectedTag() {
		assertThrows(AuthenticationException.class, () -> Macs.require(ALGORITHM, key, DATA, null));
	}
	
	@Test
	void initWithUsableKey() {
		Mac mac = Macs.init(ALGORITHM, key);
		
		assertNotNull(mac);
		assertEquals(ALGORITHM.jcaName(), mac.getAlgorithm());
		assertEquals(ALGORITHM.tagLength(), mac.getMacLength());
	}
	
	@Test
	void generateKeyLength() {
		for (MacAlgorithm algorithm : MacAlgorithm.values()) {
			SecretKey generated = Macs.generateKey(algorithm);
			assertEquals(algorithm.recommendedKeyLength(), generated.getEncoded().length);
			assertEquals(algorithm.jcaName(), generated.getAlgorithm());
		}
	}
	
	@Test
	void generateKeyProducesDifferentKeys() {
		assertFalse(Arrays.equals(Macs.generateKey(ALGORITHM).getEncoded(), Macs.generateKey(ALGORITHM).getEncoded()));
	}
	
	@Test
	void macTagLength() {
		for (MacAlgorithm algorithm : MacAlgorithm.values()) {
			assertEquals(algorithm.tagLength(), Macs.mac(algorithm, Macs.generateKey(algorithm), DATA).length);
		}
	}
	
	@Test
	void macOfEmptyData() {
		byte[] tag = assertDoesNotThrow(() -> Macs.mac(ALGORITHM, key, new byte[0]));
		assertEquals(ALGORITHM.tagLength(), tag.length);
		assertArrayEquals(tag, Macs.mac(ALGORITHM, key, new byte[0]));
	}
	
	@Test
	void macIsDeterministic() {
		assertArrayEquals(Macs.mac(ALGORITHM, key, DATA), Macs.mac(ALGORITHM, key, DATA));
	}
	
	@Test
	void macDiffersForDifferentData() {
		byte[] other = DATA.clone();
		other[0] ^= 1;
		assertFalse(Arrays.equals(Macs.mac(ALGORITHM, key, DATA), Macs.mac(ALGORITHM, key, other)));
	}
	
	@Test
	void macDiffersForDifferentKeys() {
		assertFalse(Arrays.equals(Macs.mac(ALGORITHM, key, DATA), Macs.mac(ALGORITHM, otherKey, DATA)));
	}
	
	@Test
	void macWithRawKeyMatchesWrappedKey() {
		byte[] raw = CryptoRandom.bytes(32);
		assertArrayEquals(Macs.mac(ALGORITHM, Macs.key(ALGORITHM, raw), DATA), Macs.mac(ALGORITHM, raw, DATA));
	}
	
	@Test
	void macWithShortKey() {
		byte[] tag = assertDoesNotThrow(() -> Macs.mac(ALGORITHM, new byte[] { 7 }, DATA));
		assertEquals(ALGORITHM.tagLength(), tag.length);
	}
	
	@Test
	void macWithLongKey() {
		byte[] longKey = CryptoRandom.bytes(200);
		byte[] tag = assertDoesNotThrow(() -> Macs.mac(ALGORITHM, longKey, DATA));
		
		assertEquals(ALGORITHM.tagLength(), tag.length);
		assertFalse(Arrays.equals(tag, Macs.mac(ALGORITHM, new byte[] { 7 }, DATA)));
	}
	
	@Test
	void macStreamDoesNotCloseIt() throws Exception {
		RecordingStream stream = new RecordingStream(DATA);
		byte[] tag = Macs.mac(ALGORITHM, key, stream);
		
		assertArrayEquals(Macs.mac(ALGORITHM, key, DATA), tag);
		assertFalse(stream.closed);
		assertEquals(-1, stream.read());
	}
	
	@Test
	void keyDoesNotAliasRawArray() {
		byte[] raw = CryptoRandom.bytes(32);
		byte[] copy = raw.clone();
		SecretKey wrapped = Macs.key(ALGORITHM, raw);
		
		Arrays.fill(raw, (byte) 0);
		assertArrayEquals(copy, wrapped.getEncoded());
	}
	
	@Test
	void macKnownAnswerVectors() {
		byte[] rfcKey = new byte[20];
		Arrays.fill(rfcKey, (byte) 0x0b);
		byte[] rfcData = "Hi There".getBytes(StandardCharsets.UTF_8);
		
		assertEquals("b0344c61d8db38535ca8afceaf0bf12b881dc200c9833da726e9376c2e32cff7", HexFormat.of().formatHex(Macs.mac(MacAlgorithm.HMAC_SHA_256, rfcKey, rfcData)));
		assertTrue(HexFormat.of().formatHex(Macs.mac(MacAlgorithm.HMAC_SHA_512, rfcKey, rfcData)).startsWith("87aa7cdea5ef619d4ff0b4241a1d6cb0"));
	}
	
	@Test
	void allOverloadsAgreeOnTheSameContent() {
		byte[] fromKey = Macs.mac(ALGORITHM, key, DATA);
		byte[] fromRaw = Macs.mac(ALGORITHM, RAW_KEY, DATA);
		byte[] fromStream = Macs.mac(ALGORITHM, key, new ByteArrayInputStream(DATA));
		
		assertArrayEquals(fromKey, fromRaw);
		assertArrayEquals(fromKey, fromStream);
	}
	
	@Test
	void macForEveryAlgorithm() {
		for (MacAlgorithm algorithm : MacAlgorithm.values()) {
			SecretKey generated = Macs.generateKey(algorithm);
			byte[] tag = Macs.mac(algorithm, generated, DATA);
			
			assertEquals(algorithm.tagLength(), tag.length);
			assertTrue(Macs.verify(algorithm, generated, DATA, tag));
		}
	}
	
	@Test
	void verifyRejectsTagFromDifferentAlgorithm() {
		byte[] tag = Macs.mac(MacAlgorithm.HMAC_SHA3_256, RAW_KEY, DATA);
		
		assertEquals(MacAlgorithm.HMAC_SHA_256.tagLength(), MacAlgorithm.HMAC_SHA3_256.tagLength());
		assertFalse(Macs.verify(MacAlgorithm.HMAC_SHA_256, key, DATA, tag));
	}
	
	@Test
	void verifyRejectsTagFromDifferentKey() {
		assertFalse(Macs.verify(ALGORITHM, key, DATA, Macs.mac(ALGORITHM, otherKey, DATA)));
	}
	
	@Test
	void verifyRejectsTruncatedTag() {
		byte[] tag = Macs.mac(ALGORITHM, key, DATA);
		assertFalse(Macs.verify(ALGORITHM, key, DATA, Arrays.copyOf(tag, 16)));
		assertFalse(Macs.verify(ALGORITHM, key, DATA, Arrays.copyOf(tag, tag.length + 1)));
	}
	
	@Test
	void macDoesNotMutateInputs() {
		byte[] data = DATA.clone();
		byte[] raw = RAW_KEY.clone();
		
		Macs.mac(ALGORITHM, key, data);
		Macs.mac(ALGORITHM, raw, data);
		Macs.mac(ALGORITHM, key, new ByteArrayInputStream(data));
		
		assertArrayEquals(DATA, data);
		assertArrayEquals(RAW_KEY, raw);
	}
	
	@Test
	void macReturnsFreshArrays() {
		byte[] first = Macs.mac(ALGORITHM, key, DATA);
		byte[] second = Macs.mac(ALGORITHM, key, DATA);
		
		assertNotSame(first, second);
		first[0] ^= 1;
		assertArrayEquals(second, Macs.mac(ALGORITHM, key, DATA));
	}
	
	@Test
	void macLargeStream() {
		byte[] content = CryptoRandom.bytes(100000);
		assertArrayEquals(Macs.mac(ALGORITHM, key, content), Macs.mac(ALGORITHM, key, new ByteArrayInputStream(content)));
	}
	
	@Test
	void requireAndVerifyAgreeAcrossInputs() {
		byte[] tag = Macs.mac(ALGORITHM, key, DATA);
		byte[] flipped = tag.clone();
		flipped[0] ^= 1;
		
		for (byte[] candidate : new byte[][] { tag, flipped, Arrays.copyOf(tag, 16), new byte[0] }) {
			if (Macs.verify(ALGORITHM, key, DATA, candidate)) {
				assertDoesNotThrow(() -> Macs.require(ALGORITHM, key, DATA, candidate));
			} else {
				assertThrows(AuthenticationException.class, () -> Macs.require(ALGORITHM, key, DATA, candidate));
			}
		}
	}
	
	@Test
	void keyRejectsEmptyButAcceptsZeroFilled() {
		assertThrows(CryptoException.class, () -> Macs.key(ALGORITHM, new byte[0]));
		
		SecretKey zeroKey = assertDoesNotThrow(() -> Macs.key(ALGORITHM, new byte[32]));
		assertArrayEquals(Macs.mac(ALGORITHM, zeroKey, DATA), Macs.mac(ALGORITHM, zeroKey, DATA));
	}
	
	@Test
	void generateKeyProducesUsableKeys() {
		for (MacAlgorithm algorithm : MacAlgorithm.values()) {
			SecretKey generated = Macs.generateKey(algorithm);
			assertDoesNotThrow(() -> Macs.require(algorithm, generated, DATA, Macs.mac(algorithm, generated, DATA)));
		}
	}
	
	private record UnencodableKey(String algorithm) implements SecretKey {
		
		@Override
		public String getAlgorithm() {
			return this.algorithm;
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
