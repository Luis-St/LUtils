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

package net.luis.utils.io.data.toml;

import net.luis.utils.io.data.toml.exception.TomlTypeException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TomlValue}.<br>
 *
 * @author Luis-St
 */
class TomlValueTest {
	
	private static final TomlConfig CUSTOM_CONFIG = new TomlConfig(
		true, true, "\t",
		false, 3, false, 10, false, 80, false,
		TomlConfig.DateTimeStyle.RFC_3339, false, StandardCharsets.UTF_8
	);
	
	@Test
	void constructorWithBoolean() {
		TomlValue trueValue = new TomlValue(true);
		TomlValue falseValue = new TomlValue(false);
		
		assertTrue(trueValue.getAsBoolean());
		assertFalse(falseValue.getAsBoolean());
	}
	
	@Test
	void constructorWithNumbers() {
		assertDoesNotThrow(() -> new TomlValue((byte) 1));
		assertDoesNotThrow(() -> new TomlValue((short) 2));
		assertDoesNotThrow(() -> new TomlValue(3));
		assertDoesNotThrow(() -> new TomlValue(4L));
		assertDoesNotThrow(() -> new TomlValue(5.5f));
		assertDoesNotThrow(() -> new TomlValue(6.6));
		assertDoesNotThrow(() -> new TomlValue(new BigInteger("123456789")));
		assertDoesNotThrow(() -> new TomlValue(new BigDecimal("123.456")));
		
		assertThrows(NullPointerException.class, () -> new TomlValue((Number) null));
	}
	
	@Test
	void constructorWithCharacter() {
		TomlValue charValue = new TomlValue('a');
		assertEquals("a", charValue.getAsString());
		
		TomlValue digitChar = new TomlValue('5');
		assertEquals("5", digitChar.getAsString());
	}
	
	@Test
	void constructorWithString() {
		TomlValue stringValue = new TomlValue("test string");
		assertEquals("test string", stringValue.getAsString());
		
		TomlValue emptyString = new TomlValue("");
		assertEquals("", emptyString.getAsString());
		
		assertThrows(NullPointerException.class, () -> new TomlValue((String) null));
	}
	
	@Test
	void constructorWithLocalDate() {
		LocalDate date = LocalDate.of(2025, 1, 15);
		TomlValue dateValue = new TomlValue(date);
		
		assertTrue(dateValue.isLocalDate());
		assertEquals(date, dateValue.getAsLocalDate());
		
		assertThrows(NullPointerException.class, () -> new TomlValue((LocalDate) null));
	}
	
	@Test
	void constructorWithLocalTime() {
		LocalTime time = LocalTime.of(14, 30, 45);
		TomlValue timeValue = new TomlValue(time);
		
		assertTrue(timeValue.isLocalTime());
		assertEquals(time, timeValue.getAsLocalTime());
		
		assertThrows(NullPointerException.class, () -> new TomlValue((LocalTime) null));
	}
	
	@Test
	void constructorWithLocalDateTime() {
		LocalDateTime dateTime = LocalDateTime.of(2025, 1, 15, 14, 30, 45);
		TomlValue dateTimeValue = new TomlValue(dateTime);
		
		assertTrue(dateTimeValue.isLocalDateTime());
		assertEquals(dateTime, dateTimeValue.getAsLocalDateTime());
		
		assertThrows(NullPointerException.class, () -> new TomlValue((LocalDateTime) null));
	}
	
	@Test
	void constructorWithOffsetDateTime() {
		OffsetDateTime dateTime = OffsetDateTime.of(2025, 1, 15, 14, 30, 45, 0, ZoneOffset.ofHours(2));
		TomlValue dateTimeValue = new TomlValue(dateTime);
		
		assertTrue(dateTimeValue.isOffsetDateTime());
		assertEquals(dateTime, dateTimeValue.getAsOffsetDateTime());
		
		assertThrows(NullPointerException.class, () -> new TomlValue((OffsetDateTime) null));
	}
	
	@Test
	void tomlElementTypeChecks() {
		TomlValue value = new TomlValue(42);
		
		assertFalse(value.isTomlNull());
		assertTrue(value.isTomlValue());
		assertFalse(value.isTomlArray());
		assertFalse(value.isTomlTable());
	}
	
	@Test
	void tomlElementConversions() {
		TomlValue value = new TomlValue("test");
		
		assertThrows(TomlTypeException.class, value::getAsTomlArray);
		assertThrows(TomlTypeException.class, value::getAsTomlTable);
		assertSame(value, value.getAsTomlValue());
	}
	
