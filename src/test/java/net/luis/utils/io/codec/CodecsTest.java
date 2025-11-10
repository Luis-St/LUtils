/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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

package net.luis.utils.io.codec;

import net.luis.utils.io.codec.internal.struct.EitherCodec;
import net.luis.utils.io.codec.internal.struct.UnitCodec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.Utils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.*;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.*;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Codecs}.<br>
 *
 * @author Luis-St
 */
public class CodecsTest {
	
	@Test
	void constantCodecsEncodeCorrectly() throws MalformedURLException {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		assertEquals(new JsonPrimitive(true), BOOLEAN.encode(provider, true));
		assertEquals(new JsonPrimitive((byte) 1), BYTE.encode(provider, (byte) 1));
		assertEquals(new JsonPrimitive((short) 1), SHORT.encode(provider, (short) 1));
		assertEquals(new JsonPrimitive(1), INTEGER.encode(provider, 1));
		assertEquals(new JsonPrimitive(1L), LONG.encode(provider, 1L));
		assertEquals(new JsonPrimitive(1.0F), FLOAT.encode(provider, 1.0F));
		assertEquals(new JsonPrimitive(1.0), DOUBLE.encode(provider, 1.0));
		assertEquals(new JsonPrimitive("test"), STRING.encode(provider, "test"));
		assertEquals(new JsonPrimitive("a"), CHARACTER.encode(provider, 'a'));
		
		assertEquals(new JsonArray(List.of(new JsonPrimitive(true))), BOOLEAN_ARRAY.encode(provider, new boolean[] { true }));
		assertEquals(new JsonArray(List.of(new JsonPrimitive((byte) 42))), BYTE_ARRAY.encode(provider, new byte[] { 42 }));
		assertEquals(new JsonArray(List.of(new JsonPrimitive((short) 42))), SHORT_ARRAY.encode(provider, new short[] { 42 }));
		assertEquals(new JsonArray(List.of(new JsonPrimitive(42))), INTEGER_ARRAY.encode(provider, new int[] { 42 }));
		assertEquals(new JsonArray(List.of(new JsonPrimitive(42L))), LONG_ARRAY.encode(provider, new long[] { 42 }));
		assertEquals(new JsonArray(List.of(new JsonPrimitive(42.0F))), FLOAT_ARRAY.encode(provider, new float[] { 42.0F }));
		assertEquals(new JsonArray(List.of(new JsonPrimitive(42.0))), DOUBLE_ARRAY.encode(provider, new double[] { 42.0 }));
		assertEquals(new JsonArray(List.of(new JsonPrimitive("a"))), CHARACTER_ARRAY.encode(provider, new char[] { 'a' }));
		
		assertEquals(new JsonArray(List.of(new JsonPrimitive(42))), INT_STREAM.encode(provider, IntStream.of(42)));
		assertEquals(new JsonArray(List.of(new JsonPrimitive(42L))), LONG_STREAM.encode(provider, LongStream.of(42)));
		assertEquals(new JsonArray(List.of(new JsonPrimitive(42.0))), DOUBLE_STREAM.encode(provider, DoubleStream.of(42.0)));
		
		assertEquals(new JsonPrimitive(Utils.EMPTY_UUID.toString()), UUID.encode(provider, Utils.EMPTY_UUID));
		
		LocalTime time = LocalTime.of(19, 2);
		assertEquals(new JsonPrimitive(time.toString()), LOCAL_TIME.encode(provider, time));
		
		LocalDate date = LocalDate.of(2025, 1, 6);
		assertEquals(new JsonPrimitive(date.toString()), LOCAL_DATE.encode(provider, date));
		
		LocalDateTime dateTime = LocalDateTime.of(2025, 1, 6, 19, 2);
		assertEquals(new JsonPrimitive(dateTime.toString()), LOCAL_DATE_TIME.encode(provider, dateTime));
		
		ZonedDateTime zonedDateTime = ZonedDateTime.of(dateTime, ZoneId.systemDefault());
		assertEquals(new JsonPrimitive(zonedDateTime.toString()), ZONED_DATE_TIME.encode(provider, zonedDateTime));
		
		Instant instant = Instant.ofEpochMilli(1751059065063L);
		assertEquals(new JsonPrimitive(instant.toString()), INSTANT.encode(provider, instant));
		
		Duration duration = Duration.ofMillis(1751059065063L);
		assertEquals(new JsonPrimitive("20266d 21h 17m 45s 63ms"), DURATION.encode(provider, duration));
		
		Period period = Period.of(1, 2, 3);
		assertEquals(new JsonPrimitive("1y 2m 3d"), PERIOD.encode(provider, period));
		
		assertEquals(new JsonPrimitive(StandardCharsets.UTF_8.name()), CHARSET.encode(provider, StandardCharsets.UTF_8));
		
		File file = new File("test.json");
		assertEquals(new JsonPrimitive(file.toString()), FILE.encode(provider, file));
		
		Path path = Path.of("test.json");
		assertEquals(new JsonPrimitive(path.toString()), PATH.encode(provider, path));
		
		java.net.URI uri = java.net.URI.create("https://www.luis-st.net");
		assertEquals(new JsonPrimitive(uri.toString()), URI.encode(provider, uri));
		
		java.net.URL url = java.net.URI.create("https://www.luis-st.net").toURL();
		assertEquals(new JsonPrimitive(url.toString()), URL.encode(provider, url));
	}
	
