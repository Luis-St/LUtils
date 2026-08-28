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

import net.luis.utils.crypto.exception.MalformedDataException;
import net.luis.utils.crypto.util.CryptoBytes;
import net.luis.utils.crypto.util.CryptoRandom;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link HybridSignature}.<br>
 *
 * @author Luis-St
 */
class HybridSignatureTest {
	
	@BeforeAll
	static void installProvider() {
		Providers.installBouncyCastle();
	}
	
	private static byte[] withPrefix(int length, byte[] payload) {
		return CryptoBytes.concat(CryptoBytes.of(length), payload);
	}
	
	@Test
	void constructHybridSignature() {
		HybridSignature signature = new HybridSignature(new byte[] { 1, 2 }, new byte[] { 3, 4, 5 });
		assertArrayEquals(new byte[] { 1, 2 }, signature.classical());
		assertArrayEquals(new byte[] { 3, 4, 5 }, signature.postQuantum());
	}
	
	@Test
	void constructWithNullClassical() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> new HybridSignature(null, new byte[1]));
		assertEquals("Classical signature must not be null", exception.getMessage());
	}
	
	@Test
	void constructWithNullPostQuantum() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> new HybridSignature(new byte[1], null));
		assertEquals("Post-quantum signature must not be null", exception.getMessage());
	}
	
	@Test
	void constructWithBothNull() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> new HybridSignature(null, null));
		assertEquals("Classical signature must not be null", exception.getMessage());
	}
	
	@Test
	void constructWithEmptyComponents() {
		HybridSignature signature = assertDoesNotThrow(() -> new HybridSignature(new byte[0], new byte[0]));
		assertEquals(0, signature.classical().length);
		assertEquals(0, signature.postQuantum().length);
	}
	
	@Test
	void parseWithNullSignature() {
		assertThrows(NullPointerException.class, () -> HybridSignature.parse(null));
	}
	
	@Test
	void parseWithEmptyInput() {
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> HybridSignature.parse(new byte[0]));
		assertEquals("Malformed hybrid signature", exception.getMessage());
		assertInstanceOf(BufferUnderflowException.class, exception.getCause());
	}
	
	@Test
	void parseWithTruncatedFirstLengthPrefix() {
		for (int length : new int[] { 1, 2, 3 }) {
			MalformedDataException exception = assertThrows(MalformedDataException.class, () -> HybridSignature.parse(new byte[length]));
			assertInstanceOf(BufferUnderflowException.class, exception.getCause());
		}
	}
	
	@Test
	void parseWithTruncatedFirstComponent() {
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> HybridSignature.parse(withPrefix(10, new byte[4])));
		
		assertInstanceOf(MalformedDataException.class, exception.getCause());
		assertTrue(exception.getCause().getMessage().contains("10"));
		assertTrue(exception.getCause().getMessage().contains("4"));
	}
	
	@Test
	void parseWithTruncatedSecondLengthPrefix() {
		byte[] input = CryptoBytes.concat(CryptoBytes.of(2), new byte[] { 1, 2 }, new byte[] { 0, 0 });
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> HybridSignature.parse(input));
		assertInstanceOf(BufferUnderflowException.class, exception.getCause());
	}
	
	@Test
	void parseWithTruncatedSecondComponent() {
		byte[] input = CryptoBytes.concat(CryptoBytes.of(2), new byte[] { 1, 2 }, CryptoBytes.of(10), new byte[3]);
		assertThrows(MalformedDataException.class, () -> HybridSignature.parse(input));
	}
	
	@Test
	void parseWithNegativeLengthPrefix() {
		for (int length : new int[] { -1, Integer.MIN_VALUE }) {
			MalformedDataException exception = assertThrows(MalformedDataException.class, () -> HybridSignature.parse(withPrefix(length, new byte[8])));
			assertTrue(exception.getCause().getMessage().contains(String.valueOf(length)));
		}
	}
	
	@Test
	void parseWithHugeLengthPrefix() {
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> HybridSignature.parse(withPrefix(Integer.MAX_VALUE, new byte[8])));
		assertTrue(exception.getCause().getMessage().contains(String.valueOf(Integer.MAX_VALUE)));
	}
	
	@Test
	void parseWithNegativeSecondLengthPrefix() {
		byte[] input = CryptoBytes.concat(CryptoBytes.of(2), new byte[] { 1, 2 }, CryptoBytes.of(-1), new byte[4]);
		assertThrows(MalformedDataException.class, () -> HybridSignature.parse(input));
	}
	
	@Test
	void encodeWithNullClassical() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> HybridSignature.encode(null, new byte[1]));
		assertEquals("Classical signature must not be null", exception.getMessage());
	}
	
	@Test
	void encodeWithNullPostQuantum() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> HybridSignature.encode(new byte[1], null));
		assertEquals("Post-quantum signature must not be null", exception.getMessage());
	}
	
	@Test
	void encodeWithBothNull() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> HybridSignature.encode(null, null));
		assertEquals("Classical signature must not be null", exception.getMessage());
	}
	
	@Test
	void parseWithValidEncoding() {
		HybridSignature signature = HybridSignature.parse(HybridSignature.encode(new byte[] { 1, 2 }, new byte[] { 3, 4, 5 }));
		assertArrayEquals(new byte[] { 1, 2 }, signature.classical());
		assertArrayEquals(new byte[] { 3, 4, 5 }, signature.postQuantum());
	}
	
	@Test
	void parseWithEmptyComponents() {
		byte[] encoded = HybridSignature.encode(new byte[0], new byte[0]);
		assertEquals(8, encoded.length);
		
		HybridSignature signature = assertDoesNotThrow(() -> HybridSignature.parse(encoded));
		assertEquals(0, signature.classical().length);
		assertEquals(0, signature.postQuantum().length);
	}
	
	@Test
	void parseWithLengthExactlyMatchingRemaining() {
		byte[] encoded = HybridSignature.encode(new byte[] { 1, 2 }, new byte[] { 3, 4, 5 });
		HybridSignature signature = assertDoesNotThrow(() -> HybridSignature.parse(encoded));
		
		assertEquals(3, signature.postQuantum().length);
		assertEquals(encoded.length, 8 + signature.classical().length + signature.postQuantum().length);
	}
	
	@Test
	void parseIgnoresTrailingBytes() {
		byte[] encoded = CryptoBytes.concat(HybridSignature.encode(new byte[] { 1, 2 }, new byte[] { 3, 4, 5 }), new byte[] { 9, 9, 9, 9, 9 });
		HybridSignature signature = assertDoesNotThrow(() -> HybridSignature.parse(encoded));
		
		assertArrayEquals(new byte[] { 1, 2 }, signature.classical());
		assertArrayEquals(new byte[] { 3, 4, 5 }, signature.postQuantum());
	}
	
	@Test
	void encodeLayout() {
		byte[] encoded = HybridSignature.encode(new byte[] { 1, 2 }, new byte[] { 3, 4, 5 });
		
		assertEquals(13, encoded.length);
		assertEquals(2, ByteBuffer.wrap(encoded, 0, 4).getInt());
		assertArrayEquals(new byte[] { 1, 2 }, Arrays.copyOfRange(encoded, 4, 6));
		assertEquals(3, ByteBuffer.wrap(encoded, 6, 4).getInt());
		assertArrayEquals(new byte[] { 3, 4, 5 }, Arrays.copyOfRange(encoded, 10, 13));
	}
	
	@Test
	void encodeLengthsAreBigEndian() {
		byte[] encoded = HybridSignature.encode(new byte[258], new byte[1]);
		
		assertEquals(258, ByteBuffer.wrap(encoded, 0, 4).getInt());
		assertArrayEquals(new byte[] { 0, 0, 1, 2 }, Arrays.copyOf(encoded, 4));
	}
	
	@Test
	void encodeWithEmptyComponents() {
		byte[] encoded = HybridSignature.encode(new byte[0], new byte[0]);
		assertEquals(8, encoded.length);
		assertArrayEquals(new byte[8], encoded);
	}
	
	@Test
	void encodeWithOneEmptyComponent() {
		byte[] first = HybridSignature.encode(new byte[0], new byte[] { 1 });
		byte[] second = HybridSignature.encode(new byte[] { 1 }, new byte[0]);
		
		assertEquals(9, first.length);
		assertEquals(9, second.length);
		assertEquals(0, ByteBuffer.wrap(first, 0, 4).getInt());
		assertEquals(0, ByteBuffer.wrap(second, 5, 4).getInt());
		assertFalse(Arrays.equals(first, second));
	}
	
	@Test
	void encodeTotalLength() {
		for (int[] sizes : new int[][] { { 0, 0 }, { 1, 1 }, { 64, 3309 }, { 2614, 158 } }) {
			assertEquals(8 + sizes[0] + sizes[1], HybridSignature.encode(new byte[sizes[0]], new byte[sizes[1]]).length);
		}
	}
	
	@Test
	void encodeReturnsFreshArrays() {
		byte[] first = HybridSignature.encode(new byte[] { 1 }, new byte[] { 2 });
		byte[] second = HybridSignature.encode(new byte[] { 1 }, new byte[] { 2 });
		
		assertNotSame(first, second);
		assertArrayEquals(first, second);
		first[0] ^= 1;
		assertFalse(Arrays.equals(first, second));
	}
	
	@Test
	void encodeDoesNotMutateInputs() {
		byte[] classical = { 1, 2 };
		byte[] postQuantum = { 3, 4, 5 };
		HybridSignature.encode(classical, postQuantum);
		
		assertArrayEquals(new byte[] { 1, 2 }, classical);
		assertArrayEquals(new byte[] { 3, 4, 5 }, postQuantum);
	}
	
	@Test
	void parseReturnsIndependentArrays() {
		byte[] encoded = HybridSignature.encode(new byte[] { 1, 2 }, new byte[] { 3, 4, 5 });
		HybridSignature signature = HybridSignature.parse(encoded);
		
		Arrays.fill(encoded, (byte) 9);
		assertArrayEquals(new byte[] { 1, 2 }, signature.classical());
		assertArrayEquals(new byte[] { 3, 4, 5 }, signature.postQuantum());
	}
	
	@Test
	void accessorsReturnLiveArrays() {
		byte[] classical = { 1, 2 };
		byte[] postQuantum = { 3, 4, 5 };
		HybridSignature signature = new HybridSignature(classical, postQuantum);
		
		assertSame(classical, signature.classical());
		assertSame(postQuantum, signature.postQuantum());
	}
	
	@Test
	void encodeParseRoundTrip() {
		for (int[] sizes : new int[][] { { 0, 0 }, { 1, 1 }, { 64, 1974 }, { 2614, 158 } }) {
			byte[] classical = CryptoRandom.bytes(sizes[0]);
			byte[] postQuantum = CryptoRandom.bytes(sizes[1]);
			HybridSignature parsed = HybridSignature.parse(HybridSignature.encode(classical, postQuantum));
			
			assertArrayEquals(classical, parsed.classical());
			assertArrayEquals(postQuantum, parsed.postQuantum());
		}
	}
	
	@Test
	void roundTripAtTheSlhDsaHybridSize() {
		byte[] classical = CryptoRandom.bytes(64);
		byte[] postQuantum = CryptoRandom.bytes(7856);
		byte[] encoded = HybridSignature.encode(classical, postQuantum);
		
		assertEquals(7928, encoded.length);
		HybridSignature parsed = HybridSignature.parse(encoded);
		assertArrayEquals(classical, parsed.classical());
		assertArrayEquals(postQuantum, parsed.postQuantum());
	}
	
	@Test
	void roundTripWithRealisticSignatureSizes() {
		byte[] classical = CryptoRandom.bytes(64);
		byte[] postQuantum = CryptoRandom.bytes(3309);
		byte[] encoded = HybridSignature.encode(classical, postQuantum);
		
		assertEquals(8 + 64 + 3309, encoded.length);
		HybridSignature parsed = HybridSignature.parse(encoded);
		assertArrayEquals(classical, parsed.classical());
		assertArrayEquals(postQuantum, parsed.postQuantum());
	}
	
	@Test
	void encodeDistinguishesSwappedComponents() {
		byte[] first = { 1, 2, 3 };
		byte[] second = { 4, 5, 6 };
		
		assertFalse(Arrays.equals(HybridSignature.encode(first, second), HybridSignature.encode(second, first)));
		assertArrayEquals(first, HybridSignature.parse(HybridSignature.encode(first, second)).classical());
		assertArrayEquals(second, HybridSignature.parse(HybridSignature.encode(second, first)).classical());
	}
	
	@Test
	void encodeDistinguishesSplitPoint() {
		byte[] first = HybridSignature.encode(new byte[] { 1, 2, 3 }, new byte[] { 4 });
		byte[] second = HybridSignature.encode(new byte[] { 1, 2 }, new byte[] { 3, 4 });
		
		assertFalse(Arrays.equals(first, second));
		assertArrayEquals(new byte[] { 1, 2, 3 }, HybridSignature.parse(first).classical());
		assertArrayEquals(new byte[] { 1, 2 }, HybridSignature.parse(second).classical());
	}
	
	@Test
	void equalsIsIdentityBasedForArrayComponents() {
		byte[] classical = { 1, 2 };
		byte[] postQuantum = { 3, 4 };
		HybridSignature first = new HybridSignature(classical, postQuantum);
		
		assertNotEquals(new HybridSignature(classical.clone(), postQuantum.clone()), first);
		assertEquals(first, first);
		assertEquals(new HybridSignature(classical, postQuantum), first);
	}
	
	@Test
	void toStringDoesNotShowComponentContents() {
		byte[] classical = { 1, 2 };
		byte[] postQuantum = { 3, 4, 5 };
		String rendered = new HybridSignature(classical, postQuantum).toString();
		
		assertTrue(rendered.startsWith("HybridSignature["));
		assertFalse(rendered.contains(Arrays.toString(classical)));
		assertFalse(rendered.contains(HexFormat.of().formatHex(postQuantum)));
	}
	
	@Test
	void parseRejectsAllTruncationsOfAValidEncoding() {
		byte[] encoded = HybridSignature.encode(new byte[] { 1, 2 }, new byte[] { 3, 4, 5 });
		for (int length = 0; length < encoded.length; length++) {
			byte[] prefix = Arrays.copyOf(encoded, length);
			assertThrows(MalformedDataException.class, () -> HybridSignature.parse(prefix), "prefix length " + length);
		}
		assertDoesNotThrow(() -> HybridSignature.parse(encoded));
	}
	
	@Test
	void parseRejectsCorruptedLengthPrefixes() {
		byte[] encoded = HybridSignature.encode(new byte[] { 1, 2 }, new byte[] { 3, 4, 5 });
		for (int index = 0; index < 4; index++) {
			byte[] corrupted = encoded.clone();
			corrupted[index] = (byte) 0xFF;
			
			int length = ByteBuffer.wrap(corrupted, 0, 4).getInt();
			if (length < 0 || length > corrupted.length - 4) {
				assertThrows(MalformedDataException.class, () -> HybridSignature.parse(corrupted), "index " + index);
			}
		}
	}
	
	@Test
	void parseIsDeterministic() {
		byte[] encoded = HybridSignature.encode(new byte[] { 1, 2 }, new byte[] { 3, 4, 5 });
		
		assertArrayEquals(HybridSignature.parse(encoded).classical(), HybridSignature.parse(encoded).classical());
		assertArrayEquals(HybridSignature.parse(encoded).postQuantum(), HybridSignature.parse(encoded).postQuantum());
	}
}
