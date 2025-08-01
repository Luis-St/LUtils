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

import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.codec.struct.*;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.Result;
import net.luis.utils.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Codec}.<br>
 *
 * @author Luis-St
 */
class CodecTest {
	
	@Test
	void constantCodecsEncodeCorrectly() throws MalformedURLException {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		assertEquals(new JsonPrimitive(true), Codec.BOOLEAN.encode(provider, true));
		assertEquals(new JsonPrimitive((byte) 1), Codec.BYTE.encode(provider, (byte) 1));
		assertEquals(new JsonPrimitive((short) 1), Codec.SHORT.encode(provider, (short) 1));
		assertEquals(new JsonPrimitive(1), Codec.INTEGER.encode(provider, 1));
		assertEquals(new JsonPrimitive(1L), Codec.LONG.encode(provider, 1L));
		assertEquals(new JsonPrimitive(1.0F), Codec.FLOAT.encode(provider, 1.0F));
		assertEquals(new JsonPrimitive(1.0), Codec.DOUBLE.encode(provider, 1.0));
		assertEquals(new JsonPrimitive("test"), Codec.STRING.encode(provider, "test"));
		assertEquals(new JsonPrimitive("a"), Codec.CHARACTER.encode(provider, 'a'));
		
		assertEquals(new JsonArray(List.of(new JsonPrimitive(true))), Codec.BOOLEAN_ARRAY.encode(provider, new boolean[] { true }));
		assertEquals(new JsonArray(List.of(new JsonPrimitive((byte) 42))), Codec.BYTE_ARRAY.encode(provider, new byte[] { 42 }));
		assertEquals(new JsonArray(List.of(new JsonPrimitive((short) 42))), Codec.SHORT_ARRAY.encode(provider, new short[] { 42 }));
		assertEquals(new JsonArray(List.of(new JsonPrimitive(42))), Codec.INTEGER_ARRAY.encode(provider, new int[] { 42 }));
		assertEquals(new JsonArray(List.of(new JsonPrimitive(42L))), Codec.LONG_ARRAY.encode(provider, new long[] { 42 }));
		assertEquals(new JsonArray(List.of(new JsonPrimitive(42.0F))), Codec.FLOAT_ARRAY.encode(provider, new float[] { 42.0F }));
		assertEquals(new JsonArray(List.of(new JsonPrimitive(42.0))), Codec.DOUBLE_ARRAY.encode(provider, new double[] { 42.0 }));
		assertEquals(new JsonArray(List.of(new JsonPrimitive("a"))), Codec.CHARACTER_ARRAY.encode(provider, new char[] { 'a' }));
		
		assertEquals(new JsonArray(List.of(new JsonPrimitive(42))), Codec.INT_STREAM.encode(provider, IntStream.of(42)));
		assertEquals(new JsonArray(List.of(new JsonPrimitive(42L))), Codec.LONG_STREAM.encode(provider, LongStream.of(42)));
		assertEquals(new JsonArray(List.of(new JsonPrimitive(42.0))), Codec.DOUBLE_STREAM.encode(provider, DoubleStream.of(42.0)));
		
		assertEquals(new JsonPrimitive(Utils.EMPTY_UUID.toString()), Codec.UUID.encode(provider, Utils.EMPTY_UUID));
		
		LocalTime time = LocalTime.of(19, 2);
		assertEquals(new JsonPrimitive(time.toString()), Codec.LOCAL_TIME.encode(provider, time));
		
		LocalDate date = LocalDate.of(2025, 1, 6);
		assertEquals(new JsonPrimitive(date.toString()), Codec.LOCAL_DATE.encode(provider, date));
		
		LocalDateTime dateTime = LocalDateTime.of(2025, 1, 6, 19, 2);
		assertEquals(new JsonPrimitive(dateTime.toString()), Codec.LOCAL_DATE_TIME.encode(provider, dateTime));
		
		ZonedDateTime zonedDateTime = ZonedDateTime.of(dateTime, ZoneId.systemDefault());
		assertEquals(new JsonPrimitive(zonedDateTime.toString()), Codec.ZONED_DATE_TIME.encode(provider, zonedDateTime));
		
		Instant instant = Instant.ofEpochMilli(1751059065063L);
		assertEquals(new JsonPrimitive(instant.toString()), Codec.INSTANT.encode(provider, instant));
		
		Duration duration = Duration.ofMillis(1751059065063L);
		assertEquals(new JsonPrimitive("20266d 21h 17m 45s 63ms"), Codec.DURATION.encode(provider, duration));
		
		Period period = Period.of(1, 2, 3);
		assertEquals(new JsonPrimitive("1y 2m 3d"), Codec.PERIOD.encode(provider, period));
		
		assertEquals(new JsonPrimitive(StandardCharsets.UTF_8.name()), Codec.CHARSET.encode(provider, StandardCharsets.UTF_8));
		
		File file = new File("test.json");
		assertEquals(new JsonPrimitive(file.toString()), Codec.FILE.encode(provider, file));
		
		Path path = Path.of("test.json");
		assertEquals(new JsonPrimitive(path.toString()), Codec.PATH.encode(provider, path));
		
		URI uri = URI.create("https://www.luis-st.net");
		assertEquals(new JsonPrimitive(uri.toString()), Codec.URI.encode(provider, uri));
		
		URL url = URI.create("https://www.luis-st.net").toURL();
		assertEquals(new JsonPrimitive(url.toString()), Codec.URL.encode(provider, url));
	}
	