	@Test
	void constantCodecsDecodeCorrectly() throws MalformedURLException {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		assertEquals(true, BOOLEAN.decode(provider, new JsonPrimitive(true)));
		assertEquals((byte) 1, BYTE.decode(provider, new JsonPrimitive((byte) 1)));
		assertEquals((short) 1, SHORT.decode(provider, new JsonPrimitive((short) 1)));
		assertEquals(1, INTEGER.decode(provider, new JsonPrimitive(1)));
		assertEquals(1L, LONG.decode(provider, new JsonPrimitive(1)));
		assertEquals(1.0F, FLOAT.decode(provider, new JsonPrimitive(1.0F)));
		assertEquals(1.0, DOUBLE.decode(provider, new JsonPrimitive(1.0)));
		assertEquals("test", STRING.decode(provider, new JsonPrimitive("test")));
		assertEquals('a', CHARACTER.decode(provider, new JsonPrimitive("a")));
		
		assertArrayEquals(new boolean[] { true }, BOOLEAN_ARRAY.decode(provider, new JsonArray(List.of(new JsonPrimitive(true)))));
		assertArrayEquals(new byte[] { 42 }, BYTE_ARRAY.decode(provider, new JsonArray(List.of(new JsonPrimitive((byte) 42)))));
		assertArrayEquals(new short[] { 42 }, SHORT_ARRAY.decode(provider, new JsonArray(List.of(new JsonPrimitive((short) 42)))));
		assertArrayEquals(new int[] { 42 }, INTEGER_ARRAY.decode(provider, new JsonArray(List.of(new JsonPrimitive(42)))));
		assertArrayEquals(new long[] { 42 }, LONG_ARRAY.decode(provider, new JsonArray(List.of(new JsonPrimitive(42L)))));
		assertArrayEquals(new float[] { 42.0F }, FLOAT_ARRAY.decode(provider, new JsonArray(List.of(new JsonPrimitive(42.0F)))));
		assertArrayEquals(new double[] { 42.0 }, DOUBLE_ARRAY.decode(provider, new JsonArray(List.of(new JsonPrimitive(42.0)))));
		assertArrayEquals(new char[] { 'a' }, CHARACTER_ARRAY.decode(provider, new JsonArray(List.of(new JsonPrimitive("a")))));
		
		assertArrayEquals(new int[] { 42 }, INT_STREAM.decode(provider, new JsonArray(List.of(new JsonPrimitive(42)))).toArray());
		assertArrayEquals(new long[] { 42 }, LONG_STREAM.decode(provider, new JsonArray(List.of(new JsonPrimitive(42L)))).toArray());
		assertArrayEquals(new double[] { 42.0 }, DOUBLE_STREAM.decode(provider, new JsonArray(List.of(new JsonPrimitive(42.0)))).toArray());
		
		assertEquals(Utils.EMPTY_UUID, UUID.decode(provider, new JsonPrimitive(Utils.EMPTY_UUID.toString())));
		
		assertEquals(StandardCharsets.UTF_8, CHARSET.decode(provider, new JsonPrimitive(StandardCharsets.UTF_8.name())));
		
		File file = new File("test.json");
		assertEquals(file, FILE.decode(provider, new JsonPrimitive(file.toString())));
		
		Path path = Path.of("test.json");
		assertEquals(path, PATH.decode(provider, new JsonPrimitive(path.toString())));
		
		java.net.URI uri = java.net.URI.create("https://www.luis-st.net");
		assertEquals(uri, URI.decode(provider, new JsonPrimitive(uri.toString())));
		
		java.net.URL url = java.net.URI.create("https://www.luis-st.net").toURL();
		assertEquals(url, URL.decode(provider, new JsonPrimitive(url.toString())));
	}
	
