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
import java.security.SecureRandom;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CryptoRandom}.<br>
 *
 * @author Luis-St
 */
class CryptoRandomTest {
	
	private static final char[] ALPHABET = "abcd".toCharArray();
	private static final byte[] SEED = { 1, 2, 3, 4, 5, 6, 7, 8 };
	
	private static SecureRandom seeded() throws Exception {
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		random.setSeed(SEED);
		return random;
	}
	
	@Test
	void constructorIsPrivate() throws Exception {
		Constructor<?>[] constructors = CryptoRandom.class.getDeclaredConstructors();
		assertEquals(1, constructors.length);
		assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
		assertTrue(Modifier.isFinal(CryptoRandom.class.getModifiers()));
		
		Constructor<CryptoRandom> constructor = CryptoRandom.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		assertNotNull(constructor.newInstance());
	}
	
	@Test
	void bytesWithNullRandom() {
		assertThrows(NullPointerException.class, () -> CryptoRandom.bytes(null, 16));
	}
	
	@Test
	void bytesWithNullRandomAndNegativeLength() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> CryptoRandom.bytes(null, -1));
		assertEquals("Random must not be null", exception.getMessage());
	}
	
	@Test
	void bytesWithNegativeLength() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> CryptoRandom.bytes(new SecureRandom(), -1));
		assertTrue(exception.getMessage().contains("-1"));
	}
	
	@Test
	void bytesWithNegativeLengthFromSharedSource() {
		assertThrows(IllegalArgumentException.class, () -> CryptoRandom.bytes(-1));
	}
	
	@Test
	void bytesWithIntegerMinLength() {
		assertThrows(IllegalArgumentException.class, () -> CryptoRandom.bytes(Integer.MIN_VALUE));
	}
	
	@Test
	void integerWithOriginEqualToBound() {
		assertThrows(IllegalArgumentException.class, () -> CryptoRandom.integer(5, 5));
	}
	
	@Test
	void integerWithOriginGreaterThanBound() {
		assertThrows(IllegalArgumentException.class, () -> CryptoRandom.integer(10, 5));
	}
	
	@Test
	void tokenBase64UrlWithNegativeLength() {
		assertThrows(IllegalArgumentException.class, () -> CryptoRandom.tokenBase64Url(-1));
	}
	
	@Test
	void tokenHexWithNegativeLength() {
		assertThrows(IllegalArgumentException.class, () -> CryptoRandom.tokenHex(-1));
	}
	
	@Test
	void passwordWithNullAlphabet() {
		assertThrows(NullPointerException.class, () -> CryptoRandom.password(8, null));
	}
	
	@Test
	void passwordWithNegativeLength() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> CryptoRandom.password(-1, "abc".toCharArray()));
		assertTrue(exception.getMessage().contains("-1"));
	}
	
	@Test
	void passwordWithEmptyAlphabet() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> CryptoRandom.password(8, new char[0]));
		assertEquals("Alphabet must not be empty", exception.getMessage());
	}
	
	@Test
	void passwordWithNegativeLengthAndEmptyAlphabet() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> CryptoRandom.password(-1, new char[0]));
		assertTrue(exception.getMessage().contains("-1"));
		assertFalse(exception.getMessage().contains("empty"));
	}
	
	@Test
	void bytesWithZeroLength() {
		byte[] shared = CryptoRandom.bytes(0);
		assertNotNull(shared);
		assertEquals(0, shared.length);
		
		byte[] explicit = CryptoRandom.bytes(new SecureRandom(), 0);
		assertNotNull(explicit);
		assertEquals(0, explicit.length);
	}
	
	@Test
	void bytesWithPositiveLength() {
		byte[] result = CryptoRandom.bytes(32);
		assertEquals(32, result.length);
		assertFalse(Arrays.equals(new byte[32], result));
	}
	
	@Test
	void bytesWithExplicitSourceIsDeterministic() throws Exception {
		assertArrayEquals(CryptoRandom.bytes(seeded(), 32), CryptoRandom.bytes(seeded(), 32));
		
		SecureRandom other = SecureRandom.getInstance("SHA1PRNG");
		other.setSeed(new byte[] { 9, 9, 9, 9 });
		assertFalse(Arrays.equals(CryptoRandom.bytes(seeded(), 32), CryptoRandom.bytes(other, 32)));
	}
	
	@Test
	void passwordWithZeroLength() {
		char[] result = CryptoRandom.password(0, ALPHABET);
		assertNotNull(result);
		assertEquals(0, result.length);
	}
	
	@Test
	void passwordWithSingleCharacter() {
		char[] result = CryptoRandom.password(1, ALPHABET);
		assertEquals(1, result.length);
		assertTrue(new String(ALPHABET).indexOf(result[0]) >= 0);
	}
	
	@Test
	void passwordWithMultipleCharacters() {
		char[] result = CryptoRandom.password(64, ALPHABET);
		assertEquals(64, result.length);
		for (char character : result) {
			assertTrue(new String(ALPHABET).indexOf(character) >= 0);
		}
	}
	
	@Test
	void passwordWithSingleCharacterAlphabet() {
		char[] result = CryptoRandom.password(10, new char[] { 'x' });
		assertEquals(10, result.length);
		assertArrayEquals("xxxxxxxxxx".toCharArray(), result);
	}
	
	@Test
	void strongReturnsSource() {
		SecureRandom random = assertDoesNotThrow(CryptoRandom::strong);
		assertNotNull(random);
		assertDoesNotThrow(() -> random.nextBytes(new byte[8]));
	}
	
	@Test
	void saltIsSixteenBytes() {
		assertEquals(16, CryptoRandom.salt().length);
	}
	
	@Test
	void saltReturnsFreshArrays() {
		byte[] first = CryptoRandom.salt();
		byte[] second = CryptoRandom.salt();
		assertNotSame(first, second);
		assertFalse(Arrays.equals(first, second));
	}
	
	@Test
	void bytesReturnsFreshArrays() {
		byte[] first = CryptoRandom.bytes(32);
		byte[] second = CryptoRandom.bytes(32);
		assertNotSame(first, second);
		assertFalse(Arrays.equals(first, second));
	}
	
	@Test
	void integerWithinRange() {
		for (int i = 0; i < 1000; i++) {
			int value = CryptoRandom.integer(5, 10);
			assertTrue(value >= 5);
			assertTrue(value < 10);
		}
	}
	
	@Test
	void integerWithSingleValueRange() {
		for (int i = 0; i < 100; i++) {
			assertEquals(7, CryptoRandom.integer(7, 8));
		}
	}
	
	@Test
	void integerWithNegativeRange() {
		for (int i = 0; i < 1000; i++) {
			int value = CryptoRandom.integer(-10, -5);
			assertTrue(value >= -10);
			assertTrue(value < -5);
		}
	}
	
	@Test
	void tokenBase64UrlLength() {
		for (int byteLength : new int[] { 1, 16, 32 }) {
			String token = CryptoRandom.tokenBase64Url(byteLength);
			assertEquals((int) Math.ceil(byteLength * 4 / 3.0), token.length());
			assertFalse(token.contains("="));
			assertTrue(token.chars().allMatch(c -> Character.isLetterOrDigit(c) || c == '-' || c == '_'));
		}
	}
	
	@Test
	void tokenBase64UrlWithZeroLength() {
		String token = CryptoRandom.tokenBase64Url(0);
		assertNotNull(token);
		assertEquals("", token);
	}
	
	@Test
	void tokenHexLength() {
		for (int byteLength : new int[] { 1, 16, 32 }) {
			String token = CryptoRandom.tokenHex(byteLength);
			assertEquals(2 * byteLength, token.length());
			assertTrue(token.chars().allMatch(c -> (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f')));
			assertEquals(token, token.toLowerCase(Locale.ROOT));
		}
	}
	
	@Test
	void tokenHexWithZeroLength() {
		assertEquals("", CryptoRandom.tokenHex(0));
	}
	
	@Test
	void instanceReturnsSharedSource() {
		assertNotNull(CryptoRandom.instance());
		assertSame(CryptoRandom.instance(), CryptoRandom.instance());
	}
	
	@Test
	void bytesWithExplicitSourceMatchesDirectDraw() throws Exception {
		byte[] expected = new byte[32];
		seeded().nextBytes(expected);
		assertArrayEquals(expected, CryptoRandom.bytes(seeded(), 32));
	}
	
	@Test
	void bytesWithLargeLength() {
		byte[] result = CryptoRandom.bytes(1 << 16);
		assertEquals(65536, result.length);
		
		Set<Byte> values = new HashSet<>();
		for (byte value : result) {
			values.add(value);
		}
		assertTrue(values.size() >= 200);
	}
	
	@Test
	void passwordDrawsAcrossWholeAlphabet() {
		char[] result = CryptoRandom.password(2000, ALPHABET);
		String generated = new String(result);
		for (char character : ALPHABET) {
			assertTrue(generated.indexOf(character) >= 0);
		}
		assertTrue(generated.indexOf(ALPHABET[ALPHABET.length - 1]) >= 0);
	}
	
	@Test
	void passwordWithDuplicateAlphabetCharacters() {
		char[] result = CryptoRandom.password(1000, new char[] { 'a', 'a', 'b' });
		long a = new String(result).chars().filter(c -> c == 'a').count();
		long b = new String(result).chars().filter(c -> c == 'b').count();
		assertEquals(1000, a + b);
		assertTrue(a > b);
	}
	
	@Test
	void passwordReturnsFreshArrays() {
		char[] first = CryptoRandom.password(16, ALPHABET);
		char[] second = CryptoRandom.password(16, ALPHABET);
		assertNotSame(first, second);
		
		char[] copy = second.clone();
		CryptoBytes.wipe(first);
		assertArrayEquals(copy, second);
	}
	
	@Test
	void passwordDoesNotMutateAlphabet() {
		char[] alphabet = { 'a', 'b', 'c' };
		CryptoRandom.password(32, alphabet);
		assertArrayEquals(new char[] { 'a', 'b', 'c' }, alphabet);
	}
	
	@Test
	void tokensAreUniqueAcrossCalls() {
		Set<String> hex = new HashSet<>();
		Set<String> base64 = new HashSet<>();
		for (int i = 0; i < 1000; i++) {
			hex.add(CryptoRandom.tokenHex(16));
			base64.add(CryptoRandom.tokenBase64Url(16));
		}
		assertEquals(1000, hex.size());
		assertEquals(1000, base64.size());
	}
	
	@Test
	void tokenHexDecodesToRequestedByteCount() {
		for (int byteLength : new int[] { 1, 16, 32 }) {
			assertEquals(byteLength, HexFormat.of().parseHex(CryptoRandom.tokenHex(byteLength)).length);
		}
	}
	
	@Test
	void tokenBase64UrlDecodesToRequestedByteCount() {
		for (int byteLength : new int[] { 1, 16, 32 }) {
			assertEquals(byteLength, Base64.getUrlDecoder().decode(CryptoRandom.tokenBase64Url(byteLength)).length);
		}
	}
	
	@Test
	void saltIsSuitableAsPasswordSalt() {
		Set<String> salts = new HashSet<>();
		for (int i = 0; i < 100; i++) {
			byte[] salt = CryptoRandom.salt();
			assertEquals(16, salt.length);
			salts.add(HexFormat.of().formatHex(salt));
		}
		assertEquals(100, salts.size());
	}
	
	@Test
	void strongIsIndependentOfSharedInstance() {
		assertNotSame(CryptoRandom.instance(), CryptoRandom.strong());
	}
}
