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

package net.luis.utils.io.codec.integration;

import net.luis.utils.io.codec.*;
import net.luis.utils.io.codec.provider.XmlTypeProvider;
import net.luis.utils.io.data.xml.*;
import net.luis.utils.util.Either;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.*;
import java.util.*;
import java.util.stream.*;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test class for {@link XmlTypeProvider}.<br>
 * Tests encoding and decoding of various objects using the XML type provider.<br>
 *
 * @author Luis-St
 */
class XmlTypeProviderIntegrationTest {
	
	private static final XmlTypeProvider PROVIDER = XmlTypeProvider.INSTANCE;
	
	private static @NotNull Codec<UltraComplexRecord> createUltraComplexCodec() {
		return CodecBuilder.group(
			UUID.configure("id", UltraComplexRecord::id),
			Codec.ranged(INTEGER, 0, 150).configure("age", UltraComplexRecord::age),
			formattedString("[a-zA-Z]+").configure("name", UltraComplexRecord::name),
			LOCAL_DATE_TIME.configure("timestamp", UltraComplexRecord::timestamp),
			DURATION.configure("elapsed", UltraComplexRecord::elapsed),
			Codec.map(UUID, DOUBLE).configure("metrics", UltraComplexRecord::metrics),
			Codecs.either(FILE, PATH).configure("location", UltraComplexRecord::location),
			Codec.nullable(CHARSET).configure("encoding", UltraComplexRecord::encoding),
			FLOAT_ARRAY.configure("scores", UltraComplexRecord::scores),
			INT_STREAM.configure("dataStream", UltraComplexRecord::dataStream),
			Codec.noneEmptyList(URI).configure("endpoints", UltraComplexRecord::endpoints),
			Codec.optional(ZONED_DATE_TIME).configure("lastModified", UltraComplexRecord::lastModified)
		).create(UltraComplexRecord::new);
	}
	
	@Test
	void encodeAndDecodePrimitiveTypes() {
		XmlElement boolEncoded = BOOLEAN.encode(PROVIDER, true);
		assertTrue(boolEncoded.isXmlValue());
		assertEquals("true", boolEncoded.getAsXmlValue().getUnescapedValue());
		assertTrue(BOOLEAN.decode(PROVIDER, boolEncoded));
		
		XmlElement byteEncoded = BYTE.encode(PROVIDER, (byte) 127);
		assertTrue(byteEncoded.isXmlValue());
		assertEquals("127", byteEncoded.getAsXmlValue().getUnescapedValue());
		assertEquals((byte) 127, BYTE.decode(PROVIDER, byteEncoded));
		
		XmlElement shortEncoded = SHORT.encode(PROVIDER, (short) 32000);
		assertTrue(shortEncoded.isXmlValue());
		assertEquals("32000", shortEncoded.getAsXmlValue().getUnescapedValue());
		assertEquals((short) 32000, SHORT.decode(PROVIDER, shortEncoded));
		
		XmlElement intEncoded = INTEGER.encode(PROVIDER, 42);
		assertTrue(intEncoded.isXmlValue());
		assertEquals("42", intEncoded.getAsXmlValue().getUnescapedValue());
		assertEquals(42, INTEGER.decode(PROVIDER, intEncoded));
		
		XmlElement longEncoded = LONG.encode(PROVIDER, 123456789L);
		assertTrue(longEncoded.isXmlValue());
		assertEquals("123456789", longEncoded.getAsXmlValue().getUnescapedValue());
		assertEquals(123456789L, LONG.decode(PROVIDER, longEncoded));
		
		XmlElement floatEncoded = FLOAT.encode(PROVIDER, 3.14f);
		assertTrue(floatEncoded.isXmlValue());
		assertEquals(3.14f, FLOAT.decode(PROVIDER, floatEncoded), 0.001f);
		
		XmlElement doubleEncoded = DOUBLE.encode(PROVIDER, 2.718281828);
		assertTrue(doubleEncoded.isXmlValue());
		assertEquals(2.718281828, DOUBLE.decode(PROVIDER, doubleEncoded), 0.0000001);
		
		XmlElement charEncoded = CHARACTER.encode(PROVIDER, 'A');
		assertTrue(charEncoded.isXmlValue());
		assertEquals("A", charEncoded.getAsXmlValue().getUnescapedValue());
		assertEquals('A', CHARACTER.decode(PROVIDER, charEncoded));
		
		XmlElement stringEncoded = STRING.encode(PROVIDER, "Hello, World!");
		assertTrue(stringEncoded.isXmlValue());
		assertEquals("Hello, World!", stringEncoded.getAsXmlValue().getUnescapedValue());
		assertEquals("Hello, World!", STRING.decode(PROVIDER, stringEncoded));
	}
	
	@Test
	void encodeAndDecodePrimitiveArrays() {
		boolean[] boolArray = { true, false, true };
		XmlElement boolArrayEncoded = BOOLEAN_ARRAY.encode(PROVIDER, boolArray);
		assertTrue(boolArrayEncoded.isXmlContainer());
		assertArrayEquals(boolArray, BOOLEAN_ARRAY.decode(PROVIDER, boolArrayEncoded));
		
		byte[] byteArray = { 1, 2, 3, 4, 5 };
		XmlElement byteArrayEncoded = BYTE_ARRAY.encode(PROVIDER, byteArray);
		assertTrue(byteArrayEncoded.isXmlContainer());
		assertArrayEquals(byteArray, BYTE_ARRAY.decode(PROVIDER, byteArrayEncoded));
		
		short[] shortArray = { 100, 200, 300 };
		XmlElement shortArrayEncoded = SHORT_ARRAY.encode(PROVIDER, shortArray);
		assertTrue(shortArrayEncoded.isXmlContainer());
		assertArrayEquals(shortArray, SHORT_ARRAY.decode(PROVIDER, shortArrayEncoded));
		
		int[] intArray = { 1, 2, 3, 4, 5 };
		XmlElement intArrayEncoded = INTEGER_ARRAY.encode(PROVIDER, intArray);
		assertTrue(intArrayEncoded.isXmlContainer());
		assertArrayEquals(intArray, INTEGER_ARRAY.decode(PROVIDER, intArrayEncoded));
		
		long[] longArray = { 1000L, 2000L, 3000L };
		XmlElement longArrayEncoded = LONG_ARRAY.encode(PROVIDER, longArray);
		assertTrue(longArrayEncoded.isXmlContainer());
		assertArrayEquals(longArray, LONG_ARRAY.decode(PROVIDER, longArrayEncoded));
		
		float[] floatArray = { 1.1f, 2.2f, 3.3f };
		XmlElement floatArrayEncoded = FLOAT_ARRAY.encode(PROVIDER, floatArray);
		assertTrue(floatArrayEncoded.isXmlContainer());
		assertArrayEquals(floatArray, FLOAT_ARRAY.decode(PROVIDER, floatArrayEncoded), 0.001f);
		
		double[] doubleArray = { 1.1, 2.2, 3.3 };
		XmlElement doubleArrayEncoded = DOUBLE_ARRAY.encode(PROVIDER, doubleArray);
		assertTrue(doubleArrayEncoded.isXmlContainer());
		assertArrayEquals(doubleArray, DOUBLE_ARRAY.decode(PROVIDER, doubleArrayEncoded), 0.0001);
		
		char[] charArray = { 'H', 'e', 'l', 'l', 'o' };
		XmlElement charArrayEncoded = CHARACTER_ARRAY.encode(PROVIDER, charArray);
		assertTrue(charArrayEncoded.isXmlContainer());
		assertArrayEquals(charArray, CHARACTER_ARRAY.decode(PROVIDER, charArrayEncoded));
	}
	
	@Test
	void encodeAndDecodeCollections() {
		List<Integer> intList = List.of(1, 2, 3, 4, 5);
		Codec<List<Integer>> intListCodec = INTEGER.list();
		XmlElement intListEncoded = intListCodec.encode(PROVIDER, intList);
		assertTrue(intListEncoded.isXmlContainer());
		assertEquals(intList, intListCodec.decode(PROVIDER, intListEncoded));
		
		List<String> stringList = List.of("apple", "banana", "cherry");
		Codec<List<String>> stringListCodec = STRING.list();
		XmlElement stringListEncoded = stringListCodec.encode(PROVIDER, stringList);
		assertTrue(stringListEncoded.isXmlContainer());
		assertEquals(stringList, stringListCodec.decode(PROVIDER, stringListEncoded));
		
		Map<String, Integer> stringIntMap = Map.of("one", 1, "two", 2, "three", 3);
		Codec<Map<String, Integer>> stringIntMapCodec = Codec.map(STRING, INTEGER);
		XmlElement stringIntMapEncoded = stringIntMapCodec.encode(PROVIDER, stringIntMap);
		assertTrue(stringIntMapEncoded.isXmlContainer());
		assertEquals(stringIntMap, stringIntMapCodec.decode(PROVIDER, stringIntMapEncoded));
		
		Integer[] intObjectArray = { 10, 20, 30, 40 };
		Codec<Integer[]> intObjectArrayCodec = INTEGER.array(Integer.class);
		XmlElement intObjectArrayEncoded = intObjectArrayCodec.encode(PROVIDER, intObjectArray);
		assertTrue(intObjectArrayEncoded.isXmlContainer());
		assertArrayEquals(intObjectArray, intObjectArrayCodec.decode(PROVIDER, intObjectArrayEncoded));
	}
	