	@Test
	void constantCodecsDecodeCorrectly() throws MalformedURLException {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		assertEquals(true, Codec.BOOLEAN.decode(provider, new JsonPrimitive(true)));
		assertEquals((byte) 1, Codec.BYTE.decode(provider, new JsonPrimitive((byte) 1)));
		assertEquals((short) 1, Codec.SHORT.decode(provider, new JsonPrimitive((short) 1)));
		assertEquals(1, Codec.INTEGER.decode(provider, new JsonPrimitive(1)));
		assertEquals(1L, Codec.LONG.decode(provider, new JsonPrimitive(1)));
		assertEquals(1.0F, Codec.FLOAT.decode(provider, new JsonPrimitive(1.0F)));
		assertEquals(1.0, Codec.DOUBLE.decode(provider, new JsonPrimitive(1.0)));
		assertEquals("test", Codec.STRING.decode(provider, new JsonPrimitive("test")));
		assertEquals('a', Codec.CHARACTER.decode(provider, new JsonPrimitive("a")));
		
		assertArrayEquals(new boolean[] { true }, Codec.BOOLEAN_ARRAY.decode(provider, new JsonArray(List.of(new JsonPrimitive(true)))));
		assertArrayEquals(new byte[] { 42 }, Codec.BYTE_ARRAY.decode(provider, new JsonArray(List.of(new JsonPrimitive((byte) 42)))));
		assertArrayEquals(new short[] { 42 }, Codec.SHORT_ARRAY.decode(provider, new JsonArray(List.of(new JsonPrimitive((short) 42)))));
		assertArrayEquals(new int[] { 42 }, Codec.INTEGER_ARRAY.decode(provider, new JsonArray(List.of(new JsonPrimitive(42)))));
		assertArrayEquals(new long[] { 42 }, Codec.LONG_ARRAY.decode(provider, new JsonArray(List.of(new JsonPrimitive(42L)))));
		assertArrayEquals(new float[] { 42.0F }, Codec.FLOAT_ARRAY.decode(provider, new JsonArray(List.of(new JsonPrimitive(42.0F)))));
		assertArrayEquals(new double[] { 42.0 }, Codec.DOUBLE_ARRAY.decode(provider, new JsonArray(List.of(new JsonPrimitive(42.0)))));
		assertArrayEquals(new char[] { 'a' }, Codec.CHARACTER_ARRAY.decode(provider, new JsonArray(List.of(new JsonPrimitive("a")))));
		
		assertArrayEquals(new int[] { 42 }, Codec.INT_STREAM.decode(provider, new JsonArray(List.of(new JsonPrimitive(42)))).toArray());
		assertArrayEquals(new long[] { 42 }, Codec.LONG_STREAM.decode(provider, new JsonArray(List.of(new JsonPrimitive(42L)))).toArray());
		assertArrayEquals(new double[] { 42.0 }, Codec.DOUBLE_STREAM.decode(provider, new JsonArray(List.of(new JsonPrimitive(42.0)))).toArray());
		
		assertEquals(Utils.EMPTY_UUID, Codec.UUID.decode(provider, new JsonPrimitive(Utils.EMPTY_UUID.toString())));
		
		assertEquals(StandardCharsets.UTF_8, Codec.CHARSET.decode(provider, new JsonPrimitive(StandardCharsets.UTF_8.name())));
		
		File file = new File("test.json");
		assertEquals(file, Codec.FILE.decode(provider, new JsonPrimitive(file.toString())));
		
		Path path = Path.of("test.json");
		assertEquals(path, Codec.PATH.decode(provider, new JsonPrimitive(path.toString())));
		
		URI uri = URI.create("https://www.luis-st.net");
		assertEquals(uri, Codec.URI.decode(provider, new JsonPrimitive(uri.toString())));
		
		URL url = URI.create("https://www.luis-st.net").toURL();
		assertEquals(url, Codec.URL.decode(provider, new JsonPrimitive(url.toString())));
	}
	
