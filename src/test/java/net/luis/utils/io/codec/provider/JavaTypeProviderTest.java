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

package net.luis.utils.io.codec.provider;

import com.google.common.util.concurrent.AtomicDouble;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.Function;

import static net.luis.utils.io.codec.provider.JavaTypeProvider.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link JavaTypeProvider}.<br>
 *
 * @author Luis-St
 */
class JavaTypeProviderTest {
	
	private static final Function<String, IllegalStateException> EXCEPTION = IllegalStateException::new;
	
	@Test
	void constantsAreDistinctInstances() {
		assertNotNull(STRICT);
		assertNotNull(DEFAULT);
		assertNotNull(LENIENT);
		assertNotSame(STRICT, DEFAULT);
		assertNotSame(DEFAULT, LENIENT);
		assertNotSame(STRICT, LENIENT);
	}
	
	@Test
	void emptyReturnsSameInstance() {
		assertNotNull(DEFAULT.empty());
		assertSame(DEFAULT.empty(), DEFAULT.empty());
		assertSame(STRICT.empty(), DEFAULT.empty());
		assertSame(DEFAULT.empty(), LENIENT.empty());
	}
	
	@Test
	void emptyToStringHasNoRepresentation() {
		assertEquals("Empty java element has no string representation", DEFAULT.empty().toString());
	}
	
	@Test
	void createMethodsWithNullExceptionConstructor() {
		assertThrows(NullPointerException.class, () -> DEFAULT.createNull(null));
		assertThrows(NullPointerException.class, () -> DEFAULT.createBoolean(true, null));
		assertThrows(NullPointerException.class, () -> DEFAULT.createByte((byte) 1, null));
		assertThrows(NullPointerException.class, () -> DEFAULT.createShort((short) 1, null));
		assertThrows(NullPointerException.class, () -> DEFAULT.createInteger(1, null));
		assertThrows(NullPointerException.class, () -> DEFAULT.createLong(1L, null));
		assertThrows(NullPointerException.class, () -> DEFAULT.createFloat(1.0f, null));
		assertThrows(NullPointerException.class, () -> DEFAULT.createDouble(1.0, null));
		assertThrows(NullPointerException.class, () -> DEFAULT.createString("value", null));
		assertThrows(NullPointerException.class, () -> DEFAULT.createList(List.of(), null));
		assertThrows(NullPointerException.class, () -> DEFAULT.createMap(null));
		assertThrows(NullPointerException.class, () -> DEFAULT.createMap(Map.of(), null));
	}
	
	@Test
	void typeCheckMethodsWithNullExceptionConstructor() {
		assertThrows(NullPointerException.class, () -> DEFAULT.isEmpty("value", null));
		assertThrows(NullPointerException.class, () -> DEFAULT.isNull("value", null));
	}
	
	@Test
	void getMethodsWithNullExceptionConstructor() {
		assertThrows(NullPointerException.class, () -> DEFAULT.getBoolean(true, null));
		assertThrows(NullPointerException.class, () -> DEFAULT.getByte((byte) 1, null));
		assertThrows(NullPointerException.class, () -> DEFAULT.getShort((short) 1, null));
		assertThrows(NullPointerException.class, () -> DEFAULT.getInteger(1, null));
		assertThrows(NullPointerException.class, () -> DEFAULT.getLong(1L, null));
		assertThrows(NullPointerException.class, () -> DEFAULT.getFloat(1.0f, null));
		assertThrows(NullPointerException.class, () -> DEFAULT.getDouble(1.0, null));
		assertThrows(NullPointerException.class, () -> DEFAULT.getString("value", null));
		assertThrows(NullPointerException.class, () -> DEFAULT.getList(List.of(), null));
		assertThrows(NullPointerException.class, () -> DEFAULT.getMap(Map.of(), null));
	}
	
	@Test
	void accessMethodsWithNullExceptionConstructor() {
		assertThrows(NullPointerException.class, () -> DEFAULT.has(Map.of(), "key", null));
		assertThrows(NullPointerException.class, () -> DEFAULT.get(Map.of(), "key", null));
		assertThrows(NullPointerException.class, () -> DEFAULT.set(new HashMap<>(), "key", 1, null));
		assertThrows(NullPointerException.class, () -> DEFAULT.merge(List.of(), List.of(), null));
	}
	
	@Test
	void createStringWithNull() {
		assertThrows(IllegalStateException.class, () -> DEFAULT.createString(null, EXCEPTION));
	}
	
	@Test
	void createListWithNull() {
		assertThrows(IllegalStateException.class, () -> DEFAULT.createList(null, EXCEPTION));
	}
	
	@Test
	void createMapWithNull() {
		assertThrows(IllegalStateException.class, () -> DEFAULT.createMap(null, EXCEPTION));
	}
	