	@Test
	void encodeAndDecodeTimeTypes() {
		LocalTime localTime = LocalTime.of(14, 30, 45);
		XmlElement localTimeEncoded = LOCAL_TIME.encode(PROVIDER, localTime);
		assertTrue(localTimeEncoded.isXmlValue());
		assertEquals("14:30:45", localTimeEncoded.getAsXmlValue().getUnescapedValue());
		assertEquals(localTime, LOCAL_TIME.decode(PROVIDER, localTimeEncoded));
		
		LocalDate localDate = LocalDate.of(2025, 10, 19);
		XmlElement localDateEncoded = LOCAL_DATE.encode(PROVIDER, localDate);
		assertTrue(localDateEncoded.isXmlValue());
		assertEquals("2025-10-19", localDateEncoded.getAsXmlValue().getUnescapedValue());
		assertEquals(localDate, LOCAL_DATE.decode(PROVIDER, localDateEncoded));
		
		LocalDateTime localDateTime = LocalDateTime.of(2025, 10, 19, 14, 30, 45);
		XmlElement localDateTimeEncoded = LOCAL_DATE_TIME.encode(PROVIDER, localDateTime);
		assertTrue(localDateTimeEncoded.isXmlValue());
		assertEquals("2025-10-19T14:30:45", localDateTimeEncoded.getAsXmlValue().getUnescapedValue());
		assertEquals(localDateTime, LOCAL_DATE_TIME.decode(PROVIDER, localDateTimeEncoded));
		
		ZonedDateTime zonedDateTime = ZonedDateTime.of(2025, 10, 19, 14, 30, 45, 0, ZoneId.of("UTC"));
		XmlElement zonedDateTimeEncoded = ZONED_DATE_TIME.encode(PROVIDER, zonedDateTime);
		assertTrue(zonedDateTimeEncoded.isXmlValue());
		assertEquals(zonedDateTime, ZONED_DATE_TIME.decode(PROVIDER, zonedDateTimeEncoded));
		
		Instant instant = Instant.parse("2025-10-19T14:30:45Z");
		XmlElement instantEncoded = INSTANT.encode(PROVIDER, instant);
		assertTrue(instantEncoded.isXmlValue());
		assertEquals("2025-10-19T14:30:45Z", instantEncoded.getAsXmlValue().getUnescapedValue());
		assertEquals(instant, INSTANT.decode(PROVIDER, instantEncoded));
		
		Duration duration = Duration.ofHours(2).plusMinutes(30);
		XmlElement durationEncoded = DURATION.encode(PROVIDER, duration);
		assertTrue(durationEncoded.isXmlValue());
		assertEquals(duration, DURATION.decode(PROVIDER, durationEncoded));
		
		Period period = Period.of(1, 6, 15);
		XmlElement periodEncoded = PERIOD.encode(PROVIDER, period);
		assertTrue(periodEncoded.isXmlValue());
		assertEquals(period, PERIOD.decode(PROVIDER, periodEncoded));
	}
	
	@Test
	void encodeAndDecodeIOTypes() throws Exception {
		Charset charset = StandardCharsets.UTF_8;
		XmlElement charsetEncoded = CHARSET.encode(PROVIDER, charset);
		assertTrue(charsetEncoded.isXmlValue());
		assertEquals("UTF-8", charsetEncoded.getAsXmlValue().getUnescapedValue());
		assertEquals(charset, CHARSET.decode(PROVIDER, charsetEncoded));
		
		File file = new File("/tmp/test.txt");
		XmlElement fileEncoded = FILE.encode(PROVIDER, file);
		assertTrue(fileEncoded.isXmlValue());
		assertEquals("/tmp/test.txt", fileEncoded.getAsXmlValue().getUnescapedValue());
		assertEquals(file, FILE.decode(PROVIDER, fileEncoded));
		
		Path path = Path.of("/tmp/test.txt");
		XmlElement pathEncoded = PATH.encode(PROVIDER, path);
		assertTrue(pathEncoded.isXmlValue());
		assertEquals("/tmp/test.txt", pathEncoded.getAsXmlValue().getUnescapedValue());
		assertEquals(path, PATH.decode(PROVIDER, pathEncoded));
		
		URI uri = new URI("https://example.com/path");
		XmlElement uriEncoded = URI.encode(PROVIDER, uri);
		assertTrue(uriEncoded.isXmlValue());
		assertEquals("https://example.com/path", uriEncoded.getAsXmlValue().getUnescapedValue());
		assertEquals(uri, URI.decode(PROVIDER, uriEncoded));
		
		URL url = java.net.URI.create("https://example.com/path").toURL();
		XmlElement urlEncoded = URL.encode(PROVIDER, url);
		assertTrue(urlEncoded.isXmlValue());
		assertEquals("https://example.com/path", urlEncoded.getAsXmlValue().getUnescapedValue());
		assertEquals(url, URL.decode(PROVIDER, urlEncoded));
	}
	
	@Test
	void encodeAndDecodeUUID() {
		java.util.UUID uuid = java.util.UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
		XmlElement uuidEncoded = UUID.encode(PROVIDER, uuid);
		assertTrue(uuidEncoded.isXmlValue());
		assertEquals("123e4567-e89b-12d3-a456-426614174000", uuidEncoded.getAsXmlValue().getUnescapedValue());
		assertEquals(uuid, UUID.decode(PROVIDER, uuidEncoded));
	}
	
	@Test
	void encodeAndDecodeStreams() {
		IntStream intStream = IntStream.of(1, 2, 3, 4, 5);
		XmlElement intStreamEncoded = INT_STREAM.encode(PROVIDER, intStream);
		assertTrue(intStreamEncoded.isXmlContainer());
		assertArrayEquals(new int[] { 1, 2, 3, 4, 5 }, INT_STREAM.decode(PROVIDER, intStreamEncoded).toArray());
		
		LongStream longStream = LongStream.of(100L, 200L, 300L);
		XmlElement longStreamEncoded = LONG_STREAM.encode(PROVIDER, longStream);
		assertTrue(longStreamEncoded.isXmlContainer());
		assertArrayEquals(new long[] { 100L, 200L, 300L }, LONG_STREAM.decode(PROVIDER, longStreamEncoded).toArray());
		
		DoubleStream doubleStream = DoubleStream.of(1.1, 2.2, 3.3);
		XmlElement doubleStreamEncoded = DOUBLE_STREAM.encode(PROVIDER, doubleStream);
		assertTrue(doubleStreamEncoded.isXmlContainer());
		assertArrayEquals(new double[] { 1.1, 2.2, 3.3 }, DOUBLE_STREAM.decode(PROVIDER, doubleStreamEncoded).toArray(), 0.0001);
		
		Stream<String> stringStream = Stream.of("alpha", "beta", "gamma");
		Codec<Stream<String>> stringStreamCodec = STRING.stream();
		XmlElement stringStreamEncoded = stringStreamCodec.encode(PROVIDER, stringStream);
		assertTrue(stringStreamEncoded.isXmlContainer());
		assertEquals(List.of("alpha", "beta", "gamma"), stringStreamCodec.decode(PROVIDER, stringStreamEncoded).toList());
	}
	
	@Test
	void encodeAndDecodeEnums() {
		KeyableCodec<TestEnum> ordinalCodec = enumOrdinal(TestEnum.class);
		XmlElement ordinalEncoded = ordinalCodec.encode(PROVIDER, TestEnum.SECOND);
		assertTrue(ordinalEncoded.isXmlValue());
		assertEquals("1", ordinalEncoded.getAsXmlValue().getUnescapedValue());
		assertEquals(TestEnum.SECOND, ordinalCodec.decode(PROVIDER, ordinalEncoded));
		
		KeyableCodec<TestEnum> nameCodec = enumName(TestEnum.class);
		XmlElement nameEncoded = nameCodec.encode(PROVIDER, TestEnum.THIRD);
		assertTrue(nameEncoded.isXmlValue());
		assertEquals("THIRD", nameEncoded.getAsXmlValue().getUnescapedValue());
		assertEquals(TestEnum.THIRD, nameCodec.decode(PROVIDER, nameEncoded));
		
		KeyableCodec<TestEnum> dynamicCodec = dynamicEnum(TestEnum.class);
		XmlElement dynamicNameEncoded = dynamicCodec.encode(PROVIDER, TestEnum.FIRST);
		assertTrue(dynamicNameEncoded.isXmlValue());
		assertEquals("FIRST", dynamicNameEncoded.getAsXmlValue().getUnescapedValue());
		assertEquals(TestEnum.FIRST, dynamicCodec.decode(PROVIDER, dynamicNameEncoded));
		
		XmlElement ordinalEncodedXml = new XmlValue("test", "1");
		assertEquals(TestEnum.SECOND, dynamicCodec.decode(PROVIDER, ordinalEncodedXml));
	}
	
	@Test
	void encodeAndDecodeComplexObjects_SimpleRecord() {
		Codec<Person> personCodec = CodecBuilder.group(
			STRING.configure("name", Person::name),
			INTEGER.configure("age", Person::age)
		).create(Person::new);
		
		Person person = new Person("Alice", 30);
		XmlElement personEncoded = personCodec.encode(PROVIDER, person);
		
		assertTrue(personEncoded.isXmlContainer());
		XmlContainer personContainer = personEncoded.getAsXmlContainer();
		XmlElement nameElement = personContainer.getElements().get("name");
		assertNotNull(nameElement);
		assertTrue(nameElement.isXmlValue());
		assertEquals("Alice", nameElement.getAsXmlValue().getUnescapedValue());
		
		Person personDecoded = personCodec.decode(PROVIDER, personEncoded);
		assertEquals(person, personDecoded);
	}
	
	@Test
	void encodeAndDecodeComplexObjects_NestedRecord() {
		Codec<Address> addressCodec = CodecBuilder.group(
			STRING.configure("street", Address::street),
			STRING.configure("city", Address::city),
			INTEGER.configure("zipCode", Address::zipCode)
		).create(Address::new);
		
		Codec<PersonWithAddress> personWithAddressCodec = CodecBuilder.group(
			STRING.configure("name", PersonWithAddress::name),
			INTEGER.configure("age", PersonWithAddress::age),
			addressCodec.configure("address", PersonWithAddress::address)
		).create(PersonWithAddress::new);
		
		Address address = new Address("123 Main St", "Springfield", 12345);
		PersonWithAddress person = new PersonWithAddress("Bob", 25, address);
		
		XmlElement personEncoded = personWithAddressCodec.encode(PROVIDER, person);
		assertTrue(personEncoded.isXmlContainer());
		
		PersonWithAddress personDecoded = personWithAddressCodec.decode(PROVIDER, personEncoded);
		assertEquals(person, personDecoded);
		assertEquals(address, personDecoded.address());
	}
	
	@Test
	void encodeAndDecodeComplexObjects_WithCollections() {
		Codec<Team> teamCodec = CodecBuilder.group(
			STRING.configure("teamName", Team::teamName),
			STRING.list().configure("members", Team::members)
		).create(Team::new);
		
		Team team = new Team("DevTeam", List.of("Alice", "Bob", "Charlie"));
		XmlElement teamEncoded = teamCodec.encode(PROVIDER, team);
		
		assertTrue(teamEncoded.isXmlContainer());
		XmlContainer teamContainer = teamEncoded.getAsXmlContainer();
		XmlElement teamNameElement = teamContainer.getElements().get("teamName");
		assertNotNull(teamNameElement);
		assertTrue(teamNameElement.isXmlValue());
		assertEquals("DevTeam", teamNameElement.getAsXmlValue().getUnescapedValue());
		
		Team teamDecoded = teamCodec.decode(PROVIDER, teamEncoded);
		assertEquals(team, teamDecoded);
		assertEquals(3, teamDecoded.members().size());
	}
	
