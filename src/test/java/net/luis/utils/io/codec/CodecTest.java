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
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
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
	void codecFactoryMethods() {
		assertThrows(NullPointerException.class, () -> Codec.of(null, INTEGER, "IntegerCodec"));
		assertThrows(NullPointerException.class, () -> Codec.of(INTEGER, null, "IntegerCodec"));
		assertThrows(NullPointerException.class, () -> Codec.of(INTEGER, INTEGER, null));
		assertNotNull(Codec.of(INTEGER, INTEGER, "IntegerCodec"));
	}
	
	@Test
	void keyableCodecFactoryMethods() {
		assertThrows(NullPointerException.class, () -> Codec.keyable((Codec<Integer>) null, ResultingFunction.direct(Integer::valueOf)));
		assertThrows(NullPointerException.class, () -> Codec.keyable(INTEGER, null));
		assertInstanceOf(KeyableCodec.class, Codec.keyable(INTEGER, ResultingFunction.direct(Integer::valueOf)));
		
		assertThrows(NullPointerException.class, () -> Codec.keyable(null, ResultingFunction.direct(String::valueOf), ResultingFunction.direct(Integer::valueOf)));
		assertThrows(NullPointerException.class, () -> Codec.keyable(INTEGER, null, ResultingFunction.direct(Integer::valueOf)));
		assertThrows(NullPointerException.class, () -> Codec.keyable(INTEGER, ResultingFunction.direct(String::valueOf), null));
		assertInstanceOf(KeyableCodec.class, Codec.keyable(INTEGER, ResultingFunction.direct(String::valueOf), ResultingFunction.direct(Integer::valueOf)));
	}
	
	@Test
	void optionalCodecs() {
		assertThrows(NullPointerException.class, () -> Codec.optional((Codec<Integer>) null));
		assertInstanceOf(OptionalCodec.class, Codec.optional(INTEGER));
		assertInstanceOf(OptionalCodec.class, INTEGER.optional());
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> optionalCodec = INTEGER.optional();
		
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
		assertThrows(IllegalArgumentException.class, () -> Codec.list(INTEGER, -1));
		assertThrows(IllegalArgumentException.class, () -> Codec.list(INTEGER, -1, 2));
		assertThrows(IllegalArgumentException.class, () -> Codec.list(INTEGER, 2, 1));
		
		assertInstanceOf(ListCodec.class, Codec.list(INTEGER));
		assertInstanceOf(ListCodec.class, INTEGER.list());
		assertInstanceOf(ListCodec.class, Codec.noneEmptyList(INTEGER));
		assertInstanceOf(ListCodec.class, INTEGER.noneEmptyList());
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> listCodec = INTEGER.list();
		
		List<Integer> originalList = List.of(1, 2, 3);
		JsonElement encoded = listCodec.encode(provider, originalList);
		assertInstanceOf(JsonArray.class, encoded);
		assertEquals(3, ((JsonArray) encoded).size());
		
		List<Integer> decoded = listCodec.decode(provider, encoded);
		assertEquals(originalList, decoded);
		
		Codec<List<Integer>> limitedListCodec = INTEGER.list(2);
		assertTrue(limitedListCodec.encodeStart(provider, provider.empty(), List.of(1, 2)).isSuccess());
		assertTrue(limitedListCodec.encodeStart(provider, provider.empty(), List.of(1, 2, 3)).isError());
	}
	
	@Test
	void streamCodecs() {
		assertThrows(NullPointerException.class, () -> Codec.stream((Codec<Integer>) null));
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		Codec<Stream<Integer>> streamCodec = INTEGER.stream();
		
		JsonElement encoded = streamCodec.encode(provider, Stream.of(1, 2, 3));
		assertInstanceOf(JsonArray.class, encoded);
		assertEquals(3, ((JsonArray) encoded).size());
		
		Stream<Integer> decoded = streamCodec.decode(provider, encoded);
		assertEquals(List.of(1, 2, 3), decoded.toList());
	}
	
	@Test
	void mapCodecs() {
		assertThrows(NullPointerException.class, () -> Codec.map((Codec<Integer>) null));
		assertThrows(NullPointerException.class, () -> Codec.map((KeyableCodec<Integer>) null, BOOLEAN));
		assertThrows(NullPointerException.class, () -> Codec.map(INTEGER, (Codec<Boolean>) null));
		assertThrows(NullPointerException.class, () -> Codec.noneEmptyMap((KeyableCodec<Integer>) null, BOOLEAN));
		assertThrows(NullPointerException.class, () -> Codec.noneEmptyMap(INTEGER, (Codec<Boolean>) null));
		assertThrows(IllegalArgumentException.class, () -> Codec.map(INTEGER, BOOLEAN, -1));
		assertThrows(IllegalArgumentException.class, () -> Codec.map(INTEGER, BOOLEAN, -1, 2));
		assertThrows(IllegalArgumentException.class, () -> Codec.map(INTEGER, BOOLEAN, 2, 1));
		
		assertInstanceOf(MapCodec.class, Codec.map(INTEGER));
		assertInstanceOf(MapCodec.class, Codec.map(INTEGER, BOOLEAN));
		assertInstanceOf(MapCodec.class, Codec.noneEmptyMap(INTEGER, BOOLEAN));
	}
	
	@Test
	void alternativeCodecs() {
		Codec<Integer> alternative = DOUBLE.xmap(Integer::doubleValue, Double::intValue);
		assertThrows(NullPointerException.class, () -> Codec.withAlternative(null, alternative));
		assertThrows(NullPointerException.class, () -> Codec.withAlternative(INTEGER, null));
		assertThrows(NullPointerException.class, () -> INTEGER.withAlternative(null));
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codecWithAlternative = Codec.withAlternative(INTEGER, alternative);
		
		JsonElement encoded = codecWithAlternative.encode(provider, 42);
		assertEquals(new JsonPrimitive(42), encoded);
		
		Integer decoded = codecWithAlternative.decode(provider, new JsonPrimitive(42.0));
		assertEquals(42, decoded);
	}
	
	@Test
	void codecTransformations() {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		assertThrows(NullPointerException.class, () -> STRING.xmap(null, Integer::valueOf));
		assertThrows(NullPointerException.class, () -> STRING.xmap(String::valueOf, null));
		
		Codec<Integer> xmappedCodec = STRING.xmap(String::valueOf, Integer::valueOf);
		assertEquals(new JsonPrimitive("42"), xmappedCodec.encode(provider, 42));
		assertEquals(42, xmappedCodec.decode(provider, new JsonPrimitive("42")));
		
		Codec<Integer> flatMappedCodec = STRING.mapFlat(String::valueOf, ResultMappingFunction.throwable(Integer::valueOf));
		assertEquals(new JsonPrimitive("42"), flatMappedCodec.encode(provider, 42));
		assertEquals(42, flatMappedCodec.decode(provider, new JsonPrimitive("42")));
		assertTrue(flatMappedCodec.decodeStart(provider, new JsonPrimitive("invalid")).isError());
		
		assertThrows(NullPointerException.class, () -> STRING.validate(null));
		
		Codec<String> validatedCodec = STRING.validate(s -> {
			if (s.length() > 2) {
				return Result.error("Too long");
			}
			return Result.success(s);
		});
		assertEquals(new JsonPrimitive("ab"), validatedCodec.encode(provider, "ab"));
		assertEquals("ab", validatedCodec.decode(provider, new JsonPrimitive("ab")));
		assertTrue(validatedCodec.decodeStart(provider, new JsonPrimitive("abc")).isError());
		
		assertThrows(NullPointerException.class, () -> STRING.withDefaultGet(null));
		
		Codec<String> orElseCodec = STRING.withDefault("default");
		assertEquals("default", orElseCodec.decode(provider, JsonNull.INSTANCE));
		
		Codec<String> orElseGetCodec = STRING.withDefaultGet(() -> "default");
		assertEquals("default", orElseGetCodec.decode(provider, JsonNull.INSTANCE));
	}
	
	@Test
	void namedAndConfiguredCodecs() {
		assertThrows(NullPointerException.class, () -> STRING.named(null));
		assertThrows(NullPointerException.class, () -> STRING.getter(null));
		assertThrows(NullPointerException.class, () -> STRING.configure(null, TestObject::name));
		assertThrows(NullPointerException.class, () -> STRING.configure("name", null));
		
		assertInstanceOf(NamedCodec.class, STRING.named("name"));
		assertInstanceOf(NamedCodec.class, STRING.named("name", "alias"));
		assertInstanceOf(ConfiguredCodec.class, STRING.getter(TestObject::name));
		assertInstanceOf(ConfiguredCodec.class, STRING.configure("name", TestObject::name));
		
		Codec<String> namedCodec = STRING.named("StringField");
		assertEquals("NamedCodec['StringField', StringCodec]", namedCodec.toString());
		
		ConfiguredCodec<String, TestObject> configuredCodec = STRING.configure("name", TestObject::name);
		assertNotNull(configuredCodec);
	}
	
	private record TestObject(@NotNull String name) {}
}