	@Test
	void codecFactoryMethods() {
		assertThrows(NullPointerException.class, () -> Codec.of(null, Codec.INTEGER, "IntegerCodec"));
		assertThrows(NullPointerException.class, () -> Codec.of(Codec.INTEGER, null, "IntegerCodec"));
		assertThrows(NullPointerException.class, () -> Codec.of(Codec.INTEGER, Codec.INTEGER, null));
		assertNotNull(Codec.of(Codec.INTEGER, Codec.INTEGER, "IntegerCodec"));
	}
	
	@Test
	void keyableCodecFactoryMethods() {
		assertThrows(NullPointerException.class, () -> Codec.keyable((Codec<Integer>) null, ResultingFunction.direct(Integer::valueOf)));
		assertThrows(NullPointerException.class, () -> Codec.keyable(Codec.INTEGER, null));
		assertInstanceOf(KeyableCodec.class, Codec.keyable(Codec.INTEGER, ResultingFunction.direct(Integer::valueOf)));
		
		assertThrows(NullPointerException.class, () -> Codec.keyable((Codec<Integer>) null, ResultingFunction.direct(String::valueOf), ResultingFunction.direct(Integer::valueOf)));
		assertThrows(NullPointerException.class, () -> Codec.keyable(Codec.INTEGER, null, ResultingFunction.direct(Integer::valueOf)));
		assertThrows(NullPointerException.class, () -> Codec.keyable(Codec.INTEGER, ResultingFunction.direct(String::valueOf), null));
		assertInstanceOf(KeyableCodec.class, Codec.keyable(Codec.INTEGER, ResultingFunction.direct(String::valueOf), ResultingFunction.direct(Integer::valueOf)));
	}
	
	@Test
	void enumCodecs() {
		assertThrows(NullPointerException.class, () -> Codec.enumOrdinal((Class<TestEnum>) null));
		assertThrows(NullPointerException.class, () -> Codec.enumName((Class<TestEnum>) null));
		assertThrows(NullPointerException.class, () -> Codec.dynamicEnum((Class<TestEnum>) null));
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		Codec<TestEnum> ordinalCodec = Codec.enumOrdinal(TestEnum.class);
		assertInstanceOf(KeyableCodec.class, ordinalCodec);
		assertEquals(new JsonPrimitive(0), ordinalCodec.encode(provider, TestEnum.ONE));
		assertEquals(TestEnum.ONE, ordinalCodec.decode(provider, new JsonPrimitive(0)));
		
		Codec<TestEnum> nameCodec = Codec.enumName(TestEnum.class);
		assertInstanceOf(KeyableCodec.class, nameCodec);
		assertEquals(new JsonPrimitive("ONE"), nameCodec.encode(provider, TestEnum.ONE));
		assertEquals(TestEnum.ONE, nameCodec.decode(provider, new JsonPrimitive("ONE")));
		
		Codec<TestEnum> dynamicCodec = Codec.dynamicEnum(TestEnum.class);
		assertInstanceOf(KeyableCodec.class, dynamicCodec);
		assertEquals(new JsonPrimitive("ONE"), dynamicCodec.encode(provider, TestEnum.ONE));
		assertEquals(TestEnum.ONE, dynamicCodec.decode(provider, new JsonPrimitive("ONE")));
		assertEquals(TestEnum.ONE, dynamicCodec.decode(provider, new JsonPrimitive(0)));
		
		Function<TestEnum, String> toFriendly = e -> e.name().toLowerCase();
		Function<String, TestEnum> fromFriendly = s -> TestEnum.valueOf(s.toUpperCase());
		assertThrows(NullPointerException.class, () -> Codec.friendlyEnumName(null, fromFriendly));
		assertThrows(NullPointerException.class, () -> Codec.friendlyEnumName(toFriendly, null));
		
		Codec<TestEnum> friendlyCodec = Codec.friendlyEnumName(toFriendly, fromFriendly);
		assertInstanceOf(KeyableCodec.class, friendlyCodec);
		assertEquals(new JsonPrimitive("one"), friendlyCodec.encode(provider, TestEnum.ONE));
		assertEquals(TestEnum.ONE, friendlyCodec.decode(provider, new JsonPrimitive("one")));
	}
	