	@Test
	void encodeAndDecodeComplexObjects_WithOptionals() {
		Codec<PersonWithOptionalEmail> personCodec = CodecBuilder.group(
			STRING.configure("name", PersonWithOptionalEmail::name),
			STRING.optional().configure("email", PersonWithOptionalEmail::email)
		).create(PersonWithOptionalEmail::new);
		
		PersonWithOptionalEmail personWithEmail = new PersonWithOptionalEmail("Alice", Optional.of("alice@example.com"));
		XmlElement encoded1 = personCodec.encode(PROVIDER, personWithEmail);
		PersonWithOptionalEmail decoded1 = personCodec.decode(PROVIDER, encoded1);
		assertEquals(personWithEmail, decoded1);
		assertTrue(decoded1.email().isPresent());
		assertEquals("alice@example.com", decoded1.email().get());
		
		PersonWithOptionalEmail personWithoutEmail = new PersonWithOptionalEmail("Bob", Optional.empty());
		XmlElement encoded2 = personCodec.encode(PROVIDER, personWithoutEmail);
		PersonWithOptionalEmail decoded2 = personCodec.decode(PROVIDER, encoded2);
		assertEquals(personWithoutEmail, decoded2);
		assertTrue(decoded2.email().isEmpty());
	}
	
	@Test
	void encodeAndDecodeComplexObjects_WithMaps() {
		Codec<Config> configCodec = CodecBuilder.group(
			STRING.configure("name", Config::name),
			Codec.map(STRING, INTEGER).configure("settings", Config::settings)
		).create(Config::new);
		
		Config config = new Config("AppConfig", Map.of("timeout", 30, "retries", 3, "maxConnections", 100));
		XmlElement configEncoded = configCodec.encode(PROVIDER, config);
		
		assertTrue(configEncoded.isXmlContainer());
		
		Config configDecoded = configCodec.decode(PROVIDER, configEncoded);
		assertEquals(config, configDecoded);
		assertEquals(3, configDecoded.settings().size());
		assertEquals(30, configDecoded.settings().get("timeout"));
	}
	
	@Test
	void encodeAndDecodeAutoMappedCodec() {
		Codec<AutoMappedPerson> autoCodec = CodecBuilder.autoMapCodec(AutoMappedPerson.class);
		
		AutoMappedPerson person = new AutoMappedPerson("Charlie", 35, "charlie@example.com");
		XmlElement personEncoded = autoCodec.encode(PROVIDER, person);
		
		assertTrue(personEncoded.isXmlContainer());
		XmlContainer personContainer = personEncoded.getAsXmlContainer();
		
		XmlElement nameElement = personContainer.getElements().get("name");
		assertNotNull(nameElement);
		assertTrue(nameElement.isXmlValue());
		assertEquals("Charlie", nameElement.getAsXmlValue().getUnescapedValue());
		
		AutoMappedPerson personDecoded = autoCodec.decode(PROVIDER, personEncoded);
		assertEquals(person, personDecoded);
	}
	
	@Test
	void encodeAndDecodeEmptyCollections() {
		List<String> emptyList = List.of();
		Codec<List<String>> listCodec = STRING.list();
		XmlElement emptyListEncoded = listCodec.encode(PROVIDER, emptyList);
		assertTrue(emptyListEncoded.isXmlContainer());
		assertTrue(listCodec.decode(PROVIDER, emptyListEncoded).isEmpty());
		
		Map<String, Integer> emptyMap = Map.of();
		Codec<Map<String, Integer>> mapCodec = Codec.map(STRING, INTEGER);
		XmlElement emptyMapEncoded = mapCodec.encode(PROVIDER, emptyMap);
		assertTrue(emptyMapEncoded.isXmlContainer());
		assertTrue(mapCodec.decode(PROVIDER, emptyMapEncoded).isEmpty());
	}
	
	@Test
	void encodeAndDecodeNestedCollections() {
		List<List<Integer>> nestedList = List.of(
			List.of(1, 2, 3),
			List.of(4, 5, 6),
			List.of(7, 8, 9)
		);
		Codec<List<List<Integer>>> nestedListCodec = INTEGER.list().list();
		XmlElement nestedListEncoded = nestedListCodec.encode(PROVIDER, nestedList);
		assertTrue(nestedListEncoded.isXmlContainer());
		assertEquals(nestedList, nestedListCodec.decode(PROVIDER, nestedListEncoded));
		
		Map<String, List<Integer>> mapOfLists = Map.of(
			"first", List.of(1, 2, 3),
			"second", List.of(4, 5, 6)
		);
		Codec<Map<String, List<Integer>>> mapOfListsCodec = Codec.map(STRING, INTEGER.list());
		XmlElement mapOfListsEncoded = mapOfListsCodec.encode(PROVIDER, mapOfLists);
		assertTrue(mapOfListsEncoded.isXmlContainer());
		assertEquals(mapOfLists, mapOfListsCodec.decode(PROVIDER, mapOfListsEncoded));
	}
	
	@Test
	void encodeAndDecodeXmlElementStructure() {
		XmlElement intEncoded = INTEGER.encode(PROVIDER, 42);
		assertTrue(intEncoded.isXmlValue());
		assertNotNull(intEncoded.getName());
		assertTrue(intEncoded.getName().contains(":generated"));
		
		List<Integer> list = List.of(1, 2, 3);
		XmlElement listEncoded = INTEGER.list().encode(PROVIDER, list);
		assertTrue(listEncoded.isXmlContainer());
		XmlContainer container = listEncoded.getAsXmlContainer();
		assertFalse(container.isEmpty());
		assertEquals(3, container.getElements().size());
	}
	
	@Test
	void encodeAndDecodeNumericKeys() {
		Map<String, String> mapWithNumericKeys = Map.of(
			"123", "numeric",
			"key", "normal",
			"456", "another"
		);
		
		Codec<Map<String, String>> mapCodec = Codec.map(STRING, STRING);
		XmlElement encoded = mapCodec.encode(PROVIDER, mapWithNumericKeys);
		
		assertTrue(encoded.isXmlContainer());
		
		Map<String, String> decoded = mapCodec.decode(PROVIDER, encoded);
		assertEquals(mapWithNumericKeys, decoded);
		assertTrue(decoded.containsKey("123"));
		assertEquals("numeric", decoded.get("123"));
	}
	
	@Test
	void encodeAndDecodeLargeListOfPrimitives() {
		List<Integer> largeList = IntStream.range(0, 1000).boxed().toList();
		Codec<List<Integer>> listCodec = INTEGER.list();
		XmlElement encoded = listCodec.encode(PROVIDER, largeList);
		List<Integer> decoded = listCodec.decode(PROVIDER, encoded);
		assertEquals(largeList, decoded);
		assertEquals(1000, decoded.size());
	}
	
	@Test
	void encodeAndDecodeLargeMapStructure() {
		Map<String, Integer> largeMap = IntStream.range(0, 100)
			.boxed()
			.collect(java.util.stream.Collectors.toMap(
				i -> "key" + i,
				i -> i * 10
			));
		
		Codec<Map<String, Integer>> mapCodec = Codec.map(STRING, INTEGER);
		XmlElement encoded = mapCodec.encode(PROVIDER, largeMap);
		Map<String, Integer> decoded = mapCodec.decode(PROVIDER, encoded);
		assertEquals(largeMap, decoded);
		assertEquals(100, decoded.size());
	}
	
	@Test
	void encodeAndDecodeLargeNestedListStructure() {
		List<List<List<Integer>>> largeNestedList = IntStream.range(0, 10)
			.mapToObj(i -> IntStream.range(0, 10)
				.mapToObj(j -> IntStream.range(0, 10)
					.mapToObj(k -> i * 100 + j * 10 + k)
					.toList())
				.toList())
			.toList();
		
		Codec<List<List<List<Integer>>>> codec = INTEGER.list().list().list();
		XmlElement encoded = codec.encode(PROVIDER, largeNestedList);
		List<List<List<Integer>>> decoded = codec.decode(PROVIDER, encoded);
		assertEquals(largeNestedList, decoded);
		assertEquals(10, decoded.size());
		assertEquals(10, decoded.getFirst().size());
		assertEquals(10, decoded.getFirst().getFirst().size());
	}
	
	@Test
	void encodeAndDecodeListOfComplexObjects() {
		Codec<Person> personCodec = CodecBuilder.group(
			STRING.configure("name", Person::name),
			INTEGER.configure("age", Person::age)
		).create(Person::new);
		
		List<Person> people = IntStream.range(0, 50)
			.mapToObj(i -> new Person("Person" + i, 20 + i))
			.toList();
		
		Codec<List<Person>> listCodec = personCodec.list();
		XmlElement encoded = listCodec.encode(PROVIDER, people);
		List<Person> decoded = listCodec.decode(PROVIDER, encoded);
		assertEquals(people, decoded);
		assertEquals(50, decoded.size());
	}
	
	@Test
	void encodeAndDecodeEitherWithComplexTypes() {
		Codec<Address> addressCodec = CodecBuilder.group(
			STRING.configure("street", Address::street),
			STRING.configure("city", Address::city),
			INTEGER.configure("zipCode", Address::zipCode)
		).create(Address::new);
		
		Codec<Person> personCodec = CodecBuilder.group(
			STRING.configure("name", Person::name),
			INTEGER.configure("age", Person::age)
		).create(Person::new);
		
		Codec<Either<Person, Address>> eitherCodec = Codecs.either(personCodec, addressCodec);
		
		Person person = new Person("John Doe", 30);
		Either<Person, Address> leftEither = Either.left(person);
		XmlElement encodedLeft = eitherCodec.encode(PROVIDER, leftEither);
		Either<Person, Address> decodedLeft = eitherCodec.decode(PROVIDER, encodedLeft);
		assertTrue(decodedLeft.isLeft());
		assertEquals(person, decodedLeft.leftOrThrow());
		
		Address address = new Address("456 Oak Ave", "Boston", 2101);
		Either<Person, Address> rightEither = Either.right(address);
		XmlElement encodedRight = eitherCodec.encode(PROVIDER, rightEither);
		Either<Person, Address> decodedRight = eitherCodec.decode(PROVIDER, encodedRight);
		assertTrue(decodedRight.isRight());
		assertEquals(address, decodedRight.rightOrThrow());
	}
	
