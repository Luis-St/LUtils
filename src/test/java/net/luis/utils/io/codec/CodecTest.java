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

import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.codec.types.struct.*;
import net.luis.utils.io.codec.types.struct.collection.*;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.result.Result;
import net.luis.utils.util.result.ResultingFunction;
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
	void typed() {
		Codec<String> codec = STRING.typed(String.class);
		assertEquals(String.class, codec.getType());
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		assertEquals(new JsonPrimitive("test"), codec.encode(provider, "test"));
		assertEquals("test", codec.decode(provider, new JsonPrimitive("test")));
	}
	
	@Test
	void keyable() {
		assertThrows(NullPointerException.class, () -> STRING.keyable((ResultingFunction<String, String>) null));
		assertThrows(NullPointerException.class, () -> STRING.keyable(null, ResultingFunction.identity()));
		assertThrows(NullPointerException.class, () -> STRING.keyable(ResultingFunction.identity(), null));
		
		Codec<Integer> keyableCodec = INTEGER.keyable(
			ResultingFunction.direct(String::valueOf),
			key -> {
				try {
					return Result.success(Integer.parseInt(key));
				} catch (NumberFormatException e) {
					return Result.error("Invalid integer: " + key);
				}
			}
		);
		
		assertEquals("42", keyableCodec.encodeKey(42).resultOrThrow());
		assertEquals(42, keyableCodec.decodeKey("42").resultOrThrow());
		assertTrue(keyableCodec.decodeKey("invalid").isError());
	}
	
	@Test
	void nullable() {
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
		
		assertTrue(unionCodec.encodeStart(provider, provider.empty(), "a").isSuccess());
		assertTrue(unionCodec.encodeStart(provider, provider.empty(), "d").isError());
		
		assertTrue(unionCodec.decodeStart(provider, provider.empty(), new JsonPrimitive("b")).isSuccess());
		assertTrue(unionCodec.decodeStart(provider, provider.empty(), new JsonPrimitive("invalid")).isError());
	}
	
	@Test
	void optional() {
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
	void optionalWithDefaultValue() {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> optionalCodec = INTEGER.optional(100);
		
		JsonElement encodedPresent = optionalCodec.encode(provider, Optional.of(42));
		assertEquals(new JsonPrimitive(42), encodedPresent);
		
		Optional<Integer> decodedPresent = optionalCodec.decode(provider, encodedPresent);
		assertTrue(decodedPresent.isPresent());
		assertEquals(42, decodedPresent.get());
	}
	
	@Test
	void optionalWithSupplier() {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> optionalCodec = INTEGER.optional(() -> 100);
		
		JsonElement encodedPresent = optionalCodec.encode(provider, Optional.of(42));
		assertEquals(new JsonPrimitive(42), encodedPresent);
		
		Optional<Integer> decodedPresent = optionalCodec.decode(provider, encodedPresent);
		assertTrue(decodedPresent.isPresent());
		assertEquals(42, decodedPresent.get());
	}
	
	@Test
	void array() {
		Codec<String[]> arrayCodec = STRING.array();
		assertInstanceOf(ArrayCodec.class, arrayCodec);
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		JsonElement encoded = arrayCodec.encode(provider, new String[] { "a", "b", "c" });
		assertEquals(new JsonArray(List.of(new JsonPrimitive("a"), new JsonPrimitive("b"), new JsonPrimitive("c"))), encoded);
		
		String[] decoded = arrayCodec.decode(provider, encoded);
		assertArrayEquals(new String[] { "a", "b", "c" }, decoded);
	}
	
	@Test
	void list() {
		Codec<List<String>> listCodec = STRING.list();
		assertInstanceOf(ListCodec.class, listCodec);
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		JsonElement encoded = listCodec.encode(provider, List.of("a", "b", "c"));
		assertEquals(new JsonArray(List.of(new JsonPrimitive("a"), new JsonPrimitive("b"), new JsonPrimitive("c"))), encoded);
		
		List<String> decoded = listCodec.decode(provider, encoded);
		assertEquals(List.of("a", "b", "c"), decoded);
	}
	
	@Test
	void set() {
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
	void stream() {
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
		
		assertTrue(combinedCodec.encodeStart(provider, provider.empty(), "a").isSuccess());
		assertTrue(combinedCodec.encodeStart(provider, provider.empty(), "c").isSuccess());
		assertTrue(combinedCodec.encodeStart(provider, provider.empty(), "invalid").isError());
		
		assertTrue(combinedCodec.decodeStart(provider, provider.empty(), new JsonPrimitive("b")).isSuccess());
		assertTrue(combinedCodec.decodeStart(provider, provider.empty(), new JsonPrimitive("d")).isSuccess());
		assertTrue(combinedCodec.decodeStart(provider, provider.empty(), new JsonPrimitive("invalid")).isError());
	}
	
	@Test
	void xmap() {
		assertThrows(NullPointerException.class, () -> STRING.xmap((Function<String, String>) null, Function.identity()));
		assertThrows(NullPointerException.class, () -> STRING.xmap(Function.identity(), (Function<String, String>) null));
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		Codec<Integer> lengthCodec = STRING.xmap(String::valueOf, String::length);
		
		assertEquals(new JsonPrimitive("42"), lengthCodec.encode(provider, 42));
		assertEquals(4, lengthCodec.decode(provider, new JsonPrimitive("test")));
	}
	
	@Test
	void mapFlat() {
		assertThrows(NullPointerException.class, () -> STRING.mapFlat((Function<String, String>) null, result -> result.map(Function.identity())));
		assertThrows(NullPointerException.class, () -> STRING.<String>mapFlat(Function.identity(), null));
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		Codec<Integer> parseCodec = STRING.mapFlat(
			String::valueOf,
			result -> {
				if (result.isError()) {
					return Result.error(result.errorOrThrow());
				}
				try {
					return Result.success(Integer.parseInt(result.resultOrThrow()));
				} catch (NumberFormatException e) {
					return Result.error("Not a valid integer: " + result.resultOrThrow());
				}
			}
		);
		
		assertEquals(new JsonPrimitive("42"), parseCodec.encode(provider, 42));
		assertEquals(42, parseCodec.decode(provider, new JsonPrimitive("42")));
		assertTrue(parseCodec.decodeStart(provider, provider.empty(), new JsonPrimitive("not-a-number")).isError());
	}
	
	@Test
	void map() {
		assertThrows(NullPointerException.class, () -> STRING.map((ResultingFunction<String, String>) null, result -> result.map(Function.identity())));
		assertThrows(NullPointerException.class, () -> STRING.<String>map(ResultingFunction.direct(Function.identity()), null));
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		Codec<Integer> mappedCodec = STRING.map(
			value -> Result.success(String.valueOf(value)),
			result -> {
				if (result.isError()) {
					return Result.error(result.errorOrThrow());
				}
				try {
					return Result.success(Integer.parseInt(result.resultOrThrow()));
				} catch (NumberFormatException e) {
					return Result.error("Not a valid integer: " + result.resultOrThrow());
				}
			}
		);
		
		assertEquals(new JsonPrimitive("42"), mappedCodec.encode(provider, 42));
		assertEquals(42, mappedCodec.decode(provider, new JsonPrimitive("42")));
		assertTrue(mappedCodec.decodeStart(provider, provider.empty(), new JsonPrimitive("not-a-number")).isError());
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
		
		assertTrue(positiveCodec.encodeStart(provider, provider.empty(), 42).isSuccess());
		assertTrue(positiveCodec.encodeStart(provider, provider.empty(), -1).isError());
		
		assertTrue(positiveCodec.decodeStart(provider, provider.empty(), new JsonPrimitive(42)).isSuccess());
		assertTrue(positiveCodec.decodeStart(provider, provider.empty(), new JsonPrimitive(-1)).isError());
	}
	
	@Test
	void orElse() {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		Codec<String> positiveOnlyCodec = STRING.union("positive").orElse("default");
		
		assertEquals("default", positiveOnlyCodec.decode(provider, new JsonPrimitive("negative")));
		assertEquals("positive", positiveOnlyCodec.decode(provider, new JsonPrimitive("positive")));
	}
	
	@Test
	void orElseGet() {
		assertThrows(NullPointerException.class, () -> STRING.orElseGet(null));
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		Codec<String> positiveOnlyCodec = STRING.union("positive").orElseGet(() -> "default");
		
		assertEquals("default", positiveOnlyCodec.decode(provider, new JsonPrimitive("negative")));
		assertEquals("positive", positiveOnlyCodec.decode(provider, new JsonPrimitive("positive")));
	}
	
	@Test
	void fieldOf() {
		assertThrows(NullPointerException.class, () -> STRING.fieldOf(null, TestObject::name));
		assertThrows(NullPointerException.class, () -> STRING.fieldOf("name", (Function<TestObject, String>) null));
		
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		JsonObject current = new JsonObject();
		Result<JsonElement> result = fieldCodec.encodeStart(provider, current, new TestObject("John"));
		assertTrue(result.isSuccess());
		
		JsonObject expected = new JsonObject();
		expected.add("name", new JsonPrimitive("John"));
		assertEquals(expected, result.resultOrThrow());
		
		Result<String> decoded = fieldCodec.decodeStart(provider, provider.empty(), expected);
		assertTrue(decoded.isSuccess());
		assertEquals("John", decoded.resultOrThrow());
	}
	
	@Test
	void fieldOfWithAlias() {
		assertThrows(NullPointerException.class, () -> STRING.fieldOf("name", (String) null, TestObject::name));
		
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", "alias", TestObject::name);
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		JsonObject aliasJson = new JsonObject();
		aliasJson.add("alias", new JsonPrimitive("John"));
		
		Result<String> decoded = fieldCodec.decodeStart(provider, provider.empty(), aliasJson);
		assertTrue(decoded.isSuccess());
		assertEquals("John", decoded.resultOrThrow());
	}
	
	@Test
	void fieldOfWithAliases() {
		assertThrows(NullPointerException.class, () -> STRING.fieldOf("name", (Set<String>) null, TestObject::name));
		
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", Set.of("alias1", "alias2"), TestObject::name);
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		JsonObject alias1Json = new JsonObject();
		alias1Json.add("alias1", new JsonPrimitive("John"));
		
		Result<String> decoded1 = fieldCodec.decodeStart(provider, provider.empty(), alias1Json);
		assertTrue(decoded1.isSuccess());
		assertEquals("John", decoded1.resultOrThrow());
		
		JsonObject alias2Json = new JsonObject();
		alias2Json.add("alias2", new JsonPrimitive("Jane"));
		
		Result<String> decoded2 = fieldCodec.decodeStart(provider, provider.empty(), alias2Json);
		assertTrue(decoded2.isSuccess());
		assertEquals("Jane", decoded2.resultOrThrow());
	}
	
	@Test
	void optionalFieldOf() {
		FieldCodec<String, TestObject> fieldCodec = STRING.optionalFieldOf("name", "default", TestObject::name);
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		JsonObject emptyJson = new JsonObject();
		Result<String> decoded = fieldCodec.decodeStart(provider, provider.empty(), emptyJson);
		assertTrue(decoded.isSuccess());
		assertEquals("default", decoded.resultOrThrow());
		
		JsonObject presentJson = new JsonObject();
		presentJson.add("name", new JsonPrimitive("John"));
		Result<String> decodedPresent = fieldCodec.decodeStart(provider, provider.empty(), presentJson);
		assertTrue(decodedPresent.isSuccess());
		assertEquals("John", decodedPresent.resultOrThrow());
	}
	
	@Test
	void optionalFieldOfWithSupplier() {
		FieldCodec<String, TestObject> fieldCodec = STRING.optionalFieldOf("name", () -> "default", TestObject::name);
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		JsonObject emptyJson = new JsonObject();
		Result<String> decoded = fieldCodec.decodeStart(provider, provider.empty(), emptyJson);
		assertTrue(decoded.isSuccess());
		assertEquals("default", decoded.resultOrThrow());
	}
	
	@Test
	void optionalFieldOfWithAlias() {
		FieldCodec<String, TestObject> fieldCodec = STRING.optionalFieldOf("name", "alias", "default", TestObject::name);
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		JsonObject emptyJson = new JsonObject();
		Result<String> decodedDefault = fieldCodec.decodeStart(provider, provider.empty(), emptyJson);
		assertTrue(decodedDefault.isSuccess());
		assertEquals("default", decodedDefault.resultOrThrow());
	}
	
	@Test
	void optionalFieldOfWithAliasAndSupplier() {
		FieldCodec<String, TestObject> fieldCodec = STRING.optionalFieldOf("name", "alias", () -> "default", TestObject::name);
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		JsonObject emptyJson = new JsonObject();
		Result<String> decoded = fieldCodec.decodeStart(provider, provider.empty(), emptyJson);
		assertTrue(decoded.isSuccess());
		assertEquals("default", decoded.resultOrThrow());
	}
	
	@Test
	void optionalFieldOfWithAliases() {
		FieldCodec<String, TestObject> fieldCodec = STRING.optionalFieldOf("name", Set.of("alias1", "alias2"), "default", TestObject::name);
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		JsonObject emptyJson = new JsonObject();
		Result<String> decodedDefault = fieldCodec.decodeStart(provider, provider.empty(), emptyJson);
		assertTrue(decodedDefault.isSuccess());
		assertEquals("default", decodedDefault.resultOrThrow());
	}
	
	@Test
	void optionalFieldOfWithAliasesAndSupplier() {
		FieldCodec<String, TestObject> fieldCodec = STRING.optionalFieldOf("name", Set.of("alias1", "alias2"), () -> "default", TestObject::name);
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		JsonObject emptyJson = new JsonObject();
		Result<String> decoded = fieldCodec.decodeStart(provider, provider.empty(), emptyJson);
		assertTrue(decoded.isSuccess());
		assertEquals("default", decoded.resultOrThrow());
	}
	
	private record TestObject(@NonNull String name) {}
}