	@Test
	void stringCodecs() {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		assertThrows(IllegalArgumentException.class, () -> Codec.string(-1));
		assertThrows(IllegalArgumentException.class, () -> Codec.string(-1, 2));
		assertThrows(IllegalArgumentException.class, () -> Codec.string(2, 1));
		
		Codec<String> limitedCodec = Codec.string(2);
		assertInstanceOf(KeyableCodec.class, limitedCodec);
		assertTrue(limitedCodec.encodeStart(provider, provider.empty(), "ab").isSuccess());
		assertTrue(limitedCodec.encodeStart(provider, provider.empty(), "abc").isError());
		assertTrue(limitedCodec.decodeStart(provider, new JsonPrimitive("ab")).isSuccess());
		assertTrue(limitedCodec.decodeStart(provider, new JsonPrimitive("abc")).isError());
		
		Codec<String> boundedCodec = Codec.string(2, 4);
		assertTrue(boundedCodec.encodeStart(provider, provider.empty(), "a").isError());
		assertTrue(boundedCodec.encodeStart(provider, provider.empty(), "abc").isSuccess());
		assertTrue(boundedCodec.encodeStart(provider, provider.empty(), "abcde").isError());
		
		Codec<String> nonEmptyCodec = Codec.noneEmptyString();
		assertInstanceOf(KeyableCodec.class, nonEmptyCodec);
		assertTrue(nonEmptyCodec.encodeStart(provider, provider.empty(), "").isError());
		assertTrue(nonEmptyCodec.encodeStart(provider, provider.empty(), "a").isSuccess());
	}
	
	@Test
	void stringCodecKeyValidation() {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		KeyableCodec<String> limitedCodec = Codec.string(2);
		
		assertTrue(limitedCodec.encodeKey(provider, "ab").isSuccess());
		assertTrue(limitedCodec.encodeKey(provider, "abc").isError());
		assertTrue(limitedCodec.encodeKey(provider, "").isSuccess());
		
		assertTrue(limitedCodec.decodeKey(provider, "ab").isSuccess());
		assertTrue(limitedCodec.decodeKey(provider, "abc").isError());
		assertTrue(limitedCodec.decodeKey(provider, "").isSuccess());
		
		KeyableCodec<String> boundedCodec = Codec.string(2, 4);
		
		assertTrue(boundedCodec.encodeKey(provider, "a").isError());
		assertTrue(boundedCodec.encodeKey(provider, "abc").isSuccess());
		assertTrue(boundedCodec.encodeKey(provider, "abcde").isError());
		
		assertTrue(boundedCodec.decodeKey(provider, "a").isError());
		assertTrue(boundedCodec.decodeKey(provider, "abc").isSuccess());
		assertTrue(boundedCodec.decodeKey(provider, "abcde").isError());
		
		KeyableCodec<String> nonEmptyCodec = Codec.noneEmptyString();
		
		assertTrue(nonEmptyCodec.encodeKey(provider, "").isError());
		assertTrue(nonEmptyCodec.encodeKey(provider, "a").isSuccess());
		assertTrue(nonEmptyCodec.decodeKey(provider, "").isError());
		assertTrue(nonEmptyCodec.decodeKey(provider, "a").isSuccess());
	}
	
