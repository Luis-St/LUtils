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
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.codec.types.struct.*;
import net.luis.utils.io.codec.types.struct.collection.*;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Codec}.<br>
 *
 * @author Luis-St
 */
class CodecTest {
	
	@Test
	void typed() throws Exception {
		Codec<String> codec = STRING.typed(String.class);
		assertEquals(String.class, codec.getType());
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		assertEquals(new JsonPrimitive("test"), codec.encode(provider, "test"));
		assertEquals("test", codec.decode(provider, new JsonPrimitive("test")));
	}
	
	@Test
	void keyable() throws Exception {
		Codec<Integer> keyableCodec = INTEGER.keyable(
			String::valueOf,
			key -> {
				try {
					return Integer.parseInt(key);
				} catch (NumberFormatException e) {
					throw new DecoderException("Invalid integer: " + key);
				}
			}
		);
		
		assertEquals("42", keyableCodec.encodeKey(42));
		assertEquals(42, keyableCodec.decodeKey("42"));
		assertThrows(DecoderException.class, () -> keyableCodec.decodeKey("invalid"));
	}
	
	@Test
	void nullable() throws Exception {
		Codec<String> nullableCodec = STRING.nullable();
		assertInstanceOf(NullableCodec.class, nullableCodec);
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		assertEquals(new JsonPrimitive("test"), nullableCodec.encode(provider, "test"));
		assertEquals("test", nullableCodec.decode(provider, new JsonPrimitive("test")));
		
		assertTrue(nullableCodec.encode(provider, null).isJsonNull());
		assertNull(nullableCodec.decode(provider, JsonNull.INSTANCE));
	}
	
	@Test
	void union() {
		assertThrows(NullPointerException.class, () -> STRING.union((Collection<String>) null));
		assertThrows(NullPointerException.class, () -> STRING.union((String[]) null));
		assertThrows(IllegalArgumentException.class, () -> STRING.union(List.of()));
		
		Codec<String> unionCodec = STRING.union("a", "b", "c");
		assertInstanceOf(UnionCodec.class, unionCodec);
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		assertDoesNotThrow(() -> unionCodec.encode(provider, provider.empty(), "a"));
		assertThrows(EncoderException.class, () -> unionCodec.encode(provider, provider.empty(), "d"));
		
		assertDoesNotThrow(() -> unionCodec.decode(provider, provider.empty(), new JsonPrimitive("b")));
		assertThrows(DecoderException.class, () -> unionCodec.decode(provider, provider.empty(), new JsonPrimitive("invalid")));
	}
	
	@Test
	void optional() throws Exception {
		assertInstanceOf(OptionalCodec.class, INTEGER.optional());
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> optionalCodec = INTEGER.optional();
		
		JsonElement encodedPresent = optionalCodec.encode(provider, Optional.of(42));
		assertEquals(new JsonPrimitive(42), encodedPresent);
		Optional<Integer> decodedPresent = optionalCodec.decode(provider, encodedPresent);
		assertTrue(decodedPresent.isPresent());
		assertEquals(42, decodedPresent.get());
		
		JsonElement encodedEmpty = optionalCodec.encode(provider, Optional.empty());
		assertFalse(encodedEmpty.isJsonNull());
		assertFalse(encodedEmpty.isJsonPrimitive());
		assertFalse(encodedEmpty.isJsonArray());
		assertFalse(encodedEmpty.isJsonObject());
		Optional<Integer> decodedEmpty = optionalCodec.decode(provider, encodedEmpty);
		assertTrue(decodedEmpty.isEmpty());
	}
	
	@Test
	void optionalWithDefaultValue() throws Exception {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> optionalCodec = INTEGER.optional(100);
		
		JsonElement encodedPresent = optionalCodec.encode(provider, Optional.of(42));
		assertEquals(new JsonPrimitive(42), encodedPresent);
		
		Optional<Integer> decodedPresent = optionalCodec.decode(provider, encodedPresent);
		assertTrue(decodedPresent.isPresent());
		assertEquals(42, decodedPresent.get());
	}
	
	@Test
	void optionalWithSupplier() throws Exception {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> optionalCodec = INTEGER.optional(() -> 100);
		
		JsonElement encodedPresent = optionalCodec.encode(provider, Optional.of(42));
		assertEquals(new JsonPrimitive(42), encodedPresent);
		
		Optional<Integer> decodedPresent = optionalCodec.decode(provider, encodedPresent);
		assertTrue(decodedPresent.isPresent());
		assertEquals(42, decodedPresent.get());
	}
	
	@Test
	void array() throws Exception {
		Codec<String[]> arrayCodec = STRING.array();
		assertInstanceOf(ArrayCodec.class, arrayCodec);
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		JsonElement encoded = arrayCodec.encode(provider, new String[] { "a", "b", "c" });
		assertEquals(new JsonArray(List.of(new JsonPrimitive("a"), new JsonPrimitive("b"), new JsonPrimitive("c"))), encoded);
		
		String[] decoded = arrayCodec.decode(provider, encoded);
		assertArrayEquals(new String[] { "a", "b", "c" }, decoded);
	}
	
