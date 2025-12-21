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
import net.luis.utils.io.codec.types.struct.*;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.Utils;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
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
	void constantCodecsEncodeCorrectly() throws Exception {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		assertEquals(new JsonPrimitive(true), BOOLEAN.encode(provider, true));
		assertEquals(new JsonPrimitive((byte) 1), BYTE.encode(provider, (byte) 1));
		assertEquals(new JsonPrimitive((short) 1), SHORT.encode(provider, (short) 1));
		assertEquals(new JsonPrimitive(1), INTEGER.encode(provider, 1));
		assertEquals(new JsonPrimitive(1L), LONG.encode(provider, 1L));
		assertEquals(new JsonPrimitive(1.0F), FLOAT.encode(provider, 1.0F));
		assertEquals(new JsonPrimitive(1.0), DOUBLE.encode(provider, 1.0));
		assertEquals(new JsonPrimitive("12345678901234567890"), BIG_INTEGER.encode(provider, new BigInteger("12345678901234567890")));
		assertEquals(new JsonPrimitive("123.456789"), BIG_DECIMAL.encode(provider, new BigDecimal("123.456789")));
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
		
		assertEquals(new JsonPrimitive("MONDAY"), DAY_OF_WEEK.encode(provider, DayOfWeek.MONDAY));
		assertEquals(new JsonPrimitive("JANUARY"), MONTH.encode(provider, Month.JANUARY));
		assertEquals(new JsonPrimitive(2025), YEAR.encode(provider, Year.of(2025)));
		
		LocalTime time = LocalTime.of(19, 2);
		assertEquals(new JsonPrimitive(time.toString()), LOCAL_TIME.encode(provider, time));
		
		LocalDate date = LocalDate.of(2025, 1, 6);
		assertEquals(new JsonPrimitive(date.toString()), LOCAL_DATE.encode(provider, date));
		
		LocalDateTime dateTime = LocalDateTime.of(2025, 1, 6, 19, 2);
		assertEquals(new JsonPrimitive(dateTime.toString()), LOCAL_DATE_TIME.encode(provider, dateTime));
		
		ZonedDateTime zonedDateTime = ZonedDateTime.of(dateTime, ZoneId.systemDefault());
		assertEquals(new JsonPrimitive(zonedDateTime.toString()), ZONED_DATE_TIME.encode(provider, zonedDateTime));
		
		assertEquals(new JsonPrimitive("Europe/Paris"), ZONE_ID.encode(provider, ZoneId.of("Europe/Paris")));
		assertEquals(new JsonPrimitive("+01:00"), ZONE_OFFSET.encode(provider, ZoneOffset.ofHours(1)));
		
		OffsetTime offsetTime = OffsetTime.of(19, 2, 0, 0, ZoneOffset.ofHours(1));
		assertEquals(new JsonPrimitive(offsetTime.toString()), OFFSET_TIME.encode(provider, offsetTime));
		
		OffsetDateTime offsetDateTime = OffsetDateTime.of(2025, 1, 6, 19, 2, 0, 0, ZoneOffset.ofHours(1));
		assertEquals(new JsonPrimitive(offsetDateTime.toString()), OFFSET_DATE_TIME.encode(provider, offsetDateTime));
		
		Instant instant = Instant.ofEpochMilli(1751059065063L);
		assertEquals(new JsonPrimitive(instant.toString()), INSTANT.encode(provider, instant));
		
		Duration duration = Duration.ofMillis(1751059065063L);
		assertEquals(new JsonPrimitive("20266d 21h 17m 45s 63ms"), DURATION.encode(provider, duration));
		
		Period period = Period.of(1, 2, 3);
		assertEquals(new JsonPrimitive("1y 2m 3d"), PERIOD.encode(provider, period));
		
		assertEquals(new JsonPrimitive(StandardCharsets.UTF_8.name()), CHARSET.encode(provider, StandardCharsets.UTF_8));
		
		Path path = Path.of("test.json");
		assertEquals(new JsonPrimitive(path.toString()), PATH.encode(provider, path));
		
		java.net.URI uri = java.net.URI.create("https://www.luis-st.net");
		assertEquals(new JsonPrimitive(uri.toString()), URI.encode(provider, uri));
		
		InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
		assertEquals(new JsonPrimitive("127.0.0.1"), INET_ADDRESS.encode(provider, inetAddress));
		
		InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 8080);
		assertEquals(new JsonPrimitive("127.0.0.1:8080"), INET_SOCKET_ADDRESS.encode(provider, inetSocketAddress));
		
		assertEquals(new JsonPrimitive("en-US"), LOCALE.encode(provider, Locale.US));
		assertEquals(new JsonPrimitive("USD"), CURRENCY.encode(provider, Currency.getInstance("USD")));

		assertEquals(new JsonPrimitive("SGVsbG8="), BASE64.encode(provider, new byte[] { 72, 101, 108, 108, 111 }));
	}
	
	@Test
	void constantCodecsDecodeCorrectly() throws Exception {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		assertEquals(true, BOOLEAN.decode(provider, new JsonPrimitive(true)));
		assertEquals((byte) 1, BYTE.decode(provider, new JsonPrimitive((byte) 1)));
		assertEquals((short) 1, SHORT.decode(provider, new JsonPrimitive((short) 1)));
		assertEquals(1, INTEGER.decode(provider, new JsonPrimitive(1)));
		assertEquals(1L, LONG.decode(provider, new JsonPrimitive(1)));
		assertEquals(1.0F, FLOAT.decode(provider, new JsonPrimitive(1.0F)));
		assertEquals(1.0, DOUBLE.decode(provider, new JsonPrimitive(1.0)));
		assertEquals(new BigInteger("12345678901234567890"), BIG_INTEGER.decode(provider, new JsonPrimitive("12345678901234567890")));
		assertEquals(new BigDecimal("123.456789"), BIG_DECIMAL.decode(provider, new JsonPrimitive("123.456789")));
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
		
		assertEquals(DayOfWeek.MONDAY, DAY_OF_WEEK.decode(provider, new JsonPrimitive("MONDAY")));
		assertEquals(Month.JANUARY, MONTH.decode(provider, new JsonPrimitive("JANUARY")));
		assertEquals(Year.of(2025), YEAR.decode(provider, new JsonPrimitive(2025)));
		
		LocalTime time = LocalTime.of(19, 2);
		assertEquals(time, LOCAL_TIME.decode(provider, new JsonPrimitive(time.toString())));
		
		LocalDate date = LocalDate.of(2025, 1, 6);
		assertEquals(date, LOCAL_DATE.decode(provider, new JsonPrimitive(date.toString())));
		
		LocalDateTime dateTime = LocalDateTime.of(2025, 1, 6, 19, 2);
		assertEquals(dateTime, LOCAL_DATE_TIME.decode(provider, new JsonPrimitive(dateTime.toString())));
		
		ZonedDateTime zonedDateTime = ZonedDateTime.of(dateTime, ZoneId.systemDefault());
		assertEquals(zonedDateTime, ZONED_DATE_TIME.decode(provider, new JsonPrimitive(zonedDateTime.toString())));
		
		assertEquals(ZoneId.of("Europe/Paris"), ZONE_ID.decode(provider, new JsonPrimitive("Europe/Paris")));
		assertEquals(ZoneOffset.ofHours(1), ZONE_OFFSET.decode(provider, new JsonPrimitive("+01:00")));
		
		OffsetTime offsetTime = OffsetTime.of(19, 2, 0, 0, ZoneOffset.ofHours(1));
		assertEquals(offsetTime, OFFSET_TIME.decode(provider, new JsonPrimitive(offsetTime.toString())));
		
		OffsetDateTime offsetDateTime = OffsetDateTime.of(2025, 1, 6, 19, 2, 0, 0, ZoneOffset.ofHours(1));
		assertEquals(offsetDateTime, OFFSET_DATE_TIME.decode(provider, new JsonPrimitive(offsetDateTime.toString())));
		
		Instant instant = Instant.ofEpochMilli(1751059065063L);
		assertEquals(instant, INSTANT.decode(provider, new JsonPrimitive(instant.toString())));
		
		Duration duration = Duration.ofMillis(1751059065063L);
		assertEquals(duration, DURATION.decode(provider, new JsonPrimitive("20266d 21h 17m 45s 63ms")));
		
		Period period = Period.of(1, 2, 3);
		assertEquals(period, PERIOD.decode(provider, new JsonPrimitive("1y 2m 3d")));
		
		assertEquals(StandardCharsets.UTF_8, CHARSET.decode(provider, new JsonPrimitive(StandardCharsets.UTF_8.name())));
		
		Path path = Path.of("test.json");
		assertEquals(path, PATH.decode(provider, new JsonPrimitive(path.toString())));
		
		java.net.URI uri = java.net.URI.create("https://www.luis-st.net");
		assertEquals(uri, URI.decode(provider, new JsonPrimitive(uri.toString())));
		
		InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
		assertEquals(inetAddress, INET_ADDRESS.decode(provider, new JsonPrimitive("127.0.0.1")));
		
		InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 8080);
		assertEquals(inetSocketAddress, INET_SOCKET_ADDRESS.decode(provider, new JsonPrimitive("127.0.0.1:8080")));
		
		assertEquals(Locale.US, LOCALE.decode(provider, new JsonPrimitive("en-US")));
		assertEquals(Currency.getInstance("USD"), CURRENCY.decode(provider, new JsonPrimitive("USD")));

		assertArrayEquals(new byte[] { 72, 101, 108, 108, 111 }, BASE64.decode(provider, new JsonPrimitive("SGVsbG8=")));
	}
	
	@Test
	void enumCodecs() {
		assertThrows(NullPointerException.class, () -> enumOrdinal((Class<TestEnum>) null));
		assertThrows(NullPointerException.class, () -> enumName((Class<TestEnum>) null));
		assertThrows(NullPointerException.class, () -> dynamicEnum((Class<TestEnum>) null));
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		Codec<TestEnum> ordinalCodec = enumOrdinal(TestEnum.class);
		assertInstanceOf(Codec.class, ordinalCodec);
		assertEquals(new JsonPrimitive(0), ordinalCodec.encode(provider, TestEnum.ONE));
		assertEquals(TestEnum.ONE, ordinalCodec.decode(provider, new JsonPrimitive(0)));
		
		Codec<TestEnum> nameCodec = enumName(TestEnum.class);
		assertInstanceOf(Codec.class, nameCodec);
		assertEquals(new JsonPrimitive("ONE"), nameCodec.encode(provider, TestEnum.ONE));
		assertEquals(TestEnum.ONE, nameCodec.decode(provider, new JsonPrimitive("ONE")));
		
		Codec<TestEnum> dynamicCodec = dynamicEnum(TestEnum.class);
		assertInstanceOf(Codec.class, dynamicCodec);
		assertEquals(new JsonPrimitive("ONE"), dynamicCodec.encode(provider, TestEnum.ONE));
		assertEquals(TestEnum.ONE, dynamicCodec.decode(provider, new JsonPrimitive("ONE")));
		assertEquals(TestEnum.ONE, dynamicCodec.decode(provider, new JsonPrimitive(0)));
		
		Function<TestEnum, String> toFriendly = e -> e.name().toLowerCase();
		Function<String, TestEnum> fromFriendly = s -> TestEnum.valueOf(s.toUpperCase());
		assertThrows(NullPointerException.class, () -> friendlyEnumName(null, fromFriendly));
		assertThrows(NullPointerException.class, () -> friendlyEnumName(toFriendly, null));
		
		Codec<TestEnum> friendlyCodec = friendlyEnumName(toFriendly, fromFriendly);
		assertInstanceOf(Codec.class, friendlyCodec);
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
	void discriminatedBy() {
		Map<String, Codec<? extends String>> codecs = Map.of("a", STRING, "b", STRING);
		DiscriminatedCodecProvider<String, String> provider = DiscriminatedCodecProvider.create(String.class, codecs);

		assertThrows(NullPointerException.class, () -> Codecs.discriminatedBy(null, STRING, provider));
		assertThrows(NullPointerException.class, () -> Codecs.discriminatedBy("field", null, provider));
		assertThrows(NullPointerException.class, () -> Codecs.discriminatedBy("field", STRING, null));
		assertInstanceOf(DiscriminatedCodec.class, Codecs.discriminatedBy("field", STRING, provider));
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
	void stringResolverCodecs() {
		assertThrows(NullPointerException.class, () -> stringResolver(null, Integer::valueOf));
		assertThrows(NullPointerException.class, () -> stringResolver(String::valueOf, null));

		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		Codec<Integer> stringResolverCodec = stringResolver(String::valueOf, Integer::valueOf);

		JsonElement encoded = stringResolverCodec.encode(provider, 42);
		assertEquals(new JsonPrimitive("42"), encoded);

		Integer decoded = stringResolverCodec.decode(provider, new JsonPrimitive("42"));
		assertEquals(42, decoded);

		assertTrue(stringResolverCodec.decodeStart(provider, provider.empty(), new JsonPrimitive("invalid")).isError());
	}

	@Test
	void recursiveCodecs() {
		assertThrows(NullPointerException.class, () -> recursive(null));

		record LinkedListNode(int value, LinkedListNode next) {}

		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		Codec<LinkedListNode> recursiveCodec = recursive(self ->
			CodecBuilder.of(
				INTEGER.fieldOf("value", LinkedListNode::value),
				self.nullable().fieldOf("next", LinkedListNode::next)
			).create(LinkedListNode::new)
		);

		assertNotNull(recursiveCodec);

		LinkedListNode list = new LinkedListNode(1, new LinkedListNode(2, new LinkedListNode(3, null)));
		JsonElement encoded = recursiveCodec.encode(provider, list);

		assertInstanceOf(JsonObject.class, encoded);
		JsonObject obj = (JsonObject) encoded;
		assertEquals(1, obj.get("value").getAsJsonPrimitive().getAsInteger());
		assertNotNull(obj.get("next"));

		LinkedListNode decoded = recursiveCodec.decode(provider, encoded);
		assertEquals(list, decoded);
	}

	private enum TestEnum {
		ONE, TWO, THREE
	}
}