	@Test
	void formattedStringCodecs() {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		assertThrows(NullPointerException.class, () -> Codec.formattedString((String) null));
		assertThrows(PatternSyntaxException.class, () -> Codec.formattedString("[invalid"));
		
		assertThrows(NullPointerException.class, () -> Codec.formattedString((Pattern) null));
		
		Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
		Codec<String> emailCodec = Codec.formattedString(emailPattern);
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
		KeyableCodec<String> emailCodec = Codec.formattedString(emailPattern);
		
		String validEmail = "test@example.com";
		String invalidEmail = "not-an-email";
		
		assertTrue(emailCodec.encodeKey(provider, validEmail).isSuccess());
		assertTrue(emailCodec.encodeKey(provider, invalidEmail).isError());
		
		assertTrue(emailCodec.decodeKey(provider, validEmail).isSuccess());
		assertTrue(emailCodec.decodeKey(provider, invalidEmail).isError());
		
		KeyableCodec<String> digitsCodec = Codec.formattedString("\\d+");
		assertTrue(digitsCodec.encodeKey(provider, "12345").isSuccess());
		assertTrue(digitsCodec.encodeKey(provider, "abc123").isError());
		assertTrue(digitsCodec.decodeKey(provider, "12345").isSuccess());
		assertTrue(digitsCodec.decodeKey(provider, "abc123").isError());
	}
	
	@Test
	void formattedStringWithDigitsOnly() {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		Codec<String> digitsCodec = Codec.formattedString("\\d+");
		
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
		Codec<String> alphanumericCodec = Codec.formattedString(alphanumericPattern);
		
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
		
		Codec<String> phoneCodec = Codec.formattedString("^\\+?[1-9]\\d{1,14}$");
		
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
		
		Codec<String> emptyCodec = Codec.formattedString("^$");
		
		assertTrue(emptyCodec.encodeStart(provider, provider.empty(), "").isSuccess());
		assertTrue(emptyCodec.decodeStart(provider, new JsonPrimitive("")).isSuccess());
		
		assertTrue(emptyCodec.encodeStart(provider, provider.empty(), "a").isError());
		assertTrue(emptyCodec.decodeStart(provider, new JsonPrimitive("a")).isError());
	}
	
	@Test
	void formattedStringMatchesAnyCharacter() {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		Codec<String> singleCharCodec = Codec.formattedString("^.$");
		
		assertTrue(singleCharCodec.encodeStart(provider, provider.empty(), "a").isSuccess());
		assertTrue(singleCharCodec.encodeStart(provider, provider.empty(), "1").isSuccess());
		assertTrue(singleCharCodec.encodeStart(provider, provider.empty(), "@").isSuccess());
		
		assertTrue(singleCharCodec.encodeStart(provider, provider.empty(), "").isError());
		assertTrue(singleCharCodec.encodeStart(provider, provider.empty(), "ab").isError());
	}
	
	@Test
	void unitCodecs() {
		assertDoesNotThrow(() -> Codec.unit((Object) null));
		assertThrows(NullPointerException.class, () -> Codec.unit((Supplier<?>) null));
		
		assertInstanceOf(UnitCodec.class, Codec.unit("test"));
		assertInstanceOf(UnitCodec.class, Codec.unit(() -> "test"));
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		Codec<String> unitCodec = Codec.unit("constant");
		
		JsonElement encoded = unitCodec.encode(provider, "anything");
		assertEquals(JsonNull.INSTANCE, encoded);
		
		String decoded = unitCodec.decode(provider, JsonNull.INSTANCE);
		assertEquals("constant", decoded);
	}
	
	@Test
	void optionalCodecs() {
		assertThrows(NullPointerException.class, () -> Codec.optional((Codec<Integer>) null));
		assertInstanceOf(OptionalCodec.class, Codec.optional(Codec.INTEGER));
		assertInstanceOf(OptionalCodec.class, Codec.INTEGER.optional());
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> optionalCodec = Codec.INTEGER.optional();
		
		JsonElement encodedPresent = optionalCodec.encode(provider, Optional.of(42));
		assertEquals(new JsonPrimitive(42), encodedPresent);
		Optional<Integer> decodedPresent = optionalCodec.decode(provider, encodedPresent);
		assertTrue(decodedPresent.isPresent());
		assertEquals(42, decodedPresent.get());
		
		JsonElement encodedEmpty = optionalCodec.encode(provider, Optional.empty());
		assertEquals(JsonNull.INSTANCE, encodedEmpty);
		Optional<Integer> decodedEmpty = optionalCodec.decode(provider, encodedEmpty);
		assertTrue(decodedEmpty.isEmpty());
	}
	
