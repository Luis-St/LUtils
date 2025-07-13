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

package net.luis.utils.io.codec.mapping;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.CodecBuilder;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.Either;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CodecAutoMapping}.<br>
 *
 * @author Luis-St
 */
class CodecAutoMappingTest {
	
	@Test
	void createAutoMappedCodecSimpleRecord() {
		record SimpleRecord(String name, int age, double height, boolean active) {}
		
		Codec<SimpleRecord> codec = CodecBuilder.autoMapCodec(SimpleRecord.class);
		assertNotNull(codec);
		
		SimpleRecord record = new SimpleRecord("Test", 25, 1.80, true);
		JsonElement encoded = codec.encode(JsonTypeProvider.INSTANCE, record);
		
		JsonObject jsonObject = assertInstanceOf(JsonObject.class, encoded);
		assertEquals("Test", jsonObject.get("name").getAsJsonPrimitive().getAsString());
		assertEquals(25, jsonObject.get("age").getAsJsonPrimitive().getAsInteger());
		assertEquals(1.80, jsonObject.get("height").getAsJsonPrimitive().getAsDouble(), 0.001);
		assertTrue(jsonObject.get("active").getAsJsonPrimitive().getAsBoolean());
		
		SimpleRecord decoded = codec.decode(JsonTypeProvider.INSTANCE, encoded);
		assertEquals(record, decoded);
	}
	
	@Test
	void createAutoMappedCodecRegularClass() {
		Codec<TestClass> codec = CodecBuilder.autoMapCodec(TestClass.class);
		assertNotNull(codec);
		
		TestClass testClass = new TestClass("Test", 25);
		JsonElement encoded = codec.encode(JsonTypeProvider.INSTANCE, testClass);
		
		JsonObject jsonObject = assertInstanceOf(JsonObject.class, encoded);
		assertEquals("Test", jsonObject.get("name").getAsJsonPrimitive().getAsString());
		assertEquals(25, jsonObject.get("age").getAsJsonPrimitive().getAsInteger());
		
		TestClass decoded = codec.decode(JsonTypeProvider.INSTANCE, encoded);
		assertEquals(testClass.name, decoded.name);
		assertEquals(testClass.age, decoded.age);
	}
	
	@Test
	void createAutoMappedCodecImplicitFields() {
		Codec<ImplicitFieldsClass> codec = CodecBuilder.autoMapCodec(ImplicitFieldsClass.class);
		assertNotNull(codec);
		
		ImplicitFieldsClass testObj = new ImplicitFieldsClass("Test", 25);
		JsonElement encoded = codec.encode(JsonTypeProvider.INSTANCE, testObj);
		
		JsonObject jsonObject = assertInstanceOf(JsonObject.class, encoded);
		assertEquals("Test", jsonObject.get("name").getAsJsonPrimitive().getAsString());
		assertEquals(25, jsonObject.get("age").getAsJsonPrimitive().getAsInteger());
		assertFalse(jsonObject.containsKey("ignored"));
		assertFalse(jsonObject.containsKey("transientField"));
		
		ImplicitFieldsClass decoded = codec.decode(JsonTypeProvider.INSTANCE, encoded);
		assertEquals(testObj.name, decoded.name);
		assertEquals(testObj.age, decoded.age);
	}
	
