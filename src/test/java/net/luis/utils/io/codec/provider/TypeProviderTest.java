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

import net.luis.utils.io.codec.*;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.data.json.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the default methods of {@link TypeProvider}.<br>
 *
 * @author Luis-St
 */
class TypeProviderTest {
	
	private static final JsonTypeProvider PROVIDER = JsonTypeProvider.INSTANCE;
	private static final ToonTypeProvider SECOND_PROVIDER = ToonTypeProvider.INSTANCE;
	
	private static JsonObject objectWith(String key, String value) {
		JsonObject object = new JsonObject();
		object.add(key, new JsonPrimitive(value));
		return object;
	}
	
	@Test
	void createStructWithNullExceptionConstructor() {
		assertThrows(NullPointerException.class, () -> PROVIDER.createStruct(1, null));
	}
	
	@Test
	void createStructWithNegativeFieldCount() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> PROVIDER.createStruct(-1, DecoderException::new));
		
		assertTrue(exception.getMessage().contains("-1"));
	}
	
	@Test
	void validateStructWithNullExceptionConstructor() {
		JsonObject object = new JsonObject();
		
		assertThrows(NullPointerException.class, () -> PROVIDER.validateStruct(object, null));
	}
	
	@Test
	void validateStructWithNullValue() {
		assertThrows(DecoderException.class, () -> PROVIDER.validateStruct(null, DecoderException::new));
	}
	
	@Test
	void validateStructWithNonMapValue() {
		JsonPrimitive primitive = new JsonPrimitive("text");
		
		assertThrows(DecoderException.class, () -> PROVIDER.validateStruct(primitive, DecoderException::new));
	}
	
	@Test
	void setFieldWithNullExceptionConstructor() {
		JsonObject object = new JsonObject();
		FieldRef field = new FieldRef("name");
		
		assertThrows(NullPointerException.class, () -> PROVIDER.setField(object, field, new JsonPrimitive("value"), null));
	}
	
	@Test
	void setFieldWithNullField() {
		JsonObject object = new JsonObject();
		
		assertThrows(NullPointerException.class, () -> PROVIDER.setField(object, null, new JsonPrimitive("value"), EncoderException::new));
	}
	
	@Test
	void hasFieldWithNullFieldOrExceptionConstructor() {
		JsonObject object = new JsonObject();
		FieldRef field = new FieldRef("name");
		
		assertThrows(NullPointerException.class, () -> PROVIDER.hasField(object, null, DecoderException::new));
		assertThrows(NullPointerException.class, () -> PROVIDER.hasField(object, field, null));
	}
	
	@Test
	void getFieldWithNullFieldOrExceptionConstructor() {
		JsonObject object = new JsonObject();
		FieldRef field = new FieldRef("name");
		
		assertThrows(NullPointerException.class, () -> PROVIDER.getField(object, null, DecoderException::new));
		assertThrows(NullPointerException.class, () -> PROVIDER.getField(object, field, null));
	}
	
	@Test
	void setFieldOnNonMapValue() {
		JsonPrimitive primitive = new JsonPrimitive("text");
		FieldRef field = new FieldRef("name");
		
		assertThrows(EncoderException.class, () -> PROVIDER.setField(primitive, field, new JsonPrimitive("value"), EncoderException::new));
	}
	
	@Test
	void createStructWithZeroFieldCount() {
		JsonElement result = assertDoesNotThrow(() -> PROVIDER.createStruct(0, EncoderException::new));
		
		assertTrue(result.isJsonObject());
		assertTrue(result.getAsJsonObject().isEmpty());
	}
	
	@Test
	void createStructIgnoresFieldCount() throws Exception {
		JsonElement zero = PROVIDER.createStruct(0, EncoderException::new);
		JsonElement five = PROVIDER.createStruct(5, EncoderException::new);
		
		assertEquals(zero, five);
		
		PROVIDER.set(five, "key", new JsonPrimitive("value"), EncoderException::new);
		assertEquals(new JsonPrimitive("value"), five.getAsJsonObject().get("key"));
	}
	
	@Test
	void validateStructWithMapValue() {
		JsonObject object = new JsonObject();
		
		assertDoesNotThrow(() -> PROVIDER.validateStruct(object, DecoderException::new));
	}
	
	@Test
	void setFieldUsesFieldName() throws Exception {
		JsonObject object = new JsonObject();
		FieldRef field = new FieldRef("name", Set.of("alias"), 3);
		
		PROVIDER.setField(object, field, new JsonPrimitive("value"), EncoderException::new);
		
		assertEquals(1, object.size());
		assertTrue(object.containsKey("name"));
		assertFalse(object.containsKey("alias"));
		assertFalse(object.containsKey("3"));
	}
	
	@Test
	void hasFieldWithPresentName() throws Exception {
		JsonObject object = objectWith("name", "value");
		
		assertTrue(PROVIDER.hasField(object, new FieldRef("name", Set.of("alias")), DecoderException::new));
	}
	
	@Test
	void hasFieldWithoutAliases() throws Exception {
		JsonObject object = objectWith("other", "value");
		
		assertFalse(PROVIDER.hasField(object, new FieldRef("name"), DecoderException::new));
	}
	
	@Test
	void hasFieldWithPresentAlias() throws Exception {
		JsonObject object = objectWith("alias", "value");
		
		assertTrue(PROVIDER.hasField(object, new FieldRef("name", Set.of("alias")), DecoderException::new));
	}
	
	@Test
	void hasFieldWithNoMatchingAlias() throws Exception {
		JsonObject object = objectWith("other", "value");
		
		assertFalse(PROVIDER.hasField(object, new FieldRef("name", Set.of("a1", "a2")), DecoderException::new));
	}
	
	@Test
	void getFieldWithPresentName() throws Exception {
		JsonObject object = objectWith("name", "value");
		
		assertEquals(new JsonPrimitive("value"), PROVIDER.getField(object, new FieldRef("name"), DecoderException::new));
	}
	
	@Test
	void getFieldWithPresentAlias() throws Exception {
		JsonObject object = objectWith("alias", "value");
		
		assertEquals(new JsonPrimitive("value"), PROVIDER.getField(object, new FieldRef("name", Set.of("alias")), DecoderException::new));
	}
	
	@Test
	void getFieldWithMissingNameAndAliases() throws Exception {
		JsonObject object = objectWith("other", "value");
		
		assertNull(PROVIDER.getField(object, new FieldRef("name", Set.of("a1", "a2")), DecoderException::new));
	}
	
	@Test
	void getFieldWithoutAliases() throws Exception {
		JsonObject object = objectWith("other", "value");
		
		assertNull(PROVIDER.getField(object, new FieldRef("name"), DecoderException::new));
	}
	
	@Test
	void getFieldPrefersNameOverAlias() throws Exception {
		JsonObject object = new JsonObject();
		object.add("name", new JsonPrimitive("primary"));
		object.add("alias", new JsonPrimitive("secondary"));
		
		assertEquals(new JsonPrimitive("primary"), PROVIDER.getField(object, new FieldRef("name", Set.of("alias")), DecoderException::new));
	}
	
	@Test
	void createStructReturnsIndependentInstances() throws Exception {
		JsonElement first = PROVIDER.createStruct(2, EncoderException::new);
		JsonElement second = PROVIDER.createStruct(2, EncoderException::new);
		
		assertNotSame(first, second);
		
		PROVIDER.set(first, "key", new JsonPrimitive("value"), EncoderException::new);
		
		assertEquals(1, first.getAsJsonObject().size());
		assertEquals(0, second.getAsJsonObject().size());
	}
	
	@Test
	void setFieldOverwritesExistingValue() throws Exception {
		JsonObject object = new JsonObject();
		FieldRef field = new FieldRef("name");
		
		PROVIDER.setField(object, field, new JsonPrimitive("first"), EncoderException::new);
		PROVIDER.setField(object, field, new JsonPrimitive("second"), EncoderException::new);
		
		assertEquals(1, object.size());
		assertEquals(new JsonPrimitive("second"), object.get("name"));
	}
	
	@Test
	void hasFieldAndGetFieldAgreeOnPresence() throws Exception {
		FieldRef field = new FieldRef("name", Set.of("alias"));
		
		JsonObject byName = objectWith("name", "value");
		assertTrue(PROVIDER.hasField(byName, field, DecoderException::new));
		assertNotNull(PROVIDER.getField(byName, field, DecoderException::new));
		
		JsonObject byAlias = objectWith("alias", "value");
		assertTrue(PROVIDER.hasField(byAlias, field, DecoderException::new));
		assertNotNull(PROVIDER.getField(byAlias, field, DecoderException::new));
		
		JsonObject missing = objectWith("other", "value");
		assertFalse(PROVIDER.hasField(missing, field, DecoderException::new));
		assertNull(PROVIDER.getField(missing, field, DecoderException::new));
	}
	
	@Test
	void defaultsBehaveTheSameForASecondProvider() throws Exception {
		FieldRef field = new FieldRef("name", Set.of("alias"));
		
		Object struct = SECOND_PROVIDER.createStruct(1, EncoderException::new);
		assertNotNull(struct);
		assertDoesNotThrow(() -> SECOND_PROVIDER.validateStruct(SECOND_PROVIDER.createStruct(1, EncoderException::new), DecoderException::new));
		
		var byName = SECOND_PROVIDER.createMap(EncoderException::new);
		SECOND_PROVIDER.setField(byName, field, SECOND_PROVIDER.createString("value", EncoderException::new), EncoderException::new);
		assertTrue(SECOND_PROVIDER.hasField(byName, field, DecoderException::new));
		assertNotNull(SECOND_PROVIDER.getField(byName, field, DecoderException::new));
		
		var byAlias = SECOND_PROVIDER.createMap(EncoderException::new);
		SECOND_PROVIDER.set(byAlias, "alias", SECOND_PROVIDER.createString("value", EncoderException::new), EncoderException::new);
		assertTrue(SECOND_PROVIDER.hasField(byAlias, field, DecoderException::new));
		assertNotNull(SECOND_PROVIDER.getField(byAlias, field, DecoderException::new));
		
		var missing = SECOND_PROVIDER.createMap(EncoderException::new);
		assertFalse(SECOND_PROVIDER.hasField(missing, field, DecoderException::new));
		assertNull(SECOND_PROVIDER.getField(missing, field, DecoderException::new));
	}
	
	@Test
	void fieldWithMultipleAliasesResolvesFirstPresent() throws Exception {
		FieldRef field = new FieldRef("name", Set.of("a1", "a2", "a3"));
		JsonObject object = objectWith("a2", "value");
		
		assertTrue(PROVIDER.hasField(object, field, DecoderException::new));
		assertEquals(new JsonPrimitive("value"), PROVIDER.getField(object, field, DecoderException::new));
	}
	
	@Test
	void structRoundTripThroughDefaults() throws Exception {
		JsonElement struct = PROVIDER.createStruct(3, EncoderException::new);
		
		FieldRef first = new FieldRef("first");
		FieldRef second = new FieldRef("second");
		FieldRef third = new FieldRef("third");
		
		PROVIDER.setField(struct, first, new JsonPrimitive("a"), EncoderException::new);
		PROVIDER.setField(struct, second, new JsonPrimitive("b"), EncoderException::new);
		PROVIDER.setField(struct, third, new JsonPrimitive("c"), EncoderException::new);
		
		assertDoesNotThrow(() -> PROVIDER.validateStruct(struct, DecoderException::new));
		
		assertEquals(new JsonPrimitive("a"), PROVIDER.getField(struct, first, DecoderException::new));
		assertEquals(new JsonPrimitive("b"), PROVIDER.getField(struct, second, DecoderException::new));
		assertEquals(new JsonPrimitive("c"), PROVIDER.getField(struct, third, DecoderException::new));
	}
	
	@Test
	void defaultsUsedByCodecGroupEncoding() throws Exception {
		List<FieldCodec<?, TestObject>> codecs = List.of(
			STRING.fieldOf("name", TestObject::name),
			INTEGER.fieldOf("value", TestObject::value),
			BOOLEAN.fieldOf("flag", TestObject::flag)
		);
		CodecGroup<TestObject> group = new CodecGroup<>(codecs, components -> new TestObject((String) components.getFirst(), (Integer) components.get(1), (Boolean) components.get(2)));
		
		TestObject original = new TestObject("test", 42, true);
		JsonElement encoded = group.encode(PROVIDER, PROVIDER.empty(), original);
		
		assertTrue(encoded.isJsonObject());
		JsonObject object = encoded.getAsJsonObject();
		assertEquals(new JsonPrimitive("test"), object.get("name"));
		assertEquals(new JsonPrimitive(42), object.get("value"));
		assertEquals(new JsonPrimitive(true), object.get("flag"));
		
		assertEquals(original, group.decode(PROVIDER, encoded, encoded));
	}
	
	@Test
	void aliasDecodingStillWorksAfterFieldRefRefactor() throws Exception {
		FieldCodec<String, TestObject> codec = STRING.fieldOf("name", Set.of("username"), TestObject::name);
		
		JsonObject object = objectWith("username", "John");
		
		assertEquals("John", codec.decode(PROVIDER, PROVIDER.empty(), object));
	}
	
	private record TestObject(String name, int value, boolean flag) {}
}