	@Test
	void getAsStringFromDifferentTypes() {
		assertEquals("true", new TomlValue(true).getAsString());
		assertEquals("false", new TomlValue(false).getAsString());
		assertEquals("42", new TomlValue(42).getAsString());
		assertEquals("3.14", new TomlValue(3.14).getAsString());
		assertEquals("1.0", new TomlValue(1.0f).getAsString());
		assertEquals("test", new TomlValue("test").getAsString());
		assertEquals("123456789", new TomlValue(new BigInteger("123456789")).getAsString());
		assertEquals("123.456", new TomlValue(new BigDecimal("123.456")).getAsString());
	}
	
	@Test
	void getAsStringFromDateTime() {
		LocalDate date = LocalDate.of(2025, 1, 15);
		LocalTime time = LocalTime.of(14, 30, 45);
		LocalDateTime dateTime = LocalDateTime.of(2025, 1, 15, 14, 30, 45);
		
		TomlValue dateValue = new TomlValue(date);
		TomlValue timeValue = new TomlValue(time);
		TomlValue dateTimeValue = new TomlValue(dateTime);
		
		assertEquals("2025-01-15", dateValue.getAsString());
		assertEquals("14:30:45", timeValue.getAsString());
		assertTrue(dateTimeValue.getAsString().contains("2025-01-15"));
		assertTrue(dateTimeValue.getAsString().contains("14:30:45"));
	}
	
	@Test
	void getAsBooleanValid() {
		assertTrue(new TomlValue(true).getAsBoolean());
		assertFalse(new TomlValue(false).getAsBoolean());
	}
	
	@Test
	void getAsBooleanInvalid() {
		assertThrows(TomlTypeException.class, () -> new TomlValue("true").getAsBoolean());
		assertThrows(TomlTypeException.class, () -> new TomlValue("false").getAsBoolean());
		assertThrows(TomlTypeException.class, () -> new TomlValue("test").getAsBoolean());
		assertThrows(TomlTypeException.class, () -> new TomlValue(1).getAsBoolean());
		assertThrows(TomlTypeException.class, () -> new TomlValue(0).getAsBoolean());
		assertThrows(TomlTypeException.class, () -> new TomlValue(3.14).getAsBoolean());
		assertThrows(TomlTypeException.class, () -> new TomlValue(LocalDate.now()).getAsBoolean());
	}
	
	@Test
	void getAsNumberValid() {
		assertEquals((byte) 1, new TomlValue((byte) 1).getAsNumber());
		assertEquals((short) 2, new TomlValue((short) 2).getAsNumber());
		assertEquals(3, new TomlValue(3).getAsNumber());
		assertEquals(4L, new TomlValue(4L).getAsNumber());
		assertEquals(5.5f, new TomlValue(5.5f).getAsNumber());
		assertEquals(6.6, new TomlValue(6.6).getAsNumber());
	}
	
	@Test
	void getAsNumberInvalid() {
		assertThrows(TomlTypeException.class, () -> new TomlValue(false).getAsNumber());
		assertThrows(TomlTypeException.class, () -> new TomlValue(true).getAsNumber());
		assertThrows(TomlTypeException.class, () -> new TomlValue("test").getAsNumber());
		assertThrows(TomlTypeException.class, () -> new TomlValue("123").getAsNumber());
		assertThrows(TomlTypeException.class, () -> new TomlValue("").getAsNumber());
		assertThrows(TomlTypeException.class, () -> new TomlValue(LocalDate.now()).getAsNumber());
	}
	
	@Test
	void getAsIntegralTypesValid() {
		TomlValue intValue = new TomlValue(42);
		
		assertEquals((byte) 42, intValue.getAsByte());
		assertEquals((short) 42, intValue.getAsShort());
		assertEquals(42, intValue.getAsInteger());
		assertEquals(42L, intValue.getAsLong());
		
		assertEquals(1, new TomlValue(1L).getAsInteger());
		assertEquals(1L, new TomlValue((short) 1).getAsLong());
	}
	
	@Test
	void getAsIntegralTypesInvalid() {
		TomlValue booleanValue = new TomlValue(true);
		TomlValue stringValue = new TomlValue("test");
		TomlValue dateValue = new TomlValue(LocalDate.now());
		
		assertThrows(TomlTypeException.class, booleanValue::getAsByte);
		assertThrows(TomlTypeException.class, booleanValue::getAsShort);
		assertThrows(TomlTypeException.class, booleanValue::getAsInteger);
		assertThrows(TomlTypeException.class, booleanValue::getAsLong);
		
		assertThrows(TomlTypeException.class, stringValue::getAsByte);
		assertThrows(TomlTypeException.class, stringValue::getAsShort);
		assertThrows(TomlTypeException.class, stringValue::getAsInteger);
		assertThrows(TomlTypeException.class, stringValue::getAsLong);
		
		assertThrows(TomlTypeException.class, dateValue::getAsByte);
		assertThrows(TomlTypeException.class, dateValue::getAsShort);
		assertThrows(TomlTypeException.class, dateValue::getAsInteger);
		assertThrows(TomlTypeException.class, dateValue::getAsLong);
	}
	