	@Test
	void createAutoMappedCodecNestedStructures() {
		Codec<Person> codec = CodecBuilder.autoMapCodec(Person.class);
		assertNotNull(codec);
		
		Person person = new Person(
			"John Doe",
			30,
			1.75,
			true,
			new Address[] {
				new Address("123 Main St", "Springfield", State.USA, 12345),
				new Address("456 Elm St", "Shelbyville", State.GERMANY, 67890)
			},
			Lists.newArrayList("Reading", "Traveling", "Cooking"),
			Maps.newHashMap(Map.of("Math", 95, "Science", 90, "History", 85)),
			Either.left(List.of("Mike", "Sarah")),
			Optional.empty()
		);
		
		JsonElement encoded = codec.encode(JsonTypeProvider.INSTANCE, person);
		JsonObject jsonObject = assertInstanceOf(JsonObject.class, encoded);
		
		assertEquals("John Doe", jsonObject.get("name").getAsJsonPrimitive().getAsString());
		assertEquals(30, jsonObject.get("age").getAsJsonPrimitive().getAsInteger());
		assertEquals(1.75, jsonObject.get("height").getAsJsonPrimitive().getAsDouble(), 0.001);
		assertTrue(jsonObject.get("gender").getAsJsonPrimitive().getAsBoolean());
		
		JsonArray addressesArray = jsonObject.get("addresses").getAsJsonArray();
		assertEquals(2, addressesArray.size());
		assertEquals("123 Main St", addressesArray.get(0).getAsJsonObject().get("street").getAsJsonPrimitive().getAsString());
		assertEquals("Springfield", addressesArray.get(0).getAsJsonObject().get("city").getAsJsonPrimitive().getAsString());
		assertEquals("USA", addressesArray.get(0).getAsJsonObject().get("state").getAsJsonPrimitive().getAsString());
		
		JsonArray hobbiesArray = jsonObject.get("hobbies").getAsJsonArray();
		assertEquals(3, hobbiesArray.size());
		assertEquals("Reading", hobbiesArray.get(0).getAsJsonPrimitive().getAsString());
		
		JsonArray contactInfo = jsonObject.get("contactInfo").getAsJsonArray();
		assertEquals(2, contactInfo.size());
		assertEquals("Mike", contactInfo.get(0).getAsJsonPrimitive().getAsString());
		assertEquals("Sarah", contactInfo.get(1).getAsJsonPrimitive().getAsString());
		
		Person decoded = codec.decode(JsonTypeProvider.INSTANCE, encoded);
		assertEquals(person.name(), decoded.name());
		assertEquals(person.age(), decoded.age());
		assertEquals(person.height(), decoded.height(), 0.001);
		assertEquals(person.gender(), decoded.gender());
		
		assertEquals(person.addresses().length, decoded.addresses().length);
		for (int i = 0; i < person.addresses().length; i++) {
			assertEquals(person.addresses()[i].street, decoded.addresses()[i].street);
			assertEquals(person.addresses()[i].city, decoded.addresses()[i].city);
			assertEquals(person.addresses()[i].state, decoded.addresses()[i].state);
			assertEquals(person.addresses()[i].zip, decoded.addresses()[i].zip);
		}
		
		assertEquals(person.hobbies(), decoded.hobbies());
		assertEquals(person.scores(), decoded.scores());
		
		assertTrue(decoded.contactInfo().isLeft());
		assertEquals(person.contactInfo().left(), decoded.contactInfo().left());
		
		assertTrue(decoded.nickname().isEmpty());
	}
	
	@Test
	void createAutoMappedCodecCollections() {
		record CollectionRecord(
			@GenericInfo(Integer.class) List<Integer> numbers,
			@GenericInfo(String.class) Set<String> strings,
			@GenericInfo({ String.class, LocalDate.class }) Map<String, LocalDate> dates
		) {}
		
		Codec<CollectionRecord> codec = CodecBuilder.autoMapCodec(CollectionRecord.class);
		assertNotNull(codec);
		
		List<Integer> numbers = List.of(1, 2, 3, 4, 5);
		Set<String> strings = new HashSet<>(Set.of("one", "two", "three"));
		Map<String, LocalDate> dates = Map.of(
			"birthday", LocalDate.of(1990, 1, 1),
			"anniversary", LocalDate.of(2020, 6, 15)
		);
		
		CollectionRecord record = new CollectionRecord(numbers, strings, dates);
		JsonElement encoded = codec.encode(JsonTypeProvider.INSTANCE, record);
		
		JsonObject jsonObject = assertInstanceOf(JsonObject.class, encoded);
		JsonArray numbersArray = jsonObject.get("numbers").getAsJsonArray();
		assertEquals(5, numbersArray.size());
		for (int i = 0; i < numbers.size(); i++) {
			assertEquals(numbers.get(i), numbersArray.get(i).getAsJsonPrimitive().getAsInteger());
		}
		
		JsonArray stringsArray = jsonObject.get("strings").getAsJsonArray();
		assertEquals(3, stringsArray.size());
		
		JsonObject datesObject = jsonObject.get("dates").getAsJsonObject();
		assertEquals(2, datesObject.size());
		assertEquals("1990-01-01", datesObject.get("birthday").getAsJsonPrimitive().getAsString());
		assertEquals("2020-06-15", datesObject.get("anniversary").getAsJsonPrimitive().getAsString());
		
		CollectionRecord decoded = codec.decode(JsonTypeProvider.INSTANCE, encoded);
		assertEquals(record.numbers(), decoded.numbers());
		assertEquals(record.strings().size(), decoded.strings().size());
		assertTrue(record.strings().containsAll(decoded.strings()));
		assertEquals(record.dates(), decoded.dates());
	}
	