	@Test
	void list() throws Exception {
		Codec<List<String>> listCodec = STRING.list();
		assertInstanceOf(ListCodec.class, listCodec);
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		JsonElement encoded = listCodec.encode(provider, List.of("a", "b", "c"));
		assertEquals(new JsonArray(List.of(new JsonPrimitive("a"), new JsonPrimitive("b"), new JsonPrimitive("c"))), encoded);
		
		List<String> decoded = listCodec.decode(provider, encoded);
		assertEquals(List.of("a", "b", "c"), decoded);
	}
	
	@Test
	void set() throws Exception {
		Codec<Set<String>> setCodec = STRING.set();
		assertInstanceOf(SetCodec.class, setCodec);
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		JsonElement encoded = setCodec.encode(provider, Set.of("a", "b", "c"));
		assertTrue(encoded.isJsonArray());
		assertEquals(3, encoded.getAsJsonArray().size());
		
		Set<String> decoded = setCodec.decode(provider, encoded);
		assertEquals(Set.of("a", "b", "c"), decoded);
	}
	
	@Test
	void stream() throws Exception {
		Codec<Stream<String>> streamCodec = STRING.stream();
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		JsonElement encoded = streamCodec.encode(provider, Stream.of("a", "b", "c"));
		assertEquals(new JsonArray(List.of(new JsonPrimitive("a"), new JsonPrimitive("b"), new JsonPrimitive("c"))), encoded);
		
		Stream<String> decoded = streamCodec.decode(provider, encoded);
		assertEquals(List.of("a", "b", "c"), decoded.toList());
	}
	
	@Test
	void withFallback() {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		Codec<String> primaryCodec = STRING.union("a", "b");
		Codec<String> fallbackCodec = STRING.union("c", "d");
		Codec<String> combinedCodec = primaryCodec.withFallback(fallbackCodec);
		
		assertDoesNotThrow(() -> combinedCodec.encode(provider, provider.empty(), "a"));
		assertDoesNotThrow(() -> combinedCodec.encode(provider, provider.empty(), "c"));
		assertThrows(EncoderException.class, () -> combinedCodec.encode(provider, provider.empty(), "invalid"));
		
		assertDoesNotThrow(() -> combinedCodec.decode(provider, provider.empty(), new JsonPrimitive("b")));
		assertDoesNotThrow(() -> combinedCodec.decode(provider, provider.empty(), new JsonPrimitive("d")));
		assertThrows(DecoderException.class, () -> combinedCodec.decode(provider, provider.empty(), new JsonPrimitive("invalid")));
	}
	
	@Test
	void xmap() throws Exception {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		Codec<Integer> lengthCodec = STRING.xmap(String::valueOf, String::length);
		
		assertEquals(new JsonPrimitive("42"), lengthCodec.encode(provider, 42));
		assertEquals(4, lengthCodec.decode(provider, new JsonPrimitive("test")));
	}
	
	@Test
	void mapFlat() throws Exception {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		Codec<Integer> parseCodec = STRING.xmap(
			String::valueOf,
			value -> {
				try {
					return Integer.parseInt(value);
				} catch (NumberFormatException e) {
					throw new DecoderException("Not a valid integer: " + value);
				}
			}
		);
		
		assertEquals(new JsonPrimitive("42"), parseCodec.encode(provider, 42));
		assertEquals(42, parseCodec.decode(provider, new JsonPrimitive("42")));
		assertThrows(DecoderException.class, () -> parseCodec.decode(provider, new JsonPrimitive("not-a-number")));
	}
	
	@Test
	void validate() {
		assertThrows(NullPointerException.class, () -> INTEGER.validate(null));
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		Codec<Integer> positiveCodec = INTEGER.validate(value -> {
			if (value > 0) {
				return Result.success(value);
			}
			return Result.error("Value must be positive, got: " + value);
		});
		
		assertDoesNotThrow(() -> positiveCodec.encode(provider, provider.empty(), 42));
		assertThrows(EncoderException.class, () -> positiveCodec.encode(provider, provider.empty(), -1));
		
		assertDoesNotThrow(() -> positiveCodec.decode(provider, provider.empty(), new JsonPrimitive(42)));
		assertThrows(DecoderException.class, () -> positiveCodec.decode(provider, provider.empty(), new JsonPrimitive(-1)));
	}
	
	@Test
	void orElse() throws Exception {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		Codec<String> positiveOnlyCodec = STRING.union("positive").orElse("default");
		
		assertEquals("default", positiveOnlyCodec.decode(provider, new JsonPrimitive("negative")));
		assertEquals("positive", positiveOnlyCodec.decode(provider, new JsonPrimitive("positive")));
	}
	
