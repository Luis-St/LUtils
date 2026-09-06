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

package net.luis.utils.io.codec;

import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.BinaryTypeProvider;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.binary.BinaryStruct;
import net.luis.utils.io.data.json.*;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link FieldCodec}.<br>
 *
 * @author Luis-St
 */
class FieldCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		TestObject obj = new TestObject("test");
		
		assertThrows(NullPointerException.class, () -> fieldCodec.encode(null, typeProvider.empty(), obj));
		assertThrows(NullPointerException.class, () -> fieldCodec.encode(typeProvider, null, obj));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		
		EncoderException exception = assertThrows(EncoderException.class, () -> fieldCodec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode component because the component can not be retrieved from a null object"));
	}
	
	@Test
	void encodeWithValidObject() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		TestObject obj = new TestObject("John");
		
		JsonObject current = new JsonObject();
		JsonElement result = fieldCodec.encode(typeProvider, current, obj);
		
		JsonObject expected = new JsonObject();
		expected.add("name", new JsonPrimitive("John"));
		assertEquals(expected, result);
	}
	
	@Test
	void encodeWithMultipleFields() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObjectWithAge> nameCodec = STRING.fieldOf("name", TestObjectWithAge::name);
		FieldCodec<Integer, TestObjectWithAge> ageCodec = INTEGER.fieldOf("age", TestObjectWithAge::age);
		TestObjectWithAge obj = new TestObjectWithAge("John", 25);
		
		JsonObject current = new JsonObject();
		nameCodec.encode(typeProvider, current, obj);
		ageCodec.encode(typeProvider, current, obj);
		
		JsonObject expected = new JsonObject();
		expected.add("name", new JsonPrimitive("John"));
		expected.add("age", new JsonPrimitive(25));
		assertEquals(expected, current);
	}
	
	@Test
	void encodeWithNullFieldValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObjectNullable> fieldCodec = STRING.fieldOf("name", TestObjectNullable::name);
		TestObjectNullable obj = new TestObjectNullable(null);
		
		EncoderException exception = assertThrows(EncoderException.class, () -> fieldCodec.encode(typeProvider, typeProvider.empty(), obj));
		assertTrue(exception.getMessage().contains("Unable to encode named 'name'"));
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		
		JsonObject obj = new JsonObject();
		obj.add("name", new JsonPrimitive("John"));
		
		assertThrows(NullPointerException.class, () -> fieldCodec.decode(null, typeProvider.empty(), obj));
		assertThrows(NullPointerException.class, () -> fieldCodec.decode(typeProvider, null, obj));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		
		DecoderException exception = assertThrows(DecoderException.class, () -> fieldCodec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode named 'name' null value"));
	}
	
	@Test
	void decodeWithValidObject() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		
		JsonObject obj = new JsonObject();
		obj.add("name", new JsonPrimitive("John"));
		
		String result = fieldCodec.decode(typeProvider, typeProvider.empty(), obj);
		assertEquals("John", result);
	}
	
	@Test
	void decodeWithMissingField() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		
		JsonObject obj = new JsonObject();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> fieldCodec.decode(typeProvider, typeProvider.empty(), obj));
		assertTrue(exception.getMessage().contains("Name 'name' not found"));
	}
	
	@Test
	void decodeWithAlias() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", "username", TestObject::name);
		
		JsonObject obj = new JsonObject();
		obj.add("username", new JsonPrimitive("John"));
		
		String result = fieldCodec.decode(typeProvider, typeProvider.empty(), obj);
		assertEquals("John", result);
	}
	
	@Test
	void decodeWithMultipleAliases() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", Set.of("username", "user", "displayName"), TestObject::name);
		
		JsonObject obj1 = new JsonObject();
		obj1.add("username", new JsonPrimitive("John"));
		assertEquals("John", fieldCodec.decode(typeProvider, typeProvider.empty(), obj1));
		
		JsonObject obj2 = new JsonObject();
		obj2.add("user", new JsonPrimitive("Jane"));
		assertEquals("Jane", fieldCodec.decode(typeProvider, typeProvider.empty(), obj2));
		
		JsonObject obj3 = new JsonObject();
		obj3.add("displayName", new JsonPrimitive("Bob"));
		assertEquals("Bob", fieldCodec.decode(typeProvider, typeProvider.empty(), obj3));
	}
	
	@Test
	void decodeWithNoMatchingAlias() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", Set.of("username", "user"), TestObject::name);
		
		JsonObject obj = new JsonObject();
		obj.add("other", new JsonPrimitive("John"));
		
		DecoderException exception = assertThrows(DecoderException.class, () -> fieldCodec.decode(typeProvider, typeProvider.empty(), obj));
		assertTrue(exception.getMessage().contains("Name and aliases 'name'"));
		assertTrue(exception.getMessage().contains("not found"));
	}
	
	@Test
	void decodePrefersPrimaryName() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", "username", TestObject::name);
		
		JsonObject obj = new JsonObject();
		obj.add("name", new JsonPrimitive("John"));
		obj.add("username", new JsonPrimitive("Jane"));
		
		String result = fieldCodec.decode(typeProvider, typeProvider.empty(), obj);
		assertEquals("John", result);
	}
	
	@Test
	void equalsAndHashCode() {
		FieldCodec<String, TestObject> codec1 = STRING.fieldOf("name", TestObject::name);
		FieldCodec<String, TestObject> codec2 = STRING.fieldOf("name", TestObject::name);
		FieldCodec<String, TestObject> codec3 = STRING.fieldOf("other", TestObject::name);
		FieldCodec<String, TestObject> codec4 = STRING.fieldOf("name", "alias", TestObject::name);
		
		assertEquals(codec1, codec2);
		
		assertNotEquals(codec1, codec3);
		assertNotEquals(codec1, codec4);
	}
	
	@Test
	void toStringRepresentation() {
		FieldCodec<String, TestObject> codec1 = STRING.fieldOf("name", TestObject::name);
		String result1 = codec1.toString();
		assertTrue(result1.startsWith("NamedCodec['name'"));
		assertTrue(result1.contains("StringCodec"));
		
		FieldCodec<String, TestObject> codec2 = STRING.fieldOf("name", Set.of("username", "user"), TestObject::name);
		String result2 = codec2.toString();
		assertTrue(result2.startsWith("NamedCodec['name'"));
		assertTrue(result2.contains("[username, user]") || result2.contains("[user, username]"));
		assertTrue(result2.contains("StringCodec"));
	}
	
	@Test
	void constructWithFieldRef() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = new FieldCodec<>(STRING, new FieldRef("name", Set.of("alias"), 2), TestObject::name);
		
		JsonObject current = new JsonObject();
		fieldCodec.encode(typeProvider, current, new TestObject("John"));
		assertEquals(new JsonPrimitive("John"), current.get("name"));
		
		assertEquals("John", fieldCodec.decode(typeProvider, typeProvider.empty(), current));
		
		String result = fieldCodec.toString();
		assertTrue(result.contains("'name'"));
		assertTrue(result.contains("alias"));
	}
	
	@Test
	void constructWithNullFieldRef() {
		assertThrows(NullPointerException.class, () -> new FieldCodec<>(STRING, null, TestObject::name));
	}
	
	@Test
	void constructWithNullCodec() {
		assertThrows(NullPointerException.class, () -> new FieldCodec<String, TestObject>(null, "name", Set.of(), TestObject::name));
		assertThrows(NullPointerException.class, () -> new FieldCodec<String, TestObject>(null, new FieldRef("name"), TestObject::name));
	}
	
	@Test
	void constructWithNullGetter() {
		assertThrows(NullPointerException.class, () -> new FieldCodec<String, TestObject>(STRING, "name", Set.of(), null));
		assertThrows(NullPointerException.class, () -> new FieldCodec<String, TestObject>(STRING, new FieldRef("name"), null));
	}
	
	@Test
	void constructWithNullAliases() {
		assertThrows(NullPointerException.class, () -> new FieldCodec<String, TestObject>(STRING, "name", null, TestObject::name));
	}
	
	@Test
	void withIndexWithNegativeIndex() {
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> fieldCodec.withIndex(-1));
		assertTrue(exception.getMessage().contains("-1"));
	}
	
	@Test
	void decodeWithFailingInnerCodecKeepsCauseMessage() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<Integer, TestObjectWithAge> fieldCodec = INTEGER.fieldOf("value", TestObjectWithAge::age);
		
		JsonObject obj = new JsonObject();
		obj.add("value", new JsonPrimitive("not-a-number"));
		
		DecoderException exception = assertThrows(DecoderException.class, () -> fieldCodec.decode(typeProvider, typeProvider.empty(), obj));
		assertTrue(exception.getMessage().startsWith("Unable to decode named 'value' from '"));
		assertNotNull(exception.getCause());
		assertInstanceOf(DecoderException.class, exception.getCause());
	}
	
	@Test
	void decodeWithMissingFieldReportsNotFound() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		
		JsonObject obj = new JsonObject();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> fieldCodec.decode(typeProvider, typeProvider.empty(), obj));
		assertTrue(exception.getMessage().startsWith("Name 'name' not found"));
		assertNotNull(exception.getCause());
		assertInstanceOf(DecoderException.class, exception.getCause());
	}
	
	@Test
	void encodeErrorMessageIncludesAliases() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObjectNullable> fieldCodec = STRING.fieldOf("name", Set.of("username"), TestObjectNullable::name);
		
		EncoderException exception = assertThrows(EncoderException.class, () -> fieldCodec.encode(typeProvider, typeProvider.empty(), new TestObjectNullable(null)));
		assertTrue(exception.getMessage().contains("'name', ["));
		assertTrue(exception.getMessage().contains("username"));
	}
	
	@Test
	void decodeErrorMessageIncludesCauseText() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<Integer, TestObjectWithAge> fieldCodec = INTEGER.fieldOf("value", TestObjectWithAge::age);
		
		JsonObject obj = new JsonObject();
		obj.add("value", new JsonPrimitive("not-a-number"));
		
		DecoderException exception = assertThrows(DecoderException.class, () -> fieldCodec.decode(typeProvider, typeProvider.empty(), obj));
		assertTrue(exception.getMessage().length() > "Unable to decode named 'value' from ''".length());
		assertTrue(exception.getMessage().contains(":"));
	}
	
	@Test
	void withIndexAssignsIndex() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		FieldCodec<String, TestObject> indexed = fieldCodec.withIndex(0);
		
		assertNotSame(fieldCodec, indexed);
		
		JsonObject current = new JsonObject();
		indexed.encode(typeProvider, current, new TestObject("John"));
		assertEquals(new JsonPrimitive("John"), current.get("name"));
		assertEquals("John", indexed.decode(typeProvider, typeProvider.empty(), current));
	}
	
	@Test
	void withIndexWithZeroIndex() {
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		
		assertDoesNotThrow(() -> fieldCodec.withIndex(0));
	}
	
	@Test
	void withIndexKeepsNameAndAliases() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> indexed = STRING.fieldOf("name", "username", TestObject::name).withIndex(1);
		
		String result = indexed.toString();
		assertTrue(result.contains("'name'"));
		assertTrue(result.contains("username"));
		
		JsonObject obj = new JsonObject();
		obj.add("username", new JsonPrimitive("John"));
		assertEquals("John", indexed.decode(typeProvider, typeProvider.empty(), obj));
	}
	
	@Test
	void withIndexDoesNotAffectEquality() {
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		
		assertEquals(fieldCodec, fieldCodec.withIndex(3));
		assertEquals(fieldCodec.hashCode(), fieldCodec.withIndex(3).hashCode());
	}
	
	@Test
	void encodeWithEmptyEncodedValueSkipsField() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<Optional<String>, TestObjectOptional> fieldCodec = STRING.optional().fieldOf("name", TestObjectOptional::name);
		
		JsonObject map = new JsonObject();
		JsonElement result = assertDoesNotThrow(() -> fieldCodec.encode(typeProvider, map, new TestObjectOptional(Optional.empty())));
		
		assertNotSame(map, result);
		assertEquals(typeProvider.empty(), result);
		assertFalse(map.containsKey("name"));
		assertTrue(map.isEmpty());
	}
	
	@Test
	void encodeWithNonEmptyEncodedValueSetsField() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		
		JsonObject map = new JsonObject();
		JsonElement result = fieldCodec.encode(typeProvider, map, new TestObject("John"));
		
		assertSame(map, result);
		assertTrue(map.containsKey("name"));
		assertEquals(new JsonPrimitive("John"), map.get("name"));
	}
	
	@Test
	void formatFieldWithoutAliases() {
		String result = STRING.fieldOf("name", TestObject::name).toString();
		
		assertTrue(result.startsWith("NamedCodec['name'"));
		assertFalse(result.contains("'name', ["));
	}
	
	@Test
	void formatFieldWithAliases() {
		String result = STRING.fieldOf("name", Set.of("username"), TestObject::name).toString();
		
		assertTrue(result.contains("'name', ["));
		assertTrue(result.contains("username"));
	}
	
	@Test
	void notFoundMessageWithoutAliases() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		
		DecoderException exception = assertThrows(DecoderException.class, () -> fieldCodec.decode(typeProvider, typeProvider.empty(), new JsonObject()));
		assertTrue(exception.getMessage().contains("no aliases configured"));
	}
	
	@Test
	void notFoundMessageWithAliases() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", Set.of("username"), TestObject::name);
		
		DecoderException exception = assertThrows(DecoderException.class, () -> fieldCodec.decode(typeProvider, typeProvider.empty(), new JsonObject()));
		assertTrue(exception.getMessage().contains("Name and aliases"));
		assertFalse(exception.getMessage().contains("no aliases configured"));
	}
	
	@Test
	void encodeUsesSetFieldOnProvider() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", Set.of("username", "user"), TestObject::name);
		
		JsonObject current = new JsonObject();
		fieldCodec.encode(typeProvider, current, new TestObject("John"));
		
		assertEquals(1, current.size());
		assertTrue(current.containsKey("name"));
		assertFalse(current.containsKey("username"));
		assertFalse(current.containsKey("user"));
	}
	
	@Test
	void decodeWithIndexedFieldOnMapProvider() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> indexed = STRING.fieldOf("name", TestObject::name).withIndex(1);
		
		JsonObject obj = new JsonObject();
		obj.add("name", new JsonPrimitive("John"));
		
		assertEquals("John", indexed.decode(typeProvider, typeProvider.empty(), obj));
	}
	
	@Test
	void toStringWithIndexedField() {
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		
		assertEquals(fieldCodec.toString(), fieldCodec.withIndex(2).toString());
	}
	
	@Test
	void indexedFieldCodecEncodesIntoBinaryStruct() throws Exception {
		BinaryTypeProvider typeProvider = BinaryTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> indexed = STRING.fieldOf("name", TestObject::name).withIndex(1);
		
		BinaryStruct struct = new BinaryStruct(3);
		indexed.encode(typeProvider, struct, new TestObject("John"));
		
		assertEquals("John", struct.getAsString(1));
		assertFalse(struct.has(0));
		assertFalse(struct.has(2));
	}
	
	@Test
	void unindexedFieldCodecFailsOnBinaryStruct() {
		BinaryTypeProvider typeProvider = BinaryTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		BinaryStruct struct = new BinaryStruct(3);
		
		EncoderException exception = assertThrows(EncoderException.class, () -> fieldCodec.encode(typeProvider, struct, new TestObject("John")));
		assertTrue(exception.getMessage().contains("not indexed"));
	}
	
	@Test
	void indexedFieldCodecDecodesFromBinaryStruct() throws Exception {
		BinaryTypeProvider typeProvider = BinaryTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> indexed = STRING.fieldOf("name", TestObject::name).withIndex(1);
		
		BinaryStruct struct = new BinaryStruct(3);
		struct.set(1, "John");
		
		assertEquals("John", indexed.decode(typeProvider, struct, struct));
	}
	
	@Test
	void aliasResolutionStillWorksAfterRefactor() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", Set.of("username", "user", "displayName"), TestObject::name);
		
		for (String alias : Set.of("username", "user", "displayName")) {
			JsonObject obj = new JsonObject();
			obj.add(alias, new JsonPrimitive("John"));
			assertEquals("John", fieldCodec.decode(typeProvider, typeProvider.empty(), obj));
		}
		
		JsonObject both = new JsonObject();
		both.add("name", new JsonPrimitive("Primary"));
		both.add("username", new JsonPrimitive("Alias"));
		assertEquals("Primary", fieldCodec.decode(typeProvider, typeProvider.empty(), both));
	}
	
	private record TestObject(@NonNull String name) {}
	
	private record TestObjectWithAge(@NonNull String name, int age) {}
	
	private record TestObjectNullable(String name) {}
	
	private record TestObjectOptional(@NonNull Optional<String> name) {}
}
