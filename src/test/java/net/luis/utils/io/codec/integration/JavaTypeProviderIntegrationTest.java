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
import net.luis.utils.io.codec.provider.JavaTypeProvider;
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
 * Integration test class for {@link JavaTypeProvider}.<br>
 *
 * @author Luis-St
 */
class JavaTypeProviderIntegrationTest {
	
	private static final JavaTypeProvider PROVIDER = JavaTypeProvider.INSTANCE;
	
	@Test
	void encodeAndDecodeBooleanValues() {
		assertTrue(BOOLEAN.decode(PROVIDER, BOOLEAN.encode(PROVIDER, true)));
		assertFalse(BOOLEAN.decode(PROVIDER, BOOLEAN.encode(PROVIDER, false)));
	}
	
	@Test
	void encodeAndDecodeNumericPrimitives() {
		assertEquals((byte) 127, BYTE.decode(PROVIDER, BYTE.encode(PROVIDER, (byte) 127)));
		assertEquals((byte) -128, BYTE.decode(PROVIDER, BYTE.encode(PROVIDER, (byte) -128)));
		
		assertEquals((short) 32000, SHORT.decode(PROVIDER, SHORT.encode(PROVIDER, (short) 32000)));
		assertEquals((short) -32000, SHORT.decode(PROVIDER, SHORT.encode(PROVIDER, (short) -32000)));
		
		assertEquals(Integer.MAX_VALUE, INTEGER.decode(PROVIDER, INTEGER.encode(PROVIDER, Integer.MAX_VALUE)));
		assertEquals(Integer.MIN_VALUE, INTEGER.decode(PROVIDER, INTEGER.encode(PROVIDER, Integer.MIN_VALUE)));
		
		assertEquals(Long.MAX_VALUE, LONG.decode(PROVIDER, LONG.encode(PROVIDER, Long.MAX_VALUE)));
		assertEquals(Long.MIN_VALUE, LONG.decode(PROVIDER, LONG.encode(PROVIDER, Long.MIN_VALUE)));
		
		assertEquals(3.14159f, FLOAT.decode(PROVIDER, FLOAT.encode(PROVIDER, 3.14159f)), 0.00001f);
		assertEquals(Float.MAX_VALUE, FLOAT.decode(PROVIDER, FLOAT.encode(PROVIDER, Float.MAX_VALUE)), 1.0e31f);
		
		assertEquals(2.718281828, DOUBLE.decode(PROVIDER, DOUBLE.encode(PROVIDER, 2.718281828)), 0.0000001);
		assertEquals(Double.MAX_VALUE, DOUBLE.decode(PROVIDER, DOUBLE.encode(PROVIDER, Double.MAX_VALUE)), 1.0e300);
	}
	
	@Test
	void encodeAndDecodeCharacterAndStrings() {
		assertEquals('A', CHARACTER.decode(PROVIDER, CHARACTER.encode(PROVIDER, 'A')));
		assertEquals('€', CHARACTER.decode(PROVIDER, CHARACTER.encode(PROVIDER, '€')));
		assertEquals('中', CHARACTER.decode(PROVIDER, CHARACTER.encode(PROVIDER, '中')));
		
		assertEquals("Hello, World!", STRING.decode(PROVIDER, STRING.encode(PROVIDER, "Hello, World!")));
		assertEquals("", STRING.decode(PROVIDER, STRING.encode(PROVIDER, "")));
		assertEquals("Line1\nLine2\tTabbed", STRING.decode(PROVIDER, STRING.encode(PROVIDER, "Line1\nLine2\tTabbed")));
		assertEquals("Special chars: !@#$%^&*()", STRING.decode(PROVIDER, STRING.encode(PROVIDER, "Special chars: !@#$%^&*()")));
	}
	
	@Test
	void encodeAndDecodePrimitiveArrays() {
		boolean[] boolArray = { true, false, true, false, true };
		assertArrayEquals(boolArray, BOOLEAN_ARRAY.decode(PROVIDER, BOOLEAN_ARRAY.encode(PROVIDER, boolArray)));
		
		byte[] byteArray = { 1, 2, 3, 4, 5, -1, -2, -3 };
		assertArrayEquals(byteArray, BYTE_ARRAY.decode(PROVIDER, BYTE_ARRAY.encode(PROVIDER, byteArray)));
		
		short[] shortArray = { 100, 200, 300, -100, -200 };
		assertArrayEquals(shortArray, SHORT_ARRAY.decode(PROVIDER, SHORT_ARRAY.encode(PROVIDER, shortArray)));
		
		int[] intArray = { 1, 2, 3, 4, 5, 10, 100, 1000 };
		assertArrayEquals(intArray, INTEGER_ARRAY.decode(PROVIDER, INTEGER_ARRAY.encode(PROVIDER, intArray)));
		
		long[] longArray = { 1000L, 2000L, 3000L, 999999999L };
		assertArrayEquals(longArray, LONG_ARRAY.decode(PROVIDER, LONG_ARRAY.encode(PROVIDER, longArray)));
		
		float[] floatArray = { 1.1f, 2.2f, 3.3f, 4.4f };
		assertArrayEquals(floatArray, FLOAT_ARRAY.decode(PROVIDER, FLOAT_ARRAY.encode(PROVIDER, floatArray)), 0.001f);
		
		double[] doubleArray = { 1.1, 2.2, 3.3, 4.4, 5.5 };
		assertArrayEquals(doubleArray, DOUBLE_ARRAY.decode(PROVIDER, DOUBLE_ARRAY.encode(PROVIDER, doubleArray)), 0.0001);
		
		char[] charArray = { 'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd' };
		assertArrayEquals(charArray, CHARACTER_ARRAY.decode(PROVIDER, CHARACTER_ARRAY.encode(PROVIDER, charArray)));
	}
	
	@Test
	void encodeAndDecodeListOfIntegers() {
		List<Integer> intList = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		assertEquals(intList, INTEGER.list().decode(PROVIDER, INTEGER.list().encode(PROVIDER, intList)));
		
		List<String> stringList = List.of("alpha", "beta", "gamma", "delta", "epsilon");
		assertEquals(stringList, STRING.list().decode(PROVIDER, STRING.list().encode(PROVIDER, stringList)));
		
		List<String> emptyList = List.of();
		assertEquals(emptyList, STRING.list().decode(PROVIDER, STRING.list().encode(PROVIDER, emptyList)));
	}
	
	@Test
	void encodeAndDecodeMapOfStringToIntegers() {
		Map<String, Integer> map = Map.of("one", 1, "two", 2, "three", 3, "four", 4, "five", 5);
		Codec<Map<String, Integer>> mapCodec = Codec.map(STRING, INTEGER);
		assertEquals(map, mapCodec.decode(PROVIDER, mapCodec.encode(PROVIDER, map)));
		
		Map<String, String> stringMap = Map.of("key1", "value1", "key2", "value2", "key3", "value3");
		Codec<Map<String, String>> stringMapCodec = Codec.map(STRING, STRING);
		assertEquals(stringMap, stringMapCodec.decode(PROVIDER, stringMapCodec.encode(PROVIDER, stringMap)));
	}
	
