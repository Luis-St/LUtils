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
	void nullClassThrowsException() {
		assertThrows(NullPointerException.class, () -> CodecBuilder.autoMapCodec(null));
	}
	
	@Test
	void invalidClassTypesThrowExceptions() {
		assertThrows(IllegalArgumentException.class, () -> CodecBuilder.autoMapCodec(TestInterface.class));
		assertThrows(IllegalArgumentException.class, () -> CodecBuilder.autoMapCodec(TestAnnotation.class));
		assertThrows(IllegalArgumentException.class, () -> CodecBuilder.autoMapCodec(int.class));
	}
	
	@Test
	void simpleRecordCodecWorks() {
		record SimpleRecord(String name, int age, double height, boolean active) {}
		
		Codec<SimpleRecord> codec = CodecBuilder.autoMapCodec(SimpleRecord.class);
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		SimpleRecord original = new SimpleRecord("Alice", 30, 1.70, true);
		JsonElement encoded = codec.encode(provider, original);
		
		assertInstanceOf(JsonObject.class, encoded);
		JsonObject jsonObject = (JsonObject) encoded;
		assertEquals("Alice", jsonObject.get("name").getAsJsonPrimitive().getAsString());
		assertEquals(30, jsonObject.get("age").getAsJsonPrimitive().getAsInteger());
		assertEquals(1.70, jsonObject.get("height").getAsJsonPrimitive().getAsDouble(), 0.001);
		assertTrue(jsonObject.get("active").getAsJsonPrimitive().getAsBoolean());
		
		SimpleRecord decoded = codec.decode(provider, encoded);
		assertEquals(original, decoded);
	}
	
	@Test
	void emptyRecordCodecWorks() {
		record EmptyRecord() {}
		
		Codec<EmptyRecord> codec = CodecBuilder.autoMapCodec(EmptyRecord.class);
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		EmptyRecord original = new EmptyRecord();
		JsonElement encoded = codec.encode(provider, original);
		assertEquals(JsonNull.INSTANCE, encoded);
		
		EmptyRecord decoded = codec.decode(provider, encoded);
		assertEquals(original, decoded);
	}
	
	@Test
	void enumCodecWorks() {
		Codec<TestEnum> codec = CodecBuilder.autoMapCodec(TestEnum.class);
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		JsonElement encoded = codec.encode(provider, TestEnum.FIRST);
		assertEquals(new JsonPrimitive("FIRST"), encoded);
		
		TestEnum decoded = codec.decode(provider, encoded);
		assertEquals(TestEnum.FIRST, decoded);
		
		TestEnum decodedFromOrdinal = codec.decode(provider, new JsonPrimitive(0));
		assertEquals(TestEnum.FIRST, decodedFromOrdinal);
	}
	
	@Test
	void annotatedFieldsOnlyAreUsed() {
		Codec<AnnotatedFieldsClass> codec = CodecBuilder.autoMapCodec(AnnotatedFieldsClass.class);
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		AnnotatedFieldsClass original = new AnnotatedFieldsClass("John", 25);
		JsonElement encoded = codec.encode(provider, original);
		
		assertInstanceOf(JsonObject.class, encoded);
		JsonObject jsonObject = (JsonObject) encoded;
		assertEquals("John", jsonObject.get("name").getAsJsonPrimitive().getAsString());
		assertEquals(25, jsonObject.get("age").getAsJsonPrimitive().getAsInteger());
		assertFalse(jsonObject.containsKey("ignored"));
		assertFalse(jsonObject.containsKey("transientField"));
		
		AnnotatedFieldsClass decoded = codec.decode(provider, encoded);
		assertEquals(original.name, decoded.name);
		assertEquals(original.age, decoded.age);
	}
	
	@Test
	void implicitFinalFieldsAreUsed() {
		Codec<ImplicitFieldsClass> codec = CodecBuilder.autoMapCodec(ImplicitFieldsClass.class);
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		ImplicitFieldsClass original = new ImplicitFieldsClass("Jane", 30);
		JsonElement encoded = codec.encode(provider, original);
		
		assertInstanceOf(JsonObject.class, encoded);
		JsonObject jsonObject = (JsonObject) encoded;
		assertEquals("Jane", jsonObject.get("name").getAsJsonPrimitive().getAsString());
		assertEquals(30, jsonObject.get("age").getAsJsonPrimitive().getAsInteger());
		assertFalse(jsonObject.containsKey("ignored"));
		assertFalse(jsonObject.containsKey("transientField"));
		
		ImplicitFieldsClass decoded = codec.decode(provider, encoded);
		assertEquals(original.name, decoded.name);
		assertEquals(original.age, decoded.age);
	}
	
	@Test
	void emptyClassCodecWorks() {
		Codec<EmptyClass> codec = CodecBuilder.autoMapCodec(EmptyClass.class);
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		EmptyClass original = new EmptyClass();
		JsonElement encoded = codec.encode(provider, original);
		assertEquals(JsonNull.INSTANCE, encoded);
		
		EmptyClass decoded = codec.decode(provider, encoded);
		assertNotNull(decoded);
	}
	
	@Test
	void multipleConstructorsWithAnnotation() {
		Codec<MultiConstructorClass> codec = CodecBuilder.autoMapCodec(MultiConstructorClass.class);
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		MultiConstructorClass original = new MultiConstructorClass("Test", 25);
		JsonElement encoded = codec.encode(provider, original);
		
		assertInstanceOf(JsonObject.class, encoded);
		JsonObject jsonObject = (JsonObject) encoded;
		assertEquals("Test", jsonObject.get("name").getAsJsonPrimitive().getAsString());
		assertEquals(25, jsonObject.get("age").getAsJsonPrimitive().getAsInteger());
		
		MultiConstructorClass decoded = codec.decode(provider, encoded);
		assertEquals(original.name, decoded.name);
		assertEquals(original.age, decoded.age);
	}
	
	@Test
	void invalidConstructorParametersThrowException() {
		assertThrows(IllegalArgumentException.class, () -> CodecBuilder.autoMapCodec(InvalidConstructorClass.class));
	}
	
	@Test
	void collectionsWithGenericInfo() {
		record CollectionRecord(
			@GenericInfo(Integer.class) List<Integer> numbers,
			@GenericInfo(String.class) Set<String> strings,
			@GenericInfo({ String.class, LocalDate.class }) Map<String, LocalDate> dates
		) {}
		
		Codec<CollectionRecord> codec = CodecBuilder.autoMapCodec(CollectionRecord.class);
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		List<Integer> numbers = List.of(1, 2, 3);
		Set<String> strings = Set.of("a", "b", "c");
		Map<String, LocalDate> dates = Map.of(
			"start", LocalDate.of(2025, 1, 1),
			"end", LocalDate.of(2025, 12, 31)
		);
		
		CollectionRecord original = new CollectionRecord(numbers, strings, dates);
		JsonElement encoded = codec.encode(provider, original);
		
		assertInstanceOf(JsonObject.class, encoded);
		JsonObject jsonObject = (JsonObject) encoded;
		
		JsonArray numbersArray = jsonObject.get("numbers").getAsJsonArray();
		assertEquals(3, numbersArray.size());
		assertEquals(1, numbersArray.get(0).getAsJsonPrimitive().getAsInteger());
		
		JsonArray stringsArray = jsonObject.get("strings").getAsJsonArray();
		assertEquals(3, stringsArray.size());
		
		JsonObject datesObject = jsonObject.get("dates").getAsJsonObject();
		assertEquals(2, datesObject.size());
		assertEquals("2025-01-01", datesObject.get("start").getAsJsonPrimitive().getAsString());
		
		CollectionRecord decoded = codec.decode(provider, encoded);
		assertEquals(original.numbers(), decoded.numbers());
		assertEquals(original.strings().size(), decoded.strings().size());
		assertTrue(original.strings().containsAll(decoded.strings()));
		assertEquals(original.dates(), decoded.dates());
	}
	
	@Test
	void nestedGenericTypes() {
		record NestedGenericRecord(
			@GenericInfo({ List.class, Integer.class }) List<List<Integer>> nestedList,
			@GenericInfo({ String.class, List.class, String.class }) Map<String, List<String>> nestedMap,
			@GenericInfo({ List.class, String.class }) Optional<List<String>> optionalList
		) {}
		
		Codec<NestedGenericRecord> codec = CodecBuilder.autoMapCodec(NestedGenericRecord.class);
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		List<List<Integer>> nestedList = List.of(List.of(1, 2), List.of(3, 4));
		Map<String, List<String>> nestedMap = Map.of(
			"group1", List.of("a", "b"),
			"group2", List.of("c", "d")
		);
		Optional<List<String>> optionalList = Optional.of(List.of("x", "y"));
		
		NestedGenericRecord original = new NestedGenericRecord(nestedList, nestedMap, optionalList);
		JsonElement encoded = codec.encode(provider, original);
		assertInstanceOf(JsonObject.class, encoded);
		
		NestedGenericRecord decoded = codec.decode(provider, encoded);
		assertEquals(2, decoded.nestedList().size());
		assertEquals(2, decoded.nestedList().getFirst().size());
		assertEquals(1, decoded.nestedList().getFirst().getFirst());
		
		assertEquals(2, decoded.nestedMap().size());
		assertEquals(2, decoded.nestedMap().get("group1").size());
		assertEquals("a", decoded.nestedMap().get("group1").getFirst());
		
		assertTrue(decoded.optionalList().isPresent());
		assertEquals(2, decoded.optionalList().get().size());
		assertEquals("x", decoded.optionalList().get().getFirst());
	}
	
	@Test
	void complexNestedStructures() {
		record Address(String street, String city, TestEnum country, int zipCode) {}
		
		record Person(
			String name,
			int age,
			double height,
			boolean active,
			Address[] addresses,
			@GenericInfo(String.class) List<String> hobbies,
			@GenericInfo({ String.class, Integer.class }) Map<String, Integer> scores,
			@GenericInfo({ List.class, List.class, String.class, Integer.class }) Either<List<String>, List<Integer>> contact,
			@GenericInfo(String.class) Optional<String> nickname
		) {}
		
		Codec<Person> codec = CodecBuilder.autoMapCodec(Person.class);
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		Person original = new Person(
			"John Doe",
			35,
			1.80,
			true,
			new Address[] {
				new Address("Main St 1", "Springfield", TestEnum.FIRST, 12345),
				new Address("Elm St 2", "Shelbyville", TestEnum.SECOND, 67890)
			},
			List.of("Reading", "Gaming"),
			Map.of("Math", 95, "Science", 88),
			Either.left(List.of("John", "Jane")),
			Optional.of("Johnny")
		);
		
		JsonElement encoded = codec.encode(provider, original);
		JsonObject jsonObject = assertInstanceOf(JsonObject.class, encoded);
		
		assertEquals("John Doe", jsonObject.get("name").getAsJsonPrimitive().getAsString());
		assertEquals(35, jsonObject.get("age").getAsJsonPrimitive().getAsInteger());
		assertEquals(1.80, jsonObject.get("height").getAsJsonPrimitive().getAsDouble(), 0.001);
		assertTrue(jsonObject.get("active").getAsJsonPrimitive().getAsBoolean());
		
		JsonArray addressesArray = jsonObject.get("addresses").getAsJsonArray();
		assertEquals(2, addressesArray.size());
		assertEquals("Main St 1", addressesArray.get(0).getAsJsonObject().get("street").getAsJsonPrimitive().getAsString());
		
		JsonArray hobbiesArray = jsonObject.get("hobbies").getAsJsonArray();
		assertEquals(2, hobbiesArray.size());
		assertEquals("Reading", hobbiesArray.get(0).getAsJsonPrimitive().getAsString());
		
		JsonArray contactArray = jsonObject.get("contact").getAsJsonArray();
		assertEquals(2, contactArray.size());
		assertEquals("John", contactArray.get(0).getAsJsonPrimitive().getAsString());
		
		assertEquals("Johnny", jsonObject.get("nickname").getAsJsonPrimitive().getAsString());
		
		Person decoded = codec.decode(provider, encoded);
		assertEquals(original.name(), decoded.name());
		assertEquals(original.age(), decoded.age());
		assertEquals(original.height(), decoded.height(), 0.001);
		assertEquals(original.active(), decoded.active());
		
		assertEquals(original.addresses().length, decoded.addresses().length);
		for (int i = 0; i < original.addresses().length; i++) {
			assertEquals(original.addresses()[i], decoded.addresses()[i]);
		}
		
		assertEquals(original.hobbies(), decoded.hobbies());
		assertEquals(original.scores(), decoded.scores());
		assertEquals(original.contact(), decoded.contact());
		assertEquals(original.nickname(), decoded.nickname());
	}
	
	@Test
	void missingGenericInfoThrowsException() {
		record MissingGenericInfo(@NotNull List<String> list) {}
		
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> CodecBuilder.autoMapCodec(MissingGenericInfo.class));
		assertTrue(exception.getMessage().contains("Missing generic type information"));
	}
	
	@Test
	void tooManyFieldsThrowsException() {
		record TooManyFieldsRecord(
			int f1, int f2, int f3, int f4, int f5, int f6, int f7, int f8,
			int f9, int f10, int f11, int f12, int f13, int f14, int f15, int f16, int f17
		) {}
		
		assertThrows(IllegalArgumentException.class, () -> CodecBuilder.autoMapCodec(TooManyFieldsRecord.class));
	}
	
	@Test
	void arrayTypesAreSupported() {
		record ArrayRecord(String[] strings, int[] numbers) {}
		
		Codec<ArrayRecord> codec = CodecBuilder.autoMapCodec(ArrayRecord.class);
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		ArrayRecord original = new ArrayRecord(new String[] { "a", "b" }, new int[] { 1, 2 });
		JsonElement encoded = codec.encode(provider, original);
		ArrayRecord decoded = codec.decode(provider, encoded);
		
		assertArrayEquals(original.strings(), decoded.strings());
		assertArrayEquals(original.numbers(), decoded.numbers());
	}
	
	//region Test Classes
	private enum TestEnum {
		FIRST, SECOND, THIRD
	}
	
	private interface TestInterface {}
	
	private @interface TestAnnotation {}
	
	private static final class AnnotatedFieldsClass {
		
		@CodecField
		private final String name;
		@CodecField
		private final int age;
		private final String ignored;
		
		@CodecConstructor
		private AnnotatedFieldsClass(String name, int age) {
			this.name = name;
			this.age = age;
			this.ignored = "ignored";
		}
	}
	
	private static final class ImplicitFieldsClass {
		
		private final String name;
		private final int age;
		private transient final String transientField = "transient";
		
		private ImplicitFieldsClass(@NotNull String name, int age) {
			this.name = name;
			this.age = age;
		}
	}
	
	private static class EmptyClass {}
	
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
	//endregion
}