	@Test
	void enumCodecs() {
		assertThrows(NullPointerException.class, () -> enumOrdinal((Class<TestEnum>) null));
		assertThrows(NullPointerException.class, () -> enumName((Class<TestEnum>) null));
		assertThrows(NullPointerException.class, () -> dynamicEnum((Class<TestEnum>) null));
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		Codec<TestEnum> ordinalCodec = enumOrdinal(TestEnum.class);
		assertInstanceOf(KeyableCodec.class, ordinalCodec);
		assertEquals(new JsonPrimitive(0), ordinalCodec.encode(provider, TestEnum.ONE));
		assertEquals(TestEnum.ONE, ordinalCodec.decode(provider, new JsonPrimitive(0)));
		
		Codec<TestEnum> nameCodec = enumName(TestEnum.class);
		assertInstanceOf(KeyableCodec.class, nameCodec);
		assertEquals(new JsonPrimitive("ONE"), nameCodec.encode(provider, TestEnum.ONE));
		assertEquals(TestEnum.ONE, nameCodec.decode(provider, new JsonPrimitive("ONE")));
		
		Codec<TestEnum> dynamicCodec = dynamicEnum(TestEnum.class);
		assertInstanceOf(KeyableCodec.class, dynamicCodec);
		assertEquals(new JsonPrimitive("ONE"), dynamicCodec.encode(provider, TestEnum.ONE));
		assertEquals(TestEnum.ONE, dynamicCodec.decode(provider, new JsonPrimitive("ONE")));
		assertEquals(TestEnum.ONE, dynamicCodec.decode(provider, new JsonPrimitive(0)));
		
		Function<TestEnum, String> toFriendly = e -> e.name().toLowerCase();
		Function<String, TestEnum> fromFriendly = s -> TestEnum.valueOf(s.toUpperCase());
		assertThrows(NullPointerException.class, () -> friendlyEnumName(null, fromFriendly));
		assertThrows(NullPointerException.class, () -> friendlyEnumName(toFriendly, null));
		
		Codec<TestEnum> friendlyCodec = friendlyEnumName(toFriendly, fromFriendly);
		assertInstanceOf(KeyableCodec.class, friendlyCodec);
		assertEquals(new JsonPrimitive("one"), friendlyCodec.encode(provider, TestEnum.ONE));
		assertEquals(TestEnum.ONE, friendlyCodec.decode(provider, new JsonPrimitive("one")));
	}
	
	@Test
	void eitherCodecs() {
		assertThrows(NullPointerException.class, () -> either((Codec<Integer>) null, BOOLEAN));
		assertThrows(NullPointerException.class, () -> either(INTEGER, (Codec<Boolean>) null));
		assertInstanceOf(EitherCodec.class, either(INTEGER, BOOLEAN));
	}
	
	@Test
	void unitCodecs() {
		assertDoesNotThrow(() -> unit((Object) null));
		assertThrows(NullPointerException.class, () -> unit((Supplier<?>) null));
		
		assertInstanceOf(UnitCodec.class, unit("test"));
		assertInstanceOf(UnitCodec.class, unit(() -> "test"));
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		Codec<String> unitCodec = unit("constant");
		
		JsonElement encoded = unitCodec.encode(provider, "anything");
		assertFalse(encoded.isJsonNull());
		assertFalse(encoded.isJsonPrimitive());
		assertFalse(encoded.isJsonArray());
		assertFalse(encoded.isJsonObject());
		
		String decoded = unitCodec.decode(provider, JsonNull.INSTANCE);
		assertEquals("constant", decoded);
	}
	