	@Test
	void orElseGet() throws Exception {
		assertThrows(NullPointerException.class, () -> STRING.orElseGet(null));
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		Codec<String> positiveOnlyCodec = STRING.union("positive").orElseGet(() -> "default");
		
		assertEquals("default", positiveOnlyCodec.decode(provider, new JsonPrimitive("negative")));
		assertEquals("positive", positiveOnlyCodec.decode(provider, new JsonPrimitive("positive")));
	}
	
	@Test
	void fieldOf() throws Exception {
		assertThrows(NullPointerException.class, () -> STRING.fieldOf(null, TestObject::name));
		assertThrows(NullPointerException.class, () -> STRING.fieldOf("name", (Function<TestObject, String>) null));
		
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		JsonObject current = new JsonObject();
		JsonElement result = fieldCodec.encode(provider, current, new TestObject("John"));
		
		JsonObject expected = new JsonObject();
		expected.add("name", new JsonPrimitive("John"));
		assertEquals(expected, result);
		
		String decoded = fieldCodec.decode(provider, expected, expected);
		assertEquals("John", decoded);
	}
	
	@Test
	void fieldOfWithAlias() throws Exception {
		assertThrows(NullPointerException.class, () -> STRING.fieldOf("name", (String) null, TestObject::name));
		
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", "alias", TestObject::name);
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		JsonObject aliasJson = new JsonObject();
		aliasJson.add("alias", new JsonPrimitive("John"));
		
		String decoded = fieldCodec.decode(provider, aliasJson, aliasJson);
		assertEquals("John", decoded);
	}
	
	@Test
	void fieldOfWithAliases() throws Exception {
		assertThrows(NullPointerException.class, () -> STRING.fieldOf("name", (Set<String>) null, TestObject::name));
		
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", Set.of("alias1", "alias2"), TestObject::name);
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		JsonObject alias1Json = new JsonObject();
		alias1Json.add("alias1", new JsonPrimitive("John"));
		assertEquals("John", fieldCodec.decode(provider, alias1Json, alias1Json));
		
		JsonObject alias2Json = new JsonObject();
		alias2Json.add("alias2", new JsonPrimitive("Jane"));
		assertEquals("Jane", fieldCodec.decode(provider, alias2Json, alias2Json));
	}
	
	@Test
	void optionalFieldOf() throws Exception {
		FieldCodec<String, TestObject> fieldCodec = STRING.optionalFieldOf("name", "default", TestObject::name);
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		JsonObject emptyJson = new JsonObject();
		String decoded = fieldCodec.decode(provider, emptyJson, emptyJson);
		assertEquals("default", decoded);
		
		JsonObject presentJson = new JsonObject();
		presentJson.add("name", new JsonPrimitive("John"));
		String decodedPresent = fieldCodec.decode(provider, presentJson, presentJson);
		assertEquals("John", decodedPresent);
	}
	
	@Test
	void optionalFieldOfWithSupplier() throws Exception {
		FieldCodec<String, TestObject> fieldCodec = STRING.optionalFieldOf("name", () -> "default", TestObject::name);
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		JsonObject emptyJson = new JsonObject();
		String decoded = fieldCodec.decode(provider, emptyJson, emptyJson);
		assertEquals("default", decoded);
	}
	
	@Test
	void optionalFieldOfWithAlias() throws Exception {
		FieldCodec<String, TestObject> fieldCodec = STRING.optionalFieldOf("name", "alias", "default", TestObject::name);
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		JsonObject emptyJson = new JsonObject();
		String decodedDefault = fieldCodec.decode(provider, emptyJson, emptyJson);
		assertEquals("default", decodedDefault);
	}
	
	@Test
	void optionalFieldOfWithAliasAndSupplier() throws Exception {
		FieldCodec<String, TestObject> fieldCodec = STRING.optionalFieldOf("name", "alias", () -> "default", TestObject::name);
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		JsonObject emptyJson = new JsonObject();
		String decoded = fieldCodec.decode(provider, emptyJson, emptyJson);
		assertEquals("default", decoded);
	}
	
	@Test
	void optionalFieldOfWithAliases() throws Exception {
		FieldCodec<String, TestObject> fieldCodec = STRING.optionalFieldOf("name", Set.of("alias1", "alias2"), "default", TestObject::name);
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		JsonObject emptyJson = new JsonObject();
		String decodedDefault = fieldCodec.decode(provider, emptyJson, emptyJson);
		assertEquals("default", decodedDefault);
	}
	
	@Test
	void optionalFieldOfWithAliasesAndSupplier() throws Exception {
		FieldCodec<String, TestObject> fieldCodec = STRING.optionalFieldOf("name", Set.of("alias1", "alias2"), () -> "default", TestObject::name);
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		JsonObject emptyJson = new JsonObject();
		String decoded = fieldCodec.decode(provider, emptyJson, emptyJson);
		assertEquals("default", decoded);
	}
	
	private record TestObject(@NonNull String name) {}
}