	@Test
	void getAsIntegralTypesFromFloatingPoint() {
		TomlValue floatValue = new TomlValue(3.14f);
		TomlValue doubleValue = new TomlValue(3.14);
		
		assertEquals((byte) 3, floatValue.getAsByte());
		assertEquals((short) 3, floatValue.getAsShort());
		assertEquals(3, floatValue.getAsInteger());
		assertEquals(3L, floatValue.getAsLong());
		
		assertEquals((byte) 3, doubleValue.getAsByte());
		assertEquals((short) 3, doubleValue.getAsShort());
		assertEquals(3, doubleValue.getAsInteger());
		assertEquals(3L, doubleValue.getAsLong());
	}
	
	@Test
	void getAsFloatingPointTypesValid() {
		assertEquals(1.0f, new TomlValue((byte) 1).getAsFloat());
		assertEquals(1.0f, new TomlValue((short) 1).getAsFloat());
		assertEquals(1.0f, new TomlValue(1).getAsFloat());
		assertEquals(1.0f, new TomlValue(1L).getAsFloat());
		
		assertEquals(1.0, new TomlValue((byte) 1).getAsDouble());
		assertEquals(1.0, new TomlValue((short) 1).getAsDouble());
		assertEquals(1.0, new TomlValue(1).getAsDouble());
		assertEquals(1.0, new TomlValue(1L).getAsDouble());
		
		assertEquals(3.14f, new TomlValue(3.14f).getAsFloat());
		assertEquals(3.14, new TomlValue(3.14).getAsDouble());
		assertEquals(3.14f, new TomlValue(3.14).getAsFloat());
		assertEquals(3.14, new TomlValue(3.14f).getAsDouble(), 1.0e-6);
	}
	
	@Test
	void getAsFloatingPointTypesInvalid() {
		TomlValue booleanValue = new TomlValue(true);
		TomlValue stringValue = new TomlValue("test");
		
		assertThrows(TomlTypeException.class, booleanValue::getAsFloat);
		assertThrows(TomlTypeException.class, booleanValue::getAsDouble);
		
		assertThrows(TomlTypeException.class, stringValue::getAsFloat);
		assertThrows(TomlTypeException.class, stringValue::getAsDouble);
	}
	
	@Test
	void dateTimeTypeChecks() {
		TomlValue dateValue = new TomlValue(LocalDate.of(2025, 1, 15));
		TomlValue timeValue = new TomlValue(LocalTime.of(14, 30, 45));
		TomlValue dateTimeValue = new TomlValue(LocalDateTime.of(2025, 1, 15, 14, 30, 45));
		TomlValue offsetDateTimeValue = new TomlValue(OffsetDateTime.of(2025, 1, 15, 14, 30, 45, 0, ZoneOffset.UTC));
		
		assertTrue(dateValue.isLocalDate());
		assertFalse(dateValue.isLocalTime());
		assertFalse(dateValue.isLocalDateTime());
		assertFalse(dateValue.isOffsetDateTime());
		assertTrue(dateValue.isDateTime());
		
		assertFalse(timeValue.isLocalDate());
		assertTrue(timeValue.isLocalTime());
		assertFalse(timeValue.isLocalDateTime());
		assertFalse(timeValue.isOffsetDateTime());
		assertTrue(timeValue.isDateTime());
		
		assertFalse(dateTimeValue.isLocalDate());
		assertFalse(dateTimeValue.isLocalTime());
		assertTrue(dateTimeValue.isLocalDateTime());
		assertFalse(dateTimeValue.isOffsetDateTime());
		assertTrue(dateTimeValue.isDateTime());
		
		assertFalse(offsetDateTimeValue.isLocalDate());
		assertFalse(offsetDateTimeValue.isLocalTime());
		assertFalse(offsetDateTimeValue.isLocalDateTime());
		assertTrue(offsetDateTimeValue.isOffsetDateTime());
		assertTrue(offsetDateTimeValue.isDateTime());
		
		TomlValue stringValue = new TomlValue("test");
		assertFalse(stringValue.isDateTime());
	}
	