	@Test
	void stringCodecs() {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		assertThrows(IllegalArgumentException.class, () -> string(-1));
		assertThrows(IllegalArgumentException.class, () -> string(-1, 2));
		assertThrows(IllegalArgumentException.class, () -> string(2, 1));
		
		Codec<String> limitedCodec = string(2);
		assertInstanceOf(KeyableCodec.class, limitedCodec);
		assertTrue(limitedCodec.encodeStart(provider, provider.empty(), "ab").isSuccess());
		assertTrue(limitedCodec.encodeStart(provider, provider.empty(), "abc").isError());
		assertTrue(limitedCodec.decodeStart(provider, new JsonPrimitive("ab")).isSuccess());
		assertTrue(limitedCodec.decodeStart(provider, new JsonPrimitive("abc")).isError());
		
		Codec<String> boundedCodec = string(2, 4);
		assertTrue(boundedCodec.encodeStart(provider, provider.empty(), "a").isError());
		assertTrue(boundedCodec.encodeStart(provider, provider.empty(), "abc").isSuccess());
		assertTrue(boundedCodec.encodeStart(provider, provider.empty(), "abcde").isError());
		
		Codec<String> nonEmptyCodec = noneEmptyString();
		assertInstanceOf(KeyableCodec.class, nonEmptyCodec);
		assertTrue(nonEmptyCodec.encodeStart(provider, provider.empty(), "").isError());
		assertTrue(nonEmptyCodec.encodeStart(provider, provider.empty(), "a").isSuccess());
	}
	
	@Test
	void stringCodecKeyValidation() {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		KeyableCodec<String> limitedCodec = string(2);
		
		assertTrue(limitedCodec.encodeKey(provider, "ab").isSuccess());
		assertTrue(limitedCodec.encodeKey(provider, "abc").isError());
		assertTrue(limitedCodec.encodeKey(provider, "").isSuccess());
		
		assertTrue(limitedCodec.decodeKey(provider, "ab").isSuccess());
		assertTrue(limitedCodec.decodeKey(provider, "abc").isError());
		assertTrue(limitedCodec.decodeKey(provider, "").isSuccess());
		
		KeyableCodec<String> boundedCodec = string(2, 4);
		
		assertTrue(boundedCodec.encodeKey(provider, "a").isError());
		assertTrue(boundedCodec.encodeKey(provider, "abc").isSuccess());
		assertTrue(boundedCodec.encodeKey(provider, "abcde").isError());
		
		assertTrue(boundedCodec.decodeKey(provider, "a").isError());
		assertTrue(boundedCodec.decodeKey(provider, "abc").isSuccess());
		assertTrue(boundedCodec.decodeKey(provider, "abcde").isError());
		
		KeyableCodec<String> nonEmptyCodec = noneEmptyString();
		
		assertTrue(nonEmptyCodec.encodeKey(provider, "").isError());
		assertTrue(nonEmptyCodec.encodeKey(provider, "a").isSuccess());
		assertTrue(nonEmptyCodec.decodeKey(provider, "").isError());
		assertTrue(nonEmptyCodec.decodeKey(provider, "a").isSuccess());
	}
	
	@Test
	void formattedStringCodecs() {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		assertThrows(NullPointerException.class, () -> formattedString((String) null));
		assertThrows(PatternSyntaxException.class, () -> formattedString("[invalid"));
		
		assertThrows(NullPointerException.class, () -> formattedString((Pattern) null));
		
		Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
		Codec<String> emailCodec = formattedString(emailPattern);
		assertInstanceOf(KeyableCodec.class, emailCodec);
		
		String validEmail = "test@example.com";
		assertEquals(new JsonPrimitive(validEmail), emailCodec.encode(provider, validEmail));
		assertEquals(validEmail, emailCodec.decode(provider, new JsonPrimitive(validEmail)));
		
		String invalidEmail = "not-an-email";
		assertTrue(emailCodec.encodeStart(provider, provider.empty(), invalidEmail).isError());
		assertTrue(emailCodec.decodeStart(provider, new JsonPrimitive(invalidEmail)).isError());
	}
	
	@Test
	void formattedStringKeyValidation() {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
		KeyableCodec<String> emailCodec = formattedString(emailPattern);
		
		String validEmail = "test@example.com";
		String invalidEmail = "not-an-email";
		
		assertTrue(emailCodec.encodeKey(provider, validEmail).isSuccess());
		assertTrue(emailCodec.encodeKey(provider, invalidEmail).isError());
		
		assertTrue(emailCodec.decodeKey(provider, validEmail).isSuccess());
		assertTrue(emailCodec.decodeKey(provider, invalidEmail).isError());
		
		KeyableCodec<String> digitsCodec = formattedString("\\d+");
		assertTrue(digitsCodec.encodeKey(provider, "12345").isSuccess());
		assertTrue(digitsCodec.encodeKey(provider, "abc123").isError());
		assertTrue(digitsCodec.decodeKey(provider, "12345").isSuccess());
		assertTrue(digitsCodec.decodeKey(provider, "abc123").isError());
	}
	