	@Test
	void listCodecs() {
		assertThrows(NullPointerException.class, () -> Codec.list((Codec<Integer>) null));
		assertThrows(NullPointerException.class, () -> Codec.noneEmptyList((Codec<Integer>) null));
		assertThrows(IllegalArgumentException.class, () -> Codec.list(Codec.INTEGER, -1));
		assertThrows(IllegalArgumentException.class, () -> Codec.list(Codec.INTEGER, -1, 2));
		assertThrows(IllegalArgumentException.class, () -> Codec.list(Codec.INTEGER, 2, 1));
		
		assertInstanceOf(ListCodec.class, Codec.list(Codec.INTEGER));
		assertInstanceOf(ListCodec.class, Codec.INTEGER.list());
		assertInstanceOf(ListCodec.class, Codec.noneEmptyList(Codec.INTEGER));
		assertInstanceOf(ListCodec.class, Codec.INTEGER.noneEmptyList());
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> listCodec = Codec.INTEGER.list();
		
		List<Integer> originalList = List.of(1, 2, 3);
		JsonElement encoded = listCodec.encode(provider, originalList);
		assertInstanceOf(JsonArray.class, encoded);
		assertEquals(3, ((JsonArray) encoded).size());
		
		List<Integer> decoded = listCodec.decode(provider, encoded);
		assertEquals(originalList, decoded);
		
		Codec<List<Integer>> limitedListCodec = Codec.INTEGER.list(2);
		assertTrue(limitedListCodec.encodeStart(provider, provider.empty(), List.of(1, 2)).isSuccess());
		assertTrue(limitedListCodec.encodeStart(provider, provider.empty(), List.of(1, 2, 3)).isError());
	}
	
	@Test
	void streamCodecs() {
		assertThrows(NullPointerException.class, () -> Codec.stream((Codec<Integer>) null));
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		Codec<Stream<Integer>> streamCodec = Codec.INTEGER.stream();
		
		JsonElement encoded = streamCodec.encode(provider, Stream.of(1, 2, 3));
		assertInstanceOf(JsonArray.class, encoded);
		assertEquals(3, ((JsonArray) encoded).size());
		
		Stream<Integer> decoded = streamCodec.decode(provider, encoded);
		assertEquals(List.of(1, 2, 3), decoded.toList());
	}
	
	@Test
	void mapCodecs() {
		assertThrows(NullPointerException.class, () -> Codec.map((Codec<Integer>) null));
		assertThrows(NullPointerException.class, () -> Codec.map((KeyableCodec<Integer>) null, Codec.BOOLEAN));
		assertThrows(NullPointerException.class, () -> Codec.map(Codec.INTEGER, (Codec<Boolean>) null));
		assertThrows(NullPointerException.class, () -> Codec.noneEmptyMap((KeyableCodec<Integer>) null, Codec.BOOLEAN));
		assertThrows(NullPointerException.class, () -> Codec.noneEmptyMap(Codec.INTEGER, (Codec<Boolean>) null));
		assertThrows(IllegalArgumentException.class, () -> Codec.map(Codec.INTEGER, Codec.BOOLEAN, -1));
		assertThrows(IllegalArgumentException.class, () -> Codec.map(Codec.INTEGER, Codec.BOOLEAN, -1, 2));
		assertThrows(IllegalArgumentException.class, () -> Codec.map(Codec.INTEGER, Codec.BOOLEAN, 2, 1));
		
		assertInstanceOf(MapCodec.class, Codec.map(Codec.INTEGER));
		assertInstanceOf(MapCodec.class, Codec.map(Codec.INTEGER, Codec.BOOLEAN));
		assertInstanceOf(MapCodec.class, Codec.noneEmptyMap(Codec.INTEGER, Codec.BOOLEAN));
	}
	
	@Test
	void eitherCodecs() {
		assertThrows(NullPointerException.class, () -> Codec.either((Codec<Integer>) null, Codec.BOOLEAN));
		assertThrows(NullPointerException.class, () -> Codec.either(Codec.INTEGER, (Codec<Boolean>) null));
		assertInstanceOf(EitherCodec.class, Codec.either(Codec.INTEGER, Codec.BOOLEAN));
	}
	
	@Test
	void alternativeCodecs() {
		Codec<Integer> alternative = Codec.DOUBLE.xmap(Integer::doubleValue, Double::intValue);
		assertThrows(NullPointerException.class, () -> Codec.withAlternative((Codec<Integer>) null, alternative));
		assertThrows(NullPointerException.class, () -> Codec.withAlternative(Codec.INTEGER, null));
		assertThrows(NullPointerException.class, () -> Codec.INTEGER.withAlternative(null));
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codecWithAlternative = Codec.withAlternative(Codec.INTEGER, alternative);
		
		JsonElement encoded = codecWithAlternative.encode(provider, 42);
		assertEquals(new JsonPrimitive(42), encoded);
		
		Integer decoded = codecWithAlternative.decode(provider, new JsonPrimitive(42.0));
		assertEquals(42, decoded);
	}
	