	@Test
	void dateTimeConversions() {
		LocalDate date = LocalDate.of(2025, 1, 15);
		LocalTime time = LocalTime.of(14, 30, 45);
		LocalDateTime dateTime = LocalDateTime.of(2025, 1, 15, 14, 30, 45);
		OffsetDateTime offsetDateTime = OffsetDateTime.of(2025, 1, 15, 14, 30, 45, 0, ZoneOffset.ofHours(2));
		
		TomlValue dateValue = new TomlValue(date);
		TomlValue timeValue = new TomlValue(time);
		TomlValue dateTimeValue = new TomlValue(dateTime);
		TomlValue offsetDateTimeValue = new TomlValue(offsetDateTime);
		
		assertEquals(date, dateValue.getAsLocalDate());
		assertEquals(time, timeValue.getAsLocalTime());
		assertEquals(dateTime, dateTimeValue.getAsLocalDateTime());
		assertEquals(offsetDateTime, offsetDateTimeValue.getAsOffsetDateTime());
	}
	
	@Test
	void dateTimeConversionsInvalid() {
		TomlValue stringValue = new TomlValue("2025-01-15");
		TomlValue numberValue = new TomlValue(42);
		TomlValue booleanValue = new TomlValue(true);
		
		assertThrows(TomlTypeException.class, stringValue::getAsLocalDate);
		assertThrows(TomlTypeException.class, stringValue::getAsLocalTime);
		assertThrows(TomlTypeException.class, stringValue::getAsLocalDateTime);
		assertThrows(TomlTypeException.class, stringValue::getAsOffsetDateTime);
		
		assertThrows(TomlTypeException.class, numberValue::getAsLocalDate);
		assertThrows(TomlTypeException.class, booleanValue::getAsLocalTime);
	}
	
	@Test
	void equalsAndHashCode() {
		TomlValue value1 = new TomlValue("test");
		TomlValue value2 = new TomlValue("test");
		TomlValue value3 = new TomlValue("different");
		TomlValue value4 = new TomlValue(42);
		TomlValue value5 = new TomlValue(42);
		
		assertEquals(value1, value2);
		assertEquals(value1.hashCode(), value2.hashCode());
		assertNotEquals(value1, value3);
		
		assertEquals(value4, value5);
		assertEquals(value4.hashCode(), value5.hashCode());
		
		assertNotEquals(value1, value4);
		assertNotEquals(value1, null);
		assertNotEquals(value1, "not a value");
		
		assertEquals(value1, value1);
		
		LocalDate date = LocalDate.of(2025, 1, 15);
		TomlValue date1 = new TomlValue(date);
		TomlValue date2 = new TomlValue(date);
		TomlValue date3 = new TomlValue(LocalDate.of(2025, 1, 16));
		
		assertEquals(date1, date2);
		assertEquals(date1.hashCode(), date2.hashCode());
		assertNotEquals(date1, date3);
	}
	
	@Test
	void toStringWithDefaultConfig() {
		assertEquals("false", new TomlValue(false).toString());
		assertEquals("true", new TomlValue(true).toString());
		
		assertEquals("42", new TomlValue(42).toString());
		assertEquals("3.14", new TomlValue(3.14).toString());
		assertEquals("1", new TomlValue((byte) 1).toString());
		assertEquals("2", new TomlValue((short) 2).toString());
		assertEquals("3", new TomlValue(3L).toString());
		assertEquals("4.5", new TomlValue(4.5f).toString());
		
		assertEquals("\"test\"", new TomlValue("test").toString());
		assertEquals("\"\"", new TomlValue("").toString());
		
		assertEquals("2025-01-15", new TomlValue(LocalDate.of(2025, 1, 15)).toString());
		assertEquals("14:30:45", new TomlValue(LocalTime.of(14, 30, 45)).toString());
	}
	
	@Test
	void toStringWithCustomConfig() {
		TomlValue booleanValue = new TomlValue(true);
		TomlValue numberValue = new TomlValue(42);
		TomlValue stringValue = new TomlValue("test");
		
		assertThrows(NullPointerException.class, () -> booleanValue.toString(null));
		assertThrows(NullPointerException.class, () -> numberValue.toString(null));
		assertThrows(NullPointerException.class, () -> stringValue.toString(null));
		
		assertEquals("true", booleanValue.toString(CUSTOM_CONFIG));
		assertEquals("42", numberValue.toString(CUSTOM_CONFIG));
		assertEquals("\"test\"", stringValue.toString(CUSTOM_CONFIG));
	}
	