	@Test
	void encodeAndDecodeObjectArrays() {
		Integer[] intArray = { 10, 20, 30, 40, 50 };
		Codec<Integer[]> arrayCodec = INTEGER.array(Integer.class);
		assertArrayEquals(intArray, arrayCodec.decode(PROVIDER, arrayCodec.encode(PROVIDER, intArray)));
		
		String[] stringArray = { "apple", "banana", "cherry", "date" };
		Codec<String[]> stringArrayCodec = STRING.array(String.class);
		assertArrayEquals(stringArray, stringArrayCodec.decode(PROVIDER, stringArrayCodec.encode(PROVIDER, stringArray)));
	}
	
	@Test
	void encodeAndDecodeStreamTypes() {
		IntStream intStream = IntStream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		assertArrayEquals(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }, INT_STREAM.decode(PROVIDER, INT_STREAM.encode(PROVIDER, intStream)).toArray());
		
		LongStream longStream = LongStream.of(100L, 200L, 300L, 400L, 500L);
		assertArrayEquals(new long[] { 100L, 200L, 300L, 400L, 500L }, LONG_STREAM.decode(PROVIDER, LONG_STREAM.encode(PROVIDER, longStream)).toArray());
		
		DoubleStream doubleStream = DoubleStream.of(1.1, 2.2, 3.3, 4.4, 5.5);
		assertArrayEquals(new double[] { 1.1, 2.2, 3.3, 4.4, 5.5 }, DOUBLE_STREAM.decode(PROVIDER, DOUBLE_STREAM.encode(PROVIDER, doubleStream)).toArray(), 0.0001);
		