	@Test
	void encodeAndDecodeListOfEither() {
		Codec<Either<Integer, String>> eitherCodec = Codecs.either(INTEGER, STRING);
		
		List<Either<Integer, String>> mixedList = List.of(
			Either.left(1),
			Either.right("hello"),
			Either.left(42),
			Either.right("world"),
			Either.left(100),
			Either.right("test")
		);
		
		Codec<List<Either<Integer, String>>> listCodec = eitherCodec.list();
		XmlElement encoded = listCodec.encode(PROVIDER, mixedList);
		List<Either<Integer, String>> decoded = listCodec.decode(PROVIDER, encoded);
		assertEquals(mixedList.size(), decoded.size());
		
		for (int i = 0; i < mixedList.size(); i++) {
			Either<Integer, String> expected = mixedList.get(i);
			Either<Integer, String> actual = decoded.get(i);
			assertEquals(expected.isLeft(), actual.isLeft());
			if (expected.isLeft()) {
				assertEquals(expected.leftOrThrow(), actual.leftOrThrow());
			} else {
				assertEquals(expected.rightOrThrow(), actual.rightOrThrow());
			}
		}
	}
	
	@Test
	void encodeAndDecodeNestedEither() {
		Codec<Either<Either<Integer, String>, Boolean>> nestedEitherCodec = Codecs.either(Codecs.either(INTEGER, STRING), BOOLEAN);
		
		Either<Either<Integer, String>, Boolean> leftLeft = Either.left(Either.left(42));
		XmlElement encodedLL = nestedEitherCodec.encode(PROVIDER, leftLeft);
		Either<Either<Integer, String>, Boolean> decodedLL = nestedEitherCodec.decode(PROVIDER, encodedLL);
		assertTrue(decodedLL.isLeft());
		assertTrue(decodedLL.leftOrThrow().isLeft());
		assertEquals(42, decodedLL.leftOrThrow().leftOrThrow());
		
		Either<Either<Integer, String>, Boolean> leftRight = Either.left(Either.right("hello"));
		XmlElement encodedLR = nestedEitherCodec.encode(PROVIDER, leftRight);
		Either<Either<Integer, String>, Boolean> decodedLR = nestedEitherCodec.decode(PROVIDER, encodedLR);
		assertTrue(decodedLR.isLeft());
		assertTrue(decodedLR.leftOrThrow().isRight());
		assertEquals("hello", decodedLR.leftOrThrow().rightOrThrow());
		
		Either<Either<Integer, String>, Boolean> right = Either.right(true);
		XmlElement encodedR = nestedEitherCodec.encode(PROVIDER, right);
		Either<Either<Integer, String>, Boolean> decodedR = nestedEitherCodec.decode(PROVIDER, encodedR);
		assertTrue(decodedR.isLeft());
		assertTrue(decodedR.leftOrThrow().isRight());
		assertEquals("true", decodedR.leftOrThrow().rightOrThrow());
	}
	
	@Test
	void encodeAndDecodeVeryLongString() {
		String longString = "A".repeat(10000);
		XmlElement encoded = STRING.encode(PROVIDER, longString);
		String decoded = STRING.decode(PROVIDER, encoded);
		assertEquals(longString, decoded);
		assertEquals(10000, decoded.length());
	}
	
	@Test
	void encodeAndDecodeUnicodeStrings() {
		String unicode = "Hello世界🌍مرحبا🚀Привет";
		XmlElement encoded = STRING.encode(PROVIDER, unicode);
		String decoded = STRING.decode(PROVIDER, encoded);
		assertEquals(unicode, decoded);
		
		List<String> unicodeList = List.of("日本語", "한국어", "中文", "العربية", "עברית");
		Codec<List<String>> listCodec = STRING.list();
		XmlElement encodedList = listCodec.encode(PROVIDER, unicodeList);
		List<String> decodedList = listCodec.decode(PROVIDER, encodedList);
		assertEquals(unicodeList, decodedList);
	}
	
	@Test
	void encodeAndDecodeEmptyStringEdgeCases() {
		assertEquals("", STRING.decode(PROVIDER, STRING.encode(PROVIDER, "")));
		
		List<String> listWithEmpty = List.of("", "a", "", "b", "");
		Codec<List<String>> listCodec = STRING.list();
		XmlElement encoded = listCodec.encode(PROVIDER, listWithEmpty);
		assertEquals(listWithEmpty, listCodec.decode(PROVIDER, encoded));
	}
	
	@Test
	void encodeAndDecodeExtremeNumericValues() {
		assertEquals(Byte.MIN_VALUE, BYTE.decode(PROVIDER, BYTE.encode(PROVIDER, Byte.MIN_VALUE)));
		assertEquals(Byte.MAX_VALUE, BYTE.decode(PROVIDER, BYTE.encode(PROVIDER, Byte.MAX_VALUE)));
		
		assertEquals(Short.MIN_VALUE, SHORT.decode(PROVIDER, SHORT.encode(PROVIDER, Short.MIN_VALUE)));
		assertEquals(Short.MAX_VALUE, SHORT.decode(PROVIDER, SHORT.encode(PROVIDER, Short.MAX_VALUE)));
		
		assertEquals(Integer.MIN_VALUE, INTEGER.decode(PROVIDER, INTEGER.encode(PROVIDER, Integer.MIN_VALUE)));
		assertEquals(Integer.MAX_VALUE, INTEGER.decode(PROVIDER, INTEGER.encode(PROVIDER, Integer.MAX_VALUE)));
		
		assertEquals(Long.MIN_VALUE, LONG.decode(PROVIDER, LONG.encode(PROVIDER, Long.MIN_VALUE)));
		assertEquals(Long.MAX_VALUE, LONG.decode(PROVIDER, LONG.encode(PROVIDER, Long.MAX_VALUE)));
		
		assertEquals(Float.MIN_VALUE, FLOAT.decode(PROVIDER, FLOAT.encode(PROVIDER, Float.MIN_VALUE)), 0.0f);
		assertEquals(Float.MAX_VALUE, FLOAT.decode(PROVIDER, FLOAT.encode(PROVIDER, Float.MAX_VALUE)), 1.0e31f);
		
		assertEquals(Double.MIN_VALUE, DOUBLE.decode(PROVIDER, DOUBLE.encode(PROVIDER, Double.MIN_VALUE)), 0.0);
		assertEquals(Double.MAX_VALUE, DOUBLE.decode(PROVIDER, DOUBLE.encode(PROVIDER, Double.MAX_VALUE)), 1.0e300);
	}
	
	@Test
	void encodeAndDecodeLargePrimitiveArrays() {
		int[] largeIntArray = IntStream.range(0, 1000).toArray();
		XmlElement encodedInt = INTEGER_ARRAY.encode(PROVIDER, largeIntArray);
		assertArrayEquals(largeIntArray, INTEGER_ARRAY.decode(PROVIDER, encodedInt));
		
		double[] largeDoubleArray = IntStream.range(0, 1000).mapToDouble(i -> i * 0.5).toArray();
		XmlElement encodedDouble = DOUBLE_ARRAY.encode(PROVIDER, largeDoubleArray);
		assertArrayEquals(largeDoubleArray, DOUBLE_ARRAY.decode(PROVIDER, encodedDouble), 0.0001);
		
		boolean[] largeBoolArray = new boolean[1000];
		for (int i = 0; i < 1000; i++) {
			largeBoolArray[i] = i % 2 == 0;
		}
		XmlElement encodedBool = BOOLEAN_ARRAY.encode(PROVIDER, largeBoolArray);
		assertArrayEquals(largeBoolArray, BOOLEAN_ARRAY.decode(PROVIDER, encodedBool));
	}
	
	@Test
	void encodeAndDecodeComplexNestedOptionals() {
		Codec<Optional<List<Optional<String>>>> complexOptionalCodec = STRING.optional().list().optional();
		
		Optional<List<Optional<String>>> presentMixed = Optional.of(List.of(
			Optional.of("a"), Optional.empty(), Optional.of("b"), Optional.empty()
		));
		XmlElement encoded = complexOptionalCodec.encode(PROVIDER, presentMixed);
		Optional<List<Optional<String>>> decodedPresent = complexOptionalCodec.decode(PROVIDER, encoded);
		assertTrue(decodedPresent.isPresent());
		assertEquals(2, decodedPresent.get().size());
		assertTrue(decodedPresent.get().get(0).isPresent());
		assertTrue(decodedPresent.get().get(1).isPresent());
		
		Optional<List<Optional<String>>> empty = Optional.empty();
		XmlElement encodedEmpty = complexOptionalCodec.encode(PROVIDER, empty);
		Optional<List<Optional<String>>> decodedEmpty = complexOptionalCodec.decode(PROVIDER, encodedEmpty);
		assertTrue(decodedEmpty.isEmpty());
	}
	
	@Test
	void encodeAndDecodeMapWithComplexValues() {
		Codec<Person> personCodec = CodecBuilder.group(
			STRING.configure("name", Person::name),
			INTEGER.configure("age", Person::age)
		).create(Person::new);
		
		Map<String, List<Person>> departmentEmployees = Map.of(
			"Engineering", List.of(
				new Person("Alice", 30),
				new Person("Bob", 35)
			),
			"Sales", List.of(
				new Person("Charlie", 28),
				new Person("Diana", 32)
			),
			"Marketing", List.of(
				new Person("Eve", 29)
			)
		);
		
		Codec<Map<String, List<Person>>> codec = Codec.map(STRING, personCodec.list());
		XmlElement encoded = codec.encode(PROVIDER, departmentEmployees);
		Map<String, List<Person>> decoded = codec.decode(PROVIDER, encoded);
		assertEquals(departmentEmployees, decoded);
		assertEquals(3, decoded.size());
		assertEquals(2, decoded.get("Engineering").size());
	}
	