	@Test
	void createAutoMappedCodecNestedGenerics() {
		record NestedGenericRecord(
			@GenericInfo({ List.class, Integer.class }) List<List<Integer>> nestedList,
			@GenericInfo({ String.class, List.class, String.class }) Map<String, List<String>> nestedMap,
			@GenericInfo({ List.class, String.class }) Optional<List<String>> optionalList
		) {}
		
		Codec<NestedGenericRecord> codec = CodecBuilder.autoMapCodec(NestedGenericRecord.class);
		assertNotNull(codec);
		
		List<List<Integer>> nestedList = List.of(
			List.of(1, 2, 3),
			List.of(4, 5, 6)
		);
		Map<String, List<String>> nestedMap = Map.of(
			"group1", List.of("a", "b", "c"),
			"group2", List.of("d", "e", "f")
		);
		Optional<List<String>> optionalList = Optional.of(List.of("x", "y", "z"));
		
		NestedGenericRecord record = new NestedGenericRecord(nestedList, nestedMap, optionalList);
		JsonElement encoded = codec.encode(JsonTypeProvider.INSTANCE, record);
		assertInstanceOf(JsonObject.class, encoded);
		
		NestedGenericRecord decoded = codec.decode(JsonTypeProvider.INSTANCE, encoded);
		
		assertEquals(2, decoded.nestedList().size());
		assertEquals(3, decoded.nestedList().get(0).size());
		assertEquals(1, decoded.nestedList().get(0).getFirst());
		assertEquals(6, decoded.nestedList().get(1).get(2));
		
		assertEquals(2, decoded.nestedMap().size());
		assertEquals(3, decoded.nestedMap().get("group1").size());
		assertEquals("a", decoded.nestedMap().get("group1").getFirst());
		
		assertTrue(decoded.optionalList().isPresent());
		assertEquals(3, decoded.optionalList().get().size());
		assertEquals("x", decoded.optionalList().get().getFirst());
	}
	
	@Test
	void createAutoMappedCodecEdgeCases() {
		record EmptyRecord() {}
		Codec<EmptyRecord> emptyCodec = CodecBuilder.autoMapCodec(EmptyRecord.class);
		assertNotNull(emptyCodec);
		
		EmptyRecord emptyRecord = new EmptyRecord();
		JsonElement encodedEmpty = emptyCodec.encode(JsonTypeProvider.INSTANCE, emptyRecord);
		assertInstanceOf(JsonNull.class, encodedEmpty);
		
		EmptyRecord decodedEmpty = emptyCodec.decode(JsonTypeProvider.INSTANCE, encodedEmpty);
		assertEquals(emptyRecord, decodedEmpty);
		
		Codec<EmptyClass> emptyClassCodec = CodecBuilder.autoMapCodec(EmptyClass.class);
		assertNotNull(emptyClassCodec);
		
		EmptyClass emptyClass = new EmptyClass();
		JsonElement encodedEmptyClass = emptyClassCodec.encode(JsonTypeProvider.INSTANCE, emptyClass);
		assertInstanceOf(JsonNull.class, encodedEmptyClass);
		
		EmptyClass decodedEmptyClass = emptyClassCodec.decode(JsonTypeProvider.INSTANCE, encodedEmptyClass);
		assertNotNull(decodedEmptyClass);
	}
	
	@Test
	void createAutoMappedCodecEnum() {
		Codec<State> codec = CodecBuilder.autoMapCodec(State.class);
		assertNotNull(codec);
		
		State state = State.GERMANY;
		JsonElement encoded = codec.encode(JsonTypeProvider.INSTANCE, state);
		
		assertInstanceOf(JsonPrimitive.class, encoded);
		assertEquals("GERMANY", encoded.getAsJsonPrimitive().getAsString());
		
		State decoded = codec.decode(JsonTypeProvider.INSTANCE, encoded);
		assertEquals(state, decoded);
	}
	