	@Test
	void stringResolverCodecs() {
		assertThrows(NullPointerException.class, () -> Codec.stringResolver(null, Integer::valueOf));
		assertThrows(NullPointerException.class, () -> Codec.stringResolver(String::valueOf, null));
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		Codec<Integer> stringResolverCodec = Codec.stringResolver(String::valueOf, Integer::valueOf);
		
		JsonElement encoded = stringResolverCodec.encode(provider, 42);
		assertEquals(new JsonPrimitive("42"), encoded);
		
		Integer decoded = stringResolverCodec.decode(provider, new JsonPrimitive("42"));
		assertEquals(42, decoded);
		
		assertTrue(stringResolverCodec.decodeStart(provider, new JsonPrimitive("invalid")).isError());
	}
	
	@Test
	void codecTransformations() {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		assertThrows(NullPointerException.class, () -> Codec.STRING.xmap(null, Integer::valueOf));
		assertThrows(NullPointerException.class, () -> Codec.STRING.xmap(String::valueOf, null));
		
		Codec<Integer> xmappedCodec = Codec.STRING.xmap(String::valueOf, Integer::valueOf);
		assertEquals(new JsonPrimitive("42"), xmappedCodec.encode(provider, 42));
		assertEquals(42, xmappedCodec.decode(provider, new JsonPrimitive("42")));
		
		Codec<Integer> flatMappedCodec = Codec.STRING.mapFlat(String::valueOf, ResultMappingFunction.throwable(Integer::valueOf));
		assertEquals(new JsonPrimitive("42"), flatMappedCodec.encode(provider, 42));
		assertEquals(42, flatMappedCodec.decode(provider, new JsonPrimitive("42")));
		assertTrue(flatMappedCodec.decodeStart(provider, new JsonPrimitive("invalid")).isError());
		
		assertThrows(NullPointerException.class, () -> Codec.STRING.validate(null));
		
		Codec<String> validatedCodec = Codec.STRING.validate(s -> {
			if (s.length() > 2) {
				return Result.error("Too long");
			}
			return Result.success(s);
		});
		assertEquals(new JsonPrimitive("ab"), validatedCodec.encode(provider, "ab"));
		assertEquals("ab", validatedCodec.decode(provider, new JsonPrimitive("ab")));
		assertTrue(validatedCodec.decodeStart(provider, new JsonPrimitive("abc")).isError());
		
		assertThrows(NullPointerException.class, () -> Codec.STRING.orElseGet(null));
		
		Codec<String> orElseCodec = Codec.STRING.orElse("default");
		assertEquals("default", orElseCodec.decode(provider, JsonNull.INSTANCE));
		
		Codec<String> orElseGetCodec = Codec.STRING.orElseGet(() -> "default");
		assertEquals("default", orElseGetCodec.decode(provider, JsonNull.INSTANCE));
	}
	
	@Test
	void namedAndConfiguredCodecs() {
		assertThrows(NullPointerException.class, () -> Codec.STRING.named(null));
		assertThrows(NullPointerException.class, () -> Codec.STRING.getter(null));
		assertThrows(NullPointerException.class, () -> Codec.STRING.configure(null, TestObject::name));
		assertThrows(NullPointerException.class, () -> Codec.STRING.configure("name", null));
		
		assertInstanceOf(NamedCodec.class, Codec.STRING.named("name"));
		assertInstanceOf(NamedCodec.class, Codec.STRING.named("name", "alias"));
		assertInstanceOf(ConfiguredCodec.class, Codec.STRING.getter(TestObject::name));
		assertInstanceOf(ConfiguredCodec.class, Codec.STRING.configure("name", TestObject::name));
		
		Codec<String> namedCodec = Codec.STRING.named("StringField");
		assertEquals("NamedCodec['StringField', StringCodec]", namedCodec.toString());
		
		ConfiguredCodec<String, TestObject> configuredCodec = Codec.STRING.configure("name", TestObject::name);
		assertNotNull(configuredCodec);
	}
	
	private enum TestEnum {
		ONE, TWO, THREE
	}
	
	private record TestObject(@NotNull String name) {}
}