	@Test
	void encodeAndDecodeDeeplyNestedStructure() {
		Codec<Address> addressCodec = CodecBuilder.group(
			STRING.configure("street", Address::street),
			STRING.configure("city", Address::city),
			INTEGER.configure("zipCode", Address::zipCode)
		).create(Address::new);
		
		Codec<PersonWithAddress> personCodec = CodecBuilder.group(
			STRING.configure("name", PersonWithAddress::name),
			INTEGER.configure("age", PersonWithAddress::age),
			addressCodec.configure("address", PersonWithAddress::address)
		).create(PersonWithAddress::new);
		
		Codec<OfficeWithPeople> officeCodec = CodecBuilder.group(
			STRING.configure("officeName", OfficeWithPeople::officeName),
			addressCodec.configure("location", OfficeWithPeople::location),
			personCodec.list().configure("employees", OfficeWithPeople::employees)
		).create(OfficeWithPeople::new);
		
		List<PersonWithAddress> employees = IntStream.range(0, 20)
			.mapToObj(i -> new PersonWithAddress(
				"Employee" + i,
				25 + i,
				new Address((100 + i) + " Street", "City" + i, 10000 + i)
			))
			.toList();
		
		OfficeWithPeople office = new OfficeWithPeople(
			"Main Office",
			new Address("1 Corporate Plaza", "New York", 10001),
			employees
		);
		
		XmlElement encoded = officeCodec.encode(PROVIDER, office);
		OfficeWithPeople decoded = officeCodec.decode(PROVIDER, encoded);
		assertEquals(office, decoded);
		assertEquals(20, decoded.employees().size());
	}
	
	@Test
	void encodeAndDecodeMapOfMapsOfLists() {
		Map<String, Map<String, List<Integer>>> complexNested = Map.of(
			"region1", Map.of(
				"store1", List.of(100, 200, 300),
				"store2", List.of(150, 250, 350)
			),
			"region2", Map.of(
				"store3", List.of(120, 220, 320),
				"store4", List.of(180, 280, 380)
			),
			"region3", Map.of(
				"store5", List.of(110, 210, 310)
			)
		);
		
		Codec<Map<String, Map<String, List<Integer>>>> codec =
			Codec.map(STRING, Codec.map(STRING, INTEGER.list()));
		
		XmlElement encoded = codec.encode(PROVIDER, complexNested);
		Map<String, Map<String, List<Integer>>> decoded = codec.decode(PROVIDER, encoded);
		assertEquals(complexNested, decoded);
		assertEquals(3, decoded.size());
		assertEquals(2, decoded.get("region1").size());
		assertEquals(3, decoded.get("region1").get("store1").size());
	}
	
	@Test
	void encodeAndDecodeConstrainedNumericCodecs() {
		KeyableCodec<Integer> rangedInt = Codec.ranged(INTEGER, 0, 100);
		XmlElement encoded50 = rangedInt.encode(PROVIDER, 50);
		assertEquals(50, rangedInt.decode(PROVIDER, encoded50));
		XmlElement encoded0 = rangedInt.encode(PROVIDER, 0);
		assertEquals(0, rangedInt.decode(PROVIDER, encoded0));
		XmlElement encoded100 = rangedInt.encode(PROVIDER, 100);
		assertEquals(100, rangedInt.decode(PROVIDER, encoded100));
		
		KeyableCodec<Long> atLeastLong = Codec.atLeast(LONG, 1000L);
		XmlElement encodedLong = atLeastLong.encode(PROVIDER, 5000L);
		assertEquals(5000L, atLeastLong.decode(PROVIDER, encodedLong));
		
		KeyableCodec<Double> atMostDouble = Codec.atMost(DOUBLE, 1.0);
		XmlElement encodedDouble = atMostDouble.encode(PROVIDER, 0.5);
		assertEquals(0.5, atMostDouble.decode(PROVIDER, encodedDouble));
	}
	