		Stream<String> stringStream = Stream.of("alpha", "beta", "gamma", "delta");
		Codec<Stream<String>> streamCodec = STRING.stream();
		assertEquals(List.of("alpha", "beta", "gamma", "delta"), streamCodec.decode(PROVIDER, streamCodec.encode(PROVIDER, stringStream)).toList());
	}
	
	@Test
	void encodeAndDecodeTimeTypes() {
		LocalTime time = LocalTime.of(14, 30, 45, 123456789);
		assertEquals(time, LOCAL_TIME.decode(PROVIDER, LOCAL_TIME.encode(PROVIDER, time)));
		
		LocalDate date = LocalDate.of(2025, 10, 19);
		assertEquals(date, LOCAL_DATE.decode(PROVIDER, LOCAL_DATE.encode(PROVIDER, date)));
		
		LocalDateTime dateTime = LocalDateTime.of(2025, 10, 19, 14, 30, 45);
		assertEquals(dateTime, LOCAL_DATE_TIME.decode(PROVIDER, LOCAL_DATE_TIME.encode(PROVIDER, dateTime)));
		
		ZonedDateTime zonedDateTime = ZonedDateTime.of(2025, 10, 19, 14, 30, 45, 0, ZoneId.of("UTC"));
		assertEquals(zonedDateTime, ZONED_DATE_TIME.decode(PROVIDER, ZONED_DATE_TIME.encode(PROVIDER, zonedDateTime)));
		
		Instant instant = Instant.parse("2025-10-19T14:30:45.123Z");
		assertEquals(instant, INSTANT.decode(PROVIDER, INSTANT.encode(PROVIDER, instant)));
		
		Duration duration = Duration.ofHours(25).plusMinutes(30).plusSeconds(45);
		assertEquals(duration, DURATION.decode(PROVIDER, DURATION.encode(PROVIDER, duration)));
	}
	
	@Test
	void encodeAndDecodePeriod() {
		Period periodZero = Period.ZERO;
		Object encodedZero = PERIOD.encode(PROVIDER, periodZero);
		assertEquals("0d", encodedZero);
		
		Period periodYears = Period.ofYears(5);
		Object encodedYears = PERIOD.encode(PROVIDER, periodYears);
		assertEquals("5y", encodedYears);
		
		Period decodedComplex = PERIOD.decode(PROVIDER, "2y 6m 15d");
		assertEquals(Period.of(2, 6, 15), decodedComplex);
	}
	
	@Test
	void encodeAndDecodeIOTypes() throws Exception {
		Charset charset = StandardCharsets.UTF_16;
		assertEquals(charset, CHARSET.decode(PROVIDER, CHARSET.encode(PROVIDER, charset)));
		
		File file = new File("/home/user/documents/file.txt");
		assertEquals(file, FILE.decode(PROVIDER, FILE.encode(PROVIDER, file)));
		
		Path path = Path.of("/home/user/documents/folder/file.txt");
		assertEquals(path, PATH.decode(PROVIDER, PATH.encode(PROVIDER, path)));
		
		URI uri = new URI("https://example.com/api/v1/users?id=123&name=test");
		assertEquals(uri, URI.decode(PROVIDER, URI.encode(PROVIDER, uri)));
		
		URL url = java.net.URI.create("https://example.com:8080/path/to/resource").toURL();
		assertEquals(url, URL.decode(PROVIDER, URL.encode(PROVIDER, url)));
	}
	
	@Test
	void encodeAndDecodeUUID() {
		java.util.UUID uuid1 = java.util.UUID.randomUUID();
		assertEquals(uuid1, UUID.decode(PROVIDER, UUID.encode(PROVIDER, uuid1)));
		
		java.util.UUID uuid2 = java.util.UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
		assertEquals(uuid2, UUID.decode(PROVIDER, UUID.encode(PROVIDER, uuid2)));
	}
	
	@Test
	void encodeAndDecodeEnumValues() {
		KeyableCodec<Priority> ordinalCodec = enumOrdinal(Priority.class);
		assertEquals(Priority.HIGH, ordinalCodec.decode(PROVIDER, ordinalCodec.encode(PROVIDER, Priority.HIGH)));
		assertEquals(Priority.MEDIUM, ordinalCodec.decode(PROVIDER, ordinalCodec.encode(PROVIDER, Priority.MEDIUM)));
		
		KeyableCodec<Priority> nameCodec = enumName(Priority.class);
		assertEquals(Priority.LOW, nameCodec.decode(PROVIDER, nameCodec.encode(PROVIDER, Priority.LOW)));
		assertEquals(Priority.CRITICAL, nameCodec.decode(PROVIDER, nameCodec.encode(PROVIDER, Priority.CRITICAL)));
		
		KeyableCodec<Priority> dynamicCodec = dynamicEnum(Priority.class);
		assertEquals(Priority.URGENT, dynamicCodec.decode(PROVIDER, dynamicCodec.encode(PROVIDER, Priority.URGENT)));
		assertEquals(Priority.HIGH, dynamicCodec.decode(PROVIDER, 2));
	}
	
	@Test
	void encodeAndDecodeOptionalValues() {
		Codec<Optional<String>> optionalCodec = STRING.optional();
		
		Optional<String> present = Optional.of("test value");
		Optional<String> decodedPresent = optionalCodec.decode(PROVIDER, optionalCodec.encode(PROVIDER, present));
		assertTrue(decodedPresent.isPresent());
		assertEquals("test value", decodedPresent.get());
		
		Optional<String> empty = Optional.empty();
		Optional<String> decodedEmpty = optionalCodec.decode(PROVIDER, optionalCodec.encode(PROVIDER, empty));
		assertTrue(decodedEmpty.isEmpty());
	}
	
	@Test
	void encodeAndDecodeOptionalWithDefaultValue() {
		Codec<String> optionalWithDefault = STRING.optionalWithDefault("default");
		assertEquals("test", optionalWithDefault.decode(PROVIDER, optionalWithDefault.encode(PROVIDER, "test")));
		assertEquals("default", optionalWithDefault.decode(PROVIDER, optionalWithDefault.encode(PROVIDER, null)));
	}
	
	@Test
	void encodeAndDecodeNullableValues() {
		Codec<String> nullableCodec = Codec.nullable(STRING);
		assertEquals("value", nullableCodec.decode(PROVIDER, nullableCodec.encode(PROVIDER, "value")));
		assertNull(nullableCodec.decode(PROVIDER, nullableCodec.encode(PROVIDER, null)));
	}
	
	@Test
	void encodeAndDecodeEitherValues() {
		Codec<Either<Integer, String>> eitherCodec = either(INTEGER, STRING);
		
		Either<Integer, String> left = Either.left(42);
		Either<Integer, String> decodedLeft = eitherCodec.decode(PROVIDER, eitherCodec.encode(PROVIDER, left));
		assertTrue(decodedLeft.isLeft());
		assertEquals(42, decodedLeft.leftOrThrow());
		
		Either<Integer, String> right = Either.right("test");
		Either<Integer, String> decodedRight = eitherCodec.decode(PROVIDER, eitherCodec.encode(PROVIDER, right));
		assertTrue(decodedRight.isRight());
		assertEquals("test", decodedRight.rightOrThrow());
	}
	
	@Test
	void encodeAndDecodeUnitValues() {
		Codec<String> unitCodec = unit("constant");
		assertEquals("constant", unitCodec.decode(PROVIDER, unitCodec.encode(PROVIDER, "anything")));
		assertEquals("constant", unitCodec.decode(PROVIDER, unitCodec.encode(PROVIDER, null)));
	}
	
	@Test
	void encodeAndDecodeSimpleRecord() {
		Codec<Person> personCodec = CodecBuilder.group(
			STRING.configure("name", Person::name),
			INTEGER.configure("age", Person::age),
			STRING.configure("email", Person::email)
		).create(Person::new);
		
		Person person = new Person("Alice Johnson", 30, "alice@example.com");
		Person decoded = personCodec.decode(PROVIDER, personCodec.encode(PROVIDER, person));
		assertEquals(person, decoded);
	}
	
	@Test
	void encodeAndDecodeNestedRecord() {
		Codec<Address> addressCodec = CodecBuilder.group(
			STRING.configure("street", Address::street),
			STRING.configure("city", Address::city),
			STRING.configure("state", Address::state),
			INTEGER.configure("zipCode", Address::zipCode),
			STRING.configure("country", Address::country)
		).create(Address::new);
		
		Codec<Employee> employeeCodec = CodecBuilder.group(
			STRING.configure("id", Employee::id),
			STRING.configure("name", Employee::name),
			INTEGER.configure("age", Employee::age),
			STRING.configure("department", Employee::department),
			addressCodec.configure("address", Employee::address)
		).create(Employee::new);
		
		Address address = new Address("123 Main St", "Springfield", "IL", 62701, "USA");
		Employee employee = new Employee("EMP001", "Bob Smith", 35, "Engineering", address);
		
		Employee decoded = employeeCodec.decode(PROVIDER, employeeCodec.encode(PROVIDER, employee));
		assertEquals(employee, decoded);
	}
	
	@Test
	void encodeAndDecodeRecordWithCollections() {
		Codec<Team> teamCodec = CodecBuilder.group(
			STRING.configure("id", Team::id),
			STRING.configure("name", Team::name),
			STRING.list().configure("members", Team::members),
			Codec.map(STRING, INTEGER).configure("scores", Team::scores)
		).create(Team::new);
		
		Team team = new Team(
			"TEAM001",
			"Development Team A",
			List.of("Alice", "Bob", "Charlie", "Diana", "Eve"),
			Map.of("Q1", 95, "Q2", 87, "Q3", 92, "Q4", 98)
		);
		
		Team decoded = teamCodec.decode(PROVIDER, teamCodec.encode(PROVIDER, team));
		assertEquals(team, decoded);
	}
	
	@Test
	void encodeAndDecodeAutoMappedRecord() {
		Codec<Product> productCodec = CodecBuilder.autoMapCodec(Product.class);
		
		Product product = new Product("PROD123", "Laptop Computer", 1299.99, 50);
		Product decoded = productCodec.decode(PROVIDER, productCodec.encode(PROVIDER, product));
		assertEquals(product, decoded);
	}
	
	@Test
	void encodeAndDecodeLargeNestedStructure() {
		Codec<Address> addressCodec = CodecBuilder.group(
			STRING.configure("street", Address::street),
			STRING.configure("city", Address::city),
			STRING.configure("state", Address::state),
			INTEGER.configure("zipCode", Address::zipCode),
			STRING.configure("country", Address::country)
		).create(Address::new);
		
		Codec<Employee> employeeCodec = CodecBuilder.group(
			STRING.configure("id", Employee::id),
			STRING.configure("name", Employee::name),
			INTEGER.configure("age", Employee::age),
			STRING.configure("department", Employee::department),
			addressCodec.configure("address", Employee::address)
		).create(Employee::new);
		
		Codec<Department> departmentCodec = CodecBuilder.group(
			STRING.configure("id", Department::id),
			STRING.configure("name", Department::name),
			employeeCodec.configure("manager", Department::manager),
			employeeCodec.list().configure("employees", Department::employees),
			Codec.map(STRING, STRING).configure("metadata", Department::metadata)
		).create(Department::new);
		
		Codec<Company> companyCodec = CodecBuilder.group(
			STRING.configure("id", Company::id),
			STRING.configure("name", Company::name),
			addressCodec.configure("headquarters", Company::headquarters),
			departmentCodec.list().configure("departments", Company::departments),
			INTEGER.configure("foundedYear", Company::foundedYear)
		).create(Company::new);
		
		Address hqAddress = new Address("100 Tech Plaza", "San Francisco", "CA", 94105, "USA");
		
		Address managerAddr1 = new Address("10 Oak St", "San Francisco", "CA", 94102, "USA");
		Employee manager1 = new Employee("MGR001", "John Manager", 45, "Engineering", managerAddr1);
		
		List<Employee> engEmployees = List.of(
			new Employee("ENG001", "Alice Developer", 28, "Engineering", new Address("20 Pine St", "San Francisco", "CA", 94103, "USA")),
			new Employee("ENG002", "Bob Programmer", 32, "Engineering", new Address("30 Elm St", "San Francisco", "CA", 94104, "USA")),
			new Employee("ENG003", "Charlie Coder", 26, "Engineering", new Address("40 Maple St", "San Francisco", "CA", 94105, "USA"))
		);
		
		Department engineering = new Department("DEPT001", "Engineering", manager1, engEmployees, Map.of("budget", "5000000", "projects", "15", "headcount", "50"));
		
		Address managerAddr2 = new Address("50 Market St", "San Francisco", "CA", 94106, "USA");
		Employee manager2 = new Employee("MGR002", "Jane Sales", 42, "Sales", managerAddr2);
		
		List<Employee> salesEmployees = List.of(
			new Employee("SAL001", "Diana Seller", 30, "Sales", new Address("60 First St", "San Francisco", "CA", 94107, "USA")),
			new Employee("SAL002", "Eve Closer", 35, "Sales", new Address("70 Second St", "San Francisco", "CA", 94108, "USA"))
		);
		
		Department sales = new Department("DEPT002", "Sales", manager2, salesEmployees, Map.of("budget", "3000000", "region", "West Coast", "headcount", "25"));
		
		Company company = new Company("COMP001", "TechCorp Inc", hqAddress, List.of(engineering, sales), 2010);
		
		Company decoded = companyCodec.decode(PROVIDER, companyCodec.encode(PROVIDER, company));
		assertEquals(company, decoded);
		assertEquals(2, decoded.departments().size());
		assertEquals(3, decoded.departments().get(0).employees().size());
		assertEquals(2, decoded.departments().get(1).employees().size());
	}
	
	@Test
	void encodeAndDecodeDeeplyNestedCollections() {
		List<List<List<Integer>>> deepList = List.of(
			List.of(
				List.of(1, 2, 3),
				List.of(4, 5, 6)
			),
			List.of(
				List.of(7, 8, 9),
				List.of(10, 11, 12)
			),
			List.of(
				List.of(13, 14, 15),
				List.of(16, 17, 18)
			)
		);
		
		Codec<List<List<List<Integer>>>> deepCodec = INTEGER.list().list().list();
		List<List<List<Integer>>> decoded = deepCodec.decode(PROVIDER, deepCodec.encode(PROVIDER, deepList));
		assertEquals(deepList, decoded);
	}
	
	@Test
	void encodeAndDecodeComplexMapStructures() {
		Map<String, List<Map<String, Integer>>> complexMap = Map.of(
			"group1", List.of(
				Map.of("a", 1, "b", 2, "c", 3),
				Map.of("d", 4, "e", 5, "f", 6)
			),
			"group2", List.of(
				Map.of("g", 7, "h", 8, "i", 9),
				Map.of("j", 10, "k", 11, "l", 12)
			)
		);
		
		Codec<Map<String, List<Map<String, Integer>>>> complexCodec = Codec.map(STRING, Codec.map(STRING, INTEGER).list());
		
		Map<String, List<Map<String, Integer>>> decoded = complexCodec.decode(PROVIDER, complexCodec.encode(PROVIDER, complexMap));
		assertEquals(complexMap, decoded);
	}
	
	@Test
	void encodeAndDecodeLargeListOfPrimitives() {
		List<Integer> largeList = IntStream.range(0, 1000).boxed().toList();
		Codec<List<Integer>> listCodec = INTEGER.list();
		List<Integer> decoded = listCodec.decode(PROVIDER, listCodec.encode(PROVIDER, largeList));
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
		Map<String, Integer> decoded = mapCodec.decode(PROVIDER, mapCodec.encode(PROVIDER, largeMap));
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
		List<List<List<Integer>>> decoded = codec.decode(PROVIDER, codec.encode(PROVIDER, largeNestedList));
		assertEquals(largeNestedList, decoded);
		assertEquals(10, decoded.size());
		assertEquals(10, decoded.getFirst().size());
		assertEquals(10, decoded.getFirst().getFirst().size());
	}
	
	@Test
	void encodeAndDecodeListOfComplexObjects() {
		Codec<Person> personCodec = CodecBuilder.group(
			STRING.configure("name", Person::name),
			INTEGER.configure("age", Person::age),
			STRING.configure("email", Person::email)
		).create(Person::new);
		
		List<Person> people = IntStream.range(0, 50)
			.mapToObj(i -> new Person("Person" + i, 20 + i, "person" + i + "@example.com"))
			.toList();
		
		Codec<List<Person>> listCodec = personCodec.list();
		List<Person> decoded = listCodec.decode(PROVIDER, listCodec.encode(PROVIDER, people));
		assertEquals(people, decoded);
		assertEquals(50, decoded.size());
	}
	
	@Test
	void encodeAndDecodeEitherWithComplexTypes() {
		Codec<Address> addressCodec = CodecBuilder.group(
			STRING.configure("street", Address::street),
			STRING.configure("city", Address::city),
			STRING.configure("state", Address::state),
			INTEGER.configure("zipCode", Address::zipCode),
			STRING.configure("country", Address::country)
		).create(Address::new);
		
		Codec<Person> personCodec = CodecBuilder.group(
			STRING.configure("name", Person::name),
			INTEGER.configure("age", Person::age),
			STRING.configure("email", Person::email)
		).create(Person::new);
		
		Codec<Either<Person, Address>> eitherCodec = either(personCodec, addressCodec);
		
		Person person = new Person("John Doe", 30, "john@example.com");
		Either<Person, Address> leftEither = Either.left(person);
		Either<Person, Address> decodedLeft = eitherCodec.decode(PROVIDER, eitherCodec.encode(PROVIDER, leftEither));
		assertTrue(decodedLeft.isLeft());
		assertEquals(person, decodedLeft.leftOrThrow());
		
		Address address = new Address("456 Oak Ave", "Boston", "MA", 2101, "USA");
		Either<Person, Address> rightEither = Either.right(address);
		Either<Person, Address> decodedRight = eitherCodec.decode(PROVIDER, eitherCodec.encode(PROVIDER, rightEither));
		assertTrue(decodedRight.isRight());
		assertEquals(address, decodedRight.rightOrThrow());
	}
	
	@Test
	void encodeAndDecodeListOfEither() {
		Codec<Either<Integer, String>> eitherCodec = either(INTEGER, STRING);
		
		List<Either<Integer, String>> mixedList = List.of(
			Either.left(1),
			Either.right("hello"),
			Either.left(42),
			Either.right("world"),
			Either.left(100),
			Either.right("test")
		);
		
		Codec<List<Either<Integer, String>>> listCodec = eitherCodec.list();
		List<Either<Integer, String>> decoded = listCodec.decode(PROVIDER, listCodec.encode(PROVIDER, mixedList));
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
	void encodeAndDecodeEitherWithOptional() {
		Codec<Either<Optional<String>, Integer>> eitherOptionalCodec =
			either(STRING.optional(), INTEGER);
		
		Either<Optional<String>, Integer> leftPresent = Either.left(Optional.of("test"));
		Either<Optional<String>, Integer> decodedLeftPresent = eitherOptionalCodec.decode(PROVIDER, eitherOptionalCodec.encode(PROVIDER, leftPresent));
		assertTrue(decodedLeftPresent.isLeft());
		assertTrue(decodedLeftPresent.leftOrThrow().isPresent());
		assertEquals("test", decodedLeftPresent.leftOrThrow().get());
		
		Either<Optional<String>, Integer> leftEmpty = Either.left(Optional.empty());
		Either<Optional<String>, Integer> decodedLeftEmpty = eitherOptionalCodec.decode(PROVIDER, eitherOptionalCodec.encode(PROVIDER, leftEmpty));
		assertTrue(decodedLeftEmpty.isLeft());
		assertTrue(decodedLeftEmpty.leftOrThrow().isEmpty());
		
		Either<Optional<String>, Integer> right = Either.right(42);
		Either<Optional<String>, Integer> decodedRight = eitherOptionalCodec.decode(PROVIDER, eitherOptionalCodec.encode(PROVIDER, right));
		assertTrue(decodedRight.isLeft());
		assertTrue(decodedRight.leftOrThrow().isEmpty());
	}
	
	@Test
	void encodeAndDecodeNestedEither() {
		Codec<Either<Either<Integer, String>, Boolean>> nestedEitherCodec =
			either(either(INTEGER, STRING), BOOLEAN);
		
		Either<Either<Integer, String>, Boolean> leftLeft = Either.left(Either.left(42));
		Either<Either<Integer, String>, Boolean> decodedLL = nestedEitherCodec.decode(PROVIDER, nestedEitherCodec.encode(PROVIDER, leftLeft));
		assertTrue(decodedLL.isLeft());
		assertTrue(decodedLL.leftOrThrow().isLeft());
		assertEquals(42, decodedLL.leftOrThrow().leftOrThrow());
		
		Either<Either<Integer, String>, Boolean> leftRight = Either.left(Either.right("hello"));
		Either<Either<Integer, String>, Boolean> decodedLR = nestedEitherCodec.decode(PROVIDER, nestedEitherCodec.encode(PROVIDER, leftRight));
		assertTrue(decodedLR.isLeft());
		assertTrue(decodedLR.leftOrThrow().isRight());
		assertEquals("hello", decodedLR.leftOrThrow().rightOrThrow());
		
		Either<Either<Integer, String>, Boolean> right = Either.right(true);
		Either<Either<Integer, String>, Boolean> decodedR = nestedEitherCodec.decode(PROVIDER, nestedEitherCodec.encode(PROVIDER, right));
		assertTrue(decodedR.isRight());
		assertTrue(decodedR.rightOrThrow());
	}
	
	@Test
	void encodeAndDecodeVeryLongString() {
		String longString = "A".repeat(10000);
		String decoded = STRING.decode(PROVIDER, STRING.encode(PROVIDER, longString));
		assertEquals(longString, decoded);
		assertEquals(10000, decoded.length());
	}
	
	@Test
	void encodeAndDecodeStringWithSpecialCharacters() {
		String specialChars = "!@#$%^&*()_+-=[]{}|;':\",./<>?`~\n\r\t\\\u0000\u0001";
		String decoded = STRING.decode(PROVIDER, STRING.encode(PROVIDER, specialChars));
		assertEquals(specialChars, decoded);
	}
	
	@Test
	void encodeAndDecodeUnicodeStrings() {
		String unicode = "Hello世界🌍مرحبا🚀Привет";
		String decoded = STRING.decode(PROVIDER, STRING.encode(PROVIDER, unicode));
		assertEquals(unicode, decoded);
		
		List<String> unicodeList = List.of("日本語", "한국어", "中文", "العربية", "עברית");
		Codec<List<String>> listCodec = STRING.list();
		List<String> decodedList = listCodec.decode(PROVIDER, listCodec.encode(PROVIDER, unicodeList));
		assertEquals(unicodeList, decodedList);
	}
	
	@Test
	void encodeAndDecodeEmptyStringEdgeCases() {
		assertEquals("", STRING.decode(PROVIDER, STRING.encode(PROVIDER, "")));
		
		List<String> listWithEmpty = List.of("", "a", "", "b", "");
		Codec<List<String>> listCodec = STRING.list();
		assertEquals(listWithEmpty, listCodec.decode(PROVIDER, listCodec.encode(PROVIDER, listWithEmpty)));
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
		assertArrayEquals(largeIntArray, INTEGER_ARRAY.decode(PROVIDER, INTEGER_ARRAY.encode(PROVIDER, largeIntArray)));
		
		double[] largeDoubleArray = IntStream.range(0, 1000).mapToDouble(i -> i * 0.5).toArray();
		assertArrayEquals(largeDoubleArray, DOUBLE_ARRAY.decode(PROVIDER, DOUBLE_ARRAY.encode(PROVIDER, largeDoubleArray)), 0.0001);
		
		boolean[] largeBoolArray = new boolean[1000];
		for (int i = 0; i < 1000; i++) {
			largeBoolArray[i] = i % 2 == 0;
		}
		assertArrayEquals(largeBoolArray, BOOLEAN_ARRAY.decode(PROVIDER, BOOLEAN_ARRAY.encode(PROVIDER, largeBoolArray)));
	}
	
	@Test
	void encodeAndDecodeComplexNestedOptionals() {
		Codec<Optional<List<Optional<String>>>> complexOptionalCodec =
			STRING.optional().list().optional();
		
		Optional<List<Optional<String>>> presentMixed = Optional.of(List.of(
			Optional.of("a"), Optional.empty(), Optional.of("b"), Optional.empty()
		));
		Optional<List<Optional<String>>> decodedPresent = complexOptionalCodec.decode(PROVIDER, complexOptionalCodec.encode(PROVIDER, presentMixed));
		assertTrue(decodedPresent.isPresent());
		assertEquals(2, decodedPresent.get().size());
		assertTrue(decodedPresent.get().get(0).isPresent());
		assertTrue(decodedPresent.get().get(1).isPresent());
		
		Optional<List<Optional<String>>> empty = Optional.empty();
		Optional<List<Optional<String>>> decodedEmpty = complexOptionalCodec.decode(PROVIDER, complexOptionalCodec.encode(PROVIDER, empty));
		assertTrue(decodedEmpty.isEmpty());
	}
	
	@Test
	void encodeAndDecodeMapWithComplexValues() {
		Codec<Person> personCodec = CodecBuilder.group(
			STRING.configure("name", Person::name),
			INTEGER.configure("age", Person::age),
			STRING.configure("email", Person::email)
		).create(Person::new);
		
		Map<String, List<Person>> departmentEmployees = Map.of(
			"Engineering", List.of(
				new Person("Alice", 30, "alice@example.com"),
				new Person("Bob", 35, "bob@example.com")
			),
			"Sales", List.of(
				new Person("Charlie", 28, "charlie@example.com"),
				new Person("Diana", 32, "diana@example.com")
			),
			"Marketing", List.of(
				new Person("Eve", 29, "eve@example.com")
			)
		);
		
		Codec<Map<String, List<Person>>> codec = Codec.map(STRING, personCodec.list());
		Map<String, List<Person>> decoded = codec.decode(PROVIDER, codec.encode(PROVIDER, departmentEmployees));
		assertEquals(departmentEmployees, decoded);
		assertEquals(3, decoded.size());
		assertEquals(2, decoded.get("Engineering").size());
	}
	
	@Test
	void encodeAndDecodeRecordWithAllCodecTypes() {
		Codec<ComplexRecord> complexCodec = CodecBuilder.group(
			STRING.configure("id", ComplexRecord::id),
			INTEGER.list().configure("numbers", ComplexRecord::numbers),
			Codec.map(STRING, DOUBLE).configure("metrics", ComplexRecord::metrics),
			STRING.optional().configure("optionalField", ComplexRecord::optionalField),
			either(INTEGER, STRING).configure("eitherField", ComplexRecord::eitherField),
			BOOLEAN_ARRAY.configure("flags", ComplexRecord::flags),
			LOCAL_DATE_TIME.configure("timestamp", ComplexRecord::timestamp)
		).create(ComplexRecord::new);
		
		ComplexRecord record = new ComplexRecord(
			"COMPLEX001",
			List.of(1, 2, 3, 4, 5),
			Map.of("cpu", 75.5, "memory", 60.2, "disk", 45.8),
			Optional.of("optional value"),
			Either.left(42),
			new boolean[] { true, false, true, false },
			LocalDateTime.of(2025, 10, 20, 10, 30, 0)
		);
		
		ComplexRecord decoded = complexCodec.decode(PROVIDER, complexCodec.encode(PROVIDER, record));
		assertEquals(record, decoded);
		assertEquals(5, decoded.numbers().size());
		assertEquals(3, decoded.metrics().size());
		assertTrue(decoded.optionalField().isPresent());
		assertTrue(decoded.eitherField().isLeft());
	}
	
	@Test
	void encodeAndDecodeDeeplyNestedCompanyStructure() {
		Codec<Address> addressCodec = CodecBuilder.group(
			STRING.configure("street", Address::street),
			STRING.configure("city", Address::city),
			STRING.configure("state", Address::state),
			INTEGER.configure("zipCode", Address::zipCode),
			STRING.configure("country", Address::country)
		).create(Address::new);
		
		Codec<Employee> employeeCodec = CodecBuilder.group(
			STRING.configure("id", Employee::id),
			STRING.configure("name", Employee::name),
			INTEGER.configure("age", Employee::age),
			STRING.configure("department", Employee::department),
			addressCodec.configure("address", Employee::address)
		).create(Employee::new);
		
		Codec<Department> departmentCodec = CodecBuilder.group(
			STRING.configure("id", Department::id),
			STRING.configure("name", Department::name),
			employeeCodec.configure("manager", Department::manager),
			employeeCodec.list().configure("employees", Department::employees),
			Codec.map(STRING, STRING).configure("metadata", Department::metadata)
		).create(Department::new);
		
		Codec<Company> companyCodec = CodecBuilder.group(
			STRING.configure("id", Company::id),
			STRING.configure("name", Company::name),
			addressCodec.configure("headquarters", Company::headquarters),
			departmentCodec.list().configure("departments", Company::departments),
			INTEGER.configure("foundedYear", Company::foundedYear)
		).create(Company::new);
		
		Address hqAddress = new Address("500 Enterprise Blvd", "New York", "NY", 10001, "USA");
		
		List<Department> departments = IntStream.range(0, 5)
			.mapToObj(deptIndex -> {
				Address managerAddr = new Address(
					(100 + deptIndex * 10) + " Manager St",
					"New York", "NY", 10001 + deptIndex, "USA"
				);
				Employee manager = new Employee(
					"MGR" + String.format("%03d", deptIndex),
					"Manager " + deptIndex,
					40 + deptIndex,
					"Department " + deptIndex,
					managerAddr
				);
				
				List<Employee> employees = IntStream.range(0, 10)
					.mapToObj(empIndex -> new Employee(
						"EMP" + String.format("%03d", deptIndex * 10 + empIndex),
						"Employee " + deptIndex + "-" + empIndex,
						25 + empIndex,
						"Department " + deptIndex,
						new Address(
							(200 + empIndex) + " Employee Ave",
							"New York", "NY", 10001 + deptIndex, "USA"
						)
					))
					.toList();
				
				return new Department(
					"DEPT" + String.format("%03d", deptIndex),
					"Department " + deptIndex,
					manager,
					employees,
					Map.of(
						"budget", String.valueOf((deptIndex + 1) * 1000000),
						"headcount", String.valueOf(employees.size() + 1)
					)
				);
			})
			.toList();
		
		Company company = new Company("COMP001", "MegaCorp International", hqAddress, departments, 2000);
		
		Company decoded = companyCodec.decode(PROVIDER, companyCodec.encode(PROVIDER, company));
		assertEquals(company, decoded);
		assertEquals(5, decoded.departments().size());
		
		for (int i = 0; i < 5; i++) {
			Department dept = decoded.departments().get(i);
			assertEquals(10, dept.employees().size());
			assertEquals("Department " + i, dept.name());
			assertEquals("Manager " + i, dept.manager().name());
		}
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
		
		Map<String, Map<String, List<Integer>>> decoded =
			codec.decode(PROVIDER, codec.encode(PROVIDER, complexNested));
		assertEquals(complexNested, decoded);
		assertEquals(3, decoded.size());
		assertEquals(2, decoded.get("region1").size());
		assertEquals(3, decoded.get("region1").get("store1").size());
	}
	
	@Test
	void encodeAndDecodeConstrainedNumericCodecs() {
		KeyableCodec<Integer> rangedInt = Codec.ranged(INTEGER, 0, 100);
		assertEquals(50, rangedInt.decode(PROVIDER, rangedInt.encode(PROVIDER, 50)));
		assertEquals(0, rangedInt.decode(PROVIDER, rangedInt.encode(PROVIDER, 0)));
		assertEquals(100, rangedInt.decode(PROVIDER, rangedInt.encode(PROVIDER, 100)));
		
		KeyableCodec<Long> atLeastLong = Codec.atLeast(LONG, 1000L);
		assertEquals(5000L, atLeastLong.decode(PROVIDER, atLeastLong.encode(PROVIDER, 5000L)));
		
		KeyableCodec<Double> atMostDouble = Codec.atMost(DOUBLE, 1.0);
		assertEquals(0.5, atMostDouble.decode(PROVIDER, atMostDouble.encode(PROVIDER, 0.5)));
	}
	
	@Test
	void encodeAndDecodeFormattedAndValidatedStrings() {
		KeyableCodec<String> emailCodec = formattedString("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
		String email = "test@example.com";
		assertEquals(email, emailCodec.decode(PROVIDER, emailCodec.encode(PROVIDER, email)));
		
		KeyableCodec<String> nonEmptyCodec = noneEmptyString();
		assertEquals("hello", nonEmptyCodec.decode(PROVIDER, nonEmptyCodec.encode(PROVIDER, "hello")));
		
		KeyableCodec<String> lengthCodec = string(5, 10);
		assertEquals("hello", lengthCodec.decode(PROVIDER, lengthCodec.encode(PROVIDER, "hello")));
	}
	
	@Test
	void encodeAndDecodeMapCodecWithVariousKeyTypes() {
		Codec<Map<java.util.UUID, String>> uuidMapCodec = Codec.map(UUID, STRING);
		java.util.UUID uuid1 = java.util.UUID.randomUUID();
		java.util.UUID uuid2 = java.util.UUID.randomUUID();
		Map<java.util.UUID, String> uuidMap = Map.of(uuid1, "first", uuid2, "second");
		Map<java.util.UUID, String> decodedUuidMap = uuidMapCodec.decode(PROVIDER, uuidMapCodec.encode(PROVIDER, uuidMap));
		assertEquals(uuidMap, decodedUuidMap);
		
		Codec<Map<Integer, Double>> intMapCodec = Codec.map(INTEGER, DOUBLE);
		Map<Integer, Double> intMap = Map.of(1, 1.5, 2, 2.5, 3, 3.5);
		assertEquals(intMap, intMapCodec.decode(PROVIDER, intMapCodec.encode(PROVIDER, intMap)));
		
		Codec<Map<Long, List<String>>> longMapCodec = Codec.map(LONG, STRING.list());
		Map<Long, List<String>> longMap = Map.of(
			100L, List.of("a", "b", "c"),
			200L, List.of("d", "e", "f")
		);
		assertEquals(longMap, longMapCodec.decode(PROVIDER, longMapCodec.encode(PROVIDER, longMap)));
	}
	
	@Test
	void encodeAndDecodeCollectionCodecsWithSizeConstraints() {
		Codec<List<Integer>> nonEmptyList = INTEGER.noneEmptyList();
		List<Integer> list1 = List.of(1, 2, 3);
		assertEquals(list1, nonEmptyList.decode(PROVIDER, nonEmptyList.encode(PROVIDER, list1)));
		
		Codec<List<String>> constrainedList = Codec.list(STRING, 2, 5);
		List<String> list2 = List.of("a", "b", "c");
		assertEquals(list2, constrainedList.decode(PROVIDER, constrainedList.encode(PROVIDER, list2)));
		
		Codec<Map<String, Integer>> nonEmptyMap = Codec.noneEmptyMap(STRING, INTEGER);
		Map<String, Integer> map1 = Map.of("x", 1, "y", 2);
		assertEquals(map1, nonEmptyMap.decode(PROVIDER, nonEmptyMap.encode(PROVIDER, map1)));
		
		Codec<Integer[]> constrainedArray = Codec.array(Integer.class, INTEGER, 1, 10);
		Integer[] arr = { 1, 2, 3, 4, 5 };
		assertArrayEquals(arr, constrainedArray.decode(PROVIDER, constrainedArray.encode(PROVIDER, arr)));
	}
	
	@Test
	void encodeAndDecodeOptionalCodecVariants() {
		Codec<String> optionalWithSupplier = STRING.optionalWithDefaultFrom(() -> "default-" + System.currentTimeMillis());
		String decoded1 = optionalWithSupplier.decode(PROVIDER, optionalWithSupplier.encode(PROVIDER, "test"));
		assertEquals("test", decoded1);
		
		Codec<List<Integer>> nullableList = Codec.nullable(INTEGER.list());
		List<Integer> list = List.of(1, 2, 3);
		assertEquals(list, nullableList.decode(PROVIDER, nullableList.encode(PROVIDER, list)));
		assertNull(nullableList.decode(PROVIDER, nullableList.encode(PROVIDER, null)));
	}
	
	@Test
	void encodeAndDecodeAlternativeCodecFallback() {
		Codec<Integer> alternativeCodec = Codec.withAlternative(
			Codec.ranged(INTEGER, 0, 100),
			INTEGER
		);
		
		assertEquals(50, alternativeCodec.decode(PROVIDER, alternativeCodec.encode(PROVIDER, 50)));
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
		
		UltraComplexRecord decoded = ultraCodec.decode(PROVIDER, ultraCodec.encode(PROVIDER, record));
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
		
		UltraComplexRecord decoded = ultraCodec.decode(PROVIDER, ultraCodec.encode(PROVIDER, record));
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
		
		UltraComplexRecord decoded = ultraCodec.decode(PROVIDER, ultraCodec.encode(PROVIDER, record));
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
		
		UltraComplexRecord decoded = ultraCodec.decode(PROVIDER, ultraCodec.encode(PROVIDER, record));
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
			either(STRING, INTEGER).configure("sensorId", ScientificDataRecord::sensorId),
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
		
		ScientificDataRecord decoded = codec.decode(PROVIDER, codec.encode(PROVIDER, record));
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
			either(STRING, INTEGER).configure("sensorId", ScientificDataRecord::sensorId),
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
		
		ScientificDataRecord decoded = codec.decode(PROVIDER, codec.encode(PROVIDER, record));
		assertEquals(-273.15, decoded.temperature(), 0.001);
		assertEquals(100.0, decoded.humidity(), 0.001);
		assertTrue(decoded.sensorId().isRight());
		assertEquals(999999, decoded.sensorId().rightOrThrow());
	}
	
	@Test
	void encodeAndDecodeNetworkConfigurationWithSSL() {
		Codec<NetworkConfigRecord> codec = CodecBuilder.group(
			STRING.configure("hostname", NetworkConfigRecord::hostname),
			Codec.ranged(INTEGER, 1, 65535).configure("port", NetworkConfigRecord::port),
			URI.list(1, 10).configure("endpoints", NetworkConfigRecord::endpoints),
			Codec.map(STRING, URL).configure("services", NetworkConfigRecord::services),
			either(Codec.ranged(INTEGER, 1000, 10000), STRING).configure("timeout", NetworkConfigRecord::timeout),
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
			
			NetworkConfigRecord decoded = codec.decode(PROVIDER, codec.encode(PROVIDER, record));
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
			either(Codec.ranged(INTEGER, 1000, 10000), STRING).configure("timeout", NetworkConfigRecord::timeout),
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
			
			NetworkConfigRecord decoded = codec.decode(PROVIDER, codec.encode(PROVIDER, record));
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
			either(Codec.ranged(DOUBLE, 0.0, 1.0), STRING).configure("confidence", DataAnalyticsRecord::confidence)
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
		
		DataAnalyticsRecord decoded = codec.decode(PROVIDER, codec.encode(PROVIDER, record));
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
			either(Codec.ranged(DOUBLE, 0.0, 1.0), STRING).configure("confidence", DataAnalyticsRecord::confidence)
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
		
		DataAnalyticsRecord decoded = codec.decode(PROVIDER, codec.encode(PROVIDER, record));
		assertEquals(1000, decoded.timeSeries().size());
		assertEquals(50, decoded.categoryCounts().size());
		assertTrue(decoded.confidence().isRight());
	}
	
	@Test
	void encodeAndDecodeConfigurationRecordWithValidation() {
		Codec<ConfigurationRecord> codec = CodecBuilder.group(
			STRING.configure("configId", ConfigurationRecord::configId),
			STRING.optionalWithDefault("default-app").configure("appName", ConfigurationRecord::appName),
			Codec.ranged(INTEGER, 1, 100).optionalWithDefault(10).configure("threadPoolSize", ConfigurationRecord::threadPoolSize),
			BOOLEAN.optionalWithDefault(false).configure("debugMode", ConfigurationRecord::debugMode),
			formattedString("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}").optional().configure("ipAddress", ConfigurationRecord::ipAddress),
			Codec.map(STRING, STRING).configure("environment", ConfigurationRecord::environment),
			DURATION.optionalWithDefault(Duration.ofSeconds(30)).configure("timeout", ConfigurationRecord::timeout),
			either(FILE, URI).list().configure("configSources", ConfigurationRecord::configSources)
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
		
		ConfigurationRecord decoded = codec.decode(PROVIDER, codec.encode(PROVIDER, record));
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
			STRING.optionalWithDefault("default-app").configure("appName", ConfigurationRecord::appName),
			Codec.ranged(INTEGER, 1, 100).optionalWithDefault(10).configure("threadPoolSize", ConfigurationRecord::threadPoolSize),
			BOOLEAN.optionalWithDefault(false).configure("debugMode", ConfigurationRecord::debugMode),
			formattedString("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}").optional().configure("ipAddress", ConfigurationRecord::ipAddress),
			Codec.map(STRING, STRING).configure("environment", ConfigurationRecord::environment),
			DURATION.optionalWithDefault(Duration.ofSeconds(30)).configure("timeout", ConfigurationRecord::timeout),
			either(FILE, URI).list().configure("configSources", ConfigurationRecord::configSources)
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
		
		ConfigurationRecord decoded = codec.decode(PROVIDER, codec.encode(PROVIDER, record));
		assertEquals("default-app", decoded.appName());
		assertEquals(10, decoded.threadPoolSize());
		assertFalse(decoded.debugMode());
		assertTrue(decoded.ipAddress().isEmpty());
	}
	
	@Test
	void encodeAndDecodeDeeplyNestedMixedCodecTypes() {
		Codec<Map<java.util.UUID, List<Either<LocalDate, Instant>>>> complexNested = Codec.map(
			UUID,
			either(LOCAL_DATE, INSTANT).list()
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
		
		Map<java.util.UUID, List<Either<LocalDate, Instant>>> decoded = complexNested.decode(PROVIDER, complexNested.encode(PROVIDER, data));
		
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
		
		TimeAndIORecord decoded = codec.decode(PROVIDER, codec.encode(PROVIDER, record));
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
		
		List<IntStream> decoded = listOfStreamsCodec.decode(PROVIDER, listOfStreamsCodec.encode(PROVIDER, streams));
		assertEquals(3, decoded.size());
		assertArrayEquals(new int[] { 1, 2, 3 }, decoded.getFirst().toArray());
		
		Codec<Map<String, DoubleStream>> mapOfStreamsCodec = Codec.map(STRING, DOUBLE_STREAM);
		Map<String, DoubleStream> streamMap = Map.of(
			"data1", DoubleStream.of(1.1, 2.2, 3.3),
			"data2", DoubleStream.of(4.4, 5.5)
		);
		
		Map<String, DoubleStream> decodedMap = mapOfStreamsCodec.decode(PROVIDER, mapOfStreamsCodec.encode(PROVIDER, streamMap));
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
		
		Map<Priority, List<Task>> decoded = enumMapCodec.decode(PROVIDER, enumMapCodec.encode(PROVIDER, taskMap));
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
		
		LocalDate[] decoded = dateArrayCodec.decode(PROVIDER, dateArrayCodec.encode(PROVIDER, dates));
		assertArrayEquals(dates, decoded);
		
		Codec<Path[]> pathArrayCodec = PATH.array(Path.class, 1, 5);
		Path[] paths = { Path.of("/home/user1"), Path.of("/home/user2") };
		Path[] decodedPaths = pathArrayCodec.decode(PROVIDER, pathArrayCodec.encode(PROVIDER, paths));
		assertArrayEquals(paths, decodedPaths);
	}
	
	@Test
	void encodeAndDecodeDeeplyNestedOptionalAndEither() {
		Codec<Optional<List<Optional<Either<Integer, String>>>>> multiLevelCodec = either(INTEGER, STRING).optional().list().optional();
		
		Optional<List<Optional<Either<Integer, String>>>> data = Optional.of(List.of(
			Optional.of(Either.left(42)),
			Optional.empty(),
			Optional.of(Either.right("hello")),
			Optional.of(Either.left(100))
		));
		
		Optional<List<Optional<Either<Integer, String>>>> decoded = multiLevelCodec.decode(PROVIDER, multiLevelCodec.encode(PROVIDER, data));
		
		assertTrue(decoded.isPresent());
		assertEquals(3, decoded.get().size());
		assertTrue(decoded.get().getFirst().isPresent());
		assertTrue(decoded.get().getFirst().get().isLeft());
		assertEquals(42, decoded.get().getFirst().get().leftOrThrow());
	}
	
	private static @NotNull Codec<UltraComplexRecord> createUltraComplexCodec() {
		return CodecBuilder.group(
			UUID.configure("id", UltraComplexRecord::id),
			Codec.ranged(INTEGER, 0, 150).configure("age", UltraComplexRecord::age),
			formattedString("[a-zA-Z]+").configure("name", UltraComplexRecord::name),
			LOCAL_DATE_TIME.configure("timestamp", UltraComplexRecord::timestamp),
			DURATION.configure("elapsed", UltraComplexRecord::elapsed),
			Codec.map(UUID, DOUBLE).configure("metrics", UltraComplexRecord::metrics),
			either(FILE, PATH).configure("location", UltraComplexRecord::location),
			Codec.nullable(CHARSET).configure("encoding", UltraComplexRecord::encoding),
			FLOAT_ARRAY.configure("scores", UltraComplexRecord::scores),
			INT_STREAM.configure("dataStream", UltraComplexRecord::dataStream),
			Codec.noneEmptyList(URI).configure("endpoints", UltraComplexRecord::endpoints),
			Codec.optional(ZONED_DATE_TIME).configure("lastModified", UltraComplexRecord::lastModified)
		).create(UltraComplexRecord::new);
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
	
	private record ComplexRecord(
		@NotNull String id,
		@NotNull List<Integer> numbers,
		@NotNull Map<String, Double> metrics,
		@NotNull Optional<String> optionalField,
		@NotNull Either<Integer, String> eitherField,
		boolean[] flags,
		@NotNull LocalDateTime timestamp
	) {
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!(obj instanceof ComplexRecord other)) return false;
			return this.id.equals(other.id) &&
				this.numbers.equals(other.numbers) &&
				this.metrics.equals(other.metrics) &&
				this.optionalField.equals(other.optionalField) &&
				this.eitherField.equals(other.eitherField) &&
				java.util.Arrays.equals(this.flags, other.flags) &&
				this.timestamp.equals(other.timestamp);
		}
		
		@Override
		public int hashCode() {
			return java.util.Objects.hash(this.id, this.numbers, this.metrics, this.optionalField, this.eitherField, java.util.Arrays.hashCode(this.flags), this.timestamp);
		}
	}
	
	private enum Priority {
		LOW, MEDIUM, HIGH, URGENT, CRITICAL
	}
	
	private record Person(@NotNull String name, int age, @NotNull String email) {}
	
	private record Address(@NotNull String street, @NotNull String city, @NotNull String state, int zipCode, @NotNull String country) {}
	
	private record Employee(@NotNull String id, @NotNull String name, int age, @NotNull String department, @NotNull Address address) {}
	
	private record Team(@NotNull String id, @NotNull String name, @NotNull List<String> members, @NotNull Map<String, Integer> scores) {}
	
	private record Product(@NotNull String id, @NotNull String name, double price, int quantity) {}
	
	private record Department(@NotNull String id, @NotNull String name, @NotNull Employee manager, @NotNull List<Employee> employees, @NotNull Map<String, String> metadata) {}
	
	private record Company(@NotNull String id, @NotNull String name, @NotNull Address headquarters, @NotNull List<Department> departments, int foundedYear) {}
}