	@Test
	void createAutoMappedCodecMissingGenericInfo() {
		record MissingGenericInfo(@NotNull List<String> list) {}
		
		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> CodecBuilder.autoMapCodec(MissingGenericInfo.class)
		);
		assertTrue(exception.getMessage().contains("Missing generic type information"));
	}
	
	@Test
	void createAutoMappedCodecErrorCases() {
		assertThrows(NullPointerException.class, () -> CodecBuilder.autoMapCodec(null));
		assertThrows(IllegalArgumentException.class, () -> CodecBuilder.autoMapCodec(TestInterface.class));
		assertThrows(IllegalArgumentException.class, () -> CodecBuilder.autoMapCodec(TestAnnotation.class));
		assertThrows(IllegalArgumentException.class, () -> CodecBuilder.autoMapCodec(int.class));
		
		assertThrows(IllegalArgumentException.class, () -> CodecBuilder.autoMapCodec(TooManyFieldsRecord.class));
	}
	
	@Test
	void createAutoMappedCodecMultipleConstructors() {
		Codec<MultiConstructorClass> codec = CodecBuilder.autoMapCodec(MultiConstructorClass.class);
		assertNotNull(codec);
		
		MultiConstructorClass obj = new MultiConstructorClass("Test", 30);
		JsonElement encoded = codec.encode(JsonTypeProvider.INSTANCE, obj);
		
		JsonObject jsonObject = assertInstanceOf(JsonObject.class, encoded);
		assertEquals("Test", jsonObject.get("name").getAsJsonPrimitive().getAsString());
		assertEquals(30, jsonObject.get("age").getAsJsonPrimitive().getAsInteger());
		
		MultiConstructorClass decoded = codec.decode(JsonTypeProvider.INSTANCE, encoded);
		assertEquals(obj.name, decoded.name);
		assertEquals(obj.age, decoded.age);
	}
	
	@Test
	void createAutoMappedCodecInvalidConstructor() {
		// Test with a class that has incompatible constructor parameters
		assertThrows(
			IllegalArgumentException.class,
			() -> CodecBuilder.autoMapCodec(InvalidConstructorClass.class)
		);
	}
	
	//region Test Classes
	private static final class TestClass {
		
		@CodecField
		private final String name;
		@CodecField
		private final int age;
		private final String ignored; // Not annotated, should be ignored
		
		@CodecConstructor
		private TestClass(String name, int age) {
			this.name = name;
			this.age = age;
			this.ignored = "This field is ignored";
		}
	}
	
	private static final class ImplicitFieldsClass {
		
		private final String name;
		private final int age;
		private String ignored; // Not final, should be ignored
		private transient final String transientField = "ignored"; // Transient, should be ignored
		
		private ImplicitFieldsClass(@NotNull String name, int age) {
			this.name = name;
			this.age = age;
			this.ignored = "This field is ignored";
		}
	}
	
	private static class EmptyClass {
		// Empty class with default constructor
	}
	
	private static final class MultiConstructorClass {
		
		private final String name;
		private final int age;
		
		private MultiConstructorClass(@NotNull String name) {
			this(name, 0);
		}
		
		@CodecConstructor
		private MultiConstructorClass(@NotNull String name, int age) {
			this.name = name;
			this.age = age;
		}
	}
	
	private static final class InvalidConstructorClass {
		
		@CodecField
		private final String name;
		@CodecField
		private final int age;
		
		@CodecConstructor
		private InvalidConstructorClass(@NotNull String name, @NotNull String age) {
			this.name = name;
			this.age = Integer.parseInt(age);
		}
	}
	
	private enum State {
		USA, GERMANY, FRANCE, ITALY, SPAIN, UK
	}
	
	private static class Address {
		
		final String street;
		final String city;
		final State state;
		final long zip;
		
		Address(String street, String city, State state) {
			this(street, city, state, 0);
		}
		
		@CodecConstructor
		Address(String street, String city, State state, long zip) {
			this.street = street;
			this.city = city;
			this.state = state;
			this.zip = zip;
		}
	}
	
	private record Person(
		String name,
		int age,
		double height,
		boolean gender,
		Address[] addresses,
		@GenericInfo(String.class) @NotNull List<String> hobbies,
		@GenericInfo({ String.class, Integer.class }) @NotNull Map<String, Integer> scores,
		@GenericInfo({ List.class, List.class, String.class, Integer.class }) @NotNull Either<List<String>, List<Integer>> contactInfo,
		@GenericInfo(String.class) @NotNull Optional<String> nickname
	) {}
	
	private record TooManyFieldsRecord(
		int field1, int field2, int field3, int field4,
		int field5, int field6, int field7, int field8,
		int field9, int field10, int field11, int field12,
		int field13, int field14, int field15, int field16,
		int field17 // 17 fields, which exceeds the limit of 16
	) {}
	
	private interface TestInterface {}
	
	private @interface TestAnnotation {}
	//endregion
}