	@Test
	void formattedStringWithDigitsOnly() {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		Codec<String> digitsCodec = formattedString("\\d+");
		
		assertTrue(digitsCodec.encodeStart(provider, provider.empty(), "12345").isSuccess());
		assertTrue(digitsCodec.decodeStart(provider, new JsonPrimitive("12345")).isSuccess());
		assertEquals("12345", digitsCodec.decode(provider, new JsonPrimitive("12345")));
		
		assertTrue(digitsCodec.encodeStart(provider, provider.empty(), "abc123").isError());
		assertTrue(digitsCodec.decodeStart(provider, new JsonPrimitive("abc123")).isError());
		assertTrue(digitsCodec.encodeStart(provider, provider.empty(), "").isError());
	}
	
	@Test
	void formattedStringWithAlphanumeric() {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		Pattern alphanumericPattern = Pattern.compile("[a-zA-Z0-9]{3,10}");
		Codec<String> alphanumericCodec = formattedString(alphanumericPattern);
		
		assertTrue(alphanumericCodec.encodeStart(provider, provider.empty(), "abc123").isSuccess());
		assertTrue(alphanumericCodec.encodeStart(provider, provider.empty(), "ABC").isSuccess());
		assertTrue(alphanumericCodec.encodeStart(provider, provider.empty(), "1234567890").isSuccess());
		
		assertTrue(alphanumericCodec.encodeStart(provider, provider.empty(), "ab").isError());
		assertTrue(alphanumericCodec.encodeStart(provider, provider.empty(), "12345678901").isError());
		assertTrue(alphanumericCodec.encodeStart(provider, provider.empty(), "abc-123").isError());
	}
	
	@Test
	void formattedStringWithComplexPattern() {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		Codec<String> phoneCodec = formattedString("^\\+?[1-9]\\d{1,14}$");
		
		assertTrue(phoneCodec.encodeStart(provider, provider.empty(), "+1234567890").isSuccess());
		assertTrue(phoneCodec.encodeStart(provider, provider.empty(), "1234567890").isSuccess());
		assertTrue(phoneCodec.decodeStart(provider, new JsonPrimitive("+49123456789")).isSuccess());
		
		assertTrue(phoneCodec.encodeStart(provider, provider.empty(), "0123456789").isError()); // starts with 0
		assertTrue(phoneCodec.encodeStart(provider, provider.empty(), "+").isError()); // just plus
		assertTrue(phoneCodec.encodeStart(provider, provider.empty(), "123-456-7890").isError()); // contains dashes
	}
	
	@Test
	void formattedStringEmptyPattern() {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		Codec<String> emptyCodec = formattedString("^$");
		
		assertTrue(emptyCodec.encodeStart(provider, provider.empty(), "").isSuccess());
		assertTrue(emptyCodec.decodeStart(provider, new JsonPrimitive("")).isSuccess());
		
		assertTrue(emptyCodec.encodeStart(provider, provider.empty(), "a").isError());
		assertTrue(emptyCodec.decodeStart(provider, new JsonPrimitive("a")).isError());
	}
	
	@Test
	void formattedStringMatchesAnyCharacter() {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		Codec<String> singleCharCodec = formattedString("^.$");
		
		assertTrue(singleCharCodec.encodeStart(provider, provider.empty(), "a").isSuccess());
		assertTrue(singleCharCodec.encodeStart(provider, provider.empty(), "1").isSuccess());
		assertTrue(singleCharCodec.encodeStart(provider, provider.empty(), "@").isSuccess());
		
		assertTrue(singleCharCodec.encodeStart(provider, provider.empty(), "").isError());
		assertTrue(singleCharCodec.encodeStart(provider, provider.empty(), "ab").isError());
	}
	
	@Test
	void stringResolverCodecs() {
		assertThrows(NullPointerException.class, () -> stringResolver(null, Integer::valueOf));
		assertThrows(NullPointerException.class, () -> stringResolver(String::valueOf, null));
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		Codec<Integer> stringResolverCodec = stringResolver(String::valueOf, Integer::valueOf);
		
		JsonElement encoded = stringResolverCodec.encode(provider, 42);
		assertEquals(new JsonPrimitive("42"), encoded);
		
		Integer decoded = stringResolverCodec.decode(provider, new JsonPrimitive("42"));
		assertEquals(42, decoded);
		
		assertTrue(stringResolverCodec.decodeStart(provider, new JsonPrimitive("invalid")).isError());
	}
	
	private enum TestEnum {
		ONE, TWO, THREE
	}
}