	@Test
	void toStringSpecialNumbers() {
		assertEquals("inf", new TomlValue(Double.POSITIVE_INFINITY).toString());
		assertEquals("-inf", new TomlValue(Double.NEGATIVE_INFINITY).toString());
		assertEquals("nan", new TomlValue(Double.NaN).toString());
		
		assertEquals("inf", new TomlValue(Float.POSITIVE_INFINITY).toString());
		assertEquals("-inf", new TomlValue(Float.NEGATIVE_INFINITY).toString());
		assertEquals("nan", new TomlValue(Float.NaN).toString());
	}
	
	@Test
	void toStringEscapedCharacters() {
		assertEquals("\"hello\\nworld\"", new TomlValue("hello\nworld").toString());
		assertEquals("\"hello\\tworld\"", new TomlValue("hello\tworld").toString());
		assertEquals("\"hello\\\"world\"", new TomlValue("hello\"world").toString());
		assertEquals("\"hello\\\\world\"", new TomlValue("hello\\world").toString());
	}
	
	@Test
	void extremeValues() {
		TomlValue maxInt = new TomlValue(Integer.MAX_VALUE);
		TomlValue minInt = new TomlValue(Integer.MIN_VALUE);
		TomlValue maxLong = new TomlValue(Long.MAX_VALUE);
		TomlValue minLong = new TomlValue(Long.MIN_VALUE);
		TomlValue maxDouble = new TomlValue(Double.MAX_VALUE);
		TomlValue minDouble = new TomlValue(Double.MIN_VALUE);
		
		assertEquals(Integer.MAX_VALUE, maxInt.getAsInteger());
		assertEquals(Integer.MIN_VALUE, minInt.getAsInteger());
		assertEquals(Long.MAX_VALUE, maxLong.getAsLong());
		assertEquals(Long.MIN_VALUE, minLong.getAsLong());
		assertEquals(Double.MAX_VALUE, maxDouble.getAsDouble());
		assertEquals(Double.MIN_VALUE, minDouble.getAsDouble());
		
		String longString = "a".repeat(10000);
		TomlValue longStringValue = new TomlValue(longString);
		assertEquals(longString, longStringValue.getAsString());
	}
	
	@Test
	void primitiveTypeChecks() {
		TomlValue boolValue = new TomlValue(true);
		TomlValue byteValue = new TomlValue((byte) 1);
		TomlValue shortValue = new TomlValue((short) 2);
		TomlValue intValue = new TomlValue(3);
		TomlValue longValue = new TomlValue(4L);
		TomlValue floatValue = new TomlValue(5.5f);
		TomlValue doubleValue = new TomlValue(6.6);
		TomlValue stringValue = new TomlValue("test");
		
		assertTrue(boolValue.isBoolean());
		assertFalse(boolValue.isNumber());
		assertFalse(boolValue.isString());
		
		assertFalse(byteValue.isBoolean());
		assertTrue(byteValue.isNumber());
		assertTrue(byteValue.isByte());
		assertFalse(byteValue.isString());
		
		assertTrue(shortValue.isShort());
		assertTrue(intValue.isInteger());
		assertTrue(longValue.isLong());
		assertTrue(floatValue.isFloat());
		assertTrue(doubleValue.isDouble());
		
		assertFalse(stringValue.isBoolean());
		assertFalse(stringValue.isNumber());
		assertTrue(stringValue.isString());
	}
	
	@Test
	void dateTimeFormatWithDifferentStyles() {
		OffsetDateTime offsetDateTime = OffsetDateTime.of(2025, 1, 15, 14, 30, 45, 0, ZoneOffset.ofHours(2));
		TomlValue value = new TomlValue(offsetDateTime);
		
		TomlConfig rfc3339Config = new TomlConfig(
			true, true, "  ",
			false, 3, true, 10, false, 80, true,
			TomlConfig.DateTimeStyle.RFC_3339, false, StandardCharsets.UTF_8
		);
		TomlConfig iso8601Config = new TomlConfig(
			true, true, "  ",
			false, 3, true, 10, false, 80, true,
			TomlConfig.DateTimeStyle.ISO_8601, false, StandardCharsets.UTF_8
		);
		TomlConfig spaceSeparatedConfig = new TomlConfig(
			true, true, "  ",
			false, 3, true, 10, false, 80, true,
			TomlConfig.DateTimeStyle.SPACE_SEPARATED, false, StandardCharsets.UTF_8
		);
		
		String rfc3339 = value.toString(rfc3339Config);
		String iso8601 = value.toString(iso8601Config);
		String spaceSeparated = value.toString(spaceSeparatedConfig);
		
		assertTrue(rfc3339.contains("+02:00"));
		assertFalse(iso8601.contains("+02:00"));
		assertTrue(spaceSeparated.contains(" "));
		assertFalse(spaceSeparated.contains("T"));
	}
}