	@Test
	void isEmptyWithNull() {
		assertThrows(IllegalStateException.class, () -> STRICT.isEmpty(null, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.isEmpty(null, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> LENIENT.isEmpty(null, EXCEPTION));
	}
	
	@Test
	void getMethodsWithNullInStrictMode() {
		assertThrows(IllegalStateException.class, () -> STRICT.getBoolean(null, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> STRICT.getByte(null, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> STRICT.getShort(null, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> STRICT.getInteger(null, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> STRICT.getLong(null, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> STRICT.getFloat(null, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> STRICT.getDouble(null, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> STRICT.getString(null, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> STRICT.getList(null, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> STRICT.getMap(null, EXCEPTION));
	}
	
	@Test
	void getNumberNullMessageArticle() {
		IllegalStateException integer = assertThrows(IllegalStateException.class, () -> STRICT.getInteger(null, EXCEPTION));
		assertTrue(integer.getMessage().endsWith("not an integer"));
		
		IllegalStateException longValue = assertThrows(IllegalStateException.class, () -> STRICT.getLong(null, EXCEPTION));
		assertTrue(longValue.getMessage().endsWith("not a long"));
	}
	
	@Test
	void hasWithNullTypeInStrictMode() {
		assertThrows(IllegalStateException.class, () -> STRICT.has(null, "key", EXCEPTION));
	}
	
	@Test
	void hasWithNullKeyInStrictMode() {
		assertThrows(IllegalStateException.class, () -> STRICT.has(Map.of(), null, EXCEPTION));
	}
	
	@Test
	void getWithNullType() {
		assertThrows(IllegalStateException.class, () -> STRICT.get(null, "key", EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.get(null, "key", EXCEPTION));
		assertThrows(IllegalStateException.class, () -> LENIENT.get(null, "key", EXCEPTION));
	}
	
	@Test
	void getWithNullKey() {
		assertThrows(IllegalStateException.class, () -> STRICT.get(Map.of(), null, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.get(Map.of(), null, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> LENIENT.get(Map.of(), null, EXCEPTION));
	}
	
	@Test
	void setWithNullTypeInStrictMode() {
		assertThrows(IllegalStateException.class, () -> STRICT.set(null, "key", 1, EXCEPTION));
	}
	
	@Test
	void setWithNullKeyInStrictMode() {
		Map<String, Object> map = new HashMap<>();
		assertThrows(IllegalStateException.class, () -> STRICT.set(map, null, 1, EXCEPTION));
		assertTrue(map.isEmpty());
	}
	
	@Test
	void getNumberWithOverflowingSuffixedString() {
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> DEFAULT.getByte("300b", EXCEPTION));
		assertTrue(exception.getMessage().contains("is not a valid number"));
	}
	
	@Test
	void hasAndGetWithUnparseableMapString() {
		IllegalStateException has = assertThrows(IllegalStateException.class, () -> DEFAULT.has("{a}", "a", EXCEPTION));
		assertTrue(has.getMessage().contains("could not be parsed as map"));
		
		IllegalStateException get = assertThrows(IllegalStateException.class, () -> DEFAULT.get("{a}", "a", EXCEPTION));
		assertTrue(get.getMessage().contains("could not be parsed as map"));
	}
	
	@Test
	void createNullReturnsNull() {
		assertNull(DEFAULT.createNull(EXCEPTION));
	}
	
	@Test
	void createStringWithValue() {
		assertEquals("value", DEFAULT.createString("value", EXCEPTION));
		assertEquals("", DEFAULT.createString("", EXCEPTION));
	}
	
	@Test
	void createListReturnsSameInstance() {
		List<Object> list = new ArrayList<>(List.of(1, 2));
		assertSame(list, DEFAULT.createList(list, EXCEPTION));
	}
	
	@Test
	void createMapReturnsSameInstance() {
		Map<String, Object> map = new HashMap<>(Map.of("a", 1));
		assertSame(map, DEFAULT.createMap(map, EXCEPTION));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	void createEmptyMapIsNewMutableMap() {
		Object first = DEFAULT.createMap(EXCEPTION);
		Object second = DEFAULT.createMap(EXCEPTION);
		
		assertInstanceOf(LinkedHashMap.class, first);
		assertTrue(((Map<String, Object>) first).isEmpty());
		assertNotSame(first, second);
		((Map<String, Object>) first).put("key", 1);
		assertEquals(1, ((Map<String, Object>) first).get("key"));
	}
	
	@Test
	void isEmptyWithEmptyElement() {
		assertTrue(DEFAULT.isEmpty(DEFAULT.empty(), EXCEPTION));
	}
	
	@Test
	void isEmptyWithOtherObject() {
		assertFalse(DEFAULT.isEmpty("x", EXCEPTION));
		assertFalse(DEFAULT.isEmpty(0, EXCEPTION));
		assertFalse(DEFAULT.isEmpty(new Object(), EXCEPTION));
	}
	
	@Test
	void isNullWithNull() {
		assertTrue(DEFAULT.isNull(null, EXCEPTION));
	}
	
	@Test
	void isNullWithNonNull() {
		assertFalse(DEFAULT.isNull("x", EXCEPTION));
		assertFalse(DEFAULT.isNull(DEFAULT.empty(), EXCEPTION));
	}
	
	@Test
	void getBooleanWithNullInDefaultMode() {
		assertFalse(DEFAULT.getBoolean(null, EXCEPTION));
		assertFalse(LENIENT.getBoolean(null, EXCEPTION));
	}
	
	@Test
	void getBooleanWithBoolean() {
		assertTrue(STRICT.getBoolean(true, EXCEPTION));
		assertFalse(STRICT.getBoolean(false, EXCEPTION));
	}
	
	@Test
	void getBooleanWithStringInStrictMode() {
		assertThrows(IllegalStateException.class, () -> STRICT.getBoolean("true", EXCEPTION));
	}
	
	@Test
	void getBooleanWithTrueStrings() {
		assertTrue(DEFAULT.getBoolean("true", EXCEPTION));
		assertTrue(DEFAULT.getBoolean("TRUE", EXCEPTION));
		assertTrue(DEFAULT.getBoolean("1", EXCEPTION));
	}
	
	@Test
	void getBooleanWithFalseStrings() {
		assertFalse(DEFAULT.getBoolean("false", EXCEPTION));
		assertFalse(DEFAULT.getBoolean("False", EXCEPTION));
		assertFalse(DEFAULT.getBoolean("0", EXCEPTION));
	}
	
	@Test
	void getBooleanWithInvalidString() {
		assertThrows(IllegalStateException.class, () -> DEFAULT.getBoolean("yes", EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.getBoolean(" true", EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.getBoolean("01", EXCEPTION));
	}
	
	@Test
	void getBooleanWithNumberOne() {
		assertTrue(DEFAULT.getBoolean(1, EXCEPTION));
		assertTrue(DEFAULT.getBoolean(1L, EXCEPTION));
		assertTrue(DEFAULT.getBoolean(1.0, EXCEPTION));
	}
	
	@Test
	void getBooleanWithNumberZero() {
		assertFalse(DEFAULT.getBoolean(0, EXCEPTION));
		assertFalse(DEFAULT.getBoolean((byte) 0, EXCEPTION));
		assertFalse(DEFAULT.getBoolean(0.0f, EXCEPTION));
	}
	
	@Test
	void getBooleanWithOtherIntegralNumber() {
		assertThrows(IllegalStateException.class, () -> DEFAULT.getBoolean(2, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.getBoolean(-1, EXCEPTION));
	}
	
	@Test
	void getBooleanWithNonIntegralNumber() {
		assertThrows(IllegalStateException.class, () -> DEFAULT.getBoolean(0.5, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.getBoolean(Double.NaN, EXCEPTION));
	}
	
	@Test
	void getBooleanWithNumberInStrictMode() {
		assertThrows(IllegalStateException.class, () -> STRICT.getBoolean(1, EXCEPTION));
	}
	
	@Test
	void getBooleanWithOtherType() {
		assertThrows(IllegalStateException.class, () -> DEFAULT.getBoolean(List.of(), EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.getBoolean('t', EXCEPTION));
	}
	
	@Test
	void getNumbersWithNullInDefaultMode() {
		assertEquals((byte) 0, DEFAULT.getByte(null, EXCEPTION));
		assertEquals((short) 0, DEFAULT.getShort(null, EXCEPTION));
		assertEquals(0, DEFAULT.getInteger(null, EXCEPTION));
		assertEquals(0L, DEFAULT.getLong(null, EXCEPTION));
		assertEquals(0.0f, DEFAULT.getFloat(null, EXCEPTION));
		assertEquals(0.0, DEFAULT.getDouble(null, EXCEPTION));
	}
	
	@Test
	void getNumbersWithMatchingType() {
		assertEquals((byte) 1, STRICT.getByte((byte) 1, EXCEPTION));
		assertEquals((short) 2, STRICT.getShort((short) 2, EXCEPTION));
		assertEquals(3, STRICT.getInteger(3, EXCEPTION));
		assertEquals(4L, STRICT.getLong(4L, EXCEPTION));
		assertEquals(5.5f, STRICT.getFloat(5.5f, EXCEPTION));
		assertEquals(6.5, STRICT.getDouble(6.5, EXCEPTION));
	}
	
	@Test
	void getNumberWithNonMatchingTypeInStrictMode() {
		IllegalStateException fromLong = assertThrows(IllegalStateException.class, () -> STRICT.getInteger(42L, EXCEPTION));
		assertTrue(fromLong.getMessage().contains("not an integer"));
		
		IllegalStateException fromString = assertThrows(IllegalStateException.class, () -> STRICT.getInteger("42", EXCEPTION));
		assertTrue(fromString.getMessage().contains("not an integer"));
	}
	
	@Test
	void getNumberWithNonNumberInLenientMode() {
		assertThrows(IllegalStateException.class, () -> DEFAULT.getInteger(true, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.getInteger(List.of(), EXCEPTION));
	}
	
	@Test
	void getNumberWithBlankString() {
		IllegalStateException empty = assertThrows(IllegalStateException.class, () -> DEFAULT.getInteger("", EXCEPTION));
		assertTrue(empty.getMessage().contains("found nothing"));
		
		IllegalStateException blank = assertThrows(IllegalStateException.class, () -> DEFAULT.getInteger("   ", EXCEPTION));
		assertTrue(blank.getMessage().contains("found nothing"));
	}
	
	@Test
	void getNumberWithInvalidString() {
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> DEFAULT.getInteger("abc", EXCEPTION));
		assertTrue(exception.getMessage().contains("is not a valid number"));
	}
	
	@Test
	void getNumberWithTrailingCharacters() {
		IllegalStateException letters = assertThrows(IllegalStateException.class, () -> DEFAULT.getInteger("42abc", EXCEPTION));
		assertTrue(letters.getMessage().contains("trailing characters"));
		
		IllegalStateException number = assertThrows(IllegalStateException.class, () -> DEFAULT.getInteger("42 43", EXCEPTION));
		assertTrue(number.getMessage().contains("trailing characters"));
	}
	
	@Test
	void getNumberWithPaddedString() {
		assertEquals(42, DEFAULT.getInteger("  42  ", EXCEPTION));
	}
	
	@Test
	void getNumberWithStringInLenientMode() {
		assertEquals((byte) 44, LENIENT.getByte("300", EXCEPTION));
	}
	
	@Test
	void getNumberCastAlways() {
		assertEquals(3, LENIENT.getInteger(3.9, EXCEPTION));
		assertEquals((byte) 44, LENIENT.getByte(300, EXCEPTION));
	}
	
	@Test
	void getNumberCastInRangeSuccess() {
		assertEquals(5, DEFAULT.getInteger((byte) 5, EXCEPTION));
	}
	
	@Test
	void getNumberCastInRangeError() {
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> DEFAULT.getByte(300, EXCEPTION));
		assertTrue(exception.getMessage().contains("could not be converted"));
	}
	
	@Test
	void getNumberWithStringOfMatchingType() {
		assertEquals(42L, DEFAULT.getLong("42", EXCEPTION));
		assertEquals(1.5, DEFAULT.getDouble("1.5", EXCEPTION));
	}
	
	@Test
	void convertUnknownNumberClass() {
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> DEFAULT.getInteger(new AtomicInteger(5), EXCEPTION));
		assertTrue(exception.getMessage().contains("is not a known number"));
	}
	
	@Test
	void convertWideningIntegral() {
		assertEquals(7L, DEFAULT.getLong((short) 7, EXCEPTION));
		assertEquals(7, DEFAULT.getInteger((byte) 7, EXCEPTION));
	}
	
	@Test
	void convertWideningFloatingPoint() {
		assertEquals(1.5, DEFAULT.getDouble(1.5f, EXCEPTION));
	}
	
	@Test
	void convertIntegralToFloatingPointExact() {
		assertEquals(42.0, DEFAULT.getDouble(42, EXCEPTION));
		assertEquals(42.0f, DEFAULT.getFloat(42L, EXCEPTION));
	}
	
	@Test
	void convertIntegralToFloatingPointInexact() {
		assertThrows(IllegalStateException.class, () -> DEFAULT.getFloat(16777217, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.getDouble((1L << 53) + 1, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.getFloat(Long.MAX_VALUE, EXCEPTION));
	}
	
	@Test
	void convertIntegralToFloatingPointInfinite() {
		assertThrows(IllegalStateException.class, () -> DEFAULT.getFloat(BigInteger.TWO.pow(200), EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.getDouble(BigInteger.TWO.pow(1100), EXCEPTION));
	}
	
	@Test
	void convertNonIntegralToIntegral() {
		assertThrows(IllegalStateException.class, () -> DEFAULT.getInteger(3.5, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.getLong(Double.NaN, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.getLong(Double.POSITIVE_INFINITY, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.getLong(Float.NaN, EXCEPTION));
	}
	
	@Test
	void convertIntegralValuedDecimalToIntegral() {
		assertEquals(3, DEFAULT.getInteger(3.0, EXCEPTION));
		assertEquals((byte) 42, DEFAULT.getByte(new BigDecimal("42"), EXCEPTION));
	}
	
	@Test
	void convertNarrowingInRange() {
		assertEquals((byte) 100, DEFAULT.getByte(100L, EXCEPTION));
		assertEquals((byte) -128, DEFAULT.getByte(-128, EXCEPTION));
	}
	
	@Test
	void convertNarrowingOutOfRange() {
		assertThrows(IllegalStateException.class, () -> DEFAULT.getByte(128, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.getByte(-129, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.getLong(3.0e20, EXCEPTION));
	}
	
	@Test
	void convertFloatingPointNarrowing() {
		assertEquals(2.0f, DEFAULT.getFloat(2.0, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.getFloat(1.5, EXCEPTION));
		assertEquals(2.0, DEFAULT.getDouble(new BigDecimal("2"), EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.getDouble(new BigDecimal("1.5"), EXCEPTION));
	}
	
	@Test
	void booleanFromEveryIntegralNumberClass() {
		assertTrue(DEFAULT.getBoolean((byte) 1, EXCEPTION));
		assertTrue(DEFAULT.getBoolean((short) 1, EXCEPTION));
		assertTrue(DEFAULT.getBoolean(1, EXCEPTION));
		assertTrue(DEFAULT.getBoolean(new AtomicInteger(1), EXCEPTION));
		assertTrue(DEFAULT.getBoolean(1L, EXCEPTION));
		assertTrue(DEFAULT.getBoolean(new AtomicLong(1), EXCEPTION));
		assertTrue(DEFAULT.getBoolean(BigInteger.ONE, EXCEPTION));
	}
	
	@Test
	void booleanFromEveryDecimalNumberClass() {
		assertTrue(DEFAULT.getBoolean(1.0f, EXCEPTION));
		assertTrue(DEFAULT.getBoolean(1.0, EXCEPTION));
		assertFalse(DEFAULT.getBoolean(new AtomicDouble(0), EXCEPTION));
		assertTrue(DEFAULT.getBoolean(new BigDecimal("1.00"), EXCEPTION));
	}
	
	@Test
	void booleanFromInfiniteAndNaN() {
		assertThrows(IllegalStateException.class, () -> DEFAULT.getBoolean(Float.NaN, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.getBoolean(Float.POSITIVE_INFINITY, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.getBoolean(Double.NaN, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.getBoolean(Double.NEGATIVE_INFINITY, EXCEPTION));
	}
	
	@Test
	void booleanFromNonExactDecimal() {
		assertThrows(IllegalStateException.class, () -> DEFAULT.getBoolean(0.5f, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.getBoolean(0.5, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.getBoolean(new BigDecimal("0.5"), EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.getBoolean(new AtomicDouble(0.5), EXCEPTION));
	}
	
	@Test
	void booleanFromOtherNumberClass() {
		LongAdder longAdder = new LongAdder();
		longAdder.add(1);
		assertTrue(DEFAULT.getBoolean(longAdder, EXCEPTION));
		
		DoubleAdder doubleAdder = new DoubleAdder();
		assertFalse(DEFAULT.getBoolean(doubleAdder, EXCEPTION));
	}
	
	@Test
	void booleanFromUnparseableNumberClass() {
		assertThrows(IllegalStateException.class, () -> DEFAULT.getBoolean(new InvalidNumber(), EXCEPTION));
	}
	
	@Test
	void getStringWithNullInDefaultMode() {
		assertEquals("", DEFAULT.getString(null, EXCEPTION));
	}
	
	@Test
	void getStringWithString() {
		String value = "value";
		assertSame(value, STRICT.getString(value, EXCEPTION));
	}
	
	@Test
	void getStringWithPrimitiveLikeInLenientMode() {
		assertEquals("42", DEFAULT.getString(42, EXCEPTION));
		assertEquals("1.5", DEFAULT.getString(1.5, EXCEPTION));
		assertEquals("true", DEFAULT.getString(true, EXCEPTION));
		assertEquals("c", DEFAULT.getString('c', EXCEPTION));
	}
	
	@Test
	void getStringWithPrimitiveLikeInStrictMode() {
		assertThrows(IllegalStateException.class, () -> STRICT.getString(42, EXCEPTION));
	}
	
	@Test
	void getStringWithOtherTypeInLenientMode() {
		assertThrows(IllegalStateException.class, () -> DEFAULT.getString(List.of(), EXCEPTION));
	}
	
	@Test
	void getListWithNullInDefaultMode() {
		List<Object> first = DEFAULT.getList(null, EXCEPTION);
		List<Object> second = DEFAULT.getList(null, EXCEPTION);
		
		assertTrue(first.isEmpty());
		assertNotSame(first, second);
		first.add(1);
		assertEquals(1, first.size());
	}
	
	@Test
	void getListWithList() {
		List<Object> list = new ArrayList<>(List.of(1, 2));
		assertSame(list, STRICT.getList(list, EXCEPTION));
	}
	
	@Test
	void getListWithListStringInLenientMode() {
		assertEquals(List.of(1L, 2L, 3L), DEFAULT.getList("[1, 2, 3]", EXCEPTION));
	}
	
	@Test
	void getListWithListStringInStrictMode() {
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> STRICT.getList("[1]", EXCEPTION));
		assertTrue(exception.getMessage().contains("is not a list"));
	}
	
	@Test
	void getListWithNonListString() {
		assertThrows(IllegalStateException.class, () -> DEFAULT.getList("abc", EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.getList("[1", EXCEPTION));
	}
	
	@Test
	void getListWithUnparseableListString() {
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> DEFAULT.getList("[[1]", EXCEPTION));
		assertTrue(exception.getMessage().contains("could not be parsed as list"));
	}
	
	@Test
	void getListWithOtherType() {
		assertThrows(IllegalStateException.class, () -> DEFAULT.getList(42, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.getList(Map.of(), EXCEPTION));
	}
	
	@Test
	void getMapWithNullInDefaultMode() {
		Map<String, Object> map = DEFAULT.getMap(null, EXCEPTION);
		assertInstanceOf(LinkedHashMap.class, map);
		assertTrue(map.isEmpty());
		map.put("key", 1);
		assertEquals(1, map.size());
	}
	
	@Test
	void getMapWithMap() {
		Map<String, Object> map = new HashMap<>(Map.of("a", 1));
		assertSame(map, STRICT.getMap(map, EXCEPTION));
	}
	
	@Test
	void getMapWithMapStringInLenientMode() {
		Map<String, Object> map = DEFAULT.getMap("{a=1, b=x}", EXCEPTION);
		assertEquals(2, map.size());
		assertEquals(1L, map.get("a"));
		assertEquals("x", map.get("b"));
	}
	
	@Test
	void getMapWithMapStringInStrictMode() {
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> STRICT.getMap("{a=1}", EXCEPTION));
		assertTrue(exception.getMessage().contains("is not a map"));
	}
	
	@Test
	void getMapWithNonMapString() {
		assertThrows(IllegalStateException.class, () -> DEFAULT.getMap("a=1", EXCEPTION));
	}
	
	@Test
	void getMapWithUnparseableMapString() {
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> DEFAULT.getMap("{a}", EXCEPTION));
		assertTrue(exception.getMessage().contains("could not be parsed as map"));
	}
	
	@Test
	void getMapWithOtherType() {
		assertThrows(IllegalStateException.class, () -> DEFAULT.getMap(List.of(), EXCEPTION));
	}
	
	@Test
	void hasWithNullTypeInDefaultMode() {
		assertFalse(DEFAULT.has(null, "key", EXCEPTION));
	}
	
	@Test
	void hasWithNullKeyInDefaultMode() {
		assertFalse(DEFAULT.has(Map.of("key", 1), null, EXCEPTION));
	}
	
	@Test
	void hasWithMapContainingKey() {
		Map<String, Object> map = new HashMap<>();
		map.put("a", null);
		assertTrue(DEFAULT.has(map, "a", EXCEPTION));
	}
	
	@Test
	void hasWithMapMissingKey() {
		assertFalse(DEFAULT.has(Map.of("a", 1), "b", EXCEPTION));
	}
	
	@Test
	void hasWithMapStringInLenientMode() {
		assertTrue(DEFAULT.has("{a=1}", "a", EXCEPTION));
		assertFalse(DEFAULT.has("{a=1}", "b", EXCEPTION));
	}
	
	@Test
	void hasWithMapStringInStrictMode() {
		assertThrows(IllegalStateException.class, () -> STRICT.has("{a=1}", "a", EXCEPTION));
	}
	
	@Test
	void hasWithNonMap() {
		assertThrows(IllegalStateException.class, () -> DEFAULT.has(42, "a", EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.has("abc", "a", EXCEPTION));
	}
	
	@Test
	void getWithMapPresentKey() {
		assertEquals(1, DEFAULT.get(Map.of("a", 1), "a", EXCEPTION));
	}
	
	@Test
	void getWithMapMissingKey() {
		assertNull(DEFAULT.get(Map.of("a", 1), "b", EXCEPTION));
	}
	
	@Test
	void getWithMapStringInLenientMode() {
		assertEquals(1L, DEFAULT.get("{a=1}", "a", EXCEPTION));
	}
	
	@Test
	void getWithMapStringInStrictMode() {
		assertThrows(IllegalStateException.class, () -> STRICT.get("{a=1}", "a", EXCEPTION));
	}
	
	@Test
	void getWithNonMap() {
		assertThrows(IllegalStateException.class, () -> DEFAULT.get(42, "a", EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.get("abc", "a", EXCEPTION));
	}
	
	@Test
	void setWithNullTypeInDefaultMode() {
		assertDoesNotThrow(() -> DEFAULT.set(null, "key", 1, EXCEPTION));
	}
	
	@Test
	void setWithNullKeyInDefaultMode() {
		Map<String, Object> map = new HashMap<>();
		assertDoesNotThrow(() -> DEFAULT.set(map, null, 1, EXCEPTION));
		assertTrue(map.isEmpty());
	}
	
	@Test
	void setWithMap() {
		Map<String, Object> map = new HashMap<>();
		DEFAULT.set(map, "a", 1, EXCEPTION);
		assertEquals(1, map.get("a"));
		
		DEFAULT.set(map, "a", 2, EXCEPTION);
		assertEquals(2, map.get("a"));
		
		DEFAULT.set(map, "b", null, EXCEPTION);
		assertTrue(map.containsKey("b"));
		assertNull(map.get("b"));
	}
	
	@Test
	void setWithNonMap() {
		assertThrows(IllegalStateException.class, () -> DEFAULT.set("{a=1}", "b", 2, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.set(42, "a", 1, EXCEPTION));
	}
	
	@Test
	void mergeWithNullCurrent() {
		assertEquals("value", DEFAULT.merge(null, "value", EXCEPTION));
		assertNull(DEFAULT.merge(null, null, EXCEPTION));
	}
	
	@Test
	void mergeWithNullValue() {
		assertEquals("current", DEFAULT.merge("current", null, EXCEPTION));
	}
	
	@Test
	void mergeWithEmptyCurrent() {
		List<Object> value = List.of(1);
		assertSame(value, DEFAULT.merge(DEFAULT.empty(), value, EXCEPTION));
	}
	
	@Test
	void mergeLists() {
		List<Object> current = List.of(1, 2);
		List<Object> value = List.of(3);
		
		assertEquals(List.of(1, 2, 3), DEFAULT.merge(current, value, EXCEPTION));
		assertEquals(List.of(1, 2), current);
		assertEquals(List.of(3), value);
	}
	
	@Test
	void mergeMaps() {
		Map<String, Object> current = new LinkedHashMap<>();
		current.put("a", 1);
		current.put("b", 2);
		Map<String, Object> value = new LinkedHashMap<>();
		value.put("b", 3);
		value.put("c", 4);
		
		Map<?, ?> merged = assertInstanceOf(Map.class, DEFAULT.merge(current, value, EXCEPTION));
		assertEquals(List.of("a", "b", "c"), List.copyOf(merged.keySet()));
		assertEquals(3, merged.get("b"));
		assertEquals(2, current.get("b"));
		assertEquals(2, value.size());
	}
	
	@Test
	void mergeListWithNonList() {
		assertThrows(IllegalStateException.class, () -> DEFAULT.merge(List.of(1), Map.of(), EXCEPTION));
	}
	
	@Test
	void mergeMapWithNonMap() {
		assertThrows(IllegalStateException.class, () -> DEFAULT.merge(Map.of(), List.of(), EXCEPTION));
	}
	
	@Test
	void mergeIncompatibleValues() {
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> DEFAULT.merge(1, 2, EXCEPTION));
		assertTrue(exception.getMessage().contains("Unable to merge"));
	}
	
	@Test
	void createPrimitiveValues() {
		assertEquals(true, DEFAULT.createBoolean(true, EXCEPTION));
		assertEquals((byte) 1, DEFAULT.createByte((byte) 1, EXCEPTION));
		assertEquals((short) 2, DEFAULT.createShort((short) 2, EXCEPTION));
		assertEquals(3, DEFAULT.createInteger(3, EXCEPTION));
		assertEquals(4L, DEFAULT.createLong(4L, EXCEPTION));
		assertEquals(Float.NaN, DEFAULT.createFloat(Float.NaN, EXCEPTION));
		assertEquals(Double.MIN_VALUE, DEFAULT.createDouble(Double.MIN_VALUE, EXCEPTION));
	}
	
	@Test
	void getNumbersAtTypeBoundaries() {
		assertEquals((byte) 127, DEFAULT.getByte(127L, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.getByte(128L, EXCEPTION));
		assertEquals(Short.MIN_VALUE, DEFAULT.getShort((long) Short.MIN_VALUE, EXCEPTION));
		assertEquals(Integer.MAX_VALUE, DEFAULT.getInteger((long) Integer.MAX_VALUE, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.getInteger(Integer.MAX_VALUE + 1L, EXCEPTION));
	}
	
	@Test
	void getNumberWithSuffixedString() {
		assertEquals((byte) 12, DEFAULT.getByte("12b", EXCEPTION));
		assertEquals(1.5, DEFAULT.getDouble("1.5f", EXCEPTION));
	}
	
	@Test
	void getNumberWithNegativeString() {
		assertEquals(-42, DEFAULT.getInteger("-42", EXCEPTION));
	}
	
	@Test
	void getStringWithEmptyString() {
		assertEquals("", STRICT.getString("", EXCEPTION));
	}
	
	@Test
	void getListWithEmptyListString() {
		assertTrue(DEFAULT.getList("[]", EXCEPTION).isEmpty());
	}
	
	@Test
	void getMapWithEmptyMapString() {
		assertTrue(DEFAULT.getMap("{}", EXCEPTION).isEmpty());
	}
	
	@Test
	void getListWithMultiLineString() {
		assertEquals(List.of(1L, 2L), DEFAULT.getList("[\n1,\n2\n]", EXCEPTION));
	}
	
	@Test
	void getListWithNestedCollections() {
		List<Object> list = DEFAULT.getList("[[1, 2], {a=b}, 'c', \"str\", true]", EXCEPTION);
		
		assertEquals(5, list.size());
		assertEquals(List.of(1L, 2L), list.get(0));
		assertEquals(Map.of("a", "b"), list.get(1));
		assertEquals('c', list.get(2));
		assertEquals("str", list.get(3));
		assertEquals(true, list.get(4));
	}
	
	@Test
	void getMapWithNestedValues() {
		Map<String, Object> map = DEFAULT.getMap("{a=[1, 2], b={c=3}}", EXCEPTION);
		
		assertEquals(2, map.size());
		assertEquals(List.of(1L, 2L), map.get("a"));
		assertEquals(Map.of("c", 3L), map.get("b"));
	}
	
	@Test
	void hasAndGetOnParsedMapString() {
		String value = "{a=1, b=x}";
		
		assertEquals(2, DEFAULT.getMap(value, EXCEPTION).size());
		assertTrue(DEFAULT.has(value, "a", EXCEPTION));
		assertEquals(1L, DEFAULT.get(value, "a", EXCEPTION));
		assertFalse(DEFAULT.has(value, "c", EXCEPTION));
		assertNull(DEFAULT.get(value, "c", EXCEPTION));
	}
	
	@Test
	void setThenGetRoundTrip() {
		Object map = DEFAULT.createMap(EXCEPTION);
		DEFAULT.set(map, "a", 1, EXCEPTION);
		DEFAULT.set(map, "b", "x", EXCEPTION);
		DEFAULT.set(map, "a", 2, EXCEPTION);
		
		assertTrue(DEFAULT.has(map, "a", EXCEPTION));
		assertTrue(DEFAULT.has(map, "b", EXCEPTION));
		assertEquals(2, DEFAULT.get(map, "a", EXCEPTION));
		assertEquals("x", DEFAULT.get(map, "b", EXCEPTION));
		assertEquals(2, DEFAULT.getMap(map, EXCEPTION).size());
	}
	
	@Test
	void mergeNestedStructures() {
		Map<String, Object> current = Map.of("a", List.of(1));
		Map<String, Object> value = Map.of("a", List.of(2));
		
		Map<?, ?> merged = assertInstanceOf(Map.class, DEFAULT.merge(current, value, EXCEPTION));
		assertEquals(1, merged.size());
		assertEquals(List.of(2), merged.get("a"));
	}
	
	@Test
	void mergeEmptyWithEmpty() {
		assertSame(DEFAULT.empty(), DEFAULT.merge(DEFAULT.empty(), DEFAULT.empty(), EXCEPTION));
	}
	
	@Test
	void configsDifferOnSameInput() {
		assertThrows(IllegalStateException.class, () -> STRICT.getInteger(3.5, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> DEFAULT.getInteger(3.5, EXCEPTION));
		assertEquals(3, LENIENT.getInteger(3.5, EXCEPTION));
		assertThrows(IllegalStateException.class, () -> STRICT.getString(null, EXCEPTION));
		assertEquals("", DEFAULT.getString(null, EXCEPTION));
		assertEquals("", LENIENT.getString(null, EXCEPTION));
	}
	
	private static final class InvalidNumber extends Number {
		
		@Override
		public int intValue() {
			return 0;
		}
		
		@Override
		public long longValue() {
			return 0;
		}
		
		@Override
		public float floatValue() {
			return 0;
		}
		
		@Override
		public double doubleValue() {
			return 0;
		}
		
		@Override
		public String toString() {
			return "invalid";
		}
	}
}
