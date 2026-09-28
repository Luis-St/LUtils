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

package net.luis.utils.crypto.util;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CryptoBytes}.<br>
 *
 * @author Luis-St
 */
class CryptoBytesTest {
	
	@Test
	void constructorIsPrivate() throws Exception {
		Constructor<?>[] constructors = CryptoBytes.class.getDeclaredConstructors();
		assertEquals(1, constructors.length);
		assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
		assertTrue(Modifier.isFinal(CryptoBytes.class.getModifiers()));
		
		Constructor<CryptoBytes> constructor = CryptoBytes.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		assertNotNull(constructor.newInstance());
	}
	
	@Test
	void concatWithNullParts() {
		assertThrows(NullPointerException.class, () -> CryptoBytes.concat((byte[][]) null));
	}
	
	@Test
	void concatWithNullPart() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> CryptoBytes.concat(new byte[] { 1 }, null, new byte[] { 2 }));
		assertEquals("Part must not be null", exception.getMessage());
	}
	
	@Test
	void concatWithOnlyNullPart() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> CryptoBytes.concat((byte[]) null));
		assertEquals("Part must not be null", exception.getMessage());
	}
	
	@Test
	void xorWithNullFirst() {
		assertThrows(NullPointerException.class, () -> CryptoBytes.xor(null, new byte[1]));
	}
	
	@Test
	void xorWithNullSecond() {
		assertThrows(NullPointerException.class, () -> CryptoBytes.xor(new byte[1], null));
	}
	
	@Test
	void xorWithBothNull() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> CryptoBytes.xor(null, null));
		assertEquals("First array must not be null", exception.getMessage());
	}
	
	@Test
	void xorWithDifferentLengths() {
		IllegalArgumentException first = assertThrows(IllegalArgumentException.class, () -> CryptoBytes.xor(new byte[3], new byte[4]));
		assertTrue(first.getMessage().contains("3"));
		assertTrue(first.getMessage().contains("4"));
		
		IllegalArgumentException second = assertThrows(IllegalArgumentException.class, () -> CryptoBytes.xor(new byte[4], new byte[3]));
		assertTrue(second.getMessage().contains("4"));
		assertTrue(second.getMessage().contains("3"));
	}
	
	@Test
	void xorWithOneEmptyArray() {
		assertThrows(IllegalArgumentException.class, () -> CryptoBytes.xor(new byte[0], new byte[1]));
		assertThrows(IllegalArgumentException.class, () -> CryptoBytes.xor(new byte[1], new byte[0]));
	}
	
	@Test
	void sliceWithNullData() {
		assertThrows(NullPointerException.class, () -> CryptoBytes.slice(null, 0, 0));
	}
	
	@Test
	void sliceWithNegativeOffset() {
		assertThrows(IndexOutOfBoundsException.class, () -> CryptoBytes.slice(new byte[4], -1, 2));
	}
	
	@Test
	void sliceWithNegativeLength() {
		assertThrows(IndexOutOfBoundsException.class, () -> CryptoBytes.slice(new byte[4], 0, -1));
	}
	
	@Test
	void sliceBeyondArrayEnd() {
		assertThrows(IndexOutOfBoundsException.class, () -> CryptoBytes.slice(new byte[4], 2, 3));
		assertThrows(IndexOutOfBoundsException.class, () -> CryptoBytes.slice(new byte[4], 5, 0));
	}
	
	@Test
	void toBytesWithNullChars() {
		assertThrows(NullPointerException.class, () -> CryptoBytes.toBytes(null, StandardCharsets.UTF_8));
	}
	
	@Test
	void toBytesWithNullCharset() {
		assertThrows(NullPointerException.class, () -> CryptoBytes.toBytes(new char[0], null));
	}
	
	@Test
	void toBytesWithBothNull() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> CryptoBytes.toBytes(null, null));
		assertEquals("Chars must not be null", exception.getMessage());
	}
	
	@Test
	void equalsConstantTimeWithBothNull() {
		assertTrue(CryptoBytes.equalsConstantTime(null, null));
	}
	
	@Test
	void equalsConstantTimeWithOneNull() {
		assertFalse(CryptoBytes.equalsConstantTime(null, new byte[0]));
		assertFalse(CryptoBytes.equalsConstantTime(new byte[0], null));
		assertFalse(CryptoBytes.equalsConstantTime(null, new byte[] { 1, 2, 3 }));
		assertFalse(CryptoBytes.equalsConstantTime(new byte[] { 1, 2, 3 }, null));
	}
	
	@Test
	void equalsConstantTimeWithEqualArrays() {
		assertTrue(CryptoBytes.equalsConstantTime(new byte[] { 1, 2, 3 }, new byte[] { 1, 2, 3 }));
		assertTrue(CryptoBytes.equalsConstantTime(new byte[0], new byte[0]));
		assertTrue(CryptoBytes.equalsConstantTime(CryptoBytes.EMPTY, new byte[0]));
	}
	
	@Test
	void equalsConstantTimeWithDifferentContents() {
		assertFalse(CryptoBytes.equalsConstantTime(new byte[] { 1, 2, 3 }, new byte[] { 1, 2, 4 }));
		assertFalse(CryptoBytes.equalsConstantTime(new byte[] { 1, 2, 3 }, new byte[] { 9, 2, 3 }));
	}
	
	@Test
	void equalsConstantTimeWithDifferentLengths() {
		assertFalse(CryptoBytes.equalsConstantTime(new byte[] { 1, 2 }, new byte[] { 1, 2, 3 }));
		assertFalse(CryptoBytes.equalsConstantTime(new byte[0], new byte[] { 1 }));
	}
	
	@Test
	void concatWithNoParts() {
		byte[] result = CryptoBytes.concat();
		assertNotNull(result);
		assertEquals(0, result.length);
	}
	
	@Test
	void concatWithSinglePart() {
		byte[] part = { 1, 2 };
		byte[] result = CryptoBytes.concat(part);
		assertArrayEquals(new byte[] { 1, 2 }, result);
		assertNotSame(part, result);
	}
	
	@Test
	void concatWithMultipleParts() {
		byte[] result = CryptoBytes.concat(new byte[] { 1 }, new byte[] { 2, 3 }, new byte[] { 4 });
		assertArrayEquals(new byte[] { 1, 2, 3, 4 }, result);
	}
	
	@Test
	void concatWithEmptyParts() {
		assertArrayEquals(new byte[] { 1 }, CryptoBytes.concat(new byte[0], new byte[] { 1 }, new byte[0]));
		assertEquals(0, CryptoBytes.concat(new byte[0], new byte[0]).length);
	}
	
	@Test
	void xorWithEmptyArrays() {
		byte[] result = CryptoBytes.xor(new byte[0], new byte[0]);
		assertNotNull(result);
		assertEquals(0, result.length);
	}
	
	@Test
	void xorWithSingleByte() {
		assertArrayEquals(new byte[] { (byte) 0xFF }, CryptoBytes.xor(new byte[] { 0x0F }, new byte[] { (byte) 0xF0 }));
	}
	
	@Test
	void xorWithMultipleBytes() {
		assertArrayEquals(new byte[] { 2, 0, 2 }, CryptoBytes.xor(new byte[] { 1, 2, 3 }, new byte[] { 3, 2, 1 }));
	}
	
	@Test
	void sliceEntireArray() {
		byte[] data = { 1, 2, 3, 4, 5 };
		byte[] result = CryptoBytes.slice(data, 0, data.length);
		assertArrayEquals(data, result);
		assertNotSame(data, result);
	}
	
	@Test
	void sliceEmptySection() {
		assertEquals(0, CryptoBytes.slice(new byte[4], 0, 0).length);
		assertEquals(0, CryptoBytes.slice(new byte[4], 4, 0).length);
		assertEquals(0, CryptoBytes.slice(new byte[4], 2, 0).length);
	}
	
	@Test
	void sliceMiddleSection() {
		assertArrayEquals(new byte[] { 2, 3, 4 }, CryptoBytes.slice(new byte[] { 1, 2, 3, 4, 5 }, 1, 3));
	}
	
	@Test
	void sliceFromEmptyArray() {
		assertEquals(0, CryptoBytes.slice(new byte[0], 0, 0).length);
		assertThrows(IndexOutOfBoundsException.class, () -> CryptoBytes.slice(new byte[0], 0, 1));
	}
	
	@Test
	void wipeByteArray() {
		byte[] data = { 1, 2, 3 };
		CryptoBytes.wipe(data);
		assertArrayEquals(new byte[] { 0, 0, 0 }, data);
	}
	
	@Test
	void wipeNullByteArray() {
		assertDoesNotThrow(() -> CryptoBytes.wipe((byte[]) null));
	}
	
	@Test
	void wipeEmptyByteArray() {
		byte[] data = new byte[0];
		assertDoesNotThrow(() -> CryptoBytes.wipe(data));
		assertEquals(0, data.length);
	}
	
	@Test
	void wipeCharArray() {
		char[] data = { 'a', 'b' };
		CryptoBytes.wipe(data);
		assertArrayEquals(new char[] { '\0', '\0' }, data);
	}
	
	@Test
	void wipeNullCharArray() {
		assertDoesNotThrow(() -> CryptoBytes.wipe((char[]) null));
	}
	
	@Test
	void wipeEmptyCharArray() {
		char[] data = new char[0];
		assertDoesNotThrow(() -> CryptoBytes.wipe(data));
		assertEquals(0, data.length);
	}
	
	@Test
	void toBytesWithAsciiChars() {
		byte[] result = CryptoBytes.toBytes("abc".toCharArray(), StandardCharsets.UTF_8);
		assertArrayEquals("abc".getBytes(StandardCharsets.UTF_8), result);
		assertEquals(3, result.length);
	}
	
	@Test
	void toBytesWithEmptyChars() {
		byte[] result = CryptoBytes.toBytes(new char[0], StandardCharsets.UTF_8);
		assertNotNull(result);
		assertEquals(0, result.length);
	}
	
	@Test
	void emptyConstantIsZeroLength() {
		assertNotNull(CryptoBytes.EMPTY);
		assertEquals(0, CryptoBytes.EMPTY.length);
	}
	
	@Test
	void ofShortValue() {
		assertArrayEquals(new byte[] { 0x12, 0x34 }, CryptoBytes.of((short) 0x1234));
		assertEquals(2, CryptoBytes.of((short) 0x1234).length);
	}
	
	@Test
	void ofShortBoundaryValues() {
		assertArrayEquals(new byte[] { 0, 0 }, CryptoBytes.of((short) 0));
		assertArrayEquals(new byte[] { 0x7F, (byte) 0xFF }, CryptoBytes.of(Short.MAX_VALUE));
		assertArrayEquals(new byte[] { (byte) 0x80, 0 }, CryptoBytes.of(Short.MIN_VALUE));
		assertArrayEquals(new byte[] { (byte) 0xFF, (byte) 0xFF }, CryptoBytes.of((short) -1));
	}
	
	@Test
	void ofIntValue() {
		assertArrayEquals(new byte[] { 0x12, 0x34, 0x56, 0x78 }, CryptoBytes.of(0x12345678));
		assertEquals(4, CryptoBytes.of(0x12345678).length);
	}
	
	@Test
	void ofIntBoundaryValues() {
		assertArrayEquals(new byte[] { 0, 0, 0, 0 }, CryptoBytes.of(0));
		assertArrayEquals(new byte[] { 0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF }, CryptoBytes.of(Integer.MAX_VALUE));
		assertArrayEquals(new byte[] { (byte) 0x80, 0, 0, 0 }, CryptoBytes.of(Integer.MIN_VALUE));
		assertArrayEquals(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF }, CryptoBytes.of(-1));
	}
	
	@Test
	void ofLongValue() {
		assertArrayEquals(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 }, CryptoBytes.of(0x0102030405060708L));
		assertEquals(8, CryptoBytes.of(0x0102030405060708L).length);
	}
	
	@Test
	void ofLongBoundaryValues() {
		assertArrayEquals(new byte[8], CryptoBytes.of(0L));
		assertArrayEquals(new byte[] { 0x7F, -1, -1, -1, -1, -1, -1, -1 }, CryptoBytes.of(Long.MAX_VALUE));
		assertArrayEquals(new byte[] { (byte) 0x80, 0, 0, 0, 0, 0, 0, 0 }, CryptoBytes.of(Long.MIN_VALUE));
		assertArrayEquals(new byte[] { -1, -1, -1, -1, -1, -1, -1, -1 }, CryptoBytes.of(-1L));
	}
	
	@Test
	void toBytesWithMultiByteCharacters() {
		char[] chars = "äöü".toCharArray();
		byte[] result = CryptoBytes.toBytes(chars, StandardCharsets.UTF_8);
		assertEquals(3, chars.length);
		assertEquals(6, result.length);
		assertArrayEquals("äöü".getBytes(StandardCharsets.UTF_8), result);
	}
	
	@Test
	void toBytesLeavesCallerArrayUntouched() {
		char[] chars = { 's', 'e', 'c' };
		CryptoBytes.toBytes(chars, StandardCharsets.UTF_8);
		assertArrayEquals(new char[] { 's', 'e', 'c' }, chars);
	}
	
	@Test
	void concatMatchesManualConcatenation() {
		byte[][] parts = new byte[10][];
		for (int i = 0; i < parts.length; i++) {
			parts[i] = new byte[i];
			Arrays.fill(parts[i], (byte) i);
		}
		
		byte[] result = CryptoBytes.concat(parts);
		assertEquals(45, result.length);
		int offset = 0;
		for (byte[] part : parts) {
			assertArrayEquals(part, Arrays.copyOfRange(result, offset, offset + part.length));
			offset += part.length;
		}
	}
	
	@Test
	void concatDoesNotAliasInputs() {
		byte[] first = { 1, 2 };
		byte[] second = { 3, 4 };
		byte[] result = CryptoBytes.concat(first, second);
		
		first[0] = 9;
		second[0] = 9;
		assertArrayEquals(new byte[] { 1, 2, 3, 4 }, result);
		assertNotSame(first, CryptoBytes.concat(first));
	}
	
	@Test
	void xorIsItsOwnInverse() {
		byte[] first = CryptoRandom.bytes(32);
		byte[] second = CryptoRandom.bytes(32);
		
		assertArrayEquals(first, CryptoBytes.xor(CryptoBytes.xor(first, second), second));
		assertArrayEquals(new byte[32], CryptoBytes.xor(first, first));
	}
	
	@Test
	void xorDoesNotMutateInputs() {
		byte[] first = { 1, 2, 3 };
		byte[] second = { 3, 2, 1 };
		byte[] result = CryptoBytes.xor(first, second);
		
		assertArrayEquals(new byte[] { 1, 2, 3 }, first);
		assertArrayEquals(new byte[] { 3, 2, 1 }, second);
		assertNotSame(first, result);
		assertNotSame(second, result);
	}
	
	@Test
	void sliceDoesNotAliasSource() {
		byte[] data = { 1, 2, 3, 4, 5 };
		byte[] slice = CryptoBytes.slice(data, 1, 3);
		
		slice[0] = 9;
		assertArrayEquals(new byte[] { 1, 2, 3, 4, 5 }, data);
		data[2] = 9;
		assertArrayEquals(new byte[] { 9, 3, 4 }, slice);
	}
	
	@Test
	void sliceEveryWindowOfArray() {
		byte[] data = { 1, 2, 3, 4, 5 };
		for (int offset = 0; offset <= data.length; offset++) {
			for (int length = 0; length <= data.length - offset; length++) {
				assertArrayEquals(Arrays.copyOfRange(data, offset, offset + length), CryptoBytes.slice(data, offset, length));
			}
		}
	}
	
	@Test
	void equalsConstantTimeAgainstArraysEquals() {
		byte[] base = CryptoRandom.bytes(32);
		byte[] firstDiffers = base.clone();
		firstDiffers[0] ^= 1;
		byte[] lastDiffers = base.clone();
		lastDiffers[31] ^= 1;
		
		List<byte[]> inputs = List.of(new byte[0], new byte[] { 7 }, base, base.clone(), firstDiffers, lastDiffers, Arrays.copyOf(base, 16));
		for (byte[] first : inputs) {
			for (byte[] second : inputs) {
				assertEquals(Arrays.equals(first, second), CryptoBytes.equalsConstantTime(first, second));
			}
		}
	}
	
	@Test
	void toBytesRoundTripsThroughCharset() {
		for (Charset charset : List.of(StandardCharsets.UTF_8, StandardCharsets.UTF_16, StandardCharsets.ISO_8859_1)) {
			for (String value : List.of("secret", "äöü")) {
				byte[] result = CryptoBytes.toBytes(value.toCharArray(), charset);
				assertArrayEquals(value.toCharArray(), new String(result, charset).toCharArray());
			}
		}
	}
	
	@Test
	void wipeAfterConcatDoesNotAffectResult() {
		byte[] first = { 1, 2 };
		byte[] second = { 3, 4 };
		byte[] result = CryptoBytes.concat(first, second);
		
		CryptoBytes.wipe(first);
		CryptoBytes.wipe(second);
		assertArrayEquals(new byte[] { 1, 2, 3, 4 }, result);
		assertArrayEquals(new byte[] { 0, 0 }, first);
	}
	
	@Test
	void ofRoundTripsThroughByteBuffer() {
		for (long value : new long[] { 0L, 1L, -1L, 42L, -42L, Long.MAX_VALUE, Long.MIN_VALUE }) {
			assertEquals(value, ByteBuffer.wrap(CryptoBytes.of(value)).getLong());
		}
		for (int value : new int[] { 0, 1, -1, 42, Integer.MAX_VALUE, Integer.MIN_VALUE }) {
			assertEquals(value, ByteBuffer.wrap(CryptoBytes.of(value)).getInt());
		}
		for (short value : new short[] { 0, 1, -1, 42, Short.MAX_VALUE, Short.MIN_VALUE }) {
			assertEquals(value, ByteBuffer.wrap(CryptoBytes.of(value)).getShort());
		}
	}
}
