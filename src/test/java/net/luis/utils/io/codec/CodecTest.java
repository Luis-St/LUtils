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

import net.luis.utils.io.codec.internal.struct.*;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
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
	void atLeastCodecs() {
		assertThrows(NullPointerException.class, () -> Codec.atLeast(null, 0));
		assertThrows(NullPointerException.class, () -> Codec.atLeast(INTEGER, null));
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		KeyableCodec<Integer> atLeastCodec = Codec.atLeast(INTEGER, 10);
		
		Result<JsonElement> encoded = atLeastCodec.encodeStart(provider, provider.empty(), 20);
		assertTrue(encoded.isSuccess());
		assertEquals(new JsonPrimitive(20), encoded.resultOrThrow());
		
		Result<Integer> decoded = atLeastCodec.decodeStart(provider, new JsonPrimitive(20));
		assertTrue(decoded.isSuccess());
		assertEquals(20, decoded.resultOrThrow());
		
		KeyableCodec<Double> doubleAtLeastCodec = Codec.atLeast(DOUBLE, 5.5);
		assertTrue(doubleAtLeastCodec.encodeStart(provider, provider.empty(), 10.5).isSuccess());
		assertTrue(doubleAtLeastCodec.decodeStart(provider, new JsonPrimitive(10.5)).isSuccess());
	}
	
	@Test
	void atMostCodecs() {
		assertThrows(NullPointerException.class, () -> Codec.atMost(null, 100));
		assertThrows(NullPointerException.class, () -> Codec.atMost(INTEGER, null));
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		KeyableCodec<Integer> atMostCodec = Codec.atMost(INTEGER, 50);
		
		Result<JsonElement> encoded = atMostCodec.encodeStart(provider, provider.empty(), 25);
		assertTrue(encoded.isSuccess());
		assertEquals(new JsonPrimitive(25), encoded.resultOrThrow());
		
		Result<Integer> decoded = atMostCodec.decodeStart(provider, new JsonPrimitive(25));
		assertTrue(decoded.isSuccess());
		assertEquals(25, decoded.resultOrThrow());
		
		KeyableCodec<Float> floatAtMostCodec = Codec.atMost(FLOAT, 99.9f);
		assertTrue(floatAtMostCodec.encodeStart(provider, provider.empty(), 50.5f).isSuccess());
		assertTrue(floatAtMostCodec.decodeStart(provider, new JsonPrimitive(50.5f)).isSuccess());
	}
	
	@Test
	void rangedCodecs() {
		assertThrows(NullPointerException.class, () -> Codec.ranged(null, 0, 100));
		assertThrows(NullPointerException.class, () -> Codec.ranged(INTEGER, null, 100));
		assertThrows(NullPointerException.class, () -> Codec.ranged(INTEGER, 0, null));
		assertThrows(IllegalArgumentException.class, () -> Codec.ranged(INTEGER, 100, 50));
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		KeyableCodec<Integer> rangedCodec = Codec.ranged(INTEGER, 10, 50);
		
		Result<JsonElement> encoded = rangedCodec.encodeStart(provider, provider.empty(), 25);
		assertTrue(encoded.isSuccess());
		assertEquals(new JsonPrimitive(25), encoded.resultOrThrow());
		
		Result<Integer> decoded = rangedCodec.decodeStart(provider, new JsonPrimitive(25));
		assertTrue(decoded.isSuccess());
		assertEquals(25, decoded.resultOrThrow());
		
		KeyableCodec<Integer> singleValueCodec = Codec.ranged(INTEGER, 42, 42);
		assertTrue(singleValueCodec.encodeStart(provider, provider.empty(), 42).isSuccess());
		assertTrue(singleValueCodec.decodeStart(provider, new JsonPrimitive(42)).isSuccess());
	}
	
	@Test
	void keyableInstanceMethod() {
		assertThrows(NullPointerException.class, () -> INTEGER.keyable(null, ResultingFunction.direct(Integer::valueOf)));
		assertThrows(NullPointerException.class, () -> INTEGER.keyable(ResultingFunction.direct(String::valueOf), null));
		
		KeyableCodec<Integer> keyableCodec = INTEGER.keyable(
			ResultingFunction.direct(String::valueOf),
			ResultingFunction.direct(Integer::valueOf)
		);
		
		assertInstanceOf(KeyableCodec.class, keyableCodec);
		assertEquals("KeyableCodec[IntegerCodec]", keyableCodec.toString());
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		Result<JsonElement> encoded = keyableCodec.encodeStart(provider, provider.empty(), 42);
		assertTrue(encoded.isSuccess());
		assertEquals(new JsonPrimitive(42), encoded.resultOrThrow());
		
		Result<String> encodedKey = keyableCodec.encodeKey(provider, 42);
		assertTrue(encodedKey.isSuccess());
		assertEquals("42", encodedKey.resultOrThrow());
		
		Result<Integer> decoded = keyableCodec.decodeStart(provider, new JsonPrimitive(42));
		assertTrue(decoded.isSuccess());
		assertEquals(42, decoded.resultOrThrow());
		
		Result<Integer> decodedKey = keyableCodec.decodeKey(provider, "42");
		assertTrue(decodedKey.isSuccess());
		assertEquals(42, decodedKey.resultOrThrow());
	}
	
	@Test
	void unionCodecs() {
		assertThrows(NullPointerException.class, () -> Codec.union(null, "a", "b"));
		assertThrows(NullPointerException.class, () -> Codec.union(STRING, (String[]) null));
		assertThrows(IllegalArgumentException.class, () -> Codec.union(STRING));
		assertInstanceOf(UnionCodec.class, Codec.union(STRING, "a", "b"));
		assertInstanceOf(UnionCodec.class, STRING.union("a", "b"));

		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		Codec<String> unionCodec = STRING.union("pending", "active", "completed");

		JsonElement encoded = unionCodec.encode(provider, "active");
		assertEquals(new JsonPrimitive("active"), encoded);
		String decoded = unionCodec.decode(provider, encoded);
		assertEquals("active", decoded);

		assertTrue(unionCodec.encodeStart(provider, provider.empty(), "pending").isSuccess());
		assertTrue(unionCodec.encodeStart(provider, provider.empty(), "active").isSuccess());
		assertTrue(unionCodec.encodeStart(provider, provider.empty(), "completed").isSuccess());
		assertTrue(unionCodec.encodeStart(provider, provider.empty(), "invalid").isError());

		Codec<Integer> integerUnionCodec = INTEGER.union(1, 2, 3);
		assertTrue(integerUnionCodec.encodeStart(provider, provider.empty(), 2).isSuccess());
		assertTrue(integerUnionCodec.encodeStart(provider, provider.empty(), 5).isError());
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
		assertFalse(encodedEmpty.isJsonNull());
		assertFalse(encodedEmpty.isJsonPrimitive());
		assertFalse(encodedEmpty.isJsonArray());
		assertFalse(encodedEmpty.isJsonObject());
		Optional<Integer> decodedEmpty = optionalCodec.decode(provider, encodedEmpty);
		assertTrue(decodedEmpty.isEmpty());
	}
	
	@Test
	void optionalCodecsWithDefault() {
		assertThrows(NullPointerException.class, () -> Codec.optional(null, 0));
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		Codec<Integer> optionalWithDefaultCodec = INTEGER.optional(100);
		
		JsonElement encodedPresent = optionalWithDefaultCodec.encode(provider, 42);
		assertEquals(new JsonPrimitive(42), encodedPresent);
		Integer decodedPresent = optionalWithDefaultCodec.decode(provider, encodedPresent);
		assertEquals(42, decodedPresent);
		
		JsonElement encodedNull = optionalWithDefaultCodec.encode(provider, null);
		assertFalse(encodedNull.isJsonNull());
		assertFalse(encodedNull.isJsonPrimitive());
		assertFalse(encodedNull.isJsonArray());
		assertFalse(encodedNull.isJsonObject());
		Integer decodedNull = optionalWithDefaultCodec.decode(provider, encodedNull);
		assertEquals(100, decodedNull);
		
		Codec<String> stringOptionalCodec = STRING.optional("default");
		JsonElement encodedDefault = stringOptionalCodec.encode(provider, "default");
		assertEquals(new JsonPrimitive("default"), encodedDefault);
		String decodedEmpty = stringOptionalCodec.decode(provider, JsonNull.INSTANCE);
		assertEquals("default", decodedEmpty);
	}
	
	@Test
	void optionalCodecsWithDefaultSupplier() {
		assertThrows(NullPointerException.class, () -> Codec.<Supplier<Integer>>optional(null, (Supplier<Integer>) () -> 0));
		assertThrows(NullPointerException.class, () -> Codec.optional(INTEGER, (Supplier<Integer>) null));
		assertThrows(NullPointerException.class, () -> INTEGER.optional((Supplier<Integer>) null));
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		Codec<Integer> optionalWithDefaultFromCodec = INTEGER.optional(() -> 200);
		
		JsonElement encodedPresent = optionalWithDefaultFromCodec.encode(provider, 42);
		assertEquals(new JsonPrimitive(42), encodedPresent);
		Integer decodedPresent = optionalWithDefaultFromCodec.decode(provider, encodedPresent);
		assertEquals(42, decodedPresent);
		
		JsonElement encodedNull = optionalWithDefaultFromCodec.encode(provider, null);
		assertFalse(encodedNull.isJsonNull());
		assertFalse(encodedNull.isJsonPrimitive());
		assertFalse(encodedNull.isJsonArray());
		assertFalse(encodedNull.isJsonObject());
		Integer decodedNull = optionalWithDefaultFromCodec.decode(provider, encodedNull);
		assertEquals(200, decodedNull);
		
		Codec<String> stringOptionalCodec = STRING.optional(() -> "supplier-default");
		JsonElement encodedDefault = stringOptionalCodec.encode(provider, "supplier-default");
		assertEquals(new JsonPrimitive("supplier-default"), encodedDefault);
		String decodedEmpty = stringOptionalCodec.decode(provider, JsonNull.INSTANCE);
		assertEquals("supplier-default", decodedEmpty);
	}
	
	@Test
	void arrayCodecs() {
		assertThrows(NullPointerException.class, () -> Codec.array(null, INTEGER));
		assertThrows(NullPointerException.class, () -> Codec.array(Integer.class, null));
		assertThrows(NullPointerException.class, () -> Codec.noneEmptyArray(null, INTEGER));
		assertThrows(NullPointerException.class, () -> Codec.noneEmptyArray(Integer.class, null));
		assertThrows(IllegalArgumentException.class, () -> Codec.array(Integer.class, INTEGER, -1));
		assertThrows(IllegalArgumentException.class, () -> Codec.array(Integer.class, INTEGER, -1, 2));
		assertThrows(IllegalArgumentException.class, () -> Codec.array(Integer.class, INTEGER, 2, 1));
		
		assertInstanceOf(ArrayCodec.class, Codec.array(Integer.class, INTEGER));
		assertInstanceOf(ArrayCodec.class, INTEGER.array(Integer.class));
		assertInstanceOf(ArrayCodec.class, Codec.noneEmptyArray(Integer.class, INTEGER));
		assertInstanceOf(ArrayCodec.class, INTEGER.noneEmptyArray(Integer.class));
		
		assertThrows(NullPointerException.class, () -> INTEGER.array(null));
		assertThrows(NullPointerException.class, () -> INTEGER.array(null, 5));
		assertThrows(NullPointerException.class, () -> INTEGER.array(null, 1, 5));
		assertThrows(NullPointerException.class, () -> INTEGER.noneEmptyArray(null));
		
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		Codec<Integer[]> arrayCodec = INTEGER.array(Integer.class);
		
		Integer[] originalArray = new Integer[] { 1, 2, 3 };
		JsonElement encoded = arrayCodec.encode(provider, originalArray);
		assertInstanceOf(JsonArray.class, encoded);
		assertEquals(3, ((JsonArray) encoded).size());
		
		Integer[] decoded = arrayCodec.decode(provider, encoded);
		assertArrayEquals(originalArray, decoded);
		
		Codec<Integer[]> limitedArrayCodec = INTEGER.array(Integer.class, 2);
		assertTrue(limitedArrayCodec.encodeStart(provider, provider.empty(), new Integer[] { 1, 2 }).isSuccess());
		assertTrue(limitedArrayCodec.encodeStart(provider, provider.empty(), new Integer[] { 1, 2, 3 }).isError());
		
		Codec<String[]> nonEmptyArrayCodec = STRING.noneEmptyArray(String.class);
		assertTrue(nonEmptyArrayCodec.encodeStart(provider, provider.empty(), new String[] { "test" }).isSuccess());
		assertTrue(nonEmptyArrayCodec.encodeStart(provider, provider.empty(), new String[] {}).isError());
	}
	
	@Test
	void nestedArrayCodecs() {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		Codec<Integer[][]> twoDArrayCodec = Codec.array(Integer[].class, INTEGER.array(Integer.class));
		
		Integer[][] originalArray = new Integer[][] {
			{ 1, 2, 3 },
			{ 4, 5, 6 }
		};
		
		JsonElement encoded = twoDArrayCodec.encode(provider, originalArray);
		assertInstanceOf(JsonArray.class, encoded);
		
		Integer[][] decoded = twoDArrayCodec.decode(provider, encoded);
		assertEquals(2, decoded.length);
		assertArrayEquals(new Integer[] { 1, 2, 3 }, decoded[0]);
		assertArrayEquals(new Integer[] { 4, 5, 6 }, decoded[1]);
		
		Codec<String[][][]> threeDArrayCodec = Codec.array(String[][].class,
			Codec.array(String[].class, STRING.array(String.class)));
		
		String[][][] original3D = new String[][][] {
			{
				{ "a", "b" },
				{ "c", "d" }
			},
			{
				{ "e", "f" },
				{ "g", "h" }
			}
		};
		
		JsonElement encoded3D = threeDArrayCodec.encode(provider, original3D);
		String[][][] decoded3D = threeDArrayCodec.decode(provider, encoded3D);
		
		assertEquals(2, decoded3D.length);
		assertEquals(2, decoded3D[0].length);
		assertArrayEquals(new String[] { "a", "b" }, decoded3D[0][0]);
		assertArrayEquals(new String[] { "c", "d" }, decoded3D[0][1]);
		assertArrayEquals(new String[] { "e", "f" }, decoded3D[1][0]);
		assertArrayEquals(new String[] { "g", "h" }, decoded3D[1][1]);
	}
	
	@Test
	void arrayCodecsWithConstraints() {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		
		Codec<Integer[]> constrainedArrayCodec = INTEGER.array(Integer.class, 2, 4);
		
		Integer[] validArray = new Integer[] { 1, 2, 3 };
		assertTrue(constrainedArrayCodec.encodeStart(provider, provider.empty(), validArray).isSuccess());
		
		Integer[] tooSmall = new Integer[] { 1 };
		assertTrue(constrainedArrayCodec.encodeStart(provider, provider.empty(), tooSmall).isError());
		
		Integer[] tooLarge = new Integer[] { 1, 2, 3, 4, 5 };
		assertTrue(constrainedArrayCodec.encodeStart(provider, provider.empty(), tooLarge).isError());
		
		Codec<Integer[][]> constrainedNestedCodec = Codec.array(Integer[].class,
			INTEGER.array(Integer.class, 2, 3), 1, 2);
		
		Integer[][] validNested = new Integer[][] {
			{ 1, 2, 3 },
			{ 4, 5 }
		};
		assertTrue(constrainedNestedCodec.encodeStart(provider, provider.empty(), validNested).isSuccess());
		
		Integer[][] invalidNested = new Integer[][] {
			{ 1 },
			{ 2, 3 }
		};
		assertTrue(constrainedNestedCodec.encodeStart(provider, provider.empty(), invalidNested).isError());
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
		
		assertThrows(NullPointerException.class, () -> STRING.orElseGet(null));
		
		Codec<String> orElseCodec = STRING.orElse("default");
		assertEquals("default", orElseCodec.decode(provider, JsonNull.INSTANCE));
		
		Codec<String> orElseGetCodec = STRING.orElseGet(() -> "default");
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