	@Test
	void encodeAndDecodeFormattedAndValidatedStrings() {
		KeyableCodec<String> emailCodec = formattedString("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
		String email = "test@example.com";
		XmlElement encodedEmail = emailCodec.encode(PROVIDER, email);
		assertEquals(email, emailCodec.decode(PROVIDER, encodedEmail));
		
		KeyableCodec<String> nonEmptyCodec = noneEmptyString();
		XmlElement encodedNonEmpty = nonEmptyCodec.encode(PROVIDER, "hello");
		assertEquals("hello", nonEmptyCodec.decode(PROVIDER, encodedNonEmpty));
		
		KeyableCodec<String> lengthCodec = string(5, 10);
		XmlElement encodedLength = lengthCodec.encode(PROVIDER, "hello");
		assertEquals("hello", lengthCodec.decode(PROVIDER, encodedLength));
	}
	
	@Test
	void encodeAndDecodeMapCodecWithVariousKeyTypes() {
		Codec<Map<java.util.UUID, String>> uuidMapCodec = Codec.map(UUID, STRING);
		java.util.UUID uuid1 = java.util.UUID.randomUUID();
		java.util.UUID uuid2 = java.util.UUID.randomUUID();
		Map<java.util.UUID, String> uuidMap = Map.of(uuid1, "first", uuid2, "second");
		XmlElement encodedUuidMap = uuidMapCodec.encode(PROVIDER, uuidMap);
		Map<java.util.UUID, String> decodedUuidMap = uuidMapCodec.decode(PROVIDER, encodedUuidMap);
		assertEquals(uuidMap, decodedUuidMap);
		
		Codec<Map<Integer, Double>> intMapCodec = Codec.map(INTEGER, DOUBLE);
		Map<Integer, Double> intMap = Map.of(1, 1.5, 2, 2.5, 3, 3.5);
		XmlElement encodedIntMap = intMapCodec.encode(PROVIDER, intMap);
		assertEquals(intMap, intMapCodec.decode(PROVIDER, encodedIntMap));
		
		Codec<Map<Long, List<String>>> longMapCodec = Codec.map(LONG, STRING.list());
		Map<Long, List<String>> longMap = Map.of(
			100L, List.of("a", "b", "c"),
			200L, List.of("d", "e", "f")
		);
		XmlElement encodedLongMap = longMapCodec.encode(PROVIDER, longMap);
		assertEquals(longMap, longMapCodec.decode(PROVIDER, encodedLongMap));
	}
	
	@Test
	void encodeAndDecodeCollectionCodecsWithSizeConstraints() {
		Codec<List<Integer>> nonEmptyList = INTEGER.noneEmptyList();
		List<Integer> list1 = List.of(1, 2, 3);
		XmlElement encodedList1 = nonEmptyList.encode(PROVIDER, list1);
		assertEquals(list1, nonEmptyList.decode(PROVIDER, encodedList1));
		
		Codec<List<String>> constrainedList = Codec.list(STRING, 2, 5);
		List<String> list2 = List.of("a", "b", "c");
		XmlElement encodedList2 = constrainedList.encode(PROVIDER, list2);
		assertEquals(list2, constrainedList.decode(PROVIDER, encodedList2));
		
		Codec<Map<String, Integer>> nonEmptyMap = Codec.noneEmptyMap(STRING, INTEGER);
		Map<String, Integer> map1 = Map.of("x", 1, "y", 2);
		XmlElement encodedMap1 = nonEmptyMap.encode(PROVIDER, map1);
		assertEquals(map1, nonEmptyMap.decode(PROVIDER, encodedMap1));
		
		Codec<Integer[]> constrainedArray = Codec.array(Integer.class, INTEGER, 1, 10);
		Integer[] arr = { 1, 2, 3, 4, 5 };
		XmlElement encodedArr = constrainedArray.encode(PROVIDER, arr);
		assertArrayEquals(arr, constrainedArray.decode(PROVIDER, encodedArr));
	}
	
	@Test
	void encodeAndDecodeOptionalCodecVariants() {
		Codec<String> optionalWithSupplier = STRING.optional(() -> "default-" + System.currentTimeMillis());
		XmlElement encoded1 = optionalWithSupplier.encode(PROVIDER, "test");
		String decoded1 = optionalWithSupplier.decode(PROVIDER, encoded1);
		assertEquals("test", decoded1);
		
		Codec<List<Integer>> nullableList = Codec.nullable(INTEGER.list());
		List<Integer> list = List.of(1, 2, 3);
		XmlElement encodedList = nullableList.encode(PROVIDER, list);
		assertEquals(list, nullableList.decode(PROVIDER, encodedList));
		XmlElement encodedNull = nullableList.encode(PROVIDER, null);
		assertNull(nullableList.decode(PROVIDER, encodedNull));
	}
	
	@Test
	void encodeAndDecodeAlternativeCodecFallback() {
		Codec<Integer> alternativeCodec = Codec.withAlternative(
			Codec.ranged(INTEGER, 0, 100),
			INTEGER
		);
		
		XmlElement encoded = alternativeCodec.encode(PROVIDER, 50);
		assertEquals(50, alternativeCodec.decode(PROVIDER, encoded));
	}
	
	@Test
	void encodeAndDecodeComplexRecordWithAllCodecTypes() {
		Codec<UltraComplexRecord> ultraCodec = createUltraComplexCodec();
		
		java.util.UUID id = java.util.UUID.randomUUID();
		java.util.UUID metricKey1 = java.util.UUID.randomUUID();
		java.util.UUID metricKey2 = java.util.UUID.randomUUID();
		
		int[] expectedStreamData = { 10, 20, 30, 40, 50 };
		
		UltraComplexRecord record = new UltraComplexRecord(
			id,
			25,
			"JohnDoe",
			LocalDateTime.of(2025, 10, 20, 15, 30),
			Duration.ofHours(5),
			Map.of(metricKey1, 95.5, metricKey2, 87.3),
			Either.left(new File("/tmp/data.txt")),
			StandardCharsets.UTF_8,
			new float[] { 1.1f, 2.2f, 3.3f },
			IntStream.of(10, 20, 30, 40, 50),
			List.of(java.net.URI.create("https://api.example.com"), java.net.URI.create("https://backup.example.com")),
			Optional.of(ZonedDateTime.of(2025, 10, 20, 10, 0, 0, 0, ZoneId.of("UTC")))
		);
		
		XmlElement encoded = ultraCodec.encode(PROVIDER, record);
		UltraComplexRecord decoded = ultraCodec.decode(PROVIDER, encoded);
		assertEquals(record.id(), decoded.id());
		assertEquals(record.age(), decoded.age());
		assertEquals(record.name(), decoded.name());
		assertEquals(record.timestamp(), decoded.timestamp());
		assertEquals(record.elapsed(), decoded.elapsed());
		assertEquals(record.metrics(), decoded.metrics());
		assertTrue(decoded.location().isLeft());
		assertEquals(record.encoding(), decoded.encoding());
		assertArrayEquals(record.scores(), decoded.scores());
		assertArrayEquals(expectedStreamData, decoded.dataStream().toArray());
		assertEquals(record.endpoints(), decoded.endpoints());
		assertEquals(record.lastModified(), decoded.lastModified());
	}
	
	@Test
	void encodeAndDecodeComplexRecordWithMinimumEdgeCases() {
		Codec<UltraComplexRecord> ultraCodec = createUltraComplexCodec();
		
		java.util.UUID id = java.util.UUID.randomUUID();
		java.util.UUID metricKey = java.util.UUID.randomUUID();
		
		UltraComplexRecord record = new UltraComplexRecord(
			id,
			0,
			"a",
			LocalDateTime.of(1970, 1, 1, 0, 0),
			Duration.ZERO,
			Map.of(metricKey, 0.0),
			Either.right(Path.of("/tmp")),
			null,
			new float[] { 0.0f },
			IntStream.of(0),
			List.of(java.net.URI.create("http://localhost")),
			Optional.empty()
		);
		
		XmlElement encoded = ultraCodec.encode(PROVIDER, record);
		UltraComplexRecord decoded = ultraCodec.decode(PROVIDER, encoded);
		assertEquals(record.id(), decoded.id());
		assertEquals(0, decoded.age());
		assertEquals("a", decoded.name());
		assertNull(decoded.encoding());
		assertTrue(decoded.lastModified().isEmpty());
	}
	
	@Test
	void encodeAndDecodeComplexRecordWithLargeDatasets() {
		Codec<UltraComplexRecord> ultraCodec = createUltraComplexCodec();
		
		java.util.UUID id = java.util.UUID.randomUUID();
		Map<java.util.UUID, Double> largeMetrics = new LinkedHashMap<>();
		for (int i = 0; i < 20; i++) {
			largeMetrics.put(java.util.UUID.randomUUID(), i * 10.5);
		}
		
		float[] largeScores = new float[100];
		Arrays.fill(largeScores, 99.9f);
		
		List<java.net.URI> manyEndpoints = IntStream.range(0, 10)
			.mapToObj(i -> java.net.URI.create("https://endpoint" + i + ".example.com"))
			.toList();
		
		UltraComplexRecord record = new UltraComplexRecord(
			id,
			150,
			"MaximumLengthNameWithManyCharacters",
			LocalDateTime.of(2099, 12, 31, 23, 59, 59),
			Duration.ofDays(365),
			largeMetrics,
			Either.left(new File("/very/long/path/to/some/important/data/file.txt")),
			StandardCharsets.UTF_16BE,
			largeScores,
			IntStream.range(0, 100),
			manyEndpoints,
			Optional.of(ZonedDateTime.of(2099, 12, 31, 23, 59, 59, 0, ZoneId.of("Pacific/Auckland")))
		);
		
		XmlElement encoded = ultraCodec.encode(PROVIDER, record);
		UltraComplexRecord decoded = ultraCodec.decode(PROVIDER, encoded);
		assertEquals(record.id(), decoded.id());
		assertEquals(150, decoded.age());
		assertEquals(20, decoded.metrics().size());
		assertEquals(100, decoded.scores().length);
		assertEquals(10, decoded.endpoints().size());
	}
	
	@Test
	void encodeAndDecodeComplexRecordWithEitherAlternative() {
		Codec<UltraComplexRecord> ultraCodec = createUltraComplexCodec();
		
		java.util.UUID id = java.util.UUID.randomUUID();
		LocalDateTime timestamp = LocalDateTime.of(2025, 10, 20, 15, 30);
		ZonedDateTime zonedTime = ZonedDateTime.of(2025, 10, 20, 10, 0, 0, 0, ZoneId.of("UTC"));
		
		UltraComplexRecord record = new UltraComplexRecord(
			id,
			75,
			"PathUser",
			timestamp,
			Duration.ofMinutes(30),
			Map.of(java.util.UUID.randomUUID(), 50.0),
			Either.right(Path.of("/home/user/documents")),
			StandardCharsets.ISO_8859_1,
			new float[] { 1.0f, 2.0f },
			IntStream.of(5, 10, 15),
			List.of(java.net.URI.create("https://example.com")),
			Optional.of(zonedTime)
		);
		
		XmlElement encoded = ultraCodec.encode(PROVIDER, record);
		UltraComplexRecord decoded = ultraCodec.decode(PROVIDER, encoded);
		assertTrue(decoded.location().isLeft());
		assertEquals(Path.of("/home/user/documents").toFile(), decoded.location().leftOrThrow());
		assertEquals(75, decoded.age());
		assertEquals(timestamp, decoded.timestamp());
	}
	
	@Test
	void encodeAndDecodeScientificDataRecordWithRangedValues() {
		Codec<ScientificDataRecord> codec = CodecBuilder.group(
			UUID.configure("experimentId", ScientificDataRecord::experimentId),
			Codec.ranged(DOUBLE, -273.15, 1000000.0).configure("temperature", ScientificDataRecord::temperature),
			Codec.ranged(DOUBLE, 0.0, 100.0).configure("humidity", ScientificDataRecord::humidity),
			DOUBLE_ARRAY.configure("measurements", ScientificDataRecord::measurements),
			Codec.map(INTEGER, DOUBLE).configure("statisticsByHour", ScientificDataRecord::statisticsByHour),
			INSTANT.configure("recordedAt", ScientificDataRecord::recordedAt),
			DURATION.configure("observationPeriod", ScientificDataRecord::observationPeriod),
			Codecs.either(STRING, INTEGER).configure("sensorId", ScientificDataRecord::sensorId),
			LONG_STREAM.configure("rawData", ScientificDataRecord::rawData)
		).create(ScientificDataRecord::new);
		
		ScientificDataRecord record = new ScientificDataRecord(
			java.util.UUID.randomUUID(),
			23.5,
			65.3,
			new double[] { 23.4, 23.5, 23.6, 23.7, 23.5, 23.4 },
			Map.of(0, 23.4, 1, 23.5, 2, 23.6, 3, 23.7),
			Instant.parse("2025-10-20T10:00:00Z"),
			Duration.ofHours(4),
			Either.left("SENSOR-A123"),
			LongStream.of(234, 235, 236, 237, 235, 234)
		);
		
		XmlElement encoded = codec.encode(PROVIDER, record);
		ScientificDataRecord decoded = codec.decode(PROVIDER, encoded);
		assertEquals(record.experimentId(), decoded.experimentId());
		assertEquals(record.temperature(), decoded.temperature(), 0.001);
		assertEquals(record.humidity(), decoded.humidity(), 0.001);
		assertArrayEquals(record.measurements(), decoded.measurements(), 0.001);
		assertEquals(record.statisticsByHour(), decoded.statisticsByHour());
		assertTrue(decoded.sensorId().isLeft());
	}
	
	@Test
	void encodeAndDecodeScientificDataRecordWithBoundaryValues() {
		Codec<ScientificDataRecord> codec = CodecBuilder.group(
			UUID.configure("experimentId", ScientificDataRecord::experimentId),
			Codec.ranged(DOUBLE, -273.15, 1000000.0).configure("temperature", ScientificDataRecord::temperature),
			Codec.ranged(DOUBLE, 0.0, 100.0).configure("humidity", ScientificDataRecord::humidity),
			DOUBLE_ARRAY.configure("measurements", ScientificDataRecord::measurements),
			Codec.map(INTEGER, DOUBLE).configure("statisticsByHour", ScientificDataRecord::statisticsByHour),
			INSTANT.configure("recordedAt", ScientificDataRecord::recordedAt),
			DURATION.configure("observationPeriod", ScientificDataRecord::observationPeriod),
			Codecs.either(STRING, INTEGER).configure("sensorId", ScientificDataRecord::sensorId),
			LONG_STREAM.configure("rawData", ScientificDataRecord::rawData)
		).create(ScientificDataRecord::new);
		
		ScientificDataRecord record = new ScientificDataRecord(
			java.util.UUID.randomUUID(),
			-273.15,
			100.0,
			new double[] { -273.15, 0.0, 100.0, 1000000.0 },
			Map.of(0, -273.15, 23, 1000000.0),
			Instant.ofEpochMilli(0),
			Duration.ofDays(1000),
			Either.right(999999),
			LongStream.of(Long.MAX_VALUE, Long.MIN_VALUE, 0L)
		);
		
		XmlElement encoded = codec.encode(PROVIDER, record);
		ScientificDataRecord decoded = codec.decode(PROVIDER, encoded);
		assertEquals(-273.15, decoded.temperature(), 0.001);
		assertEquals(100.0, decoded.humidity(), 0.001);
		assertTrue(decoded.sensorId().isLeft());
		assertEquals("999999", decoded.sensorId().leftOrThrow());
	}
	
	@Test
	void encodeAndDecodeNetworkConfigurationWithSSL() {
		Codec<NetworkConfigRecord> codec = CodecBuilder.group(
			STRING.configure("hostname", NetworkConfigRecord::hostname),
			Codec.ranged(INTEGER, 1, 65535).configure("port", NetworkConfigRecord::port),
			URI.list(1, 10).configure("endpoints", NetworkConfigRecord::endpoints),
			Codec.map(STRING, URL).configure("services", NetworkConfigRecord::services),
			Codecs.either(Codec.ranged(INTEGER, 1000, 10000), STRING).configure("timeout", NetworkConfigRecord::timeout),
			BOOLEAN.configure("sslEnabled", NetworkConfigRecord::sslEnabled),
			PATH.optional().configure("certificatePath", NetworkConfigRecord::certificatePath),
			Codec.map(Codec.ranged(INTEGER, 1, 65535), STRING).configure("portMapping", NetworkConfigRecord::portMapping)
		).create(NetworkConfigRecord::new);
		
		try {
			NetworkConfigRecord record = new NetworkConfigRecord(
				"api.example.com",
				8443,
				List.of(
					java.net.URI.create("https://api.example.com/v1"),
					java.net.URI.create("https://api.example.com/v2")
				),
				Map.of(
					"auth", java.net.URI.create("https://auth.example.com").toURL(),
					"data", java.net.URI.create("https://data.example.com").toURL()
				),
				Either.left(5000),
				true,
				Optional.of(Path.of("/etc/ssl/cert.pem")),
				Map.of(8080, "http", 8443, "https", 3306, "mysql")
			);
			
			XmlElement encoded = codec.encode(PROVIDER, record);
			NetworkConfigRecord decoded = codec.decode(PROVIDER, encoded);
			assertEquals("api.example.com", decoded.hostname());
			assertEquals(8443, decoded.port());
			assertEquals(2, decoded.endpoints().size());
			assertTrue(decoded.sslEnabled());
			assertTrue(decoded.certificatePath().isPresent());
			assertEquals(3, decoded.portMapping().size());
		} catch (Exception e) {
			fail("Failed to create test record: " + e.getMessage());
		}
	}
	
	@Test
	void encodeAndDecodeNetworkConfigurationWithoutSSL() {
		Codec<NetworkConfigRecord> codec = CodecBuilder.group(
			STRING.configure("hostname", NetworkConfigRecord::hostname),
			Codec.ranged(INTEGER, 1, 65535).configure("port", NetworkConfigRecord::port),
			URI.list(1, 10).configure("endpoints", NetworkConfigRecord::endpoints),
			Codec.map(STRING, URL).configure("services", NetworkConfigRecord::services),
			Codecs.either(Codec.ranged(INTEGER, 1000, 10000), STRING).configure("timeout", NetworkConfigRecord::timeout),
			BOOLEAN.configure("sslEnabled", NetworkConfigRecord::sslEnabled),
			PATH.optional().configure("certificatePath", NetworkConfigRecord::certificatePath),
			Codec.map(Codec.ranged(INTEGER, 1, 65535), STRING).configure("portMapping", NetworkConfigRecord::portMapping)
		).create(NetworkConfigRecord::new);
		
		try {
			NetworkConfigRecord record = new NetworkConfigRecord(
				"localhost",
				80,
				List.of(java.net.URI.create("http://localhost/api")),
				Map.of("main", java.net.URI.create("http://localhost").toURL()),
				Either.right("infinite"),
				false,
				Optional.empty(),
				Map.of(80, "http", 8080, "http-alt")
			);
			
			XmlElement encoded = codec.encode(PROVIDER, record);
			NetworkConfigRecord decoded = codec.decode(PROVIDER, encoded);
			assertFalse(decoded.sslEnabled());
			assertTrue(decoded.certificatePath().isEmpty());
			assertTrue(decoded.timeout().isRight());
			assertEquals("infinite", decoded.timeout().rightOrThrow());
		} catch (Exception e) {
			fail("Failed to create test record: " + e.getMessage());
		}
	}
	
	@Test
	void encodeAndDecodeAnalyticsRecordWithTimeSeries() {
		Codec<DataAnalyticsRecord> codec = CodecBuilder.group(
			STRING.configure("datasetId", DataAnalyticsRecord::datasetId),
			Codec.map(LONG, DOUBLE).configure("timeSeries", DataAnalyticsRecord::timeSeries),
			INT_STREAM.configure("samples", DataAnalyticsRecord::samples),
			DOUBLE_STREAM.configure("normalizedData", DataAnalyticsRecord::normalizedData),
			Codec.map(STRING, Codec.atLeast(INTEGER, 0)).configure("categoryCounts", DataAnalyticsRecord::categoryCounts),
			LOCAL_DATE.configure("analysisDate", DataAnalyticsRecord::analysisDate),
			DURATION.configure("processingTime", DataAnalyticsRecord::processingTime),
			Codecs.either(Codec.ranged(DOUBLE, 0.0, 1.0), STRING).configure("confidence", DataAnalyticsRecord::confidence)
		).create(DataAnalyticsRecord::new);
		
		DataAnalyticsRecord record = new DataAnalyticsRecord(
			"DATASET-2025-001",
			Map.of(1729425600000L, 1.5, 1729429200000L, 2.3, 1729432800000L, 1.8),
			IntStream.range(0, 100),
			DoubleStream.of(0.1, 0.5, 0.3, 0.7, 0.2),
			Map.of("CategoryA", 150, "CategoryB", 200, "CategoryC", 75),
			LocalDate.of(2025, 10, 20),
			Duration.ofMinutes(45),
			Either.left(0.95)
		);
		
		XmlElement encoded = codec.encode(PROVIDER, record);
		DataAnalyticsRecord decoded = codec.decode(PROVIDER, encoded);
		assertEquals("DATASET-2025-001", decoded.datasetId());
		assertEquals(3, decoded.timeSeries().size());
		assertEquals(3, decoded.categoryCounts().size());
		assertTrue(decoded.confidence().isLeft());
		assertEquals(0.95, decoded.confidence().leftOrThrow(), 0.001);
	}
	
	@Test
	void encodeAndDecodeAnalyticsRecordWithLargeTimeSeries() {
		Codec<DataAnalyticsRecord> codec = CodecBuilder.group(
			STRING.configure("datasetId", DataAnalyticsRecord::datasetId),
			Codec.map(LONG, DOUBLE).configure("timeSeries", DataAnalyticsRecord::timeSeries),
			INT_STREAM.configure("samples", DataAnalyticsRecord::samples),
			DOUBLE_STREAM.configure("normalizedData", DataAnalyticsRecord::normalizedData),
			Codec.map(STRING, Codec.atLeast(INTEGER, 0)).configure("categoryCounts", DataAnalyticsRecord::categoryCounts),
			LOCAL_DATE.configure("analysisDate", DataAnalyticsRecord::analysisDate),
			DURATION.configure("processingTime", DataAnalyticsRecord::processingTime),
			Codecs.either(Codec.ranged(DOUBLE, 0.0, 1.0), STRING).configure("confidence", DataAnalyticsRecord::confidence)
		).create(DataAnalyticsRecord::new);
		
		Map<Long, Double> largeTimeSeries = new LinkedHashMap<>();
		for (long i = 0; i < 1000; i++) {
			largeTimeSeries.put(1729425600000L + i * 3600000, Math.random());
		}
		
		Map<String, Integer> manyCounts = new LinkedHashMap<>();
		for (int i = 0; i < 50; i++) {
			manyCounts.put("Category" + i, i * 10);
		}
		
		DataAnalyticsRecord record = new DataAnalyticsRecord(
			"LARGE-DATASET-001",
			largeTimeSeries,
			IntStream.range(0, 10000),
			DoubleStream.generate(Math::random).limit(1000),
			manyCounts,
			LocalDate.of(2025, 10, 20),
			Duration.ofHours(5),
			Either.right("high-confidence")
		);
		
		XmlElement encoded = codec.encode(PROVIDER, record);
		DataAnalyticsRecord decoded = codec.decode(PROVIDER, encoded);
		assertEquals(1000, decoded.timeSeries().size());
		assertEquals(50, decoded.categoryCounts().size());
		assertTrue(decoded.confidence().isRight());
	}
	
	@Test
	void encodeAndDecodeConfigurationRecordWithValidation() {
		Codec<ConfigurationRecord> codec = CodecBuilder.group(
			STRING.configure("configId", ConfigurationRecord::configId),
			STRING.optional("default-app").configure("appName", ConfigurationRecord::appName),
			Codec.ranged(INTEGER, 1, 100).optional(10).configure("threadPoolSize", ConfigurationRecord::threadPoolSize),
			BOOLEAN.optional(false).configure("debugMode", ConfigurationRecord::debugMode),
			formattedString("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}").optional().configure("ipAddress", ConfigurationRecord::ipAddress),
			Codec.map(STRING, STRING).configure("environment", ConfigurationRecord::environment),
			DURATION.optional(Duration.ofSeconds(30)).configure("timeout", ConfigurationRecord::timeout),
			Codecs.either(FILE, URI).list().configure("configSources", ConfigurationRecord::configSources)
		).create(ConfigurationRecord::new);
		
		ConfigurationRecord record = new ConfigurationRecord(
			"CONFIG-001",
			"MyApplication",
			20,
			true,
			Optional.of("192.168.1.100"),
			Map.of("ENV", "production", "REGION", "us-west"),
			Duration.ofMinutes(5),
			List.of(
				Either.left(new File("/etc/app/config.yaml")),
				Either.right(java.net.URI.create("https://config.example.com/app.conf"))
			)
		);
		
		XmlElement encoded = codec.encode(PROVIDER, record);
		ConfigurationRecord decoded = codec.decode(PROVIDER, encoded);
		assertEquals("MyApplication", decoded.appName());
		assertEquals(20, decoded.threadPoolSize());
		assertTrue(decoded.debugMode());
		assertTrue(decoded.ipAddress().isPresent());
		assertEquals(2, decoded.configSources().size());
	}
	
	@Test
	void encodeAndDecodeConfigurationRecordWithDefaultValues() {
		Codec<ConfigurationRecord> codec = CodecBuilder.group(
			STRING.configure("configId", ConfigurationRecord::configId),
			STRING.optional("default-app").configure("appName", ConfigurationRecord::appName),
			Codec.ranged(INTEGER, 1, 100).optional(10).configure("threadPoolSize", ConfigurationRecord::threadPoolSize),
			BOOLEAN.optional(false).configure("debugMode", ConfigurationRecord::debugMode),
			formattedString("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}").optional().configure("ipAddress", ConfigurationRecord::ipAddress),
			Codec.map(STRING, STRING).configure("environment", ConfigurationRecord::environment),
			DURATION.optional(Duration.ofSeconds(30)).configure("timeout", ConfigurationRecord::timeout),
			Codecs.either(FILE, URI).list().configure("configSources", ConfigurationRecord::configSources)
		).create(ConfigurationRecord::new);
		
		ConfigurationRecord record = new ConfigurationRecord(
			"CONFIG-002",
			"default-app",
			10,
			false,
			Optional.empty(),
			Map.of(),
			Duration.ofSeconds(30),
			List.of(Either.left(new File("/etc/app/default.conf")))
		);
		
		XmlElement encoded = codec.encode(PROVIDER, record);
		ConfigurationRecord decoded = codec.decode(PROVIDER, encoded);
		assertEquals("default-app", decoded.appName());
		assertEquals(10, decoded.threadPoolSize());
		assertFalse(decoded.debugMode());
		assertTrue(decoded.ipAddress().isEmpty());
	}
	
	@Test
	void encodeAndDecodeDeeplyNestedMixedCodecTypes() {
		Codec<Map<java.util.UUID, List<Either<LocalDate, Instant>>>> complexNested = Codec.map(
			UUID,
			Codecs.either(LOCAL_DATE, INSTANT).list()
		);
		
		java.util.UUID key1 = java.util.UUID.randomUUID();
		java.util.UUID key2 = java.util.UUID.randomUUID();
		
		Map<java.util.UUID, List<Either<LocalDate, Instant>>> data = Map.of(
			key1, List.of(
				Either.left(LocalDate.of(2025, 1, 1)),
				Either.right(Instant.parse("2025-01-01T10:00:00Z")),
				Either.left(LocalDate.of(2025, 1, 2))
			),
			key2, List.of(
				Either.right(Instant.parse("2025-01-03T14:30:00Z"))
			)
		);
		
		XmlElement encoded = complexNested.encode(PROVIDER, data);
		Map<java.util.UUID, List<Either<LocalDate, Instant>>> decoded =
			complexNested.decode(PROVIDER, encoded);
		
		assertEquals(data.size(), decoded.size());
		assertEquals(3, decoded.get(key1).size());
		assertTrue(decoded.get(key1).get(0).isLeft());
		assertTrue(decoded.get(key1).get(1).isRight());
	}
	
	@Test
	void encodeAndDecodeRecordWithAllTimeAndIOTypes() {
		Codec<TimeAndIORecord> codec = CodecBuilder.group(
			LOCAL_TIME.configure("time", TimeAndIORecord::time),
			LOCAL_DATE.configure("date", TimeAndIORecord::date),
			INSTANT.configure("instant", TimeAndIORecord::instant),
			PERIOD.configure("period", TimeAndIORecord::period),
			FILE.configure("file", TimeAndIORecord::file),
			PATH.configure("path", TimeAndIORecord::path),
			URI.configure("uri", TimeAndIORecord::uri),
			URL.configure("url", TimeAndIORecord::url),
			CHARSET.configure("charset", TimeAndIORecord::charset)
		).create(TimeAndIORecord::new);
		
		TimeAndIORecord record = null;
		try {
			record = new TimeAndIORecord(
				LocalTime.of(10, 30, 45),
				LocalDate.of(2025, 10, 20),
				Instant.parse("2025-10-20T10:30:45Z"),
				Period.of(1, 2, 15),
				new File("/tmp/test.txt"),
				Path.of("/home/user/data"),
				java.net.URI.create("https://example.com/api"),
				java.net.URI.create("https://example.com:8080/resource").toURL(),
				StandardCharsets.UTF_16
			);
		} catch (Exception e) {
			fail("Failed to create test record: " + e.getMessage());
		}
		
		XmlElement encoded = codec.encode(PROVIDER, record);
		TimeAndIORecord decoded = codec.decode(PROVIDER, encoded);
		assertEquals(record, decoded);
	}
	
	@Test
	void encodeAndDecodeCollectionsOfStreamTypes() {
		Codec<List<IntStream>> listOfStreamsCodec = INT_STREAM.list();
		List<IntStream> streams = List.of(
			IntStream.of(1, 2, 3),
			IntStream.of(4, 5, 6),
			IntStream.of(7, 8, 9)
		);
		
		XmlElement encoded = listOfStreamsCodec.encode(PROVIDER, streams);
		List<IntStream> decoded = listOfStreamsCodec.decode(PROVIDER, encoded);
		assertEquals(3, decoded.size());
		assertArrayEquals(new int[] { 1, 2, 3 }, decoded.getFirst().toArray());
		
		Codec<Map<String, DoubleStream>> mapOfStreamsCodec = Codec.map(STRING, DOUBLE_STREAM);
		Map<String, DoubleStream> streamMap = Map.of(
			"data1", DoubleStream.of(1.1, 2.2, 3.3),
			"data2", DoubleStream.of(4.4, 5.5)
		);
		
		XmlElement encodedMap = mapOfStreamsCodec.encode(PROVIDER, streamMap);
		Map<String, DoubleStream> decodedMap = mapOfStreamsCodec.decode(PROVIDER, encodedMap);
		assertEquals(2, decodedMap.size());
		assertArrayEquals(new double[] { 1.1, 2.2, 3.3 }, decodedMap.get("data1").toArray(), 0.001);
	}
	
	@Test
	void encodeAndDecodeEnumMapWithComplexValues() {
		Codec<Map<Priority, List<Task>>> enumMapCodec = Codec.map(
			dynamicEnum(Priority.class),
			CodecBuilder.group(
				STRING.configure("id", Task::id),
				STRING.configure("description", Task::description),
				LOCAL_DATE_TIME.configure("deadline", Task::deadline),
				INTEGER.list().configure("tags", Task::tags)
			).create(Task::new).list()
		);
		
		Map<Priority, List<Task>> taskMap = Map.of(
			Priority.HIGH, List.of(
				new Task("T1", "Important task", LocalDateTime.of(2025, 10, 25, 17, 0), List.of(1, 2)),
				new Task("T2", "Critical fix", LocalDateTime.of(2025, 10, 21, 9, 0), List.of(3))
			),
			Priority.LOW, List.of(
				new Task("T3", "Minor update", LocalDateTime.of(2025, 11, 1, 14, 0), List.of(4, 5, 6))
			)
		);
		
		XmlElement encoded = enumMapCodec.encode(PROVIDER, taskMap);
		Map<Priority, List<Task>> decoded = enumMapCodec.decode(PROVIDER, encoded);
		assertEquals(taskMap, decoded);
		assertEquals(2, decoded.get(Priority.HIGH).size());
	}
	
	@Test
	void encodeAndDecodeArrayCodecsForComplexTypes() {
		Codec<LocalDate[]> dateArrayCodec = LOCAL_DATE.array(LocalDate.class);
		LocalDate[] dates = {
			LocalDate.of(2025, 1, 1),
			LocalDate.of(2025, 6, 15),
			LocalDate.of(2025, 12, 31)
		};
		
		XmlElement encoded = dateArrayCodec.encode(PROVIDER, dates);
		LocalDate[] decoded = dateArrayCodec.decode(PROVIDER, encoded);
		assertArrayEquals(dates, decoded);
		
		Codec<Path[]> pathArrayCodec = PATH.array(Path.class, 1, 5);
		Path[] paths = { Path.of("/home/user1"), Path.of("/home/user2") };
		XmlElement encodedPaths = pathArrayCodec.encode(PROVIDER, paths);
		Path[] decodedPaths = pathArrayCodec.decode(PROVIDER, encodedPaths);
		assertArrayEquals(paths, decodedPaths);
	}
	
	@Test
	void encodeAndDecodeDeeplyNestedOptionalAndEither() {
		Codec<Optional<List<Optional<Either<Integer, String>>>>> multiLevelCodec =
			Codecs.either(INTEGER, STRING).optional().list().optional();
		
		Optional<List<Optional<Either<Integer, String>>>> data = Optional.of(List.of(
			Optional.of(Either.left(42)),
			Optional.empty(),
			Optional.of(Either.right("hello")),
			Optional.of(Either.left(100))
		));
		
		XmlElement encoded = multiLevelCodec.encode(PROVIDER, data);
		Optional<List<Optional<Either<Integer, String>>>> decoded = multiLevelCodec.decode(PROVIDER, encoded);
		
		assertTrue(decoded.isPresent());
		assertEquals(3, decoded.get().size());
		assertTrue(decoded.get().getFirst().isPresent());
		assertTrue(decoded.get().getFirst().get().isLeft());
		assertEquals(42, decoded.get().getFirst().get().leftOrThrow());
	}
	
	private enum Priority {
		LOW, MEDIUM, HIGH, URGENT, CRITICAL
	}
	
	private enum TestEnum {
		FIRST, SECOND, THIRD
	}
	
	private record UltraComplexRecord(
		@NotNull java.util.UUID id,
		int age,
		@NotNull String name,
		@NotNull LocalDateTime timestamp,
		@NotNull Duration elapsed,
		@NotNull Map<java.util.UUID, Double> metrics,
		@NotNull Either<File, Path> location,
		Charset encoding,
		float[] scores,
		@NotNull IntStream dataStream,
		@NotNull List<java.net.URI> endpoints,
		@NotNull Optional<ZonedDateTime> lastModified
	) {}
	
	private record TimeAndIORecord(
		@NotNull LocalTime time,
		@NotNull LocalDate date,
		@NotNull Instant instant,
		@NotNull Period period,
		@NotNull File file,
		@NotNull Path path,
		@NotNull java.net.URI uri,
		@NotNull java.net.URL url,
		@NotNull Charset charset
	) {}
	
	private record Task(
		@NotNull String id,
		@NotNull String description,
		@NotNull LocalDateTime deadline,
		@NotNull List<Integer> tags
	) {}
	
	private record ScientificDataRecord(
		@NotNull java.util.UUID experimentId,
		double temperature,
		double humidity,
		double[] measurements,
		@NotNull Map<Integer, Double> statisticsByHour,
		@NotNull Instant recordedAt,
		@NotNull Duration observationPeriod,
		@NotNull Either<String, Integer> sensorId,
		@NotNull LongStream rawData
	) {}
	
	private record NetworkConfigRecord(
		@NotNull String hostname,
		int port,
		@NotNull List<java.net.URI> endpoints,
		@NotNull Map<String, java.net.URL> services,
		@NotNull Either<Integer, String> timeout,
		boolean sslEnabled,
		@NotNull Optional<Path> certificatePath,
		@NotNull Map<Integer, String> portMapping
	) {}
	
	private record DataAnalyticsRecord(
		@NotNull String datasetId,
		@NotNull Map<Long, Double> timeSeries,
		@NotNull IntStream samples,
		@NotNull DoubleStream normalizedData,
		@NotNull Map<String, Integer> categoryCounts,
		@NotNull LocalDate analysisDate,
		@NotNull Duration processingTime,
		@NotNull Either<Double, String> confidence
	) {}
	
	private record ConfigurationRecord(
		@NotNull String configId,
		@NotNull String appName,
		int threadPoolSize,
		boolean debugMode,
		@NotNull Optional<String> ipAddress,
		@NotNull Map<String, String> environment,
		@NotNull Duration timeout,
		@NotNull List<Either<File, java.net.URI>> configSources
	) {}
	
	private record OfficeWithPeople(
		@NotNull String officeName,
		@NotNull Address location,
		@NotNull List<PersonWithAddress> employees
	) {}
	
	private record Person(@NotNull String name, int age) {}
	
	private record Address(@NotNull String street, @NotNull String city, int zipCode) {}
	
	private record PersonWithAddress(@NotNull String name, int age, @NotNull Address address) {}
	
	private record Team(@NotNull String teamName, @NotNull List<String> members) {}
	
	private record PersonWithOptionalEmail(@NotNull String name, @NotNull Optional<String> email) {}
	
	private record Config(@NotNull String name, @NotNull Map<String, Integer> settings) {}
	
	private record AutoMappedPerson(@NotNull String name, int age, @NotNull String email) {}
}
